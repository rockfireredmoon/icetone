/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icetone.controls.extras.emitter.animation;

import icetone.controls.extras.emitter.ElementEmitter;
import icetone.core.Element;
import icetone.framework.animation.TemporalAction;

/**
 *
 * @author t0neg0d
 */
public class EmitterEnableAction extends TemporalAction {
	Element target = null;
	boolean enable = true;
	boolean destroyOnRemove = false;
	
	@Override
	protected void begin() {
		setTime(1f);
		setDuration(0);
		((ElementEmitter)quad).setIsActive(enable);
	}
	
	@Override
	protected void update(float percent) {  }
	
	@Override
	protected void end() {  }
	
	@Override
	public void restart() {
		setTime(0);
		setComplete(false);
		setDuration(1);
		reset();
	}
	
	public void setEnableEmitter() {
		enable = true;
		setDuration(1);
	}
	public void setDisableEmitter() {
		enable = false;
		setDuration(1);
	}
	public void setDestroyOnRemove(boolean destroy) {
		this.destroyOnRemove = destroy;
		setDuration(1);
	}
}
