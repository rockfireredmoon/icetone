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
import java.util.List;
import java.util.Objects;

import com.jme3.input.KeyInput;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.buttons.Button;
import icetone.core.AbstractGenericLayout;
import icetone.core.BaseElement;
import icetone.core.ElementManager;
import icetone.core.Layout.LayoutType;
import icetone.core.Orientation;
import icetone.core.BaseScreen;
import icetone.core.StyledContainer;
import icetone.core.Element;
import icetone.core.event.ChangeSupport;
import icetone.core.event.MouseUIWheelEvent.Direction;
import icetone.core.utils.ClassUtil;
import icetone.core.event.UIChangeEvent;
import icetone.core.event.UIChangeListener;

/**
 *
 * @author t0neg0d
 * @author rockfire
 */
public class Slider<V extends Number> extends Button {

	//

	public static class SliderLayout<V extends Number> extends AbstractGenericLayout<Slider<V>, Object> {

		@Override
		protected Vector2f calcMinimumSize(Slider<V> container) {
			float size = calcSliderSize(container);
			Vector2f min = new Vector2f();
			if (container.orientation == Orientation.VERTICAL) {
				min.x = size;
				min.y = min.x * 3;
			} else {
				min.y = size;
				min.x = min.y * 3;
			}
			min.addLocal(container.getTotalPadding());
			return min;
		}

		@Override
		protected Vector2f calcPreferredSize(Slider<V> container) {
			Vector2f ps = new Vector2f();
			float size = calcSliderSize(container);
			if (container.orientation == Orientation.VERTICAL) {
				ps.x = size;
				ps.y = (int) ((container.getSliderModel().getMax().doubleValue()
						- container.getSliderModel().getMin().doubleValue())
						/ container.getSliderModel().getStep().doubleValue());
			} else {
				ps.x = (int) ((container.getSliderModel().getMax().doubleValue()
						- container.getSliderModel().getMin().doubleValue())
						/ container.getSliderModel().getStep().doubleValue());
				ps.y = size;
			}
			ps.addLocal(container.getTotalPadding());
			return ps;
		}

		protected float calcSliderSize(Slider<V> container) {
			Vector2f pref = container.elThumb.calcPreferredSize();
			if (container.orientation == Orientation.HORIZONTAL) {
				return pref.y;
			} else {
				return pref.x;
			}
		}

		@Override
		protected void onLayout(Slider<V> container) {
			Vector4f textPadding = container.getAllPadding();
			float controlWidth = container.getWidth();
			float controlHeight = container.getHeight();
			float size = calcSliderSize(container);
			V val = container.model.getValue();
			float i = container.valueToIndex(val.doubleValue());
			if (container.orientation == Orientation.HORIZONTAL) {
				container.elThumbLock.setBounds(i + container.getIndent(), textPadding.z, 1,
						controlHeight - textPadding.w - textPadding.z);
				container.elThumb.setBounds(-(size / 2), 0, size, controlHeight - textPadding.w - textPadding.z);
			} else {
				container.elThumbLock.setBounds(textPadding.x, i - size + container.getIndent(),
						controlWidth - textPadding.x - textPadding.y, 1);
				container.elThumb.setBounds(0, size / 2, controlWidth - textPadding.x - textPadding.y, size);
				container.elThumb.setBounds(0, size / 2, controlWidth - textPadding.x - textPadding.y, size);
			}
		}

	}

	protected Orientation orientation;

	private ChangeSupport<Slider<V>, V> changeSupport;
	private Button elThumb;
	private Element elThumbLock;
	private boolean lockToStep = false;
	private SliderModel<V> model;
	private boolean reversed = false;
	private Vector2f startPosition;
	private MouseButtonEvent trackEvt;

	/**
	 * Creates a new instance of the Slider control when using the single
	 * default screen
	 */
	public Slider() {
		this(Orientation.HORIZONTAL);
	}

