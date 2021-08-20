package discord;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.LoginException;

import commands.MessageCommand;
import handler.Handler;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;

public class ZitatMaster extends Bot {

	Map<Guild, Handler> h;
	Map<Guild, UserInformation> userinfo;

	public ZitatMaster(String token) throws LoginException {
		super(token);
		setPresence(OnlineStatus.ONLINE, Activity.playing("eh keine Rolle"));
		h = new HashMap<Guild, Handler>();
		userinfo = new HashMap<Guild, UserInformation>();
		addListener(new Listener(
				new MessageCommand('\\', null, new String[][] { null }, (e, s) -> createHandler(e.getGuild()), null)));
	}

	public void createHandler(Guild g) {
		if (h.get(g) == null) {
			File f = new File("Guild/" + g.getId());
			if(!f.exists())
				f.mkdir();
			
			UserInformation ui = new UserInformation(g);
			
			
			Handler handler = new Handler(g, ui);
			
			h.put(g, handler);
			userinfo.put(g, ui);
			listener.addHandler(handler);
		}
	}
}
