package minion.module;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import helpers.BodyDefCallback;
import helpers.FixtureDefCallback;
import init.The_Game;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.contacts.ContactEdge;
import org.jbox2d.dynamics.joints.ConstantVolumeJoint;
import org.jbox2d.dynamics.joints.ConstantVolumeJointDef;
import org.jbox2d.dynamics.joints.DistanceJoint;
import org.jbox2d.dynamics.joints.DistanceJointDef;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.WeldJoint;
import org.jbox2d.dynamics.joints.WeldJointDef;
import org.jbox2d.dynamics.joints.WheelJointDef;

import assets.FixtureData;

import entity_managers.CollisionManager;

import minion.abstracts.MinionEntity;
import minion.abstracts.MinionModule;
import minion.clever.Suction;

public abstract class BlobBodyModule extends MinionModule{

	
	/*
	 * What happens is
	 * -Impact happens
	 * 	-if we already have a 
	 * 
	 * 
	 */
	
	ConstantVolumeJoint cvj;
	float distance = 20f;
	Float suction;
	TreeSet<Body> bodies = new TreeSet<Body>(new Comparator<Body>(){
		public int compare(Body o1, Body o2) {
			return (int) Math.signum(o1.getAngle() - o2.getAngle());
		}
	});
	TreeMap<Body, Suction> suck = new TreeMap<Body, Suction>(new Comparator<Body>(){
		public int compare(Body o1, Body o2) {
			return o1.hashCode() - o2.hashCode();
		}
	});
	
	TreeMap<Body, DistanceJoint> dists = new TreeMap<Body, DistanceJoint>(new Comparator<Body>(){
		public int compare(Body o1, Body o2) {
			return o1.hashCode() - o2.hashCode();
		}
	});

	
	
	CollisionManager stickys = 	new CollisionManager(1){
			public void preSolve(Contact contact, Manifold point_manifold, boolean is_fix_a) {}

			public void beginContact(Contact contact, boolean is_fix_a) {
				Body gotit = null;
				Body other = null;
				if(is_fix_a){
//					if(contact.getFixtureB().getBody().getType() == BodyType.DYNAMIC) return;
					gotit = contact.getFixtureA().getBody();
					other = contact.getFixtureB().getBody();
				}else{
//					if(contact.getFixtureA().getBody().getType() == BodyType.DYNAMIC) return;
					gotit = contact.getFixtureB().getBody();
					other = contact.getFixtureA().getBody();
				}
				if(!suck.containsKey(gotit)){
					System.out.println("made 1");
					suck.put(gotit, new Suction(contact, other));
				}
			}
			public void endContact(Contact contact, boolean is_fix_a) {}
			public void postSolve(Contact contact, ContactImpulse impulse, boolean is_fix_a) {}
		};


