package handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.audio.AudioPlayer;
import com.sun.speech.freetts.audio.SingleFileAudioPlayer;

import commands.Command;
import commands.MessageCommand;
import commands.ReactionAddCommand;
import commands.TimedCommand;
import discord.ActionMessage;
import discord.Configuration;
import discord.Emoji;
import discord.Game;
import discord.ScrollMessage;
import discord.UserInformation;
import discord.Zitat;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import potatocoin.Challenge;
import potatocoin.GnocciGangException;
import potatocoin.Inventory;
import potatocoin.LootBox;
import potatocoin.Shop;
import potatocoin.TradeOffer;
import spiel.Brett;
import spiel.Figur.Farbe;
import spiel.Spielzug;
import spiel.Zug;

public class Handler implements AudioSendHandler {

	Guild g;
	UserInformation userinfo;
	Command[] commands;

	ArrayList<Zitat> zitate;
	Map<String, Integer[]> scores;

	final String path = "zitat_scores";
	final int K = 400;

	Game game;
	Brett b;
	int p;

	static AudioFormat Format = new AudioFormat(1000f, 16, 2, true, true);

	ArrayList<ByteBuffer> bb;

	boolean randomZitatAudio = false;
	Timer t;
	final int periodMillis = 500000;
	final double probability = 1;

	AudioManager manager;

	Configuration config;

	ArrayList<Member> acceptParticipation, leaveEvent;

	public Handler(Guild g, UserInformation userinfo) {
		this.g = g;
		this.userinfo = userinfo;
		this.zitate = userinfo.getZitatLoader().getZitate();
		userinfo.put("guild", "zitate", zitate);

		if (userinfo.get("guild", "ActionMessages", ArrayList.class) != null) {
			for (ActionMessage a : (ArrayList<ActionMessage>) userinfo.get("guild", "ActionMessages",
					ArrayList.class)) {

			}
		}

		userinfo.put("guild", "ActionMessages", new ArrayList<ActionMessage>());
		userinfo.put("guild", "ActionEmojis", new ArrayList<Emoji>());

		commands = new Command[] { new MessageCommand('<', new String[] { "stats" },
				new String[][] { new String[] { "\\w+" } }, this::cmdStats),
				new MessageCommand('"', null, new String[][] { null }, (e, s) -> {
					Zitat z = new Zitat(e.getMessage());
					if (z.isFull()) {
						zitate.add(z);
					}
				}), // <rate || <rate 1
				new MessageCommand('<', new String[] { "rate", "r" }, new String[][] { new String[] {} },
						this::cmdRate),
				new MessageCommand('<', new String[] { "loadScores" }, new String[][] { new String[] {} },
						this::cmdLoadScores),
				new MessageCommand('<', new String[] { "top" },
						new String[][] { new String[] {}, new String[] { "\\d+" } }, this::cmdTop),
				new MessageCommand('<', new String[] { "spiel", "s" }, new String[][] { new String[] { "\\d+" } },
						this::cmdSpiel),
				new MessageCommand('<', new String[] { "guess", "g" }, new String[][] { new String[] { ".+" } },
						this::cmdGuess),
				new MessageCommand('<', new String[] { "skip" }, new String[][] { new String[] {} }, this::cmdSkip),
				new MessageCommand('<', new String[] { "ergebnisse", "e" }, new String[][] { new String[] {} },
						this::cmdErgebnisse),
				new MessageCommand('<', new String[] { "schach" },
						new String[][] { new String[] {}, new String[] { "[a-h]\\d->[a-h]\\d" } }, this::cmdSchach),
				new MessageCommand('<', new String[] { "trza" }, new String[][] { new String[] {} },
						this::cmdToggleRandomZitatAudio),
				new MessageCommand('<', new String[] { "config" },
						new String[][] { new String[] { "[1-2]", "\\w+", "[0-1]" } }, this::cmdConfig),
				new MessageCommand('<', new String[] { "participate" }, new String[][] { new String[] {} },
						this::cmdParticipate),
				new MessageCommand('<', new String[] { "accept" }, new String[][] { new String[] {} }, this::cmdAccept),
				new MessageCommand('<', new String[] { "decline" }, new String[][] { new String[] {} },
						this::cmdDecline),
				new MessageCommand('<', new String[] { "leaveSupercoolesEvent" }, new String[][] { new String[] {} },
						this::cmdLeaveEvent),
				new MessageCommand('<', new String[] { "trade" }, new String[][] {
						new String[] { "\\w+", "(\\d+\\.\\d+)|(\\d+)", "((\\d+,)*\\d+)|n", "((\\d+,)*\\d+)|n" } },
						this::cmdTrade),
				new MessageCommand('<', new String[] { "addTag" }, new String[][] { new String[] { ".+" } },
						this::cmdaddTag),
				new MessageCommand('<', new String[] { "removeTag" }, new String[][] { new String[] { ".+" } },
						this::cmdremoveTag),
				new MessageCommand('<', new String[] { "showTags" }, new String[][] { new String[0] },
						this::cmdshowTags),
				new MessageCommand('<', new String[] { "assignTag" },
						new String[][] { new String[0], new String[] { "\\d+" }, new String[] { "(.+,)*.+" } },
						this::cmdassignTag),
				new MessageCommand('<', new String[] { "inventory", "inv", "i" }, new String[][] { new String[0] },
						this::cmdInventory),
				new MessageCommand('<', new String[] { "test" }, new String[][] { null }, (e, cmd_body) -> {
					sendScrollMessage("Jakob der Hurensohn", "der Peniskopf",
							new String[][][] {
									new String[][] { new String[] { "hallo", "test", "true" },
											new String[] { "penis", "kopf", "true" },
											new String[] { "jojo", "pimmel", "true" } },
									new String[][] { new String[] { "emil", "cool", "false" },
											new String[] { "jakob", "stinkt", "true" },
											new String[] { "jojo", "pillenschlucker", "true" } },
									new String[][] { new String[] { "java", "good", "false" },
											new String[] { "python", "stinkt", "false" },
											new String[] { "jan der Rapper", "rappt", "false" } } },
							e.getChannel());
				}), new TimedCommand(/* name = */"save UserInfo", /* period = */ 1000 * 60, (e, cmd_body) -> {
					userinfo.save();
				}), new MessageCommand('<', new String[] { "owteam","ow" }, new String[][] { new String[] {} }, this::cmdOwTeam),
				 new ReactionAddCommand(userinfo.get("guild", "ActionEmojis", ArrayList.class),
						this::cmdResolveActionMessages)
				/*
				 * , new TimedCommand( name = "updateMeinkraft", period = 1000 * 60,
				 * this::updateMeinkraft)
				 */ };

		config = new Configuration(userinfo, commands);

		bb = new ArrayList<ByteBuffer>();

		acceptParticipation = new ArrayList<Member>();
		leaveEvent = new ArrayList<Member>();
	}

