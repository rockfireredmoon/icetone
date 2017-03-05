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
package icetone.controls.lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.jme3.input.KeyInput;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;

import icetone.controls.buttons.Button;
import icetone.core.AbstractGenericLayout;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Layout.LayoutType;
import icetone.core.Element;
import icetone.core.event.ChangeSupport;
import icetone.core.event.UIChangeEvent;
import icetone.core.event.UIChangeListener;

/**
 * @author t0neg0d
 * @author rockfire
 */
public class Dial<V> extends Button {

	public static class IntegerRangeDial extends Dial<Integer> {

		public IntegerRangeDial(int start, int end) {
			this(start, end, 1);
		}

		public IntegerRangeDial(int start, int end, int step) {
			super();
			build(start, end, step);
		}

		public IntegerRangeDial(BaseScreen screen, int start, int end) {
			this(screen, start, end, 1);
		}

		public IntegerRangeDial(BaseScreen screen, int start, int end, int step) {
			super(screen);
			build(start, end, step);
		}

		protected void build(int start, int end, int step) {
			for (int i = start; i < end; i += step) {
				addStepValue(i);
			}
		}
	}

	public static class FloatRangeDial extends Dial<Float> {

		public FloatRangeDial(float start, float end, float step) {
			super();
			build(start, end, step);
		}

		public FloatRangeDial(BaseScreen screen, float start, float end, float step) {
			super(screen);
			build(start, end, step);
		}

		protected void build(float start, float end, float step) {
			for (float i = start; i < end; i += step) {
				addStepValue(i);
			}
		}
	}

	public static class StringListDial extends Dial<String> {
		public StringListDial(Collection<String> strings) {
			super();
			build(strings);

		}

		public StringListDial(BaseScreen screen, Collection<String> strings) {
			super(screen);
			build(strings);
		}

		public StringListDial(String... strings) {
			super();
			build(strings);

		}

		public StringListDial(BaseScreen screen, String... strings) {
			super(screen);
			build(strings);
		}

		protected void build(String... it) {
			for (String s : it) {
				addStepValue(s);
			}

		}

		protected void build(Iterable<String> it) {
			for (String s : it) {
				addStepValue(s);
			}
		}
	}

	class DialLayout extends AbstractGenericLayout<Dial<V>, Object> {
		@Override
		protected Vector2f calcMinimumSize(Dial<V> container) {
			return calcPreferredSize(container);
		}

		@Override
		protected Vector2f calcPreferredSize(Dial<V> container) {
			return elIndicator.calcPreferredSize().add(container.getTotalPadding());
		}

		@Override
		protected void onLayout(Dial<V> parent) {
			Vector2f indSz = elIndicator.calcPreferredSize();
			elCenter.setLocalRotation(new Quaternion());
			elCenter.setBounds(parent.getWidth() / 2 - 1, (parent.getHeight() / 2) - indSz.y, indSz.x, indSz.y);
			elCenter.getGeometry().center();
			elIndicator.setDimensions(indSz.x, indSz.y);
			elIndicator.setPosition(new Vector2f(0, 0));
			elIndicator.getGeometry().center();
			elCenter.setLocalRotation(
					elCenter.getLocalRotation().fromAngleAxis(-(currentAngle * FastMath.DEG_TO_RAD), Vector3f.UNIT_Z));

		}

	}

	protected List<V> stepValues = new ArrayList<V>();
	private ChangeSupport<Dial<V>, V> changeSupport;
	private float currentAngle = 0;
	private Element elCenter, elIndicator;
	private boolean isStepped = false;
	private float maxDegrees = 359;
	private float minDegrees = 0;
	private int selectedIndex = 0;
	private float startGap, endGap, totalGap, totalSize;

	private float stepSize = 1;

	/**
	 * Creates a new instance of the Dial control
	 * 
	 */
	public Dial() {
		this(BaseScreen.get());
	}

