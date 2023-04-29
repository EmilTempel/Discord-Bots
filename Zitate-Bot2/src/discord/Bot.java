package discord;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.security.auth.login.LoginException;
import javax.swing.JFrame;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class Bot {

	JDA jda;
	ArrayList<Listener> listeners;

	public Bot(String token) throws LoginException {
		jda = JDABuilder.createDefault(token).setMemberCachePolicy(MemberCachePolicy.ALL)
				.enableIntents(GatewayIntent.GUILD_MEMBERS).build();
		listeners = new ArrayList<Listener>();
	}

	public void addListener(Listener listener) {
		jda.addEventListener(listener);
		listeners.add(listener);
	}

	public void setPresence(OnlineStatus status, Activity activity) {
		jda.getPresence().setPresence(status, activity);
	}

	public static void main(String[] args) throws LoginException {
		String tokenDir = "",token = "";
		
		
		try {
			BufferedReader b = new BufferedReader(new FileReader("tokenDir.txt"));
			tokenDir = b.readLine();
			b.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			BufferedReader b = new BufferedReader(new FileReader(tokenDir));
			token = b.readLine();
			b.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ZitatMaster t_d = new ZitatMaster(token);

		JFrame frame = new JFrame("Zitate-Bot");
		frame.setSize(100, 100);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		File f = new File("test123.txt");

		try (FileReader r = new FileReader(f); BufferedReader reader = new BufferedReader(r)) {
			frame.setTitle(reader.readLine());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
