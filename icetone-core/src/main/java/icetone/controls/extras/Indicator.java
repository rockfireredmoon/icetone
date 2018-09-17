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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

import icetone.controls.text.AbstractTextLayout;
import icetone.core.ClippingDefine;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Layout.LayoutType;
import icetone.core.Orientation;
import icetone.core.Element;
import icetone.core.layout.DefaultLayout;
import icetone.core.utils.ClassUtil;
import icetone.core.utils.MathUtil;
import icetone.framework.animation.Interpolation;

/**
 * @author t0neg0d
 * @author rockfire
 */
public class Indicator extends Element {
	public enum DisplayMode {
		none, percentages, value
	}

	public enum BarMode {
		clip, resize
	}

	public class IndicatorLayout extends AbstractTextLayout<Indicator> {

		@Override
		protected Vector2f calcMaximumSize(Indicator parent) {
			return elIndicator.calcMaximumSize().addLocal(parent.getTotalPadding());
		}

		@Override
		protected Vector2f calcMinimumSize(Indicator parent) {
			return MathUtil.largest(elIndicator.calcMinimumSize(), elOverlay.calcMinimumSize())
					.addLocal(parent.getTotalPadding());
		}

		@Override
		protected Vector2f calcPreferredSize(Indicator parent) {
			return MathUtil.largest(elIndicator.calcPreferredSize(), elOverlay.calcPreferredSize())
					.addLocal(parent.getTotalPadding());
		}

		@Override
		protected void onLayout(Indicator childElement) {
			Vector2f sz = childElement.getDimensions().clone();
			Vector4f pad = childElement.getAllPadding();
			float y = pad.z;
			float x = pad.x;
			sz.subtractLocal(x + pad.y, y + pad.w);
			if (sz.x < 0)
				sz.x = 0;

			if (sz.y < 0)
				sz.y = 0;

			elOverlay.setBounds(pad.x, pad.z, sz.x, sz.y);
			if (barMode == BarMode.resize) {
				if (orientation == Orientation.VERTICAL) {
					float z = sz.y * barFactor;
					if (!reverseDirection) {
						y = sz.y - z;
					}
					sz.y = z;
				} else {
					float z = sz.x * barFactor;
					if (reverseDirection) {
						x = sz.x - z;
					}
					sz.x = z;
				}

			}
			elIndicator.setBounds(x, y, sz.x, sz.y);
		}
	}

	private String alphaMapPath;
	private DisplayMode displayMode = DisplayMode.percentages;
	private Element elIndicator, elOverlay;
	private float maxValue = 0, currentValue = 0, barFactor = 0;
	private Orientation orientation;
	private boolean reverseDirection = false;
	private BarMode barMode = BarMode.resize;
	private float animationTime;
	private Interpolation interpolation = Interpolation.linear;

	/**
	 * Creates a new instance of the Indicator control
	 */
	public Indicator() {
		this(Orientation.HORIZONTAL);
	}

	/**
	 * Creates a new instance of the Indicator control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public Indicator(BaseScreen screen, Orientation orientation) {
		super(screen);

		setStyleClass(orientation.name().toLowerCase());

		layoutManager = new IndicatorLayout();

		setOrientation(orientation);

		elIndicator = new Element(screen) {
			{
				setStyleClass("bar");
			}

		};
		elIndicator.addClippingLayer(elIndicator);
		elIndicator.setLayoutManager(new DefaultLayout() {

			@Override
			protected Vector4f getClipPadding(ClippingDefine def) {
				if (barMode == BarMode.clip) {
					Vector4f pad = def.getElement().getClipPaddingVec().clone();
					if (Indicator.this.getOrientation() == Orientation.HORIZONTAL) {
						float pc = elOverlay.getWidth() * barFactor;
						if (reverseDirection) {
							pad.x = pc;
							pad.z = 0;
						} else {
							pad.z = elOverlay.getWidth() - pc;
							pad.x = 0;
						}
					} else {
						float pc = elOverlay.getHeight() * barFactor;
						if (reverseDirection) {
							pad.y = pc;
							pad.w = 0;
						} else {
							pad.w = elOverlay.getHeight() - pc;
							pad.y = 0;
						}
					}
					return pad;
				} else {
					return super.getClipPadding(def);
				}
			}

		});
		elIndicator.setIgnoreMouse(true);
		addElement(elIndicator);

		elOverlay = new Element(screen) {
			{
				setStyleClass("overlay");
			}
		};
		elOverlay.setIgnoreMouse(true);
		setIgnoreMouse(true);

		// Load default font info

		addElement(elOverlay);
	}

	/**
	 * Creates a new instance of the Indicator control
	 * 
	 * @param orientation
	 *            The orientation
	 */
	public Indicator(Orientation orientation) {
		this(BaseScreen.get(), orientation);
	}

	public float getAnimationTime() {
		return animationTime;
	}

	public Indicator setAnimationTime(float animationTime) {
		if (this.animationTime != animationTime) {
			this.animationTime = animationTime;
			if (animationTime == 0)
				removeControl(ProgressAnimControl.class);
			else if (getControl(ProgressAnimControl.class) == null)
				addControl(new ProgressAnimControl());
		}
		return this;
	}

	@Override
	public void controlResizeHook() {
		refactorIndicator();
	}

	/**
	 * Returns current value as a percent of the max value
	 * 
	 * @return percentage
	 */
	public float getCurrentPercentage() {
		return (currentValue / maxValue) * 100f;
	}

	/**
	 * Returns the current value of the Indicator
	 * 
	 * @return currentValue
	 */
	public float getCurrentValue() {
		return this.currentValue;
	}

