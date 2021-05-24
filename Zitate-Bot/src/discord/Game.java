package discord;

import java.util.HashMap;
import java.util.Map;

import net.dv8tion.jda.api.entities.Guild;

public class Game {

	int len, c;
	Map<String, Integer> map;

	Zitat antwort;
	ZitatMaster zm;
	Guild g;

	public Game(int len, Guild g, ZitatMaster zm) {
		this.len = len;
		this.zm = zm;
		this.g = g;
		this.map = new HashMap<String,Integer>();
	}

	public String challenge() {
		if (!isOver()) {
			c++;
			antwort = zm.randomZitat();
			return antwort.inhalt;
		}else {
			return "Game Over! <ergebnisse";
		}
	}
	
	public boolean isOver() {
		return !(c < len);
	}

	public boolean guess(String antwort, String autor) {
		if (antwort.equalsIgnoreCase(this.antwort.autor)) {
			if (map.get(autor) == null) {
				map.put(autor, 1);
			} else {
				map.put(autor, map.get(autor) + 1);
			}
			return true;
		} else {
			return false;
		}
	}
	
	public String antwort() {
		return antwort.getAutor();
	}

	public String ergebnis() {
		return ZitatMaster.toString(map.entrySet(),true,true,false);
	}

}