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

		ZitatMaster t_d = new ZitatMaster("ODUzMzg1MTc4MDY3NTAxMDY2.YMUm4Q.p26jJlDJ-EEJV08vd942v1wf0FY");

	}
}
