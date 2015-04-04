package input;

import java.util.ArrayList;

public class InputBuffer {

	int player_number;
	public ArrayList<Input> inputs;
	private byte framebuffer =0;
	
	public InputBuffer(int playernumber){
		player_number = playernumber;
	}
	
	public void setInputListeners(String[] Listeners){
		
	}
	
	public void addInput(String button, float extent){
		if(framebuffer == 3) System.out.println("that person is a fast one");
		inputs.add(new Input(button, extent > .5, framebuffer));
		framebuffer = 3;
	}
	
	public boolean checkInput(String bufferCheck){
		/*
		 * Press
		 * v
		 * 
		 * Release
		 * ^
		 * 
		 * Nothing Inbetween
		 * !
		 * 
		 * []
		 * 
		 * 
		 * What are we looking for?
		 * 
		 * If we're looking for a chain, within 4 frames
		 * a,b,x,y
		 * 
		 * If we're looking for rapid fire,
		 * We want on off, on off, on off
		 * 3b
		 * 
		 * 
		 * If we're looking for two at the same time
		 * We want within X frames both are on (generally 2 or 1)
		 * a+b
		 * 
		 * On and hold
		 * <A
		 * >A
		 * 
		 * 
		 * 
		 * If we're looking for charge
		 * -We want to see how long theyve been holding something for
		 * -we won't worry about charge for now...
		 * 
		 * 
		 * We may also check conditions
		 * -See if something is currently on/off
		 * (<A)?
		 * 
		 * 
		 * I could do input listener
		 * -Then while each of the inputs are done, either a specific input is set to true or false
		 * 
		 * 
		 */
		bufferCheck = bufferCheck.toLowerCase();
		String[] chain = bufferCheck.split(",");
		int currentInput;
		for(int i=chain.length-1;i>0;i--){
			if(chain[i].contains("+"))
			
		}
		
		
		return false;
	}
	public void nextframe(){
		if(framebuffer == 0) 		inputs.clear();
		else if(framebuffer >= 0) 	framebuffer--;
	}
	
	private class Input{
		public String button;
		public boolean onoff;
		public byte framediff;
		public Input(String but, boolean oo, byte framediff){
			button = but;
			onoff = oo;
			this.framediff = framediff;
		}
	}
	
	public abstract class InputListener{
		public String sequence;
		public boolean stillpossible = false;
		public int nextframeLimit = 0;
		public String[] check;
		public ArrayList<String> multi = new ArrayList<String>();
		public ArrayList<String> tohold = new ArrayList<String>();
		public int hitnumber = 0;
		public int i;
		
		public InputListener(String sequence){
			this.sequence = sequence;
			this.check = sequence.split(",");
			this.i = 0;
		}
		
		public void newInput(String button, boolean onoff){
			if(tohold.contains(button) && !onoff){
				fail();
			}
			if(check[i].contains("+") && onoff && check[i].contains(button) && !multi.contains(button)){
				if(multi.size() == check[i].split("+"))
			}
		}
		
		public void nextInChain(){
			i++;
			if(i> check.length){ success(); return; }
			
		}
		
		public void nextFrame(){
			if(nextframeLimit == 0){ System.out.println("toolate"); fail(); }
			else if(nextframeLimit > 0) nextframeLimit--;
		}
		
		public void fail(){
			i = -1;
			nextframeLimit = -1;
			nextInChain();
		}
		
		public abstract void success();

		public abstract void failure();

	}
	
}
