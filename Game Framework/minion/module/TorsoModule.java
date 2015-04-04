package minion.module;

import init.The_Game;
import game_aspects.BodyChain;
import helpers.BodyHelper;
import helpers.JointHelper;

import minion.abstracts.MinionEntity;
import minion.abstracts.MinionModule;

import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.joints.PrismaticJoint;


public abstract class TorsoModule extends MinionModule{
	public Body chest;
	private PrismaticJoint spine;
	private float sp_len;

	public TorsoModule(float body_width, MinionEntity owner, Body hips) {
		super(owner, hips);
		chest = The_Game.help.createFromArguments(
				hips.getWorldCenter().add(new Vec2(0,body_width*2)), 
				BodyHelper.rectangle(new Vec2(body_width*1.3f,body_width*1.3f/2)),
				BodyType.DYNAMIC
		);
//		chest.setGravityScale(0);
		spine = The_Game.jhelp.prismaticJoint(main, chest);
		sp_len = spine.getUpperLimit() - spine.getLowerLimit();
		spine.setMaxMotorForce(spine.getMaxMotorForce()/8);

	}


	protected abstract boolean solveDuckAI();
	
	public boolean doAI(){
		duck(solveDuckAI());
		return true;
	}
	
	public void duck(boolean onoff){
		float desiredVel;
		float des_dist;

//    	spine.enableMotor(false);
		

    	des_dist = (onoff)?spine.getLowerLimit()+sp_len*.1f:spine.getUpperLimit()-sp_len*.1f;

	    if(owner.in_air){
			spine.enableMotor(true);
		    if(		onoff && spine.getJointTranslation() < spine.getLowerLimit()+sp_len*.1f
		    	    || 		!onoff && spine.getJointTranslation() > spine.getUpperLimit()-sp_len*.1f
		    ){
	    		spine.setMotorSpeed(0);
	    		return;
	    	}
	    	des_dist -= spine.getJointTranslation();

	    	byte multiplier = (onoff)?(byte)-1:1;
	    	desiredVel = multiplier*Math.abs(des_dist)*60;
		    spine.setMotorSpeed(desiredVel);
	    }else{
	    	spine.enableMotor(false);
	    	if(The_Game.jhelp.tweenB(spine, des_dist)) System.out.print("");//System.out.print(" everythings kosher");
//	    	else System.out.print(" need to predic something");
	    }
	}

	
}
