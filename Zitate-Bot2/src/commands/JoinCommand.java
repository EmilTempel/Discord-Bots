package commands;

import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;

public class JoinCommand implements Command {
	Executable exe;

	public JoinCommand(Executable exe) {
		this.exe = exe;
	}

	public void execute(Event e, String... cmd_body) {
		exe.run((GuildVoiceJoinEvent) e, cmd_body);
	}

	public interface Executable {
		public abstract void run(GuildVoiceJoinEvent e, String... cmd_body);
	}
}
