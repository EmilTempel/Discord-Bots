package commands;

import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;

public class ReactionRemoveCommand extends Command{
	Executable<GuildMessageReactionRemoveEvent> exe;
	public ReactionRemoveCommand(Executable<GuildMessageReactionRemoveEvent> exe) {
		this.exe = exe;
	}
	
	public void execute(Event e, String... cmd_body) {
		if(active) {
			exe.run((GuildMessageReactionRemoveEvent)e, cmd_body);
		}
	}

	public String getName() {
		return "ReactionRemove";
	}
}
