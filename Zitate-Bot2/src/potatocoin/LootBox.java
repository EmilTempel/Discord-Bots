package potatocoin;

import java.util.ArrayList;

import discord.UserInformation;
import discord.Zitat;

public class LootBox {

	static final double standard_sigma = 0.15;

	UserInformation ui;
	Type type;

	public LootBox(UserInformation ui, Type type) {
		this.ui = ui;
		this.type = type;
	}

	public Dropable open() {
		ArrayList<Dropable> available = type.getDropables(ui);
		return getNormalDistributed(available, type.sigma, type.myu, type.t);
	}

	public static <E> E getNormalDistributed(ArrayList<E> list, double sigma, double myu, double t) {
		double[] probs = new double[list.size()];

		double temp = 0;
		for (int i = 0; i < probs.length; i++) {
			probs[i] = norm_dist((double) (i + 0.5) / (double) probs.length, sigma, myu, t);
			temp += probs[i];
		}

		for (int i = 0; i < probs.length; i++) {
			probs[i] = probs[i] / temp;
		}
		temp = 0;

		for (int i = 0; i < probs.length; i++) {
			temp += probs[i];
			probs[i] = temp;
		}

		double rand = Math.random();
		for (int i = 0; i < probs.length; i++) {
			if (rand <= probs[i])
				return list.get(i);
		}

		return null;
	}

	public static double norm_dist(double x, double sigma, double myu, double t) {
		return 1 / (sigma * Math.sqrt(2 * Math.PI)) * Math.exp(-0.5 * Math.pow(((x - myu) / sigma), 2)) + t;
	}

	public static double getT(double sigma) {
		return 1 / (sigma * Math.sqrt(2 * Math.PI));
	}

	public enum Type {
		veryBad(standard_sigma, 0, 0, Type::getZitate, 20, "billige Lootbox mit schlechter Chance für gute Zitate"),
		Basic(standard_sigma, 0.5, 0, Type::getZitate, 50, "normale Lootbox mit normalen Chancen"),
		CoinMaster(standard_sigma, 0.5, 0, ui -> Money.getDistribution(), 15,
				"mit dieser geilen Lootbox wirst auch du der Coin Master $$$"),
		// TODO VVV
		Potaemom(standard_sigma, 0.5, 0, ui -> null, 150, "die Lootbox für alle deine Potaemon-Needs"),
		Boss(standard_sigma, 0.75, 0, Type::getAvailableZitate, 200, "Premium-Lootbox mit meistens sehr guten Zitaten"),
		ExtremeDangerDangerHighDanger(standard_sigma, 0.5, getT(standard_sigma), Type::getZitate, 100,
				"gefährliche Lootbox mit einem entweder sehr guten oder sehr schlechten Zitat (sehr gefährlich aber auch sehr lecker)");

		double sigma, myu, t;
		Function f;
		double price;
		String description;

		Type(double sigma, double myu, double t, Function f, double price, String description) {
			this.sigma = sigma;
			this.myu = myu;
			this.t = t;
			this.f = f;
			this.price = price;
			this.description = description;
		}

		public ArrayList<Dropable> getDropables(UserInformation ui) {
			return f.getDropables(ui);
		}

		static ArrayList<Dropable> getZitate(UserInformation ui) {
			ArrayList<Zitat> zitate = ui.get("guild", "zitate", ArrayList.class);
			ArrayList<Dropable> list = new ArrayList<Dropable>();
			for (Zitat z : zitate)
				list.add(z);
			return list;
		}

		static ArrayList<Dropable> getAvailableZitate(UserInformation ui) {
			ArrayList<Zitat> zitate = ui.get("guild", "zitate", ArrayList.class);
			ArrayList<Dropable> list = new ArrayList<Dropable>();

			for (Zitat z : zitate) {
				if (z.getBesitzer() == null)
					list.add(z);
			}
			if (list.size() == 0)
				for (Zitat z : zitate)
					list.add(z);

			return list;
		}

		public interface Function {
			public abstract ArrayList<Dropable> getDropables(UserInformation ui);
		}

	}
//	
//	public static void main(String[]args) {
//		ArrayList<Integer> nums = new ArrayList<Integer>();
//		ArrayList<Integer> counter = new ArrayList<Integer>();
//		
//		for(int i = 0; i < 1000; i++) {
//			nums.add(i);
//			counter.add(0);
//		}
//		
//		for(int i = 0; i < 100000; i++) {
//			Integer integer = getNormalDistributed(nums, standard_sigma, 0.5, 0);
//			counter.set(integer, counter.get(integer)+1);
//		}
//		
//		System.out.println(counter);
//	}

}
