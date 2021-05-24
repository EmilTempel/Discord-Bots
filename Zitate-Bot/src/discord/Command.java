package discord;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Command {
	
	Executable exe;
	String[] cmd;
	String[] cmd_body;
	
	public Command(Executable exe,String... cmd) {
		this.exe = exe;
		this.cmd = cmd;
	}
	
	public void execute(GuildMessageReceivedEvent e) {
		exe.execute(e, cmd_body);
	}
	
	public void addCommand_Body(String[] body) {
		cmd_body = new String[body.length-1];
		for(int i = 0; i < cmd_body.length; i++) {
			cmd_body[i] = body[i+1] != null ? body[i+1] : "";
		}
	}
	
	interface Executable{
		public abstract void execute(GuildMessageReceivedEvent e, String[] cmd_body);
	}
}
