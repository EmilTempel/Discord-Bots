package discord;

import java.util.ArrayList;
import java.util.List;

import handler.Handler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;

public class ScrollMessage {

	Guild g;
	String channel, MessageId, title, description;
	String[][][] content_raw;
	MessageEmbed[] content;
	int page, item;
	Emoji left, right, up, down, accept;
	Message m;

	public ScrollMessage(Guild g, String channel, String MessageId, String title, String description,
			String[][][] content_raw, int page, Emoji left, Emoji right, Emoji up, Emoji down, Emoji accept) {
		this.g = g;
		this.channel = channel;
		this.MessageId = MessageId;
		this.content_raw = content_raw;
		this.title = title;
		this.description = description;
		this.content = from3DStringArray(content_raw);
		this.page = page;
		this.left = left;
		this.right = right;
		this.up = up;
		this.down = down;
		this.accept = accept;

	}

	public ScrollMessage(Guild g, String channel, String MessageId, String title, String description,
			String[][][] content_raw, int page) {
		this(g, channel, MessageId, title, description, content_raw, page, Emoji.arrow_left, Emoji.arrow_right,
				Emoji.arrow_up, Emoji.arrow_down, Emoji.white_check_mark);
	}

	public ScrollMessage(Guild g, Object[] o) {
		this(g, (String) o[0], (String) o[1], (String) o[2], (String) o[3], (String[][][]) o[4], (int) o[5],
				(Emoji) o[6], (Emoji) o[7], (Emoji) o[8], (Emoji) o[9], (Emoji) o[10]);
		setMessage();
	}

	public ScrollMessage(Guild g, Message m, String title, String description, String[][][] content_raw, int page) {
		this(g, m.getChannel().getName(), m.getId(),title, description, content_raw, page);
		setMessage(m);
	}

	public MessageEmbed[] from3DStringArray(String[][][] content) {
		MessageEmbed[] erg = new MessageEmbed[content.length];
		for (int i = 0; i < content.length; i++) {
			erg[i] = from2DStringArray(content[i], 0);
		}
		return erg;
	}

	public MessageEmbed from2DStringArray(String[][] c, int item) {
		String[][] content = new String[c.length][];
		for (int i = 0; i < c.length; i++) {
			content[i] = c[i].clone();
			if (i == item) {
				content[i][0] = "__" + content[i][0] + "__";

			}
		}
		EmbedBuilder eb = new EmbedBuilder();
		for (int i = 0; i < content.length; i++) {
			if (content[i] != null && content[i].length == 3)
				eb.addField(content[i][0], content[i][1], Boolean.parseBoolean(content[i][2]));
		}
		eb.setTitle(title);
		eb.setDescription(description);
		return eb.build();
	}

	public static String[][] to2DStringArray(MessageEmbed me) {
		List<Field> fields = me.getFields();
		String[][] content = new String[fields.size()][];
		for (int i = 0; i < content.length; i++) {
			Field f = fields.get(i);
			content[i] = new String[] { f.getName(), f.getValue(), String.valueOf(f.isInline()) };
		}
		return content;
	}

	public boolean matches(String channel, String MessageId) {
		return (this.channel.equals(channel) && this.MessageId.equals(MessageId));
	}

	public void setMessage(Message m) {
		this.m = m;
		this.channel = m.getChannel().getName();
		this.MessageId = m.getId();
		addReactions();
	}

	public void setMessage() {
		setMessage(g.getTextChannelsByName(channel, false).get(0).retrieveMessageById(MessageId).complete());

	}

	public Message getMessage() {
		return m;
	}
	
	public MessageEmbed getContent(int i) {
		return content[i];
	}

	public void addReactions() {
		if (m != null) {
			List<MessageReaction> reactions = m.getReactions();
			List<Emoji> r_emojis = new ArrayList<Emoji>();
			for (MessageReaction r : reactions) {
				r_emojis.add(Emoji.fromUnicode(r.getReactionEmote().getAsCodepoints()));
			}

			Emoji[] emojis = { left, right, up, down, accept };
			for (int i = 0; i < emojis.length; i++) {
				if (!r_emojis.contains(emojis[i])) {
					Handler.addReaction(m, emojis[i]);
				}
			}
		}
	}

	public void fliyp(Emoji emoji) {
		if (emoji.equals(left)) {
			flip(-1);
		}
		if (emoji.equals(right)) {
			flip(1);
		}
		if (emoji.equals(up)) {
			flyp(-1);
		}
		if (emoji.equals(down)) {
			flyp(1);
		}
		if (emoji.equals(accept)) {

		}
	}

	public void flipTo(int page) {
		page = (page = page < 0 ? content.length - 1 : page) < content.length ? page : 0;
		m.editMessage(content[page]).queue();
		this.page = page;
		item = 0;
	}

	public void flip(int dir) {
		dir = dir >= 0 ? 1 : -1;
		flipTo(page + dir);
	}

	public void flypTo(int item) {
		item = (item = item >= 0 ? item : content_raw[page].length - 1) < content_raw[page].length ? item : 0;
		Handler.editMessage(m,from2DStringArray(content_raw[page], item));
		this.item = item;
	}

	public void flyp(int dir) {
		dir = dir >= 0 ? 1 : -1;
		flypTo(item + dir);
	}

	public String toString() {
		return UserInformation.ArrayToString(channel, MessageId, title, description, content_raw, page, left, right, up,
				down, accept);
	}
}
