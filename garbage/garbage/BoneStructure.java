package garbage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.JointDef;
import org.jbox2d.dynamics.joints.JointType;
import org.jbox2d.dynamics.joints.PrismaticJoint;
import org.jbox2d.dynamics.joints.PrismaticJointDef;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;
import org.jbox2d.dynamics.joints.WeldJointDef;

import abstracts.Game.Ownership;

public abstract class BoneStructure {
	Vec2 center_of_gravity;
	World world;
	protected HashMap<String,Bone> bones;
	public HashMap<String,Joint> joints;
	BoneStructure bs;
	public Object userData;
	
	public BoneStructure(World world){
		this.world = world;
		this.bs = this;
		bones = new HashMap<String,Bone>();
		joints = new HashMap<String,Joint>();
	}
	
	public Body get(String key){
		return bones.get(key).bod;
	}
	
	public Bone createBone(String name, BodyDef bd, FixtureDef[] fd){
		return new Bone(name, bd, fd);
	}
	
	public float getMass(){
		float total = 0;
		Iterator i = bones.entrySet().iterator();
		while(i.hasNext()) total += ((Entry<String,Bone>)i.next()).getValue().bod.getMass();
		return total;
	}

	
	public abstract void tweenToLocation(String j, float desired_location);
	public abstract void tweenToRotation(String j, float desired_angle);	
	
	public abstract BodyDef defaultBody(Vec2 offset, float angle);
	public abstract FixtureDef[] defaultFixtures(JointType joint, String name);
	public abstract JointDef defaultJoint();
	
	public class Bone{
		
		Bone ancestor;
		Joint joint_to_ancestor;

		String name;
		public Body bod;
		public float held_mass;
		
		HashMap<String, Bone> children;
		
		public Bone(String n){
			children = new HashMap<String, Bone>();
			this.name = n;
			bs.bones.put(name, this);
		}

		public Bone(Bone ancestor, String name, JointType joint, Vec2 offset, float angle, Vec2 limits){
			this(name);
			this.ancestor = ancestor;
			bod = bs.world.createBody(bs.defaultBody(ancestor.bod.getWorldCenter().add(offset), angle));
			FixtureDef[] fd = bs.defaultFixtures(joint, name);
			held_mass = bod.getMass();
			for(FixtureDef f : fd) bod.createFixture(f);
			
			if(joint == JointType.WELD){
				WeldJointDef jd = new WeldJointDef();
				jd.initialize(
					ancestor.bod,
					this.bod,
					ancestor.bod.getWorldCenter().add(offset)
				);
				jd.collideConnected = false;
				joint_to_ancestor = world.createJoint(jd);
			}else if(joint == JointType.PRISMATIC){
				PrismaticJointDef pd = new PrismaticJointDef();
//				pd.initialize(player_entity.bodies.get(attachRef), player_entity.bodies.get(newRef), 
//						new Vec2(0,0), offset);
				pd.bodyA = ancestor.bod;
				pd.bodyB = this.bod;
				pd.collideConnected = false;
				pd.enableMotor = true;
				pd.maxMotorForce = bod.getMass();
				pd.motorSpeed = 0;
				if(limits != null){
					pd.enableLimit = true;
					pd.lowerTranslation = limits.x;
					pd.upperTranslation = limits.y;
				}
				joint_to_ancestor = world.createJoint(pd);
			}else if(joint == JointType.REVOLUTE){
				RevoluteJointDef jd = new RevoluteJointDef();
				jd.bodyA = ancestor.bod;
				jd.localAnchorA = new Vec2(offset.x, offset.y);
				jd.bodyB = bod;
				jd.collideConnected = false;
				jd.enableMotor = true;
				jd.maxMotorTorque = bod.getMass();
				jd.motorSpeed = 0;
				if(limits != null){
					jd.enableLimit = true;
					jd.lowerAngle =(float) limits.x;
					jd.upperAngle = (float)limits.y;
				}
				joint_to_ancestor = world.createJoint(jd);
			}
			bs.joints.put(name, joint_to_ancestor);
		}
		
		public Bone(String This_Reference, BodyDef bd, FixtureDef[] fd){
			this(This_Reference);
			bod = bs.world.createBody(bd);
			for(FixtureDef f : fd)	bod.createFixture(f);
			resetMaxForce();
		}

		public Bone addNewChild(JointType joint, String child_name, float angle, Vec2 offset, Vec2 limits){
			Bone child = new Bone(this, child_name, joint, offset,  angle, limits);
			bs.bones.put(child.name, child);
			children.put(child.name, child);
			resetMaxForce();
			return child;
		}

		
		public void resetMaxForce(){
			held_mass = bod.getMass();
			for(Entry<String, Bone> e : children.entrySet()){
				held_mass += e.getValue().held_mass;
				if(JointType.PRISMATIC == 
						children.get(e.getKey())
						.joint_to_ancestor.getType()){
					float distance = ((PrismaticJoint)bs.joints.get(e.getKey())).getUpperLimit() - ((PrismaticJoint)bs.joints.get(e.getKey())).getLowerLimit();
					((PrismaticJoint)bs.joints.get(e.getKey())).setMaxMotorForce(e.getValue().held_mass*distance*360);
				}else if(JointType.REVOLUTE == children.get(e.getKey()).joint_to_ancestor.getType()){
					((RevoluteJoint)bs.joints.get(e.getKey())).setMaxMotorTorque(e.getValue().held_mass*2*(float)Math.PI*360);
				}
			}
			if(ancestor != null){
				ancestor.resetMaxForce();
			}
		}
	}
}
