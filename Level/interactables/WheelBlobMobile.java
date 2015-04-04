package interactables;

import game_aspects.BodyChain;
import init.The_Game;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.joints.ConstantVolumeJointDef;
import org.jbox2d.dynamics.joints.DistanceJointDef;
import org.jbox2d.dynamics.joints.WheelJointDef;


public class WheelBlobMobile extends BodyChain{

	
	final int links = 40;
	final float distance = 40f;
	final float mainradius = (float)Math.PI;
	public WheelBlobMobile(Vec2 position){
		super(position);
		
		float radius = (float)Math.PI*distance/links;
		radius *= .7f;
		
		float squishfreq = 10;
		float squishdamp = 1f;
		
		ConstantVolumeJointDef cvjd = new ConstantVolumeJointDef();

		
		float angle = 0;
		Vec2 nextpos = new Vec2(1,0);
		
		

		
		for (int i = 0; i < links; i++) {
			angle = (float)Math.PI*2f*((float)i/links);
			nextpos = new Vec2((float)(Math.cos(angle)),(float)(Math.sin(angle)));
			Body b = The_Game.help.createFromArguments(position.add(nextpos.mul(distance)), The_Game.help.circle(radius), BodyType.DYNAMIC);

			cvjd.addBody(b);

			
			WheelJointDef ljd = new WheelJointDef();
			Vec2 axis = b.getWorldCenter().sub(main.getWorldCenter());
			axis.normalize();
			ljd.initialize(b, main, main.getWorldCenter(), axis);
			ljd.dampingRatio = 1.0f;
			ljd.frequencyHz = 5;
			The_Game.world.createJoint(ljd);

			

		}
		
	    cvjd.frequencyHz = 10.0f;
	    cvjd.dampingRatio = 1.0f;
	    cvjd.collideConnected = false;
	    The_Game.world.createJoint(cvjd);

	}

@Override
	protected Body getTheMain(Vec2 position) {
		// TODO Auto-generated method stub
		return The_Game.help.createFromArguments(position, The_Game.help.circle(mainradius), BodyType.DYNAMIC);
	}
}
