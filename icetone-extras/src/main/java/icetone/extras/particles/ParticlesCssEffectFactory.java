package icetone.extras.particles;

import icetone.core.BaseElement;
import icetone.css.AbstractCssEffectFactory;
import icetone.css.CssEffect;
import icetone.effects.Effect;

public class ParticlesCssEffectFactory extends AbstractCssEffectFactory {

	@Override
	public Effect createEffect(CssEffect effect, BaseElement el, String name) {
		if (name.equals("Particles")) {
			return new ParticleEffect(effect.getUri()); 
		}
		return null;
	}

}
