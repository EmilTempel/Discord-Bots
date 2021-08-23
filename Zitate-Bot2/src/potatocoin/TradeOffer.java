package potatocoin;

import java.util.ArrayList;

import discord.UserInformation;
import discord.Zitat;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public class TradeOffer {

	Member from, to;
	ArrayList<Zitat> angZ, fordZ;
	double coinBalance;
	Guild g;
	UserInformation ui;

	public TradeOffer(Guild g, UserInformation ui, Member from, Member to, ArrayList<Zitat> angZ,
			ArrayList<Zitat> fordZ, double coinBalance) {
		this.g = g;
		this.ui = ui;
		this.from = from;
		this.to = to;
		this.angZ = angZ;
		this.fordZ = fordZ;
		// CoinBalance > 0 ==> fromMember zahlt Coins an toMember
		this.coinBalance = coinBalance;
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

}
