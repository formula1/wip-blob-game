package input;

import controller.Player;

public abstract class InputListener implements Comparable<InputListener>{

	public String name;
	public String command;
	public int priority;
	
	public int currentIndex=0;
	public InputCommand[] ic;
	private Player player;
	
	public InputListener(int priority, String name, String command){
		this.priority = priority;
		this.name = name;
		this.command = command;
		ic = parseCommand(command);
	}
	
	private InputCommand[] parseCommand(String command){
		
		return null;
	}
	
	public abstract void doAction();
	
	public boolean newInput(String input, boolean onoff){
		if()
	}
	
	public int compareTo(InputListener il){
		if(priority == il.priority)
			return this.name.compareTo(il.name);
		else return il.priority - this.priority ;
	}
	
	private abstract class InputCommand{
		public boolean dont_interrupt;
		public int time_till_reset;
		
		public abstract boolean check(String input, boolean onoff);
	}
	
	private class ReleaseCommand extends InputCommand{
		String input;
		public ReleaseCommand(String input){
			this.input = input;
		}
		public boolean check(String inpput, boolean onoff) {
			if(onoff || !inpput.equals(input)) return false;
			return true;
		}
	}
	private class PressCommand extends InputCommand{
		String input;
		public PressCommand(String input){
			this.input = input;
		}
		public boolean check(String inpput, boolean onoff) {
			if(!onoff || !inpput.equals(input)) return false;
			return true;
		}
	}
	private class TimeCommand extends InputCommand{
		private int timeleft;
		InputCondition[] conditions;
		public TimeCommand(){
		}
		public boolean check(String inpput, boolean onoff) {
			if(conditionsCheck()) 
				if(timeleft == 0)return true;
				else timeleft--;
			 return false;
		}
		
		public boolean conditionsCheck(){
			for(InputCondition ic : conditions)
				if(!ic.check()) return false;
			return true;
		}

	}

	
	private class InputCondition{
		public String[] tohold;
		public String[] torel;
		public InputCondition[] conditions;
		
		public boolean check(){
			for(String h : tohold)
				if(player.buttons.get(tohold) < .5) return false;
			for(String h : torel)
				if(player.buttons.get(torel) > .5) return false;
			return true;
		}
	}
}
