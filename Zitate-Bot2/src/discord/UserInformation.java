package discord;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import net.dv8tion.jda.api.entities.Guild;

public class UserInformation {

	Guild g;
	HashMap<String, HashMap<String, Object>> users;

	String path;

	public UserInformation(Guild g) {
		this.g = g;
		this.path = "Guild/" + g.getId() + "/userinfo";
		File f = new File(path);
		if (!f.exists())
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		load();
	}

	public void put(String UserID, String key, Object[] value) {
		if (users.get(UserID) == null) {
			users.put(UserID, new HashMap<String, Object>());
		}
		users.get(UserID).put(key, value);
		save();
	}

	public <E> E get(String UserID, String key, Class<E> clazz) {
		if (users.get(UserID) == null || users.get(UserID).get(key) == null) {
			return null;
		} else {
			return convertInstanceOfObject(users.get(UserID).get(key), clazz);
		}
	}

	public void load() {
		users = new HashMap<String, HashMap<String, Object>>();
		try {
			BufferedReader b = new BufferedReader(new FileReader(path));
			String line = null;
			while ((line = b.readLine()) != null) {
				String[] split = line.split("->");
				HashMap<String, Object> value = new HashMap<String, Object>();
				split[1] = split[1].replace("{", "").replace("}", "");
				String[] json = split[1].split(";");
				for (String j : json) {
					String[] v = j.split(":");
					value.put(v[0], v[1]);
				}
				users.put(split[0], value);
			}

			b.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void save() {
		String str = "";
		for (Entry<String, HashMap<String, Object>> entry : users.entrySet()) {
			str += entry.getKey() + "->" + toJSON(entry.getValue()) + "\n";
		}
		try {
			BufferedWriter b = new BufferedWriter(new FileWriter(path));
			b.write(str);
			b.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String toJSON(HashMap<String, Object> in) {
		String str = "{";
		for (Entry<String, Object> entry : in.entrySet()) {
			str += entry.getKey() + ":";
			if (entry.getValue() == null) {
				str += "null";
			} else if (!(entry.getValue() instanceof Object[])) {
				str += entry.getValue().toString();
			} else {
				str += "[";
				Object[] arr = (Object[]) entry.getValue();
				for(int i = 0; i < arr.length; i++) {
					str += arr[i].toString();
					if(i != arr.length-1)
						str+=",";
				}
				str += "]";
			}
			str += ",";

		}
		System.out.println(str);
		str = str.substring(0, str.length() - 1);
		System.out.println(str);
		return str + "}";
	}

	public static <E> E convertInstanceOfObject(Object o, Class<E> clazz) {
		try {
			return clazz.cast(o);
		} catch (ClassCastException e) {
			return null;
		}
	}

	public static String Format(Object[] arg) {
		String str = "";
		for (int i = 0; i < arg.length; i++) {
			str += arg[i].toString();
			if (i != arg.length - 1) {
				str += ",";
			}
		}
		return str;
	}
}
