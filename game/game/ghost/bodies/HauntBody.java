package game.ghost.bodies;

import game.ghost.abstracts.GhostBodyAbstract;
import helpers.BodyDefCallback;
import init.The_Game;
import minion.abstracts.MinionModule;
import minion.module.FistModule;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;


public class HauntBody extends GhostBodyAbstract{
	int player_number;
	float body_width = 5f;
	byte walking;

	
	
	public HauntBody(int player_number, Vec2 position) {
		super(position);
		this.player_number = player_number;
	}

	protected Body getTheMain(Vec2 position) {
		PolygonShape ps = new PolygonShape();
		Vec2[] points = new Vec2[5];
		points[0] = new Vec2(-10, 10);
		points[1] = new Vec2(-10, 0);
		points[2] = new Vec2(0,-10);
		points[3] = new Vec2(10, 0);
		points[4] = new Vec2(10,10);
		
		ps.set(points, points.length);
		
		return 	The_Game.help.createFromArguments(
				position, 
				ps,
				BodyType.STATIC,
				new BodyDefCallback(){
					public BodyDef bodyDefCallback(BodyDef bod){
						bod.fixedRotation = true;
						return bod;
					}
				}
			);
	}

	public void die() {
		
	}

	public MinionModule[] getModules() {
		return new MinionModule[]{
			new FistModule(this, main, 40){

				@Override
				public float solvePunchDirection() {
					// TODO Auto-generated method stub
					int walk = 0;
					int upd = 0;
					if(The_Game.players[player_number].buttons.get("left") > .5) walk -= 1;
					if(The_Game.players[player_number].buttons.get("right") > .5) walk += 1;
					
					if(The_Game.players[player_number].buttons.get("up") > .5) upd += 1;
					if(The_Game.players[player_number].buttons.get("down") > .5) upd -= 1;
					
					
					if(walk==0 && upd==0) walk = walking;
					float ret = (float)Math.atan2(upd, walk);
					ret -= (float)Math.PI/2;
					return ret;
				}

				@Override
				public byte solvePunchType() {
					// TODO Auto-generated method stub
					return (byte)((The_Game.players[player_number].buttons.get("a") > .5)?1:-1);
				}
				
			}
		};
	}

	public void solveAI() {
	}

	@Override
	public int fadeTime() {
		return 30;
	}
	
}
