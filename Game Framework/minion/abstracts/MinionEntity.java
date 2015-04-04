package minion.abstracts;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import abstracts.BodyData;

import helpers.JointCallback;
import init.The_Game;
import intelligence.IntelInterface;
import game_aspects.BodyChain;

public abstract class MinionEntity extends BodyChain implements IntelInterface{

	public int ID;
	public MinionModule[] modules;
	public boolean in_air = true;
	public int filterindex = -1;
	
	public MinionEntity(Vec2 position) {
		super(position);
		The_Game.intelmanager.createIntelligence(this);
		main.setUserData(new BodyData(this));
		modules = getModules();
		mass = this.getMass();
		
		filterindex = (Integer)The_Game.jhelp.downTheTree(main, new JointCallback(filterindex){
			public void processBody(Body body) {
				System.out.println("The int:"+((Integer)this.additive).compareTo(-1));
				
				if(((Integer)this.additive).compareTo(-1) == 0) this.additive = The_Game.mhelp.createNewMask(body, 0x0000, true);
				else The_Game.mhelp.setMask(body, (Integer)this.additive);
			}
		});
		
	}
	public abstract MinionModule[] getModules();

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return ID;
	}

	@Override
	public void setID(int ID) {
		// TODO Auto-generated method stub
		this.ID = ID;
	}
	
	public int compareTo(IntelInterface i){
		return this.ID - i.getID();
	}

	public abstract void solveAI();
	
	@Override
	public boolean doAI() {
		solveAI();
		for(MinionModule m : modules) m.doAI();
		return true;
	}

}
