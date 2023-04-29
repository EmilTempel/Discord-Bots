package discord;

import java.util.ArrayList;

import net.dv8tion.jda.api.entities.Message;
import potatocoin.Dropable;
import potatocoin.Inventory;
import potatocoin.Shop;

public class Zitat extends Dropable implements Comparable<Zitat> {

	String inhalt, autor, untertitel, schreiber, ID, channel;
	Integer[] score;
	ArrayList<String> tags;
	int number;
	String besitzer;

	public Zitat(Message msg) {
		String z = msg.getContentRaw();
		if (z.length() > 0 && (z.charAt(0) == '"' || z.charAt(0) == '“' || z.charAt(0) == '„')) {
			for (int i = 1; i < z.length(); i++) {
				if (z.charAt(i) == '"' || z.charAt(i) == '“') {
					inhalt = z.substring(0, i + 1);
					for (int j = i + 1; j < z.length(); j++) {
						if (z.charAt(j) == '-') {
							for (int k = j + 1; k < z.length(); k++) {
								if(z.charAt(k) == ',') {
								autor = z.substring(j+1, k).trim();
								untertitel = z.substring(k+1, z.length());
								break;
								}
							}
							break;
						}
					}
					break;
				}
			}

			schreiber = msg.getAuthor().isBot() ? z.split("<")[1].replace(">", "") : msg.getAuthor().getName().trim();
			ID = msg.getId();
			channel = msg.getChannel().getName();
		}
		score = new Integer[] { 0, 0, 0 };
		tags = new ArrayList<String>();
		besitzer = null;
		number = -1;
	}

	public boolean isFull() {
		return inhalt != null && autor != null && untertitel != null && schreiber != null;
	}

	public String getAll() {
		return inhalt + " -" + autor + "," + untertitel;
	}

	public String getInhalt() {
		return inhalt;
	}

	public String getAutor() {
		return autor;
	}

	public String getUntertitel() {
		return untertitel;
	}

	public String getSchreiber() {
		return schreiber;
	}

	public String getChannel() {
		return channel;
	}

	public String getID() {
		return ID;
	}

	public String getPath() {
		return channel + "/" + ID;
	}

	public void setScore(int i, int value) {
		score[i] = value;
	}

	public void setScore(Integer[] arr) {
		score = arr;
	}

	public Integer[] getScore() {
		return score;
	}

	public void addTag(String tag) {
		tags.add(tag);
	}

	public void setTags(ArrayList<String> list) {
		tags = list;
	}

	public ArrayList<String> getTags() {
		return tags;
	}

	public void setBesitzer(String besitzer) {
		this.besitzer = besitzer;
	}

	public String getBesitzer() {
		return besitzer;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getNumber() {
		return number;
	}

	public double calcPrice() {
		double erg = score[2] * Shop.pricePerScore;
		return (erg < Shop.minPrice) ? Shop.minPrice : erg;
	}

	public String toString() {
		return UserInformation.ArrayToString(new Object[] { channel, ID, score, tags, besitzer, number});
	}

	public void drop(UserInformation ui, String UserId) {
		Inventory i = ui.get(UserId, "inventory", Inventory.class);
		i.addZitat(this);
		besitzer = UserId;
	}

	@Override
	public int compareTo(Zitat z) {
		return Integer.compare(z.getScore()[2], this.getScore()[2]);
	}
	
//	public enum Format{
//		
//		MONOLOG, DIALOG;
//		
//		FormatChecker checker;
//		
//		Format(FormatChecker checker){
//			this.checker = checker;
//		}
//	}
//	
//	interface FormatChecker{
//		public boolean check(String s);
//	}
}