	/**
	 * Creates a new instance of the Dial control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public Dial(BaseScreen screen) {
		super(screen);

		startGap = minDegrees;
		endGap = 359 - maxDegrees;
		totalGap = startGap + endGap;
		totalSize = 359 - totalGap;

		calcStepSize();

		setInterval(100);
	}

	@Override
	protected void configureStyledElement() {
		super.configureStyledElement();
		layoutManager = new DialLayout();
		setStyleClass("dial");
		elCenter = new Element(screen) {
			{
				setStyleClass("hub");
			}
		};
		elCenter.setUseParentPseudoStyles(true);
		((Geometry) elCenter.getChild(0)).center();
		addElement(elCenter);

		elIndicator = new Element(screen) {
			{
				setStyleClass("radial");
			}
		};
		elIndicator.setUseParentPseudoStyles(true);
		((Geometry) elIndicator.getChild(0)).center();
		elCenter.addElement(elIndicator);

		onKeyboardPressed(evt -> {
			if (evt.getKeyCode() == KeyInput.KEY_LEFT) {
				setSelectedIndexWithCallback(getSelectedIndex() - 1);
				evt.setConsumed();
			} else if (evt.getKeyCode() == KeyInput.KEY_RIGHT) {
				setSelectedIndexWithCallback(getSelectedIndex() + 1);
				evt.setConsumed();
			}
		});

		onMouseHeld(evt -> {
			float fx = screen.getMouseXY().x - elCenter.getAbsoluteX();
			float fy = (screen.getMouseXY().y - elCenter.getAbsoluteHeight()) * -1;

			currentAngle = (float) Math.atan2(fx, fy) * FastMath.RAD_TO_DEG;

			if (currentAngle < -(180 - startGap))
				currentAngle = -(180 - startGap);
			else if (currentAngle > (180 - endGap))
				currentAngle = (180 - endGap);

			float angle = Float.valueOf(currentAngle);

			angle = getStepAngle(angle);

			currentAngle = angle;
			dirtyLayout(false, LayoutType.boundsChange());
			layoutChildren();

		});

	}

	/**
	 * Adds a step value to the Slider. When 2 or more step values are
	 * associated with a Dial, the rotation becomes stepped and advances to the
	 * next/previous slot position as the mouse is moved. Each slot added has an
	 * associated value that is returned via the onChange event or
	 * getSelectedValue() method.
	 * 
	 * @param value
	 *            The string value to add for the next step.
	 */
	public Dial<V> addStepValue(V value) {
		stepValues.add(value);
		if (stepValues.size() >= 2) {
			isStepped = true;
			setStepSize();
		}
		return this;
	}

	public Dial<V> onChange(UIChangeListener<Dial<V>, V> listener) {
		if (changeSupport == null)
			changeSupport = new ChangeSupport<>();
		changeSupport.bind(listener);
		return this;
	}

	public BaseElement getDialCenter() {
		return this.elCenter;
	}

	public BaseElement getDialIndicator() {
		return this.elIndicator;
	}

	/**
	 * Returns the text value of the current selected step.
	 * 
	 * @return String stepValue
	 */
	public int getSelectedIndex() {
		return selectedIndex;
	}

	/**
	 * Removes a step value by the value originally added.
	 * 
	 * @param value
	 *            The string value of the step to be removed.
	 */
	public Dial<V> removeStepValue(V value) {
		stepValues.remove(value);
		if (stepValues.size() < 2) {
			isStepped = false;
			setStepSize();
		}
		return this;
	}

	/**
	 * Sets the angle at which the maximum rotation of the dial will stop (359
	 * being the bottom)
	 * 
	 * @param angle
	 *            float
	 */
	public Dial<V> setGapEndAngle(int angle) {
		if (angle > 359)
			angle = 359;
		maxDegrees = angle;
		startGap = minDegrees;
		endGap = 359 - maxDegrees;
		totalGap = startGap + endGap;
		totalSize = 359 - totalGap;
		setStepSize();
		return this;
	}

	/**
	 * Sets the angle at which the minimum rotation of the dial will start (0
	 * being the bottom)
	 * 
	 * @param angle
	 *            float
	 */
	public Dial<V> setGapStartAngle(int angle) {
		if (angle < 0)
			angle = 0;
		minDegrees = angle;
		startGap = minDegrees;
		endGap = 359 - maxDegrees;
		totalGap = startGap + endGap;
		totalSize = 359 - totalGap;
		setStepSize();
		return this;
	}

	/**
	 * For use with free-floating Dials - Sets the selected index of the Dial
	 * 
	 * @param index
	 *            float A range from 0.0 to 100.0 for a more accurate
	 *            representation of the angle desired
	 */
	public Dial<V> setSelectedIndex(float index) {
		float angle;
		int index1 = Math.round(index);
		if (isStepped) {
			if (index1 < 0)
				index1 = 0;
			else if (index1 > stepValues.size() - 1)
				index1 = stepValues.size() - 1;
			this.selectedIndex = index1;
			angle = index1 * stepSize - 180 + startGap;
		} else {
			if (index1 < 0)
				index1 = 0;
			else if (index1 > 100)
				index1 = 100;
			this.selectedIndex = index1;
			angle = index * stepSize - 180 + startGap;
		}

		currentAngle = angle;
		dirtyLayout(false, LayoutType.boundsChange());
		layoutChildren();
		return this;
	}

