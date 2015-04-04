package init;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;



import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.JointType;
import org.jbox2d.dynamics.joints.PrismaticJoint;
import org.jbox2d.dynamics.joints.WeldJoint;
import org.jbox2d.particle.ParticleColor;
import org.jbox2d.particle.ParticleGroup;


import abstracts.Game;
import assets.SinCosTable;
import assets.Vec2c;
import graphic.Camera;
import graphic.GameGraphic;
import graphic.GameRenderer;
import graphic.GraphicManager;

public class CopyRender extends GameRenderer{
	private static ArrayList<GameGraphic> imt;

	
	public CopyRender() {
		sct = new SinCosTable(12);
		imt = new ArrayList<GameGraphic>();
	}

	int framenum = 0;
	Long tizle = 0L;
	public int frames_per_second = 0;
	
	
	public static void addGraphic(GameGraphic adding){
		imt.add(adding);
	}

	@Override
	public TreeSet<GameGraphic> getGraphics(final GraphicManager gr) {
		World pm = The_Game.world;
		setCamera(gr);
		int bcount = pm.getBodyCount();
		int jcount = pm.getJointCount();

//		System.out.println("body count"+bcount);
		
		TreeSet<GameGraphic> ret = new TreeSet<GameGraphic>();
		
		Long this_time = System.nanoTime();
		if(this_time - tizle>= 1000*1000*1000){
			long trans = (int)(this_time - tizle);
			long f = ((long)framenum) * 1000*1000*1000;
			frames_per_second = (int)Math.round(f/trans);
			tizle = this_time;;
			framenum = 0;
		}else framenum++;
		
		Joint joints = pm.getJointList();
		while(joints != null){
			
			if(joints.getType() == JointType.WELD){
				ret.add(new GameGraphic(2, joints){
					public void draw(Graphics2D g) {
						WeldJoint wj = (WeldJoint)userdata;
						Vec2 point = new Vec2(); 
						wj.getAnchorA(point);
						g.setColor(new Color(0xFF0000FF, true));
						g.translate(point.x-5, point.y-5);
						g.fillOval(-1, -1, 2, 2);
					}
					
				});
			}
			joints = joints.getNext();
		}
/*		
		Contact contacts = pm.getContactList();
		while(contacts != null){
			ret.add(new GameGraphic(5, contacts){
				public void draw(Graphics2D g) {
					Contact wj = (Contact)userdata;
					
					g.setColor(new Color(0xFF00FF00, true));
					g.translate(wj.getBodyA().getWorldCenter().add(wj.getLocalAnchorA()).x, wj.getBodyA().getWorldCenter().add(wj.getLocalAnchorA()).y);
					g.fillOval(0, 0, 10, 10);

					
					g.setColor(new Color(0xFF0000FF, true));
					g.translate(wj.getBodyA().getWorldCenter().add(wj.getLocalAnchorA()).x, wj.getBodyA().getWorldCenter().add(wj.getLocalAnchorA()).y);
					g.fillOval(0, 0, 10, 10);
				}
				
				});
			contacts = contacts.getNext();
		}
*/
		
		
		ret.add(new GameGraphic(10, false){
			@Override
			public void draw(Graphics2D g) {
				g.setColor(new Color(1,1,1,.5f));
				g.fillRect(0, 0, 100, 100);
				g.setColor(new Color(0x000000));
//				g.drawString("Game FPS: "+((The_Game)gr.game).frames_per_second, 10, 10);
				g.drawString("Graphic FPS: "+frames_per_second, 10, 30);
				
			}
			
		});
		
		ret.add(new GameGraphic(4, new ParticleWrapper(pm.getParticleCount(), pm.getParticleRadius(), pm.getParticlePositionBuffer())){
			public void draw(Graphics2D g) {
				for (int i = 0; i < ((ParticleWrapper)userdata).count; i++) {
					Vec2 center = ((ParticleWrapper)userdata).pos[i];
					if(Float.isNaN(center.x) || Float.isNaN(center.y)) continue;
					g.translate(center.x, center.y);
					g.scale(((ParticleWrapper)userdata).radius, ((ParticleWrapper)userdata).radius);
					g.setColor(new Color(0xFFFFFF));
					g.fillOval(-1, -1, 2, 2);
					g.scale(1/((ParticleWrapper)userdata).radius, 1/((ParticleWrapper)userdata).radius);
					g.translate(-center.x, -center.y);
				}
			}
		});

		
		
		for(Body bodies = pm.getBodyList();bodies != null;bodies = bodies.getNext()){
			final Vec2 bc = bodies.getWorldCenter();
			ret.add(new GameGraphic(4, bodies){
				public void draw(Graphics2D g) {
					Vec2 position = ((Body)userdata).getWorldCenter();
					float angle = ((Body)userdata).getAngle();
					g.setColor(new Color(0xFF0000));
					g.translate(position.x, position.y);
					g.rotate(angle);
					g.drawLine(0, 0, 2, 2);
					g.fillOval(-1, -1, 2, 2);
				}
				
			});
			
			for(Fixture fs = bodies.getFixtureList();fs != null;fs = fs.getNext()){
				int laying = 3;
				if(fs.m_filter.categoryBits == 0 || fs.isSensor())
					laying = 1;

				ret.add(new GameGraphic(laying, fs){

					@Override
					public void draw(Graphics2D g) {
						Fixture fix = ((Fixture)userdata);
						if(fix.m_filter.categoryBits == 0)
							g.setColor(new Color(0x00FFFF));
						else if(fix.m_isSensor)
							g.setColor(new Color(0xFF0000));
						else
							g.setColor(new Color(0x0000FF));
							

						if(fix.getType() == ShapeType.CIRCLE ){
							CircleShape retype = (CircleShape)fix.getShape();
							Vec2 npos = retype.m_p.add(bc);
							g.translate(npos.x, npos.y);
							g.rotate(fix.m_body.getAngle());

							float radius = retype.m_radius;
							Vec2 currentloc;
							Vec2 nextloc;
							for(int i=0;i<sct.na;i++){
								currentloc = new Vec2c(sct.calculate(i)).asVec2();
								nextloc = new Vec2c(sct.calculate(i+1)).asVec2();
								g.drawLine(
									Math.round(currentloc.x*radius),
									Math.round(currentloc.y*radius),
									Math.round(
											nextloc.x*
											radius
									),
									Math.round(nextloc.y*radius)
								);
							}
							g.drawLine(0, 0, Math.round(radius), 0);

						}else if(fix.getType() == ShapeType.POLYGON){
							g.translate(fix.m_body.getWorldCenter().x, fix.m_body.getWorldCenter().y);
							g.rotate(fix.m_body.getAngle());
							PolygonShape retype = (PolygonShape)fix.getShape();
							Vec2[] points = retype.getVertices();
							int vcount = retype.getVertexCount();
							for(int i=0;i<vcount;i++){
								int j = (i+1)%(vcount);
								g.drawLine(
									Math.round(points[i].x), Math.round(points[i].y),
									Math.round(points[j].x), Math.round(points[j].y)
								);
							}
						}else if(fix.getType() == ShapeType.EDGE){
							EdgeShape retype = (EdgeShape)fix.getShape();
							g.drawLine(
								Math.round(retype.m_vertex1.x), Math.round(retype.m_vertex1.y),
								Math.round(retype.m_vertex2.x), Math.round(retype.m_vertex2.y)
							);
							
						}else if(fix.getType() == ShapeType.CHAIN){
							g.translate(fix.m_body.getWorldCenter().x, fix.m_body.getWorldCenter().y);
							g.rotate(fix.m_body.getAngle());
							ChainShape retype = (ChainShape)fix.getShape();
							for(int i=0;i<retype.m_vertices.length-1;i++){
								g.drawLine(
									Math.round(retype.m_vertices[i].x), Math.round(retype.m_vertices[i].y),
									Math.round(retype.m_vertices[i+1].x), Math.round(retype.m_vertices[i+1].y)
								);
							}
						}

					}
					
				});
//				if(fs.getNext() == null) System.out.println(ret.size());
			}
			/*
			 * Do I put the Graphic Information inside of the User Object?
			 * 
			 */
		}
		
		ret.addAll(imt);
		imt = new ArrayList<GameGraphic>();

		return ret;
	}

