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
		setPresence(OnlineStatus.ONLINE, Activity.playing("eh keine Rolle"));
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

			UserInformation ui = new UserInformation(g);
			
			System.out.println(ui.get("guild", "zitate", ArrayList.class).size());
//			ui.put("general", "Int", 1);
//
//			ui.put("general", "Array", new Integer[][] { new Integer[] { 1, 2 }, new Integer[] { 4, 5 } });
//			ArrayList<ArrayList<Integer>> list = new ArrayList<ArrayList<Integer>>();
//			for (int i = 0; i < 10; i++) {
//				ArrayList<Integer> l = new ArrayList<Integer>();
//				l.add(1+1);
//				l.add(69+1);
//				list.add(l);
//			}
//			ui.put("general", "ArrayList", list);
//			HashMap<Integer, String> map = new HashMap<Integer, String>();
//			map.put(1, "helol");
//			map.put(2, "helal");
//			ui.put("general", "HashMap", map);
//
//			System.out.println("Array: " + Arrays.deepToString(ui.get("general", "Array", Object[].class)));
//			System.out.println(ui.get("general", "Array", Object[].class).getClass());
//			System.out.println("ArrayList: " + ui.get("general", "ArrayList", ArrayList.class));
//			System.out.println(ui.get("general", "ArrayList", ArrayList.class));
//			System.out.println("HashMap: " + ui.get("general", "HashMap", HashMap.class));
//			System.out.println(ui.get("general", "HashMap", HashMap.class).getClass());
//			System.out.println("Int: " + ui.get("general", "Int", Integer.class));
//			
//
//			ui.save();

//			Handler handler = new Handler(g, ui);
//
//			h.put(g, handler);
//			userinfo.put(g, ui);
//
//			Listener listener = listeners.get(listeners.size() - 1);
//			listener.commands = new ArrayList<Command>();
//			listener.g = g;
//			listener.addHandler(handler);
//			
//
//			addListener(new Listener(null, init));
		}
	}
}
