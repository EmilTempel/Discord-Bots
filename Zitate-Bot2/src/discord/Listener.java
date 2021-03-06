package discord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import commands.Command;
import commands.JoinCommand;
import commands.LeaveCommand;
import commands.MessageCommand;
import commands.ReactionAddCommand;
import commands.ReactionRemoveCommand;
import commands.TimedCommand;
import handler.Handler;
import math.Functions;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Listener extends ListenerAdapter {

	Guild g;
	ArrayList<Command> commands;
	HashMap<Class<? extends Command>, ArrayList<Command>> map;

	Timer timer;

	public Listener(Guild g, Command... commands) {
		this.g = g;
		this.commands = new ArrayList<Command>();
		this.map = new HashMap<Class<? extends Command>, ArrayList<Command>>();
		addCommands(commands);
		
	}

	public void addCommands(Command... commands) {
		for (int i = 0; i < commands.length; i++) {
			this.commands.add(commands[i]);
		}

		for (int i = 0; i < commands.length; i++) {
			Class<? extends Command> c = commands[i].getClass();
			if (map.get(c) == null) {
				map.put(c, new ArrayList<Command>());
			}

			map.get(c).add(commands[i]);
		}
		
		startTimedCommands();
	}

	public ArrayList<Command> getCommands(Class<? extends Command> c) {
		if (map.get(c) == null) {
			map.put(c, new ArrayList<Command>());
		}
		return map.get(c);
	}

	public <E extends Command> ArrayList<E> getCastCommands(Class<E> c) {
		ArrayList<E> list = new ArrayList<E>();
		for (Command command : getCommands(c)) {
			list.add(c.cast(command));
		}
		return list;
	}

	public void addHandler(Handler... handler) {
		for (int i = 0; i < handler.length; i++) {
			addCommands(handler[i].getCommands());
		}
	}

	public boolean isRightGuild(Guild g) {
		if (this.g == null) {
			return true;
		}
		return this.g.getId().equals(g.getId());
	}

	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		if (isRightGuild(e.getGuild())) {
			String msg = e.getMessage().getContentRaw();

			if (msg.length() > 0 && !e.getAuthor().isBot()) {
				String[] cmd = msg.split(" ");
				for (MessageCommand c : getCastCommands(MessageCommand.class)) {
					if (c.matches(cmd)) {
						c.execute(e, MessageCommand.Command_Body(cmd));
					}
				}
			}
		}
	}

	public void onGuildVoiceJoin(GuildVoiceJoinEvent e) {
		if (isRightGuild(e.getGuild())) {
			for (Command command : getCommands(JoinCommand.class)) {
				command.execute(e);
			}
		}
	}

	public void onGuildVoiceLeave(GuildVoiceLeaveEvent e) {
		if (isRightGuild(e.getGuild())) {
			for (Command command : getCommands(LeaveCommand.class)) {
				command.execute(e);
			}
		}
	}

	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
		if (isRightGuild(e.getGuild())) {
			for (Command command : getCommands(ReactionAddCommand.class)) {
				command.execute(e, e.getReactionEmote().getName());
			}
		}
	}

	public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent e) {
		if (isRightGuild(e.getGuild())) {
			System.out.println("reaction");
			for (Command command : getCommands(ReactionRemoveCommand.class)) {
				command.execute(e, e.getReactionEmote().getName());
			}
		}
	}

	public void startTimedCommands() {
		ArrayList<TimedCommand> tcommands = getCastCommands(TimedCommand.class);
		if (tcommands.size() > 0) {
			System.out.println("start");
			long[] periods = new long[tcommands.size()];
			for (int i = 0; i < tcommands.size(); i++) {
				periods[i] = tcommands.get(i).getPeriod();
			}
			long period = Functions.ggT(periods);
			for (TimedCommand c : tcommands) {
				c.setMaxCounter(c.getPeriod() / period);
			}
			timer = new Timer();
			TimerTask task = new TimerTask() {

				public void run() {
					for (TimedCommand c : tcommands) {
						if (c.getCounter() == 0) {
							c.execute(null, new String[0]);
						}
						c.updateCounter();
					}
				}

			};

			timer.scheduleAtFixedRate(task, 0, period);
		}
	}
}
