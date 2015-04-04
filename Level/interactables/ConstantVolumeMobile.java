package interactables;

import game_aspects.BodyChain;
import helpers.FixtureDefCallback;
import init.The_Game;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.joints.ConstantVolumeJointDef;
import org.jbox2d.dynamics.joints.WheelJoint;

public class ConstantVolumeMobile extends BodyChain{
	
	public ConstantVolumeMobile(Vec2 pos){
		super(pos);
		ConstantVolumeJointDef cvjd = new ConstantVolumeJointDef();
				
		int count = 24;
		float distance = 10;
		float bodyRadius = (float)Math.PI*distance/count;
		bodyRadius *= .5f;
		
		for (int i = 0; i < count; ++i) {
			float angle = (float)Math.PI*2f*((float)i/count);
			Vec2 nextpos = pos.add(new Vec2(
					(float)(distance*Math.cos(angle)),
					(float)(distance*Math.sin(angle)))
			);
			Body b = The_Game.help.createFromArguments(nextpos.clone(), The_Game.help.circle(bodyRadius), BodyType.DYNAMIC);
			cvjd.addBody(b);
	    }

	    cvjd.frequencyHz = 10.0f;
	    cvjd.dampingRatio = 1.0f;
	    cvjd.collideConnected = false;
	    The_Game.world.createJoint(cvjd);

	}

	@Override
	protected Body getTheMain(Vec2 position) {
		// TODO Auto-generated method stub
		return The_Game.help.createFromArguments(position, The_Game.help.circle(10), BodyType.DYNAMIC);

	}

}
