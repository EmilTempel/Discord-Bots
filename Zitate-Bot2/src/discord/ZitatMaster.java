package discord;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.LoginException;

import commands.MessageCommand;
import handler.AudioHandler;
import handler.GameHandler;
import handler.Handler;
import handler.ZitatHandler;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;

public class ZitatMaster extends Bot {

	Map<Guild, Handler[]> handler;
	Map<Guild, UserInformation> userinfo;

	public ZitatMaster(String token) throws LoginException {
		super(token);
		setPresence(OnlineStatus.ONLINE, Activity.playing("eh keine Rolle"));
		handler = new HashMap<Guild, Handler[]>();
		userinfo = new HashMap<Guild, UserInformation>();
		addListener(new Listener(
				new MessageCommand('\\', null, new String[][] { null }, (e, s) -> createHandler(e.getGuild()))));
	}

	public void createHandler(Guild g) {
		if (handler.get(g) == null) {
			File f = new File("Guild/" + g.getId());
			if(!f.exists())
				f.mkdir();
			
			UserInformation ui = new UserInformation(g);
			
			ZitatHandler zh = new ZitatHandler(g,ui);
			Handler[] handlers = { zh, new GameHandler(zh,ui), new AudioHandler(zh,ui)};
			
			handler.put(g, handlers);
			userinfo.put(g, ui);
			listener.addHandler(handlers);
		}
	}
}