	public void setCamera(GraphicManager gr){
		float buffer = 20;
		Vec2 level_bounds = new Vec2(500,500);
		float minimum_camera_bounds = 100;
		float maximum_camera_bounds = 500;
		

		
		Vec2 cur_pos = new Vec2();
		Vec2 minimums = null;
		Vec2 maximums = new Vec2();
		minimums = Vec2.max(cur_pos, level_bounds.mul(-1)).clone();
		maximums = Vec2.min( cur_pos,  level_bounds).clone();
		
		//get Midpoint between extremes
		Vec2 Midpoint = maximums.add(minimums);
		Midpoint = Midpoint.mul(1f/2f);
		
		
		Vec2 camera_bounds = new Vec2();
		
//		camera_bounds.y = camera_bounds.x = Math.max(camera_bounds.x+buffer*2, camera_bounds.y+buffer*2);
		
		Vec2c v = new Vec2c(0, 0);
		for(int i=0;i<The_Game.ps.length;i++)
			v = v.add(The_Game.ps[i].main.getWorldCenter().mul(1/The_Game.ps.length));
		gr.curCam = new Camera(v, new Vec2c(500,500), 0f);

	}
	
	class ParticleWrapper{
		public int count;
		public float radius;
		public Vec2[] pos;
		
		public ParticleWrapper(int i, float r, Vec2[] poss){
			this.count = i;
			this.radius = r;
			this.pos = poss;
		}
	}


}
