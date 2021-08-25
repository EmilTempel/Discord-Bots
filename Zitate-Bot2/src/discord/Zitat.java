package discord;

import java.util.ArrayList;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

public class Zitat {

	String inhalt, autor, untertitel, schreiber, ID;
	int elo, wins, playouts;
	ArrayList<String> tags;

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

			schreiber = msg.getAuthor().getName().trim();
			ID = msg.getId();
		}
		tags = new ArrayList<String>();
	}
	
	public Zitat(String[] values, Guild g) {
		
		
		
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

	public String getID() {
		return ID;
	}
	
	public void addTag(String tag) {
		tags.add(tag);
	}
	
	public ArrayList<String> getTags(){
		return tags;
	}
	
	public String toString() {
		return ID + "," + elo + "," + wins + "," + playouts + "," + tags.toString().replace("[", "").replace("]", "");
	}
}
