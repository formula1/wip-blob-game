package minion.clever;

import java.awt.Color;
import java.awt.Graphics2D;

import graphic.GameGraphic;
import init.CopyRender;
import init.The_Game;
import minion.abstracts.MinionEntity;

import org.jbox2d.collision.WorldManifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.WeldJoint;
import org.jbox2d.dynamics.joints.WeldJointDef;

public class Suction{
	WeldJoint j;
	Contact contact;
	Vec2 contactpoint;
	public Body other;
	public Suction(Contact contact, Body other){
		this.contact = contact;
		this.other = other;
	}
	
	public boolean time(float power){
		if(j == null){
			establish();
		}
		
		//The problem seems to be when its created the same time it should be destroyed
		//When its created the ReactionForce is 0 because it was just created
		//
		Vec2 react = new Vec2();
		j.getReactionForce(1/60f, react);

		/*
		 * So the max reaction should be if the mass is moving at a specific velocity
		 * But more importantly is the speed we're pulling at
		 */
		if(react.lengthSquared() > power*60){
			The_Game.world.destroyJoint(j);
			return false;
		}else return true;
	}
	
	public Vec2 getContactPoint(){
		if(j != null) return contactpoint;
		else{
			establish();
			return contactpoint;
		}
	}
	
	public void breakIt(){
		The_Game.world.destroyJoint(j);
	}
	
	private void establish(){
		WorldManifold m = new WorldManifold();
		contact.getWorldManifold(m);
		WeldJointDef wjd = new WeldJointDef();
		wjd.initialize(contact.getFixtureA().getBody(), contact.getFixtureB().getBody(), m.points[0].clone());
		
		contactpoint = m.points[0].clone();
		
		CopyRender.addGraphic(new GameGraphic(3,m.points[0].clone()){

			@Override
			public void draw(Graphics2D g) {
				Vec2 wj = (Vec2)userdata;
				g.setColor(new Color(0xFFFF7800, true));
				g.translate(wj.x-5, wj.y-5);
				g.fillOval(0, 0, 10, 10);
			}
		});
		
		CopyRender.addGraphic(new GameGraphic(3,contact.getFixtureB().getBody().getWorldCenter()){

			@Override
			public void draw(Graphics2D g) {
				Vec2 wj = (Vec2)userdata;
				g.setColor(new Color(0x78FFFF00, true));
				g.translate(wj.x-5, wj.y-5);
				g.fillOval(0, 0, 10, 10);
			}
		});

		CopyRender.addGraphic(new GameGraphic(3,contact.getFixtureA().getBody().getWorldCenter()){

			@Override
			public void draw(Graphics2D g) {
				Vec2 wj = (Vec2)userdata;
				g.setColor(new Color(0xFFFFFF00, true));
				g.translate(wj.x-5, wj.y-5);
				g.fillOval(0, 0, 10, 10);
			}
		});

		
		
		wjd.frequencyHz = 30;
		wjd.dampingRatio =1;
		wjd.collideConnected = true;

		j = (WeldJoint)The_Game.world.createJoint(wjd);
		System.out.println("created"+(j == null));

	}
}