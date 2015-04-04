package garbage;

import game.BS_to_Game;
import helpers.Box2dHelper;
import helpers.Ownership;

import java.util.HashMap;
import java.util.Map.Entry;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import projectiles.Basic_Projectile;

import abstracts.Game;
import abstracts.Logic_Entity;
import abstracts.PlayerEntity;

public class PlayerRotatingFeetEntity extends PlayerEntity{
	String state;
	public float precent_damage = 0;
	public HashMap<String, Body> bodies;
	Fixture[] swordslash;
	BS_to_Game game;
	
	RevoluteJoint turret;
	boolean fired = false;
	
	
	public int stun = 0;
	float mass;
	public int pn;
	PE_info_ground ground;
	
	/*
	 * 
	 * Create Combat Boxes
	 * -Combat Boxes "don't exist" until a specific move is done
	 * 		-Based off Contact filtering mostly
	 * -On hit, apply forces, set effects
	 * 		-I may want to create effect Listeners of some sort So that I can apply effects as a routine effect
	 * 		-Also I may have to multithread the listeners simply because there may be alot of effects that take place
	 * 		in terms of multiplication and such
	 * 		-It would be chaining, where all the listsners for a specific chain would be in its own thread
	 * 		-Checks PreSolve
	 * 
	 * Interesting projectiles
	 * 	-Boomerang
	 * 	-ball and chain -
	 * 	-Homing - very important
	 * 
	 */
	
	
	public PlayerRotatingFeetEntity(BS_to_Game game, int player_number, Vec2 position){

		super(game, player_number);
		System.out.println(player_number);

		precent_damage = 0;
		bodies = new HashMap<String,Body>();
		
		bodies.put("feet", Box2dHelper.createPlayerEntityCircle(new Ownership(pn, this, this), position.add(new Vec2(0,1)), 1f, false));
		bodies.put("rest", Box2dHelper.createPlayerEntityRectangle(new Ownership(pn, this, this), position.add(new Vec2(0,1.5f)), new Vec2(4,3.5f), true));
		bodies.put("turret", Box2dHelper.createPlayerEntityRectangle(new Ownership(pn, this, this), position.add(new Vec2(0,3.5f)), new Vec2(3,1f), false));
		
		
		/*
		 * Punch
		 * 
		 * need to create a Punch with filtermask (nonexsistant)
		 * 
		 * -On press, 
		 * -set the Filtermask to player

		 * Create a presolve condition
		 * -If its button set velocities
		 * -If it struck before, no worries
		 * 
		 * 
		 * functions:
		 * -create attack boxes (shape, offsets, on hit)
		 * -turn attack box on (string)
		 * -turn attack box off (string)
		 * 
		 * 
		 */
		
		getMass();
		
		RevoluteJointDef rjd = new RevoluteJointDef();
		rjd.bodyA = bodies.get("rest");
		rjd.localAnchorA = new Vec2(0, -3f);
		rjd.bodyB = bodies.get("feet");
		rjd.collideConnected = false;
		rjd.enableMotor = true;
		rjd.maxMotorTorque = mass*2*(float)Math.PI*360;
		rjd.motorSpeed = 0;
		ground = new PE_info_ground((RevoluteJoint) game.world.createJoint(rjd));
		
		rjd = new RevoluteJointDef();
		rjd.bodyA = bodies.get("rest");
		rjd.localAnchorA = new Vec2(-1.5f, 0);
		rjd.bodyB = bodies.get("turret");
		rjd.collideConnected = false;
		rjd.enableMotor = true;
		rjd.maxMotorTorque = bodies.get("turret").getMass()*2*(float)Math.PI*360;
		rjd.motorSpeed = 0;
		turret = (RevoluteJoint)game.world.createJoint(rjd);

		
	}
		
	public Vec2 getCenter(){
		return bodies.get("feet").getWorldCenter();
	}
	

