package commands;

import net.dv8tion.jda.api.events.Event;

public interface Command {
	public abstract void execute(Event e, String... cmd_body);
}
