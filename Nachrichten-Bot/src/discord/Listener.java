package discord;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Listener extends ListenerAdapter {

	NachrichtenBot nb;
	String name;
	
	public Listener(NachrichtenBot nb, String name) {
		this.nb = nb;
		this.name = name;
	}

	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		
		if (e.getMessage().getContentRaw().equals("loadguild")) {
			System.out.println(e.getMessage().getContentRaw() + "-" + e.getGuild().getName());
			nb.addGuild(e.getGuild());
		}
		
		if (nb.active && !e.getAuthor().getName().equals(name)) {
			e.getChannel().sendMessage(nb.msgs.get(nb.count).getContentRaw() + " <" + nb.msgs.get(nb.count).getAuthor().getName() + ">").queue();
			nb.count++;
		}
	}
}
