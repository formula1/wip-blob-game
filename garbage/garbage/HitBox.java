package garbage;

import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;

public abstract class HitBox {
	public String name;
	public Shape shape;
	public Vec2 offset;
	public float angle;
	
	public abstract short overrideLevel();
	public abstract short overrideResistance();
	public abstract void hit();	
	public abstract void receive();
	
}
