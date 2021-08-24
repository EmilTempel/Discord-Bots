package discord;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
				new MessageCommand('\\', null, new String[][] { null }, (e, s) -> createHandler(e.getGuild()))));
	}

	public void createHandler(Guild g) {
		if (h.get(g) == null) {
			File f = new File("Guild/" + g.getId());
			if(!f.exists())
				f.mkdir();
			
			UserInformation ui = new UserInformation(g);
//			ui.put("general", "Int", 1);
//			
//			ui.put("general", "Array", new Integer[] {1,2,4,5});
//			ArrayList<Integer> list = new ArrayList<Integer>();
//			list.add(1);
//			list.add(3);
//			ui.put("general", "ArrayList", list);
//			HashMap<Integer,String> map = new HashMap<Integer,String>();
//			map.put(1, "helol");
//			map.put(2, "helal");
//			ui.put("general", "HashMap", map);
			
			System.out.println("Array: " + Arrays.deepToString(ui.get("general", "Array", Object[].class)));
			System.out.println("ArrayList: " + ui.get("general", "ArrayList", ArrayList.class));
			System.out.println("HashMap: " + ui.get("general", "HashMap", HashMap.class));
			System.out.println("Int: " +  ui.get("general", "Int", Integer.class));
			
			ui.save();
			
//			Handler handler = new Handler(g, ui);
//			
//			h.put(g, handler);
//			userinfo.put(g, ui);
//			listener.addHandler(handler);
		}
	}
}