	public Interpolation getInterpolation() {
		return interpolation;
	}

	public Indicator setInterpolation(Interpolation interpolation) {
		this.interpolation = interpolation;
		return this;
	}

	public BarMode getBarMode() {
		return barMode;
	}

	public Indicator setBarMode(BarMode barMode) {
		if (!Objects.equals(barMode, this.barMode)) {
			this.barMode = barMode;
			if (elOverlay != null)
				refactorIndicator();
		}
		return this;
	}

	public DisplayMode getDisplayMode() {
		return displayMode;
	}

	public BaseElement getIndicatorElement() {
		return elIndicator;
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
	 * Returns the Indicator.Orientation of the Indicator instance
	 * 
	 * @return Indicator.Orientation
	 */
	public Orientation getOrientation() {
		return this.orientation;
	}

	public BaseElement getOverlayElement() {
		return elOverlay;
	}

	/**
	 * Applies an alpha map to the indicator, allowing for curved shapes
	 * 
	 * @param alphaMapPath
	 */
	@Override
	public BaseElement setAlphaMap(String alphaMapPath) {
		// TODO no CSS for this yet
		this.alphaMapPath = alphaMapPath;
		elIndicator.setAlphaMap(this.alphaMapPath);
		return this;
	}

	/**
	 * Sets the current value of the Indicator
	 * 
	 * @param currentValue
	 */
	public Indicator setCurrentValue(float currentValue) {
		float was = this.currentValue;
		if (was != currentValue) {
			this.currentValue = currentValue;
			if (animationTime > 0) {
				refactorIndicator();
				;
				getControl(ProgressAnimControl.class).init(was, currentValue, animationTime);
			} else
				refactorIndicator();
		}
		return this;
	}

	public Indicator setDisplayMode(DisplayMode displayMode) {
		this.displayMode = displayMode;
		refactorIndicator();
		if (displayMode == DisplayMode.none)
			elOverlay.setText("");
		return this;
	}

	/**
	 * Convenience operator to set the text on the indicator..
	 * 
	 * @param color
	 *            color
	 * @return this for chaining
	 */
	public Indicator setIndicatorColor(ColorRGBA color) {
		elIndicator.setDefaultColor(color);
		return this;
	}

	/**
	 * Convenience operator to set the text on the indicator..
	 * 
	 * @param text
	 *            text
	 * @return this for chaining
	 */
	public Indicator setIndicatorText(String text) {
		elOverlay.setText(text);
		refactorIndicator();
		return this;
	}

	/**
	 * Set the maximum value (e.g. float = 100%)
	 * 
	 * @param maxValue
	 */
	public Indicator setMaxValue(float maxValue) {
		this.maxValue = maxValue;
		refactorIndicator();
		return this;
	}

	public Indicator setOrientation(Orientation orientation) {
		if (!Objects.equals(orientation, this.orientation)) {
			this.orientation = orientation;
			dirtyLayout(true, LayoutType.all);
			layoutChildren();
		}
		return this;
	}

	/**
	 * Set the image to use in front of the indicator
	 * 
	 * @param imgPath
	 */
	public void setOverlayImage(String imgPath) {
		elOverlay.setTexture(imgPath);
	}

	public Indicator setReverseDirection(boolean reverseDirection) {
		if (reverseDirection != this.reverseDirection) {
			this.reverseDirection = reverseDirection;
			refactorIndicator();
		}
		return this;
	}

	@Override
	public List<String> getStyleClassNames() {
		List<String> l = new ArrayList<>(super.getStyleClassNames());
		l.add(ClassUtil.getMainClassName(getClass())
				+ (orientation == Orientation.HORIZONTAL ? "Horizontal" : "Vertical"));
		return l;
	}

	protected void onChange(float currentValue, float currentPercentage) {

	}

	protected final void refactorIndicator() {
		refactorText();
		refactorBar(currentValue);
		onChange(currentValue, getCurrentPercentage());
	}

	protected void refactorText() {
		if (displayMode != null) {
			switch (displayMode) {
			case percentages:
				elOverlay.setText((int) getCurrentPercentage() + "%");
				break;
			case value:
				elOverlay.setText(String.valueOf((int) currentValue) + "/" + String.valueOf((int) maxValue));
				break;
			default:
				break;
			}
		}
	}

	protected void refactorBar(float currentValue) {
		if (currentValue > maxValue) {
			currentValue = maxValue;
		} else if (currentValue < 0f) {
			currentValue = 0f;
		}
		barFactor = currentValue / maxValue;
		if (barMode == BarMode.clip) {
			elIndicator.dirtyLayout(false, LayoutType.clipping);
			elIndicator.layoutChildren();
		} else {
			dirtyLayout(true, LayoutType.boundsChange());
			layoutChildren();
		}
	}

	class ProgressAnimControl extends AbstractControl {

		private float time;
		private float start;
		private float end;
		private float progress;
		private float inter;

		void init(float start, float end, float time) {
			this.time = time;
			this.start = start;
			this.end = end;
			this.progress = 0;
		}

		@Override
		protected void controlUpdate(float tpf) {
			if (progress == time)
				return;

			progress += tpf;
			if (progress >= time) {
				progress = time;
			}
			inter = progress / time;
			// if (end > start) {
			// refactorBar(start + ((end - start) * inter));
			// } else {
			// refactorBar(start - ((start - end) * inter));
			// }
			if (end > start) {
				refactorBar(interpolation.apply(start, end, inter));
			} else {
				refactorBar(interpolation.apply(start, end, inter));
			}
		}

		@Override
		protected void controlRender(RenderManager rm, ViewPort vp) {
		}

	}
}
