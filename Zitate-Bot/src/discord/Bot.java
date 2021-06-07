package discord;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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
		
		BufferedReader f;
		try {
			f = new BufferedReader(new FileReader("Token"));
			Bot t_d = new ZitatMaster(f.readLine());
			f.close();
		} catch (LoginException | IOException e) {
			e.printStackTrace();
		}

		
	}
}
