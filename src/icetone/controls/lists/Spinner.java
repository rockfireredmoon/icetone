/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icetone.controls.lists;

import com.jme3.font.BitmapFont;
import com.jme3.font.LineWrapMode;
import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.buttons.Button;
import icetone.controls.buttons.ButtonAdapter;
import icetone.controls.text.AbstractTextLayout;
import icetone.controls.text.TextField;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.Screen;
import icetone.core.layout.LUtil;
import icetone.core.utils.BitmapTextUtil;
import icetone.core.utils.UIDUtil;

/**
 *
 * @author t0neg0d
 */
public class Spinner<V> extends TextField {

	private SpinnerModel<V> model;
	private boolean cycle = false;

	private float btnWidth;
	private String btnIncIcon, btnDecIcon;
	private String valueFormat = "%s";
	private ButtonAdapter btnInc, btnDec;
	private Orientation orientation;
	private int largest;

	/**
	 * Creates a new instance of the Spinner control
	 */
	public Spinner() {
		this(Screen.get());
	}

	/**
	 * Creates a new instance of the Spinner control
	 */
	public Spinner(SpinnerModel<V> model) {
		this(Screen.get());
		setSpinnerModel(model);
	}

	/**
	 * Creates a new instance of the Spinner control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public Spinner(ElementManager screen) {
		this(screen, Vector2f.ZERO, Orientation.HORIZONTAL, false);
	}

	/**
	 * Creates a new instance of the Spinner control
	 * 
	 * @param orientation
	 *            Orientation used to establish Horizontal/Vertical
	 *            layout during control configuration
	 * @param cycle
	 *            Boolean used to determine if the spinner should cycle back
	 *            through values
	 */
	public Spinner(Orientation orientation, boolean cycle) {
		this(Screen.get(), orientation, cycle);
	}

	/**
	 * Creates a new instance of the Spinner control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param orientation
	 *            Orientation used to establish Horizontal/Vertical
	 *            layout during control configuration
	 * @param cycle
	 *            Boolean used to determine if the spinner should cycle back
	 *            through values
	 */
	public Spinner(ElementManager screen, Orientation orientation, boolean cycle) {
		this(screen, Vector2f.ZERO, orientation, cycle);
	}

	/**
	 * Creates a new instance of the Spinner control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param orientation
	 *            Orientation used to establish Horizontal/Vertical
	 *            layout during control configuration
	 * @param cycle
	 *            Boolean used to determine if the spinner should cycle back
	 *            through values
	 */
	public Spinner(ElementManager screen, Vector2f position, Orientation orientation, boolean cycle) {
		this(screen, UIDUtil.getUID(), position, LUtil.LAYOUT_SIZE,
				screen.getStyle(getStyleName(orientation)).getVector4f("resizeBorders"),
				screen.getStyle(getStyleName(orientation)).getString("defaultImg"), orientation, cycle);
	}

	/**
	 * Creates a new instance of the Spinner control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 * @param orientation
	 *            Orientation used to establish Horizontal/Vertical
	 *            layout during control configuration
	 * @param cycle
	 *            Boolean used to determine if the spinner should cycle back
	 *            through values
	 */
	public Spinner(ElementManager screen, Vector2f position, Vector2f dimensions, Orientation orientation, boolean cycle) {
		this(screen, UIDUtil.getUID(), position, dimensions,
				screen.getStyle(getStyleName(orientation)).getVector4f("resizeBorders"),
				screen.getStyle(getStyleName(orientation)).getString("defaultImg"), orientation, cycle);
	}

	/**
	 * Creates a new instance of the Spinner control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 * @param resizeBorders
	 *            A Vector4f containg the border information used when resizing
	 *            the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg
	 *            The default image to use for the Spinner
	 * @param orientation
	 *            Orientation used to establish Horizontal/Vertical
	 *            layout during control configuration
	 * @param cycle
	 *            Boolean used to determine if the spinner should cycle back
	 *            through values
	 */
	public Spinner(ElementManager screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg,
			Orientation orientation, boolean cycle) {
		this(screen, UIDUtil.getUID(), position, dimensions, resizeBorders, defaultImg, orientation, cycle);
	}

