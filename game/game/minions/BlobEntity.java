package game.minions;

import java.awt.Color;
import java.awt.Graphics2D;

import game.pause.GlobalPause;
import graphic.GameGraphic;
import helpers.FixtureDefCallback;
import init.CopyRender;
import init.The_Game;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import minion.abstracts.MinionEntity;
import minion.abstracts.MinionModule;
import minion.module.BlobBodyModule;
import minion.module.BlobBodyModuleNS;
import minion.module.BlobBodyModuleNSFollow;
import minion.module.BlobBodyModuleNSRA;
import minion.module.BlobBodyModuleNSWelded;
import minion.module.BlobBodyModuleNSWheel;
import minion.module.BlobBodyModuleSkin;
import minion.module.CircleBodyModule;

public class BlobEntity extends MinionEntity{

	public int player_number;
	
	public BlobEntity(Vec2 position, int player_number) {
		super(position);
		this.player_number = player_number;
	}

	@Override
	public void die() {
		// TODO Auto-generated method stub
	}

	@Override
	public MinionModule[] getModules() {
		return new MinionModule[]{
			new BlobBodyModuleNSRA(this, 7){
				public Vec2 solveWalkAI(){
					Vec2 ret = new Vec2();
					if(The_Game.players[player_number].buttons.get("left") > .5
					&& The_Game.players[player_number].buttons.get("right") < .5
					) ret.x = -1;
					else if(The_Game.players[player_number].buttons.get("right") > .5
					&& The_Game.players[player_number].buttons.get("left") < .5
					) ret.x = 1;
					
					if(The_Game.players[player_number].buttons.get("up") > .5
					&& The_Game.players[player_number].buttons.get("down") < .5
					) ret.y = 1;
					else if(The_Game.players[player_number].buttons.get("down") > .5
					&& The_Game.players[player_number].buttons.get("up") < .5
					) ret.y = -1;
					if(ret.length() == 0) return ret;
//					ret.normalize();

					return ret;
				}

				@Override
				public boolean solveJumpAI() {
					return (The_Game.players[player_number].buttons.get("a") > .5);
				}

				@Override
				public boolean solvePushAI() {
					// TODO Auto-generated method stub
					return (The_Game.players[player_number].buttons.get("a") > .5);
				}
			}
		};
	}

	GlobalPause p;
	@Override
	public void solveAI() {
		
/**		if(The_Game.players[player_number].buttons.get("a") > .5f && p == null){
			p = new GlobalPause(The_Game.world, 60);
		}
*/		if(p != null && p.doAI()) p = null;
		
		CopyRender.addGraphic(new GameGraphic(3,main.getWorldCenter()){

			@Override
			public void draw(Graphics2D g) {
				Vec2 wj = (Vec2)userdata;
				g.setColor(new Color(0xFF00FF00, true));
				g.translate(wj.x-5, wj.y-5);
				g.fillOval(0, 0, 10, 10);
			}
		});

	}

	@Override
	protected Body getTheMain(Vec2 position) {
		// TODO Auto-generated method stub
		return The_Game.help.createFromArguments(position, The_Game.help.circle(7), BodyType.DYNAMIC,
				new FixtureDefCallback(){
					public FixtureDef fixDefCallback(FixtureDef f){
						f.friction = 1;
						f.density = 10;
						return f;
					}
				}
		);
	}

}
