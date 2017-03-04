package icetone.effects;

import com.jme3.math.ColorRGBA;

public class ColorSwapEffect extends AbstractColorEffect {

	public ColorSwapEffect(float duration, ColorRGBA blendColor) {
		super(duration, blendColor);
	}

	@Override
	public void update(float tpf) {
		if (!init) {
			disableShaderEffect();
			element.getElementMaterial().setColor("Color", getBlendColor());
			element.getElementMaterial().setFloat("EffectStep", 1.0f);
			init = true;
			isActive = false;
		}
		updatePass(tpf);
	}

}
