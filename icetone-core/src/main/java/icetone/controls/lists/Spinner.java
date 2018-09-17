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
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.buttons.Button;
import icetone.controls.text.AbstractTextLayout;
import icetone.controls.text.TextField;
import icetone.controls.text.TextField.Type;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Layout.LayoutType;
import icetone.core.Orientation;
import icetone.core.Element;
import icetone.core.event.ChangeSupport;
import icetone.core.event.UIChangeEvent;
import icetone.core.event.UIChangeListener;
import icetone.core.utils.BitmapTextUtil;
import icetone.core.utils.ClassUtil;

/**
 *
 * @author t0neg0d
 * @author rockfire
 */
public class Spinner<V> extends Element {

	class SpinnerLayout extends AbstractTextLayout<Spinner<?>> {

		@Override
		protected Vector2f calcMinimumSize(Spinner<?> parent) {
			return calcPreferredBackgroundSize(parent);
		}

		@Override
		protected Vector2f calcPreferredSize(Spinner<?> parent) {
			Vector4f textPadding = getAllPadding();
			Vector2f pref = textField.calcPreferredSize();
			float w = BitmapTextUtil.getTextWidth(parent, "Xg") * model.getLargestValueSize(valueFormat);
			Vector2f ih = btnInc.calcPreferredSize();
			Vector2f id = btnDec.calcPreferredSize();
			float h = Math.max(pref.y, Math.max(ih.y, id.y) + textPadding.z + textPadding.w);
			pref.y = Math.max(pref.y, h);
			pref.x = id.x + ih.x + w + textPadding.x + textPadding.y + (parent.getIndent() * 2);
			return pref;
		}

		@Override
		protected void onLayout(Spinner<?> childElement) {
			Vector4f pad = childElement.getAllPadding();
			Vector2f sz = childElement.getDimensions();
			overlay.setBounds(childElement.getBounds());
			btnDec.setBounds(pad.x, pad.y, sz.y - pad.z - pad.w, sz.y - pad.z - pad.w);
			btnInc.setBounds(sz.x - pad.y - sz.y, pad.y, sz.y - pad.z - pad.w, sz.y - pad.z - pad.w);
			textField.setBounds(pad.x + sz.y + childElement.getIndent(), pad.y,
					sz.x - (sz.y + childElement.getIndent()) * 2, sz.y - pad.z - pad.w);
		}
	}

	private Button btnInc, btnDec;
	private ChangeSupport<Spinner<V>, V> changeSupport;
	private boolean cycle = false;
	private SpinnerModel<V> model;
	private TextField textField;
	private Element overlay;
	private String valueFormat = "%s";
	private Orientation orientation;

	{
		doInitialLayout = false;
	}

	/**
	 * Creates a new instance of the Spinner control
	 */
	public Spinner() {
		this(BaseScreen.get());
	}

	/**
	 * Creates a new instance of the Spinner control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public Spinner(BaseScreen screen) {
		this(screen, Orientation.HORIZONTAL, false);
	}

	/**
	 * Creates a new instance of the Spinner control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param orientation
	 *            Orientation used to establish Horizontal/Vertical layout
	 *            during control configuration
	 * @param cycle
	 *            Boolean used to determine if the spinner should cycle back
	 *            through values
	 */
	public Spinner(BaseScreen screen, Orientation orientation, boolean cycle) {
		super(screen);
		this.cycle = cycle;
		this.orientation = orientation;
		addElement(btnDec);
		addElement(textField);
		addElement(btnInc);
		addElement(overlay);
		layoutChildren();
	}

	/**
	 * Creates a new instance of the Spinner control
	 * 
	 * @param orientation
	 *            Orientation used to establish Horizontal/Vertical layout
	 *            during control configuration
	 * @param cycle
	 *            Boolean used to determine if the spinner should cycle back
	 *            through values
	 */
	public Spinner(Orientation orientation, boolean cycle) {
		this(BaseScreen.get(), orientation, cycle);
	}

	/**
	 * Creates a new instance of the Spinner control
	 */
	public Spinner(SpinnerModel<V> model) {
		this(BaseScreen.get());
		setSpinnerModel(model);
	}

