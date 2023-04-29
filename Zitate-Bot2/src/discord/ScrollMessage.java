package discord;

import java.util.List;
import java.util.function.Supplier;

import discord.ActionMessage.Action;
import handler.Handler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

public class ScrollMessage {

	Handler handler;
	Guild g;
	String channel, MessageId, title, description;
	String[][][] content_raw;
	MessageEmbed[] content;
	int page, item;
	Emoji left, right, up, down, accept;
	Action acceptAction;
	Message m;

	public ScrollMessage(Handler handler, Guild g, String channel, String MessageId, String title, String description,
			String[][][] content_raw, int page, Action acceptAction ,Emoji left, Emoji right, Emoji up, Emoji down, Emoji accept) {
		this.handler = handler;
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
		
		this.acceptAction = acceptAction;
		
	}

	public ScrollMessage(Handler handler, Guild g, String channel, String MessageId, String title, String description,
			String[][][] content_raw, int page) {
		this(handler, g, channel, MessageId, title, description, content_raw, page, (e,m) -> {}, Emoji.arrow_left, Emoji.arrow_right,
				Emoji.arrow_up, Emoji.arrow_down, Emoji.white_check_mark);
	}

	public ScrollMessage(Handler handler, Guild g, Message m, String title, String description, String[][][] content_raw, int page) {
		this(handler, g, m.getChannel().getName(), m.getId(),title, description, content_raw, page);
		setMessage(m, null);
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

	public void setMessage(Message m, Supplier<String[][][]> refresh) {
		this.m = m;
		this.channel = m.getChannel().getName();
		this.MessageId = m.getId();
		addActionMessage(m, refresh);
	}
	
	public void addActionMessage(Message message, Supplier<String[][][]> refresh) {
		ActionMessage am = new ActionMessage(message, true);
		am.addAction(left, (e,m) -> flip(-1));
		am.addAction(right, (e,m) -> flip(1));
		am.addAction(down, (e,m) -> flyp(1));
		am.addAction(up, (e,m) -> flyp(-1));
		if(refresh != null) {
			am.addAction(Emoji.arrows_counterclockwise, (e,m) -> {
				content = from3DStringArray(refresh.get());
				flipTo(page);
			});
		}
		
		handler.addActionMessage(am);
	}

	public Message getMessage() {
		return m;
	}
	
	public MessageEmbed getContent(int i) {
		return content[i];
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
				down);
	}
}
