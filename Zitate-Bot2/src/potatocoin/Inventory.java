package potatocoin;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import discord.UserInformation;
import discord.Zitat;
import net.dv8tion.jda.api.entities.Member;

public class Inventory {

	private double coins;
	private ArrayList<Zitat> zitate;
	private ArrayList<LootBox> lootboxen;
	private HashMap<Challenge, Boolean> challenges;

	private static int itemsPerPage = 5;

	public Inventory(double coins, ArrayList<Zitat> zitate, ArrayList<LootBox> lootboxen,
			HashMap<Challenge, Boolean> challenges) {
		this.coins = coins;
		this.zitate = zitate;
		this.lootboxen = lootboxen;
		this.challenges = challenges;
	}

	public Inventory(Object[] o) {
		this.coins = (double) o[0];
		this.zitate = o[1] != null ? (ArrayList<Zitat>) o[1] : new ArrayList<>();
		this.lootboxen = o[2] != null ? (ArrayList<LootBox>) o[2] : new ArrayList<>();
		this.challenges = o[3] != null ? (HashMap<Challenge, Boolean>) o[3] : new HashMap<>();
	}

	public void addCoins(double amount) {
		setCoins(getCoins() + amount);
	}

	public double getCoins() {
		return coins;
	}

	public void setCoins(double coins) {
		this.coins = coins;
	}

	public void addZitat(Zitat z) {
		zitate.add(z);
	}

	public void removeZitat(int i) {
		zitate.remove(i);
	}

	public void removeZitat(Zitat z) {
		zitate.remove(z);
	}

	public ArrayList<Zitat> getZitate() {
		return zitate;
	}
	
	

	public String toString() {
		return UserInformation.ArrayToString(coins, lootboxen, zitate, challenges);
	}
}
