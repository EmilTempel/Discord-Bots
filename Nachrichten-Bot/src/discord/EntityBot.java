package discord;

import java.util.ArrayList;
import java.util.Collection;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class EntityBot {

	JDA jda;

	public EntityBot(String token) throws LoginException {
		jda = JDABuilder.createDefault(token).build();
	}

	public void sendMessage(String message, TextChannel tc) {
		tc.sendMessage(message).queue();
	}

	public void setPresence(OnlineStatus status, Activity activity) {
		jda.getPresence().setPresence(status, activity);
	}

	public void addListener(Listener l) {
		jda.addEventListener(l);
	}

	public TextChannel getChannelByName(Guild g, String name) {
		TextChannel tc = null;
		try {
			tc = g.getTextChannelsByName(name, true).get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tc;
	}

	public void deleteChannel(Guild g, String name) {
		TextChannel tc = getChannelByName(g, name);
		tc.delete().complete();
	}

	public Role getRoleByName(Guild g, String name) {
		Role r = null;
		try {
			r = g.getRolesByName(name, true).get(0);
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		return r;
	}

	public void createNostalgieZitateChannel(Guild g) {
		Role r = getRoleByName(g, "Zitatmaster");
		Collection<Permission> allow = new ArrayList<Permission>();
		allow.add(Permission.MESSAGE_READ);
		Collection<Permission> deny = new ArrayList<Permission>();
		g.createTextChannel("nostalgie-zitate").addRolePermissionOverride(r.getIdLong(), allow, deny).complete();
	}

}
