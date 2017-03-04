package icetone.extras.chooser;

import java.util.List;

import com.jme3.input.KeyInput;
import com.jme3.math.ColorRGBA;

import icetone.controls.buttons.PushButton;
import icetone.controls.text.TextField;
import icetone.core.BaseElement;
import icetone.core.ElementManager;
import icetone.core.Form;
import icetone.core.BaseScreen;
import icetone.core.Element;
import icetone.core.layout.mig.MigLayout;
import icetone.extras.chooser.ColorSelector.ColorTab;
import icetone.extras.util.ExtrasUtil;
import icetone.fontawesome.FontAwesome;

public class ColorFieldControl extends Element {

	private ColorSelector chooser;
	private ColorRGBA value;
	private TextField textField;
	private Element colorSwatch;
	private boolean includeAlpha;
	private PushButton chooserButton;
	private String chooserText = "Choose Color";
	private List<ColorRGBA> palette;
	private ColorTab[] tabs = new ColorTab[] { ColorTab.WHEEL };
	private boolean showHexInChooser;
	private boolean allowUnset;

	public ColorFieldControl(ColorRGBA initial, boolean includeAlpha, boolean showHex, boolean showChooserButton) {
		this(BaseScreen.get(), initial, includeAlpha, showHex, showChooserButton);
	}

	public ColorFieldControl(ColorRGBA initial) {
		this(BaseScreen.get(), initial);
	}

	public ColorFieldControl(ElementManager<?> screen) {
		this(screen, ColorRGBA.Black, false);
	}

	public ColorFieldControl(ElementManager<?> screen, ColorRGBA initial) {
		this(screen, initial, false);
	}

	public ColorFieldControl(ElementManager<?> screen, ColorRGBA initial, boolean includeAlpha) {
		super(screen);
		init(initial, includeAlpha, true, true);
	}

	public ColorFieldControl(ElementManager<?> screen, String UID, ColorRGBA initial) {
		this(screen, UID, initial, false);
	}

	public ColorFieldControl(ElementManager<?> screen, String UID, ColorRGBA initial, boolean includeAlpha) {
		this(screen, UID, initial, includeAlpha, true, true);
	}

	public ColorFieldControl(ElementManager<?> screen, ColorRGBA initial, boolean includeAlpha, boolean showHex,
			boolean showChooserButton) {
		super(screen);
		init(initial, includeAlpha, showHex, showChooserButton);
	}

	public ColorFieldControl(ElementManager<?> screen, String UID, ColorRGBA initial, boolean includeAlpha,
			boolean showHex, boolean showChooserButton) {
		super(screen, UID);
		init(initial, includeAlpha, showHex, showChooserButton);
	}

	@Override
	public BaseElement setToolTipText(String toolTip) {
		super.setToolTipText(toolTip);
		if (textField != null) {
			textField.setToolTipText(toolTip);
			if (chooserButton != null) {
				chooserButton.setToolTipText("Choose Colour");
			}
		} else if (chooserButton != null) {
			chooserButton.setToolTipText(toolTip);
		}
		return this;
	}

	public boolean isAllowUnset() {
		return allowUnset;
	}

	public ColorFieldControl setAllowUnset(boolean allowUnset) {
		this.allowUnset = allowUnset;
		return this;
	}

	public boolean isShowHexInChooser() {
		return showHexInChooser;
	}

	public ColorTab[] getTabs() {
		return tabs;
	}

	public ColorFieldControl setTabs(ColorTab... tabs) {
		this.tabs = tabs;
		return this;
	}

	public ColorFieldControl setShowHexInChooser(boolean showHexInChooser) {
		this.showHexInChooser = showHexInChooser;
		return this;
	}

