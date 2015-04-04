package game.pause;

import org.jbox2d.common.Vec2;

public class Impact{
	Vec2 force;
	Vec2 point;
	
	public Impact(Vec2 force, Vec2 point){
		this.force = force;
		this.point = point;
	}
}
