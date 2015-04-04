package game.pause;

import helpers.JointCallback;
import init.The_Game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

import abstracts.BodyData;
import assets.FixtureData;

public class Pause implements Comparable<Pause>{

	public int index;
	
	public boolean init = true;
	public boolean active = true;
	public boolean end = false;
	public boolean remove = false;
	
		
	int timeleft = 0;
	
	public HashMap<Body, PauseInfo> bodies;
	
	
	
	
	public Pause(Contact contact, ContactImpulse impulse, float imp, int index){
		this.index = index;
		this.bodies= new HashMap<Body, PauseInfo>();
		
		Body A = contact.getFixtureA().getBody();
		Body B = contact.getFixtureB().getBody();

		timeleft = Math.round(imp/100)*20;

		appendFreind(A);
		appendFreind(B);

	}
	
	public void appendFreind(Body body){
		final PauseInfo pi = new PauseInfo(body);
		bodies.put(body, pi);

		The_Game.jhelp.downTheTree(body, new JointCallback(this){
			@Override
			public void processBody(Body tree_body) {
				BodyData d = (BodyData)tree_body.getUserData();
				if(d == null) d = new BodyData();
				d.pause = (Pause)this.additive;
				tree_body.setUserData(d);
				if(d.intel != null)
					The_Game.intelmanager.intels.remove(d.intel);
				bodies.put(tree_body, pi);
			}
		});
	}
	
	public void unfreind(Body body){
		
	}

	public void importPause(Pause pause){
		BodyData bd;
		for(Map.Entry<Body, PauseInfo> e : pause.bodies.entrySet()){
			bd = (BodyData)e.getKey().getUserData();
			bd.pause = this;
			e.getKey().setUserData(bd);
			bodies.put(e.getKey(), e.getValue());
		}

		if(pause.timeleft > timeleft){
			this.timeleft = pause.timeleft;
		}
	}
	
	public static float findMax(ContactImpulse impulse){
		float max = 0;
		for(float f : impulse.normalImpulses)	max = Math.max(max, f);
		return max;
	}
	
	public void solveVelocities(){
		for(Map.Entry<Body, PauseInfo> e: bodies.entrySet()){
			e.getValue().solveVelocities();
		}
		this.init = false;
	}
	
	public void time(){
		
	}
	
	public void unpause(){
		this.end = true;
		this.active = false;
		ArrayList<Body> toRemove = new ArrayList<Body>();
		for(Map.Entry<Body, PauseInfo> e : bodies.entrySet()){
			BodyData d = (BodyData)e.getKey().getUserData();
			if(d != null && d.intel != null) The_Game.intelmanager.intels.add(d.intel);
			if(e.getValue().body != e.getKey()) toRemove.add(e.getKey());
		}
		for(Body b : toRemove)bodies.remove(b);
	}
	
	private class PauseInfo{
		public Body body;
		public Vec2 final_lin;
		public Float final_ang;
		public Vec2 pos;
		public float ang;
		public float gravscale;
		
		public boolean paused = true;
		
		public PauseInfo(Body body){
			this.body = body;
			final_lin = new Vec2();
			final_ang = 0f;
			pos = body.getWorldCenter();
			ang = body.getAngle();
			gravscale = body.getGravityScale();
		}
				
		public void solveVelocities(){
			
			final_lin.add(body.getLinearVelocity());
			final_ang += body.getAngularVelocity();
			
			body.setGravityScale(0);
			body.setAngularVelocity(0);
			body.setLinearVelocity(new Vec2());
			body.setTransform(pos,ang);
		}
		
		public void unpause(){
			body.setGravityScale(gravscale);
			body.setLinearVelocity(final_lin);
			body.setAngularVelocity(final_ang);
			paused = false;
		}
		
	}


	@Override
	public int compareTo(Pause o) {
		return this.index - o.index;
	}
	
}