	/**
	 * Creates a new instance of the Spinner control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param orientation
	 *            Spinner.Orient
	 *            private String valueFormat;ation used to establish
	 *            Horizontal/Vertical layout during control configuration
	 * @param cycle
	 *            Boolean used to determine if the spinner should cycle back
	 *            through values
	 */
	public Spinner(ElementManager screen, String UID, Vector2f position, Orientation orientation, boolean cycle) {
		this(screen, UID, position, LUtil.LAYOUT_SIZE, screen.getStyle(getStyleName(orientation)).getVector4f("resizeBorders"),
				screen.getStyle(getStyleName(orientation)).getString("defaultImg"), orientation, cycle);
	}

	/**
	 * Creates a new instance of the Spinner control
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
	 * @param orientation
	 *            Orientation used to establish Horizontal/Vertical
	 *            layout during control configuration
	 * @param cycle
	 *            Boolean used to determine if the spinner should cycle back
	 *            through values
	 */
	public Spinner(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Orientation orientation,
			boolean cycle) {
		this(screen, UID, position, dimensions, screen.getStyle(getStyleName(orientation)).getVector4f("resizeBorders"),
				screen.getStyle(getStyleName(orientation)).getString("defaultImg"), orientation, cycle);
	}

	/**
	 * Creates a new instance of the Spinner control
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
	 *            A Vector4f containg the border information used when resizing
	 *            the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg
	 *            The default image to use for the Spinner
	 * @param orientation
	 *            Orientation used to establish Horizontal/Vertical
	 *            layout during control configuration
	 * @param cycle
	 *            Boolean used to determine if the spinner should cycle back
	 *            through values
	 */
	@SuppressWarnings("unchecked")
	public Spinner(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
			String defaultImg, Orientation orientation, boolean cycle) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);

		layoutManager = new SpinnerLayout();
		model = (SpinnerModel<V>) new IntegerRangeSpinnerModel();

		setFontByKey(getStyleName(orientation), "fontName");
		setFontSize(screen.getStyle(getStyleName(orientation)).getFloat("fontSize"));
		setTextPaddingByKey(getStyleName(orientation), "textPadding");
		setTextWrap(LineWrapMode.valueOf(screen.getStyle(getStyleName(orientation)).getString("textWrap")));
		setTextAlign(BitmapFont.Align.valueOf(screen.getStyle(getStyleName(orientation)).getString("textAlign")));
		setTextVAlign(BitmapFont.VAlign.valueOf(screen.getStyle(getStyleName(orientation)).getString("textVAlign")));

		// defaultSize =
		// screen.getStyle(getStyleName(orientation)).getVector2f("defaultSize");
		// containerDimensions = defaultSize.clone();
		// setMinDimensions(Vector2f.ZERO);

		this.orientation = orientation;
		this.cycle = cycle;

		if (orientation == Orientation.HORIZONTAL) {
			btnIncIcon = screen.getStyle("Common").getString("arrowRight");
			btnDecIcon = screen.getStyle("Common").getString("arrowLeft");
		} else {
			btnIncIcon = screen.getStyle("Common").getString("arrowUp");
			btnDecIcon = screen.getStyle("Common").getString("arrowDown");
		}

		btnInc = new ButtonAdapter(screen, UID + ":btnInc",
				screen.getStyle(getStyleName(orientation)).getVector4f("incResizeBorders"),
				screen.getStyle(getStyleName(orientation)).getString("incImg")) {
			@Override
			public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
				screen.setTabFocusElement((Spinner<V>) getElementParent());
				((Spinner<V>) getElementParent()).incStep();
			}

			@Override
			public void onButtonStillPressedInterval() {
				((Spinner<V>) getElementParent()).incStep();
			}
		};
		btnInc.setTextPaddingByKey(getStyleName(orientation), "incTextPadding");
		btnInc.setButtonIcon(btnIncIcon);
		btnInc.setButtonHoverInfo(screen.getStyle(getStyleName(orientation)).getString("incHoverImg"), null);
		btnInc.setButtonPressedInfo(screen.getStyle(getStyleName(orientation)).getString("incPressedImg"), null);

		addChild(btnInc);

		btnDec = new ButtonAdapter(screen, UID + ":btnDec",
				screen.getStyle(getStyleName(orientation)).getVector4f("decResizeBorders"),
				screen.getStyle(getStyleName(orientation)).getString("decImg")) {
			@Override
			public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
				screen.setTabFocusElement((Spinner<V>) getElementParent());
				((Spinner<V>) getElementParent()).decStep();
			}

			@Override
			public void onButtonStillPressedInterval() {
				((Spinner<V>) getElementParent()).decStep();
			}
		};
		btnDec.setTextPaddingByKey(getStyleName(orientation), "decTextPadding");
		btnDec.setButtonHoverInfo(screen.getStyle(getStyleName(orientation)).getString("decHoverImg"), null);
		btnDec.setButtonPressedInfo(screen.getStyle(getStyleName(orientation)).getString("decPressedImg"), null);
		btnDec.setButtonIcon(btnDecIcon);

		addChild(btnDec);

	}

	public void setCaretPositionToStart() {
		caretIndex = 0;
		head = 0;
		tail = 0;
		setCaretPosition(getAbsoluteX());
	}

	/**
	 * Get the increase button
	 * 
	 * @return increase button
	 */
	public Button getIncButton() {
		return btnInc;
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
	 * Set the spinner model to use. Setting this will update the current value.
	 * 
	 * @param model
	 *            model
	 */
	public void setSpinnerModel(SpinnerModel<V> model) {
		this.model = model;
		
		displaySelectedStep();
	}

	/**
	 * Set the current value. Object must be of a type that can be converted to
	 * a string a parsed by the model.
	 * 
	 * @param value
	 *            value
	 */
	public void setSelectedValue(V value) {
		model.setValueFromString(String.valueOf(value));
		displaySelectedStep();
	}

	/**
	 * Set the current value. Object must be of a type that can be converted to
	 * a string a parsed by the model.
	 * 
	 * @param value
	 *            value
	 */
	public void setSelectedValueWithCallback(V value) {
		setSelectedValue(value);
		onChange(model.getCurrentValue());
	}

	/**
	 * Get the spinner model in use.
	 * 
	 * @return model
	 */
	public SpinnerModel<V> getSpinnerModel() {
		return model;
	}

	/**
	 * Sets the interval speed for the spinner
	 * 
	 * @param callsPerSecond
	 *            float
	 */
	public void setInterval(float callsPerSecond) {
		btnInc.setInterval(callsPerSecond);
		btnDec.setInterval(callsPerSecond);
	}

	/**
	 * Enables/disables the TextField and buttons
	 * 
	 * @param isEnabled
	 *            boolean
	 */
	@Override
	public void setIsEnabled(boolean isEnabled) {
		super.setIsEnabled(isEnabled);
		getDecButton().setIsEnabled(isEnabled);
		getIncButton().setIsEnabled(isEnabled);
	}

	// @Override
	// public void onLoseFocus(MouseMotionEvent evt) {
	// super.onLoseFocus(evt);
	// try {
	// if (!String.valueOf(model.getCurrentValue()).equals(getText())) {
	// model.setValueFromString(getText());
	// onChange(model.getCurrentValue());
	// }
	// } catch (NumberFormatException nfe) {
	// // Don't care
	// }
	// }

	@Override
	public void controlTextFieldResetTabFocusHook() {
		try {
			if (!String.valueOf(model.getCurrentValue()).equals(getText())) {
				model.setValueFromString(getText());
				onChange(model.getCurrentValue());
			}
		} catch (NumberFormatException nfe) {
			// Don't care
		}
	}

	public void setFormatterString(String valueFormat) {
		this.valueFormat = valueFormat;
		displaySelectedStep();
	}

	private static String getStyleName(Orientation orientation) {
		return orientation.equals(Orientation.HORIZONTAL) ? "HorizontalSpinner" : "VerticalSpinner";
	}

	private void incStep() {
		V newValue = model.getNextValue();
		if (newValue == null) {
			if (cycle) {
				model.wind(false);
				newValue = model.getCurrentValue();
			} else {
				return;
			}
		}
		displaySelectedStep();
		onChange(newValue);
	}

	private void decStep() {
		V newValue = model.getPreviousValue();
		if (newValue == null) {
			if (cycle) {
				model.wind(true);
				newValue = model.getCurrentValue();
			} else {
				return;
			}
		}
		displaySelectedStep();
		onChange(newValue);
	}

	private void displaySelectedStep() {
		this.setText(formatValue(model.getCurrentValue()));
	}

	@Override
	public void controlKeyPressHook(KeyInputEvent evt, String text) {
		if (evt.getKeyCode() == KeyInput.KEY_DOWN) {
			decStep();
		} else if (evt.getKeyCode() == KeyInput.KEY_UP) {
			incStep();
		} else if (evt.getKeyCode() == KeyInput.KEY_RETURN) {
			resetTabFocus();
		} else {
			if (!getIsEnabled()) {
				// Only do this if not editable
				displaySelectedStep();
			}
		}
	}
	
	protected String formatValue(V value) {
		return String.format(valueFormat, value);
	}

	/**
	 * The abstract event method that is called when the value changes
	 * 
	 * @param value
	 *            The new value from the model
	 */
	protected void onChange(V value) {

	}

	class SpinnerLayout extends AbstractTextLayout {

		public Vector2f minimumSize(Element parent) {
			Vector2f min = super.minimumSize(parent);
			min.x += LUtil.getMinimumWidth(btnDec) + LUtil.getMinimumWidth(btnInc);
			min.y = Math.max(min.y, Math.max(LUtil.getMinimumHeight(btnDec), LUtil.getMinimumHeight(btnDec)));
			return min;
		}

		public Vector2f preferredSize(Element parent) {
			Vector2f pref = super.preferredSize(parent);
			// Vector2f ps = LUtil.getPreferredTextSize(parent);
			float w = BitmapTextUtil.getTextWidth(parent, "Xg") * model.getLargestValueSize(valueFormat);
			int ih = LUtil.getPreferredHeight(btnInc);
			int id = LUtil.getPreferredHeight(btnDec);
			float h = Math.max(pref.y, Math.max(ih, id) + textPadding.z + textPadding.w);
			pref.y = Math.max(pref.y, h);
			pref.x = (pref.y * 2) + w + textPadding.x + textPadding.y;
			return pref;
		}

		public void layout(Element childElement) {

			float btnWidth = childElement.getHeight();
			float btnIncX, btnIncY, btnIncH;
			float btnDecX, btnDecY, btnDecH;

			Vector2f cd = getContainerDimensions();
			setActualDimensions(new Vector2f(cd.x - (btnWidth * 2), btnWidth));
			setActualX(containerPosition.x + cd.y);

			Vector2f d = new Vector2f(cd.x - (btnWidth * 2), btnWidth);
			getCaret().setDimensions(new Vector2f(d.x - (getTextPaddingVec().x + getTextPaddingVec().y),
					d.y - (getTextPaddingVec().y + getTextPaddingVec().z)));
			getCaret().setPosition(new Vector2f(getTextPaddingVec().x, getTextPaddingVec().z));

			btnIncX = cd.x - (btnWidth * 2);
			btnIncY = 0;
			btnIncH = cd.y;
			btnDecX = -cd.y;
			btnDecY = 0;
			btnDecH = cd.y;

			LUtil.setBounds(getIncButton(), btnIncX, btnIncY, cd.y, btnIncH);
			LUtil.setBounds(getIncButton().getButtonIcon(), cd.y / 4, btnIncH / 4, cd.y / 2, btnIncH / 2);

			LUtil.setBounds(getDecButton(), btnDecX, btnDecY, cd.y, btnDecH);
			// LUtil.setBounds(getDecButton().getButtonIcon(), cd.y / 4, btnIncH
			// / 4,
			// cd.y / 2, btnIncH / 2);
			// getDecButton().updateClippingLayers();

//			updateText(getVisibleText());

		}

		public void remove(Element child) {
		}

		public void constrain(Element child, Object constraints) {
		}
	}

	public V getSelectedValue() {
		return model.getCurrentValue();
	}
}
