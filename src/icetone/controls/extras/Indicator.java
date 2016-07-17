/**
 * ICETONE - A GUI Library for JME3 based on a heavily modified version of 
 * Tonegod's 'Tonegodgui'.  
 * 
 * Copyright (c) 2013, t0neg0d
 * Copyright (c) 2016, Emerald Icemoon
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of the FreeBSD Project.
 */
package icetone.controls.extras;

import com.jme3.font.BitmapFont;
import com.jme3.font.LineWrapMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.text.AbstractTextLayout;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.Screen;
import icetone.core.layout.LUtil;
import icetone.core.utils.UIDUtil;

/**
 *
 * @author t0neg0d
 */
public class Indicator extends Element {
	private float maxValue = 0, currentValue = 0, percentage = 0;
	private Orientation orientation;
	private ColorRGBA indicatorColor;
	private String alphaMapPath;
	private String overlayImg;
	private Element elIndicator, elOverlay;
	private boolean displayValues = false, displayPercentages = false;
	private Vector4f indPadding = Vector4f.ZERO.clone();
	private boolean reverseDirection = false;
	private ClippingDefine def = null;
	private String indicatorText;

	/**
	 * Creates a new instance of the Indicator control
	 */
	public Indicator() {
		this(Orientation.HORIZONTAL);
	}

	/**
	 * Creates a new instance of the Indicator control
	 * 
	 * @param orientation
	 *            The orientation
	 */
	public Indicator(Orientation orientation) {
		this(Screen.get(), orientation);
	}

	/**
	 * Creates a new instance of the Indicator control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public Indicator(ElementManager screen, Orientation orientation) {
		this(screen, UIDUtil.getUID(), Vector2f.ZERO, LUtil.LAYOUT_SIZE,
				screen.getStyle("Indicator").getVector4f("resizeBorders"),
				screen.getStyle("Indicator").getString("defaultImg"), orientation);
	}

	/**
	 * Creates a new instance of the Indicator control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 */
	public Indicator(ElementManager screen, Vector2f position, Orientation orientation) {
		this(screen, UIDUtil.getUID(), position, LUtil.LAYOUT_SIZE,
				screen.getStyle("Indicator").getVector4f("resizeBorders"),
				screen.getStyle("Indicator").getString("defaultImg"), orientation);
	}

	/**
	 * Creates a new instance of the Indicator control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 */
	public Indicator(ElementManager screen, Vector2f position, Vector2f dimensions, Orientation orientation) {
		this(screen, UIDUtil.getUID(), position, dimensions, screen.getStyle("Indicator").getVector4f("resizeBorders"),
				screen.getStyle("Indicator").getString("defaultImg"), orientation);
	}

	/**
	 * Creates a new instance of the Indicator control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 * @param resizeBorders
	 *            A Vector4f containing the border information used when
	 *            resizing the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg
	 *            The default image to use for the Element
	 */
	public Indicator(ElementManager screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
			String defaultImg, Orientation orientation) {
		this(screen, UIDUtil.getUID(), position, dimensions, resizeBorders, defaultImg, orientation);
	}

	/**
	 * Creates a new instance of the Indicator control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 */
	public Indicator(ElementManager screen, String UID, Vector2f position, Orientation orientation) {
		this(screen, UID, position, LUtil.LAYOUT_SIZE, screen.getStyle("Indicator").getVector4f("resizeBorders"),
				screen.getStyle("Indicator").getString("defaultImg"), orientation);
	}

	/**
	 * Creates a new instance of the Indicator control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 */
	public Indicator(ElementManager screen, String UID, Vector2f position, Vector2f dimensions,
			Orientation orientation) {
		this(screen, UID, position, dimensions, screen.getStyle("Indicator").getVector4f("resizeBorders"),
				screen.getStyle("Indicator").getString("defaultImg"), orientation);
	}

