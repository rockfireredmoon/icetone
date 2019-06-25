/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icetone.framework.animation;

import icetone.framework.core.AnimQuadData;

/**
 *
 * @author t0neg0d
 */
public class HideAction extends TemporalAction {
	@Override
	protected void begin() {
		((AnimQuadData)quad).hide();
		setDuration(0);
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
}