	/**
	 * Creates a new instance of the Slider control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public Slider(ElementManager<?> screen) {
		this(screen, Orientation.HORIZONTAL);
	}

	/**
	 * Creates a new instance of the Slider control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param orientation
	 *            Slider.Orientation used to establish Horizontal/Vertical
	 *            layout during control configuration
	 */
	@SuppressWarnings("unchecked")
	public Slider(ElementManager<?> screen, Orientation orientation) {
		super(screen);

		setLayoutManager(new SliderLayout<V>());

		this.orientation = orientation;

		elThumbLock = new StyledContainer(screen) {

			// @Override
			@Override
			public void moveTo(float x, float y) {
				float rat = getRatioForPosition(x, y);
				V oldVal = model.getValue();
				V val = model.getAsRatioOfRange(rat, lockToStep);
				if (!val.equals(oldVal)) {
					setSelectedValue(val);
				}
			}
		};
		elThumbLock.setUseParentPseudoStyles(true);
		elThumbLock.setStyleClass("thumb-lock");
		elThumbLock.setLockToParentBounds(true);
		addElement(elThumbLock);

		elThumb = new Button(screen);
		elThumb.setUseParentPseudoStyles(true);
		elThumb.setStyleClass("thumb");
		elThumb.onMouseReleased(evt -> focus());
		elThumbLock.addElement(elThumb);

		elThumb.setMovable(true);
		elThumb.setAffectParent(true);

		setInterval(100);

		setSliderModel((SliderModel<V>) new FloatRangeSliderModel(0, 100, 0));

		onMousePressed(evt -> {
			trackEvt = evt;
			startPosition = elThumbLock.getPosition().clone();
			updateThumbByTrackClick();
			evt.setConsumed();
		});

		onMouseWheel(evt -> {
			if (orientation == Orientation.VERTICAL) {
				if ((evt.getDirection() == Direction.right && !reversed)
						|| (evt.getDirection() == Direction.left && reversed)
						|| (evt.getDirection() == Direction.down && reversed)
						|| (evt.getDirection() == Direction.up && !reversed))
					downStep();
				else
					upStep();
			} else {
				if ((evt.getDirection() == Direction.right && !reversed)
						|| (evt.getDirection() == Direction.left && reversed)
						|| (evt.getDirection() == Direction.down && reversed)
						|| (evt.getDirection() == Direction.up && !reversed))
					upStep();
				else
					downStep();
			}
			evt.setConsumed();

		});

		onKeyboardPressed(evt -> {
			if ((evt.getKeyCode() == KeyInput.KEY_RIGHT && orientation == Orientation.HORIZONTAL)
					|| (evt.getKeyCode() == KeyInput.KEY_DOWN && orientation == Orientation.VERTICAL)) {
				downStep();
				evt.setConsumed();
			} else if ((evt.getKeyCode() == KeyInput.KEY_LEFT && orientation == Orientation.HORIZONTAL)
					|| (evt.getKeyCode() == KeyInput.KEY_UP && orientation == Orientation.VERTICAL)) {
				upStep();
				evt.setConsumed();
			}
		});
	}

	protected void upStep() {
		startPosition = elThumbLock.getPosition().clone();
		dirtyLayout(false, LayoutType.boundsChange());
		updateStep(false);
	}

	protected void downStep() {
		startPosition = elThumbLock.getPosition().clone();
		dirtyLayout(false, LayoutType.boundsChange());
		updateStep(true);
	}

	/**
	 * Creates a new instance of the Slider control when using the single
	 * default screen
	 * 
	 * @param orientation
	 *            Slider.Orientation used to establish Horizontal/Vertical
	 *            layout during control configuration
	 */
	public Slider(Orientation orientation) {
		this(BaseScreen.get(), orientation);
	}

	public Slider<V> onChanged(UIChangeListener<Slider<V>, V> listener) {
		if (changeSupport == null)
			changeSupport = new ChangeSupport<>();
		changeSupport.bind(listener);
		return this;
	}

	/**
	 * Convenience method to get the value.
	 * 
	 * @return value
	 */
	public V getSelectedValue() {
		return model.getValue();
	}

	/**
	 * Get the slider model. Do not set values on this, instead use
	 * {@link #setSelectedValueNoCallback(double) } and
	 * {@link #setSelectedValue(double) }
	 * 
	 * @return model
	 */
	public SliderModel<V> getSliderModel() {
		return model;
	}

	public BaseElement getThumb() {
		return elThumb;
	}

	@Override
	public String getToolTipText() {
		return this.elThumb.getToolTipText();
	}

	/**
	 * Get whether the slider should lock to whole steps. The slider will set a
	 * value and visually position itself to the next closest step (see
	 * {@link SliderModel#getStep()}.
	 * 
	 * @return lockToStep
	 */
	public boolean isLockToStep() {
		return lockToStep;
	}

	public boolean isReversed() {
		return reversed;
	}

	@Override
	public void onButtonStillPressedInterval() {
		updateThumbByTrackClick();
		if (screen.getToolTipManager() != null)
			screen.getToolTipManager().updateToolTipLocation();
	}

	public Slider<V> removeChangeListener(UIChangeListener<Slider<V>, V> listener) {
		changeSupport.removeListener(listener);
		return this;
	}

	/**
	 * Set whether the slider should lock to whole steps. The slider will set a
	 * value and visually position itself to the next closest step (see
	 * {@link SliderModel#getStep()}.
	 * 
	 * @param lockToStep
	 */
	public void setLockToStep(boolean lockToStep) {
		this.lockToStep = lockToStep;
		dirtyLayout(false, LayoutType.children);
		layoutChildren();
	}

	//

	public void setReversed(boolean reversed) {
		this.reversed = reversed;
		dirtyLayout(false, LayoutType.boundsChange());
		layoutChildren();
	}

