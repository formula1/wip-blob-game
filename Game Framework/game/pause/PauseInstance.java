package game.pause;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;



import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.contacts.Contact;

import abstracts.BodyData;


/*
 * So a single pause happens
 * 
 * Then another person comes in and hits someone
 * -need to check if that body is paused (UserData)
 * -Everyone gets paused for x longer
 * -the person that was hit needs to change their afterhit velocities
 */

public class PauseInstance {
	int timeleft;
	HashMap<Body, PauseInfo> bodies;
		
	public PauseInstance(Contact contact, ContactImpulse impulse){
		this.bodies = new HashMap<Body,PauseInfo>();

		Body A = contact.getFixtureA().getBody();
		Body B = contact.getFixtureB().getBody();

		float max = 0;
		for(float f : impulse.normalImpulses)	max = Math.max(max, f);
		int init = Math.round(max/100);
		
		
		if(timeleft == 0) timeleft = init;

		appendFreind(A);
		appendFreind(B);
	}
	/*
	public PauseInstance(Contact contact, boolean isClash){
		this.bodies = new HashMap<Body,PauseInfo>();
		contact.setEnabled(false);
		Body A = contact.getFixtureA().getBody();
		Body B = contact.getFixtureB().getBody();
		WorldManifold wm = new WorldManifold();
		contact.getWorldManifold(wm);
		Vec2 Am = A.getLinearVelocityFromWorldPoint(wm.points[0]).mul(A.getMass());
		Vec2 Bm = B.getLinearVelocityFromWorldPoint(wm.points[0]).mul(B.getMass());
		bodies.put(A, new PauseInfo(A));
		bodies.put(B, new PauseInfo(B));
		if(isClash) timeleft = -1;
		else 		timeleft = Math.round(Am.add(Bm).length()/10);


		appendFreind(A);
		appendFreind(B);
	}
*/

	
	public void appendFreind(Body body){
		bodies.put(body, new PauseInfo(body));
		BodyData d = (BodyData)body.getUserData();
		d.pause = this;
		body.setUserData(d);
	}

	public void appendFreinds(Contact contact, ContactImpulse impulse){
		contact.setEnabled(false);
		Body A = contact.getFixtureA().getBody();
		Body B = contact.getFixtureB().getBody();
		WorldManifold wm = new WorldManifold();
		contact.getWorldManifold(wm);
		
		if(bodies.containsKey(A))
			bodies.get(A).moreVel(A);
		else{
			appendFreind(A);
		}

		if(bodies.containsKey(B))
			bodies.get(B).moreVel(B);
		else{
			appendFreind(B);
		}
		float max = 0;
		for(float f : impulse.normalImpulses)	max = Math.max(max, f);
		int init = Math.round(max/100);
		
		timeleft = Math.max(timeleft,  init);
	}
	
	public void setTime(int time){
		this.timeleft = time;
	}

	public boolean time(){
		if(timeleft == 0) unpause();
		else if(timeleft > 0){
			timeleft--;
			Iterator<Entry<Body,PauseInfo>> x = bodies.entrySet().iterator();
			while(x.hasNext()){
			    Map.Entry<Body,PauseInfo> entry = x.next();
			    entry.getValue().remainPause();
			}
		}
		return (bodies.size()==0);
	}
	
	public void unpause(){
		timeleft = 0;
		Iterator<Entry<Body,PauseInfo>> x = bodies.entrySet().iterator();
		while(x.hasNext()){
		    Map.Entry<Body,PauseInfo> entry = x.next();
		    entry.getValue().unpause(entry.getKey());
		}
		
	}
	
	public boolean deconstruct(Contact contact){
		//need deconstruct because since we pause mid collision, we have to set disabled to true over and over
		//after we unpause, all those collisions happen unless we disable the collision until they've become sperated
		Body A = contact.getFixtureA().getBody();
		bodies.get(A).removePause();
		bodies.remove(A);
		A = contact.getFixtureB().getBody();
		bodies.get(A).removePause();
		bodies.remove(A);
		
		return (bodies.size() == 0);
	}
		
	public void appendImpact(Body body, Impact impact){
		bodies.get(body).moreVel(body);
	}
	
	private class PauseInfo{
		ArrayList<Vec2> LinVels;
		ArrayList<Float> AngVels;
		float gravscale;
		boolean paused = true;
		Body body;
		
		
		public PauseInfo(Body body){
			this.body = body;
			AngVels = new ArrayList<Float>();
			LinVels = new ArrayList<Vec2>();
			AngVels.add(body.getAngularVelocity());
			LinVels.add(body.getLinearVelocity().clone());
			this.gravscale = body.getGravityScale();

			
		}
		
		public void remainPause(){
			body.setGravityScale(0);
			body.setAngularVelocity(0);
			body.setLinearVelocity(new Vec2());
		}
		
		public void moreVel(Body body){
			LinVels.add(body.getLinearVelocity().clone());
			AngVels.add(body.getAngularVelocity());
		}
		
		public void removePause(){
			BodyUserData d = (BodyUserData)body.getUserData();
			d.paused = null;
			body.setUserData(d);
		}
		
		public boolean unpause(Body body){
			if(paused){
				body.setGravityScale(gravscale);
				Vec2 finallinVel = new Vec2();
				for(Vec2 lvel : LinVels)
				{
					finallinVel = finallinVel.add(lvel);
				}
				Float finalangVel = 0f;
				for(Float avel : AngVels){
					finalangVel += avel;
				}
				body.setLinearVelocity(finallinVel);
				body.setAngularVelocity(finalangVel);
				paused = false;
			}

			return true; // used for delayed rapid impacts
		}
		
		
	}
}
