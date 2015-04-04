package terrain;

import helpers.BodyDefCallback;
import init.The_Game;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.WheelJointDef;

import game_aspects.BodyChain;

public class FixedRope extends BodyChain{

	final int links = 20;
	
	public FixedRope(Vec2 position) {
		super(position);
		Vec2 nextpos = position;
		Body old = main;
		for (int i = 0; i < links; i++) {
			nextpos = nextpos.add(new Vec2(0,-5));
			Body b = The_Game.help.createFromArguments(nextpos, The_Game.help.circle(2.5f), BodyType.DYNAMIC);
			RevoluteJoint j = The_Game.jhelp.revoluteJoint(old, b);
			j.setMaxMotorTorque(1);
			j.enableMotor(true);
			old = b;
		}
	}

	@Override
	protected Body getTheMain(Vec2 position) {
		// TODO Auto-generated method stub
		return The_Game.help.createFromArguments(position, The_Game.help.circle(2.5f), BodyType.STATIC);
	}

}
