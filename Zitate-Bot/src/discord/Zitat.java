package discord;

import net.dv8tion.jda.api.entities.Message;

public class Zitat {

	String inhalt, autor, untertitel, schreiber, ID;

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
}
