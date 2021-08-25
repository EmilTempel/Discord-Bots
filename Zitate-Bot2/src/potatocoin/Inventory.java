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

import discord.Zitat;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class Inventory {

	private double coins;
	private ArrayList<Zitat> zitate;
	private HashMap<Challenge, Boolean> challenges;

	public Inventory(double coins, ArrayList<Zitat> zitate, HashMap<Challenge, Boolean> challenges) {
		this.coins = coins;
		this.zitate = zitate;
		this.challenges = challenges;
	}

	public Inventory(String userinfoString, Guild g) {
		String[] s = userinfoString.split(" | ");
		coins = Double.parseDouble(s[0].split(" ")[2]);

		String[] sz = s[1].split(" ");
		zitate = new ArrayList<Zitat>();
		TextChannel tc = g.getTextChannelsByName("zitate", true).get(0);
		for (int i = 1; i < sz.length; i++) {
			zitate.add(new Zitat(tc.retrieveMessageById(sz[i]).complete()));
		}

		s[2] = s[2].substring(13, s[2].length() - 1);
		String[] sc = s[2].split(", ");
		challenges = new HashMap<Challenge, Boolean>();
		for (int i = 0; i < sc.length; i++) {
			String[] sc0 = sc[i].split("=");
			try {
				challenges.put(new Challenge(sc0[0]), Boolean.parseBoolean(sc0[1]));
			} catch (NullPointerException n) {
				n.printStackTrace();
			}
		}
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

	public BufferedImage[] getAsImages() {
		BufferedImage toffler = null;

		try {
			toffler = ImageIO.read(new File("Toffler.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		BufferedImage img[] = new BufferedImage[((zitate.size() - 6) / 7) + 2];

		for (int i = 0; i < img.length; i++) {
			img[i] = new BufferedImage(600, 400, 0);
			Graphics g = img[i].getGraphics();

			Color back = new Color(140, 128, 128);
			g.setColor(back);
			g.fillRect(0, 0, img[i].getWidth(), img[i].getHeight());

		}
		Graphics g = img[0].getGraphics();
		g.setColor(Color.BLACK);
		g.setFont(new Font("Calibri", Font.BOLD, 30));
		g.drawString("Inventory", 20, 20);

		g.setFont(new Font("Calibri", Font.BOLD, 20));
		g.drawString("" + coins, 100, 60);
		g.drawImage(toffler, 200, 60, 20, 20, null);

		for (int i = 0; i < 6; i++) {
			if (zitate.size() > i) {
				g.drawString(i + ": " + zitate.get(i), 20, 100 + 50 * i);
			}
		}

		for (int i = 0; i < img.length; i++) {
			Graphics g0 = img[i].getGraphics();
			g0.setColor(Color.BLACK);
			g0.setFont(new Font("Calibri", Font.BOLD, 20));
			
			for (int j = 6 + i * 7; j < 13 + i * 7; i++) {
				if (zitate.size() > j) {
					g0.drawString(j + ": " + zitate.get(j), 20, 50 + 50 * j);
				}
			}
		}

		return img;
	}
	
	public String[] getFormatted(Member m) {
		int size = zitate.size() / 5;
		if (size % 5 != 0) {
			size++;
		}
		String[] erg = new String[size];
		
		erg[0] = m.getUser().getName() + "'s Inventory: \n";
		erg[0] += coins + " :toffler:";
		
		
		
		return erg;
	}

	public String toString() {
		String erg = "Inventory: Coins: ";

		erg += coins;

		erg += " | Zitate-IDs:";

		for (int i = 0; i < zitate.size(); i++) {
			erg += " " + zitate.get(i).toString();
		}

		erg += " | Challenges: ";

		erg += challenges.toString();

		return erg;
	}
}
