package garbage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.java.games.input.Component;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;

import physics.DebugPhysicsRender;

import controller.Player;
import controller.PlayerListener;

public class BabySteps implements Runnable, PlayerListener{
	float timestep;
	int velIterations;
	int posIterations;
	World world;
	boolean running;
	Body[] playerBodies;
	HashMap<String, Float>[] play;
	
	public BabySteps(int num_of_players){
		play = new HashMap[num_of_players];
		Vec2 gravity = new Vec2(0,-10.0f);
		world = new World(gravity);

		BodyDef grounddef = new BodyDef();
		grounddef.position.set(0.0f,-10.0f);
		Body groundbody = world.createBody(grounddef);
		PolygonShape gBox = new PolygonShape();
		gBox.setAsBox(50.0f, 10.0f);
		groundbody.createFixture(gBox,0.0f);

		
		grounddef.type = BodyType.DYNAMIC;
		grounddef.position.set(5.0f,50.0f);
		Body ball = world.createBody(grounddef);
		gBox.setAsBox(1.0f, 1.0f);
		
		FixtureDef boxing = new FixtureDef();
		boxing.shape = gBox;
		boxing.density = 1.0f;
		boxing.friction = 0.3f;
		
		ball.createFixture(boxing);

		grounddef.type = BodyType.DYNAMIC;
		grounddef.position.set(4.0f,40.0f);
		Body bill = world.createBody(grounddef);
		gBox.setAsBox(1.0f, 1.0f);
		
		boxing = new FixtureDef();
		boxing.shape = gBox;
		boxing.density = 1.0f;
		boxing.friction = 0.3f;
		
		bill.createFixture(boxing);
		grounddef.type = BodyType.DYNAMIC;
		grounddef.position.set(6.0f,60.0f);
		Body bull = world.createBody(grounddef);
		gBox.setAsBox(1.0f, 1.0f);
		
		boxing = new FixtureDef();
		boxing.shape = gBox;
		boxing.density = 1.0f;
		boxing.friction = 0.3f;
		
		bull.createFixture(boxing);

		
		grounddef.type = BodyType.DYNAMIC;
		playerBodies = new Body[num_of_players];
		CircleShape p = new CircleShape();
		FixtureDef cir = new FixtureDef();

		for(int i=0;i<num_of_players;i++){
			grounddef.position.set((float)(Math.random()*5)+5f,(float)(Math.random()*5)+5f);
			p.setRadius((float)(Math.random()*2.5f)+2.5f);
			playerBodies[i] = world.createBody(grounddef);
			cir.shape = p;
			cir.density = 4.0f;
			cir.friction = 0.2f;
			playerBodies[i].createFixture(cir);
			play[i] = new HashMap<String, Float>();
		}
		System.out.println(num_of_players);
		timestep = 1.0f/60.0f;
		velIterations = 6;
		posIterations = 2;
		
	}
	
	
	
	public void setInputs(int pn, HashMap<String, Float> pi){
			play[pn] = pi;
	}
	
	
	
	public void playerInput(){
		Vec2 vel;
		Vec2 forceapp;
		
		for(int i=0;i<playerBodies.length;i++){
			vel = playerBodies[i].getLinearVelocity();
			forceapp = new Vec2();
			float desvel;
			if(play[i].get("up") > 0.5f && play[i].get("down") < 0.5f){
				desvel = 5 - vel.y;
			}else if(play[i].get("up") < 0.5f && play[i].get("down") > 0.5f){
				desvel = -5 - vel.y;
			}else{
				desvel = 0;
			}
			forceapp.y = (float) (playerBodies[i].getMass() * desvel); //f = mv/t
			
			if(play[i].get("left") > 0.5f && play[i].get("right") < 0.5f){
				desvel = -5 - vel.x;
			}else if(play[i].get("right") > 0.5f && play[i].get("left") < 0.5f){
				desvel = 5 - vel.x;
			}else{
				desvel = 0;
			}
			forceapp.x = (float) (playerBodies[i].getMass() * desvel); //f = mv/t
			playerBodies[i].applyLinearImpulse( forceapp, playerBodies[i].getWorldCenter() );
		}

	}
	
	public void run(){
		running = true;
		while(running){
			playerInput();
			world.step(timestep,velIterations,posIterations);
//			dt.display();
/*			Body bodies = world.getBodyList();
			int bcount = world.getBodyCount();
			for(int i=0;i<bcount;i++){
				if(bodies.m_type == BodyType.DYNAMIC){
					Vec2 pos = bodies.getPosition();
					float angle = bodies.getAngle();
					Vec2 vels = bodies.getLinearVelocity();
					System.out.println("Position: x="+pos.x+" || y="+pos.y);
//					System.out.println("Angle: "+angle);
//					System.out.println("Velocity: x="+vels.x+" || y="+vels.y);
				}
				bodies = bodies.getNext();
			} */
			try {
				Thread.sleep(Math.round(timestep*1000));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public Body getBodyList(){
		return world.getBodyList();
	}
	public int getBodyCount(){
		return world.getBodyCount();
	}
	
	
	public void end(){
		running = false;
	}



	@Override
	public void push(int pn, Object o) {
		// TODO Auto-generated method stub
		
	}
	
}
