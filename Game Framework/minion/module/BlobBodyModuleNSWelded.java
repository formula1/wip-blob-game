package minion.module;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import helpers.BodyDefCallback;
import helpers.FixtureDefCallback;
import helpers.MathHelper;
import init.The_Game;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.contacts.ContactEdge;
import org.jbox2d.dynamics.joints.ConstantVolumeJoint;
import org.jbox2d.dynamics.joints.ConstantVolumeJointDef;
import org.jbox2d.dynamics.joints.DistanceJoint;
import org.jbox2d.dynamics.joints.DistanceJointDef;
import org.jbox2d.dynamics.joints.PrismaticJoint;
import org.jbox2d.dynamics.joints.PrismaticJointDef;
import org.jbox2d.dynamics.joints.WeldJointDef;

import assets.FixtureData;

import entity_managers.CollisionManager;


import minion.abstracts.MinionEntity;
import minion.abstracts.MinionModule;
import minion.clever.Suction;

public abstract class BlobBodyModuleNSWelded extends MinionModule{

	
	/*
	 * What happens is
	 * -Impact happens
	 * 	-if we already have a 
	 * 
	 * 
	 */
	
	ConstantVolumeJoint cvj;
	float distance = 20f;
	TreeSet<Body> bodies = new TreeSet<Body>(new Comparator<Body>(){
		public int compare(Body o1, Body o2) {
			return (int) Math.signum(o1.getAngle() - o2.getAngle());
		}
	});
	
	
	TreeSet<Body> col = new TreeSet<Body>(new Comparator<Body>(){
		public int compare(Body a, Body b){
			return a.hashCode() - b.hashCode();
		}
	});
	
	ArrayList<PrismaticJoint> pjs = new ArrayList<PrismaticJoint>();
	
