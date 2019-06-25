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
public class MoveTexToAction extends TemporalAction {

	private float x, y;
	
	@Override
	protected void begin() {
		((AnimQuadData)quad).setTCOffsetX(x);
		((AnimQuadData)quad).setTCOffsetY(y);
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
	
	public void setPosition (float x, float y) {
		this.x = x;
		this.y = y;
		setDuration(1);
	}
	
	@Override
	public MoveTexToAction clone() {
		MoveTexToAction mta = new MoveTexToAction();
		mta.setPosition(x, y);
		return mta;
	}
}
