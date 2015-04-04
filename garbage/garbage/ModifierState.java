package garbage;


import java.util.HashMap;

public abstract class ModifierState {
	String name;
	boolean fixed;
	float angle;
	short currentFrame = 0;
	
	HashMap<Short,HitBox[]> hitboxes_of_frame;

	public void time(){
		currentFrame++;
		timeLogic();
	}
	public abstract void timeLogic();
	public abstract void hitLogic(String hitbox_name);
	public abstract void recieveLogic(String hitbox_name);

}
