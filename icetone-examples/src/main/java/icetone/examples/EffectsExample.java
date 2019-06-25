package icetone.examples;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetKey;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

import emitter.Emitter;
import emitter.ogre.OGREParticleConfiguration;
import emitter.ogre.OGREParticleEmitter;
import emitter.ogre.OGREParticleLoader;
import emitter.ogre.OGREParticleScript;
import emitter.ogre.OGREParticleScript.BillboardType;
import icetone.controls.buttons.PushButton;
import icetone.controls.containers.Frame;
import icetone.controls.text.Label;
import icetone.core.BaseElement;
import icetone.core.ElementContainer;
import icetone.core.Screen;

/**
 * This example demonstrates various special effects.
 */
public class EffectsExample extends SimpleApplication {

	public static void main(String[] args) {
		EffectsExample app = new EffectsExample();
		app.start();
	}

	private Frame frm;

	@Override
	public void simpleInitApp() {

		flyCam.setMoveSpeed(10);
		flyCam.setDragToRotate(true);

		
		/*
		 * We are only using a single screen, so just initialise it (and you
		 * don't need to provide the screen instance to each control).
		 * 
		 * It is passed to the buildExample method in this way to help
		 * ExampleRunner so this example can be run from there and as a
		 * standalone JME application
		 */
		buildExample(new Screen(this));

	}

	protected void buildExample(ElementContainer<?, ?> screen) {

		/*
		 * The "Icetone Emitter" packages requires the OGRE particle script
		 * compatibility library. So we need to register the loader for this. We
		 * do this manually here, but of course you can also register it in the
		 * usual JME3 way
		 */
		assetManager.registerLoader(OGREParticleLoader.class, "particle");
		
        stateManager.attach(new ParticleViewportAppstate());

		OGREParticleConfiguration opc = assetManager
				.loadAsset(new AssetKey<OGREParticleConfiguration>("Effects.particle"));
		OGREParticleScript script = opc.getScript("Burst");

		frm = new Frame();
		frm.getContentArea().addElement(new Label("Test Label"))
				.addElement(new PushButton("Go!").onMouseReleased(evt -> runScript(script, screen, evt.getElement())));

		/* Build screen */
		screen.showElement(frm);

	}

	protected void runScript(OGREParticleScript script, ElementContainer<?, ?> screen, BaseElement source) {
		Node node = new Node();
		script.setBillboardType(BillboardType.POINT);

		for (OGREParticleEmitter i : script.getEmitters()) {
			final Emitter emitter = i.createEmitter(assetManager);
			emitter.setEnabled(true);
			emitter.initialize(assetManager);
			node.addControl(emitter);
		}
//		node.setLocalTranslation(source.getAbsoluteX(), source.getAbsoluteY(), 0);
//		 node.move(2, 0, 0);
		node.scale(10f / screen.getWidth(), 10f / screen.getHeight(), 1f);
		 node.move((source.getAbsoluteX() / screen.getWidth() * - 1 )+ 0.5f, ( source.getAbsoluteY() / screen.getHeight() * - 1) + 0.5f, 1f);
//		node.scale(1f);
//		particlesNode.attachChild(node);
//		rootNode.attachChild(node);
//		 getRootNode().attachChild(node);
		 screen.getScreen().getGUINode().attachChild(node);
//		stateManager.getState(ParticleViewportAppstate.class).particlesNode.attachChild(node);
	}
	
	class ParticleViewportAppstate extends AbstractAppState {
		private Node particlesNode;

		@Override
		public void initialize(AppStateManager stateManager, Application app) {
			super.initialize(stateManager, app);

	        // Create a new cam for the gui
	        Camera particlesCam = new Camera(settings.getWidth(), settings.getHeight());
//	        particlesCam.setParallelProjection(true);
	        ViewPort particlesViewPort = renderManager.createPostView("Particles Default", particlesCam);
//	        particlesViewPort.setClearFlags(false, false, false);
	        particlesNode = new Node("Particles Node");
//	        particlesNode.setQueueBucket(Bucket.Gui);
//	        particlesNode.setCullHint(CullHint.Never);
	        particlesViewPort.attachScene(particlesNode);



			Box b = new Box(1, 1, 1);
			Geometry geom = new Geometry("Box", b);

			Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
			mat.setColor("Color", ColorRGBA.Red);
			geom.setMaterial(mat);
			particlesNode.attachChild(geom);
		}

		@Override
		public void cleanup() {
			super.cleanup();
			renderManager.removePostView("Particles Default");
		}

		@Override
		public void update(float tpf) {
			super.update(tpf);
			particlesNode.updateLogicalState(tpf);
		}

		@Override
		public void render(RenderManager rm) {
			super.render(rm);
			particlesNode.updateGeometricState();
		}
		
	}

}
