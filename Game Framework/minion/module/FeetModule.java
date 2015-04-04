package minion.module;

import game_aspects.BodyChain;
import helpers.BodyDefCallback;
import helpers.BodyHelper;
import helpers.FixtureDefCallback;
import init.The_Game;
import intelligence.IntelInterface;

import minion.abstracts.MinionEntity;
import minion.abstracts.MinionModule;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.joints.PrismaticJoint;
import org.jbox2d.dynamics.joints.RevoluteJoint;

import abstracts.BodyData;

import entity_managers.CollisionManager;


public abstract class FeetModule extends MinionModule{


	
	private final CollisionManager feetboo;
	private Body feet;
	private RevoluteJoint feetRoto;


	
	public boolean air_attempt = false;
	public int floor = 0;
	
	public FeetModule(MinionEntity owner, float walk_speed, float jump_speed, float body_width){
		this(owner, owner.main, walk_speed, jump_speed, body_width);

	}

	public FeetModule(MinionEntity new_owner, Body hips, float walk_speed, float jump_speed, float body_width){
		super(new_owner, hips);
		Vec2 position = hips.getWorldCenter().add(new Vec2(0,-body_width));

		feetboo = new CollisionManager(1){


			@Override
			public void beginContact(Contact contact, boolean is_fix_a) {
				Fixture n_fix = (is_fix_a)?contact.m_fixtureB:contact.m_fixtureA;
				if(!n_fix.isSensor()){
					floor++;
					owner.in_air = false;
				}
			}

			@Override
			public void endContact(Contact contact, boolean is_fix_a) {
				Fixture n_fix = (is_fix_a)?contact.m_fixtureB:contact.m_fixtureA;
				if(!n_fix.isSensor() ){
					floor--;
					if(floor <= 0){
						owner.in_air = true;
						air_attempt = false;
					}
				}
			}


			@Override
			public void preSolve(Contact contact, Manifold point_manifold,
					boolean is_fix_a) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse,
					boolean is_fix_a) {
				// TODO Auto-generated method stub
				
			}
			
		};
		
		feet = The_Game.help.createFromArguments(
				position, 
				BodyHelper.circle(body_width/2), 
				BodyType.DYNAMIC,
				new FixtureDefCallback(){
					public FixtureDef fixDefCallback(FixtureDef ret){
						ret.friction = 1.0f;
						ret.restitution = 0;
						return ret;
					}
				},
				feetboo
		);
		feetRoto = The_Game.jhelp.revoluteJoint(hips, feet, new Vec2(0,-body_width/2), new Vec2());

	}


	
	protected abstract byte solveWalkAI();
	protected abstract boolean solveJumpAI();
	
	public boolean doAI(){
		walk(solveWalkAI());
		jump(solveJumpAI());
		return true;
	}
	public void jump(boolean onoff){
		if(onoff && !owner.in_air && !air_attempt){
			main.applyLinearImpulse(new Vec2(0,owner.mass*10), main.getWorldCenter(), true);
			air_attempt = true;
		}
	}
	
	public void walk(byte direction){
		if(owner.in_air){
			Vec2 cur_vel = main.getLinearVelocity();
			float desiredVel = direction*5;
			float velChange = desiredVel - cur_vel.x;
			
			main.applyLinearImpulse(new Vec2(velChange*owner.mass, 0), main.getWorldCenter(), true);
		}else if(
			(	
				feet.getAngularVelocity() < 0 && direction < 0
			|| 	feet.getAngularVelocity() > 0 && direction > 0
			)
		&&	Math.abs(feet.getAngularVelocity()) > 10
		) 	return;
		else{
			float cur_vel = feet.getAngularVelocity();
		    float desiredVel = -direction*100;
		    float velChange = desiredVel - cur_vel;

			feet.applyAngularImpulse(velChange*feet.getInertia());
		}
	}
	
	
}
