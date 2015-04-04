package garbage;

import garbage.BoneStructure.Bone;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.joints.JointDef;
import org.jbox2d.dynamics.joints.JointType;
import org.jbox2d.dynamics.joints.PrismaticJoint;
import org.jbox2d.dynamics.joints.RevoluteJoint;

import abstracts.Game.Ownership;

public class Garbo_Entity {
	public int player_number;
	public int floor = 0;
	public String state = "inair";
	BoneStructure bs;

	
	public BoneStructure getNewBoneStructure(int player_number){
		BoneStructure bs =  new BoneStructure(world){

			@Override
			public void tweenToLocation(String j, float loc) {
				if(joints.get(j).getType() != JointType.PRISMATIC) return;
				PrismaticJoint joint = (PrismaticJoint)joints.get(j);

				float nextLoc = (joint.getJointTranslation() + joint.getJointSpeed() / 60.0f);
				float totalMove = loc - nextLoc;
				if(totalMove < .1) return;
				float desiredAngularVelocity = totalMove * 60;
				float impulse = desiredAngularVelocity*bones.get(j).bod.getMass();// disregard time factor
				System.out.println(j+" impulse: "+impulse);
				joint.setMotorSpeed(impulse);
			}
			
			@Override
			public void tweenToRotation(String j, float angle) {
				if(joints.get(j).getType() != JointType.REVOLUTE) return;
				RevoluteJoint joint = (RevoluteJoint)joints.get(j);
				float nextAngle = (joint.getJointAngle() + joint.getJointSpeed() / 60.0f);
				float totalRotation = angle - nextAngle;
				if(totalRotation < .01) return;
				while(Math.abs(totalRotation) > Math.PI){
					if(totalRotation > 0)	totalRotation -= (float)2*Math.PI;
					else					totalRotation += (float)2*Math.PI;
				}

				float desiredAngularVelocity = totalRotation * 60;
				float impulse = desiredAngularVelocity*bones.get(j).bod.getInertia();// disregard time factor
				System.out.println(j+" impulse: "+impulse);
				float max_change = 1f;
				joint.setMotorSpeed(Math.max(-max_change, Math.min(impulse, max_change)));
			}


			@Override
			public BodyDef defaultBody(Vec2 offset, float angle) {
				BodyDef bd = new BodyDef();
				bd.type = BodyType.DYNAMIC;
				bd.gravityScale = 0;
				bd.angle = angle;
				bd.position = new Vec2(
					 offset.x, 
					 offset.y
				);
				return bd;
			}

			@Override
			public FixtureDef[] defaultFixtures(JointType joint, String name) {
				FixtureDef fd = new FixtureDef();
				fd.filter.categoryBits =(int)Math.pow(2, (Integer)userData+3);
				fd.filter.maskBits = getMaskIndex((Integer)userData);
				fd.density = standard_density;
				fd.friction = standard_friction;
				fd.userData = new Ownership((Integer)userData, name);
				if(joint == JointType.WELD){
					PolygonShape tp = new PolygonShape();
					tp.setAsBox(.5f, .5f);
					fd.shape = tp;
				}else if(joint == JointType.PRISMATIC){
					PolygonShape tp = new PolygonShape();
					tp.setAsBox(1, .5f);
					fd.shape = tp;
				}else if(joint == JointType.REVOLUTE){
					CircleShape s = new CircleShape();
					s.m_radius = .5f;
					fd.shape = s;
				}
				return new FixtureDef[] {fd};
			}

			@Override
			public JointDef defaultJoint() {
				// TODO Auto-generated method stub
				return null;
			}

			
		};
		bs.userData = player_number;
		return bs;
	}

	
	public Garbo_Entity(){

		
		
		bs = getNewBoneStructure(player_number);
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DYNAMIC;
		bd.position = position;
		bd.gravityScale = 0;
		bd.fixedRotation = true;


		FixtureDef fd = new FixtureDef();
		fd.density = 5.0f;
		fd.friction = 0.3f;
		fd.filter.categoryBits =(int)Math.pow(2, player_number+3);
		fd.filter.maskBits = help.getMaskIndex(player_number);
		PolygonShape tp = new PolygonShape();
		tp.setAsBox(1f, 1f);
		fd.shape = tp;
		
		Bone mid = bs.createBone("ab",bd,new FixtureDef[] {fd});
		mid
		.addNewChild(JointType.WELD, "lowerback_r", 0, new Vec2(0,0), null)
		.addNewChild(JointType.PRISMATIC, "pivot_r", -(float)Math.PI/2, new Vec2(0,0), new Vec2(-3, 3))
//		.addNewChild(JointType.REVOLUTE, "hip_r", 0, new Vec2(0,0), new Vec2(-(float)Math.PI/4, (float)Math.PI/4))
		.addNewChild(JointType.WELD, "knee_r", -(float)Math.PI/2, new Vec2(0,-6),null)
		.addNewChild(JointType.PRISMATIC, "ankle_r", 0, new Vec2(0,0f), new Vec2(-3, 3));

		mid
		.addNewChild(JointType.WELD, "lowerback_l", 0, new Vec2(0,0), null)
		.addNewChild(JointType.PRISMATIC, "pivot_l", -(float)Math.PI/2, new Vec2(0,0), new Vec2(-3, 3))
//		.addNewChild(JointType.REVOLUTE, "hip_l", 0, new Vec2(0,0), new Vec2(-(float)Math.PI/4, (float)Math.PI/4))
		.addNewChild(JointType.WELD, "knee_l", -(float)Math.PI/2, new Vec2(0,-6),null)
		.addNewChild(JointType.PRISMATIC, "ankle_l", 0, new Vec2(0,0f), new Vec2(-3, 3));

//		walking = new Walking(this);
		int filterindex = (int)Math.pow(2, player_number+2);
		
		
		this.player_number = player_number;

		
	}
	
/*		
	public class Walking{
		String anchorFoot = "";
		String a = "";
		String na = "";
		ArrayList<WeldJoint> anchorJoint;
		PlayerEntity daddy;
		boolean searching = false;

		
		
		public Walking(PlayerEntity d){
			daddy= d;
			anchorJoint = new ArrayList<WeldJoint>();
		}
		
		public void collision(Contact contact, Ownership fixadata, Ownership fixbdata){
			if(anchorFoot != fixadata.type && searching){
				anchorFoot = fixadata.type;
				na = a;
				a = anchorFoot.substring(6);
				searching = false;
				System.out.println("/////////////////////////////////new foot////////////////////");
			}
		}
		public void attemptToWalk(byte direction){
			daddy.state = "walking";
			boolean[] boos = {false,false,false,false,false,false};
			
			
			if(anchorFoot == ""){
				anchorFoot = 
					(daddy.bodies.get("pivot_r").getWorldCenter().sub(daddy.bodies.get("pivot_l").getWorldCenter()).x*direction < 0)?
							"ankle_r":"ankle_l";
				a = anchorFoot.substring(6);
				if(a == "r")
					na = "l";
				else
					na = "r";
				byte foranch = (na == "r")?(byte)1:(byte)-1;
			}
			
				help.tweenToLocation((PrismaticJoint)daddy.joints.get("pivot_"+a), (float)(direction*-2.5));
				boos[0] = (((PrismaticJoint)daddy.joints.get("pivot_"+a)).getJointTranslation()*direction <= -2.2);
				
//				System.out.println(daddy.mass*40);
				help.tweenToRotation((RevoluteJoint) daddy.joints.get("hip_"+a), (float) -(Math.PI/2+direction*Math.PI/6));
				boos[1] = daddy.joints.get("hip_"+a).getBodyA().getAngle()*direction > 0;
				
				//move left foot forward and up
				help.tweenToLocation((PrismaticJoint)daddy.joints.get("pivot_"+na), (float)(direction*2.5));
				boos[2] = ((PrismaticJoint)daddy.joints.get("pivot_"+na)).getJointTranslation()*direction >= 2.2;

				((RevoluteJoint)daddy.joints.get("hip_"+na)).setMotorSpeed(0);
				boos[3]  =true;
				if(!searching){
					help.tweenToLocation((PrismaticJoint) daddy.joints.get("ankle_"+a), 2.5f);
					boos[4] = (((PrismaticJoint)daddy.joints.get("ankle_"+a)).getJointTranslation() >= 2.2);
					help.tweenToLocation((PrismaticJoint) daddy.joints.get("ankle_"+na), -2.5f);
					boos[5] = (((PrismaticJoint)daddy.joints.get("ankle_"+na)).getJointTranslation() <= -2.2);

					System.out.println(boos[0] +" "+ boos[1] +" "+ boos[2] +" "+ boos[3] +" "+ boos[4] +" "+ boos[5]);
					if(boos[0] && boos[1] && boos[2] && boos[3] && boos[4] && boos[5]){
						searching = true; System.out.println("!!!!!!!!!!!!!!!!!!Searching!!!!!!!!!!!!!!11");
					}
				}
				if(searching){
					if(((PrismaticJoint)daddy.joints.get("ankle_"+na)).getJointTranslation() < 2.5)
						((PrismaticJoint)daddy.joints.get("ankle_"+na)).setMotorSpeed(daddy.mass);
					else if(((PrismaticJoint)daddy.joints.get("ankle_"+a)).getJointTranslation() > -2.5)
						((PrismaticJoint)daddy.joints.get("ankle_"+a)).setMotorSpeed(daddy.mass*-100);
//					else {daddy.state = "balance"; System.out.println("balance");}
				}
				
			
		}
		private void destroyJoints(){
			for(Joint j : anchorJoint){
				world.destroyJoint(j);
			}
			anchorJoint = new ArrayList<WeldJoint>();
		}
		
		public void stopWalk(){
			destroyJoints();
			anchorFoot = "";
			daddy.state = "ready";
		}
		
	}
*/				
	public void attemptToLand(){}

	public void attemptToStand(){
		float desired_ankle_dist = 2f;
		float desired_pivot_location = 2f;
		float desired_hip_rotation = 0;
		
//		((PrismaticJoint)bs.joints.get("ankle_r")).setMotorSpeed(1f);
//		((PrismaticJoint)bs.joints.get("ankle_l")).setMotorSpeed(1f);

//		((PrismaticJoint)bs.joints.get("pivot_r")).setMotorSpeed(10f);
//		((PrismaticJoint)bs.joints.get("pivot_l")).setMotorSpeed(10f);

//		((RevoluteJoint)bs.joints.get("hip_r")).setMotorSpeed(10f);
//		((RevoluteJoint)bs.joints.get("hip_l")).setMotorSpeed(10f);

		
		bs.tweenToLocation("ankle_r", desired_ankle_dist);
		bs.tweenToLocation("ankle_l", desired_ankle_dist);

		bs.tweenToLocation("pivot_r", desired_pivot_location);
		bs.tweenToLocation("pivot_l", -desired_pivot_location);

//		bs.tweenToRotation("hip_r", desired_hip_rotation);
//		bs.tweenToRotation("hip_l", desired_hip_rotation);
		
	}	
}
