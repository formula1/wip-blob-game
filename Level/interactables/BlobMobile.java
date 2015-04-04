package interactables;

import game_aspects.BodyChain;
import init.The_Game;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.joints.DistanceJointDef;
import org.jbox2d.dynamics.joints.WheelJoint;

public class BlobMobile extends BodyChain{


	final int links = 40;
	final float distance = 40f;
	
	float skinfreq = 20;
	float skindamp = .5f;
	float skinlength = 1f;

	float squishfreq = 2;
	float squishdamp = 1;
	float squishlength = 1f;
	/*
	 * Deflate, inflate
	 * -Squish goes up and down
	 * 
	 * but the problem about setting squish up at center to keep control is that
	 * it will try and bring everything closer, even if it shouldn't be
	 * 
	 * What happens when skin is greater than squish?
	 * it crumples which is good, but it doesn't try to retain a shape
	 *	-Lines overlap
	 * 
	 */
	
	public BlobMobile(Vec2 position){
		super(position);
		
		float radius = (float)Math.PI*distance/links;
		
		float angle = 0;
		Vec2 nextpos = position.add(new Vec2(distance,0));
		Body first = The_Game.help.createFromArguments(nextpos.clone(), The_Game.help.circle(radius), BodyType.DYNAMIC);
		Body old = first;
		
		DistanceJointDef squishdjd =new DistanceJointDef();
		squishdjd.initialize(main, old, main.getPosition(), old.getPosition());
		squishdjd.length *= squishlength;
		squishdjd.dampingRatio = squishdamp;
		squishdjd.frequencyHz = squishfreq;
		The_Game.world.createJoint(squishdjd);
		
		DistanceJointDef skindjd =new DistanceJointDef();
		skindjd.dampingRatio = skindamp;
		skindjd.collideConnected = false;
		skindjd.frequencyHz = skinfreq;

		for (int i = 1; i < links; i++) {
			angle = (float)Math.PI*2f*((float)i/links);
			nextpos = position.add(new Vec2((float)(distance*Math.cos(angle)),(float)(distance*Math.sin(angle))));
			Body b = The_Game.help.createFromArguments(nextpos.clone(), The_Game.help.circle(radius), BodyType.DYNAMIC);
			skindjd.initialize(old, b, old.getPosition(), b.getPosition());
			skindjd.length *= skinlength;
			The_Game.world.createJoint(skindjd);
			
			squishdjd.initialize(main, b, main.getPosition(), b.getPosition());
			squishdjd.length *= squishlength;
			The_Game.world.createJoint(squishdjd);
			
			old = b;
		}
		skindjd.initialize(old, first, old.getPosition(), first.getPosition());
		skindjd.length *= skinlength;
		The_Game.world.createJoint(skindjd);
	}

@Override
	protected Body getTheMain(Vec2 position) {
		// TODO Auto-generated method stub
		return The_Game.help.createFromArguments(position, The_Game.help.circle(7f), BodyType.DYNAMIC);
	}

	
}
