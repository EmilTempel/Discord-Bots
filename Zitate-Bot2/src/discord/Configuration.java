package discord;

import java.util.HashMap;
import java.util.Set;

import commands.Command;

public class Configuration {

	HashMap<String, Boolean> activeCmds;
	Command[] cmd;
	UserInformation ui;

	public Configuration(UserInformation ui, Command[] cmd) {
		activeCmds = new HashMap<String, Boolean>();
		this.ui = ui;
		this.cmd = cmd;
		initiateConfig(cmd);
	}

	public void initiateConfig(Command[] cmd) {
		if (ui.get("guild", "config", HashMap.class) == null) {
			for (int i = 0; i < cmd.length; i++) {
				activeCmds.put(cmd[i].getName(), true);
			}
			ui.put("guild", "config", activeCmds);
		} else {
			activeCmds = ui.get("guild", "config", HashMap.class);
			System.out.println(activeCmds.toString());
			for (int i = 0; i < cmd.length; i++) {
				Boolean b;
				if ((b = activeCmds.get(cmd[i].getName())) != null) {
					cmd[i].setActive(b);
				}
			}
		}
	}

	public void set(String name, boolean b) {
		activeCmds.put(name, b);
		Command c = getCommandByName(name);
		if (c != null)
			c.setActive(b);
		ui.save();
	}

	public void setAll(boolean b) {
		for (Command c : cmd) {
			c.setActive(b);
		}
	}

	public boolean getActive(Command cmd) {
		return activeCmds.get(cmd.getName());
	}

	public Command getCommandByName(String name) {
		for (Command c : cmd) {
			if (c.getName().equals(name)) {
				return c;
			}
		}
		return null;
	}

	public String getFormattedConfig() {
		Set<String> cmd = activeCmds.keySet();
		StringBuilder sb = new StringBuilder();
		sb.append("momentane Konfiguration:");

		cmd.forEach(k -> {
			sb.append("\n    " + k + " - " + (activeCmds.get(k) ? "an" : "aus"));
		});

		return sb.toString();
	}
}
