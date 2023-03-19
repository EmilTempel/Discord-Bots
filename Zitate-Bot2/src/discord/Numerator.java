package discord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import handler.Handler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;

public class Numerator {
	
	static int ID_len = 4;
	static int range;
	static Random random = new Random(69);
	static {
		range = 1;
		for (int i = 0; i < ID_len; i++) {
			range *= 10 - i;
		}
	}
	
	Guild g;
	UserInformation ui;
	List<Integer> usedIDs, IDPool;
	
	public Numerator(Guild g, UserInformation ui) {
		usedIDs = ui.orElseGet("guild", "usedIDs", ArrayList.class, new ArrayList<Integer>());
		IDPool = IntStream.range(0, range).filter(a -> !usedIDs.contains(a)).boxed().collect(Collectors.toList());
		this.g = g;
		this.ui = ui;
	}
	
	public void numerate() {
		List<Zitat> zitate = ui.get("guild", "zitate", ArrayList.class);
		for(Message m : ui.getZitatLoader().loader.messages) {
			Zitat zitat = null;
			for(Zitat z : zitate) {
				if(z != null && z.getChannel().equals(m.getChannel().getName()) && z.getID().equals(m.getId())) {
					zitat = z;
					break;
				}
			}
			numerate(zitat, m);
		}
	}

	public void numerate(Zitat z, Message m) {
		if (z != null) {
			int number = z.getNumber();
			if (number == -1) {
				number = IDPool.remove(random.nextInt(IDPool.size()));
				usedIDs.add(number);
				IDPool.remove(Integer.valueOf(number));
				z.setNumber(number);
			}
			System.out.println(m);
			int[] digits = IDealize(number, ID_len);
			if (!isCorrectlyNumerated(m, digits)) {
				System.out.println("numerated" + z.getAll());
				m.clearReactions().complete();
				for (int i = 0; i < digits.length; i++) {
					System.out.println(Emoji.values()[digits[i]]);
					Handler.addReaction(m, Emoji.values()[digits[i]]);
				}
			}

		}
	}
	
	public boolean isCorrectlyNumerated(Message m, int[] digits) {
		List<MessageReaction> reactions = m.getReactions();

		if (reactions.size() != ID_len) {
			return false;
		}
		for (int i = 0; i < ID_len; i++) {
			Emoji emoji = Emoji.fromUnicode(reactions.get(i).getReactionEmote().getAsCodepoints());
			if (emoji == null || emoji.ordinal() != digits[i]) {
				return false;
			}
		}
		return true;
	}

	public static int[] getFactors(int len) {
		int[] factor = new int[len];
		factor[len - 1] = 1;
		for (int i = len - 2; i >= 0; i--) {
			factor[i] = factor[i + 1] * (9 - i);
		}
		return factor;
	}

	public static int[] IDealize(int n, int len) {
		int[] factor = getFactors(len);

//		int[] idx = { n / 504, n % 504 / 56, n % 504 % 56 / 7, n % 504 % 56 % 7 };
		int[] idx = new int[len];
		for (int i = 0; i < len; i++) {
			int index = n;
			for (int j = 0; j < i; j++)
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

		for (int i = 0; i < arr.length; i++) {
			erg += list.indexOf(arr[i]) * factor[i];
			list.remove((Integer) arr[i]);
		}
		return erg;
	}

	public static void main(String[] args) {
		for (int i = 0; i < 1000; i++) {
			int[] l = IDealize(i, 7);
			System.out.println(Arrays.toString(l) + "  " + reIDealize(l));
		}
		System.out.println(range);
	}
}
