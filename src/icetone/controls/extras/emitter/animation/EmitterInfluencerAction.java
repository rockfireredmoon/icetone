/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icetone.controls.extras.emitter.animation;

import icetone.controls.extras.emitter.ElementEmitter;
import icetone.controls.extras.emitter.Influencer;
import icetone.framework.animation.TemporalAction;

/**
 *
 * @author t0neg0d
 */
public class EmitterInfluencerAction extends TemporalAction {
	Influencer influencer;
	
	@Override
	protected void begin() {
		setTime(1f);
		setDuration(0);
		((ElementEmitter)quad).removeInfluencer(influencer.getClass());
		((ElementEmitter)quad).addInfluencer(influencer);
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
	
	public void setInfluencer(Influencer influencer) {
		this.influencer = influencer;
		setDuration(1);
	}
}
