package discord;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class ScrollMessage {

	Guild g;
	String channel, MessageId;
	String[] content;
	int page;
	Message m;

	public ScrollMessage(Guild g, String channel, String MessageId, String[] content, int page) {
		this.g = g;
		this.channel = channel;
		this.MessageId = MessageId;
		this.content = content;
		this.page = page;
	}

	public ScrollMessage(Guild g, Object[] o) {
		this(g, (String) o[0], (String) o[1], (String[]) o[2], (int) o[3]);
	}

	public boolean matches(String channel, String MessageId) {
		return (this.channel.equals(channel) && this.MessageId.equals(MessageId));
	}

	public void setMessage() {
		if (m == null)
			m = g.getTextChannelsByName(channel, false).get(0).retrieveMessageById(MessageId).complete();
	}

	public void flipTo(int page) {
		page = (page >= 0 ? page : 0) < content.length ? page : content.length;
		m.editMessage(content[page]);
		this.page = page;
	}

	public void flip(int dir) {
		dir = dir >= 0 ? 1 : -1;
		flipTo(page + dir);
	}
}
