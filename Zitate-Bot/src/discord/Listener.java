package discord;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
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
	
	public void  onGuildVoiceJoin(GuildVoiceJoinEvent e) {
		List<Role> roles = new ArrayList<Role>();
		roles.add(e.getGuild().getRolesByName("middl Potato", true).get(0));
		roles.add(e.getGuild().getRolesByName("Big Potato", true).get(0));
		roles.add(e.getGuild().getRolesByName("Big Jucy Potato", true).get(0));
		
		boolean cool = false;
		for(Role r : roles) {
			if(e.getMember().getRoles().contains(r)) {
				cool = true; 
			}
		}
		
		if(!zm.access(e.getMember().getUser().getName()) && cool) {
			e.getGuild().kickVoiceMember(e.getMember()).queue();
			PrivateChannel c = e.getMember().getUser().openPrivateChannel().complete();
			
			
			c.sendMessage("Rate mal ein Zitat du schleimblättriger Vorhautzieher! (mit <r)").queue();
			c.sendFile(new File("trade_offer.png")).queue();
			c.sendMessage("https://www.youtube.com/watch?v=dQw4w9WgXcQ").queue();
		}
	}
	
	public void  onGuildVoiceLeave(GuildVoiceLeaveEvent e) {
		zm.access.put(e.getMember().getUser().getName(),false);
	}
	

}
