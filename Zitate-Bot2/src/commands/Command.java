package commands;

import net.dv8tion.jda.api.events.Event;

public abstract class Command {
	boolean active = true;
	
	public abstract void execute(Event e, String... cmd_body);
	public abstract String getName();
	public void setActive(boolean active) {
		this.active = active;
	}
}
