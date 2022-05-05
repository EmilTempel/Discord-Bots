package discord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import handler.Handler;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;

public class ActionMessage {

	Message m;
	boolean autoDelete;
	User user;

	HashMap<Emoji, Action> actions;
	ArrayList<Emoji> emojis;

	public ActionMessage(Message m, boolean autoDelete) {
		this.m = m;
		this.autoDelete = autoDelete;
		this.actions = new HashMap<>();
		this.emojis = new ArrayList<>();
	}

	public ActionMessage(Message m, boolean autoDelete, User user) {
		this(m, autoDelete);
		this.user = user;
	}

	public void addAction(Emoji emoji, Action action) {
		actions.put(emoji, action);
		emojis.add(emoji);
	}

	public void resolveReaction(User user, Emoji emoji) {
		Action a = actions.get(emoji);
		if (a != null && (this.user == null || this.user.equals(user))) {
			a.execute(emoji, m);
		}
		
		if (autoDelete) {
			Handler.removeReaction(m, emoji, user);
		}
	}

	public void addReactions() {
		if (m != null) {
			List<MessageReaction> reactions = m.getReactions();
			List<Emoji> r_emojis = new ArrayList<Emoji>();
			for (MessageReaction r : reactions) {
				r_emojis.add(Emoji.fromUnicode(r.getReactionEmote().getAsCodepoints()));
			}

			for (int i = 0; i < emojis.size(); i++) {
				if (!r_emojis.contains(emojis.get(i))) {
					System.out.println(emojis.get(i));
					Handler.addReaction(m, emojis.get(i));
				}
			}
		}
	}

	public Message getMessage() {
		return m;
	}

	public ArrayList<Emoji> getEmojis() {
		return emojis;
	}

	public interface Action {
		public abstract void execute(Emoji e, Message m);
	}
}
