package discord;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Listener extends ListenerAdapter {

	char prefix;
	ZitatMaster zm;
	Command[] commands;
	boolean first;

	public Listener(char prefix, ZitatMaster zm, Command... commands) {
		this.prefix = prefix;
		this.zm = zm;
		this.commands = commands;
		this.first = true;
	}

	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String msg = e.getMessage().getContentRaw();

		if (e.getChannel().getName().equalsIgnoreCase("zitate") || first) {
			zm.loadZitate(e.getGuild());
			first = false;
		}

		if (msg.length() > 0 && msg.charAt(0) == prefix) {
			System.out.println(msg);

			msg = msg.replace(String.valueOf(prefix), "");

			for (int i = 0; i < commands.length; i++) {
				Command c = commands[i];
				for (int j = 0; j < c.cmd.length; j++) {
					String[] cmd = msg.split(" ");
					if (c.cmd[j].equalsIgnoreCase(cmd[0])) {
						c.addCommand_Body(cmd);
						commands[i].execute(e);
						break;
					}
				}
			}
		}

	}

}