	public Spinner<V> addChangeListener(UIChangeListener<Spinner<V>, V> listener) {
		if (changeSupport == null)
			changeSupport = new ChangeSupport<>();
		changeSupport.addListener(listener);
		return this;
	}

	public Orientation getOrientation() {
		return orientation;
	}

	public Spinner<V> setOrientation(Orientation orientation) {
		if (!orientation.equals(this.orientation)) {
			this.orientation = orientation;
			dirtyLayout(true);
			layoutChildren();
		}
		return this;
	}

	/**
	 * Get the decrease button
	 * 
	 * @return decrease button
	 */
	public Button getDecButton() {
		return btnDec;
	}

	/**
	 * Get the increase button
	 * 
	 * @return increase button
	 */
	public Button getIncButton() {
		return btnInc;
	}

	public V getSelectedValue() {
		return model.getCurrentValue();
	}

	/**
	 * Get the spinner model in use.
	 * 
	 * @return model
	 */
	public SpinnerModel<V> getSpinnerModel() {
		return model;
	}

	public boolean isEditable() {
		return textField.isEditable();
	}

	@Override
	public List<String> getStyleClassNames() {
		List<String> l = new ArrayList<>(super.getStyleClassNames());
		String editableStr = isEditable() ? "Editable" : "ReadOnly";
		String orStr = orientation == Orientation.HORIZONTAL ? "Horizontal" : "Vertical";
		l.add(editableStr + ClassUtil.getMainClassName(getClass()));
		l.add(orStr + ClassUtil.getMainClassName(getClass()));
		l.add(editableStr + orStr + ClassUtil.getMainClassName(getClass()));
		return l;
	}

	public TextField getTextField() {
		return textField;
	}

	public Spinner<V> onChange(UIChangeListener<Spinner<V>, V> listener) {
		if (changeSupport == null)
			changeSupport = new ChangeSupport<>();
		changeSupport.bind(listener);
		return this;
	}

	public Spinner<V> removeChangeListener(UIChangeListener<Spinner<V>, V> listener) {
		if (changeSupport == null)
			changeSupport = new ChangeSupport<>();
		changeSupport.removeListener(listener);
		return this;
	}

	public BaseElement setEditable(boolean editable) {
		if (editable != this.textField.isEditable()) {
			textField.setEditable(editable);
			textField.setHoverable(!editable);
			setHoverable(editable);
			setFocusRootOnly(!editable);
		}
		return this;
	}

	public Spinner<V> setFormatterString(String valueFormat) {
		this.valueFormat = valueFormat;
		displaySelectedStep();
		return this;
	}

	/**
	 * Sets the interval speed for the spinner
	 * 
	 * @param callsPerSecond
	 *            float
	 */
	public Spinner<V> setInterval(float callsPerSecond) {
		btnInc.setInterval(callsPerSecond);
		btnDec.setInterval(callsPerSecond);
		return this;
	}

	/**
	 * Enables/disables the TextField and buttons
	 * 
	 * @param isEnabled
	 *            boolean
	 */
	@Override
	public BaseElement setEnabled(boolean isEnabled) {
		super.setEnabled(isEnabled);
		getDecButton().setEnabled(isEnabled);
		getIncButton().setEnabled(isEnabled);
		return this;
	}

	/**
	 * Set the current value. Object must be of a type that can be converted to
	 * a string a parsed by the model.
	 * 
	 * @param value
	 *            value
	 */
	public Spinner<V> setSelectedValue(V value) {
		V oldValue = getSelectedValue();
		if (!Objects.equals(oldValue, value)) {
			model.setValueFromString(String.valueOf(value));
			displaySelectedStep();
		}
		return this;
	}

	/**
	 * Set the current value. Object must be of a type that can be converted to
	 * a string a parsed by the model.
	 * 
	 * @param value
	 *            value
	 */
	public Spinner<V> setSelectedValueWithCallback(V value) {
		V oldValue = getSelectedValue();
		if (!Objects.equals(oldValue, value)) {
			setSelectedValue(value);
			change(oldValue, model.getCurrentValue());
		}
		return this;
	}

