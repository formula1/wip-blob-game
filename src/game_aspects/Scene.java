package game_aspects;

import controller.PlayerListener;

/*
 * Need three Scenes
 * 1) Play a new Game or Quit
 * 2) The Game Scene
 * 3) Player X One, Player Y Lost
 * 
 * 
 * 
 */
public class Scene implements PlayerListener{
	int selected = 0;
	String[] options = {"Begin","Quit"};
	int direction = 0;

	public Scene(){
		
	}

	@Override
	public void playerEvent(int player_number, String input, Float value) {
		if(input == "left" && value > .50f) direction--;
		if(input == "right" && value > .50f) direction++;
		if(input == "left" && value < .50f) direction++;
		if(input == "right" && value < .50f) direction--;
	}
	
	
	public void time(){
		selected = (selected+options.length + direction)%options.length;
		
	}
}
