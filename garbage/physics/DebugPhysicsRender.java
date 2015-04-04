package physics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

import game.BabySteps;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;

import abstracts.PhysicsManager;
public class DebugPhysicsRender implements Runnable{
	
	int circle_lines;
	static BufferStrategy strat;
	Graphics2D g;
	static PhysicsManager b;
	float scale;
	Vec2 cameraPos;
	SinCosTable sct;
	boolean running;
	int cameraheight = 500;

	public DebugPhysicsRender(int circle_lines, BufferStrategy strat, PhysicsManager tb){
		this.strat = strat;
		b = tb;
//		g.setBackground(new Color(0x000000));
		this.circle_lines = circle_lines;
		scale = 5;
		cameraPos = new Vec2(100,100);
		sct = new SinCosTable();
	}
	
	public void display(){
		g = (Graphics2D) strat.getDrawGraphics();
		g.setColor(Color.black);
		g.setClip(0, 0, 500, 500);
		g.fillRect(0,0,500,500);
		int m = cameraheight/2;
		g.translate(0,m);
		g.scale(1,-1);
		g.translate(0,-m);
		Body bodies = b.getBodyList();
		int bcount = b.getBodyCount();
		int fcount;
		for(int i=0;i<bcount;i++){
			Vec2 pos = new Vec2(bodies.getPosition().x*scale+cameraPos.x,bodies.getPosition().y*scale+cameraPos.y);
//			float angle = bodies.getAngle();
			Fixture fix = bodies.getFixtureList();
			fcount = bodies.m_fixtureCount;
			for(int j=0;j<fcount;j++){
				if(fix.isSensor()){
					g.setColor(new Color(0xFF0000));
				}else{
					g.setColor(new Color(0xFFFF00));
				}
				draw(bodies.getAngle(), pos, fix.getShape());
			}
			bodies = bodies.getNext();
		}
		g.dispose();
		strat.show();
		
	}
	
	public void run(){
		//For Each I need: Shape, color, location
		running = true;
		while(running){
			display();
			try { Thread.sleep(33); } catch (Exception e) {}
		}
		
	}
	
	public void end(){
		running = false;
	}
	
	/*
	 * -x + 500
	 * 
	 * 
	 * 
	 */
	
	
	private void draw(float bodyangle, Vec2 bodyloc, Shape shape){
		if(shape.getType() == ShapeType.CIRCLE){
			CircleShape retype = (CircleShape)shape;
			float radius = shape.m_radius*scale;
			Vec2 midpoint = new Vec2(bodyloc.x+retype.m_p.x*scale, bodyloc.y+retype.m_p.y*scale);
			Vec2 currentloc;
			Vec2 nextloc;
			g.translate(midpoint.x, midpoint.y);
			g.rotate(bodyangle);
			for(int i=0;i<circle_lines;i++){
				currentloc = sct.calculate(i);
				nextloc = sct.calculate(i+1);
				g.drawLine(
					Math.round(currentloc.x*radius),
					Math.round(currentloc.y*radius),
					Math.round(nextloc.x*radius),
					Math.round(nextloc.y*radius)
				);
			}
			g.drawLine(0, 0, Math.round(radius), 0);
			g.rotate(-bodyangle);
			g.translate(-midpoint.x, -midpoint.y);
		}else if(shape.getType() == ShapeType.POLYGON){
			PolygonShape retype = (PolygonShape)shape;
			Vec2[] points = retype.getVertices();
			int vcount = retype.getVertexCount();
			g.translate(bodyloc.x, bodyloc.y);
			g.rotate(bodyangle);
			for(int i=0;i<vcount;i++){
				int j = (i+1)%(vcount);
				g.drawLine(
					Math.round(points[i].x*scale), Math.round(points[i].y*scale),
					Math.round(points[j].x*scale), Math.round(points[j].y*scale)
				);
			}
			g.rotate(-bodyangle);
			g.translate(-bodyloc.x, -bodyloc.y);
		}
	}

	private class SinCosTable{
		Vec2[] a;
		public float pi;
		public SinCosTable(){
			a = new Vec2[circle_lines];
			a[0] = new Vec2(1,0);
			for(int i = 0;i<circle_lines;i++){
				a[i] = new Vec2((float)Math.cos(i*(2*Math.PI)/circle_lines), (float)Math.sin(i*(2*Math.PI)/circle_lines));
			}
			pi = (float)Math.PI;
		}
		public Vec2 calculate(int number){
			number = number%(circle_lines);
			return a[number];
		}
	}//End sinCosinTable
	
}//End Class
