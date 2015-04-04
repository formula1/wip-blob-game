package game_aspects;

import minion.abstracts.UserData;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.contacts.Contact;


public class Level implements ContactListener{
	
	
	public Level(){
		BodyDef grounddef = new BodyDef();
		grounddef.type = BodyType.STATIC;
		grounddef.angle = 0;
		grounddef.position.set(0, 0);
		grounddef.userData = new UserData(this);

		
	}

	@Override
	public void beginContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}
	
}
