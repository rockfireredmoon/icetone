/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icetone.controls.util;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.core.Element;
import icetone.core.Screen;
import icetone.core.layout.LUtil;
import icetone.core.utils.UIDUtil;

/**
 *
 * @author t0neg0d
 */
public class ModalBackground extends Element {
	private boolean useSeparateAlpha = true;
	ColorRGBA resetColor = new ColorRGBA(0.5f,0.5f,0.5f,0.5f);
	ColorRGBA bgColor = new ColorRGBA();
	float resetAlpha = 0.5f;
	float alpha = 0.5f;
	
	public ModalBackground(Screen screen) {
		super(
			screen,
			UIDUtil.getUID(),
			Vector2f.ZERO,
			LUtil.LAYOUT_SIZE,
			Vector4f.ZERO,
			null//screen.getStyle("Common").getString("whiteImg")
		);
		setEffectZOrder(false);
		setPriority(ZPriority.NORMAL);
		bgColor.set(resetColor);
		this.getElementMaterial().setColor("Color", bgColor);
	}
	
	public void setBackgroundColor(ColorRGBA color, boolean useSeparateAlpha) {
		this.useSeparateAlpha = useSeparateAlpha;
		if (!useSeparateAlpha)
			this.bgColor.set(color);
		else
			this.bgColor.set(color.r, color.g, color.b, alpha);
		this.getElementMaterial().setColor("Color", bgColor);
	}
	
	public void resetColor(boolean useSeparateAlpha) {
		this.useSeparateAlpha = useSeparateAlpha;
		if (!useSeparateAlpha)
			this.bgColor.set(resetColor);
		else
			this.bgColor.set(resetColor.r, resetColor.g, resetColor.b, alpha);
		this.getElementMaterial().setColor("Color", bgColor);
	}
	
	public void setAlpha(float alpha) {
		this.alpha = alpha;
		bgColor.a = alpha;
		this.getElementMaterial().setColor("Color", bgColor);
	}
	
	public void resetAlpha() {
		this.alpha = resetAlpha;
		bgColor.a = resetAlpha;
		this.getElementMaterial().setColor("Color", bgColor);
	}
	
	public void fillScreen() {
		setPosition(0,0);
		setDimensions(screen.getWidth(),screen.getHeight());
	}
}
