package discord;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.security.auth.login.LoginException;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.audio.AudioPlayer;
import com.sun.speech.freetts.audio.SingleFileAudioPlayer;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import spiel.Brett;
import spiel.Figur.Farbe;
import spiel.Spielzug;
import spiel.Zug;

public class ZitatMaster extends Bot {

	List<Zitat> zitate;

	Game game;

	Map<String, Integer[]> scores;

	final String path = "zitat_scores";
	final int K = 400;

	Map<String, Zitat[]> rating;

	Brett b;
	int p;

	boolean randomZitatAudio = false;
	Timer t;
	final int periodMillis = 500000;
	final double probability = 1;

	AudioHandler audio;
	AudioManager manager;

	Map<String, Boolean> access;

	public boolean spielActive = true, statsActive = true, topActive = true, rateActive = true, schachActive = true,
			trzaActive = true, loliusActive = true, forceRateActive = true;

	public ZitatMaster(String token) throws LoginException {
		super(token);
		setPresence(OnlineStatus.ONLINE, Activity.playing("eh keine Rolle"));
		scores = new HashMap<String, Integer[]>();
		rating = new HashMap<String, Zitat[]>();
		access = new HashMap<String, Boolean>();
		addListener(new Listener('<', this, new Command(this::cmdSpiel, "spiel", "s"),
				new Command(this::cmdGuess, "guess", "g"), new Command(this::cmdErgebnisse, "ergebnisse", "e"),
				new Command(this::cmdSkip, "skip"), new Command(this::cmdStats, "stats", "st"),
				new Command(this::cmdRate, "rate", "r"), new Command(this::cmdTop, "top", "t"),
				new Command(this::cmdSchach, "schach"), new Command(this::cmdToggleRandomZitatAudio,
						"toggleRandomZitatAudio", "togglerandomzitataudio", "trza"),
				new Command(this::cmdLolius, "lolius"), new Command(this::cmdConfig, "config")));
	}

	public void sendMessage(String msg, TextChannel channel) {
		channel.sendMessage(msg).queue();
	}
 
	public Zitat getZitat(String ID) {
		for (int i = 0; i < zitate.size(); i++) {
			if (zitate.get(i).getID().contentEquals(ID))
				return zitate.get(i);
		}
		return null;
	}

	public Set<Entry<String, Integer>> getAutorStats() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		zitate.forEach(z -> {
			String a = z.autor.toLowerCase();
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
			String s = z.schreiber;
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
			String a = z.autor.toLowerCase();
			if (z.inhalt.contains(word)) {
				if (map.get(a) == null) {
					map.put(a, 1);
				} else {
					map.put(a, map.get(a) + 1);
				}
			}
		});

