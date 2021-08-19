package handler;

import commands.Command;
import discord.UserInformation;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

public abstract class Handler {
	
	Guild g;
	UserInformation userinfo;
	Command[] commands;
	
	public Handler(Guild g, UserInformation userinfo) {
		this.g = g;
		this.userinfo = userinfo;
		commands = new Command[0];
	}
	
	public Command[] getCommands() {
		return commands;
	}
	
	public void sendMessage(String msg, TextChannel channel) {
		channel.sendMessage(msg).queue();
	}
}
