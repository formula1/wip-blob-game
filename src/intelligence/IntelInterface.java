package intelligence;

public interface IntelInterface extends Comparable<IntelInterface>{

	public abstract int getID();
	public abstract void setID(int ID);
	public abstract boolean doAI();
	public abstract void die();
	

}