	//Create The Chain
	//Add Fluff
	public BlobBodyModule(MinionEntity owner, float main_radius) {
		super(owner);
		
		float squishfreq = 3f; //jiggle (neded to stop forces)
		float squishdamp = 1f; //needed to stop jiggle?
		
		
		ConstantVolumeJointDef cvjd = new ConstantVolumeJointDef();
	    cvjd.frequencyHz = 20.0f;
	    cvjd.dampingRatio = 1.0f;
	    cvjd.collideConnected = false;

		
		int nBodies = 24;
		
		float bodyRadius = (float)Math.PI*distance/nBodies;
//		bodyRadius *= .7f;

				
		Vec2 nextpos = new Vec2(1,0);
		
		DistanceJointDef djd =new DistanceJointDef();
		djd =new DistanceJointDef();
		djd.dampingRatio = squishdamp; //how much resistance
		djd.frequencyHz = squishfreq; //rate of change
		djd.collideConnected = true;


				
		for (int i = 0; i < nBodies; i++) {
			final float angle = (float)Math.PI*2f*((float)i/nBodies);
			final Integer index = i;
			nextpos = new Vec2((float)Math.cos(angle),(float)Math.sin(angle));
			System.out.println("nextpos:" + nextpos);
			Body b = The_Game.help.createFromArguments(main.getWorldCenter().add(nextpos.mul(distance)), The_Game.help.circle(bodyRadius), BodyType.DYNAMIC,
				new BodyDefCallback(){
					public BodyDef bodyDefCallback(BodyDef bd){
						bd.angle = angle+(float)Math.PI/2;
						bd.fixedRotation = true;
						bd.userData = (Integer)index;
						return bd;
					}
				},
				new FixtureDefCallback(){
					public FixtureDef fixDefCallback(FixtureDef fixture){
						fixture.friction = 1;
						fixture.userData = new FixtureData(stickys);
						return fixture;
					}
				}
			);
			bodies.add(b);

			
			djd.initialize(main, b, main.getWorldCenter().add(nextpos.mul(main_radius)), b.getWorldCenter().sub(nextpos.mul(bodyRadius)));
			dists.put(b, (DistanceJoint)The_Game.world.createJoint(djd));
			generaldist = dists.get(b).getLength();
			
			
			cvjd.addBody(b);


		}
		
		cvj = (ConstantVolumeJoint)The_Game.world.createJoint(cvjd);
//		cvj.inflate(.9f);
		currentinflate = .9f;
	}
	float generaldist = 0;
	
	float currentinflate;
	
	
	public abstract Vec2 solveWalkAI();
	public abstract boolean solveJumpAI();
	public abstract boolean solvePushAI();

	int time_in_push = 0;
	Vec2 jumpang = new Vec2();
	
	
	public boolean jump(boolean solve){
		if(suck.size() == 0){time_in_push = 0; jumpang = new Vec2(); return false; }
		if(solve){
			time_in_push++;
			jumpang = jumpang.add(solveWalkAI());
			jumpang.normalize();

			return true;
//			main.applyForceToCenter(jumpang.mul(owner.getMass()*(-1f/(1/2f+time_in_push/2f)-2)*60*suck.size()));
		}else if(time_in_push > 0){
			Vec2 netAngle = new Vec2();
			
			HashMap<Body, Suction> flagged = new HashMap<Body,Suction>();

			float velocity = 100;
			
			float power = 0;
			Iterator<Entry<Body, Suction>> i = suck.entrySet().iterator();
			while(i.hasNext()){
				Map.Entry<Body, Suction> b = i.next();
				Vec2 diff = b.getValue().getContactPoint().sub(main.getWorldCenter());
				float dist = diff.normalize(); //distance is already factored in due to line joints
				
				netAngle = netAngle.add(diff);
				
				Vec2 cang = b.getValue().getContactPoint().sub(b.getKey().getWorldCenter());
				cang.normalize();
				
				Vec2 temp = jumpang.sub(diff);
				temp.normalize();
				if(cang.sub(temp).length() < .5){
					flagged.put(b.getKey(), b.getValue());
					suck.get(b.getKey()).breakIt();
					i.remove();
					continue;
				}

				
				diff = diff.sub(jumpang);
				b.getValue().other.applyForceToCenter(diff
						.mul(velocity) //velocity we want to set it at
						.mul(owner.getMass()/suck.size()/(1/60f)) //compensating for mass to set velocity
//						.mul(time_in_push) //as time increases push increases
//						.mul(40/dist)); //as the distance between the main and suction increases, power decreases
				);
				suck.get(b.getKey()).breakIt();
				i.remove();

			}
			float p = (netAngle.normalize());
			
			if(flagged.size() > 0) System.out.println("flagged amount: "+flagged.size());
			
			for(Map.Entry<Body, Suction> b : flagged.entrySet()){
				/*
				 * check the net offset that is perpendicular to the netAngle but towards the main
				 * 
				 * if the net angle is towards the ground, and its grabbing a wall
				 * We want to point the suction away from the wall and towards the main
				 * 
				 */
				
				
				Vec2 diff = b.getValue().getContactPoint().sub(main.getWorldCenter());
				diff = diff.sub(netAngle);
				float dist = diff.normalize(); //distance is already factored in due to line joints
				
				b.getKey().applyLinearImpulse(diff.mul(velocity*b.getKey().getMass()), b.getKey().getWorldCenter(), true);
				
				b.getValue().other.applyForceToCenter(diff
						.mul(-velocity) //velocity we want to set it at
						.mul(b.getKey().getMass()/(1/60f)) //compensating for mass to set velocity
//						.mul(time_in_push) //as time increases push increases
//						.mul(40/dist)); //as the distance between the main and suction increases, power decreases
				);
				
			}
			
			Vec2 etc = jumpang.sub(netAngle).mul(.5f);
			etc.normalize();
			
			main.applyLinearImpulse(etc.mul(velocity*main.getMass()), main.getWorldCenter(), true);
			for(Body b : bodies){
				if(suck.containsKey(b) || flagged.containsKey(b)){
					continue;
				}
				b.applyLinearImpulse(etc.mul(velocity*b.getMass()), b.getWorldCenter(), true);
			}

/*
			float p = (netAngle.normalize());
			Vec2 etc = jumpang.sub(netAngle);

			for(Body b : bodies){
//				if(suck.containsKey(b)) continue;//b.applyForceToCenter(new Vec2(2*(suck.size())/(float)bodies.size()*owner.getMass()*-direction*60,0));
//				else{
					Vec2 bodyang = b.getWorldCenter().sub(main.getWorldCenter());
					bodyang.normalize();
					bodyang = bodyang.add(etc);
					
					b.applyForceToCenter(bodyang
						.mul(1)
						.mul(velocity) //velocity we want to set it at
						.mul(owner.getMass()) //compensating for mass to set velocity
//						.mul(time_in_push) //as time increases push increases
						.mul(40/power) //as the distance between the main and suction increases, power decreases
						.mul(suck.size()/((float)bodies.size()-suck.size()))
//						.mul(suck.size()/((float)bodies.size()-suck.size()))
					);
//				}
			}
*/
			
			/*
			 * as distance increases
			 * power decreases
			 */
/*			main.applyForceToCenter(etc
					.mul(100) //velocity we want to set it at
					.mul(owner.getMass()) //compensating for mass to set velocity
					.mul(time_in_push) //as time increases push increases
					.mul(40/power) //as the distance between the main and suction increases, power decreases
					.mul(suck.size())
			);
			*/
			time_in_push++;
			return true;
			
		}
		return false;
	}
	/*
	 * While Held
	 * Pressure = -1/time_held +
	 * 
	 * 
	 * 
	 */
	
