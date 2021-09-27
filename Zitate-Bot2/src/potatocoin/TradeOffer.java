package potatocoin;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import discord.UserInformation;
import discord.Zitat;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class TradeOffer {

	User from, to;
	ArrayList<Zitat> angZ, fordZ;
	double coinBalance;
	Guild guild;
	UserInformation ui;

	public TradeOffer(Guild guild, UserInformation ui, User from, User to, ArrayList<Zitat> angZ,
			ArrayList<Zitat> fordZ, double coinBalance) {
		this.guild = guild;
		this.ui = ui;
		this.from = from;
		this.to = to;
		this.angZ = angZ;
		this.fordZ = fordZ;
		// CoinBalance > 0 ==> fromMember zahlt Coins an toMember
		this.coinBalance = coinBalance;
	}
	
	public TradeOffer(Guild guild, UserInformation ui, Object[] o) {
		this.guild = guild;
		this.ui = ui;
		this.from = (User) o[0];
		this.to = (User) o[1];
		// CoinBalance > 0 ==> fromMember zahlt Coins an toMember
		this.coinBalance = (double) o[2];
		this.angZ = (ArrayList<Zitat>) o[3];
		this.fordZ = (ArrayList<Zitat>) o[4];
	}

	public void execute() {
		Inventory fromInv = ui.get(from.getId(), "inventory", Inventory.class),
				toInv = ui.get(to.getId(), "inventory", Inventory.class);
		fromInv.addCoins(-coinBalance);
		toInv.addCoins(coinBalance);

		for (int i = 0; i < fordZ.size(); i++) {
			fromInv.addZitat(fordZ.get(i));
			toInv.removeZitat(fordZ.get(i));
		}
		for (int i = 0; i < angZ.size(); i++) {
			toInv.addZitat(angZ.get(i));
			fromInv.removeZitat(angZ.get(i));
		}
	}
	
	public User getToUser() {
		return to;
	}
	
	public double getCoinBalance() {
		return coinBalance;
	}

	public BufferedImage getAsImage() {
		BufferedImage img = null, iToffler = null;
//		int width = 607, height = 794;
		try {
			img = ImageIO.read(new File("tradeoffer.png"));
			iToffler = ImageIO.read(new File("Toffler.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		Graphics g = img.getGraphics();
		g.setColor(Color.WHITE);
		g.setFont(new Font("Calibri", Font.BOLD, 30));

		if (coinBalance > 0) {
			g.drawString("" + coinBalance, 400, 220);
			g.drawImage(iToffler, 530, 180, 65, 65, null);
		} else if (coinBalance < 0) {
			g.drawString("coinBalance", 80, 220);
			g.drawImage(iToffler, 170, 180, 65, 65, null);
		}

		for (int i = 0; i < angZ.size(); i++) {
			g.drawString(angZ.get(i).getAll().substring(0, 17) + "...", 30, 230 + 30 * i);
		}
		for (int i = 0; i < fordZ.size(); i++) {
			g.drawString(angZ.get(i).getAll().substring(0, 17) + "...", 330, 230 + 30 * i);
		}

		g.setFont(new Font("Calibri", Font.BOLD, 50));
		g.setColor(Color.BLACK);
		g.drawString(to.getName() + ",", 150, 700);

		g.finalize();
		return img;
	}
	
	public String toString() {
//		String erg = from.getId() + ";" + to.getId() + ";" + coinBalance + ";" + angZ.toString() + ";" + fordZ.toString();
//		return erg;
		return UserInformation.ArrayToString(from, to, coinBalance, angZ, fordZ);
	}
	
}
