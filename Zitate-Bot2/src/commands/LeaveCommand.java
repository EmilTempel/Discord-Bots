package commands;

import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;

public class LeaveCommand implements Command{
	
	Executable exe;
	
	public LeaveCommand(Executable exe) {
		this.exe = exe;
	}
	
	public void execute(Event e, String... cmd_body) {
		exe.run((GuildVoiceLeaveEvent) e, cmd_body);
	}
	
	interface Executable{
		public abstract void run(GuildVoiceLeaveEvent e, String...cmd_body);
	}
}
