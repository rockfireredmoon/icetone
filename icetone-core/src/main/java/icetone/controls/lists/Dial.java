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
import icetone.core.Element;
import icetone.core.Layout.LayoutType;
import icetone.core.event.ChangeSupport;
import icetone.core.event.UIChangeEvent;
import icetone.core.event.UIChangeListener;
import icetone.core.event.mouse.MouseUIWheelEvent.Direction;
import icetone.css.CssEvent;
import icetone.effects.Effect;
import icetone.effects.Interpolation;

/**
 * @author t0neg0d
 * @author rockfire
 */
public class Dial<V> extends Button {

	public final static CssEvent DIAL = new CssEvent("dial");

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
			for (int i = start; i <= end; i += step) {
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
			return elIndicator.calcMinimumSize().add(container.getTotalPadding());
		}

		@Override
		protected Vector2f calcPreferredSize(Dial<V> container) {
			return elIndicator.calcPreferredSize().add(container.getTotalPadding());
		}

		@Override
		protected void onLayout(Dial<V> parent) {
			Vector2f cSz = parent.getDimensions().clone();
			Vector2f ps = elIndicator.calcPreferredSize();

			float scale = cSz.x / ps.x;
			Vector2f indSz = ps.mult(scale);
			if (indSz.y > cSz.y) {
				scale = cSz.y / ps.y;
				indSz = ps.mult(scale);
			}

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
	private float maxDegrees = 0;
	private float minDegrees = 0;
	private int selectedIndex = -1;
	private boolean isStepped;
	private float stepSize = 1;
	private boolean held;
	private int divisions = 100;

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
	 * @param screen The screen control the Element is to be added to
	 */
	public Dial(BaseScreen screen) {
		super(screen);
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

		onNavigationKey(evt -> {
			if (evt.getKeyCode() == KeyInput.KEY_LEFT) {
				if (evt.isPressed())
					setSelectedIndex(getSelectedIndex() - 1);
				evt.setConsumed();
			} else if (evt.getKeyCode() == KeyInput.KEY_RIGHT) {
				if (evt.isPressed())
					setSelectedIndex(getSelectedIndex() + 1);
				evt.setConsumed();
			}
		});
		onMouseWheel(evt -> {
			if (evt.getDirection() == Direction.up)
				setSelectedIndex(getSelectedIndex() + 1);
			else if (evt.getDirection() == Direction.down)
				setSelectedIndex(getSelectedIndex() - 1);
		});
		onMousePressed(evt -> held = false);
		onMouseHeld(evt -> {
			held = true;
			float fx = screen.getMouseXY().x - elCenter.getAbsoluteX();
			float fy = (screen.getMouseXY().y - elCenter.getAbsoluteHeight()) * -1;
			float angle = (float) Math.atan2(fx, fy) * FastMath.RAD_TO_DEG;
			if (angle < 0)
				angle = FastMath.abs(360 + angle);

			changeAngle(getStepAngle(angle));

		});
		onMouseReleased(evt -> {
			if (!held) {
				float fx = screen.getMouseXY().x - elCenter.getAbsoluteX();
				float fy = (screen.getMouseXY().y - elCenter.getAbsoluteHeight()) * -1;
				float angle = (float) Math.atan2(fx, fy) * FastMath.RAD_TO_DEG;
				if (angle < 0)
					angle = FastMath.abs(360 + angle);

				changeAngle(getStepAngle(angle));
			}
		});

	}

	/**
	 * Get the number of divisions within the entire range of the dial. This only
	 * applies to dials that have not been configured to have one or more step value
	 * (e.g. added with {@link #addStepValue(Object)}.
	 * 
	 * @return number of divisions
	 */
	public int getDivisions() {
		return divisions;
	}

	/**
	 * Set the number of divisions within the entire range of the dial. This only
	 * applies to dials that have not been configured to have one or more step value
	 * (e.g. added with {@link #addStepValue(Object)}.
	 * 
	 * @param divisions number of divisions
	 * @return this for chaining
	 */
	public Dial<V> setDivisions(int divisions) {
		if (divisions != this.divisions) {
			this.divisions = divisions;
			setStepSize();
			setAngleToSelected();
		}
		return this;
	}

	/**
	 * Adds a step value to the Slider. When 2 or more step values are associated
	 * with a Dial, the rotation becomes stepped and advances to the next/previous
	 * slot position as the mouse is moved. Each slot added has an associated value
	 * that is returned via the onChange event or getSelectedValue() method.
	 * 
	 * @param value The string value to add for the next step.
	 */
	public Dial<V> addStepValue(V value) {
		stepValues.add(value);
		setStepSize();
		if (selectedIndex == -1 && stepValues.size() > 1)
			setSelectedIndex(0);
		else
			setAngleToSelected();
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
	 * Returns the text value of the current selected step.
	 * 
	 * @return String stepValue
	 */
	public V getSelectedValue() {
		return selectedIndex == -1 || selectedIndex >= stepValues.size() ? null : stepValues.get(selectedIndex);
	}

	/**
	 * Removes a step value by the value originally added.
	 * 
	 * @param value The string value of the step to be removed.
	 */
	public Dial<V> removeStepValue(V value) {
		stepValues.remove(value);
		setStepSize();
		if (selectedIndex != -1 && selectedIndex >= stepValues.size())
			setSelectedIndex(stepValues.size() < 2 ? -1 : stepValues.size() - 1);
		else
			setAngleToSelected();
		return this;
	}

	protected void setAngleToSelected() {
		if (selectedIndex > -1)
			changeAngle((minDegrees + (selectedIndex * stepSize)) % 360f);
		else
			changeAngle(0);
	}

	/**
	 * Sets the angle at which the maximum rotation of the dial will stop (359 being
	 * the bottom)
	 * 
	 * @param angle float
	 */
	public Dial<V> setGapEndAngle(int angle) {
		angle = angle % 360;
		maxDegrees = angle;
		setStepSize();
		setAngleToSelected();
		return this;
	}

	/**
	 * Sets the angle at which the minimum rotation of the dial will start (0 being
	 * the bottom)
	 * 
	 * @param angle float
	 */
	public Dial<V> setGapStartAngle(int angle) {
		angle = angle % 360;
		minDegrees = angle;
		setStepSize();
		setAngleToSelected();
		return this;
	}

	/**
	 * Sets the selected index for both free-floating and stepped Dials
	 * 
	 * @param index float
	 */
	public Dial<V> setSelectedValue(V value) {
		if (isStepped) {
			setSelectedIndex(stepValues.indexOf(value));
		} else
			setSelectedIndex((Integer) value);
		return this;
	}

	/**
	 * Sets the selected index for both free-floating and stepped Dials
	 * 
	 * @param index float
	 */
	public Dial<V> setSelectedIndex(int index) {
		float angle;
		if (isStepped) {
			if (index < 0)
				index = 0;
			else if (index >= stepValues.size())
				index = stepValues.size() - 1;
			setInternalIndex(index);
			angle = index * stepSize;
		} else {
			if (index < 0)
				index = 0;
			else if (index > divisions - 1)
				index = divisions - 1;
			setInternalIndex(index);
			angle = index * stepSize;
		}

		angle = (minDegrees + angle) % 360f;

		changeAngle(angle);
		return this;
	}

	protected void changeAngle(float angle) {
		if (angle != currentAngle) {

			float start = currentAngle;
			float diff = angle - currentAngle;

			if (diff < 0) {
				/*
				 * Going anticlockwise, if the distance is shorter going clockwise, do that
				 * instead
				 */
				float adiff = 360 - currentAngle + angle;
				if (adiff < FastMath.abs(diff))
					diff = adiff;
			} else {
				/*
				 * Going clockwise, if the distance is shorter going anticlockwise, do that
				 * instead
				 */
				float adiff = 360 - angle + currentAngle;
				if (adiff < FastMath.abs(diff))
					diff = -adiff;
			}

			float fdiff = diff;

			/*
			 * TODO For now, manually create an effect. To do rotation via CSS i think
			 * BaseElement is going to need a rotation parameter that an Effect can act on
			 */

			if (isInStyleHierarchy()) {
				screen.getEffectManager().applyEffect(new Effect(0.15f) {

					@Override
					public void update(float tpf) {
						if (!init) {
							init = true;
						}
						currentAngle = (start + (fdiff * pass)) % 360f;
						dirtyLayout(false, LayoutType.boundsChange());
						layoutChildren();
						updatePass(tpf);
					}
				}.setInterpolation(Interpolation.bounce));
				return;
			}

//			if (isInStyleHierarchy()) {
//				if (triggerCssEvent(new CssEventTrigger<Rot>(DIAL, (evt) -> {
//					evt.setEffectDestination(new Vector2f(angle));
//					evt.setReset(false);
//					dirtyLayout(false, LayoutType.styling, LayoutType.clipping);
//					layoutChildren();
//				})).isProcessed())
//					return;
//			}

			/* Not processed as effect or not in style heirarchy yet */
			currentAngle = angle;
			dirtyLayout(false, LayoutType.boundsChange());
			layoutChildren();
		}
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

		/* Make sure angle is within range */
		float max = getActualMaxDegrees();
		if (minDegrees > max) {
			if (angle < minDegrees && angle > max)
				if (angle > 180)
					angle = minDegrees;
				else
					angle = maxDegrees;
		} else {
			if (angle < minDegrees)
				angle = minDegrees;
			else {
				if (angle > max)
					angle = max;
			}
		}

		/*
		 * Adjust the angle by minDegrees, i.e. the starting angle. So if the selected
		 * angle is the minimum value angle, the angle used for the index calculation
		 * will be zero. The angle is normalised if this is a negative angle.
		 */
		angle -= minDegrees;
		if (angle < 0)
			angle = FastMath.abs(-360 - angle);

		int nIndex;
		if (stepValues.size() >= 2) {
			nIndex = (int) (angle / stepSize);
			if (nIndex >= 0 && nIndex < stepValues.size() && nIndex != this.selectedIndex)
				setInternalIndex(nIndex);
		} else {
			nIndex = (int) (angle / stepSize);
			int finIndex = Math.round(nIndex);
			if (finIndex != this.selectedIndex)
				setInternalIndex(finIndex);
		}

		return ((nIndex * stepSize) + minDegrees) % 360f;
	}

	/**
	 * Sets the Dial's selected index to the selected step index specified and
	 * rotates the Dial to appropriate angle to reflect this change.
	 * 
	 * @param selectedIndex The index to set the Dial's selectedIndex to.
	 */
	private void setInternalIndex(int selectedIndex) {
		if (this.selectedIndex != selectedIndex) {
			int was = this.selectedIndex;
			this.selectedIndex = selectedIndex;
			if (changeSupport != null) {
				if (isStepped)
					changeSupport.fireEvent(new UIChangeEvent<Dial<V>, V>(this, was == -1 ? null : stepValues.get(was),
							stepValues.get(selectedIndex)));
				else
					changeSupport.fireEvent(new UIChangeEvent<Dial<V>, V>(this, null, null));
			}
		}
	}

	private void setStepSize() {
		calcStepSize();
		dirtyLayout(false, LayoutType.boundsChange());
		layoutChildren();
	}

	private void calcStepSize() {
		float ts = getTotalSize();
		isStepped = false;
		if (stepValues.size() == 0)
			stepSize = getTotalSize() / (float)getDivisions();
		else {
			isStepped = true;
			if (stepValues.size() % 2 == 0)
				stepSize = ts / stepValues.size();
			else
				stepSize = ts / (stepValues.size() + 1);
		}
	}

	private float getActualMaxDegrees() {
		return maxDegrees == 0 ? 360 : maxDegrees;
	}

	private float getTotalSize() {
		float s = getActualMaxDegrees() - minDegrees;
		if (s < 0) {
			s = FastMath.abs(-360 - s);
		}
		return s;
	}
}
