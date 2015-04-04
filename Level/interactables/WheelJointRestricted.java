package interactables;

import init.The_Game;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.joints.WheelJointDef;

public class WheelJointRestricted {

	public WheelJointRestricted(Vec2 pos){
		Body a = The_Game.help.createFromArguments(pos.add(new Vec2(0,10f)), The_Game.help.circle(10), BodyType.DYNAMIC);
		Body b = The_Game.help.createFromArguments(pos.add(new Vec2(0,-10f)), The_Game.help.circle(10), BodyType.DYNAMIC);
		WheelJointDef wjd = new WheelJointDef();
		wjd.initialize(a, b, pos, new Vec2(0,1));
		wjd.enableMotor = true;
		
	}
	
}
