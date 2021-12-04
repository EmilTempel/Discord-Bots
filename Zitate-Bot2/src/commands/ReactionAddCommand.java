package commands;

import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public class ReactionAddCommand extends Command{
	Executable<GuildMessageReactionAddEvent> exe;
	String emote;
	public ReactionAddCommand(String emote, Executable<GuildMessageReactionAddEvent> exe) {
		this.emote = emote;
		this.exe = exe;
	}
	
	public void execute(Event e, String... cmd_body) {
		if(active) {
			GuildMessageReactionAddEvent e1 = (GuildMessageReactionAddEvent) e;
			System.out.println(e1.getReactionEmote().getName());
			if ((emote.equals("any") || e1.getReactionEmote().getName().equals(emote)) && !e1.getMember().getUser().isBot()) {
				exe.run((GuildMessageReactionAddEvent)e, cmd_body);
			}
		}
	}

	public String getName() {
		return "ReactionAdd";
	}
}
