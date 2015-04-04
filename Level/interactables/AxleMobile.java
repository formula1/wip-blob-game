package interactables;

import game_aspects.BodyChain;
import init.The_Game;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.joints.DistanceJointDef;
import org.jbox2d.dynamics.joints.WeldJoint;
import org.jbox2d.dynamics.joints.WeldJointDef;
import org.jbox2d.dynamics.joints.WheelJoint;

public class AxleMobile extends BodyChain{


	final int links = 10;
	final float distance = 10f;
	
	public AxleMobile(Vec2 position){
		super(position);
		float radius = (float)Math.PI*2f*distance/links;
		radius *= .5f;
		radius -= radius*.3f;
		

		Vec2 nextpos = position.add(new Vec2((float)(distance*Math.cos(0)),(float)(distance*Math.sin(0))));
		Body first = The_Game.help.createFromArguments(nextpos.clone(), The_Game.help.circle(radius), BodyType.DYNAMIC);
		
		WeldJointDef djd =new WeldJointDef();
		djd.initialize(main, first, main.getPosition());
		The_Game.world.createJoint(djd);


		Body old = first;

		
		for (int i = 1; i < links; i++) {
			float angle = (float)Math.PI*2f*((float)i/links);
			nextpos = position.add(new Vec2((float)(distance*Math.cos(angle)),(float)(distance*Math.sin(angle))));
			Body b = The_Game.help.createFromArguments(nextpos.clone(), The_Game.help.circle(radius), BodyType.DYNAMIC);
			
			djd =new WeldJointDef();
			djd.initialize(main, b, main.getPosition());
			The_Game.world.createJoint(djd);
			
//			DistanceJointDef dj = new DistanceJointDef();
//			dj.initialize(old, b, old.getWorldCenter(), b.getWorldCenter());
//			dj.collideConnected = false;
//			dj.dampingRatio = 1f;
//			dj.frequencyHz = 6;
//			The_Game.world.createJoint(dj);

			old = b;

		}
//		DistanceJointDef dj = new DistanceJointDef();
//		dj.initialize(old, first, old.getWorldCenter(), first.getWorldCenter());
//		dj.collideConnected = false;
//		dj.dampingRatio = 1f;
//		dj.frequencyHz = 6;
//		The_Game.world.createJoint(dj);

		
	}

@Override
	protected Body getTheMain(Vec2 position) {
		// TODO Auto-generated method stub
		return The_Game.help.createFromArguments(position, The_Game.help.circle(2.5f), BodyType.DYNAMIC);
	}

	
}
