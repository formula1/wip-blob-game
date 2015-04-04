package game_aspects;

import helpers.JointCallback;
import init.The_Game;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

public abstract class BodyChain {

	public Body main;
	public float mass;

	
	public BodyChain(Vec2 position){
		main = getTheMain(position);
	}
	public BodyChain(Body main){
		this.main = main;
	}

	
	protected abstract Body getTheMain(Vec2 position);

	public float getMass(){
		
		JointCallback ret = new JointCallback(new Float(0)){
			public void processBody(Body body){
				this.additive = (body.getMass() + (Float)this.additive);
			}
		};
		
		The_Game.jhelp.downTheTree(main, ret);
		
		return (Float)ret.additive;
	}

}
