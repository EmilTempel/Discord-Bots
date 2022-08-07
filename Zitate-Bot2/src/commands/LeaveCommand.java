package commands;

import discord.Configuration;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;

public class LeaveCommand extends Command {

	Executable<GuildVoiceLeaveEvent> exe;
	Configuration c;

	public LeaveCommand(Executable<GuildVoiceLeaveEvent> exe, Configuration c) {
		this.exe = exe;
	}

	public void execute(Event e, String... cmd_body) {
		if (active) {
			exe.run((GuildVoiceLeaveEvent) e, cmd_body);
		}
	}

	public String getName() {
		return "onLeave";
	}
}