	CollisionManager colm = 	new CollisionManager(1){
			public void preSolve(Contact contact, Manifold point_manifold, boolean is_fix_a) {}

			public void beginContact(Contact contact, boolean is_fix_a) {
				Body gotit = null;
				Body other = null;
				if(is_fix_a){
					gotit = contact.getFixtureA().getBody();
				}else{
					gotit = contact.getFixtureB().getBody();
				}
				if(!col.contains(gotit)){
					col.add(gotit);
				}
			}
			public void endContact(Contact contact, boolean is_fix_a) {
				Body gotit = null;
				Body other = null;
				if(is_fix_a){
					gotit = contact.getFixtureA().getBody();
				}else{
					gotit = contact.getFixtureB().getBody();
				}
				ContactEdge c = gotit.getContactList();
				while(c != null){
					if(c.contact.isTouching() && c.contact.isEnabled()) return;
					c = c.next;
				}
				col.remove(gotit);
			}
			public void postSolve(Contact contact, ContactImpulse impulse, boolean is_fix_a) {}
		};


	
	//Create The Chain
	//Add Fluff
	public BlobBodyModuleNSWelded(MinionEntity owner, float main_radius) {
		super(owner);
		
		float squishfreq = 8f; //jiggle (neded to stop forces)
		float squishdamp = 1f; //needed to stop jiggle?
		
		
		ConstantVolumeJointDef cvjd = new ConstantVolumeJointDef();
	    cvjd.frequencyHz = 30.0f;
	    cvjd.dampingRatio = 1.0f;
	    cvjd.collideConnected = false;

		
		int nBodies = 24;
		
		
		float bodyRadius = (float)Math.PI*distance/nBodies;
//		bodyRadius *= .7f;

				
		Vec2 nextpos = new Vec2(1,0);
		
		PrismaticJointDef djd =new PrismaticJointDef();
		djd.collideConnected = true;

		
		final float chain_density = main.getMass()/nBodies/((float)Math.PI*bodyRadius*bodyRadius);
		
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DYNAMIC;
				
		for (int i = 0; i < nBodies; i++) {
			final float angle = (float)Math.PI*2f*((float)i/nBodies);
			final Integer index = i;
			nextpos = new Vec2((float)Math.cos(angle),(float)Math.sin(angle));
			System.out.println("nextpos:" + nextpos);
			
			Body b = The_Game.help.createFromArguments(main.getWorldCenter().add(nextpos.mul(distance)), The_Game.help.circle(bodyRadius), BodyType.DYNAMIC,
				new BodyDefCallback(){
					public BodyDef bodyDefCallback(BodyDef bd){
						bd.angle = angle;
						bd.userData = (Integer)index;
						return bd;
					}
				},
				new FixtureDefCallback(){
					public FixtureDef fixDefCallback(FixtureDef fixture){
						fixture.friction = 1000;
						fixture.density = chain_density;
						fixture.userData = new FixtureData(colm);
						return fixture;
					}
				}
			);
			bodies.add(b);
			
			Body bweld = The_Game.help.createFromArguments(
					main.getWorldCenter().add(nextpos.mul(distance-bodyRadius)), 
					The_Game.help.circle(bodyRadius), 
					BodyType.DYNAMIC
			);

			Body mweld = The_Game.help.createFromArguments(
					main.getWorldCenter().add(nextpos.mul(main_radius)), 
					The_Game.help.circle(MathUtils.PI/nBodies*main_radius), 
					BodyType.DYNAMIC
			);

			
			WeldJointDef wjd = new WeldJointDef();
			wjd.frequencyHz = 7;
			wjd.dampingRatio = 1;
			wjd.initialize(b, bweld, b.getWorldCenter().add(bweld.getWorldCenter()).mul(.5f));
			The_Game.world.createJoint(wjd);
			wjd.initialize(main, mweld, main.getWorldCenter().add(mweld.getWorldCenter()).mul(.5f));
			The_Game.world.createJoint(wjd);

			
//			djd.initialize(main, b, main.getWorldCenter().add(nextpos.mul(main_radius)), b.getWorldCenter().sub(nextpos.mul(bodyRadius)));
			Vec2 midpoint = mweld.getWorldCenter().add(bweld.getWorldCenter()).mul(.5f);
			djd.bodyA = mweld;
			djd.bodyB = bweld;
			djd.localAnchorA.set(mweld.getWorldCenter().sub(midpoint));
			djd.localAnchorB.set(bweld.getWorldCenter().sub(midpoint));
			djd.localAxisA.set(bweld.getWorldCenter().sub(mweld.getPosition()));
			djd.localAxisA.normalize();
//			djd.enableMotor = true;
			djd.maxMotorForce = (mweld.getMass() + bweld.getMass() + main.getMass() + b.getMass())
					*			(60 * mweld.getWorldCenter().sub(bweld.getWorldCenter()).length())
					;
			pjs.add((PrismaticJoint)The_Game.world.createJoint(djd));
/*
			djd.initialize(main, b, main.getWorldCenter().sub(nextpos.mul(main_radius)), b.getWorldCenter().add(nextpos.mul(bodyRadius)));
			The_Game.world.createJoint(djd);

			
			float na = angle + (float)Math.PI/2;
			Vec2 np = nextpos = new Vec2((float)Math.cos(na),(float)Math.sin(na));
			djd.initialize(main, b, main.getWorldCenter().add(np.mul(main_radius)), b.getWorldCenter().sub(np.mul(bodyRadius)));
			The_Game.world.createJoint(djd);

			na = angle - (float)Math.PI/2;
			np = nextpos = new Vec2((float)Math.cos(na),(float)Math.sin(na));
			djd.initialize(main, b, main.getWorldCenter().add(np.mul(main_radius)), b.getWorldCenter().sub(np.mul(bodyRadius)));
			The_Game.world.createJoint(djd);
*/
			
			cvjd.addBody(b);


		}
		
		cvj = (ConstantVolumeJoint)The_Game.world.createJoint(cvjd);
	}
	float generaldist = 0;
	
	
	public abstract Vec2 solveWalkAI();
	public abstract boolean solveJumpAI();
	public abstract boolean solvePushAI();

	int time_in_push = 0;
	Vec2 jumpang = new Vec2();
	
	
	public boolean jump(boolean solve){
		return false;
	}
	/*
	 * While Held
	 * Pressure = -1/time_held +
	 * 
	 * 
	 * 
	 */
	
