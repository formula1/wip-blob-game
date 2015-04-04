package minion.module;

import init.The_Game;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.particle.ParticleDef;
import org.jbox2d.particle.ParticleGroupDef;
import org.jbox2d.particle.ParticleType;

import minion.abstracts.MinionEntity;
import minion.abstracts.MinionModule;

public class BlobBodyModuleParticle extends MinionModule{

	public BlobBodyModuleParticle(MinionEntity owner) {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean doAI() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void die() {
		// TODO Auto-generated method stub
		
	}

}
