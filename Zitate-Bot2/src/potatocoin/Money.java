package potatocoin;

import java.util.ArrayList;

import discord.UserInformation;

public class Money extends Dropable {
	double amount;

	public Money(double amount) {
		this.amount = amount;
	}

	public void drop(UserInformation ui, String UserId) {
		Inventory i = ui.get(UserId, "inventory", Inventory.class);
		i.addCoins(amount);
	}
	
	public static ArrayList<Dropable> getDistribution(){
		return new ArrayList<Dropable>();
	}
	
}