	private Vec2 getContactDiff(ContactEdge c, Vec2 other){
		WorldManifold wm = new WorldManifold();
		c.contact.getWorldManifold(wm);
		return wm.points[0].sub(other);
	}
	
	public void walk(Vec2 direction){
		//walk needs to pull
		//to pull i need to make sure the two pieces are connected
		if(direction == null || direction.length() == 0 || col.size() == 0){
			return;
		}
		direction.normalize();
		float velocity = 1000;
		/*
		 * Now I need to find the velocity to where things start bouncing
		 * Then low my center of gravity
		 */
				
		float dang = (float)Math.atan2(direction.y, direction.x);
		HashMap<Body, Vec2> b_to_c = new HashMap<Body,Vec2>();
		Vec2 netAngle = new Vec2();
		for(Body b : col){
			
			Vec2 m2b = b.getWorldCenter().sub(main.getWorldCenter());
			float angle = (float)Math.atan2(m2b.y, m2b.x);
			
			if(Math.abs(MathHelper.closestAngleDiff(b.getAngle(), angle)) > Math.PI*3/4 )
				System.out.println("theres a problem: "+MathHelper.closestAngleDiff(b.getAngle(), angle));
			
			ContactEdge c = b.getContactList();
			while(c != null){
				if(c.contact.isEnabled() && c.contact.isTouching()){
					Vec2 t = getContactDiff(c, b.getWorldCenter());
					float anglediff = (float)(Math.atan2(t.y, t.x)-dang);
					while(anglediff > Math.PI) anglediff -= Math.PI*2;
					while(anglediff < -Math.PI) anglediff += Math.PI*2;

					if(
						!(anglediff < Math.PI/12 && anglediff > -Math.PI/12)
					&& !(anglediff > Math.PI-Math.PI/12 || anglediff < -Math.PI+Math.PI/12)
					){
						t.normalize();
						b_to_c.put(b, t);
						netAngle.addLocal(t);
					}
				}
				c = c.next;
			}
		}
		
		if(netAngle.length() == 0) return;
		netAngle.normalize();

		float anglediff = (float)(Math.atan2(netAngle.y, netAngle.x)-dang);
		while(anglediff > Math.PI) anglediff -= Math.PI*2;
		while(anglediff < -Math.PI) anglediff += Math.PI*2;
		float rps = velocity/distance;
		
		float newvel  = rps*Math.signum(anglediff);// -feet.getAngularVelocity();
		
		float cvel = main.getAngularVelocity();
		if(	Math.signum(cvel) == Math.signum(newvel)
		&&	Math.abs(cvel) >= Math.abs(newvel)) return;
				
		float omass = owner.getMass();
		main.applyTorque(newvel*main.getInertia()*60);
		/*
		Iterator<Body> i = bodies.iterator();
		while(i.hasNext()){
			Body b = i.next();
//					Keep it simple stupid
//					:) Thanks Roy
			if(b_to_c.containsKey(b)){
				float angle = (float)Math.atan2(b_to_c.get(b).y, b_to_c.get(b).x);
				angle += Math.signum(newvel)*(float)Math.PI/2;
				Vec2 btomaindiff = new Vec2((float)Math.cos(angle),(float)Math.sin(angle));
				b.applyForceToCenter(btomaindiff.mul(rps*b.getMass()*60));
			}else{
				Vec2 btomaindiff = b.getWorldCenter().sub(main.getWorldCenter());
				float angle = (float)Math.atan2(btomaindiff.y, btomaindiff.x);
				angle += Math.signum(newvel)*(float)Math.PI/2;
				btomaindiff = new Vec2((float)Math.cos(angle),(float)Math.sin(angle));
				b.applyForceToCenter(btomaindiff.mul(rps*b.getMass()*60));
			}
		}
		*/
	}
	//Osmos
	@Override
	public boolean doAI() {
		
		for(PrismaticJoint pj : pjs){
//			if(pj.getJointTranslation() != 0) pj.setMotorSpeed(-pj.getJointTranslation());
		}
			
		
		walk(solveWalkAI());
		return true;
	}

	@Override
	public void die() {
		// TODO Auto-generated method stub
		
	}

}
