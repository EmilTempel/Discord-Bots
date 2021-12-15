package commands;

import net.dv8tion.jda.api.events.Event;

public class TimedCommand extends Command{
	
	Executable<Integer> exe;
	String name;
	long period, counter, maxCounter;
	
	public TimedCommand(String name, long period, Executable<Integer> exe) {
		this.name = name;
		this.exe = exe;
		this.period = period;
		this.counter = 0;
		this.maxCounter = 0;
	}

	public void execute(Event e, String... cmd_body) {
		exe.run(69, cmd_body);
	}

	public String getName() {
		return name;
	}
	
	public long getPeriod() {
		return period;
	}
	
	public long getCounter() {
		return counter;
	}
	
	public void updateCounter() {
		if(counter == 0) {
			counter = maxCounter-1;
		}else {
			counter--;
		}
	}
	
	public void setMaxCounter(long c) {
		maxCounter = c;
	}
}
