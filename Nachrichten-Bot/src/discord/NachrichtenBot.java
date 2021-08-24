package discord;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;

public class NachrichtenBot {

	EntityBot e1, e2;
	Guild fromG, toG;
	int GuildLoadedStatus = 0;
	boolean active = false;
	TextChannel fromTc, toTc;
	ArrayList<Message> msgs;
	int count = 0;

	public NachrichtenBot(String token1, String token2) {

		try {
			e1 = new EntityBot(token1);
			e2 = new EntityBot(token2);
		} catch (LoginException e) {
			e.printStackTrace();
			return;
		}

		fromG = null;
		toG = null;

		e1.setPresence(OnlineStatus.DO_NOT_DISTURB, Activity.watching("andern bei der Arbeit zu"));
		e2.setPresence(OnlineStatus.DO_NOT_DISTURB, Activity.playing("mit deiner mom, lol"));

		e1.addListener(new Listener(this, "Zitat Meister"));
		e2.addListener(new Listener(this, "Mitat Zweister"));
	}

	public static void main(String[] args) {
		String token1 = "", token2 = "";

		try {
			BufferedReader b = new BufferedReader(new FileReader("C:/Users/Public/Documents/Zitate-Bot/token.txt"));
			token2 = b.readLine();
			b.close();

			b = new BufferedReader(new FileReader("C:/Users/Public/Documents/Zitate-Bot/token1.txt"));
			token1 = b.readLine();
			b.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		NachrichtenBot nb = new NachrichtenBot(token1, token2);
	}

	public void addGuild(Guild g) {
		if (GuildLoadedStatus == 0) {
			fromG = g;
			GuildLoadedStatus++;
			System.out.println(GuildLoadedStatus + g.getName());
		} else if (GuildLoadedStatus == 1) {
			GuildLoadedStatus++;
		} else if (GuildLoadedStatus == 2) {
			toG = g;
			GuildLoadedStatus++;
			System.out.println(GuildLoadedStatus + g.getName());
			if (!active) {
				active = true;
				System.out.println(active);
				transferMessages();
			}
		}
	}

	public ArrayList<Message> loadZitate(TextChannel fromTc) {
//		zitate = new ArrayList<Message>();

		ArrayList<Message> messages = new ArrayList<Message>();

		TextChannel channel = fromTc;

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

//		h.forEach(m -> {
//			Zitat z = new Zitat(m);
//			if (z.isFull()) {
//				zitate.add(z);
//				scores.put(z.getID(), new Integer[] { 0, 0, 0 });
//			}
//		});
//		saveScores();
//		loadScores();

		System.out.println("successfully loaded");
		return messages;
	}

	public void transferMessages() {
		fromTc = e1.getChannelByName(fromG, "zitate");
		toTc = e1.getChannelByName(toG, "nostalgie-zitate");

		if (toTc != null) {
			e1.deleteChannel(toG, "nostalgie-zitate");
			System.out.println("deleted");
		}

		e1.createNostalgieZitateChannel(toG);
		System.out.println("created");

		msgs = loadZitate(fromTc);
		System.out.println(msgs.size());

//		for (int i = 0; i < msgs.size() / 2; i++) {
//			Message m1 = msgs.get(2 * i);
//			Message m2 = msgs.get(2 * i + 1);
//			String s1 = m1.getContentRaw() + " <" + m1.getAuthor().getName() + ">";
//			String s2 = m2.getContentRaw() + " <" + m2.getAuthor().getName() + ">";
//			System.out.println(s1);
//			e1.sendMessage(s1, toTc);
//			System.out.println(s2);
//			e2.sendMessage(s2, toTc);
//		}
	}

}
