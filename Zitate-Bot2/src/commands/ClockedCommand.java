package commands;

import java.util.Date;

public class ClockedCommand extends TimedCommand{

	long time;
	
	public ClockedCommand(String name, long time, Executable<Integer> exe) {
		super(name, 1000 * 60, exe);
		this.time = time;
	}
	
	public ClockedCommand(String name, ) {
		this();
	}
	
	
}
