package discord;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.EventListener;

public class Bot {

	JDA jda;

	public Bot(String token) throws LoginException {
		jda = JDABuilder.createDefault(token).build();

	}

	public void addListener(EventListener listener) {
		jda.addEventListener(listener);

	}

	public void setPresence(OnlineStatus status, Activity activity) {
		jda.getPresence().setPresence(status, activity);
	}

	public static void main(String[] args) throws LoginException {
		
		Bot t_d = new ZitatMaster("NzI1Mzg1MDQ2MjQ4MjU5NzQ1.XvN9eA.8xQgcIV9Di10d3nWs7gaofSLzH0");

	}
}
