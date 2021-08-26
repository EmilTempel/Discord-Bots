package commands;

import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public class ReactionAddCommand extends Command{
	Executable exe;
	String emote;
	public ReactionAddCommand(String emote, Executable exe) {
		this.emote = emote;
		this.exe = exe;
	}
	
	public void execute(Event e, String... cmd_body) {
		if(active) {
			GuildMessageReactionAddEvent e1 = (GuildMessageReactionAddEvent) e;
			System.out.println(e1.getReactionEmote().getName());
			if (emote.equals("any") || e1.getReactionEmote().getName().equals(emote)) {
				exe.execute((GuildMessageReactionAddEvent)e, cmd_body);
			}
		}
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public interface Executable{
		public abstract void execute(GuildMessageReactionAddEvent event, String... cmd_body);
	}
}