	public Command[] getCommands() {
		return commands;
	}

	public static void sendMessage(String msg, TextChannel channel) {
		channel.sendMessage(msg).queue();
	}

	public static void sendMessage(String msg, TextChannel channel, Consumer<? super Message> consumer) {
		channel.sendMessage(msg).queue(consumer);
	}

	public static void editMessage(Message m, String content) {
		m.editMessage(content).queue();
	}

	public static void editMessage(Message m, MessageEmbed content) {
		m.editMessage(content).queue();
	}

	public static void deleteMessage(String messageId, TextChannel channel) {
		channel.deleteMessageById(messageId).queue();
	}

	public void sendScrollMessage(String title, String description, String[][][] content, TextChannel channel) {
		ScrollMessage sm = new ScrollMessage(this, g, null, null, title, description, content, 0);

		channel.sendMessage(sm.getContent(0)).queue(m -> {
			sm.setMessage(m);
		});
	}

	public void addActionMessage(ActionMessage a) {
		userinfo.get("guild", "ActionMessages", ArrayList.class).add(a);
		userinfo.get("guild", "ActionEmojis", ArrayList.class).addAll(a.getEmojis());
		a.addReactions();
	}

	public static void addReaction(Message m, Emoji emoji) {
		if (m != null) {
			m.addReaction(emoji.code).queue();
		}
	}

	public static void removeReaction(Message m, Emoji emoji, User user) {
		if (m != null) {
			m.removeReaction(emoji.code, user).queue();
		}
	}

	public static void removeReaction(Message m, String emoji, User user) {
		if (m != null) {
			m.removeReaction(emoji, user).queue();
		}
	}

	public ArrayList<Zitat> getZitate() {
		ArrayList<Zitat> temp = new ArrayList<Zitat>();
		for (Zitat z : zitate) {
			temp.add(z);
		}

		return temp;
	}

	public Zitat getZitat(String path) {
		return userinfo.getZitatLoader().getZitat(path);

	}

	public Zitat randomZitat() {
		Zitat z = zitate.get((int) (Math.random() * zitate.size()));
		return z;
	}

	public int lowestZitat() {
		int min = (int) Double.POSITIVE_INFINITY;
		for (Zitat z : zitate) {
			int n = z.getScore()[0];

			if (n < min) {
				min = n;
			}

		}

		return min;
	}

	public Zitat get_lOR_Zitat(int n) {
		List<Zitat> temp = new ArrayList<Zitat>();
		for (Zitat z : zitate) {

			if (z.getScore()[1] == n) {
				temp.add(z);
			}

		}

		if (temp.size() == 0) {
			System.out.println(n);
			return get_lOR_Zitat(n + 1);
		} else {
			return temp.get((int) (Math.random() * temp.size()));
		}

	}

