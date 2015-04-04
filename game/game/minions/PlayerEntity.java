package game.minions;

import minion.abstracts.MinionEntity;
import minion.abstracts.MinionModule;
import minion.module.FeetModule;
import minion.module.FistModule;
import minion.module.TorsoModule;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

import assets.FixtureData;



import entity_managers.CollisionManager;

import helpers.BodyDefCallback;
import helpers.BodyHelper;
import helpers.JointCallback;
import init.The_Game;

public class PlayerEntity extends MinionEntity{
	
	int player_number;
	float body_width = 5f;
	byte walking;
	
	
	public PlayerEntity(int player_number, Vec2 position){
		super(position);
		this.player_number = player_number;

		The_Game.jhelp.downTheTree(main, new JointCallback(null){
			public void processBody(Body body) {
				Fixture f = body.getFixtureList();
				while(f != null){
					System.out.println("cb: "+f.getFilterData().categoryBits+", mb: "+Integer.toBinaryString(f.getFilterData().maskBits));
					f = f.getNext();
				}
			}
		});
		
	}
	
	@Override
	protected Body getTheMain(Vec2 position) {
		body_width = 5;
		
		return 	The_Game.help.createFromArguments(
					position, 
					BodyHelper.rectangle(new Vec2(body_width,body_width/2)),
					BodyType.DYNAMIC,
					new BodyDefCallback(){
						public BodyDef bodyDefCallback(BodyDef bod){
							bod.fixedRotation = true;
							return bod;
						}
					}
				);
	}

	public MinionModule[] getModules(){
		MinionModule[] ret = new MinionModule[3];
		ret[0] = new TorsoModule(body_width/2, this, main){

			public void die() {
				
			}

			protected boolean solveDuckAI() {
				// TODO Auto-generated method stub
				return (The_Game.players[player_number].buttons.get("down") > .5);
			}
		};
		ret[1] = new FeetModule(this, 5f, 10f, body_width*2){
			protected boolean solveJumpAI() {
				return (The_Game.players[player_number].buttons.get("up") > .5);
			}
			protected byte solveWalkAI() {
				byte ret = 0;
				if(The_Game.players[player_number].buttons.get("left") > .5
				&& The_Game.players[player_number].buttons.get("right") < .5
				) ret = -1;
				else if(The_Game.players[player_number].buttons.get("right") > .5
				&& The_Game.players[player_number].buttons.get("left") < .5
				) ret = 1;
				return ret;
			}
			public void die() {}
		};
		ret[2] = new FistModule(this, ((TorsoModule)ret[0]).chest, body_width){

			@Override
			public float solvePunchDirection() {
				int walk = 0;
				byte upd = 0;
				if(The_Game.players[player_number].buttons.get("left") > .5) walk -= 1;
				if(The_Game.players[player_number].buttons.get("right") > .5) walk += 1;
				
				if(The_Game.players[player_number].buttons.get("up") > .5
				&& The_Game.players[player_number].buttons.get("down") < .5
				) upd += 1;
				else if(The_Game.players[player_number].buttons.get("down") > .5
				&& The_Game.players[player_number].buttons.get("up") < .5
				) upd -= 1;
				
				float ret = 0;
				if(walk==0 && upd==0) ret = walking*(float)Math.PI/2;
				else if(upd == 1) ret = 0-walk*(float)Math.PI/4f;
				else if(upd == 0) ret = -walk*(float)Math.PI/2f;
				else if(walk==-1 && upd == -1) ret = (float)Math.PI*3f/4f;
				else if(walk==0 && upd == -1) ret = walking*(float)Math.PI;
				else if(walk==1 && upd == -1) ret = (float)Math.PI*-3f/4f;
				return ret;
			}

			@Override
			public byte solvePunchType() {
				// TODO Auto-generated method stub
				return (byte)((The_Game.players[player_number].buttons.get("a") > .5)?0:-1);
			}
		};
		return ret;
	}
	
	@Override
	public void solveAI() {
		/*
		 * If single press 0
		 * If rapid presses 1
		 * If direction + attack within the same time 2
		 * -three frame difference
		 * 
		 * I'm not going to worry about it for now,
		 * Instead I'm going to create a new class called input buffer, where I specify my inputs as string
		 * -It gets registered in real time
		 * Then if success, it will go to that input
		 * -If no
		 * 
		 */
		if(The_Game.players[player_number].buttons.get("left") > .5
		&& The_Game.players[player_number].buttons.get("right") < .5
		) walking = 1;
		else if(The_Game.players[player_number].buttons.get("right") > .5
		&& The_Game.players[player_number].buttons.get("left") < .5
		) walking = -1;
	}

	@Override
	public void die() {
		// TODO Auto-generated method stub
		
	}

}
