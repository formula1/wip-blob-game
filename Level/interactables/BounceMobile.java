package interactables;

import game_aspects.BodyChain;
import init.The_Game;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.joints.DistanceJointDef;
import org.jbox2d.dynamics.joints.WheelJoint;

public class BounceMobile extends BodyChain{


	final int links = 40;
	final float distance = 40f;
	
	public BounceMobile(Vec2 position){
		super(position);
		
		
		float angle = 0;
		Vec2 nextpos = position.add(new Vec2(distance,0));
		Body first = The_Game.help.createFromArguments(nextpos.clone(), The_Game.help.circle(2.5f), BodyType.DYNAMIC);
		Body old = first;
		
		DistanceJointDef djd =new DistanceJointDef();
		djd.initialize(main, old, main.getPosition(), old.getPosition());
		djd.dampingRatio = .05f;
		djd.frequencyHz = 10;
		The_Game.world.createJoint(djd);

		
		for (int i = 1; i < links; i++) {
			angle = (float)Math.PI*2f*((float)i/links);
			nextpos = position.add(new Vec2((float)(distance*Math.cos(angle)),(float)(distance*Math.sin(angle))));
			Body b = The_Game.help.createFromArguments(nextpos.clone(), The_Game.help.circle(2.5f), BodyType.DYNAMIC);
			WheelJoint j = The_Game.jhelp.wheelJoint(old, b);
			j.setSpringFrequencyHz(10);
			j.setSpringDampingRatio(.05f);
			old = b;
			
			djd =new DistanceJointDef();
			djd.initialize(main, old, main.getPosition(), old.getPosition());
			djd.dampingRatio = .05f;
			djd.frequencyHz = 10;
			The_Game.world.createJoint(djd);

		}
		WheelJoint j = The_Game.jhelp.wheelJoint(old, first);
		j.setSpringFrequencyHz(10);
		j.setSpringDampingRatio(.05f);
	}

@Override
	protected Body getTheMain(Vec2 position) {
		// TODO Auto-generated method stub
		return The_Game.help.createFromArguments(position, The_Game.help.circle(2.5f), BodyType.DYNAMIC);
	}

	
}
