package discord;

import java.util.ArrayList;

import net.dv8tion.jda.api.entities.Message;
import potatocoin.Dropable;
import potatocoin.Inventory;
import potatocoin.Shop;

public class Zitat extends Dropable{

	String inhalt, autor, untertitel, schreiber, ID, channel;
	Integer[] score; 
	ArrayList<String> tags;
	String besitzer;

	public Zitat(Message msg) {
		String z = msg.getContentRaw();
		if (z.contains(" -") && z.contains(",")) {
			String[] s = z.split(" -");
			inhalt = s[0].trim();

			if (s[1].contains(",")) {
				String[] u = s[1].split(",");
				autor = u[0].trim();
				untertitel = u[1].trim();
			}

			schreiber = msg.getAuthor().isBot() ? z.split("<")[1].replace(">","") : msg.getAuthor().getName().trim();
			ID = msg.getId();
			channel = msg.getChannel().getName();
		}
		score = new Integer[] {0,0,0};
		tags = new ArrayList<String>();
		besitzer = null;
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
		return channel+"/"+ID;
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
	
	public ArrayList<String> getTags(){
		return tags;
	}
	
	public void setBesitzer(String besitzer) {
		this.besitzer = besitzer;
	}
	
	public String getBesitzer() {
		return besitzer;
	}
	
	public double calcPrice() {
		double erg = score[2] * Shop.pricePerScore;
		return (erg < Shop.minPrice)? Shop.minPrice : erg;
	}
	
	public String toString() {
		return UserInformation.ArrayToString(new Object[] {channel, ID, score, tags, besitzer});
	}

	public void drop(UserInformation ui, String UserId) {
		Inventory i = ui.get(UserId, "inventory", Inventory.class);
		i.addZitat(this);
		besitzer = UserId;
	}
}