	/**
	 * Creates a new instance of the Indicator control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 * @param resizeBorders
	 *            A Vector4f containing the border information used when
	 *            resizing the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg
	 *            The default image to use for the Indicator
	 */
	public Indicator(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
			String defaultImg, Orientation orientation) {
		super(screen, UID, position, dimensions, resizeBorders, null);

		layoutManager = new IndicatorLayout();

		this.overlayImg = defaultImg;
		this.orientation = orientation;

		elIndicator = new Element(screen, UID + ":Indicator", Vector2f.ZERO, dimensions, resizeBorders, null) {
			@Override
			public void updateLocalClippingLayer() {
				Indicator ind = ((Indicator) this.getElementParent());
				if (def == null)
					def = elIndicator.getClippingDefine(elIndicator);

				if (getIsVisible()) {
					float clipX = 0, clipY = 0, clipW = 0, clipH = 0;
					if (ind.getOrientation() == Indicator.Orientation.HORIZONTAL) {
						if (reverseDirection) {
							clipX = def.getElement().getWidth() - ind.getCurrentPercentage();
							clipW = def.getElement().getWidth();
						} else {
							clipW = def.getElement().getWidth()
									- (def.getElement().getWidth() - ind.getCurrentPercentage());
						}
						clipH = def.getElement().getHeight();
					} else {
						if (reverseDirection) {
							clipY = def.getElement().getHeight() - ind.getCurrentPercentage();
							clipH = def.getElement().getHeight();
						} else {
							clipH = def.getElement().getHeight()
									- (def.getElement().getHeight() - ind.getCurrentPercentage());
						}
						clipW = def.getElement().getWidth();
					}
					def.clip.set(clipX, clipY, clipW, clipH);
					super.updateLocalClippingLayer();

				}
			}
		};
		elIndicator.addClippingLayer(elIndicator, new Vector4f(0, 0, elIndicator.getWidth(), elIndicator.getHeight()));
		elIndicator.setIgnoreMouse(true);
		addChild(elIndicator);

		elOverlay = new Element(screen, UID + ":Overlay", Vector2f.ZERO, dimensions, resizeBorders, overlayImg);
		elOverlay.setIgnoreMouse(true);

		// Load default font info
		elOverlay.setFontColor(screen.getStyle("Indicator").getColorRGBA("fontColor"));
		elOverlay.setFontSize(screen.getStyle("Indicator").getFloat("fontSize"));
		elOverlay.setTextAlign(BitmapFont.Align.valueOf(screen.getStyle("Indicator").getString("textAlign")));
		elOverlay.setTextVAlign(BitmapFont.VAlign.valueOf(screen.getStyle("Indicator").getString("textVAlign")));
		elOverlay.setTextWrap(LineWrapMode.valueOf(screen.getStyle("Indicator").getString("textWrap")));
		elOverlay.setTextClipPaddingByKey("Indicator", "textPadding");
		elOverlay.setTextPaddingByKey("Indicator", "textPadding");

		addChild(elOverlay);

		String indImg = screen.getStyle("Indicator").getString("indicatorImg");
		if (indImg != null) {
			setIndicatorImage(indImg);
		}
		String baseImg = screen.getStyle("Indicator").getString("baseImg");
		if (baseImg != null) {
			setBaseImage(baseImg);
		}
		String alphaImg = screen.getStyle("Indicator").getString("alphaImg");
		if (alphaImg != null) {
			setAlphaMap(alphaImg);
		}
		ColorRGBA rgba = screen.getStyle("Indicator").getColorRGBA("indicatorColor");
		if (rgba != null) {
			setIndicatorColor(rgba);
		}
		String fontName = screen.getStyle("Indicator").getString("fontName");
		if (fontName != null) {
			getOverlayElement().setFont(screen.getStyle("Font").getString(fontName));
			getOverlayElement().setFontSize(screen.getStyle("Indicator").getFloat("fontSize"));
		}
		setIndicatorPadding(indPadding);
	}

	public String getIndicatorText() {
		return indicatorText;
	}

	public void setIndicatorText(String indicatorText) {
		// TODO take into account display % and display value
		this.indicatorText = indicatorText;
		getOverlayElement().setText(indicatorText == null ? "" : indicatorText);
		refactorIndicator();
	}

	// public Vector2f getPreferredDimensions() {
	// return prefDimensions == null ?
	// (getOrgDimensions().equals(LUtil.LAYOUT_SIZE) ? null :
	// getOrgDimensions()) : prefDimensions;
	// }

	/**
	 * Use this method in place of setScaleEW and setScaleNE
	 * 
	 * @param scaleNS
	 * @param scaleEW
	 */
	public void setScaling(boolean scaleNS, boolean scaleEW) {
		setScaleNS(scaleNS);
		elIndicator.setScaleNS(scaleNS);
		elOverlay.setScaleNS(scaleNS);
		setScaleEW(scaleEW);
		elIndicator.setScaleEW(scaleEW);
		elOverlay.setScaleEW(scaleEW);
	}

	@Override
	public void controlResizeHook() {
		setIndicatorPadding(indPadding);
		refactorIndicator();
	}

	/**
	 * Returns the Indicator.Orientation of the Indicator instance
	 * 
	 * @return Indicator.Orientation
	 */
	public Orientation getOrientation() {
		return this.orientation;
	}

	/**
	 * Sets the ColorRGBA value of the Indicator
	 * 
	 * @param indicatorColor
	 */
	public void setIndicatorColor(ColorRGBA indicatorColor) {
		this.indicatorColor = indicatorColor;
		elIndicator.getElementMaterial().setColor("Color", this.indicatorColor);
		refactorIndicator();
	}

	/**
	 * Set the maximum value (e.g. float = 100%)
	 * 
	 * @param maxValue
	 */
	public void setMaxValue(float maxValue) {
		this.maxValue = maxValue;
		refactorIndicator();
	}

	/**
	 * Returns the maximum value set for the Indicator
	 * 
	 * @return maxValue
	 */
	public float getMaxValue() {
		return this.maxValue;
	}

