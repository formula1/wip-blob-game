package minion.abstracts;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import intelligence.IntelInterface;
import game_aspects.BodyChain;

public abstract class MinionModule extends BodyChain implements IntelInterface{

	protected MinionEntity owner;
	
	public MinionModule(MinionEntity owner) {
		super(owner.main);
		this.owner = owner;
		// TODO Auto-generated constructor stub
	}
	public MinionModule(MinionEntity owner, Body connection) {
		super(connection);
		this.owner = owner;
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return owner.ID;
	}

	@Override
	public void setID(int ID) {
		owner.ID = ID;
	}
	
	public int compareTo(IntelInterface i){
		return this.owner.ID - i.getID();
	}

	@Override
	public Body getTheMain(Vec2 position) {
		// TODO Auto-generated method stub
		return owner.main;
	}

}
