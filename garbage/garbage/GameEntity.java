package garbage;

import graphic.GameGraphic;

import java.awt.Graphics2D;
import java.util.Collection;
import java.util.HashMap;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import abstracts.GameRenderer.BodyGraphic;
import abstracts.GameRenderer.FixtureGraphic;
import abstracts.GameRenderer.VelocityGraphic;
import abstracts.entites.GameElement;

public abstract class GameEntity extends GameElement{

	/*
	 * To keep things in 60 fps I got to make sure I reduce the amount of "checks" for game entities
	 * To make sure cretaing game entities is simple, 
	 * 
	 * 
	 * I used to believe Libraries should only be in certian areas, but when it comes down to it, its just pointers
	 * -Maybe each class is completely loaded as a seperate "instance" but hopefully its just pointing
	 * 
	 * Each entitiy has different states
	 * -When an entity switches to a different state, logic ensues and etc
	 * -Each state actually displays all the aspects
	 * Each game entity has a draw
	 * -Draws can be animations
	 * 
	 * 
	 */
	
	public HashMap<String,VelocityGraphic> velocitygraphics;
	
	public boolean hasVelocityGraphics(){
		return (velocitygraphics.size() > 0);
	}
	
	public Collection<VelocityGraphic> getVelocityGraphics(){
		return velocitygraphics.values();
	}
	
	
}
