package potatocoin;

import java.util.ArrayList;

import discord.UserInformation;
import discord.Zitat;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import potatocoin.LootBox.Type;

public class Shop {

	double factor;
	final static double variation = 0.1;

	public final static double pricePerScore = 0.1, minPrice = 100;

	ArrayList<Zitat> dailyOffer;
	final static int nrOffers = 1;
	final static Dropable.Rarity[] includedInDailyOffers = { Dropable.Rarity.geringverdienend,
			Dropable.Rarity.sonervigeinfach, Dropable.Rarity.uuitoll };

	static Emote reactionBuy = null, reactionScrollUp = null, reactionScrollDown = null, reactionScrollRight = null,
			reactionScrollLeft = null;
	final static String buyName = ":geldluis:", scrollUpId = "U+2B06", scrollDownId = "U+2B07",
			scrollRightId = "U+27A1", scrollLeftId = "U+2B05";

	UserInformation ui;

	double nextResetInMins = 60 * 24;
	final static int resetIntervalMins = 24 * 60;

	public Shop(Guild g, UserInformation ui, double factor, ArrayList<Zitat> dailyOffer, double nextResetInMins) {
		reactionBuy = g.getEmotesByName(buyName, true).get(0);
		reactionScrollUp = g.getJDA().getEmoteById(scrollUpId);
		reactionScrollDown = g.getJDA().getEmoteById(scrollDownId);
		reactionScrollRight = g.getJDA().getEmoteById(scrollRightId);
		reactionScrollLeft = g.getJDA().getEmoteById(scrollLeftId);

		this.factor = factor;
		this.dailyOffer = dailyOffer;
		this.nextResetInMins = nextResetInMins;
	}

	public Shop(Guild g, UserInformation ui, Object[] o) {
		reactionBuy = g.getEmotesByName(buyName, true).get(0);
		reactionScrollUp = g.getJDA().getEmoteById(scrollUpId);
		reactionScrollDown = g.getJDA().getEmoteById(scrollDownId);
		reactionScrollRight = g.getJDA().getEmoteById(scrollRightId);
		reactionScrollLeft = g.getJDA().getEmoteById(scrollLeftId);

		this.factor = (double) o[0];
		this.dailyOffer = (ArrayList<Zitat>) o[1];
		this.nextResetInMins = (double) o[2];
	}

	public ArrayList<Zitat> getIncludedAvailableZitate() {
		ArrayList<Zitat> alleZitate = ui.get("guild", "zitate", ArrayList.class), erg = new ArrayList<Zitat>();
		for (Zitat z : alleZitate) {
			if (z.getBesitzer() == null) {
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

	public MessageEmbed[] toFormat() {
		MessageEmbed[] erg = new MessageEmbed[4];

		String[] seiten = new String[] { "Zitate im Angebot", "LootBoxen", "Potaemon Stuff", "Challenges" };
		String[][] inhalt = new String[][] { new String[dailyOffer.size()], new String[Type.values().length],
				new String[] { "\ncoming soon, arschloch" }, new String[] { "\nunder construction, du huso" } };

		for (int i = 0; i < dailyOffer.size(); i++) {
			Zitat z = dailyOffer.get(i);
			inhalt[0][i] += z.getRarity().name() + "es Zitat für " + (int) (z.calcPrice() * factor) + " - " + z.getAll()
					+ "\n";
		}

		for (int i = 0; i < erg.length; i++) {
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("SHOP");
			eb.setDescription("nächster Reset in " + nextResetInMins + " Minuten\n\n" + seiten[i]);

		}

//				{ "**SHOP**\nnext reset in: " + nextResetInMins + " minutes\n__Zitate im Angebot__\n",
//				"**SHOP**\nnext reset in: " + nextResetInMins + " minutes\n__LootBoxen__\n",
//				"**SHOP**\nnext reset in: " + nextResetInMins + " minutes\n__Potaemon Stuff__\n",
//				"**SHOP**\nnext reset in: " + nextResetInMins + " minutes\n__Challenges__\n" };
//		
		for (int i = 0; i < dailyOffer.size(); i++) {
			Zitat z = dailyOffer.get(i);
			erg[0] += "\n" + z.getRarity().name() + "es Zitat für " + (int) (z.calcPrice() * factor) + " - "
					+ z.getAll();
		}

		for (Type t : Type.values()) {
			erg[1] += "\n" + (int) (t.price * factor) + " - " + t.name() + " LootBox";
		}

		erg[2] += "\ncoming soon, arschloch";

		erg[3] += "\nunder construction, du huso";

		return erg;
	}

	public String toString() {
		return UserInformation.ArrayToString(factor, dailyOffer, nextResetInMins);
	}
}
