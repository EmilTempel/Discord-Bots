package discord;

import java.util.HashMap;

import commands.Command;

public class Configuration {

	HashMap<Command, Boolean> activeCmds;
	UserInformation ui;

	public Configuration(UserInformation ui) {
		activeCmds = new HashMap<Command, Boolean>();
		this.ui = ui;
	}

	public void initiateConfig(Command[] cmd) {
		if (ui.get("guild", "config", HashMap.class) != null) {
			for (int i = 0; i < cmd.length; i++) {
				activeCmds.put(cmd[i], true);
			}
			ui.put("guild", "config", activeCmds);
		} else {
			activeCmds = ui.get("guild", "config", HashMap.class);
		}
	}

	public void set(Command cmd, boolean b) {
		activeCmds.put(cmd, b);
		ui.put("guild", "config", activeCmds);
	}

	public void setAll(boolean b) {
		Command[] cmd = (Command[]) activeCmds.keySet().toArray();
		for (int i = 0; i < cmd.length; i++) {
			activeCmds.put(cmd[i], b);
		}
		ui.put("guild", "config", activeCmds);
	}

	public boolean getActive(Command cmd) {
		return activeCmds.get(cmd);
	}

	public String getFormattedConfig() {
		String erg = "momentane Konfiguration:";
		Command[] cmd = (Command[]) activeCmds.keySet().toArray();

		for (int i = 0; i < cmd.length; i++) {
			erg += "\n    " + cmd[i].getName() + " - " + (activeCmds.get(cmd[i]) ? "an" : "aus");
		}

		return erg;
	}

	public String toString() {
		// TODO
		return null;
	}
}
