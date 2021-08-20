package discord;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

public class Bot {

	JDA jda;
	Listener listener;

	public Bot(String token) throws LoginException {
		jda = JDABuilder.createDefault(token).build();
	}

	public void addListener(Listener listener) {
		jda.addEventListener(listener);
		this.listener = listener;
	}

	public void setPresence(OnlineStatus status, Activity activity) {
		jda.getPresence().setPresence(status, activity);
	}

	public static void main(String[] args) throws LoginException {
		
		String token = "";
		
		try {
			BufferedReader b = new BufferedReader(new FileReader("C:/Users/Public/Documents/Zitate-Bot/token.txt"));
			token = b.readLine();
			b.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ZitatMaster t_d = new ZitatMaster(token);
		
	}
}
