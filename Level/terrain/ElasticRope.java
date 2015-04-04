package terrain;

import init.The_Game;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.WheelJoint;

import game_aspects.BodyChain;

public class ElasticRope extends BodyChain{

	final int links = 20;
	
	public ElasticRope(Vec2 position){
		super(position);
		Vec2 nextpos = position;
		Body old = main;
		for (int i = 0; i < links; i++) {
			nextpos = nextpos.add(new Vec2(0,-5));
			Body b = The_Game.help.createFromArguments(nextpos, The_Game.help.circle(2.5f), BodyType.DYNAMIC);
			WheelJoint j = The_Game.jhelp.wheelJoint(old, b);
			j.setSpringFrequencyHz(6);
			j.setSpringDampingRatio(1);
			old = b;
		}
	}

@Override
	protected Body getTheMain(Vec2 position) {
		// TODO Auto-generated method stub
		return The_Game.help.createFromArguments(position, The_Game.help.circle(2.5f), BodyType.STATIC);
	}

}
