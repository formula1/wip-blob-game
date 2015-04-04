package interactables;

import init.The_Game;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.joints.WeldJointDef;

import game_aspects.BodyChain;

public class MinWeldChain extends BodyChain{

	public MinWeldChain(Vec2 position) {
		super(position);
		Body a = The_Game.help.createFromArguments(position.add(new Vec2(40,-40)), The_Game.help.circle(2.5f), BodyType.DYNAMIC);
		WeldJointDef djd =new WeldJointDef();
		djd.initialize(main, a, main.getWorldCenter());
		djd.dampingRatio = 1f;
		djd.frequencyHz = 10;
		The_Game.world.createJoint(djd);
		
		Body b = The_Game.help.createFromArguments(position.add(new Vec2(40,40)), The_Game.help.circle(2.5f), BodyType.DYNAMIC);
		djd.initialize(main, b, main.getWorldCenter());
		The_Game.world.createJoint(djd);
		
//		djd.initialize(a, b, a.getWorldCenter());
//		The_Game.world.createJoint(djd);

		
		Body c = The_Game.help.createFromArguments(position.add(new Vec2(-40,40)), The_Game.help.circle(2.5f), BodyType.DYNAMIC);
		djd.initialize(main, c, main.getWorldCenter());
		The_Game.world.createJoint(djd);
		
//		djd.initialize(a, c, a.getWorldCenter());
//		The_Game.world.createJoint(djd);

//		djd.initialize(b, c, b.getWorldCenter());
//		The_Game.world.createJoint(djd);


	}

	@Override
	protected Body getTheMain(Vec2 position) {
		// TODO Auto-generated method stub
		return The_Game.help.createFromArguments(position, The_Game.help.circle(2.5f), BodyType.DYNAMIC);
	}
	

}
