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

	public Inventory(double coins, ArrayList<Zitat> zitate, ArrayList<LootBox> lootboxen, HashMap<Challenge, Boolean> challenges) {
		this.coins = coins;
		this.zitate = zitate;
		this.lootboxen = lootboxen;
		this.challenges = challenges;
	}

	public Inventory(Object[] o) {
		this.coins = (double) o[0];
		this.zitate = (ArrayList<Zitat>) o[1];
		this.lootboxen = (ArrayList<LootBox>) o[2];
		this.challenges = (HashMap<Challenge, Boolean>) o[3];
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
	
	public String[][][] toFormat(Member m) {
		int size0 = zitate.size() / itemsPerPage;
		if (zitate.size() % itemsPerPage != 0) {
			size0++;
		}
		int size1 = lootboxen.size() / itemsPerPage;
		if (lootboxen.size() % itemsPerPage != 0) {
			size1++;
		}
		int size2 = challenges.size() / itemsPerPage;
		if (challenges.size() % itemsPerPage != 0) {
			size2++;
		}
		String[][][] erg = new String[size0 + size1 + size2][itemsPerPage][3];
		
		for (int i = 0; i < size0; i++) {
			erg[i][0][0] = "Zitate";
			for (int j = 1; j < itemsPerPage + 1; j++) {
				try {
					erg[i][j][0] = zitate.get(i * itemsPerPage + j - 1).getRarity() + "enes Zitat";
					erg[i][j][1] = zitate.get(i * itemsPerPage + j - 1).getAll();
					erg[i][j][2] = "false";
				}catch(Exception x) {
					x.printStackTrace();
					erg[i][j][0] = "";
					erg[i][j][1] = "";
					erg[i][j][2] = "false";
				}
			}
		}
		for (int i = size0; i < size0 + size1; i++) {
			erg[i][0][0] = "Lootboxen";
			for (int j = 1; j < itemsPerPage + 1; j++) {
				try {
					erg[i][j][0] = lootboxen.get(i * itemsPerPage + j - 1).type.name();
					erg[i][j][1] = lootboxen.get(i * itemsPerPage + j - 1).type.description;
					erg[i][j][2] = "false";
				}catch(Exception x) {
					x.printStackTrace();
					erg[i][j][0] = "";
					erg[i][j][1] = "";
					erg[i][j][2] = "false";
				}
			}
		}
		for (int i = size0 + size1; i < size0 + size1 + size2; i++) {
			erg[i][0][0] = "Challenges";
			for (int j = 1; j < itemsPerPage + 1; j++) {
//				try {
//					erg[i][j][0] = challenges.get(i * itemsPerPage + j - 1).type.name();
//					erg[i][j][1] = challenges.get(i * itemsPerPage + j - 1).type.description;
//					erg[i][j][2] = "false";
//				}catch(Exception x) {
//					x.printStackTrace();
					erg[i][j][0] = "";
					erg[i][j][1] = "";
					erg[i][j][2] = "false";
//				}
			}
		}
		
//				new String[size];
//		
//		erg[0] = m.getUser().getAsMention() + "'s Inventory: \n";
//		erg[0] += coins + " :toffler:";
//		
//		
//		
		return erg;
	}

	public String toString() {
		return UserInformation.ArrayToString(coins, lootboxen, zitate, challenges);
	}
}