	private boolean attacking = false;
	public void time(int frame_num){
		if(Game.players[pn].get("left") > 0.5f && Game.players[pn].get("right") < 0.5f){
			ground.ground_movement.setMotorSpeed(mass*1);
		}else if(Game.players[pn].get("right") > 0.5f && Game.players[pn].get("left") < 0.5f){
			ground.ground_movement.setMotorSpeed(mass*-1);
		}else{
			ground.ground_movement.setMotorSpeed(0);
		}
		
		if(			Game.players[pn].get("a") > 0.5f
		&&			!ground.in_air 						&& 		!ground.air_attempt
		){
			bodies.get("feet").applyLinearImpulse(new Vec2(0,mass*10), bodies.get("feet").getWorldCenter());
			ground.air_attempt = true;
		}
		if(Game.players[pn].get("b") > 0.5f && !fired){
			new Basic_Projectile(this);
			fired = true;
		}else if(Game.players[pn].get("b") < 0.5f){
			fired = false;
		}

		if(Game.players[pn].get("x") > 0.5f && !attacking){
			if(Game.players[pn].get("up") > 0.5f){
				swordslash[0].setFilterData(Box2dHelper.setFilter(pn, swordslash[0].getFilterData(), true));
			}else if(Game.players[pn].get("left") > 0.5f){
				swordslash[1].setFilterData(Box2dHelper.setFilter(pn, swordslash[1].getFilterData(), true));
			}else if(Game.players[pn].get("down") > 0.5f){
				swordslash[2].setFilterData(Box2dHelper.setFilter(pn, swordslash[2].getFilterData(), true));
			}else if(Game.players[pn].get("right") > 0.5f){
				swordslash[3].setFilterData(Box2dHelper.setFilter(pn, swordslash[3].getFilterData(), true));
			}
			
			
			attacking = true;
		}else if(Game.players[pn].get("x") < 0.5f){
			for(int i=0;i<4;i++){
				swordslash[i].setFilterData(Box2dHelper.setFilter(pn, swordslash[i].getFilterData(), false));
			}
			attacking = false;
		}

		
		if(			Game.players[pn].get("up") > 0.5f		&&		Game.players[pn].get("down") < 0.5f
		){
			turret.setMotorSpeed(bodies.get("turret").getMass()*2*(float)Math.PI);
		}else if(	Game.players[pn].get("up") < 0.5f		&&		Game.players[pn].get("down") > 0.5f){
			turret.setMotorSpeed(-bodies.get("turret").getMass()*2*(float)Math.PI);
		}else{
			turret.setMotorSpeed(0);
		}

		
	}
	
	
	public class PE_info_ground{
		int floor;
		RevoluteJoint ground_movement;
		boolean air_attempt;
		boolean in_air;
		
		public PE_info_ground(RevoluteJoint j){
			ground_movement = j;
			floor = 0;
			air_attempt= false;
			in_air = true;
		}
	}

	@Override
	public void preSolve(Contact contact, String fixture_name,
			boolean is_fix_a, Ownership other) {
		Fixture p_bod = (is_fix_a)?contact.m_fixtureA:contact.m_fixtureB;
		Fixture n_bod = (is_fix_a)?contact.m_fixtureB:contact.m_fixtureA;
		if(contact.isTouching() && fixture_name == "attack"){
			contact.setEnabled(false);
			if(n_bod.getUserData() != null){
				
				if(other.stored_info == "attack"){
					//clash
				}else{
					
					Vec2 dif = p_bod.m_body.getWorldCenter().sub(contact.getManifold().points[0].localPoint);					
					/*
					 * Find the angle the contact is at
					 * Apply a specific velocity depending on angle
					 * -For my purposes, opp and adj porportioins
					 * 
					 * 
					 */
					
					
					
					Vec2 normal_vel = dif.mul(100).mul(1/dif.length());
					
					float ran = .50f - .25f*(float)Math.random();
				}
				
				
			}
		}
		
		
	}

	@Override
	public void beginContact(Contact contact, String fixture_name,
			boolean is_fix_a, Ownership other) {
		Body p_bod = (is_fix_a)?contact.m_fixtureA.getBody():contact.m_fixtureB.getBody();
		Body n_bod = (is_fix_a)?contact.m_fixtureB.getBody():contact.m_fixtureA.getBody();
		if(fixture_name == "feet"){
			if(p_bod.getWorldCenter().y > contact.getManifold().points[0].localPoint.y){
				if(ground.air_attempt){
					ground.air_attempt = false;
				}
				ground.floor++;
				ground.in_air = false;
			}
		}
		
	}

	@Override
	public void endContact(Contact contact, String fixture_name,
			boolean is_fix_a, Ownership other) {
		Body p_bod = (is_fix_a)?contact.m_fixtureA.getBody():contact.m_fixtureB.getBody();
		if(fixture_name == "feet"){
			if(p_bod.getWorldCenter().y > contact.getManifold().points[0].localPoint.y){
				ground.floor--;
				if(ground.floor <= 0){
					ground.in_air = true;
					ground.air_attempt = false;
				}
			}
		}
		
	}

	@Override
	public void postSolve(Contact contact, String fixture_name,
			boolean is_fix_a, Ownership other) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void die() {
		// TODO Auto-generated method stub
		
	}

		
}//end playerEntity