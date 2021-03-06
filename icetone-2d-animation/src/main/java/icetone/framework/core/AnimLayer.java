/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icetone.framework.core;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;

import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.layout.ScreenLayoutConstraints;

/**
 *
 * @author t0neg0d
 */
public class AnimLayer extends BaseElement implements Control {
	private Map<String, AnimElement> animElements = new LinkedHashMap<String, AnimElement>();
	private List<AnimElement> tempElements = new LinkedList();
	private float childZOrder = -1;
	private float zOrderStepMid = 0.001f;
	private Spatial spatial;
	private boolean isPaused;

	public AnimLayer(BaseScreen screen) {
		super(screen);
		setConstraints(ScreenLayoutConstraints.fill);
		setAsContainerOnly();
		setIgnoreMouse(true);
		setAffectZOrder(false);
	}

	public void addAnimElement(String UID, AnimElement el) {
		if (childZOrder == -1)
			childZOrder = getLocalTranslation().z;
		el.setElementKey(UID);
		el.setParentLayer(this);
		animElements.put(UID, el);
		el.setPositionZ(childZOrder);
		childZOrder -= zOrderStepMid;
		attachChild(el);
	}

	public AnimElement removeAnimElement(String UID) {
		AnimElement el = animElements.remove(UID);
		if (el != null)
			el.removeFromParent();
		return el;
	}

	public void removeAnimElement(AnimElement el) {
		animElements.remove(el.getElementKey());
		el.removeFromParent();
	}

	public void bringAnimElementToFront(AnimElement el) {
		animElements.remove(el.getElementKey());
		animElements.put(el.getElementKey(), el);
		el.removeFromParent();
		attachChild(el);
		resetZOrder();
	}

	public void sendAnimElementToBack(AnimElement el) {
		tempElements.clear();
		for (AnimElement ae : animElements.values()) {
			if (ae != el)
				tempElements.add(ae);
		}
		animElements.clear();
		animElements.put(el.getElementKey(), el);
		for (AnimElement ae : tempElements) {
			animElements.put(ae.getElementKey(), ae);
		}
		resetZOrder();
	}

	@Override
	public void setInitialized(BaseScreen screen) {
		// TODO Auto-generated method stub
		super.setInitialized(screen);
	}

	private void resetZOrder() {
		childZOrder = getLocalTranslation().z;
		for (AnimElement ae : animElements.values()) {
			ae.setPositionZ(childZOrder);
			ae.resetZOrder();
			childZOrder -= zOrderStepMid;
		}
	}

	@Override
	public Control cloneForSpatial(Spatial spatial) {
		return this;
	}

	@Override
	public void setSpatial(Spatial spatial) {
		this.spatial = spatial;
	}

	public void pause() {
		this.isPaused = true;
	}

	public void resume() {
		this.isPaused = false;
	}

	public boolean getIsPaused() {
		return this.isPaused;
	}

	@Override
	public void update(float tpf) {
		if (!isPaused) {
			try {
				for (AnimElement el : animElements.values()) {
					el.update(tpf);
				}
			} catch (Exception ex) {
			}
		}
	}

	@Override
	public void render(RenderManager rm, ViewPort vp) {

	}
}
