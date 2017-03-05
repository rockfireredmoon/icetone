/*
 * Copyright (c) 2013-2014 Emerald Icemoon All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  *
 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package icetone.core;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.font.BitmapFont;
import com.jme3.font.LineWrapMode;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Geometry;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;

import icetone.core.Measurement.Unit;
import icetone.core.utils.BitmapTextUtil;
import icetone.core.utils.MathUtil;
import icetone.effects.EffectChannel;
import icetone.effects.EffectFactory;
import icetone.effects.EffectManager;
import icetone.effects.IEffect;
import icetone.framework.core.AnimText;

public abstract class AbstractGenericLayout<C extends ElementContainer<?, ?>, O> extends AbstractLayout<C, O> {
	final static Logger LOG = Logger.getLogger(AbstractGenericLayout.class.getName());

	protected final Vector4f clipTest = new Vector4f();
	protected final Vector4f clippedArea = new Vector4f();
	protected final Vector2f tempV2 = new Vector2f();

	private boolean startingEffects;

	@Override
	public final void layout(C container, LayoutType layoutType) {

		if (container instanceof StyledNode && ((StyledNode<?, ?>) container).getCssState() != null
				&& (layoutType == LayoutType.all || layoutType == LayoutType.styling
						|| layoutType == LayoutType.reset)) {
			if (layoutType == LayoutType.reset) {
				((StyledNode<?, ?>) container).getCssState().resetCssProcessor();
			}
			((StyledNode<?, ?>) container).getCssState().restyleCssProcessor();
			((StyledNode<?, ?>) container).getCssState().applyCss();

		}

		if (container instanceof BaseElement) {
			BaseElement element = (BaseElement) container;

			if (layoutType == LayoutType.text || layoutType == LayoutType.all)
				onLayoutText(container);

			if (layoutType == LayoutType.background || layoutType == LayoutType.all)
				onLayoutBackground(container);

			Vector4f clippingBounds = element.getClippingBounds();

			if ((layoutType == LayoutType.clipping || layoutType == LayoutType.all || layoutType == LayoutType.text))
				onCalcClip(container);

			if (layoutType == LayoutType.clipping || layoutType == LayoutType.all)
				onBackgroundClip(container, clippingBounds);

			if ((layoutType == LayoutType.clipping || layoutType == LayoutType.all || layoutType == LayoutType.text)
					&& element.getTextElement() != null) {
				clippingBounds = element.getClippingBounds().add(element.getTextClipPaddingVec());
				boolean clipEnabled = element.isClipped() && element.isClippingEnabledInHeirarchy();
				onFontClip(container, element.getTextElement(), clippingBounds, clipEnabled);
				onTextClip(container, element.getTextElement(), clippingBounds, clipEnabled);
			}

			if ((layoutType == LayoutType.effects || layoutType == LayoutType.all) && !startingEffects) {
				try {
					startingEffects = true;
					startEffects(element);
				} finally {
					startingEffects = false;
				}
			}
		}

		if ((layoutType == LayoutType.children || layoutType == LayoutType.all)) {
			onLayout(container);
		}

		if (container instanceof BaseElement) {
			BaseElement element = (BaseElement) container;

			// If updating clipping, we need the children to recalc theirs
			// too
			if (layoutType == LayoutType.clipping || layoutType == LayoutType.all)
				for (BaseElement e : element.getElements())
					e.dirtyLayout(false, LayoutType.clipping);
		}

		// if (layoutType == LayoutType.zorder || layoutType == LayoutType.all)
		// container.applyZOrder();
	}

	@Override
	public final Vector2f maximumSize(C container) {
		return calcMaximumSize(container);
	}

	@Override
	public final Vector2f minimumSize(C container) {
		Vector2f bg = container instanceof BaseElement ? calcMiniumBackgroundSize(container) : null;
		Vector2f txt = container instanceof BaseElement ? calcMinimumTextSize(container) : null;
		Vector2f p = calcMinimumSize(container);
		return MathUtil.largest(addMargin(container, MathUtil.largest(bg, txt)), p);
	}

	@Override
	public Vector2f preferredSize(C container) {
		Vector2f bg = container instanceof BaseElement ? calcPreferredBackgroundSize(container) : null;
		Vector2f txt = container instanceof BaseElement ? calcTextLayerSize(container) : null;
		Vector2f p = calcPreferredSize(container);
		Vector2f mg = MathUtil.largest(addMargin(container, MathUtil.largest(bg, txt)), p);
		return mg;
	}

	protected void startEffects(BaseElement el) {
		if (el.effects != null) {
			EffectManager effectManager = el.getScreen().getEffectManager();
			for (Map.Entry<EffectChannel, EffectFactory> en : el.effects.entrySet()) {
				try {
					IEffect e = en.getValue().createEffect(el);
					e.setElement(el);
					e.setChannel(en.getKey());

					/*
					 * If this new effect conflicts with any that are currently
					 * active we must reset the existing effect before applying
					 * the new one
					 */
					List<IEffect> ef = effectManager.getEffectsFor(el, en.getKey());
					for (int i = ef.size() - 1; i >= 0; i--) {
						IEffect effect = ef.get(i);
						if (e.isConflict(effect)) {
							effect.setIsActive(false);
						}
					}

					effectManager.applyEffect(e);
				} catch (Exception e) {
					LOG.log(Level.WARNING, String.format("Failed to create effect for %s.", el), e);
				}
			}
			el.effects = null;
		}
	}

	protected Vector2f calcTextLayerSize(C el) {
		Vector2f pf = calcTextSize(el, el.getWidth() - el.getTotalPadding().x);
		if (pf != null) {
			return pf.add(el.getTotalPadding());
		}
		return pf;
	}

	protected Vector2f calcMiniumBackgroundSize(C el) {
		return Vector2f.ZERO;
	}

	protected Vector2f addMargin(C element, Vector2f bs) {
		if (!element.getMargin().equals(Vector4f.ZERO)) {
			if (bs == null)
				bs = new Vector2f(element.getMargin().x + element.getMargin().y,
						element.getMargin().z + element.getMargin().w);
			else
				bs = bs.add(new Vector2f(element.getMargin().x + element.getMargin().y,
						element.getMargin().z + element.getMargin().w));
		}
		return bs;
	}

	protected Vector2f calcPreferredBackgroundSize(C el) {
		BaseElement element = (BaseElement) el;
		Vector2f sz = new Vector2f();
		Size bs = element.getBackgroundDimensions();
		Texture tex = element.getElementTexture();
		Image img = tex == null ? null : tex.getImage();
		if (img == null)
			return null;

		Vector2f imgsz = (element.isAtlasTextureInUse()
				? new Vector2f(element.getAtlasCoords().z, element.getAtlasCoords().w)
				: new Vector2f(img.getWidth(), img.getHeight()));

		switch (bs.xUnit) {
		case PX:
			sz.x = bs.x;
			break;
		case AUTO:
			sz.x = imgsz.x;
			break;
		default:
			break;
		}

		switch (bs.yUnit) {
		case PX:
			sz.y = bs.y;
			break;
		case AUTO:
			sz.y = imgsz.y;
			break;
		default:
			break;
		}
		//
		// if (sz.x == 0 && sz.y == 0)
		// sz = null;

		return sz;
	}

	protected Vector2f calcBackgroundSize(C el) {
		BaseElement element = (BaseElement) el;
		Vector2f sz = new Vector2f(Short.MIN_VALUE, Short.MIN_VALUE);
		Size bs = element.getBackgroundDimensions();
		Texture tex = element.getElementTexture();
		Image img = tex == null ? null : tex.getImage();
		Vector4f margin = element.getMargin();
		Vector2f psz = element.getDimensions().clone();
		psz.x -= margin.x + margin.y;
		psz.y -= margin.z + margin.w;

		if (img == null)
			return null;

		Vector2f imgsz = (element.isAtlasTextureInUse()
				? new Vector2f(element.getAtlasCoords().z, element.getAtlasCoords().w)
				: new Vector2f(img.getWidth(), img.getHeight()));

		switch (bs.xUnit) {
		case PX:
			sz.x = bs.x;
			break;
		case PERCENT:
			sz.x = psz.x * (bs.x / 100f);
			break;
		case ZOOM:
			if (img != null)
				sz.x = psz.x * Math.max(psz.x / imgsz.x, psz.y / imgsz.y);
			break;
		case FIT:
			if (img != null)
				sz.x = psz.x * Math.min(psz.x / imgsz.x, psz.y / imgsz.y);
			break;
		default:
			sz.x = imgsz.x;
			break;
		}

		switch (bs.yUnit) {
		case PX:
			sz.y = bs.y;
			break;
		case PERCENT:
			sz.y = psz.y * (bs.y / 100f);
			break;
		case ZOOM:
			if (img != null)
				sz.y = psz.y * Math.max(psz.x / imgsz.x, psz.y / imgsz.y);
			break;
		case FIT:
			if (img != null)
				sz.y = psz.y * Math.min(psz.x / imgsz.x, psz.y / imgsz.y);
			break;
		default:
			sz.y = imgsz.y;
			break;
		}

		if (sz.x == Short.MIN_VALUE && sz.y == Short.MIN_VALUE)
			sz = null;

		return sz;
	}

	/**
	 * Calculate the maximum size. The implementation does not have to return a
	 * cloned or otherwise safe Vector. This should normally add any padding
	 * such as from {@link BaseElement#getAllPadding()}. Return
	 * <code>null</code> to indicate no maximum size which is practically 0,0.
	 * By default this will be the final preferred size.
	 * 
	 * @param container
	 * @return maximum size
	 */

	protected Vector2f calcMaximumSize(C container) {
		return null;
	}

	/**
	 * Calculate the minimum size of the whole container. The implementation
	 * does not have to return a cloned or otherwise safe Vector. This should
	 * normally add any padding such as from {@link BaseElement#getAllPadding()}
	 * . Return <code>null</code> to indicate no minimum size which is
	 * practically 0,0. By default, the final minimum size will be the largest
	 * of this, the calculated minimum background image size, and the minimum
	 * size of the text).
	 * 
	 * @param container
	 * @return minimum size
	 */
	protected Vector2f calcMinimumSize(C container) {
		return container.getTotalPadding();
	}

	/**
	 * Calculate the preferred size <strong>of the child commponents</strong>.
	 * The implementation does not have to return a cloned or otherwise safe
	 * Vector. This should normally add any padding such as from
	 * {@link BaseElement#getAllPadding()}. Return <code>null</code> to indicate
	 * no preferred size which is practically 0,0. By default, the final
	 * preferred size will be the largest of this, the calculated preferred
	 * background image size, and the preferred size of the text).
	 * 
	 * @param container
	 * @return preferred size
	 */
	protected Vector2f calcPreferredSize(C container) {
		return container.getTotalPadding();
	}

	/**
	 * Calculate the text preferred size. The implementation does not have to
	 * return a cloned or otherwise safe Vector. This should NOT add any padding
	 * such as from {@link BaseElement#getAllPadding()}. Return
	 * <code>null</code> to indicate no text size.
	 * 
	 * @param container
	 * @return text size
	 */
	protected Vector2f calcTextSize(C container, float inWidth) {
		return defaultCalcTextSize(container, inWidth);
	}

	protected Vector2f defaultCalcTextSize(C container, float inWidth) {
		BaseElement el = (BaseElement) container;

		if (el.getTextElement() != null) {

			/*
			 * If the element has a fixed max dimension, keep the preferred size
			 * within that to give us a chance of properly laying out text
			 * elements of an unknown size
			 */
			Vector2f max = el.calcMaximumSize();

			/*
			 * When text is wrapping, keep the text within the width of the
			 * parent element where possible
			 */
			LineWrapMode textWrap = el.getTextElement().getTextWrap();
			if (inWidth > 0 && (LineWrapMode.Word == textWrap || LineWrapMode.Character == textWrap)) {
				max.x = inWidth;
				// TODO rotate the available width for wrapped text (require
				// both axis passed)
			}

			Vector2f textTotalSize = BitmapTextUtil.getTextTotalSize(el, el.getText(),
					max == null ? Short.MAX_VALUE : max.x);

			// Rotate the text bounds
			if (el.getTextRotation() != 0) {
				MathUtil.rotatedBoundsLocal(textTotalSize, el.getTextRotation() * FastMath.DEG_TO_RAD);
			}

			return textTotalSize;

		}
		return null;
	}

	/**
	 * Calculate the text minimum size. The implementation does not have to
	 * return a cloned or otherwise safe Vector. This should normally add any
	 * padding such as from {@link BaseElement#getAllPadding()}. Return
	 * <code>null</code> to indicate no text size.
	 * 
	 * The default implementations uses the wrap mode. unwrapped text has a
	 * minimum size of the whole text, other modes will return no minimum size.
	 * 
	 * @param container
	 * @return text size
	 */
	protected Vector2f calcMinimumTextSize(C container) {
		BaseElement el = (BaseElement) container;
		if (el.getTextElement() != null) {

			if (el.getTextWrap() == LineWrapMode.Clip || el.getTextWrap() == LineWrapMode.Word
					|| el.getTextWrap() == LineWrapMode.Character)
				return container.getTotalPadding();

			return calcTextSize(container, Float.MAX_VALUE).add(el.getTotalPadding());
		}

		return null;
	}

	protected void onLayout(C container) {
	}

	protected void onLayoutText(C container) {
		BaseElement d = (BaseElement) container;
		AnimText textElement = d.getTextElement();
		if (textElement != null) {
			Vector4f textPadding = d.getAllPadding();

			/* Inherited styles */
			textElement.setFontColor(BaseElement.calcFontColor(container));
			textElement.setFontSize(BaseElement.calcFontSize(container));
			textElement.setFont(BaseElement.calcFont(container));
			textElement.setText(textElement.getText());
			
			onPositionText(container, textElement, textPadding);
			
			switch (d.getTextWrap()) {
			case Character:
				textElement.wrapTextToCharacter(d.getWidth() - d.getTotalPadding().x);
				textElement.setUseClip(false);
				break;
			case Word:
				textElement.wrapTextToWord(d.getWidth() - d.getTotalPadding().x);
				textElement.setUseClip(false);
				break;
			case NoWrap:
				textElement.wrapTextNoWrap();
				textElement.setUseClip(false);
				break;
			case Clip:
				textElement.wrapTextNoWrap();
				textElement.setUseClip(true);
				break;
			}

		}
	}

	protected void onCalcClip(C container) {
		BaseElement el = (BaseElement) container;
		if (el.isVisible()) {
			el.getClippingBounds().set(calcClipping(el));
		}
	}

	protected boolean isClippingInUse(BaseElement element) {
		Material mat = element.getElementMaterial();
		return (Boolean) mat.getParam("UseClipping").getValue();
	}

	protected void onBackgroundClip(C container, Vector4f clippingBounds) {
		BaseElement element = (BaseElement) container;
		Material mat = element.getElementMaterial();
		if (mat != null) {
			boolean clippingInUse = isClippingInUse(element);
			boolean shouldClip = !element.getClippingLayers().isEmpty() && element.isClippingEnabledInHeirarchy();
			if (shouldClip && !clippingInUse) {
				mat.setBoolean("UseClipping", true);
			} else if (clippingInUse && !shouldClip) {
				mat.setBoolean("UseClipping", false);
			}
			mat.setVector4("Clipping", clippingBounds);
		}
	}

	protected void onTextClip(C container, AnimText textElement, Vector4f clippingBounds, boolean clipElement) {
		if (textElement != null) {
			textElement.setUseClip(clipElement);
			textElement.setClippingBounds(clippingBounds);
		}
	}

	/**
	 * Updates font materials with any changes to clipping layers
	 */
	protected void onFontClip(C container, AnimText textElement, Vector4f clippingBounds, boolean clipElement) {
		BaseElement element = (BaseElement) container;
		BitmapFont font = textElement == null ? null : textElement.getFont();
		if (font != null) {
			try {
				if (!element.isVisible()) {
					for (int i = 0; i < font.getPageSize(); i++) {
						font.getPage(i).setVector4("Clipping", clippingBounds);
						font.getPage(i).setBoolean("UseClipping", true);
					}
				} else {
					if (clipElement) {
						for (int i = 0; i < font.getPageSize(); i++) {
							font.getPage(i).setVector4("Clipping", clippingBounds);
							font.getPage(i).setBoolean("UseClipping", true);
						}
					} else {
						for (int i = 0; i < font.getPageSize(); i++) {
							font.getPage(i).setBoolean("UseClipping", false);
						}
					}
				}
			} catch (IllegalArgumentException iae) {
				throw new IllegalArgumentException(
						"Problem configuring font clip for " + container + " (" + textElement.getText() + ")", iae);
			}
		}
	}

	protected Vector4f calcClipping(BaseElement el) {
		BaseScreen screen = el.getScreen();
		clippedArea.set(0, 0, screen.getWidth(), screen.getHeight());
		if (el.isClippingEnabledInHeirarchy()) {
			for (ClippingDefine def : el.getClippingLayers()) {
				clipTest.set(def.getClipping());
				// TODO not sure why this is here. When enabled,
				// ProgressIndicator doesnt work in FancyWindow??
				// if (def.getElement() != el) {
				Vector4f pad = getClipPadding(def);
				clipTest.addLocal(pad.x, pad.y, -pad.z, -pad.w);
				// }
				clipArea(clipTest, clippedArea);
			}
		}
		return clippedArea;
	}

	protected Vector4f getClipPadding(ClippingDefine def) {
		return def.getElement().getClipPaddingVec();
	}

	protected void clipArea(Vector4f clip, Vector4f area) {
		clipArea(clip.x, clip.y, clip.z, clip.w, area);
	}

	protected void clipArea(float x, float y, float z, float w, Vector4f area) {
		if (x > area.x)
			area.x = x;
		if (y > area.y)
			area.y = y;
		if (z < area.z)
			area.z = z;
		if (w < area.w)
			area.w = w;
	}

	protected void onPositionText(C container, AnimText textElement, Vector4f textPadding) {
		BaseElement element = (BaseElement) container;
		if (textElement != null) {
			String processText = element.formatText(element.getText());
			if (!Objects.equals(processText, textElement.getText())) {
				textElement.setText(processText);
			}
			textElement.setLineHeight(element.getFixedLineHeight() > 0 ? element.getFixedLineHeight() : -1);
			textElement.setOrigin(100, 0);
			textElement.setRotation(element.getTextRotation());
			textElement.setBounds(calcTextBounds(container, textElement, textPadding));
			textElement.setMargin(calcTextOffset(container, textElement, textPadding));
		}
	}

	/**
	 * Calculate the padding to place around the text. Usually this is the
	 * padding provided by element.<code>null</code> must not be returned.
	 * 
	 * @param element
	 *            element
	 * @param textElement
	 *            text element
	 * @param textPadding
	 *            original padding
	 * @return padding to use
	 */
	protected Vector4f calcTextOffset(C element, AnimText textElement, Vector4f textPadding) {
		return textPadding;
	}

	protected Vector2f calcTextBounds(C container, AnimText textElement, Vector4f textPadding) {
		BaseElement element = (BaseElement) container;
		return tempV2.set(element.getWidth(), element.getHeight());
	}

	protected void onLayoutBackground(C container) {
		BaseElement element = (BaseElement) container;
		Material mat = element.getElementMaterial();
		Geometry geom = element.getGeometry();
		Vector2f elsz = element.getDimensions();
		if (mat != null && geom != null) {
			Vector4f margin = element.getMargin();
			Vector2f sz = calcBackgroundSize(container);
			if (sz == null)
				sz = elsz.subtract(margin.x + margin.y, margin.z + margin.w);

			Vector2f esz = container.getDimensions().clone();
			esz.subtractLocal(margin.x + margin.y, margin.z + margin.w);

			sz.x -= element.borderOffset.x + element.borderOffset.y;
			sz.y -= element.borderOffset.z + element.borderOffset.w;

			Vector2f fac = new Vector2f(esz.x / sz.x, esz.y / sz.y);

			ElementQuadGrid model = element.getModel();

			if (!element.calcBorders().equals(model.getBorders())) {
				element.recreateElementQuadGrid();
			}

			float tcW, tcH;

			switch (element.getTileMode()) {
			case REPEAT:
				model.updateDimensions(esz.x, esz.y);
				tcW = sz.x / (element.isAtlasTextureInUse() ? element.getAtlasCoords().z : model.getImageWidth())
						* fac.x;
				tcH = sz.y / (element.isAtlasTextureInUse() ? element.getAtlasCoords().w : model.getImageHeight())
						* fac.y;
				model.updateTiledTexCoords(0, -tcH, tcW, 0);
				break;
			case REPEAT_X:
				model.updateDimensions(esz.x, sz.y);
				tcW = sz.x / (element.isAtlasTextureInUse() ? element.getAtlasCoords().z : model.getImageWidth())
						* fac.x;
				tcH = sz.y / (element.isAtlasTextureInUse() ? element.getAtlasCoords().w : model.getImageHeight());
				model.updateTiledTexCoords(0, -tcH, tcW, 0);
				break;
			case REPEAT_Y:
				model.updateDimensions(sz.x, esz.y);
				tcW = sz.x / (element.isAtlasTextureInUse() ? element.getAtlasCoords().z : model.getImageWidth());
				tcH = sz.y / (element.isAtlasTextureInUse() ? element.getAtlasCoords().w : model.getImageHeight())
						* fac.y;
				model.updateTiledTexCoords(0, -tcH, tcW, 0);
				break;
			default:
				model.updateDimensions(sz.x, sz.y);
				break;
			}

			Position bp = element.getBackgroundPosition();
			Vector2f pos = new Vector2f(0, 0);
			if (bp != null)
				if (bp.xUnit == Unit.PERCENT)
					pos.x = ((elsz.x - margin.y - sz.x - margin.x) * (bp.x / 100f)) + margin.x;
				else
					pos.x = bp.x + margin.x;
			else
				pos.x = margin.x;
			if (bp != null)
				if (bp.yUnit == Unit.PERCENT) {
					pos.y = ((elsz.y - margin.z - sz.y - margin.w) * ((100 - bp.y) / 100f)) + margin.w;
				} else
					pos.y = elsz.y - sz.y - bp.y + margin.w - margin.z;
			else
				pos.y = elsz.y - sz.y - margin.z;

			geom.setLocalTranslation(pos.x + element.borderOffset.x, pos.y + element.borderOffset.z,
					geom.getLocalTranslation().z);
			geom.updateModelBound();
		}
	}

	protected Vector2f getPreferredSizeFromTexture(BaseElement c) {
		if (c != null && c.getElementTexture() != null) {
			Texture tex = c.getElementTexture();
			if (tex.getImage() != null && c.isUseColorMapForSizeCalculations())
				return new Vector2f(tex.getImage().getWidth(), tex.getImage().getHeight());
		}
		return null;
	}

}
