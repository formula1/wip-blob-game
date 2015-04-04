package minion.module;

import helpers.FixtureDefCallback;
import init.The_Game;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.contacts.ContactEdge;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import entity_managers.CollisionManager;

import assets.FixtureData;

import minion.abstracts.MinionEntity;
import minion.abstracts.MinionModule;

public abstract class CircleBodyModule extends MinionModule{

	Body feet;
	RevoluteJoint j;
	float radius;

	
	private Vec2 getContactDiff(ContactEdge c, Vec2 other){
		WorldManifold wm = new WorldManifold();
		c.contact.getWorldManifold(wm);
		return wm.points[0].sub(other);
	}

	private Vec2 getContactDiff(Contact c, Vec2 other){
		WorldManifold wm = new WorldManifold();
		c.getWorldManifold(wm);
		return wm.points[0].sub(other);
	}
	
	
	public CircleBodyModule(MinionEntity owner, float main_radius) {
		super(owner);
		// TODO Auto-generated constructor stub
		radius = 2*main_radius;
		feet = The_Game.help.createFromArguments(main.getWorldCenter(), The_Game.help.circle(radius), BodyType.DYNAMIC
			,	new FixtureDefCallback(){
			public FixtureDef fixDefCallback(FixtureDef fixture){
				fixture.friction = 1000;
				return fixture;
			}

			}
		);
		RevoluteJointDef jd = new RevoluteJointDef();
		jd.initialize(main, feet, main.getWorldCenter());
		j = (RevoluteJoint)The_Game.world.createJoint(jd);
	}

	public abstract Vec2 solveWalkAI();
	public abstract boolean solveJumpAI();
	public abstract boolean solvePushAI();

	//one revolution means we move main_radius*2*2*Math.PI meters
	//if we want to move
	
	public void walk(Vec2 direction){
		if(direction == null || direction.length() == 0){
			return;
		}
		direction.normalize();
		float velocity = 20; //goes 20 meters per second in 1 second
		float rps = velocity/(radius);
		/*
		 * if i want to go 20 meters per second
		 * And a single revolution Goes Math.PI*2*radius
		 * 
		 * how many revolutions per second would I need to go
		 * 
		 * distance/time /revolutions = # revolutions per second
		 * 
		 */
		
		float dang = (float)Math.atan2(direction.y, direction.x);
		
		//Get our contacts
		Vec2 contactAngle = new Vec2();
		int counter = 0;
		ContactEdge c = feet.getContactList();
		while(c != null){
			if(c.contact.isEnabled() && c.contact.isTouching()){
				Vec2 t = getContactDiff(c, feet.getWorldCenter());
				float anglediff = (float)(Math.atan2(t.y, t.x)-dang);
				while(anglediff > Math.PI) anglediff -= Math.PI*2;
				while(anglediff < -Math.PI) anglediff += Math.PI*2;

				if(
					!(anglediff < Math.PI/12 && anglediff > -Math.PI/12)
				&& !(anglediff > Math.PI-Math.PI/12 || anglediff < -Math.PI+Math.PI/12)
				){
					t.normalize();
					contactAngle.addLocal(t);
				}
			}
			c = c.next;
		}
		if(contactAngle.length() == 0){
			main.applyForceToCenter(direction.mul(velocity/10*owner.mass*60));
		}
		contactAngle.normalize();
		

		//We find out if we want to turn clockwise or counterclockwise
		float anglediff = (float)(Math.atan2(contactAngle.y, contactAngle.x)-dang);
		while(anglediff > Math.PI) anglediff -= Math.PI*2;
		while(anglediff < -Math.PI) anglediff += Math.PI*2;
		
		float newvel  = rps*Math.signum(anglediff);// -feet.getAngularVelocity();
		
		float cvel = feet.getAngularVelocity();
		if(	Math.signum(cvel) == Math.signum(newvel)
		&&	Math.abs(cvel) >= Math.abs(newvel)) return;
		
		feet.applyTorque(feet.getInertia()*newvel*60);
		
	}
	
	public boolean doAI() {
		walk(solveWalkAI());
		return false;
	}

	@Override
	public void die() {
		// TODO Auto-generated method stub
		
	}

}
