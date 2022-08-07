package commands;

import java.util.ArrayList;

import discord.Emoji;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public class ReactionAddCommand extends Command{
	Executable<GuildMessageReactionAddEvent> exe;
	ArrayList<Emoji> emojis;
	public ReactionAddCommand(ArrayList<Emoji> emojis, Executable<GuildMessageReactionAddEvent> exe) {
		this.emojis = emojis;
		this.exe = exe;
	}
	
	public ReactionAddCommand(Emoji emoji, Executable<GuildMessageReactionAddEvent> exe) {
		this.emojis = new ArrayList<>();
		emojis.add(emoji);
		this.exe = exe;
	}
	
	public void execute(Event e, String... cmd_body) {
		if(active) {
			GuildMessageReactionAddEvent e1 = (GuildMessageReactionAddEvent) e;
			String unicode = e1.getReactionEmote().getAsCodepoints();
			System.out.println(unicode);
			System.out.println(Emoji.fromUnicode(unicode));
			if ((emojis == null || emojis.contains(Emoji.fromUnicode(unicode))) && !e1.getMember().getUser().isBot()) {
				exe.run((GuildMessageReactionAddEvent)e, new String[] {unicode});
			}
		}
	}

	public String getName() {
		return "ReactionAdd";
	}
}
