/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icetone.framework.animation;

import icetone.core.scene.TextureRegion;
import icetone.framework.core.AnimQuadData;

/**
 *
 * @author t0neg0d
 */
public class SetRegionAction extends TemporalAction {
	TextureRegion tr;
	
	@Override
	protected void begin() {
		((AnimQuadData)quad).setTextureRegion(tr);
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
	
	public void setTextureRegion(TextureRegion tr) {
		this.tr = tr;
	}
	
	@Override
	public SetRegionAction clone() {
		SetRegionAction sra = new SetRegionAction();
		sra.setTextureRegion(tr);
		
		return sra;
	}
}