package game.pause;

import helpers.JointCallback;
import init.The_Game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.ConstantVolumeJoint;
import org.jbox2d.dynamics.joints.DistanceJoint;
import org.jbox2d.dynamics.joints.GearJoint;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.JointEdge;
import org.jbox2d.dynamics.joints.JointType;
import org.jbox2d.dynamics.joints.PrismaticJoint;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.WeldJoint;
import org.jbox2d.dynamics.joints.WheelJoint;

public class GlobalPause {
	HashMap<Body, SpeedHold> b2speed;
	HashMap<Joint, JointHold> j2speed;
	ArrayList<Pause> pauses = new ArrayList<Pause>();
	static ArrayList<Body> exceptions = new ArrayList<Body>();
	
	public GlobalPause(){
		b2speed = new HashMap<Body,SpeedHold>();
		j2speed = new HashMap<Joint, JointHold>();
	}
	
	public void addPause(Body exception, int time){
		addPause(exception, time, false);
	}

	public void addPause(Body exception, int time, boolean additional){
		int has = -1;
		int counter = 0;
		for(Pause p : pauses){
			if(p.be.contains(exception)){
				has = counter;
				break;
			}
			counter++;
		}
		if(pauses.size() > 0){
			if(!pauses.get(pauses.size()-1).be.contains(exception)){
				for(Body b : pauses.get(pauses.size()-1).be)
					b2speed.put(b,new SpeedHold(b));
				for(Joint j : pauses.get(pauses.size() -1).je)
					j2speed.put(j,new JointHold(j));
			}
		}
		if(!additional && has != -1){
			Pause temp = pauses.remove(has);
			temp.time = time;
			pauses.add(temp);
		}else{
			pauses.add(new Pause(exception, time));
		}
		setupPause();
	}
	
	public void setupPause(){
		Body b = The_Game.world.getBodyList();
		while(b != null){
			if(b.getType() != BodyType.STATIC)
				b2speed.put(b,new SpeedHold(b));
			b = b.getNext();
		}
		Joint j = The_Game.world.getJointList();
		while(j != null){
			if(	j.getType() == JointType.DISTANCE
			||	j.getType() == JointType.PRISMATIC
			||	j.getType() == JointType.REVOLUTE
			||	j.getType() == JointType.WELD
			||	j.getType() == JointType.WHEEL
			)	j2speed.put(j,new JointHold(j));
			j = j.getNext();
		}
	}
	
	public boolean doAI(){
		pauses.get(pauses.size()-1).time--;
		if(pauses.size() == 0){
			return false;
		}else if(pauses.get(pauses.size()-1).time == 0){
			return endPause(pauses.get(pauses.size()-1);
		}else if(pauses.get(pauses.size()-1).time > 0){
			for(SpeedHold s : b2speed) s.doAI();
			return false;
		}	
	}
	
	public boolean endPause(){
		for(SpeedHold s : b2speed) s.unpause();
		b2speed.clear();
		for(JointHold s : j2speed) s.unpause();
		j2speed.clear();
	}
	
	private class Pause{
		TreeSet<Joint> je = new TreeSet<Joint>(new Comparator<Joint>(){
			public int compare(Joint arg0, Joint arg1) {
				return arg0.hashCode() - arg1.hashCode();
			}
		});
		TreeSet<Body> be = new TreeSet<Body>(new Comparator<Body>(){
			public int compare(Body a, Body b){
				return a.hashCode() - b.hashCode();
			}
		});
		int time;
		public Pause(Body b, int time){
			The_Game.jhelp.downTheTree(b, new JointCallback(null){
				public void processBody(Body body) {
					be.add(body);
					JointEdge j = body.getJointList();
					while(j != null){
						je.add(j.joint);
						j = j.next;
					}
				}
			});
			this.time = time;
		}
	}

	
	private class SpeedHold{
		Vec2 linear;
		float angular;
		float gravity;
		float lindamp;
		float angdamp;
		Body b;
		
		public SpeedHold(Body b){
			this.linear = b.getLinearVelocity();
			this.angular = b.getAngularVelocity();
			this.gravity = b.getGravityScale();
			b.setGravityScale(0);
			this.lindamp = b.getLinearDamping();
			this.angdamp = b.getAngularDamping();
			this.b = b;
		}
		
		public void doAI(){
			b.applyAngularImpulse(-b.getAngularVelocity()*b.getInertia());
			b.applyLinearImpulse(b.getLinearVelocity().mul(-1*b.getMass()), b.getWorldCenter(), false);
			this.lindamp = 1;
			this.angular = 1;
		}
		public void unpause(){
			b.applyAngularImpulse(b.getInertia()*angular);
			b.applyLinearImpulse(linear.mul(b.getMass()), b.getWorldCenter(), false);
			b.setGravityScale(gravity);
			b.setLinearDamping(lindamp);
			b.setAngularDamping(angdamp);
		}
	}
	
	private class JointHold{
		boolean motor;
		float freq;
		float damp;
		
		Joint joint;
		public JointHold(Joint j){
			this.joint = j;
			JointType jt = j.getType();
			if(jt == JointType.DISTANCE){
				DistanceJoint t = (DistanceJoint)j;
				freq = t.getFrequency();
				damp = t.getDampingRatio();
				t.setFrequency(0);
				t.setDampingRatio(0);
			}else if(jt == JointType.PRISMATIC){
				PrismaticJoint t = (PrismaticJoint)j;
				motor = t.isMotorEnabled();
				t.enableMotor(false);
			}else if(jt == JointType.REVOLUTE){
				RevoluteJoint t = (RevoluteJoint)j;
				motor = t.isMotorEnabled();
				t.enableMotor(false);
			}else if(jt == JointType.WELD){
				WeldJoint t = (WeldJoint)j;
				freq = t.getFrequency();
				damp = t.getDampingRatio();
				t.setFrequency(0);
				t.setDampingRatio(0);
			}else if(jt == JointType.WHEEL){
				WheelJoint t = (WheelJoint)j;
				motor = t.isMotorEnabled();
				t.enableMotor(false);
				freq = t.getSpringFrequencyHz();
				damp = t.getSpringDampingRatio();
				t.setSpringDampingRatio(0);
				t.setSpringFrequencyHz(0);
			}
		}
		
		public void unpause(){
			JointType jt = joint.getType();
			if(jt == JointType.DISTANCE){
				DistanceJoint t = (DistanceJoint)joint;
				t.setFrequency(freq);
				t.setDampingRatio(damp);
			}else if(jt == JointType.PRISMATIC){
				PrismaticJoint t = (PrismaticJoint)joint;
				t.enableMotor(motor);
			}else if(jt == JointType.REVOLUTE){
				RevoluteJoint t = (RevoluteJoint)joint;
				t.enableMotor(motor);
			}else if(jt == JointType.WELD){
				WeldJoint t = (WeldJoint)joint;
				t.setFrequency(freq);
				t.setDampingRatio(damp);
			}else if(jt == JointType.WHEEL){
				WheelJoint t = (WheelJoint)joint;
				t.enableMotor(motor);
				t.setSpringFrequencyHz(freq);
				t.setSpringDampingRatio(damp);
			}
		}
	}
}