	/**
	 * Set the value of the slider and update it's position. No events will be
	 * fired.
	 * 
	 * @param value
	 *            new value
	 */
	public Slider<V> setSelectedValueNoCallback(V value) {
		if (!Objects.equals(model.getValue(), value)) {
			model.setValue(value);
			dirtyLayout(false, LayoutType.children);
			layoutChildren();
		}
		return this;
	}

	/**
	 * Set the value of the slider, update it's position and fire events.
	 * 
	 * @param value
	 *            new value
	 */
	public Slider<V> setSelectedValue(V value) {
		if (!Objects.equals(model.getValue(), value)) {
			V was = model.getValue();
			setSelectedValueNoCallback(value);
			change(was, model.getValue());
		}
		return this;
	}

	/**
	 * Set the slider model. The slider will be initialised with the current
	 * model. No events will be fired.
	 * 
	 * @param model
	 *            slider model
	 */
	public Slider<V> setSliderModel(SliderModel<V> model) {
		this.model = model;
		this.setInterval(1);
		setSelectedValueNoCallback(model.getValue());
		layoutChildren();
		return this;
	}

	@Override
	public BaseElement setToolTipText(String tip) {
		this.elThumb.setToolTipText(tip);
		return this;
	}

	public double stepPerIndex() {
		if (orientation == Orientation.HORIZONTAL) {
			return (getWidth() - (getIndent() * 2)) / getRange();
		} else {
			return getLength() / getRange();
		}
	}

	public Slider<V> unbindChanged(UIChangeListener<Slider<V>, V> listener) {
		if (changeSupport != null)
			changeSupport.unbind(listener);
		return this;
	}

	public Orientation getOrientation() {
		return orientation;
	}

	public Slider<V> setOrientation(Orientation orientation) {
		if (!Objects.equals(orientation, this.orientation)) {
			this.orientation = orientation;
			dirtyLayout(false, LayoutType.reset);
		}
		return this;
	}

	@Override
	public List<String> getStyleClassNames() {
		List<String> l = new ArrayList<>(super.getStyleClassNames());
		l.add((Orientation.HORIZONTAL.equals(getOrientation()) ? "Horizontal" : "Vertical")
				+ ClassUtil.getMainClassName(getClass()));
		return l;
	}

	//

	protected void change(V oldValue, V newValue) {
		if (changeSupport != null)
			changeSupport.fireEvent(new UIChangeEvent<Slider<V>, V>(this, oldValue, newValue));
	}

	protected void updateSliderPosition() {
		final double val = model.getValue().doubleValue();
		final int indexOfValue = valueToIndex(val);
		if (orientation == Orientation.HORIZONTAL) {
			elThumbLock.setY(0);
			elThumbLock.setX(indexOfValue);
		} else {
			elThumbLock.setX(0);
			elThumbLock.setY(indexOfValue);
		}
	}

	protected int valueToIndex(double val) {
		final double stepPerIndex = stepPerIndex();
		int v = (int) ((val - model.getMin().doubleValue()) * stepPerIndex);
		if (reversed)
			v = (int) getLength() - v;
		return v;
	}

	private float getLength() {
		return (orientation == Orientation.HORIZONTAL ? getWidth() : getHeight()) - (getIndent() * 2);
	}

	private double getRange() {
		return model.getMax().doubleValue() - model.getMin().doubleValue();
	}

	private float getRatioForPosition(float x, float y) {
		float val;
		if (orientation.equals(Orientation.HORIZONTAL)) {
			val = Math.min(1f, Math.max(0f, (x - getIndent()) / (getWidth() - (getIndent() * 2))));
		} else {
			val = Math.min(1f, Math.max(0f, y / getLength()));
		}
		if (reversed)
			val = 1f - val;
		return val;
	}

	private void updateStep(boolean forward) {
		V was = model.getValue();
		model.step(forward);
		// updateSliderPosition();
		layoutChildren();
		change(was, model.getValue());
	}

	private void updateThumbByTrackClick() {
		if (orientation == Orientation.HORIZONTAL) {
			if (elThumbLock.getX() > trackEvt.getX() - getAbsoluteX()
					&& startPosition.x > trackEvt.getX() - getAbsoluteX()) {
				updateStep(reversed);
			} else if (elThumbLock.getX() < trackEvt.getX() - getAbsoluteX()
					&& startPosition.x < trackEvt.getX() - getAbsoluteX()) {
				updateStep(!reversed);
			}
		} else {
			if (elThumbLock.getY() > trackEvt.getY() - getAbsoluteY()
					&& startPosition.y > trackEvt.getY() - getAbsoluteY()) {
				updateStep(reversed);
			} else if (elThumbLock.getY() < trackEvt.getY() - getAbsoluteY()
					&& startPosition.y < trackEvt.getY() - getAbsoluteY()) {
				updateStep(!reversed);
			}
		}
	}
}