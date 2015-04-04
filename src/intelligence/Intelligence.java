package intelligence;

import init.The_Game;

import org.jbox2d.common.Vec2;

public abstract class Intelligence implements IntelInterface{
/*
 * States For Every Entity
 * 
 * Walk
 * Jump
 * Duck
 * Attack
 * 
 * 
 * 
 */
	public int ID;
	
	public Intelligence(){
		The_Game.intelmanager.createIntelligence(this);
	}	
	
	public void setID(int ID){
		this.ID = ID;
	}
	public int getID(){
		return ID;
	}
	
	public int compare(IntelInterface i){
		return this.ID-i.getID();
	}
}
