package game.pause;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeSet;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.contacts.Contact;

import abstracts.BodyData;



public class PauseManager implements ContactListener{
	public TreeSet<Pause> pauses;

	
	public PauseManager(){
		pauses = new TreeSet<Pause>();

	}
	
	
	public void preSolve(Contact contact, Manifold oldManifold){
		BodyData adata = (BodyData)contact.getFixtureA().getBody().getUserData();
		BodyData bdata = (BodyData)contact.getFixtureB().getBody().getUserData();

		if( adata != null && bdata != null
			&& adata.pause != null && bdata.pause != null 
			&& bdata.pause == adata.pause && !adata.pause.end 
			&& contact.isTouching()) contact.setEnabled(false);
	}
	@Override
	public void beginContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}
	public void endContact(Contact contact){
		BodyData A = (BodyData)contact.getFixtureA().getBody().getUserData();
		BodyData B = (BodyData)contact.getFixtureB().getBody().getUserData();
		if( A != null && B != null
		&&	A.pause != null && B.pause != null
		&& A.pause == B.pause){
			if(!A.pause.end) throw new Error("Contact shouldn't be breaking with any pause");
			A.pause.bodies.remove(A);
			A.pause.bodies.remove(B);
			if(A.pause.bodies.size() == 0) A.pause.remove = true;
		}

	}


	
	public void postSolve(Contact contact, ContactImpulse impulse){
		Body A = contact.getFixtureA().getBody();
		Body B = contact.getFixtureB().getBody();
		BodyData Ad = (BodyData)(A.getUserData());
		BodyData Bd = (BodyData)(B.getUserData());
		if(	contact.isTouching() &&	!contact.getFixtureA().isSensor() 	&& !contact.getFixtureB().isSensor()
		&&	A.getType() == BodyType.DYNAMIC	&& B.getType() == BodyType.DYNAMIC
		){
			
			/*
			 * Things I need to watch out for
			 * 
			 * Both have Pause and are the same
			 * 1) but init
			 * 		-Nothing, they are active until time step
			 * 2) When Pause is same, but is neither beggining nor ending
			 * 		-Error, sould have been taken care of in pre-sovle
			 * 3) When Pause is Same, But Ending
			 * 		-Nothing
			 * 
			 * Both Have Pause and are different
			 * 1) Both are Init
			 * 		-Consolidate
			 * 2) One is init
			 * 		-Consolidate
			 * 3) Neither Are Init
			 * 		-Theres a problem, pauses shouldn't move
			 * 4) One is Ending
			 * 		-Consolidate? or remove from old, add to not ending?
			 * 5) Both are Ending
			 * 		-Create new?
			 * 
			 * One has Pause
			 * 1) Its Init
			 * 		-Append Freind
			 * 2) Its Active
			 * 		-AppendFreind
			 * 3) Its Ending
			 * 		-Create New?
			 * 
			 * Neither has pause
			 * -Create new
			 * 
			 */
			
			
			if(	(Ad != null)
			&&	(Bd != null)
			){
				if(Ad.pause != null && Bd.pause != null){
					if(Ad.pause == Bd.pause){
						if(Ad.pause.init) System.out.print("still young, still naive");
						else if(Ad.pause.end) System.out.print("let them sort it out");
						else throw new Error("Pauses should be taken care of in the presolve");
					}else{
						if(	Ad.pause.end && Bd.pause.end) newPause(contact, impulse);
						else if((Ad.pause.init || Bd.pause.init)
							&&	(Ad.pause.end || Bd.pause.end))  consolidatePauses(Ad.pause, Bd.pause);
						else throw new Error("Pauses should not move");
					}
				}else if(!onePaused(Ad, A, Bd, B)) newPause(contact, impulse);
			}else if(!onePaused(Ad, A, Bd, B)){
				newPause(contact, impulse);
			}
		}
	}

	public boolean onePaused(BodyData Ad, Body A, BodyData Bd, Body B){
		if(Ad != null && Ad.pause != null && !Ad.pause.end){ Ad.pause.appendFreind(B); Ad.pause.active = true; return true;}
		else if(Bd != null && Bd.pause != null && !Bd.pause.end){ Bd.pause.appendFreind(A); Ad.pause.active = true; return true;}
		else return false;
	}
	
	public void newPause(Contact contact, ContactImpulse impulse){
		
		float max = Pause.findMax(impulse);
		int init = Math.round(max/100);
		
		if(init > 0 ) pauses.add(new Pause(contact, impulse, max, (pauses.size() > 0)?pauses.last().index+1:0));

	}
		
	public void time(){

		for(Pause temp : pauses){
			if(temp.active) temp.solveVelocities();

			if(temp.remove || (temp.end && temp.bodies.size() < 2)) pauses.remove(temp);
			else if(temp.timeleft < 0)	temp.unpause();
			else{ 
				temp.timeleft--; 
				System.out.println(temp.timeleft+" ticks left"); 
			}
		}
	}
	
	public void consolidatePauses(Pause A, Pause B){
		A.importPause(B);
		A.active = true;
		pauses.remove(B);
		
	}
	
}
