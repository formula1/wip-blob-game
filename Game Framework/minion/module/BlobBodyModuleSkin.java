package minion.module;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import helpers.BodyDefCallback;
import helpers.FixtureDefCallback;
import init.The_Game;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.contacts.ContactEdge;
import org.jbox2d.dynamics.joints.ConstantVolumeJoint;
import org.jbox2d.dynamics.joints.ConstantVolumeJointDef;
import org.jbox2d.dynamics.joints.DistanceJoint;
import org.jbox2d.dynamics.joints.DistanceJointDef;
import org.jbox2d.dynamics.joints.WeldJointDef;

import assets.FixtureData;

import entity_managers.CollisionManager;


import minion.abstracts.MinionEntity;
import minion.abstracts.MinionModule;
import minion.clever.Suction;

public abstract class BlobBodyModuleSkin extends MinionModule{

	
	/*
	 * What happens is
	 * -Impact happens
	 * 	-if we already have a 
	 * 
	 * 
	 */
	
	ConstantVolumeJoint cvj;
	Body[] skeleton = new Body[3];
	float distance = 20f;
	float mr;
	TreeSet<Body> bodies = new TreeSet<Body>(new Comparator<Body>(){
		public int compare(Body o1, Body o2) {
			return (int) Math.signum(o1.getAngle() - o2.getAngle());
		}
	});
	TreeMap<Body, DistanceJoint> dists = new TreeMap<Body, DistanceJoint>(new Comparator<Body>(){
		public int compare(Body o1, Body o2) {
			return o1.hashCode() - o2.hashCode();
		}
	});
	
	
	TreeSet<Body> col = new TreeSet<Body>(new Comparator<Body>(){
		public int compare(Body a, Body b){
			return a.hashCode() - b.hashCode();
		}
	});
	
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
	public BlobBodyModuleSkin(MinionEntity owner, float main_radius) {
		super(owner);
		mr = main_radius;
		
		float squishfreq = 5f; //jiggle (neded to stop forces)
		float squishdamp = .5f; //needed to stop jiggle?
		
		
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
		djd.collideConnected = false;

		
		final float chain_density = main.getMass()/nBodies/((float)Math.PI*bodyRadius*bodyRadius);
		
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DYNAMIC;
		
		Vec2[] points = new Vec2[nBodies];
		for (int i = 0; i < nBodies; i++) {
			final float angle = (float)Math.PI*2f*((float)i/nBodies);
			final Integer index = i;
			nextpos = new Vec2((float)Math.cos(angle),(float)Math.sin(angle));
			System.out.println("nextpos:" + nextpos);
			
			Body b = The_Game.help.createFromArguments(main.getWorldCenter().add(nextpos.mul(distance)), The_Game.help.circle(bodyRadius), BodyType.DYNAMIC,
				new BodyDefCallback(){
					public BodyDef bodyDefCallback(BodyDef bd){
						bd.angle = angle+(float)Math.PI/2;
						bd.userData = (Integer)index;
						bd.fixedRotation = true;
						return bd;
					}
				},
				new FixtureDefCallback(){
					public FixtureDef fixDefCallback(FixtureDef fixture){
						fixture.friction = 1;
						fixture.density = chain_density;
						fixture.userData = new FixtureData(colm);
						return fixture;
					}
				}
			);
			bodies.add(b);

			
//			djd.initialize(main, b, main.getWorldCenter().add(nextpos.mul(main_radius)), b.getWorldCenter().sub(nextpos.mul(bodyRadius)));
//			dists.put(b, (DistanceJoint)The_Game.world.createJoint(djd));
//			generaldist = dists.get(b).getLength();
			
			
			cvjd.addBody(b);

			points[i] = nextpos.mul(distance+bodyRadius*2f);
		}
		
		for(int i=0;i<skeleton.length;i++){
			final float angle = (float)Math.PI*2f*(i/(float)skeleton.length);
			nextpos = new Vec2((float)Math.cos(angle),(float)Math.sin(angle));
			skeleton[i] = The_Game.help.createFromArguments(main.getWorldCenter().add(nextpos.mul(main_radius*2)), The_Game.help.circle(main_radius), BodyType.DYNAMIC,
				new FixtureDefCallback(){
					public FixtureDef fixDefCallback(FixtureDef fixture) {
						fixture.friction = 10;
						return fixture;
					}
				}
					);
			if(i>0){
				djd.initialize(skeleton[i-1], skeleton[i], skeleton[i-1].getWorldCenter(), skeleton[i].getWorldCenter());
				The_Game.world.createJoint(djd);
			}
			djd.initialize(main, skeleton[i], main.getWorldCenter(), skeleton[i].getWorldCenter());
			The_Game.world.createJoint(djd);
		}
		djd.initialize(skeleton[2], skeleton[0], skeleton[2].getWorldCenter(), skeleton[0].getWorldCenter());
		The_Game.world.createJoint(djd);

		
		cvj = (ConstantVolumeJoint)The_Game.world.createJoint(cvjd);
		cvj.inflate(.9f);
	}
	
	
	
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
		float velocity = 40;
		/*
		 * Now I need to find the velocity to where things start bouncing
		 * Then low my center of gravity
		 */
				
		float dang = (float)Math.atan2(direction.y, direction.x);

		Vec2 netAngle = new Vec2();
		for(Body b : col){
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
		float rps = velocity/mr;
		
		float newvel  = rps*Math.signum(anglediff);// -feet.getAngularVelocity();
		
				
		for(Body b : skeleton){
//					Keep it simple stupid
//					:) Thanks Roy
			b.applyAngularImpulse(newvel*b.getInertia());
		}
	}
	//Osmos
	@Override
	public boolean doAI() {
		for(int i=0;i<skeleton.length;i++){
			Fixture f = skeleton[i].getFixtureList();
			Filter fi = f.getFilterData();
			fi.categoryBits = 1;
			fi.maskBits = 0xFFFF;
			f.setFilterData(fi);
		}
		walk(solveWalkAI());
		return true;
	}

	@Override
	public void die() {
		// TODO Auto-generated method stub
		
	}

}
