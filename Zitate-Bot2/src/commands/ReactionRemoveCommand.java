package commands;

import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;

public class ReactionRemoveCommand extends Command{
	Executable exe;
	public ReactionRemoveCommand(Executable exe) {
		this.exe = exe;
	}
	
	public void execute(Event e, String... cmd_body) {
		if(active) {
			exe.execute((GuildMessageReactionRemoveEvent)e, cmd_body);
		}
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public interface Executable{
		public abstract void execute(GuildMessageReactionRemoveEvent event, String... cmd_body);
	}
}
