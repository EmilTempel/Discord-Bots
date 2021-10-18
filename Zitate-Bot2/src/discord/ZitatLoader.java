package discord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

import handler.Handler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import potatocoin.Dropable.Rarity;

public class ZitatLoader {
	
	static final int ID_len = 4;
	
	Guild g;
	MessageLoader loader;
	HashMap<String, Zitat> z;

	public ZitatLoader(Guild g, String... channels) {
		this.g = g;
		loader = new MessageLoader(g, channels);
		z = new HashMap<String, Zitat>();
		load();
		assignRarity();
//		numerate();
	}

	public void load() {
		for (Message m : loader.messages) {
			Zitat zitat = new Zitat(m);
			if (zitat.isFull())
				z.put(zitat.getPath(), zitat);
		}
	}

	public void assignRarity() {
		ArrayList<Zitat> zitate = new ArrayList<Zitat>();
		for (Zitat zitat : z.values()) {
			zitate.add(zitat);
		}

		zitate.sort((z1, z2) -> z2.getScore()[2] - z1.getScore()[2]);

		for (int i = 0; i < zitate.size(); i++) {
			double percent = (i + 1) / zitate.size();
			for (Rarity r : Rarity.values()) {
				if (percent >= r.getPercent()) {
					zitate.get(i).setRarity(r);
				}
			}
		}
	}

	public ArrayList<Zitat> getZitate() {
		ArrayList<Zitat> zitate = new ArrayList<Zitat>();
		for (Entry<String, Zitat> e : z.entrySet()) {
			zitate.add(e.getValue());
		}
		return zitate;
	}

	public Zitat getZitat(Object[] values) {
		Zitat z = null;
		if (values.length == 5) {
			z = getZitat((String) values[0], (String) values[1]);
			if (z != null) {
				z.setScore(UserInformation.convertInstanceOfObject(values[2], Integer[].class));
				System.out.println(Arrays.deepToString(z.getScore()));
				z.setTags(values[3] != null ? (ArrayList<String>) values[3] : new ArrayList<String>());
				z.setBesitzer((String) values[4]);
			}
		}
		return z;
	}

	public Zitat getZitat(String channel, String Id) {
		return getZitat(channel + "/" + Id);
	}

	public Zitat getZitat(String path) {
		if (z.get(path) == null && loader.getMessage(path) != null) {
			Zitat zitat = new Zitat(loader.getMessage(path));
			if (zitat.isFull()) {
				z.put(zitat.getPath(), zitat);
				return zitat;
			} else {
				return null;
			}
		} else {
			return z.get(path);
		}
	}

	public void numerate() {
		int c = 0;
		for (Message m : loader.messages) {
			if (getZitat(m.getChannel().getName(), m.getId()) != null) {
				int[] digits = IDealize(c,ID_len);
				System.out.println(Arrays.toString(digits));
				for (int i = 0; i < digits.length; i++) {
					System.out.println(Emoji.values()[digits[i]]);
					Handler.addReaction(m, Emoji.values()[digits[i]]);
					
				}
				c++;
			}
		}
	}
	
	public static int[] getFactors(int len) {
		int[] factor = new int[len];
		factor[len-1] = 1;
		for(int i = len-2; i >= 0; i--) {
			factor[i] = factor[i+1] * (9-i);
		}
		return factor;
	}

	public static int[] IDealize(int n, int len) {
		int[] factor = getFactors(len);
		
//		int[] idx = { n / 504, n % 504 / 56, n % 504 % 56 / 7, n % 504 % 56 % 7 };
		int[] idx = new int[len];
		for(int i = 0; i < len; i++) {
			int index = n;
			for(int j = 0; j < i; j++)
				index %= factor[j];
			idx[i] = index / factor[i];
		}
		
		
		int[] erg = new int[len];

		System.out.println();
		System.out.println(Arrays.toString(idx));
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < 10; i++) {
			list.add(i);
		}

		for (int i = 0; i < erg.length; i++) {
			erg[i] = list.get(idx[i]);
			list.remove((Integer) list.get(idx[i]));
		}

		return erg;
	}
	
	public static int reIDealize(int[] arr) {
		int[] factor = getFactors(arr.length);
		System.out.println(Arrays.toString(factor));
		
		int erg = 0;
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < 10; i++) {
			list.add(i);
		}
		
		for(int i = 0; i < arr.length; i++) {
			erg += list.indexOf(arr[i]) * factor[i];
			list.remove((Integer)arr[i]);
		}
		return erg;
	}

	public static void main(String[] args) {
		for (int i = 0; i < 1000; i++) {
			int[] l = IDealize(i,7);
			System.out.println(Arrays.toString(l) + "  " + reIDealize(l));
		}
	}
}
