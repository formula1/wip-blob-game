package interactables;

import game_aspects.BodyChain;
import init.The_Game;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.joints.DistanceJointDef;
import org.jbox2d.dynamics.joints.WheelJoint;
import org.jbox2d.particle.ParticleGroup;
import org.jbox2d.particle.ParticleGroupDef;
import org.jbox2d.particle.ParticleType;

public class BlobWheelMobile extends BodyChain{


	final int links = 40;
	final float distance = 40f;
	
	public BlobWheelMobile(Vec2 position){
		super(position);
		
		

	}

@Override
	protected Body getTheMain(Vec2 position) {
		// TODO Auto-generated method stub
		return The_Game.help.createFromArguments(position, The_Game.help.circle(2.5f), BodyType.DYNAMIC);
	}

	
}
