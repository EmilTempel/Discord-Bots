package discord;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

	public void put(String UserID, String key, Object value) {
		if (users.get(UserID) == null) {
			users.put(UserID, new HashMap<String, Object>());
		}
		users.get(UserID).put(key, value);
		save();
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
		int c = 0;
		for (Entry<String, Object> entry : in.entrySet()) {
			String type = toType(entry.getValue());
			String name = entry.getKey();
			String value = toFormat(entry.getValue());
			if (type != null && value != null) {
				str += type + ":" + name + ":" + value + ",";
				c++;
			}
		}
		System.out.println(str);
		if (c > 0)
			str = str.substring(0, str.length() - 1);
		System.out.println(str);
		return str + "}";
	}

	public static String toFormat(Object o) {
		String str = "";
		if (o == null) {
			str = "null";
		} else if (o instanceof Object[]) {
			str += "[";
			Object[] arr = (Object[]) o;
			for (int i = 0; i < arr.length; i++) {
				str += toFormat(arr[i]);
				if (i != arr.length - 1)
					str += ",";
			}
			str += "]";

		} else if (o instanceof ArrayList) {
			ArrayList<?> list = (ArrayList<?>) o;
			str += "(";
			for (Object l : list) {
				str += toFormat(l) + ",";
			}
			if (list.size() > 0) {
				str = str.substring(0, str.length() - 2);
			}
			str += ")";
		} else if (o instanceof HashMap) {
			HashMap<?, ?> map = (HashMap<?, ?>) o;
			str += "<";
			for (Entry<?, ?> e : map.entrySet()) {
				str += toFormat(e.getKey()) + "=" + toFormat(e.getValue()) + ",";
			}
			if (map.size() > 0) {
				str = str.substring(0, str.length() - 2);
			}
			str += ">";
		} else {
			str += o.toString();
		}
		return str;
	}

	public static String toType(Object o) {
		String str = null;

		if (o instanceof ArrayList) {
			ArrayList<?> list = (ArrayList<?>) o;
			String sub_type = list.size() > 0 ? toType(list.get(0)) : null;
			if (sub_type != "null") {
				str += o.getClass().getSimpleName() + "<" + sub_type + ">";
			}
		} else if (o instanceof HashMap) {
			HashMap<?, ?> map = (HashMap<?, ?>) o;
			String key_type = "";
			String value_type = "";
			for (Entry<?, ?> e : map.entrySet()) {
				key_type = toType(e.getKey());
				value_type = toType(e.getValue());
				if (key_type != null && value_type != null) {
					str = o.getClass().getSimpleName() + "<" + key_type + "," + value_type + ">";
				}
			}

		} else {
			str = o.getClass().getSimpleName();
		}
		return str;
	}

	public static <E> E convertInstanceOfObject(Object o, Class<E> clazz) {
		try {
			return clazz.cast(o);
		} catch (ClassCastException e) {
			return null;
		}
	}

	public static Object fromString(String value, String clazz) {
		if (clazz.contains("[]")) {

		} else if (clazz.contains("HashMap")) {

		}

		return null;
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

	public static void main(String[] args) {
		ArrayList<Integer> o = new ArrayList<Integer>();
		o.add(1);
		o.add(3);
		o.add(2);
		ArrayList<?> list = (ArrayList<?>) o;
		System.out.println(list.get(3));
	}
}
