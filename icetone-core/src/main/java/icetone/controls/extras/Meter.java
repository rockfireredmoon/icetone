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

import icetone.controls.text.Label;
import icetone.core.BaseScreen;
import icetone.core.Orientation;
import icetone.core.Element;
import icetone.core.layout.FillLayout;
import icetone.core.layout.mig.MigLayout;

/**
 * @author rockfire
 */
public class Meter extends Element {

	private Orientation orientation;
	private int currentValue;
	private int maxValue = 0;
	private int minValue = 0;
	private boolean reverseDirection;
	private boolean showValue;

	/**
	 * Creates a new instance of the Indicator control
	 */
	public Meter() {
		this(Orientation.HORIZONTAL);
	}

	/**
	 * Creates a new instance of the Meter control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public Meter(BaseScreen screen, Orientation orientation) {
		super(screen);

		setStyleClass(orientation.name().toLowerCase());
		setOrientation(orientation);
		setIgnoreMouse(true);
		setMaxValue(5);
	}

	/**
	 * Creates a new instance of the Meter control
	 * 
	 * @param orientation
	 *            The orientation
	 */
	public Meter(Orientation orientation) {
		this(BaseScreen.get(), orientation);
	}

	/**
	 * Returns the current value of the Meter
	 * 
	 * @return currentValue
	 */
	public int getCurrentValue() {
		return this.currentValue;
	}

	/**
	 * Returns the maximum value set for the Meter
	 * 
	 * @return maxValue
	 */
	public int getMaxValue() {
		return this.maxValue;
	}

	/**
	 * Returns the minimum value set for the Meter
	 * 
	 * @return minValue
	 */
	public int getMinValue() {
		return this.minValue;
	}

	/**
	 * Returns the orientation of the Meter instance
	 * 
	 * @return orientation
	 */
	public Orientation getOrientation() {
		return this.orientation;
	}

	/**
	 * Sets the current value of the Meter
	 * 
	 * @param currentValue
	 */
	public Meter setCurrentValue(int currentValue) {
		int was = this.currentValue;
		if (was != currentValue) {
			this.currentValue = currentValue;
			for (int i = minValue; i < maxValue; i++) {
				if (shouldHide(i))
					getElements().get(i - minValue).getElements().get(0).hide();
				else
					getElements().get(i - minValue).getElements().get(0).show();
			}
		}
		return this;
	}

	public boolean isShowValue() {
		return showValue;
	}

	public Meter setShowValue(boolean showValue) {
		if (showValue != this.showValue) {
			this.showValue = showValue;
			if (configured)
				rebuildElements();
		}
		return this;
	}

	protected boolean shouldHide(int i) {
		return ((orientation == Orientation.HORIZONTAL && !reverseDirection)
				|| (orientation == Orientation.VERTICAL && reverseDirection)) ? i >= currentValue : i < currentValue;
	}

	public Meter setMaxValue(int maxValue) {
		if (maxValue < 1)
			throw new IllegalArgumentException("Minimum size is 1");
		if (this.maxValue != maxValue) {
			this.maxValue = maxValue;
			if (configured)
				rebuildElements();
		}
		return this;
	}

	public Meter setMinValue(int minValue) {
		if (this.minValue != minValue) {
			this.minValue = minValue;
			if (configured)
				rebuildElements();
		}
		return this;
	}

	public Meter setOrientation(Orientation orientation) {
		if (!Objects.equals(orientation, this.orientation)) {
			this.orientation = orientation;
			if (orientation == Orientation.HORIZONTAL)
				setLayoutManager(new MigLayout(screen, "ins 0"));
			else
				setLayoutManager(new MigLayout(screen, "wrap 1, ins 0"));
		}
		return this;
	}

	public boolean isReverseDirection() {
		return reverseDirection;
	}

	public Meter setReverseDirection(boolean reverseDirection) {
		if (this.reverseDirection != reverseDirection) {
			this.reverseDirection = reverseDirection;
			if (configured)
				rebuildElements();
		}
		return this;
	}

	@Override
	public List<String> getStyleClassNames() {
		List<String> l = new ArrayList<>(super.getStyleClassNames());
		l.add(getClass().getSimpleName() + (orientation == Orientation.HORIZONTAL ? "Horizontal" : "Vertical"));
		return l;
	}
	
	protected String getValueString(int value) {
		return String.valueOf(value);
	}

	protected void rebuildElements() {
		invalidate();
		removeAllChildren();
		for (int i = minValue; i < maxValue; i++) {
			Element child = new Element(screen) {
				{
					setIgnoreMouse(true);
					setStyleClass("slot");
				}
			};
			child.setLayoutManager(new FillLayout());
			Element childBulb = new Element(screen) {
				{
					setIgnoreMouse(true);
					setStyleClass("bulb");
				}
			};
			if(shouldHide(i))
				child.attachElement(childBulb);
			else
				child.addElement(childBulb);
			addElement(child);
			if (showValue) {
				child.addElement(new Label(getValueString(i), screen));
			}
		}
		validate();
	}

}
