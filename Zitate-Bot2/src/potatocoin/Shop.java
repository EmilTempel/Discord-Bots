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
	final static Dropable.Rarity[] includedInDailyOffers = {Dropable.Rarity.geringverdienend, Dropable.Rarity.sonervigeinfach, Dropable.Rarity.uuitoll};
	
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
	
	public Shop(Guild g, UserInformation ui, Object[] o) {
		reactionBuy = g.getEmotesByName(buyName, true).get(0);
		reactionScrollUp = g.getJDA().getEmoteById(scrollUpId);
		reactionScrollDown = g.getJDA().getEmoteById(scrollDownId);
		reactionScrollRight = g.getJDA().getEmoteById(scrollRightId);
		reactionScrollLeft = g.getJDA().getEmoteById(scrollLeftId);
		
		this.factor = (double) o[0];
		this.dailyOffer = (ArrayList<Zitat>) o[1];
	}
	
	public ArrayList<Zitat> getIncludedAvailableZitate(){
		ArrayList<Zitat> alleZitate = ui.get("guild", "zitate", ArrayList.class), erg = new ArrayList<Zitat>();
		for (Zitat z : alleZitate) {
			if (z.getBesitzer() == null){
				for (int i = 0; i < includedInDailyOffers.length; i++) {
					if (z.getRarity() == includedInDailyOffers[i]) {
						erg.add(z);
						break;
					}
				}		
			}
		}
		return erg;
	}
	
	public void refresh() {
		factor += (((Math.random() * 2) - 1) * variation);
		
		dailyOffer.clear();
		ArrayList<Zitat> zitate = getIncludedAvailableZitate();
		for (int i = 0; i < nrOffers; i++) {
			dailyOffer.add(zitate.get((int) (Math.random() * zitate.size())));
		}
	}
	
	public String[] toFormat() {
		return null;
	}
	
	public String toString() {
		return UserInformation.ArrayToString(factor, dailyOffer);
	}
}