	public void loadScores(HashMap<String, Integer[]> scores, String path) {
		BufferedReader reader;
		try {
			if (new File(path).exists()) {

				reader = new BufferedReader(new FileReader(new File(path)));

				String line;

				while ((line = reader.readLine()) != null) {
					String[] split = line.split(",");

					Integer[] score = new Integer[] { Integer.parseInt(split[1]), Integer.parseInt(split[2]), 0 };

					if (split.length < 4) {
						score[2] = (score[1] * 1500 + (2 * score[0] - score[1]) * 400) / score[1];
					} else {
						score[2] = Integer.parseInt(split[3]);
					}

					scores.put(split[0], score);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveScores() {
		try {
			FileOutputStream fos = new FileOutputStream(new File(path));

			String str = "";

			for (int i = 0; i < zitate.size(); i++) {
				String ID = zitate.get(i).getID();
				str += ID + "," + scores.get(ID)[0] + "," + scores.get(ID)[1] + "," + scores.get(ID)[2] + "\n";
			}

			fos.write(str.getBytes());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void cmdLoadScores(GuildMessageReceivedEvent e, String[] cmd_body) {
		HashMap<String, Integer[]> scores = new HashMap<String, Integer[]>();
		loadScores(scores, "zitat_scores");
		System.out.println(scores);
		System.out.println("true");
		for (Entry<String, Integer[]> entry : scores.entrySet()) {
			for (Zitat z : zitate) {
				if (z.getID().equals(entry.getKey())) {
					z.setScore(entry.getValue());
				}
			}
		}
	}

	public void cmdStats(GuildMessageReceivedEvent e, String[] cmd_body) {
		String erg = "";
		switch (cmd_body[0]) {
		case "autor":
			erg = toString(getAutorStats(), true, true, false);
			break;
		case "schreiber":
			erg = toString(getSchreiberStats(), true, true, false);
			break;
		case "everyone":
			erg = zitate.size() + "\n" + zitate.get(zitate.size() - 1).getInhalt();
			break;
		default:
			erg = toString(getStatsFor(cmd_body[0]), true, true, false);
		}
		sendMessage(erg, e.getChannel());
	}

	public void cmdRate(GuildMessageReceivedEvent e, String[] cmd_body) {
//		loadScores();
		Zitat[] temp = new Zitat[2];
		String erg = getNewRateMessage(e, temp);

		sendMessage(erg, e.getChannel(), m -> {
			ActionMessage am = new ActionMessage(m, true, e.getAuthor());
			am.addAction(Emoji.one, (emoji, msg) -> {
				rate(0, e.getAuthor().getId(), temp);
				editMessage(m, getNewRateMessage(e, temp));
			});

			am.addAction(Emoji.two, (emoji, msg) -> {
				rate(1, e.getAuthor().getId(), temp);
				editMessage(m, getNewRateMessage(e, temp));
			});

			am.addAction(Emoji.a, (emoji, msg) -> {
				sendMessage("richtige Zahlen, du Hurensohn!", e.getChannel());
			});

			am.addAction(Emoji.b, (emoji, msg) -> {
				sendMessage("richtige Zahlen, du Hurensohn!", e.getChannel());
			});
			addActionMessage(am);
		});
	}

	public String getNewRateMessage(GuildMessageReceivedEvent e, Zitat[] temp) {
		String id = e.getAuthor().getId();
		int c = 0;

		System.out.println(id);

		int n = lowestZitat();

		int r = (int) (Math.random() * 2);
		temp[r] = get_lOR_Zitat(n);
		do {
			temp[1 - r] = randomZitat();
		} while (temp[0].equals(temp[1]));

		return temp[0].getAll() + "\n  or  \n" + temp[1].getAll() + "\n" + e.getAuthor().getAsMention();
	}

	public void rate(int r, String id, Zitat[] temp) {
		temp[r].getScore()[0] += 1;

		for (int i = 0; i < 2; i++) {
			temp[i].getScore()[1] += 1;
		}

		int Ra = temp[r].getScore()[2];
		int Rb = temp[1 - r].getScore()[2];

		double expected = 1 / (1 + Math.pow(10, (Ra - Rb) / 400));

		int change = (int) (K * (1 - expected));

		temp[r].getScore()[2] += change;
		temp[1 - r].getScore()[2] -= change;
		Inventory i = userinfo.get(id, "Inventory", Inventory.class);
		if (i != null) {
			i.addCoins(1);
			System.out.println(i.getCoins());
		}
		
		System.out.println("rated: " + temp[r]);
	}

	public void cmdTop(GuildMessageReceivedEvent e, String[] cmd_body) {
		String erg = "";
		int top = 0;
		if (cmd_body.length > 0) {
			top = top < zitate.size() ? Integer.parseInt(cmd_body[0]) : zitate.size() - 1;

		} else {
			top = 10;
		}

		if (top < 0)
			top = 1;

		if (top >= zitate.size())
			top = zitate.size();

		Map<String, Integer> map = new HashMap<String, Integer>();

		for (Zitat z : zitate) {
			Integer[] score = z.getScore();
			map.put(z.getPath(), score[2]);
		}

		String sorted = toString(map.entrySet(), true, false, true);

		String[] best = sorted.split(",");

		for (int i = top > 10 ? top - 10 : 0; i < top; i++) {
			Zitat z = getZitat(best[i]);
			erg += i + ". " + z.getAll() + " :" + z.getScore()[2] + "\n";
		}

		sendMessage(erg, e.getChannel());
	}

	public Set<Entry<String, Integer>> getAutorStats() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		zitate.forEach(z -> {
			String a = z.getAutor().toLowerCase();
			if (map.get(a) == null) {
				map.put(a, 1);
			} else {
				map.put(a, map.get(a) + 1);
			}
		});

		return map.entrySet();
	}

	public Set<Entry<String, Integer>> getSchreiberStats() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		zitate.forEach(z -> {
			String s = z.getSchreiber();
			if (map.get(s) == null) {
				map.put(s, 1);
			} else {
				map.put(s, map.get(s) + 1);
			}
		});

		return map.entrySet();
	}

	public Set<Entry<String, Integer>> getStatsFor(String word) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		zitate.forEach(z -> {
			String a = z.getAutor().toLowerCase();
			if (z.getInhalt().contains(word)) {
				if (map.get(a) == null) {
					map.put(a, 1);
				} else {
					map.put(a, map.get(a) + 1);
				}
			}
		});

		return map.entrySet();
	}

	public static <T> String toString(Set<Entry<T, Integer>> set, boolean sorted, boolean value, boolean seperated) {
		ArrayList<Entry<T, Integer>> list = new ArrayList<Entry<T, Integer>>();
		for (Entry<T, Integer> e : set) {
			list.add(e);
		}

		if (sorted) {
			list.sort(new Comparator<Entry<T, Integer>>() {
				public int compare(Entry<T, Integer> o1, Entry<T, Integer> o2) {
					return o2.getValue() - o1.getValue();
				}
			});
		}

		String str = "";
		for (Entry<T, Integer> e : list) {
			str += e.getKey() + (value ? ": " + e.getValue() : "") + (seperated ? "," : "\n");
		}
		return str != null ? str : ":(";
	}

	public void cmdSpiel(GuildMessageReceivedEvent e, String[] cmd_body) {
		int len = cmd_body[0] != null ? Integer.parseInt(cmd_body[0]) : 1;
		game = new Game(len, e.getGuild(), this);
		sendMessage(game.challenge(), e.getChannel());
	}

	public void cmdGuess(GuildMessageReceivedEvent e, String[] cmd_body) {
		String erg = "";
		if (game != null && cmd_body.length > 0 && cmd_body[0] != null) {
			if (game.guess(cmd_body[0], e.getAuthor().getName())) {
				erg = "Korrekt! \n" + game.challenge();
			} else {
				erg = "Leider Falsch!";
			}
		} else {
			erg = "Es gibt doch gar kein Spiel!";
		}
		sendMessage(erg, e.getChannel());
	}

	public void cmdSkip(GuildMessageReceivedEvent e, String[] cmd_body) {
		String erg = "";
		if (game == null) {
			erg = "Ey du Popokopf, welches Spiel?";
		} else if (game.isOver()) {
			erg = "Das Spiel is doch schon vorbei du Intelligenz Alleskika";
		} else {
			erg = "Die richtige Antwort war " + game.antwort() + ", du Geringverdiener:in\n" + game.challenge();
			game.addtoCounter(-1);
		}
		sendMessage(erg, e.getChannel());

	}

	public void cmdErgebnisse(GuildMessageReceivedEvent e, String[] cmd_body) {
		String erg = "";
		if (game == null) {
			erg = "Zu welchem Spiel? Bist du eine angebrunste Seuchkachel?";
		} else if (!game.isOver()) {
			erg = "Das Spiel läuft doch noch du halber europäischer Stör";
		} else {
			erg = game.ergebnis();
		}
		sendMessage(erg, e.getChannel());

	}

	public void cmdSchach(GuildMessageReceivedEvent e, String[] cmd_body) {
		if (b == null) {
			b = new Brett();
		} else {
			if (cmd_body.length > 0 && cmd_body[0] != null) {
				String[] zug = cmd_body[0].split("->");
				int[] x = new int[2];
				int[] y = new int[2];

				for (int i = 0; i < 2; i++) {
					x[i] = zug[i].charAt(0) - 'a';
					y[i] = zug[i].charAt(1) - '0' - 1;

					System.out.println(x[i] + "   " + y[i]);
				}

				ArrayList<Spielzug> alle_züge = b.giballeZüge(Farbe.values()[p]);

				Spielzug z = new Spielzug(x[0], y[0], new Zug(x[1] - x[0], y[1] - y[0]));

				if (z.isPartOf(alle_züge)) {
					b.ziehe(z);
					p = 1 - p;
				} else {
					sendMessage("Das Geht doch garned!", e.getChannel());
				}
			}
		}

		File f = new File("brett.png");
		try {
			ImageIO.write(b.asBild(Farbe.values()[1 - p]), "png", f);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		e.getChannel().sendFile(f).queue();
	}

	public void saveStringAsWav(String s, String filename) {
		Voice voice = VoiceManager.getInstance().getVoice("kevin16");
		if (voice != null) {
			voice.allocate();

			AudioPlayer audioplayer = new SingleFileAudioPlayer(filename, Type.AU);
			audioplayer.setAudioFormat(Format);
			voice.setAudioPlayer(audioplayer);
			try {

				voice.setRate(150);
				voice.setPitch(120);
				voice.setVolume(3);
				voice.speak(s);

			} catch (Exception e) {
				e.printStackTrace();
			}

			audioplayer.close();
		}
	}

	public void saveZitatAsWav(Zitat z, String filename) {
		saveStringAsWav(z.getAll(), filename);
	}

	public void saveRandomZitatAsTTSOutputWav() {
		saveZitatAsWav(randomZitat(), "TTSOutput");
	}

	public void cmdToggleRandomZitatAudio(GuildMessageReceivedEvent e, String[] cmd_body) {
		e.getChannel().sendMessage("gibts noch ned, Vollpfosten!");

		randomZitatAudio = (randomZitatAudio) ? false : true;

		if (manager == null) {
			manager = e.getGuild().getAudioManager();

			manager.setSendingHandler(this);
		}

		if (randomZitatAudio) {
			randomlyJoinRandomOccupiedChannel(e);
		} else {
			t.cancel();
		}
	}

	public VoiceChannel randomOccupiedVoiceChannel(GuildMessageReceivedEvent e) {
		List<VoiceChannel> vcs = e.getGuild().getVoiceChannels();
		ArrayList<VoiceChannel> ocVcs = new ArrayList<VoiceChannel>();

		for (int i = 0; i < vcs.size(); i++) {
			if (vcs.get(i).getMembers().size() != 0) {
				ocVcs.add(vcs.get(i));
			}
		}

		VoiceChannel erg = ocVcs.get((int) (Math.random() * ocVcs.size()));

		return erg;
	}

	public void joinChannel(GuildMessageReceivedEvent e, VoiceChannel vc) {
		TextChannel tc = e.getChannel();

		if (!e.getGuild().getSelfMember().hasPermission(tc, Permission.VOICE_CONNECT)) {
			tc.sendMessage("Darf nicht connecten, Depp!").queue();
			return;
		}

		if (vc == null) {
			tc.sendMessage("gibt keinen VoiceChannel, Doofbeutel!").queue();
		}

		e.getGuild().getAudioManager().openAudioConnection(vc);
	}

	public void joinRandomOccupiedChannel(GuildMessageReceivedEvent e) {
		joinChannel(e, randomOccupiedVoiceChannel(e));
	}

	public void randomlyJoinRandomOccupiedChannel(GuildMessageReceivedEvent e) {
		t = new Timer();
		t.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				if (Math.random() < probability) {
					VoiceChannel vc = randomOccupiedVoiceChannel(e);

					saveRandomZitatAsTTSOutputWav();
					manager.openAudioConnection(vc);
					play("TTSOutput.opus");
				}
			}

		}, 0, periodMillis);
	}

	public void leaveChannel(GuildMessageReceivedEvent e) {
		e.getGuild().getAudioManager().closeAudioConnection();
	}

	public void play(String path) {
		try {
			File f = new File(path);
			InputStream ais = new FileInputStream(f);

			int length = (int) f.length();

			float sample_len = 1000f / Format.getFrameRate();
			int sample_size = Format.getFrameSize();

			int delta = Math.round(sample_size * (20f / sample_len));

			for (int i = 0; i < length; i += delta) {
				if (i + delta < length) {
					byte[] temp = new byte[delta];

					ais.read(temp, 0, delta);

					bb.add(ByteBuffer.wrap(temp));
				}
			}

			ais.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean canProvide() {
		return bb.size() != 0;
	}

	public ByteBuffer provide20MsAudio() {
		ByteBuffer snippet = bb.get(bb.size() - 1);
		bb.remove(bb.size() - 1);
		System.out.println(bb.size());
		return snippet;
	}

	public boolean isOpus() {
		return true;

	}

	public boolean isMainBot(Guild g) {
		if (g.getSelfMember().getId() == "853385178067501066") {
			return true;
		} else {
			return false;
		}
	}

	public void cmdConfig(GuildMessageReceivedEvent e, String[] cmd_body) {
		System.out.println("config");
		if (cmd_body.length != 3) {
			sendMessage(config.getFormattedConfig(), e.getChannel());
			sendMessage(
					"dumm? du brauchst doch die Argumente: 1/2 (main-bot oder zweiter), gemeinter command, 0/1 (an/aus)",
					e.getChannel());
			return;
		}

		if ((isMainBot(e.getGuild()) && cmd_body[0].equals("2"))
				|| (!isMainBot(e.getGuild()) && cmd_body[0].equals("1"))) {
			return;
		}

		if (!e.getMember().getId().equals("426029391009677313")
				&& !e.getMember().getId().equals("434312954524073986")) {
			sendMessage("quod liket Iovis non liket bovis", e.getMessage().getTextChannel());
			return;
		}

		if (cmd_body[1].equals("config")) {
			sendMessage("no", e.getChannel());
			return;
		}

		config.set(cmd_body[1], cmd_body[2] == "1" ? true : false);

		if (cmd_body[1].equals("all")) {
			config.setAll((cmd_body[2].equals("0")) ? false : true);
		}

		sendMessage(config.getFormattedConfig(), e.getChannel());
	}

	public void giveRole(Guild g, Member m, String role) {
		Role r = null;
		if (g.getRolesByName(role, true).size() > 0) {
			r = g.getRolesByName(role, true).get(0);
			System.out.println("Rolle vorhanden");
		} else {
			r = g.createRole().setName(role).complete();
			System.out.println("Rolle erstellt");
		}
		if (!m.getRoles().contains(r)) {
			g.addRoleToMember(m, r).complete();
			System.out.println("Rolle zu " + m.getUser().getName() + " hinzugefuegt");
		}
	}

	public void removeRole(Guild g, Member m, String role) {
		Role r = null;
		try {
			r = g.getRolesByName(role, true).get(0);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("gibt die Rolle nicht");
			return;
		}
		if (m.getRoles().contains(r)) {
			g.removeRoleFromMember(m, r).complete();
			System.out.println("Rolle genommen: " + m.getUser().getName());
		}
	}

	public void cmdParticipate(GuildMessageReceivedEvent e, String[] cmd_body) {
		Member m = e.getGuild().getMember(e.getAuthor());
		if (!acceptParticipation.contains(m)) {
			sendMessage(m.getAsMention() + "Wenn du mitmachen willst, musst du trotzdem wirklich EHRLICH Zitate raten, "
					+ "auch wenn es verlockend wäre, manche zu bevorzugen, aber dann machst du es kaputt :( "
					+ "Stimmst du zu? tippe <accept. Willst du nicht mitmachen, dann tippe <decline. "
					+ "Falls du jemals wieder aussteigen möchtest, schreibe zweimal nacheinander <leaveSupercoolesEvent. "
					+ "!Achtung! dein Inventar wird gelöscht!", e.getChannel());

			acceptParticipation.add(m);
		}
	}

	public void cmdAccept(GuildMessageReceivedEvent e, String[] cmd_body) {
		Member m = e.getGuild().getMember(e.getAuthor());
		String id = e.getAuthor().getId();
		if (acceptParticipation.contains(m)) {
			acceptParticipation.remove(m);
			if (userinfo.get(id, "inventory", Inventory.class) == null) {
				userinfo.put(id, "inventory", new Inventory(0, new ArrayList<Zitat>(), new ArrayList<LootBox>(),
						new HashMap<Challenge, Boolean>()));
			}
			giveRole(e.getGuild(), m, "Gnocci-Gang");
			sendMessage(m.getAsMention() + "Du bist dabei!", e.getChannel());
		}
	}

	public void cmdDecline(GuildMessageReceivedEvent e, String[] cmd_body) {
		Member m = e.getGuild().getMember(e.getAuthor());
		if (acceptParticipation.contains(m)) {
			acceptParticipation.remove(m);
			sendMessage(m.getAsMention() + "Dann halt nicht. So nervig einfach", e.getChannel());
		}
	}

	public void cmdLeaveEvent(GuildMessageReceivedEvent e, String[] cmd_body) {
		Member m = e.getGuild().getMember(e.getAuthor());
		String Id = e.getAuthor().getId();
		if (leaveEvent.contains(m)) {
			if (userinfo.get(Id, "inventory", Inventory.class) != null) {
				userinfo.put(Id, "inventory", null);
			}
			removeRole(e.getGuild(), m, "Gnocci-Gang");
			sendMessage(m.getAsMention() + "Du machst dich vom Acker", e.getChannel());
		} else {
			leaveEvent.add(m);
			sendMessage(m.getAsMention() + "Sicher?", e.getChannel());
		}
	}

	public void cmdTrade(GuildMessageReceivedEvent e, String[] cmd_body) {
		double coins = Double.parseDouble(cmd_body[1]);
		ArrayList<Zitat> ang = new ArrayList<Zitat>(), ford = new ArrayList<Zitat>();
		Member fromM = e.getGuild().getMember(e.getAuthor()), toM = null;
		Inventory fromInv = userinfo.get(fromM.getId(), "inventory", Inventory.class), toInv = null;
		Role r = null;
		try {
			toM = e.getGuild().getMember(e.getJDA().getUsersByName(cmd_body[0], true).get(0));
			toInv = userinfo.get(toM.getId(), "inventory", Inventory.class);
			r = e.getGuild().getRolesByName("Gnocci-Gang", true).get(0);
			if (!fromM.getRoles().contains(r) || !toM.getRoles().contains(r)) {
				throw new GnocciGangException();
			}
		} catch (ArrayIndexOutOfBoundsException | NullPointerException e1) {
			e1.printStackTrace();
			System.out.println("user " + cmd_body[0] + " nicht gefunden");
			return;
		} catch (GnocciGangException e1) {
			e1.printStackTrace();
			return;
		}

		if (!cmd_body[2].equals("n")) {
			String[] s = cmd_body[2].split(",");
			for (int i = 0; i < s.length; i++) {
				ang.add(fromInv.getZitate().get(Integer.parseInt(s[i])));
			}
		}

		if (!cmd_body[3].equals("n")) {
			String[] s = cmd_body[3].split(",");
			for (int i = 0; i < s.length; i++) {
				ford.add(toInv.getZitate().get(Integer.parseInt(s[i])));
			}
		}

		TradeOffer t = new TradeOffer(e.getGuild(), userinfo, fromM.getUser(), toM.getUser(), ang, ford, coins);

		if (userinfo.get("guild", "trades", HashMap.class) == null) {
			userinfo.put("guild", "trades", new HashMap<Message, TradeOffer>());
		}

		try {
			String p = "tradeoffer0.png";
			File tradeoffer = new File(p);
			ImageIO.write(t.getAsImage(), "png", tradeoffer);
			e.getChannel().sendFile(tradeoffer, p).append(toM.getAsMention()).queue(m -> {
				m.addReaction("👍").queue();
				m.addReaction("👎").queue();
				userinfo.get("guild", "trades", HashMap.class).put(m, t);
			});

		} catch (IOException | IllegalArgumentException e1) {
			e1.printStackTrace();
		}

	}

	public void cmdAcceptTrade(GuildMessageReactionAddEvent e, String[] cmd_body) {
		User m = e.getUser();
		Message msg = e.retrieveMessage().complete();
		try {
			TradeOffer t = (TradeOffer) userinfo.get("guild", "trades", HashMap.class).get(msg);
			if (t.getToUser().equals(m)) {
				if (userinfo.get(m.getId(), "inventory", Inventory.class).getCoins() >= -t.getCoinBalance()) {
					t.execute();
				} else {
					msg.removeReaction(Emoji.thumbsup.code, m).complete();
//					userinfo.get("guild", "trades", HashMap.class).remove(msg);
				}
			}
		} catch (Exception x) {
			x.printStackTrace();
			return;
		}
	}

	public void cmdDeclineTrade(GuildMessageReactionAddEvent e, String[] cmd_body) {
		User m = e.getUser();
		Message msg = e.retrieveMessage().complete();
		try {
			TradeOffer t = (TradeOffer) userinfo.get("guild", "trades", HashMap.class).get(msg);
			if (t.getToUser().equals(m)) {
				userinfo.get("guild", "trades", HashMap.class).remove(msg);
				msg.delete().complete();
			}
		} catch (Exception x) {
			x.printStackTrace();
			return;
		}
	}

	public void cmdInventory(GuildMessageReceivedEvent e, String[] cmd_body) {
		Member m = e.getGuild().getMember(e.getAuthor());
		if (!m.getRoles().contains(e.getGuild().getRolesByName("Gnocci-Gang", true).get(0))) {
			System.out.println("nicht Gnocci-Gang");
			return;
		}

		Inventory inv = userinfo.get(e.getAuthor().getId(), "inventory", Inventory.class);

		sendScrollMessage("Inventar von " + m.getAsMention(), "" + (int) inv.getCoins(), inv.toFormat(m),
				e.getChannel());

	}

	public void cmdShop(GuildMessageReceivedEvent e, String[] cmd_body) {
		Member m = e.getGuild().getMember(e.getAuthor());
		if (!m.getRoles().contains(e.getGuild().getRolesByName("Gnocci-Gang", true).get(0))) {
			System.out.println("nicht Gnocci-Gang");
			return;
		}

		Shop shop = userinfo.get(m.getUser().getId(), "shop", Shop.class);
		if (shop == null) {
			shop = userinfo.get("guild", "shop", Shop.class);
			if (shop == null) {
				shop = new Shop(userinfo, 1, null, 0, null);
				userinfo.put("guild", "shop", shop);
				shop.refresh();
			}
			shop.cloneTo(m.getUser());
			shop = userinfo.get(m.getUser().getId(), "shop", Shop.class);
		}

		sendScrollMessage("Shop für " + m.getAsMention(),
				"Nächste Reset in " + (int) shop.getNextResetInMins() + " Minuten", shop.toFormat(), e.getChannel());
	}

	public void cmdaddTag(GuildMessageReceivedEvent e, String[] cmd_body) {
		ArrayList<String> tags = userinfo.get("guild", "tags", ArrayList.class);
		if (tags == null)
			tags = new ArrayList<String>();
		tags.add(cmd_body[0]);
		userinfo.put("guild", "tags", tags);

		sendMessage("Der Tag " + cmd_body[0] + " wurde hinzugefügt", e.getChannel());
	}

	public void cmdremoveTag(GuildMessageReceivedEvent e, String[] cmd_body) {
		ArrayList<String> tags = userinfo.get("guild", "tags", ArrayList.class);
		if (tags == null)
			tags = new ArrayList<String>();
		tags.remove(cmd_body[0]);
		userinfo.put("guild", "tags", tags);

		sendMessage("Der Tag " + cmd_body[0] + " wurde gelöscht", e.getChannel());
	}

	public void cmdshowTags(GuildMessageReceivedEvent e, String[] cmd_body) {
		ArrayList<String> tags = userinfo.get("guild", "tags", ArrayList.class);
		if (tags == null)
			tags = new ArrayList<String>();

		String list = "";
		for (int i = 0; i < tags.size(); i++) {
			list += (i + 1) + ". " + tags.get(i);
			if (i != tags.size() - 1)
				list += ", ";
		}
		sendMessage(list, e.getChannel());
	}

	public void cmdassignTag(GuildMessageReceivedEvent e, String[] cmd_body) {
		ArrayList<String> tags = userinfo.get("guild", "tags", ArrayList.class);
		if (tags == null)
			tags = new ArrayList<String>();

		String Id = e.getAuthor().getId();
		Zitat z;
		if ((z = userinfo.get(Id, "assign", Zitat.class)) == null) {
			if (cmd_body.length == 0 || !cmd_body[0].matches("\\d+")) {
				ArrayList<Zitat> notAssigned = new ArrayList<Zitat>();
				for (Zitat zitat : zitate) {
					if (zitat.getTags().size() == 0)
						notAssigned.add(zitat);
				}
				System.out.println(notAssigned.size());

				if (notAssigned.size() == 0) {
					notAssigned = zitate;
				}
				z = notAssigned.get((int) (Math.random() * notAssigned.size()));
			} else {
				z = zitate.get(Integer.parseInt(cmd_body[0]));
			}
			sendMessage("Welche Tags sollte folgendes Zitat besitzen? \n" + z.getAll(), e.getChannel());
		} else {
			if (cmd_body.length > 0)
				if (cmd_body[0].matches("(\\d+,)*\\d+")) {
					String[] split = cmd_body[0].split(",");
					for (int i = 0; i < split.length; i++) {
						int idx = Integer.parseInt(split[i]) - 1;
						if (idx < tags.size()) {
							z.addTag(tags.get(idx));
						}
					}
					sendMessage("Das Zitat: \n" + z.getAll() + "\nhat jetzt die Tags: " + z.getTags().toString(),
							e.getChannel());
					z = null;
				} else if (cmd_body[0].matches("(.+,)*.+")) {
					String[] split = cmd_body[0].split(",");
					for (int i = 0; i < split.length; i++) {
						if (tags.contains(split[i])) {
							z.addTag(split[i]);
						}
					}
					sendMessage("Das Zitat: \n" + z.getAll() + "\nhat jetzt die Tags: " + z.getTags().toString(),
							e.getChannel());
					z = null;
				}

		}
		userinfo.put(Id, "assign", z);
	}

	public void cmdResolveActionMessages(GuildMessageReactionAddEvent e, String[] cmd_body) {
		Message m = e.getChannel().retrieveMessageById(e.getMessageId()).complete();
		for (ActionMessage am : (ArrayList<ActionMessage>) userinfo.get("guild", "ActionMessages", ArrayList.class)) {
			if (am.getMessage().equals(m))
				am.resolveReaction(e.getUser(), Emoji.fromUnicode(cmd_body[0]));
		}
	}

	public void updateMeinkraft(Integer i, String[] cmd_body) {
		try {
			URL whatismyip = new URL("http://checkip.amazonaws.com");
			BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));

			String ip = in.readLine();
			String old_ip = userinfo.get("meinkraft", "IP", String.class);

			if (!ip.equals(old_ip)) {
				String m = null;
				TextChannel meinkraft = g.getTextChannelsByName("meinkraft", true).get(0);
				if ((m = userinfo.get("meinkraft", "Message", String.class)) != null) {
					meinkraft.deleteMessageById(m).queue();
				}
				;

				sendMessage(g.getRolesByName("Minecraft", true).get(0).getAsMention() + ": Die neue IP ist " + ip,
						meinkraft, message -> {
							userinfo.put("meinkraft", "Message", message.getId());
						});
				userinfo.put("meinkraft", "IP", ip);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void cmdOwTeam(GuildMessageReceivedEvent e, String[] cmd_body) {
		boolean inVc = false;
		
		List<VoiceChannel> l =e.getGuild().getVoiceChannels();
		for (VoiceChannel VC : l) { 
			if(VC.getMembers().contains(e.getMember())) {
				
				inVc = true;
				
				List<Member> temp = VC.getMembers();
				ArrayList<String> team1 = new ArrayList<>();
				
				for (Member m : temp) {					
						team1.add(m.getEffectiveName());
					
				}
				
				  ArrayList<String> team2 = new ArrayList<>();
				  int laenge = team1.size()-1;
				  for(int i = 0; i <= (0.5* laenge);i++) {
					  int zahl = (int)(Math.random()*team1.size());
					  team2.add(team1.get(zahl));
					  team1.remove(zahl);
				  }
				  
				  
				sendMessage("**Team 1:** "+team2.toString()+"\n**Team 2:** "+team1.toString(), e.getChannel());
				
				  
				  
			}															
		}
				if(inVc == false) {
					sendMessage("Du bist nicht im Voice Channel!", e.getChannel());
				}
		
		
	}
}
