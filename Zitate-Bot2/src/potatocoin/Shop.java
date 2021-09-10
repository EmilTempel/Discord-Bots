package potatocoin;

import java.util.ArrayList;
import java.util.HashMap;

import discord.Zitat;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;

public class Shop {

	double factor;
	ArrayList<Zitat> dailyOffer;
	final static Dropable.Rarity[] excludedFromDailyOffers = {};
	HashMap<LootBox, Double> lootBoxes;
	static Emote reactionBuy = null, reactionScrollUp = null, reactionScrollDown = null, reactionScrollRight = null, reactionScrollLeft = null;
	final static String buyName = ":geldluis:", scrollUpId = "U+2B06", scrollDownId = "U+2B07", scrollRightId = "U+27A1", scrollLeftId = "U+2B05";
	
	public Shop(Guild g, double factor, ArrayList<Zitat> dailyOffer) {
		reactionBuy = g.getEmotesByName(buyName, true).get(0);
		reactionScrollUp = g.getJDA().getEmoteById(scrollUpId);
		reactionScrollDown = g.getJDA().getEmoteById(scrollDownId);
		reactionScrollRight = g.getJDA().getEmoteById(scrollRightId);
		reactionScrollLeft = g.getJDA().getEmoteById(scrollLeftId);
		
		this.factor = factor;
		this.dailyOffer = dailyOffer;
	}

}
