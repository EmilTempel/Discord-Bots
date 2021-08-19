package handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import commands.Command;
import commands.MessageCommand;
import discord.UserInformation;
import discord.Zitat;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ZitatHandler extends Handler{
	
	ArrayList<Zitat> zitate;
	Map<String, Integer[]> scores;

	final String path = "zitat_scores";
	final int K = 400;
	
	public ZitatHandler(Guild g, UserInformation userinfo) {
		super(g,userinfo);
		commands = new Command[]{
				new MessageCommand('<', new String[] {"stats"}, new String[][] {new String[] {"\\w+"}},this::cmdStats),
				new MessageCommand('"', null, new String[][] {null}, (e,s) -> loadZitate()),
				new MessageCommand('<', new String[] { "rate", "r" },
						new String[][] { new String[] {}, new String[] { "[1-2]" } }, this::cmdRate),
				new MessageCommand('<', new String[] { "top" },
						new String[][] { new String[] {}, new String[] { "\\d+" } }, this::cmdTop)
		};
		loadZitate();
	}
	
	public ArrayList<Zitat> getZitate(){
		ArrayList<Zitat> temp = new ArrayList<Zitat>();
		for(Zitat z : zitate) {
			temp.add(z);
		}
		return temp;
	}

	public Zitat getZitat(String ID) {
		for (int i = 0; i < zitate.size(); i++) {
			if (zitate.get(i).getID().contentEquals(ID))
				return zitate.get(i);
		}
		return null;
	}

	public Zitat randomZitat() {
		Zitat z = zitate.get((int) (Math.random() * zitate.size()));
		return z;
	}
	
	public List<Zitat> loadZitate() {
		zitate = new ArrayList<Zitat>();
		scores = new HashMap<String,Integer[]>();
		

		List<Message> messages = new ArrayList<Message>();
		
		TextChannel channel = g.getTextChannelsByName("zitate", true).get(0);

		String latest = MessageHistory.getHistoryFromBeginning(channel).limit(1).complete().getRetrievedHistory().get(0)
				.getId();
		String curr = channel.getLatestMessageId();

		MessageHistory ms = channel.getHistoryAround(curr, 1).complete();

//		System.out.println(channel.retrieveMessageById(latest).complete().getContentRaw());
//		System.out.println(channel.retrieveMessageById(curr).complete().getContentRaw());

		messages.add(channel.retrieveMessageById(curr).complete());
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
				scores.put(z.getID(),new Integer[] {0,0,0});
			}
		});
		saveScores();
		loadScores();
		
		System.out.println("successfully loaded");
		return zitate;
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
		loadScores();
		String erg = "";
		String id = e.getAuthor().getId();
		int c = 0;
		if (cmd_body.length > 0 && cmd_body[0].equalsIgnoreCase("all")) {
			id = "all";
			c++;
		}

		System.out.println(id);
		if (userinfo.get(id, "rating", Object.class) == null) {
			Zitat[] temp = new Zitat[2];

			int n = lowestZitat();

			int r = (int) (Math.random()*2);
			temp[r] = get_lOR_Zitat(n);
			do {
				temp[1-r] = randomZitat();
			} while (temp[0].equals(temp[1]));

			erg = temp[0].getAll() + "\n  or  \n" + temp[1].getAll() + "\n" + e.getAuthor().getAsMention();

			userinfo.put(id,"rating",temp);
		} else {
			if (cmd_body.length > 0) {
				int r = Integer.parseInt(cmd_body[c]) - 1;

				if (r == 0 || r == 1) {
					Zitat[] temp = userinfo.get(id,"rating", Zitat[].class);
					for(int i = 0; i < temp.length; i++)
						System.out.println(temp);
					
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

					userinfo.put(id,"rating",null);
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
		loadScores();
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

		for (Entry<String, Integer[]> entry : scores.entrySet()) {
			Integer[] score = entry.getValue();
			map.put(entry.getKey(), score[2]);
		}

		String sorted = ZitatHandler.toString(map.entrySet(), true, false, true);

		String[] best = sorted.split(",");

		for (int i = top > 10 ? top - 10 : 0; i < top; i++) {
			erg += i + ". " + getZitat(best[i]).getAll() + " :" + (map.get(best[i])) + " aus "
					+ scores.get(best[i])[1] + "\n";
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
}
