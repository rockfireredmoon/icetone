package icetone.css;

import org.w3c.dom.css.CSSPrimitiveValue;

import com.jme3.math.Vector2f;
import com.jme3.texture.Texture;

import icetone.core.BaseElement;

public abstract class AbstractCssEffectFactory implements CssEffectFactory {



	protected Vector2f getActualDestination(CssEffect effect, BaseElement el) {
		Vector2f destination = effect.getDestination();
		Vector2f destinationUnits = effect.getDestinationUnits();
		if (destination == null)
			return null;
		Vector2f adest = new Vector2f(destination);
		if (destinationUnits.x == CSSPrimitiveValue.CSS_PERCENTAGE)
			adest.x = (el.getParentContainer().getWidth() / 100f) * destination.x;
		if (destinationUnits.y == CSSPrimitiveValue.CSS_PERCENTAGE)
			adest.y = (el.getParentContainer().getHeight() / 100f) * destination.y;
		return adest;
	}

	protected Texture createImage(CssEffect effect, BaseElement el) {
		Texture color = el.getScreen().getApplication().getAssetManager().loadTexture(effect.getUri());
		color.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
		color.setMagFilter(Texture.MagFilter.Nearest);
		return color;
	}
}
