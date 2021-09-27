package discord;

import java.util.ArrayList;
import java.util.HashMap;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

public class MessageLoader {

	Guild g;

	ArrayList<Message> messages;
	HashMap<String, Message> m;

	public MessageLoader(Guild g, String... channels) {
		this.g = g;
		messages = new ArrayList<Message>();
		m = new HashMap<String, Message>();
		for (String channel : channels) {
			load(channel);
		}
	}

	public Message getMessage(String channel, String Id) {
		return m.get(channel + "/" + Id);
	}

	public Message getMessage(String path) {
		return m.get(path);
	}

	public void load(String c) {
		TextChannel channel = g.getTextChannelsByName(c, true).size() > 0 ? g.getTextChannelsByName(c, true).get(0)
				: null;
		if (channel != null) {
			ArrayList<Message> messages = new ArrayList<Message>();
			String latest = MessageHistory.getHistoryFromBeginning(channel).limit(1).complete().getRetrievedHistory()
					.get(0).getId();
			String curr = channel.getLatestMessageId();
			System.out.println(curr);

			MessageHistory ms = channel.getHistoryAround(curr, 1).complete();

			try {
				messages.add(channel.retrieveMessageById(curr).complete());
			}catch(ErrorResponseException e) {
				
			}

			while (latest.equals(curr) == false) {
				ms = channel.getHistoryBefore(curr, 100).complete();
				messages.addAll(ms.getRetrievedHistory());
				curr = ms.getRetrievedHistory().get(ms.size() - 1).getId();
			}

			ArrayList<Message> h = new ArrayList<Message>();

			for (int i = messages.size() - 1; i >= 0; i--) {
				h.add(messages.get(i));
				m.put(c + "/" + messages.get(i).getId(), messages.get(i));
			}

			this.messages.addAll(h);
			System.out.println("successfully loaded");
		}
	}
}
