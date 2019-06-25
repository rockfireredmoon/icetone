package icetone.extras.chooser;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import com.jme3.input.KeyInput;
import com.jme3.math.ColorRGBA;

import icetone.controls.buttons.PushButton;
import icetone.controls.text.TextField;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Element;
import icetone.core.Form;
import icetone.core.event.ChangeSupport;
import icetone.core.event.UIChangeEvent;
import icetone.core.event.UIChangeListener;
import icetone.core.event.ElementEvent.Type;
import icetone.core.layout.mig.MigLayout;
import icetone.extras.util.ExtrasUtil;
import icetone.fontawesome.FontAwesome;

public class ColorField extends Element {
	final static Logger LOG = Logger.getLogger(ColorField.class.getName());

	private ColorSelector chooser;
	private ColorRGBA value;
	private TextField textField;
	private Element colorSwatch;
	private boolean includeAlpha;
	private PushButton chooserButton;
	private String chooserText = "Choose Color";
	private List<ColorRGBA> palette;
	private ColorTab[] tabs = new ColorTab[] { ColorTab.RGB };
	private boolean showHexInChooser;
	private boolean allowUnset;
	private boolean modalChooser;
	private ColorRestrictionType restrictionType = ColorRestrictionType.getDefaultType();
	private ChooserSelectionMode selectionMode = ChooserSelectionMode.NORMAL;

	protected ChangeSupport<ColorField, ColorRGBA> changeSupport;

	public ColorField(ColorRGBA initial, boolean showHex, boolean showChooserButton) {
		this(BaseScreen.get(), initial, showHex, showChooserButton);
	}

	public ColorField(ColorRGBA initial) {
		this(BaseScreen.get(), initial);
	}

	public ColorField(BaseScreen screen) {
		this(screen, ColorRGBA.Black, false);
	}

	public ColorField(BaseScreen screen, ColorRGBA initial) {
		this(screen, initial, false);
	}

	public ColorField(BaseScreen screen, ColorRGBA initial, boolean includeAlpha) {
		super(screen);
		init(initial, true, true);
	}

	public ColorField(BaseScreen screen, String UID, ColorRGBA initial) {
		this(screen, UID, initial, false);
	}

	public ColorField(BaseScreen screen, String UID, ColorRGBA initial, boolean includeAlpha) {
		this(screen, UID, initial, true, true);
	}

	public ColorField(BaseScreen screen, ColorRGBA initial, boolean showHex, boolean showChooserButton) {
		super(screen);
		init(initial, showHex, showChooserButton);
	}

	public ColorField(BaseScreen screen, String UID, ColorRGBA initial, boolean showHex, boolean showChooserButton) {
		super(screen, UID);
		init(initial, showHex, showChooserButton);
	}

