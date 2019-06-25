/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icetone.framework.core;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;

import icetone.core.scene.SceneElement;
import icetone.framework.animation.TemporalAction;

/**
 *
 * @author t0neg0d
 */
public interface Animatable extends SceneElement {

	

	void setIgnoreMouse(boolean ignoreMouse);

	void setIsMovable(boolean isMovable);

	boolean getIgnoreMouse();

	boolean getIsMovable();

	void addAction(TemporalAction action);

	boolean getContainsAction(TemporalAction action);
}