	private void suction(){
		Iterator<Entry<Body, Suction>> i = suck.entrySet().iterator();
		float power = owner.mass*(1f/(-1f+suck.size()*4));
		while(i.hasNext()){
			Map.Entry<Body, Suction> s = i.next();
			Vec2 diff = s.getValue().getContactPoint().sub(main.getWorldCenter());
			diff.normalize();
			Vec2 walk = solveWalkAI();
			walk.normalize();
			diff = diff.add(walk);
			
			if(!s.getValue().time(owner.mass*diff.length()/4)){
				i.remove(); 
			}
		}
	}
	
	public void breakSuck(Body b){
		suck.get(b).breakIt();
		suck.remove(b);
	}
	
	public void walk(Vec2 direction){
		direction.normalize();
		if(direction == null) return;
		direction = direction.mul(owner.mass*60);
		for(Body b : bodies)
			if(suck.containsKey(b)) b.applyForceToCenter(direction.mul(-2));//b.applyForceToCenter(new Vec2(2*(suck.size())/(float)bodies.size()*owner.getMass()*-direction*60,0));
			else b.applyForceToCenter(direction.mul(2*suck.size()/((float)bodies.size()-suck.size())));
	}
	
	
	@Override
	public boolean doAI() {
		suction();
		if(!jump(solveJumpAI())) walk(solveWalkAI());
		return true;
	}

	@Override
	public void die() {
		// TODO Auto-generated method stub
		
	}

}
