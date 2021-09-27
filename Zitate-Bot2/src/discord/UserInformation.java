package discord;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import net.dv8tion.jda.api.entities.Guild;
import potatocoin.Inventory;
import potatocoin.Shop;
import potatocoin.TradeOffer;

public class UserInformation {

	Guild g;
	ZitatLoader z_loader;
	HashMap<String, HashMap<String, Object>> users;

	String path;

	Converter[] converters;

	public UserInformation(Guild g) {
		this.g = g;
		this.z_loader = new ZitatLoader(g, "nostalgie-zitate", "zitate");
		this.path = "Guild/" + g.getId() + "/userinfo";
		File f = new File(path);
		if (!f.exists())
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		converters = new Converter[] { 
				new Converter("Boolean", Boolean.class, (v, c) -> Boolean.parseBoolean(v)),
				new Converter("Integer", Integer.class, (v, c) -> Integer.parseInt(v)),
				new Converter("Double", Double.class, (v, c) -> Double.parseDouble(v)),
				new Converter("String", String.class, (v, c) -> v),
				new Converter("Emoji", Emoji.class, (v,c) -> Emoji.valueOf(v)),
				new Converter("Zitat", Zitat.class, (v, c) -> z_loader.getZitat(StringToArray(v))),
				new Converter("Inventory", Inventory.class, (v, c) -> new Inventory(StringToArray(v))),
				new Converter("TradeOffer", TradeOffer.class, (v, c) -> new TradeOffer(g, this, StringToArray(v))),
				new Converter("ScrollMessage", ScrollMessage.class, (v,c) -> new ScrollMessage(g, StringToArray(v))),
				new Converter("Shop", Shop.class, (v, c) -> new Shop(g, this, StringToArray(v))),
				new Converter("\\w+(\\[\\])+", Object[].class, (v, c) -> {
					String[] split = split(v);
					String element_type = c.substring(0, c.length() - 2);
					return createArray(getType(element_type), split, element_type);
				}), new Converter("ArrayList<.+>", ArrayList.class, (v, c) -> {
					String[] split = split(v);
					String element_type = c.substring(10, c.length() - 1);
					return createArrayList(getType(element_type), split, element_type);
				}), new Converter("HashMap<.+,.+>", HashMap.class, (v, c) -> {
					String[] split = split(v);
					String[] types = split(c.substring(8, c.length() - 1));
					for (int i = 0; i < 2; i++) {
						types[i] = types[i].substring(1, types[i].length() - 1);
					}

					return createHashMap(getType(types[0]), getType(types[1]), split, types[0], types[1]);
				}) };

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
				for (String element : json) {
					if (!element.equals("")) {
						String[] e = element.split(":");
						Object o = fromString(e[1], e[2]);
						if (o != null) {
							value.put(e[0], o);
						}
					}
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

	public String toJSON(HashMap<String, Object> in) {
		String str = "{";
		int c = 0;
		for (Entry<String, Object> entry : in.entrySet()) {
			if (entry.getValue() != null) {
				String type = toType(entry.getValue());
				String name = entry.getKey();
				String value = toFormat(entry.getValue());
				if (type != null && supported(type) && value != null) {
					str += name + ":" + value + ":" + type + ";";
					c++;
				}
			}
		}

		if (c > 0)
			str = str.substring(0, str.length() - 1);

		return str + "}";
	}

	public static String toFormat(Object o) {
		String str = "[";
		if (o == null) {
			str += "null";
		} else if (o instanceof Object[]) {
			Object[] arr = (Object[]) o;
			for (int i = 0; i < arr.length; i++) {
				str += toFormat(arr[i]);
				if (i != arr.length - 1)
					str += ",";
			}

		} else if (o instanceof ArrayList) {
			ArrayList<?> list = (ArrayList<?>) o;
			for (Object l : list) {
				str += toFormat(l) + ",";
			}
			if (list.size() > 0) {
				str = str.substring(0, str.length() - 1);
			}
		} else if (o instanceof HashMap) {
			HashMap<?, ?> map = (HashMap<?, ?>) o;
			for (Entry<?, ?> e : map.entrySet()) {
				str += toFormat(e.getKey()) + "=" + toFormat(e.getValue()) + ",";
			}
			if (map.size() > 0) {
				str = str.substring(0, str.length() - 1);
			}
		} else {
			str += o.toString();
		}
		return str + "]";
	}

	public static String toType(Object o) {
		String str = null;
		if (o == null)
			return null;

		if (o instanceof ArrayList) {
			ArrayList<?> list = (ArrayList<?>) o;
			String sub_type = list.size() > 0 ? toType(list.get(0)) : null;
			if (sub_type != null) {
				str = o.getClass().getSimpleName() + "<" + sub_type + ">";
			}
		} else if (o instanceof HashMap) {
			HashMap<?, ?> map = (HashMap<?, ?>) o;
			String key_type = "";
			String value_type = "";
			for (Entry<?, ?> e : map.entrySet()) {
				key_type = e.getKey() != null ? toType(e.getKey()) : null;
				value_type = e.getValue() != null ? toType(e.getValue()) : null;
				if (key_type != null && value_type != null) {
					str = o.getClass().getSimpleName() + "<[" + key_type + "],[" + value_type + "]>";
					break;
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

	public Object fromString(String value, String clazz) {
		for (Converter c : converters) {
			if (clazz.matches(c.regex)) {

				return c.convert(value.substring(1, value.length() - 1), clazz);
			}
		}
		return null;
	}

	public Class<?> getType(String clazz) {
		if (clazz.matches(".+(\\[\\])+")) {
			String t = clazz.substring(0, clazz.length() - 2);
			return createArray(getType(t), new String[0], t).getClass();
		}

		for (Converter c : converters) {
			if (clazz.matches(c.regex)) {

				return c.getClazz();
			}
		}
		return null;
	}

	public boolean supported(String type) {
		if (type.matches(".+(\\[\\])+")) {
			return supported(type.replace("[", "").replace("]", ""));
		} else if (type.matches("ArrayList<.+>")) {
			return supported(type.substring(10, type.length() - 1));
		} else if (type.matches("HashMap<.+,.+>")) {
			String[] split = split(type.substring(8, type.length() - 1));
			return supported(split[0].substring(1, split[0].length() - 1))
					&& supported(split[1].substring(1, split[1].length() - 1));
		}

		for (Converter c : converters) {
			if (type.matches(c.regex))
				return true;
		}
		return false;
	}

	public String[] split(String str) {
		ArrayList<String> values = new ArrayList<String>();
		int first_idx = 0;
		int c = 0;

		for (int i = 0; i < str.length(); i++) {
			switch (str.charAt(i)) {
			case '[':
				if (c == 0)
					first_idx = i;
				c++;
				break;
			case ']':
				c--;
				if (c == 0) {
					values.add(str.substring(first_idx, i + 1));
				}
			}
		}

		return values.toArray(new String[values.size()]);
	}

	public <Element> Element[] createArray(Class<Element> clazz, String[] values, String element_type) {
		Element[] array = (Element[]) Array.newInstance(clazz, values.length);
		for (int i = 0; i < values.length; i++) {
			array[i] = clazz.cast(fromString(values[i], element_type));
		}
		return array;
	}

	public <Element> ArrayList<Element> createArrayList(Class<Element> clazz, String[] values, String element_type) {
		ArrayList<Element> list = new ArrayList<Element>();
		for (String v : values) {
			list.add(clazz.cast(fromString(v, element_type)));
		}
		return list;
	}

	public <Key, Value> HashMap<Key, Value> createHashMap(Class<Key> key, Class<Value> value, String[] values,
			String key_type, String value_type) {
		HashMap<Key, Value> map = new HashMap<Key, Value>();

		for (int i = 0; i < values.length; i += 2) {
			map.put(key.cast(fromString(values[i], key_type)), value.cast(fromString(values[i + 1], value_type)));
		}

		return map;
	}

	public Object[] StringToArray(String str) {
		String[] split = split(str);
		Object[] o = new Object[split.length];

		for (int i = 0; i < split.length; i++) {
			String[] s = split[i].substring(1, split[i].length() - 1).split("§");

			o[i] = fromString(s[0], s[1]);
		}
		return o;
	}

	public static String ArrayToString(Object... o) {
		String str = "";
		for (int i = 0; i < o.length; i++) {
			str += "[" + toFormat(o[i]) + "§" + toType(o[i]) + "]";
			if (i != o.length - 1) {
				str += ",";
			}
		}
		return str;
	}

	public ZitatLoader getZitatLoader() {
		return z_loader;
	}

	class Converter {
		String regex;
		Class<?> c;
		Method m;

		<Typ> Converter(String regex, Class<Typ> c, Method m) {
			this.regex = regex;
			this.c = c;
			this.m = m;
		}

		public Object convert(String value, String clazz) {
			return m.convert(value, clazz);
		}

		public Class<?> getClazz() {
			return c;
		}
	}

	interface Method {
		public abstract Object convert(String value, String clazz);
	}

//	public static void main(String[] args) {
//		UserInformation ui = new UserInformation(null);
//		ArrayList<?> list = ui.createArrayList(ui.getType("String"), new String[] {"[12]","[123]", "[hello]"}, "String");
//		System.out.println(list);
//	}
}
