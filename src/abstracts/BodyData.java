package abstracts;

import game.pause.Pause;
import intelligence.IntelInterface;
import intelligence.Intelligence;

public class BodyData {

	public IntelInterface intel;
	public Pause pause;
	
	public BodyData(){}

	
	public BodyData(IntelInterface i){
		intel = i;
	}
	
}
