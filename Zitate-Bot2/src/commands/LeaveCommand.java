package commands;

import discord.Configuration;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;

public class LeaveCommand implements Command {

	Executable exe;
	Configuration c;

	public LeaveCommand(Executable exe, Configuration c) {
		this.exe = exe;
		this.c = c;
	}

	public void execute(Event e, String... cmd_body) {
		if (c == null || c.getActive(this)) {
			exe.run((GuildVoiceLeaveEvent) e, cmd_body);
		}
	}

	interface Executable {
		public abstract void run(GuildVoiceLeaveEvent e, String... cmd_body);
	}

	public String getName() {
		return "onLeave";
	}
}
