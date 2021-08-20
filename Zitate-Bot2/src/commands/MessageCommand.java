package commands;

import discord.Configuration;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class MessageCommand implements Command {

	char prefix;
	String[] KeyWords;
	String[][] body_regex;
	Executable exe;
	Configuration c;

	public MessageCommand(char prefix, String[] KeyWords, String[][] body_regex, Executable exe, Configuration c) {
		this.prefix = prefix;
		this.KeyWords = KeyWords;
		this.body_regex = body_regex;
		this.exe = exe;
		this.c = c;
	}

	public void execute(Event e, String... cmd_body) {
		if (c == null || c.getActive(this)) {
			exe.run((GuildMessageReceivedEvent) e, cmd_body);
			System.out.println("run");
		} else {
			System.out.println("inactive");
		}
	}

	boolean KeyWords_contains(String cmd) {
		if (KeyWords == null)
			return true;

		for (String keyword : KeyWords) {
			if (keyword.equals(cmd.replace(String.valueOf(prefix), "")))
				return true;
		}
		return false;
	}

	boolean regex_matches(String[] body) {
		for (String[] regex : body_regex) {
			if (regex == null)
				return true;

			if (regex.length == body.length) {
				boolean matches = true;
				for (int i = 0; i < body.length; i++) {
					if (!body[i].matches(regex[i])) {
						matches = false;
						break;
					}
				}
				if (matches)
					return true;
			} else {
				continue;
			}
		}
		return false;
	}

	public boolean matches(String[] cmd) {
		if ((cmd[0].charAt(0) == prefix || prefix == '\\') && KeyWords_contains(cmd[0])) {
			String[] body = Command_Body(cmd);

			return regex_matches(body);
		} else {
			return false;
		}

	}

	public static String[] Command_Body(String[] cmd) {
		String[] body = new String[cmd.length - 1];
		for (int i = 0; i < body.length; i++) {
			body[i] = cmd[i + 1] != null ? cmd[i + 1] : "";
		}
		return body;
	}

	public interface Executable {
		public abstract void run(GuildMessageReceivedEvent e, String... cmd_body);
	}

	public String getName() {
		if (KeyWords == null || KeyWords.length == 0) {
			return "";
		} else {
			return KeyWords[0];
		}
	}
}
