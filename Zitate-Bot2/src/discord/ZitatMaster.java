package discord;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.LoginException;

import commands.Command;
import commands.MessageCommand;
import handler.Handler;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;

public class ZitatMaster extends Bot {

	Map<Guild, Handler> h;
	Map<Guild, UserInformation> userinfo;
	MessageCommand init;

	public ZitatMaster(String token) throws LoginException {
		super(token);
		setPresence(OnlineStatus.ONLINE, Activity.watching("anderen bei der Arbeit zu"));
		h = new HashMap<Guild, Handler>();
		userinfo = new HashMap<Guild, UserInformation>();
		init = new MessageCommand('\\', null, new String[][] { null }, (e, s) -> createHandler(e.getGuild()));
		addListener(new Listener(null, init));
	}

	public void createHandler(Guild g) {
		if (h.get(g) == null) {
			File f = new File("Guild/" + g.getId());
			if (!f.exists())
				f.mkdir();

			UserInformation ui = new UserInformation(g,new String[]{"nostalgie-zitate", "zitate"});

			Handler handler = new Handler(g, ui);

			h.put(g, handler);
			userinfo.put(g, ui);

			Listener listener = listeners.get(listeners.size() - 1);
			listener.commands = new ArrayList<Command>();
			listener.g = g;
			listener.addHandler(handler);
			

			addListener(new Listener(null, init));
		}
	}
}
