package game.ghost.abstracts;

import helpers.JointCallback;
import init.The_Game;
import intelligence.IntelInterface;

import minion.abstracts.MinionEntity;
import minion.abstracts.MinionModule;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

import entity_managers.CollisionManager;

import assets.FixtureData;

import game_aspects.BodyChain;

public abstract class GhostBodyAbstract extends MinionEntity{

	/*
	 * The Way any Ghost works is
	 * So the way this works is....
	 * A player presses Ghost
	 * -if ghost is not created, it will create them
	 * Ghost is created
	 * -If it gets stunned - remove
	 * -If finishes creation -do the action
	 */
	public int time_to_wait;
	public boolean stunned = false;
	public boolean flag_for_deletion = false;
		
	public GhostBodyAbstract(Vec2 position) {
		super(position);
		The_Game.jhelp.downTheTree(main, new JointCallback(new Object()){

			@Override
			public void processBody(Body body) {
				Fixture f = body.getFixtureList();
				while(f != null){
					FixtureData fd = new FixtureData();
					if(f.getUserData() != null) fd = (FixtureData)f.getUserData();
					
					fd.game_specific = "ghost";
					fd.collision_manager.add(new CollisionManager(10){

						@Override
						public void preSolve(Contact contact,
								Manifold point_manifold, boolean is_fix_a) {
							Fixture other;
							Fixture thisone;
							if(is_fix_a){
								other = contact.getFixtureB();
								thisone = contact.getFixtureA();
							}else{
								other = contact.getFixtureA();
								thisone = contact.getFixtureB();
							}
							if(other.getBody().getType() != BodyType.DYNAMIC){
								FixtureData fd;
								if(( fd = (FixtureData)other.getUserData()) == null || fd.game_specific != "ghost"){
									contact.setEnabled(false);
								}
							}
						}

						@Override
						public void beginContact(Contact contact,
								boolean is_fix_a) {
							Fixture other;
							Fixture thisone;
							if(is_fix_a){
								other = contact.getFixtureB();
								thisone = contact.getFixtureA();
							}else{
								other = contact.getFixtureA();
								thisone = contact.getFixtureB();
							}
							if(other.getBody().getType() != BodyType.DYNAMIC){
								FixtureData fd;
								if(( fd = (FixtureData)other.getUserData()) == null || fd.game_specific != "ghost"){
									contact.setEnabled(false);
								}
							}
							
						}

						@Override
						public void endContact(Contact contact, boolean is_fix_a) {
							Fixture other;
							Fixture thisone;
							if(is_fix_a){
								other = contact.getFixtureB();
								thisone = contact.getFixtureA();
							}else{
								other = contact.getFixtureA();
								thisone = contact.getFixtureB();
							}
							if(other.getBody().getType() != BodyType.DYNAMIC){
								FixtureData fd;
								if(( fd = (FixtureData)other.getUserData()) == null || fd.game_specific != "ghost"){
									contact.setEnabled(false);
								}
							}
							
						}

						@Override
						public void postSolve(Contact contact,
								ContactImpulse impulse, boolean is_fix_a) {
							Fixture other;
							Fixture thisone;
							if(is_fix_a){
								other = contact.getFixtureB();
								thisone = contact.getFixtureA();
							}else{
								other = contact.getFixtureA();
								thisone = contact.getFixtureB();
							}
							if(contact.isEnabled() && other.getBody().getType() == BodyType.KINEMATIC && impulse.normalImpulses[0] > 1000){
								stunned = true;
							}
						}
						
					});
					
					f = f.getNext();
				}
			}
			
		});
		
		time_to_wait = fadeTime();
	}
	
	public void die(){
		
	}
	
	public abstract int fadeTime();
	
	public boolean doAI(){
		if(flag_for_deletion && time_to_wait == 0) die();
		if(time_to_wait > 0){ time_to_wait--; return false;}
		boolean ret = true;
		solveAI();
		for(MinionModule m : modules) if(!m.doAI()) ret = false;
		
		if(ret){
			flag_for_deletion = true;
			time_to_wait = fadeTime();
		}
		
		return ret;
	}

}
