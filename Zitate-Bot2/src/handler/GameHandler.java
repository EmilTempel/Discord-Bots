package handler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import commands.Command;
import commands.MessageCommand;
import discord.Game;
import discord.UserInformation;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import spiel.Brett;
import spiel.Figur.Farbe;
import spiel.Spielzug;
import spiel.Zug;

public class GameHandler extends Handler {
	ZitatHandler zitathandler;

	Game game;
	Brett b;
	int p;

	public GameHandler(ZitatHandler zitathandler, UserInformation userinfo) {
		super(zitathandler.g, userinfo);
		this.zitathandler = zitathandler;
		commands = new Command[] {
				new MessageCommand('<', new String[] { "spiel", "s" }, new String[][] { new String[] { "\\d+" } },
						this::cmdSpiel),
				new MessageCommand('<', new String[] { "guess", "g" }, new String[][] { new String[] { ".+" } },
						this::cmdGuess),
				new MessageCommand('<', new String[] { "skip" }, new String[][] { new String[] {} }, this::cmdSkip),
				new MessageCommand('<', new String[] { "ergebnisse", "e" }, new String[][] { new String[] {} },
						this::cmdErgebnisse),
				new MessageCommand('<', new String[] { "schach" },
						new String[][] { new String[] {}, new String[] { "[a-h]\\d->[a-h]\\d" } }, this::cmdSchach) };
	}

	public void cmdSpiel(GuildMessageReceivedEvent e, String[] cmd_body) {
		int len = cmd_body[0] != null ? Integer.parseInt(cmd_body[0]) : 1;
		game = new Game(len, e.getGuild(), zitathandler);
		sendMessage(game.challenge(), e.getChannel());
	}

	public void cmdGuess(GuildMessageReceivedEvent e, String[] cmd_body) {
		String erg = "";
		if (game != null && cmd_body.length > 0 && cmd_body[0] != null) {
			if (game.guess(cmd_body[0], e.getAuthor().getName())) {
				erg = "Korrekt! \n" + game.challenge();
			} else {
				erg = "Leider Falsch!";
			}
		} else {
			erg = "Es gibt doch gar kein Spiel!";
		}
		sendMessage(erg, e.getChannel());
	}

	public void cmdSkip(GuildMessageReceivedEvent e, String[] cmd_body) {
		String erg = "";
		if (game == null) {
			erg = "Ey du Popokopf, welches Spiel?";
		} else if (game.isOver()) {
			erg = "Das Spiel is doch schon vorbei du Intelligenz Alleskika";
		} else {
			erg = "Die richtige Antwort war " + game.antwort() + ", du Geringverdiener\n" + game.challenge();
			game.addtoCounter(-1);
		}
		sendMessage(erg, e.getChannel());

	}

	public void cmdErgebnisse(GuildMessageReceivedEvent e, String[] cmd_body) {
		String erg = "";
		if (game == null) {
			erg = "Zu welchem Spiel? Bist du eine angebrunste Seuchkachel?";
		} else if (!game.isOver()) {
			erg = "Das Spiel läuft doch noch du halber europäischer Stör";
		} else {
			erg = game.ergebnis();
		}
		sendMessage(erg, e.getChannel());

	}

	public void cmdSchach(GuildMessageReceivedEvent e, String[] cmd_body) {
		if (b == null) {
			b = new Brett();
		} else {
			if (cmd_body.length > 0 && cmd_body[0] != null) {
				String[] zug = cmd_body[0].split("->");
				int[] x = new int[2];
				int[] y = new int[2];

				for (int i = 0; i < 2; i++) {
					x[i] = zug[i].charAt(0) - 'a';
					y[i] = zug[i].charAt(1) - '0' - 1;

					System.out.println(x[i] + "   " + y[i]);
				}

				ArrayList<Spielzug> alle_züge = b.giballeZüge(Farbe.values()[p]);

				Spielzug z = new Spielzug(x[0], y[0], new Zug(x[1] - x[0], y[1] - y[0]));

				if (z.isPartOf(alle_züge)) {
					b.ziehe(z);
					p = 1 - p;
				} else {
					sendMessage("Das Geht doch garned!", e.getChannel());
				}
			}
		}

		File f = new File("brett.png");
		try {
			ImageIO.write(b.asBild(Farbe.values()[1 - p]), "png", f);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		e.getChannel().sendFile(f).queue();
	}
}