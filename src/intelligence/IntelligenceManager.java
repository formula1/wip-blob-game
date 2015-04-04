package intelligence;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import abstracts.BodyData;



public class IntelligenceManager {

	private int curID = 0;
	public TreeSet<IntelInterface> intels;
	
	public IntelligenceManager(){
		intels = new TreeSet<IntelInterface>(new Comparator<IntelInterface>(){
			public int compare(IntelInterface a, IntelInterface b){
				return a.getID() - b.getID();
			}
		});
	}
	public IntelInterface createIntelligence(IntelInterface intel){
		intel.setID(curID);
		curID++;
		intels.add(intel);
		return intel;
	}

	public void doAI(){
		for(IntelInterface i : intels) i.doAI();
	}

	
}
