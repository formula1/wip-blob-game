package game.ghostsi;

import helpers.BodyHelper;
import helpers.FixtureDefCallback;
import init.The_Game;

import minion.abstracts.MinionEntity;
import minion.abstracts.MinionModule;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.joints.PrismaticJoint;
import org.jbox2d.dynamics.joints.RevoluteJoint;

import entity_managers.CollisionManager;

import assets.FixtureData;

public abstract class GhostFistModule extends MinionModule{

	//this is needed to find out whether or not punching causes stun to self
	//also any effects that should be applied to the person taking the punch
//	CollisionManager punchboo; 
	//I'll get into effects Later
		
	public boolean ready = false;
	private float direction = 0;
	private byte type = -1;

	RevoluteJoint rotates;
	Body robody;
	PrismaticJoint pushes;
	Body pubody;
	
	
	float pushlim_len;
	
	public GhostFistModule(MinionEntity owner, Body connection, float chestwidth) {
		super(owner, connection);
		Vec2 position = main.getWorldCenter().add(new Vec2(0,0));
		robody = The_Game.help.createFromArguments(
				position, 
				BodyHelper.circle(chestwidth), 
				BodyType.KINEMATIC
		);
		rotates = The_Game.jhelp.revoluteJoint(main, robody);
		
		pubody = The_Game.help.createFromArguments(
				position.add(new Vec2(0,chestwidth*2)), 
				BodyHelper.circle(chestwidth/4), 
				BodyType.KINEMATIC
		);
		
		pushes = The_Game.jhelp.prismaticJoint(robody, pubody);
		pushlim_len = pushes.getUpperLimit()-pushes.getLowerLimit();
//		pushes.setMaxMotorForce(pubody.getMass()*pushlim_len*60);
		rotates.setMaxMotorTorque((float)Math.PI*2*(robody.getInertia()+pubody.getMass()*pushlim_len)*60);
		rotates.setLimits((float)-Math.PI, (float)Math.PI);
//		rotates.enableMotor(true);

	}

	
	public abstract float solvePunchDirection();
	public abstract byte solvePunchType();
	@Override
	public boolean doAI() {
		/*
		 * What punches need
		 * -Aiming Step
		 * -Charge Step-This will be oriented around the idea the fist is like a spring
		 * 	-Since I might make this mostly based off weapons, I can make 1 revolute joint
		 * 	-when changing direction, instead of changing the revolute joint, transform angle and change spring
		 * -Output Step-This is when it actually goes out, using the charge as the initial force
		 * -Recovery Step-After it reaches peak, 
		 * 
		 * 
		 * 
		 */
		if(type == -1){
			this.type = solvePunchType();
			this.direction = solvePunchDirection();
		}
		punch(direction, type);
		return ready;
	}
	
	private void punch(float direction, byte type){
		switch(type){
		case -1: recover(); break;
		case 0: poke(direction); break;
		case 1: swipe(direction); break;
		case 2: big(direction); break;
		}
	}
	
	private void recover(){
		The_Game.jhelp.rtweenB(rotates,0);
		The_Game.jhelp.tweenB(pushes, pushes.getLowerLimit()+pushlim_len*.2f);
	}
	
	private void poke(float direction){

		float bang = robody.getAngle();
		
		if(!ready){
			pubody.setTransform(robody.getWorldCenter(), pubody.getAngle());
			The_Game.jhelp.rtweenB(rotates,direction);
//			The_Game.jhelp.tweenB(pushes, pushes.getLowerLimit()+pushlim_len*.1f);
			if(	((	bang >= direction-Math.PI/12
				&&	bang <= direction+Math.PI/12)
				)
			&& rotates.getJointSpeed() <= Math.PI/24
			){
				ready = true;
				System.out.println("des:"+direction+", cur: "+bang);
			}
		}else{
			robody.setAngularVelocity(0);
			The_Game.jhelp.tweenB(pushes, pushes.getUpperLimit());
			if(	pushes.getJointTranslation() >= pushes.getUpperLimit()-pushlim_len*.1f){
				type = -1;
				ready = false;
			}
		}

	}
	private void swipe(float direction){
		byte i = (byte)((Math.cos(direction) > 0)?1:0);
		if(!ready){
			The_Game.jhelp.tweenB(pushes, pushes.getUpperLimit());
			The_Game.jhelp.rtweenB(rotates, direction-90);
			if(	pushes.getJointTranslation() >= pushes.getUpperLimit()-pushlim_len*.1f
			&&	(	rotates.getJointAngle() >= direction-Math.PI/12
				||	rotates.getJointAngle() <= direction+Math.PI/12
				)
			) ready = true;
				
		}else{
			The_Game.jhelp.rtweenB(rotates, direction+90);
			if(	(	rotates.getJointAngle() >= direction-Math.PI/12
				||	rotates.getJointAngle() <= direction+Math.PI/12
				)
			){
				ready = false;
				type = -1;
				rotates.setLimits(-2*(float)Math.PI, 2*(float)Math.PI);


			}
		}
		
		
	}
	private void big(float direction){
		byte i = (byte)((Math.cos(direction) > 0)?1:0);
		if(!ready){
			float opp = direction-(1-2*i)*(float)Math.PI;
			The_Game.jhelp.rtweenB(rotates, opp);
			The_Game.jhelp.tweenB(pushes, pushes.getLowerLimit());
			if(	pushes.getJointTranslation() <= pushes.getLowerLimit()+pushlim_len*.1f
			&&	(	rotates.getJointAngle() >= opp-Math.PI/12
				||	rotates.getJointAngle() <= opp+Math.PI/12
				)
			) ready = true;
				
		}else{
			The_Game.jhelp.rtweenB(rotates, direction);
			The_Game.jhelp.tweenB(pushes, pushes.getUpperLimit());
			if(	pushes.getJointTranslation() >= pushes.getUpperLimit()-pushlim_len*.1f
			&&	(	rotates.getJointAngle() >= direction-Math.PI/12
				||	rotates.getJointAngle() <= direction+Math.PI/12
				)
			){
				ready = false;
				type = -1;
				rotates.setLimits(-2*(float)Math.PI, 2*(float)Math.PI);

			}
		}
	}

	@Override
	public void die() {
		// TODO Auto-generated method stub
		
	}

}
