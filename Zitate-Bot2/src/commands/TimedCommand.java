package commands;

import net.dv8tion.jda.api.events.Event;

public class TimedCommand extends Command{
	
	Executable<Integer> exe;
	String name;
	long period;
	
	public TimedCommand(String name, long period, Executable<Integer> exe) {
		this.name = name;
		this.exe = exe;
		this.period = period;
	}

	public void execute(Event e, String... cmd_body) {
		exe.run(69, cmd_body);
	}

	public String getName() {
		return name;
	}
}