		return map.entrySet();
	}

	public void loadScores() {
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

	public List<Zitat> loadZitate(Guild g) {
		zitate = new ArrayList<Zitat>();
		scores = new HashMap<String, Integer[]>();

		List<Message> messages = new ArrayList<Message>();

		TextChannel channel = g.getTextChannelsByName("zitate", true).get(0);

		String latest = MessageHistory.getHistoryFromBeginning(channel).limit(1).complete().getRetrievedHistory().get(0)
				.getId();
		String curr = channel.getLatestMessageId();

		MessageHistory ms = channel.getHistoryAround(curr, 1).complete();

//		System.out.println(channel.retrieveMessageById(latest).complete().getContentRaw());
//		System.out.println(channel.retrieveMessageById(curr).complete().getContentRaw());

		System.out.println(latest + "   " + curr);

		while (latest.equals(curr) == false) {
			ms = channel.getHistoryBefore(curr, 100).complete();
			messages.addAll(ms.getRetrievedHistory());
			curr = ms.getRetrievedHistory().get(ms.size() - 1).getId();
			System.out.println(channel.retrieveMessageById(curr).complete().getContentRaw());
			System.out.println(ms.getRetrievedHistory().size());
		}

		List<Message> h = new ArrayList<Message>();

		for (int i = messages.size() - 1; i >= 0; i--) {
			h.add(messages.get(i));
		}

		h.forEach(m -> {
			Zitat z = new Zitat(m);
			if (z.isFull()) {
				zitate.add(z);
				scores.put(z.getID(), new Integer[] { 0, 0, 0 });
			}
		});

		loadScores();

		return zitate;
	}

	public Zitat randomZitat() {
		Zitat z = zitate.get((int) (Math.random() * zitate.size()));
		return z;
	}

	public int lowestZitat() {
		int min = (int) Double.POSITIVE_INFINITY;
		for (Zitat z : zitate) {
			String ID = z.getID();
			int n = scores.get(ID)[1];

			if (n < min) {
				min = n;
			}

		}

		return min;
	}

	public Zitat get_lOR_Zitat(int n) {
		List<Zitat> temp = new ArrayList<Zitat>();
		for (int i = 0; i < zitate.size(); i++) {
			String ID = zitate.get(i).getID();

			if (scores.get(ID)[1] == n) {
				temp.add(zitate.get(i));
			}

		}

		if (temp.size() == 0) {
			return get_lOR_Zitat(n + 1);
		} else {
			return temp.get((int) (Math.random() * temp.size()));
		}

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
		if (!spielActive) {
			return;
		}

		int len = cmd_body[0] != null ? Integer.parseInt(cmd_body[0]) : 1;
		game = new Game(len, e.getGuild(), this);
		sendMessage(game.challenge(), e.getChannel());
	}

	public void cmdGuess(GuildMessageReceivedEvent e, String[] cmd_body) {
		if (!spielActive) {
			return;
		}

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

	public void cmdStats(GuildMessageReceivedEvent e, String[] cmd_body) {
		if (!statsActive) {
			return;
		}

		String erg = "";
		if (cmd_body.length > 0) {
			switch (cmd_body[0]) {
			case "autor":
				erg = toString(getAutorStats(), true, true, false);
				break;
			case "schreiber":
				erg = toString(getSchreiberStats(), true, true, false);
				break;
			case "everyone":
				erg = zitate.size() + "\n" + zitate.get(zitate.size() - 1).inhalt;
				break;
			default:
				erg = toString(getStatsFor(cmd_body[0]), true, true, false);
			}
			sendMessage(erg, e.getChannel());
		}
	}

	public void cmdSkip(GuildMessageReceivedEvent e, String[] cmd_body) {
		if (!spielActive) {
			return;
		}

		String erg = "";
		if (game == null) {
			erg = "Ey du Popokopf, welches Spiel?";
		} else if (game.isOver()) {
			erg = "Das Spiel is doch schon vorbei du Intelligenz Alleskika";
		} else {
			erg = "Die richtige Antwort war " + game.antwort() + ", du Geringverdiener\n" + game.challenge();
			game.c--;

		}
		sendMessage(erg, e.getChannel());

	}

	public void cmdErgebnisse(GuildMessageReceivedEvent e, String[] cmd_body) {
		if (!spielActive) {
			return;
		}

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

	public void cmdRate(GuildMessageReceivedEvent e, String[] cmd_body) {
		if (!rateActive) {
			return;
		}

		loadScores();
		String erg = "";
		String name = e.getAuthor().getName();
		int c = 0;
		if (cmd_body.length > 0 && cmd_body[0].equalsIgnoreCase("all")) {
			name = "all";
			c++;
		}

		System.out.println(name);
		if (rating.get(name) == null) {
			Zitat[] temp = new Zitat[2];

			int n = lowestZitat();

			temp[0] = get_lOR_Zitat(n);
			do {
				temp[1] = get_lOR_Zitat(n++);
			} while (temp[0].equals(temp[1]));

			erg = temp[0].getAll() + "\n  or  \n" + temp[1].getAll() + "\n" + "@" + name;

			rating.put(name, temp);
		} else {
			if (cmd_body.length > 0 && cmd_body[c] != null) {
				int r = Integer.parseInt(cmd_body[c]) - 1;

				if (r == 0 || r == 1) {
					Zitat[] temp = rating.get(name);

					scores.get(temp[r].getID())[0] += 1;

					for (int i = 0; i < 2; i++) {
						scores.get(temp[i].getID())[1] += 1;
					}

					int Ra = scores.get(temp[r].getID())[2];
					int Rb = scores.get(temp[1 - r].getID())[2];

					double expected = 1 / (1 + Math.pow(10, (Ra - Rb) / 400));

					int change = (int) (K * (1 - expected));

					scores.get(temp[r].getID())[2] += change;
					scores.get(temp[1 - r].getID())[2] -= change;

					erg = "Voted for: " + temp[r].getAll();

					rating.put(name, null);

					access.put(name, true);
				} else {
					erg = "Zwischen 1 und 2 du Evolutionsbremse";
				}
			} else {
				erg = "richtige Zahlen du Hurensohn";
			}
		}

		sendMessage(erg, e.getChannel());

		saveScores();
	}

	public void cmdTop(GuildMessageReceivedEvent e, String[] cmd_body) {
		if (!topActive) {
			return;
		}

		loadScores();
		String erg = "";
		int top = 0;
		if (cmd_body.length > 0 && cmd_body[0] != null) {
			try {
				top = top < zitate.size() ? Integer.parseInt(cmd_body[0]) : zitate.size() - 1;
				if (top < 0)
					top = 1;
			} catch (NumberFormatException ex) {
				top = 10;
			}
		} else {
			top = 10;
		}

		Map<String, Integer> map = new HashMap<String, Integer>();

		for (Entry<String, Integer[]> entry : scores.entrySet()) {
			Integer[] score = entry.getValue();
			map.put(entry.getKey(), score[2]);
		}

		String sorted = toString(map.entrySet(), true, false, true);

		String[] best = sorted.split(",");

		for (int i = top > 10 ? top - 10 : 0; i < top; i++) {
			erg += i + ". " + getZitat(best[i]).getAll() + " :" + (map.get(best[i])) + " aus " + scores.get(best[i])[1]
					+ "\n";
		}

		sendMessage(erg, e.getChannel());
	}

	public void cmdSchach(GuildMessageReceivedEvent e, String[] cmd_body) {
		if (!schachActive) {
			return;
		}

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

	public Member getRandomMemberNotNick(GuildMessageReceivedEvent e, String nick) {
		// System.out.println(e.getGuild(). + " " + e.getGuild().getMemberCount());

		List<Role> r = e.getGuild().getRoles();
		Role[] allowed = new Role[3];
		int curr = 0;

		for (int i = 0; i < r.size(); i++) {
			if (r.get(i).getName().equalsIgnoreCase("Big Potato") || r.get(i).getName().equalsIgnoreCase("Middl Potato")
					|| r.get(i).getName().equalsIgnoreCase("Big Jucy Potato")) {
				allowed[curr] = r.get(i);
				curr++;
			}
		}

		List<Member> m0 = e.getGuild().getMembersWithRoles(allowed[0]);
		System.out.println(m0.toString());
		List<Member> m1 = e.getGuild().getMembersWithRoles(allowed[1]);
		System.out.println(m1.toString());
		List<Member> m2 = e.getGuild().getMembersWithRoles(allowed[2]);
		System.out.println(m2.toString());

		for (int i = 0; i < m1.size(); i++) {
			m0.add(m1.get(i));
		}
		System.out.println(m0.toString());

		for (int i = 0; i < m2.size(); i++) {
			m0.add(m2.get(i));
		}
		System.out.println(m0.toString());

		boolean allNick = true;
		for (int i = 0; i < m0.size(); i++) {
			if (!m0.get(i).getEffectiveName().equals(nick)) {
				allNick = false;
			}
		}

		Member erg = null;

		if (!allNick) {
			while (erg == null || erg.getEffectiveName().equals(nick)) {
				int i = (int) (Math.random() * m0.size());
				erg = m0.get(i);
			}
		}

		System.out.println(erg.toString());

		return erg;
	}

	public void cmdLolius(GuildMessageReceivedEvent e, String[] cmd_body) {
		if (!loliusActive) {
			return;
		}

		System.out.println(e.getGuild().getSelfMember().getId());

		String nick = "lolius";

		if (cmd_body.length > 0) {
			nick = "";
			for (int i = 0; i < cmd_body.length; i++) {
				nick += cmd_body[i];
				nick += " ";
			}
		}

		try {
			getRandomMemberNotNick(e, nick).modifyNickname(nick).queue();
			// System.out.println(e.getGuild().getMemberById("426029391009677313").toString());
			// e.getGuild().getMemberById("426029391009677313").modifyNickname("Lolius").queue();
		} catch (Exception x) {
			x.printStackTrace();
		}

	}

	public void saveStringAsWav(String s, String filename) {
		Voice voice = VoiceManager.getInstance().getVoice("kevin16");
		if (voice != null) {
			voice.allocate();

			AudioPlayer audioplayer = new SingleFileAudioPlayer(filename, Type.WAVE);
			audioplayer.setAudioFormat(AudioHandler.Format);
			voice.setAudioPlayer(audioplayer);
			try {

				voice.setRate(150);
				;
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
		if (!trzaActive) {
			return;
		}

		e.getChannel().sendMessage("gibts noch ned, Vollpfosten!");

		randomZitatAudio = (randomZitatAudio) ? false : true;

		if (audio == null || manager == null) {
			manager = e.getGuild().getAudioManager();
			audio = new AudioHandler();

			manager.setSendingHandler(audio);
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
					audio.play("TTSOutput.wav");

				}
			}

		}, 0, periodMillis);
	}

	public void leaveChannel(GuildMessageReceivedEvent e) {
		e.getGuild().getAudioManager().closeAudioConnection();
	}

	public boolean access(String name) {
		if (access.get(name) == null) {
			access.put(name, false);
			return false;
		} else {
			return access.get(name);
		}
	}

	public boolean isMainBot(GuildMessageReceivedEvent e) {
		if (e.getGuild().getSelfMember().getId().equals("853385178067501066")) {
			return false;
		}
		return true;
	}

	public String formatConfig() {
		String erg = "momentane Konfiguration:" + "  ";

		erg += "spiel: " + (spielActive ? "an" : "aus") + " | ";
		erg += "stats: " + (statsActive ? "an" : "aus") + " | ";
		erg += "top: " + (topActive ? "an" : "aus") + " | ";
		erg += "rate: " + (rateActive ? "an" : "aus") + " | ";
		erg += "schach: " + (schachActive ? "an" : "aus") + " | ";
		erg += "trza: " + (trzaActive ? "an" : "aus") + " | ";
		erg += "lolius: " + (loliusActive ? "an" : "aus") + " | ";
		erg += "Force-Rate: " + (forceRateActive ? "an" : "aus");

		return erg;
	}

	public void cmdConfig(GuildMessageReceivedEvent e, String[] cmd_body) {

		if (cmd_body.length != 3) {
			sendMessage(formatConfig(), e.getMessage().getTextChannel());
			sendMessage(
					"dumm? du brauchst doch die Argumente: 1/2 (main-bot oder zweiter), gemeinter command, 0/1 (an/aus)",
					e.getMessage().getTextChannel());
			return;
		}
		
		if (!e.getMember().getId().equals("426029391009677313") && !e.getMember().getId().equals("434312954524073986")) {
			sendMessage("quod liket Iovis non liket bovis", e.getMessage().getTextChannel());
			return;
		}

		if ((cmd_body[0].equals("1") && isMainBot(e)) || (cmd_body[0].equals("2") && !isMainBot(e))) {
			switch (cmd_body[1]) {
			case "spiel":
				spielActive = cmd_body[2].equals("0") ? false : true;
				break;
			case "stats":
				statsActive = cmd_body[2].equals("0") ? false : true;
				break;
			case "top":
				topActive = cmd_body[2].equals("0") ? false : true;
				break;
			case "rate":
				rateActive = cmd_body[2].equals("0") ? false : true;
				break;
			case "schach":
				schachActive = cmd_body[2].equals("0") ? false : true;
				break;
			case "trza":
				trzaActive = cmd_body[2].equals("0") ? false : true;
				break;
			case "lolius":
				loliusActive = cmd_body[2].equals("0") ? false : true;
				break;
			case "forcerate":
				forceRateActive = cmd_body[2].equals("0") ? false : true;
				break;
			case "all":
				spielActive = cmd_body[2].equals("0") ? false : true;
				statsActive = cmd_body[2].equals("0") ? false : true;
				topActive = cmd_body[2].equals("0") ? false : true;
				rateActive = cmd_body[2].equals("0") ? false : true;
				schachActive = cmd_body[2].equals("0") ? false : true;
				trzaActive = cmd_body[2].equals("0") ? false : true;
				loliusActive = cmd_body[2].equals("0") ? false : true;
				forceRateActive = cmd_body[2].equals("0") ? false : true;
			default:
				break;
			}
		}

		sendMessage(formatConfig(), e.getMessage().getTextChannel());
	}
}