	/**
	 * Sets the selected index for both free-floating and stepped Dials
	 * 
	 * @param index
	 *            float
	 */
	public Dial<V> setSelectedIndex(int index) {
		float angle;
		if (isStepped) {
			if (index < 0)
				index = 0;
			else if (index > stepValues.size() - 1)
				index = stepValues.size() - 1;
			selectedIndex = index;
			angle = index * stepSize - 180 + startGap;
		} else {
			if (index < 0)
				index = 0;
			else if (index > 100)
				index = 100;
			selectedIndex = index;
			angle = index * stepSize - 180 + startGap;
		}

		currentAngle = angle;
		dirtyLayout(false, LayoutType.boundsChange());
		layoutChildren();
		return this;
	}

	/**
	 * For use with free-floating Dials - Sets the selected index of the Dial
	 * 
	 * @param index
	 *            float A range from 0.0 to 100.0 for a more accurate
	 *            representation of the angle desired
	 */
	public Dial<V> setSelectedIndexWithCallback(float index) {
		float angle;
		int index1 = Math.round(index);
		if (isStepped) {
			if (index1 < 0)
				index1 = 0;
			else if (index1 > stepValues.size() - 1)
				index1 = stepValues.size() - 1;
			setInternalIndex(index1);
			angle = index1 * stepSize - 180 + startGap;
		} else {
			if (index1 < 0)
				index1 = 0;
			else if (index1 > 100)
				index1 = 100;
			setInternalIndex(index1);
			angle = index * stepSize - 180 + startGap;
		}

		currentAngle = angle;
		dirtyLayout(false, LayoutType.boundsChange());
		layoutChildren();
		return this;
	}

	/**
	 * Sets the selected index for both free-floating and stepped Dials
	 * 
	 * @param index
	 *            float
	 */
	public Dial<V> setSelectedIndexWithCallback(int index) {
		float angle;
		if (isStepped) {
			if (index < 0)
				index = 0;
			else if (index > stepValues.size() - 1)
				index = stepValues.size() - 1;
			setInternalIndex(index);
			angle = index * stepSize - 180 + startGap;
		} else {
			if (index < 0)
				index = 0;
			else if (index > 100)
				index = 100;
			setInternalIndex(index);
			angle = index * stepSize - 180 + startGap;
		}

		currentAngle = angle;
		dirtyLayout(false, LayoutType.boundsChange());
		layoutChildren();
		return this;
	}

	public Dial<V> unbindChanged(UIChangeListener<Dial<V>, V> listener) {
		if (changeSupport != null)
			changeSupport.unbind(listener);
		return this;
	}

	public Dial<V> addChangeListener(UIChangeListener<Dial<V>, V> listener) {
		if (changeSupport == null)
			changeSupport = new ChangeSupport<>();
		changeSupport.addListener(listener);
		return this;
	}

	public Dial<V> removeChangeListener(UIChangeListener<Dial<V>, V> listener) {
		if (changeSupport == null)
			changeSupport = new ChangeSupport<>();
		changeSupport.removeListener(listener);
		return this;
	}

	private float getStepAngle(float angle) {
		angle += 180 - startGap;

		int nIndex;
		if (stepValues.size() >= 2) {
			nIndex = Math.round(angle / stepSize);
			if (nIndex >= 0 && nIndex < stepValues.size() && nIndex != this.selectedIndex)
				setInternalIndex(nIndex);
		} else {
			nIndex = Math.round(angle / stepSize);
			int finIndex = Math.round(nIndex);
			if (finIndex != this.selectedIndex)
				setInternalIndex(finIndex);
		}

		return (nIndex * stepSize) - 180 + startGap;
	}

	/**
	 * Sets the Dial's selected index to the selected step index specified and
	 * rotates the Dial to appropriate angle to reflect this change.
	 * 
	 * @param selectedIndex
	 *            The index to set the Dial's selectedIndex to.
	 */
	private void setInternalIndex(int selectedIndex) {
		if (this.selectedIndex != selectedIndex) {
			int was = this.selectedIndex;
			this.selectedIndex = selectedIndex;
			if (isStepped)
				changeSupport.fireEvent(new UIChangeEvent<Dial<V>, V>(this, was == -1 ? null : stepValues.get(was),
						stepValues.get(selectedIndex)));
			else
				changeSupport.fireEvent(new UIChangeEvent<Dial<V>, V>(this, null, null));
		}
	}

	private void setStepSize() {
		calcStepSize();
		System.out.println("step size " + stepSize + " total size; " + totalSize + " stevals: " + stepValues.size());
		dirtyLayout(false, LayoutType.boundsChange());
		layoutChildren();
	}

	private void calcStepSize() {
		if (stepValues.size() >= 2)
			stepSize = totalSize / (stepValues.size() - 1);
		else
			stepSize = totalSize / 100;
	}
}