	public ColorField onChange(UIChangeListener<ColorField, ColorRGBA> listener) {
		if (changeSupport == null)
			changeSupport = new ChangeSupport<>();
		changeSupport.bind(listener);
		return this;
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

	public boolean isIncludeAlpha() {
		return includeAlpha;
	}

	public ChooserSelectionMode getSelectionMode() {
		return selectionMode;
	}

	public ColorField setSelectionMode(ChooserSelectionMode selectionMode) {
		if (this.selectionMode != selectionMode) {
			this.selectionMode = selectionMode;
			if (chooser != null)
				chooser.setSelectionMode(selectionMode);
			updateControls();
		}
		return this;
	}

	public ColorField setIncludeAlpha(boolean includeAlpha) {
		if (includeAlpha != this.includeAlpha) {
			this.includeAlpha = includeAlpha;
			updateControls();
		}
		return this;
	}

	public boolean isAllowUnset() {
		return allowUnset;
	}

	public ColorField setAllowUnset(boolean allowUnset) {
		this.allowUnset = allowUnset;
		if(chooser != null)
			chooser.setAllowUnset(allowUnset);
		return this;
	}

	public ColorTab[] getTabs() {
		return tabs;
	}

	public ColorField setTabs(ColorTab... tabs) {
		this.tabs = tabs;
		return this;
	}

	public boolean isShowHexInChooser() {
		return showHexInChooser;
	}

	public ColorField setShowHexInChooser(boolean showHexInChooser) {
		this.showHexInChooser = showHexInChooser;
		return this;
	}

	protected void init(ColorRGBA initial, boolean showHex, final boolean showChooserButton) {
		value = (initial == null ? ColorRGBA.White : initial).clone();

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
				commitText();
				evt.setConsumed();
			});
			textField.onKeyboardPressed(evt -> {
				if (evt.getKeyCode() == KeyInput.KEY_SPACE) {
					showChooser(getAbsoluteX(), getAbsoluteY());
					evt.setConsumed();
				} else if (evt.getKeyCode() == KeyInput.KEY_RETURN) {
					commitText();
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
		onElementEvent(evt -> hideChooser(), Type.CLEANUP);
	}

	protected void commitText() {
		try {
			doSetValue(ExtrasUtil.fromColorString(textField.getText(), ColorField.this.includeAlpha));
		} catch (IllegalArgumentException iae) {
			updateControls();
		}
	}

	public ColorField addToForm(Form form) {
		if (textField != null) {
			form.addFormElement(textField);
		}
		if (chooserButton != null) {
			form.addFormElement(chooserButton);
		}
		return this;
	}

	public ColorField setValue(ColorRGBA value) {
		doSetValue(value);
		return this;
	}

	public ColorField unbindChanged(UIChangeListener<ColorField, ColorRGBA> listener) {
		if (changeSupport != null)
			changeSupport.unbind(listener);
		return this;
	}

	public ColorRGBA getValue() {
		return value;
	}

	public String getChooserText() {
		return chooserText;
	}

	public ColorField setChooserText(String chooserText) {
		this.chooserText = chooserText;
		return this;
	}

	public ColorField setPalette(List<ColorRGBA> palette) {
		this.palette = palette;
		if (chooser != null) {
			chooser.setPalette(palette);
		}
		return this;
	}

	public ColorRestrictionType getRestrictionType() {
		return restrictionType;
	}

	public ColorField setRestrictionType(ColorRestrictionType restrictionType) {
		this.restrictionType = restrictionType;
		if (chooser != null) {
			chooser.setRestrictionType(this.restrictionType);
		}
		return this;
	}

	public boolean isModalChooser() {
		return modalChooser;
	}

	public ColorField setModalChooser(boolean modalChooser) {
		this.modalChooser = modalChooser;
		return this;
	}

	protected void onBeforeShowChooser() {
	}

	protected void doSetValue(ColorRGBA value) {
		if (!Objects.equals(value, this.value)) {
			ColorRGBA old = this.value;
			this.value = value == null ? null : value.clone();
			updateControls();
			if (changeSupport != null)
				changeSupport.fireEvent(new UIChangeEvent<ColorField, ColorRGBA>(this, old, this.value));
		}
	}

	public void hideChooser() {
		if (chooser != null) {
			chooser.hide();
			chooser = null;
		}
	}

	private void showChooser(float x, float y) {
		if (tabs.length > 0) {
			onBeforeShowChooser();

			if (chooser != null) {
				chooser.setColor(value);
				if (chooser.isShowing())
					chooser.hide();
			}
			chooser = new ColorSelector(screen, includeAlpha, showHexInChooser, tabs) {
				@Override
				public void onChange(ColorRGBA crgba) {
				}

				@Override
				public void onComplete(ColorRGBA crgba) {
					ColorRGBA old = value;
					value = crgba == null ? null : crgba.clone();
					updateControls();
					if (changeSupport != null)
						changeSupport
								.fireEvent(new UIChangeEvent<ColorField, ColorRGBA>(ColorField.this, old, value));
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
			chooser.setSelectionMode(selectionMode);
			chooser.setPosition(x, y);
			chooser.setLockToParentBounds(true);
			chooser.setDestroyOnHide(true);
			chooser.setColor(value);
			chooser.setResizable(true);
			chooser.setModal(modalChooser);
			chooser.setRestrictionType(restrictionType);
			screen.showElement(chooser);
		} else
			LOG.warning("Cannot show chooser, no tabs have been selected.");
	}

	private void updateControls() {
		if (textField != null) {
			textField.setText(value == null ? "" : ExtrasUtil.toHexString(value, includeAlpha));
		}
		colorSwatch.setDefaultColor(value == null ? ColorRGBA.Black : value);
	}
}
