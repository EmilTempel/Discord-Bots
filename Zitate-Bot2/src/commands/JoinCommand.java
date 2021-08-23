package commands;

import discord.Configuration;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;

public class JoinCommand extends Command {
	Executable exe;
	Configuration c;

	public JoinCommand(Executable exe, Configuration c) {
		this.exe = exe;
	}

	public void execute(Event e, String... cmd_body) {
		if (active) {
			exe.run((GuildVoiceJoinEvent) e, cmd_body);
		}
	}

	public interface Executable {
		public abstract void run(GuildVoiceJoinEvent e, String... cmd_body);
	}

	public String getName() {
		return "onJoin";
	}
}
