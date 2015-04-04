package interactables;

import helpers.BodyHelper;
import init.The_Game;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.joints.DistanceJoint;
import org.jbox2d.dynamics.joints.DistanceJointDef;

import game_aspects.BodyChain;

public class LineJointChanges extends BodyChain{

	DistanceJoint[] js;
	float radius = 5;
	
	public LineJointChanges(Vec2 position) {
		super(position);
		js = new DistanceJoint[5];
		float distance = 10;

		
		DistanceJointDef djd = new DistanceJointDef();
		djd.frequencyHz = 5;
		djd.dampingRatio = .5f;
		
		for(int i=0;i<js.length;i++){
			float angle = (float)Math.PI*2f*((float)i/js.length);
			Vec2 nextpos = new Vec2((float)(Math.cos(angle)),(float)(Math.sin(angle)));
			Body b = The_Game.help.createFromArguments(position.add(nextpos.mul(distance)), The_Game.help.circle(radius), BodyType.DYNAMIC);
			
			djd.initialize(main, b, main.getWorldCenter(), b.getWorldCenter());
			js[i] = (DistanceJoint)The_Game.world.createJoint(djd);
			
		}
	
	}
	
	public void time(){
		for(int i=0;i<js.length;i++)
			js[i].setLength(js[i].getLength()*1.01f);
	}

	@Override
	protected Body getTheMain(Vec2 position) {
		// TODO Auto-generated method stub
		return The_Game.help.createFromArguments(position, BodyHelper.circle(5), BodyType.DYNAMIC);
	}

}
