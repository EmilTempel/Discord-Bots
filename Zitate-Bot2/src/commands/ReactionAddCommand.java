package commands;

import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public class ReactionAddCommand extends Command{
	Executable exe;
	public ReactionAddCommand(Executable exe) {
		this.exe = exe;
	}
	
	public void execute(Event e, String... cmd_body) {
		if(active) {
			exe.execute((GuildMessageReactionAddEvent)e, cmd_body);
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