	protected void init(ColorRGBA initial, boolean includeAlpha, boolean showHex, final boolean showChooserButton) {
		value = (initial == null ? ColorRGBA.White : initial).clone();
		this.includeAlpha = includeAlpha;

		// Configure layout depending on options
		if (showHex) {
			if (showChooserButton) {
				setLayoutManager(new MigLayout(screen, "gap 0, ins 0", "[shrink 0]2[][shrink 0]", "[fill, grow]"));
			} else {
				setLayoutManager(new MigLayout(screen, "gap 0, ins 0", "[shrink 0][]", "[fill, grow]"));
			}
		} else {
			if (showChooserButton) {
				setLayoutManager(new MigLayout(screen, "gap 0, ins 0", "[shrink 0]2[shrink 0]", "[fill, grow]"));
			} else {
				setLayoutManager(new MigLayout(screen, "gap 0, ins 0", "[shrink 0]", "[fill, grow]"));
			}
		}

		// Swatch
		colorSwatch = new Element(screen);
		colorSwatch.setStyleClass("swatch");
		colorSwatch.onMouseReleased(evt -> {
			if (!showChooserButton) {
				showChooser(evt.getX(), evt.getY());
			}
		});
		colorSwatch.setDefaultColor(value == null ? ColorRGBA.Black : value);
		addElement(colorSwatch);

		// Text field
		if (showHex) {
			textField = new TextField(screen);
			textField.onKeyboardFocusLost(evt -> {
				try {
					value = ExtrasUtil.fromColorString(getText(), ColorFieldControl.this.includeAlpha);
					colorSwatch.setDefaultColor(value);
					onChangeColor(value);
				} catch (IllegalArgumentException iae) {
					updateControls();
				}
				evt.setConsumed();
			});
			textField.onKeyboardPressed(evt -> {
				if (evt.getKeyCode() == KeyInput.KEY_SPACE) {
					showChooser(getAbsoluteX(), getAbsoluteY());
					evt.setConsumed();
				}
			});
			// textField.setTexture(screen.getStyle("ComboBox").getString("defaultImg"));
			textField.setMaxLength(includeAlpha ? 9 : 7);
			addElement(textField);
		}

		// Chooser
		if (showChooserButton) {
			chooserButton = new PushButton(screen);
			FontAwesome.EYEDROPPER.button(16, chooserButton);
			chooserButton.setStyleClass("chooser-button color-picker");
			chooserButton.onMouseReleased(evt -> showChooser(evt.getX(), evt.getY()));
			chooserButton.setToolTipText("Choose Colour");
			addElement(chooserButton, "wrap");
		}

		// Set controls to initial value
		setFocusRootOnly(false);
		updateControls();
	}

	public ColorFieldControl addToForm(Form form) {
		if (textField != null) {
			form.addFormElement(textField);
		}
		if (chooserButton != null) {
			form.addFormElement(chooserButton);
		}
		return this;
	}

	public void setValue(ColorRGBA value) {
		this.value = value.clone();
		updateControls();
	}

	public ColorRGBA getValue() {
		return value;
	}

	public String getChooserText() {
		return chooserText;
	}

	public ColorFieldControl setChooserText(String chooserText) {
		this.chooserText = chooserText;
		return this;
	}

	public ColorFieldControl setPalette(List<ColorRGBA> palette) {
		this.palette = palette;
		if (chooser != null) {
			chooser.setPalette(palette);
		}
		return this;
	}

	protected void onChangeColor(ColorRGBA newColor) {
	}

	protected void onBeforeShowChooser() {
	}

	public void hideChooser() {
		if (chooser != null) {
			chooser.hide();
			chooser = null;
		}
	}

	private void showChooser(float x, float y) {
		onBeforeShowChooser();

		if (chooser != null) {
			chooser.setColor(value);
			if (chooser.isShowing())
				chooser.hide();
		}
		chooser = new ColorSelector(screen, this.getPosition(), includeAlpha, showHexInChooser, tabs) {
			@Override
			public void onChange(ColorRGBA crgba) {
				value = crgba;
				updateControls();
			}

			@Override
			public void onComplete(ColorRGBA crgba) {
				value = crgba;
				updateControls();
				screen.removeElement(this);
				onChangeColor(value);
			}

			@Override
			protected void onCloseWindow() {
				chooser = null;
			}
		};
		if (chooserText != null) {
			chooser.setWindowTitle(chooserText);
		}
		chooser.setPalette(palette);
		chooser.setAllowUnset(allowUnset);
		chooser.setPosition(x, y);
		chooser.setLockToParentBounds(true);
		chooser.setDestroyOnHide(true);
		chooser.setColor(value);
		chooser.setResizable(true);
		screen.showElement(chooser);
	}

	private void updateControls() {
		if (textField != null) {
			textField.setText(value == null ? "" : ExtrasUtil.toHexString(value, includeAlpha));
		}
		colorSwatch.setDefaultColor(value == null ? ColorRGBA.Black : value);
	}
}
