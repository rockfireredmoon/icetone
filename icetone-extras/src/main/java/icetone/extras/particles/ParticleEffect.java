package icetone.extras.particles;

import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;

import emitter.Emitter;
import emitter.ogre.OGREParticleConfiguration;
import emitter.ogre.OGREParticleEmitter;
import emitter.ogre.OGREParticleScript;
import icetone.core.BaseScreen;
import icetone.effects.Effect;
import icetone.effects.IEffect;

public class ParticleEffect extends Effect {

	private String uri;

	public ParticleEffect(String uri) {
		this.uri = uri;
	}

	@Override
	public boolean isConflict(IEffect effect) {
		return false;
	}

	public String getUri() {
		return uri;
	}

	public ParticleEffect setUri(String uri) {
		this.uri = uri;
		return this;
	}

	@Override
	public void update(float tpf) {
		String configuration = uri;
		String script = null;
		int index = uri.indexOf('#');
		if (index != -1) {
			configuration = uri.substring(0, index);
			script = uri.substring(index + 1);
		}
		BaseScreen screen = element.getScreen();
		AssetManager assetManager = screen.getApplication().getAssetManager();
		OGREParticleConfiguration cgh = assetManager
				.loadAsset(new AssetKey<OGREParticleConfiguration>(configuration));
		if (script == null)
			script = cgh.getScriptNames().iterator().next();
		OGREParticleScript oscript = cgh.getScript(script);
		if (oscript == null)
			throw new IllegalArgumentException("No particle script " + uri);

		Node node = new Node();
//		cfg.setBillboardType(BillboardType.POINT);

		for (OGREParticleEmitter i : oscript.getEmitters()) {
			final Emitter emitter = i.createEmitter(assetManager);
			emitter.setEnabled(true);
			emitter.initialize(assetManager);
			node.addControl(emitter);
		}
		node.scale(0.01f, 0.01f, 1f);
		screen.getGUINode().attachChild(node);
		
		setIsActive(false);
	}

}
