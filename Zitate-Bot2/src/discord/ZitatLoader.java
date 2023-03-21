package discord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.IntStream;

import handler.Handler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import potatocoin.Dropable.Rarity;

public class ZitatLoader {

	Guild g;
	
	MessageLoader loader;
	HashMap<String, Zitat> z;

	public ZitatLoader(Guild g, String... channels) {
		this.g = g;
		loader = new MessageLoader(g, channels);
		z = new HashMap<String, Zitat>();
		load();
		assignRarity();
	}

	public void load() {
		for (Message m : loader.messages) {
			Zitat zitat = new Zitat(m);
			if (zitat.isFull()) {
				z.put(zitat.getPath(), zitat);
				zitat.setNumber(-1);
			}
		}
	}

	public void assignRarity() {
		ArrayList<Zitat> zitate = new ArrayList<Zitat>();
		for (Zitat zitat : z.values()) {
			zitate.add(zitat);
		}

		zitate.sort((z1, z2) -> z2.getScore()[2] - z1.getScore()[2]);

		for (int i = 0; i < zitate.size(); i++) {
			double percent = (i + 1) / (double) zitate.size();
			for (Rarity r : Rarity.values()) {
				if (percent >= 1 - r.getPercent()) {
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
		if (values.length == 6) {
			z = getZitat((String) values[0], (String) values[1]);
			if (z != null) {
				z.setScore(UserInformation.convertInstanceOfObject(values[2], Integer[].class));
				System.out.println(Arrays.deepToString(z.getScore()));
				z.setTags(values[3] != null ? (ArrayList<String>) values[3] : new ArrayList<String>());
				z.setBesitzer((String) values[4]);
				z.setNumber((Integer) values[5]);
			}
		}
		return z;
	}

	public Zitat getZitat(String channel, String Id) {
		return getZitat(channel + "/" + Id);
	}

	public Zitat getZitat(String path) {
		Message m = loader.getMessage(path);
		if (z.get(path) == null && m != null) {
			Zitat zitat = new Zitat(m);
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

	public MessageLoader getMessageLoader() {
		return loader;
	}
	
	public void clear() {
		int c = 0;
		for (Message m : loader.messages) {
			m.clearReactions().complete();
			System.out.println("cleared nmbr. " + c++);
		}
	}

	
	
}
