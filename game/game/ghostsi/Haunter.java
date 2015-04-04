package game.ghostsi;

import init.The_Game;
import minion.abstracts.MinionEntity;
import minion.abstracts.MinionModule;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;


import game.ghost.abstracts.GhostBodyAbstract;
import game.ghost.bodies.HauntBody;
import game_aspects.BodyChain;
import helpers.BodyDefCallback;
import helpers.BodyHelper;

public abstract class Haunter {
/*
 * 
 * So the way this works is....
 * A player Presses Punch
 * Haunter is created
 * -If it gets stunned || finishes action, remove
 * -If finishes creation -do the action
 * 
 * on remove, create minihaunt
 * -action is to move to creator
 * -on hit, is ready
 * 
 * 
 */
	public BodyChain haunt;
	
	public boolean manager_ready;
	public MinionEntity owner;
		
	public float currentAngle = 0;
	
	public Haunter(MinionEntity owner){
		this.owner = owner;
		manager_ready = true;
	}
	
	public void create(Vec2 position){
		if(manager_ready){
			state = "create";
			this.manager_ready = false;
			currentAngle = solveAttackAngle();
			haunt = new HauntBody(owner.ID, position);
		}
	}
	
	public abstract float solveAttackAngle();
	
	public String state;
	public int time_in_state = 0;
	public void doAI(){
		if(state == "create"){
			time_in_state++;
			if(time_in_state == 16) state = "punch"; 
		}else if(state == "punch"){
			((HauntBody)haunt).doAI();
		}
	}
	
	
}
