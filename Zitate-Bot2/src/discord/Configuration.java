package discord;

import java.util.HashMap;
import java.util.Set;

import commands.Command;

public class Configuration {

	HashMap<Command, Boolean> activeCmds;
	UserInformation ui;

	public Configuration(UserInformation ui, Command[] cmd) {
		activeCmds = new HashMap<Command, Boolean>();
		this.ui = ui;
		initiateConfig(cmd);
	}

	public void initiateConfig(Command[] cmd) {
		if (ui.get("guild", "config", HashMap.class) == null) {
			for (int i = 0; i < cmd.length; i++) {
				activeCmds.put(cmd[i], true);
			}
			ui.put("guild", "config", activeCmds);
		} else {
			activeCmds = ui.get("guild", "config", HashMap.class);
			for(int i = 0; i < cmd.length; i++) {
				cmd[i].setActive(activeCmds.get(cmd[i]));
			}
		}
	}

	public void set(Command cmd, boolean b) {
		activeCmds.put(cmd, b);
		ui.put("guild", "config", activeCmds);
		cmd.setActive(b);
	}

	public void setAll(boolean b) {
		Set<Command> cmd = activeCmds.keySet();
		cmd.forEach(k -> {
			activeCmds.put(k, b);
			k.setActive(b);
		});
		ui.put("guild", "config", activeCmds);
	}

	public boolean getActive(Command cmd) {
		return activeCmds.get(cmd);
	}

	public String getFormattedConfig() {
		Set<Command> cmd = activeCmds.keySet();
		StringBuilder sb = new StringBuilder();
		sb.append("momentane Konfiguration:");

		cmd.forEach(k -> {
			sb.append("\n    " + k.getName() + " - " + (activeCmds.get(k) ? "an" : "aus"));
		});

		return sb.toString();
	}

	public String toString() {
		// TODO
		return null;
	}
}