	/**
	 * Set the spinner model to use. Setting this will update the current value.
	 * 
	 * @param model
	 *            model
	 */
	public Spinner<V> setSpinnerModel(SpinnerModel<V> model) {
		this.model = model;
		if (model instanceof IntegerRangeSpinnerModel || model instanceof FloatRangeSpinnerModel)
			getTextField().setType(Type.NUMERIC);
		else if (model instanceof StringRangeSpinnerModel)
			getTextField().setType(Type.DEFAULT);
		displaySelectedStep();
		return this;
	}

	public Spinner<V> unbindChanged(UIChangeListener<Spinner<V>, V> listener) {
		if (changeSupport != null)
			changeSupport.unbind(listener);
		return this;
	}

	protected void change(V oldValue, V newValue) {
		if (changeSupport != null)
			changeSupport.fireEvent(new UIChangeEvent<Spinner<V>, V>(this, oldValue, newValue));
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void configureStyledElement() {
		super.configureStyledElement();

		layoutManager = new SpinnerLayout();
		model = (SpinnerModel<V>) new IntegerRangeSpinnerModel();

		textField = new TextField(screen) {
			{
//				setUseParentPseudoStyles(true);
			}
		};
		textField.setHoverable(false);
//		setMouseFocusable(true);
//		setFocusRootOnly(false);

		// Overlay
		overlay = new Element() {
			{
				setStyleClass("overlay");
			}
		};

		// Increase
		btnInc = new Button(screen) {
			{
				setStyleClass("increase");
//				setUseParentPseudoStyles(true);
			}

			@Override
			public void onButtonStillPressedInterval() {
				Spinner.this.incStep();
			}
		};
//		btnInc.setUseParentPseudoStyles(true);
		btnInc.onMouseReleased(evt -> {
			Spinner.this.incStep();
		});

		// Decrease
		btnDec = new Button(screen) {
			{
				styleClass = "decrease";
//				setUseParentPseudoStyles(true);
			}

			@Override
			public void onButtonStillPressedInterval() {
				Spinner.this.decStep();
			}
		};
//		btnDec.setUseParentPseudoStyles(true);
		btnDec.onMouseReleased(evt -> {
			Spinner.this.decStep();
		});

		//
		textField.onKeyboardFocusLost(evt -> {
			try {
				V currentValue = model.getCurrentValue();
				if (!String.valueOf(currentValue).equals(textField.getText())) {
					model.setValueFromString(textField.getText());
					change(currentValue, model.getCurrentValue());
				}
			} catch (NumberFormatException nfe) {
				// Don't care
			}
		});

		// Keyboard
		textField.onKeyboardPressed(evt -> {

			if (evt.getKeyCode() == KeyInput.KEY_DOWN) {
				decStep();
				evt.setConsumed();
			} else if (evt.getKeyCode() == KeyInput.KEY_UP) {
				incStep();
				evt.setConsumed();
			} else if (evt.getKeyCode() == KeyInput.KEY_RETURN) {
				textField.defocus();
				evt.setConsumed();
			} else {
				if (!isEnabled()) {
					// Only do this if not editable
					displaySelectedStep();
				}
			}
		});
	}

	protected String formatValue(V value) {
		return String.format(valueFormat, value);
	}

	@Override
	protected void onPsuedoStateChange() {
		/// TODO is done in a few places now .. need common solution
		dirtyLayout(true, LayoutType.styling);
	}

	private void decStep() {
		V prevValue = model.getPreviousValue();
		V newValue = prevValue;
		if (newValue == null) {
			if (cycle) {
				model.wind(true);
				newValue = model.getCurrentValue();
			} else {
				return;
			}
		}
		displaySelectedStep();
		change(prevValue, newValue);
	}

	private void displaySelectedStep() {
		textField.setText(formatValue(model.getCurrentValue()));
	}

	private void incStep() {
		V prevValue = model.getNextValue();
		V newValue = prevValue;
		if (newValue == null) {
			if (cycle) {
				model.wind(false);
				newValue = model.getCurrentValue();
			} else {
				return;
			}
		}
		displaySelectedStep();
		change(prevValue, newValue);
	}
}
