package garbage;


import java.util.HashMap;

import org.jbox2d.dynamics.Body;

public class FighterEntity {
	Body dummy;
	Body fixed_notfixed;
	String currentState;
	HashMap<String, ModifierState> states;
	
	public FighterEntity(ModifierState[] applied){
		states = new HashMap<String,ModifierState>();
		for(ModifierState m : applied){
			states.put(m.name, m);
		}
	}
	
	public void command(){
		/*
		 * There are both world commands, player commands and hitbox commands
		 * 
		 * World Commands
		 * -Intro
		 * -Win
		 * -Lose
		 * 
		 * Player command
		 * -I'm not sure whether to send individual buttons or wait for a full command
		 * -individual buttons for now
		 * 
		 * hitbox commands
		 * -counter
		 */
	}
	
	
}