package interactables;

import game_aspects.BodyChain;
import init.The_Game;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.joints.DistanceJointDef;
import org.jbox2d.dynamics.joints.WheelJoint;

public class BlobRadialMobile extends BodyChain{


	final int links = 40;
	final float distance = 40f;
	final float mainradius = (float)Math.PI;
	public BlobRadialMobile(Vec2 position){
		super(position);
		
		float radius = (float)Math.PI*distance/links;
		radius *= .7f;
		
		float squishfreq = 10;
		float squishdamp = 1f;
		
		float skinfreq = 5;
		float skindamp = 1f;

		
		float angle = 0;
		Vec2 nextpos = new Vec2(1,0);
		Body first = The_Game.help.createFromArguments(position.add(nextpos.mul(distance)), The_Game.help.circle(radius), BodyType.DYNAMIC);
		Body old = first;
		
		
		
		DistanceJointDef djd =new DistanceJointDef();
		djd.initialize(main, old, main.getPosition().add(nextpos.mul(mainradius)), old.getPosition().sub(nextpos.mul(radius)));
		djd.dampingRatio = squishdamp;
		djd.frequencyHz = squishfreq;
		The_Game.world.createJoint(djd);

		
		for (int i = 1; i < links; i++) {
			angle = (float)Math.PI*2f*((float)i/links);
			nextpos = new Vec2((float)(Math.cos(angle)),(float)(Math.sin(angle)));
			Body b = The_Game.help.createFromArguments(position.add(nextpos.mul(distance)), The_Game.help.circle(radius), BodyType.DYNAMIC);

			djd =new DistanceJointDef();
			djd.initialize(main, b, main.getPosition().add(nextpos.mul(mainradius)), b.getPosition().sub(nextpos.mul(radius)));
			djd.dampingRatio = squishdamp;
			djd.frequencyHz = squishfreq;
			The_Game.world.createJoint(djd);

			
			Vec2 diff = old.getWorldCenter().sub(b.getWorldCenter());
			diff.normalize();
			djd =new DistanceJointDef();
			djd.initialize(old, b, old.getPosition().add(diff.mul(radius)), b.getPosition().sub(diff.mul(radius)));
			djd.dampingRatio = skindamp;
			djd.frequencyHz = skinfreq;
			The_Game.world.createJoint(djd);

			old = b;
			

		}
		Vec2 diff = old.getWorldCenter().sub(first.getWorldCenter());
		diff.normalize();
		djd =new DistanceJointDef();
		djd.initialize(old, first, old.getPosition().add(diff.mul(radius)), first.getPosition().sub(diff.mul(radius)));
		djd.dampingRatio = skindamp;
		djd.frequencyHz = skinfreq;
		The_Game.world.createJoint(djd);
	}

@Override
	protected Body getTheMain(Vec2 position) {
		// TODO Auto-generated method stub
		return The_Game.help.createFromArguments(position, The_Game.help.circle(mainradius), BodyType.DYNAMIC);
	}

	
}