	/**
	 * Sets the current value of the Indicator
	 * 
	 * @param currentValue
	 */
	public void setCurrentValue(float currentValue) {
		this.currentValue = currentValue;
		refactorIndicator();
	}

	public void setReverseDirection(boolean reverseDirection) {
		this.reverseDirection = reverseDirection;
		refactorIndicator();
	}

	/**
	 * Returns the current value of the Indicator
	 * 
	 * @return currentValue
	 */
	public float getCurrentValue() {
		return this.currentValue;
	}

	private void refactorIndicator() {
		if (currentValue > maxValue) {
			currentValue = maxValue;
		} else if (currentValue < 0f) {
			currentValue = 0f;
		}
		percentage = currentValue / maxValue;
		if (orientation == Orientation.HORIZONTAL) {
			percentage *= getWidth();
		} else {
			percentage *= getHeight();
		}
		// System.out.println("CV: " + currentValue + " PC: " + percentage);
		elIndicator.updateLocalClippingLayer();

		if (this.displayValues) {
			elOverlay.setText(String.valueOf((int) this.currentValue) + "/" + String.valueOf((int) this.maxValue));
		} else if (this.displayPercentages) {
			elOverlay.setText(String.valueOf((int) ((this.currentValue / this.maxValue) * 100f)) + "%");
		} else {
			// elOverlay.setText("");
		}
		if (indicatorText != null) {
			getOverlayElement().setText(indicatorText);
		}
		onChange(currentValue, currentValue / maxValue * 100f);
	}

	/**
	 * Returns current value as a percent of the max value
	 * 
	 * @return percentage
	 */
	public float getCurrentPercentage() {
		return this.percentage;
	}

	/**
	 * Use setAlphaMap instead
	 * 
	 * @param alphaMapPath
	 */
	@Deprecated
	public void setIndicatorAlphaMap(String alphaMapPath) {
		this.alphaMapPath = alphaMapPath;
		elIndicator.setAlphaMap(this.alphaMapPath);
		refactorIndicator();
	}

	/**
	 * Applies an alpha map to the indicator, allowing for curved shapes
	 * 
	 * @param alphaMapPath
	 */
	@Override
	public void setAlphaMap(String alphaMapPath) {
		this.alphaMapPath = alphaMapPath;
		elIndicator.setAlphaMap(this.alphaMapPath);
	}

	public Element getIndicatorElement() {
		return elIndicator;
	}

	public Element getOverlayElement() {
		return elOverlay;
	}

	/**
	 * Return the element used for displaying overlay text
	 * 
	 * @return elOverlay
	 */
	public Element getTextDisplayElement() {
		return this.elOverlay;
	}

	/**
	 * Sets the display text to format as currentValue / maxValue
	 */
	public void setDisplayValues() {
		this.displayPercentages = false;
		this.displayValues = true;
	}

	/**
	 * Sets the display text to current value as percent %
	 */
	public void setDisplayPercentage() {
		this.displayPercentages = true;
		this.displayValues = false;
	}

	/**
	 * Hides the overlay display text
	 */
	public void setHideText() {
		this.displayPercentages = false;
		this.displayValues = false;
	}

	/**
	 * Set the image to use behind the indicator
	 * 
	 * @param imgPath
	 */
	public void setBaseImage(String imgPath) {
		setTexture(imgPath);
	}

	/**
	 * Set the image to use in front of the indicator
	 * 
	 * @param imgPath
	 */
	public void setOverlayImage(String imgPath) {
		elOverlay.setTexture(imgPath);
	}

	public void setIndicatorImage(String imgPath) {
		elIndicator.setTexture(imgPath);
	}

	public void setIndicatorPadding(Vector4f padding) {
		indPadding.set(padding);
		elIndicator.setPosition(padding.x, padding.y);
	}

	protected void onChange(float currentValue, float currentPercentage) {

	}

	public class IndicatorLayout extends AbstractTextLayout {

		@Override
		public Vector2f minimumSize(Element parent) {
			return LUtil.getMinimumSize(elOverlay);
		}

		@Override
		public Vector2f maximumSize(Element parent) {
			return LUtil.getMaximumSize(elOverlay);
		}

		@Override
		public Vector2f preferredSize(Element parent) {
			return LUtil.getPreferredSize(elOverlay);
		}

		public void layout(Element childElement) {
			Vector2f sz = childElement.getDimensions().clone();
			Vector4f pad = childElement.getTextPaddingVec();
			sz.subtract(pad.x + pad.y, pad.z + pad.w);
			if (sz.x < 0)
				sz.x = 0;

			if (sz.y < 0)
				sz.y = 0;

			LUtil.setBounds(elOverlay, pad.x, pad.z, sz.x, sz.y);
			LUtil.setBounds(elIndicator, pad.x, pad.z, sz.x, sz.y);
		}

		public void remove(Element child) {
		}

		public void constrain(Element child, Object constraints) {
		}
	}
}
