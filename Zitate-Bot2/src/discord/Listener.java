package discord;

import java.util.ArrayList;
import java.util.Arrays;

import commands.Command;
import commands.JoinCommand;
import commands.LeaveCommand;
import commands.MessageCommand;
import handler.Handler;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Listener extends ListenerAdapter {

	ArrayList<Command> commands;

	public Listener(Command... commands) {
		this.commands = new ArrayList<Command>();
		addCommands(commands);
	}

	public void addCommands(Command... commands) {
		for (int i = 0; i < commands.length; i++) {
			this.commands.add(commands[i]);
		}
	}

	public void addHandler(Handler... handler) {
		for (int i = 0; i < handler.length; i++) {
			addCommands(handler[i].getCommands());
		}
	}

	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String msg = e.getMessage().getContentRaw();

		if (msg.length() > 0 && !e.getAuthor().isBot()) {
			System.out.println(msg);
			String[] cmd = msg.split(" ");
			for (int i = 0; i < commands.size(); i++) {
				if (commands.get(i) instanceof MessageCommand) {
					MessageCommand c = (MessageCommand) commands.get(i);
					if (c.matches(cmd)) {
						c.execute(e, MessageCommand.Command_Body(cmd));
						System.out.println(Arrays.toString(cmd));
					}
				}
			}
		}
	}

	public void onGuildVoiceJoin(GuildVoiceJoinEvent e) {
		for (Command command : commands) {
			if (command instanceof JoinCommand) {
				JoinCommand c = (JoinCommand) command;
				c.execute(e);
			}
		}
	}

	public void onGuildVoiceLeave(GuildVoiceLeaveEvent e) {
		for (Command command : commands) {
			if (command instanceof LeaveCommand) {
				LeaveCommand c = (LeaveCommand) command;
				c.execute(e);
			}
		}
	}

}
