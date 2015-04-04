package interactables;

import game_aspects.BodyChain;
import init.The_Game;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.joints.ConstantVolumeJointDef;
import org.jbox2d.dynamics.joints.DistanceJointDef;
import org.jbox2d.dynamics.joints.LineJoint;
import org.jbox2d.dynamics.joints.LineJointDef;
import org.jbox2d.dynamics.joints.PrismaticJoint;
import org.jbox2d.dynamics.joints.PrismaticJointDef;


public class LineJointMobile extends BodyChain{

	
	final int links = 40;
	final float distance = 40f;
	final float mainradius = (float)Math.PI;
	PrismaticJoint[] js;
	float jt = 0;

	public LineJointMobile(Vec2 position){
		super(position);
		
		main.setFixedRotation(true);
		
		float radius = (float)Math.PI*distance/links;
		radius *= .7f;
		
		float squishfreq = 10;
		float squishdamp = 1f;
		ConstantVolumeJointDef cvjd = new ConstantVolumeJointDef();

		js = new PrismaticJoint[40];
		
		float angle = 0;
		Vec2 nextpos = new Vec2(1,0);
		
		

		
		for (int i = 0; i < links; i++) {
			angle = (float)Math.PI*2f*((float)i/links);
			nextpos = new Vec2((float)(Math.cos(angle)),(float)(Math.sin(angle)));
			Body b = The_Game.help.createFromArguments(position.add(nextpos.mul(distance)), The_Game.help.circle(radius), BodyType.DYNAMIC);

			cvjd.addBody(b);

			Body r = The_Game.help.createFromArguments(main.getWorldCenter(), The_Game.help.circle(radius), BodyType.DYNAMIC);
			
			The_Game.jhelp.revoluteJoint(main, r);
			
			
			PrismaticJointDef ljd = new PrismaticJointDef();
			Vec2 axis = b.getWorldCenter().sub(main.getWorldCenter());
			axis.normalize();
			ljd.initialize(b, r, r.getWorldCenter(), axis);
			ljd.enableMotor = true;
			ljd.maxMotorForce = (b.getMass()+main.getMass())*60*40;
			ljd.motorSpeed = 0;
			js[i] = (PrismaticJoint)The_Game.world.createJoint(ljd);
			jt = js[i].getJointTranslation();
			
		}
		System.out.println("jt: "+jt);
	    cvjd.frequencyHz = 10.0f;
	    cvjd.dampingRatio = 1.0f;
	    cvjd.collideConnected = false;
	    The_Game.world.createJoint(cvjd);

	}

	public void time(){
		for(PrismaticJoint l : js){
			
			System.out.println(jt-l.getJointTranslation());
			if(l.getJointTranslation() != jt) l.setMotorSpeed(l.getJointTranslation());
		}
	}
	
@Override
	protected Body getTheMain(Vec2 position) {
		// TODO Auto-generated method stub
		return The_Game.help.createFromArguments(position, The_Game.help.circle(mainradius), BodyType.DYNAMIC);
	}
}
