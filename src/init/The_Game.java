package init;

import intelligence.Intelligence;
import intelligence.IntelligenceManager;
import interactables.BlobMobile;
import interactables.AxleMobile;
import interactables.BlobRadialMobile;
import interactables.ConstantVolumeMobile;
import interactables.ElasticRingMobile;
import interactables.LineJointChanges;
import interactables.LineJointMobile;
import interactables.MinWeldChain;
import interactables.WheelBlobMobile;

import java.util.ArrayList;

import entity_managers.FixtureContactListener;
import entity_managers.JBoxGameManager;
import game.minions.BlobEntity;
import game.minions.PlayerEntity;
import game.pause.GlobalPause;
import game.pause.PauseManager;
import graphic.GameRenderer;
import helpers.BodyDefCallback;
import helpers.BodyHelper;
import helpers.JointHelper;
import helpers.MaskHelper;


import minion.abstracts.MinionEntity;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.particle.ParticleGroup;
import org.jbox2d.particle.ParticleGroupDef;
import org.jbox2d.particle.ParticleType;

import terrain.ElasticNecklace;
import terrain.ElasticRing;
import terrain.ElasticRope;
import terrain.FixedRope;
import game.ghost.bodies.HauntBody;

public class The_Game extends JBoxGameManager{
	public static boolean pause = false;
	public static BodyHelper help;
	public static JointHelper jhelp;
	public static MaskHelper mhelp;
	public static IntelligenceManager intelmanager;
	public static MinionEntity[] ps;
//	public static PauseManager pm = new PauseManager();
	
	public The_Game() {
		super(new Vec2(0,-100));
		intelmanager = new IntelligenceManager();
		help = new BodyHelper(world,.3f,1f){
			public BodyDef bodyDefCallback(BodyDef body) {
				return body;
			}
			public FixtureDef fixDefCallback(FixtureDef fixture) {
				return fixture;
			}
		};
		jhelp = new JointHelper(world);
		mhelp = new MaskHelper(world);
		rendbodsp = new ArrayList<Body>();
	    world.setParticleRadius(.5f);

	}

	public GameRenderer getRenderer(){
		return new CopyRender();
	}
	
	public void ready() {
		ps = new MinionEntity[players.length];
		help.createFromArguments( new Vec2(0,-50), BodyHelper.rectangle(new Vec2(500,5)), BodyType.STATIC);
		for(int i=0;i<players.length;i++){
//			ps[i] = new HauntBody(i, new Vec2(400,60));
			ps[i] = new BlobEntity(new Vec2(400,60), i);

//			ps[i] = new PlayerEntity(i,new Vec2(i*10+5f, 16f));
		}

	}


//==========================PLAYER METHODS=======================
	public void playerEvent(int player_number, String input, Float value) {
		if(input.contentEquals("start")){
			pause = !pause;
		}
	}

	public void playerEnter(int playernumber) {
		
	}

	public void playerLeave(int playernumber) {
		
	}

//============================================================
//==================AI=========================================
	
	public void time(long time, float hard){
		if(!pause) super.time(time, hard);
	}
	
	public int frame = 0;
	public int rendplus =200;
	public int rendneg =200;
	public ArrayList<Body> rendbodsp;
	
	int angleplus = 0;

	public void doAI(long time) {
		frame++;
		if(frame == 1) 	help.createFromArguments( new Vec2(0, 20), BodyHelper.circle(2), BodyType.DYNAMIC );
		if(frame == 1) 	new FixedRope(new Vec2(-300,120));
		if(frame == 1) 	new ElasticRope(new Vec2(-200,120));
		if(frame == 1) 	new ElasticNecklace(new Vec2(-100,120));
		if(frame == 1) 	new ElasticRing(new Vec2(0,120));
		if(frame == 1) 	new BlobMobile(new Vec2(200,120));
		
//		if(frame == 1) 	new WheelBlobMobile(new Vec2(300,120));
//		if(frame == 1) 	new ConstantVolumeMobile(new Vec2(300,120));
//		if(frame == 1) 	new BlobRadialMobile(new Vec2(400,120));
//		if(frame == 1)	new MinWeldChain(new Vec2(400, 120));

//		pm.time();
		intelmanager.doAI();
//		if(supered != null && supered.doAI()) supered = null;
		
		while(ps[0].main.getWorldCenter().x > angleplus){
			angleplus+=400;
			PolygonShape pss = new PolygonShape();
			Vec2[] points = new Vec2[3];
			
			points[0] = new Vec2(0,400);
			points[1] = new Vec2(0,0);
			points[2] = new Vec2(400,0);
			pss.set(points, 3);
			
			help.createFromArguments(new Vec2(angleplus, -(angleplus)), pss, BodyType.STATIC);

			System.out.println("cc");
		}
		
		if(ps[0].main.getWorldCenter().x > rendplus-300){
//Create four platforms of varied vertical heights and varied angles from -45 to +45
			
			while(ps[0].main.getWorldCenter().x > rendplus-400){
				rendbodsp.add(help.createFromArguments(new Vec2(rendplus+50,200+(float)((.5-Math.random())*Math.random()*100)), help.rectangle(new Vec2(50,20)), BodyType.STATIC
//				rendbodsp.add(help.createFromArguments(new Vec2(rendplus+50,-50), help.rectangle(new Vec2(50,20)), BodyType.STATIC
				, new BodyDefCallback(){
					public BodyDef bodyDefCallback(BodyDef d){
						d.angle = (float)(Math.PI/4 - Math.random()*Math.PI/2);
						return d;
					}
				}));
				
				rendplus+=100;

			}
		}else if(ps[0].main.getWorldCenter().x < rendplus-400 && rendbodsp.size() > 0){
			while(ps[0].main.getWorldCenter().x < rendplus-400){
				world.destroyBody(rendbodsp.get(rendbodsp.size()-1));
				rendbodsp.remove(rendbodsp.size()-1);
				rendplus -= 100;
			}
		}
	}

	
	public void deleteAssociated(Object o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ContactListener[] getContactListeners() {
		// TODO Auto-generated method stub
		return new ContactListener[]{new FixtureContactListener()};
	}




}
