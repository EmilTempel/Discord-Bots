package potatocoin;

import java.util.ArrayList;

import discord.UserInformation;
import discord.Zitat;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;

public class Shop {

	double factor;
	final static double variation = 0.1;
	
	ArrayList<Zitat> dailyOffer;
	final static int nrOffers = 1;
	final static Dropable.Rarity[] excludedFromDailyOffers = {Dropable.Rarity.gaulitastisch, Dropable.Rarity.toxymoxy, Dropable.Rarity.marximal, Dropable.Rarity.wild};
	
	static Emote reactionBuy = null, reactionScrollUp = null, reactionScrollDown = null, reactionScrollRight = null, reactionScrollLeft = null;
	final static String buyName = ":geldluis:", scrollUpId = "U+2B06", scrollDownId = "U+2B07", scrollRightId = "U+27A1", scrollLeftId = "U+2B05";
	
	UserInformation ui;
	
	public Shop(Guild g, UserInformation ui, double factor, ArrayList<Zitat> dailyOffer) {
		reactionBuy = g.getEmotesByName(buyName, true).get(0);
		reactionScrollUp = g.getJDA().getEmoteById(scrollUpId);
		reactionScrollDown = g.getJDA().getEmoteById(scrollDownId);
		reactionScrollRight = g.getJDA().getEmoteById(scrollRightId);
		reactionScrollLeft = g.getJDA().getEmoteById(scrollLeftId);
		
		this.factor = factor;
		this.dailyOffer = dailyOffer;
	}
	
	public Zitat[] getIncludedAvailableZitate(){
		return null;
	}
	
	public void refresh() {
		factor += (((Math.random() * 2) - 1) * variation);
		dailyOffer.clear();
		for (int i = 0; i < nrOffers; i++) {
			
		}
	}
	
}
