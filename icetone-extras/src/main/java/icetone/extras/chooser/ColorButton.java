package icetone.extras.chooser;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import com.jme3.math.ColorRGBA;

import icetone.controls.buttons.PushButton;
import icetone.core.BaseScreen;
import icetone.core.Form;
import icetone.core.Screen;
import icetone.core.event.ChangeSupport;
import icetone.core.event.ElementEvent.Type;
import icetone.core.event.UIChangeEvent;
import icetone.core.event.UIChangeListener;

public class ColorButton extends PushButton {
	final static Logger LOG = Logger.getLogger(ColorButton.class.getName());

	protected ChangeSupport<ColorButton, ColorRGBA> changeSupport;
	private ColorSelector chooser;
	private String chooserText = "Choose Color";
	private boolean includeAlpha;
	private List<ColorRGBA> palette;
	private boolean showHexInChooser;
	private ColorTab[] tabs = new ColorTab[] { ColorTab.RGB };
	private ColorRGBA value;
	private boolean modalChooser;
	private ColorRestrictionType restrictionType = ColorRestrictionType.getDefaultType();
	private ChooserSelectionMode selectionMode = ChooserSelectionMode.NORMAL;

	private boolean allowUnset;

	public ColorButton(ColorRGBA initial) {
		this(Screen.get(), initial);
	}

	public ColorButton(BaseScreen screen, ColorRGBA initial) {
		this(screen, initial, false);
	}

	public ColorButton(BaseScreen screen, ColorRGBA initial, boolean includeAlpha) {
		this(screen, initial, includeAlpha, false);
	}

	public ColorButton(BaseScreen screen, ColorRGBA initial, boolean includeAlpha, boolean showChooserHex) {
		this(screen, null, initial, includeAlpha, showChooserHex);
	}

	public ColorButton(BaseScreen screen, String styleId, ColorRGBA initial) {
		this(screen, styleId, initial, false);
	}

	public ColorButton(BaseScreen screen, String styleId, ColorRGBA initial, boolean includeAlpha) {
		this(screen, styleId, initial, includeAlpha, false);
	}

	public ColorButton(BaseScreen screen, String styleId, ColorRGBA initial, boolean includeAlpha,
			boolean showChooserHex) {
		super(screen, styleId);
		onMouseReleased(evt -> showChooser(evt.getX(), evt.getY()));
		onElementEvent(evt -> hideChooser(), Type.CLEANUP);
		init(initial, includeAlpha, showChooserHex);
	}

	public ChooserSelectionMode getSelectionMode() {
		return selectionMode;
	}

	public ColorButton setSelectionMode(ChooserSelectionMode selectionMode) {
		if (this.selectionMode != selectionMode) {
			this.selectionMode = selectionMode;
			updateControls();
		}
		return this;
	}

	public ColorButton addToForm(Form form) {
		form.addFormElement(this);
		return this;
	}

	public String getChooserText() {
		return chooserText;
	}

	public ColorTab[] getTabs() {
		return tabs;
	}

	public ColorRGBA getValue() {
		return value;
	}

	public void hideChooser() {
		if (chooser != null) {
			chooser.hide();
			chooser = null;
		}
	}

	public ColorButton onChange(UIChangeListener<ColorButton, ColorRGBA> listener) {
		if (changeSupport == null)
			changeSupport = new ChangeSupport<>();
		changeSupport.bind(listener);
		return this;
	}

	public ColorButton setChooserText(String chooserText) {
		this.chooserText = chooserText;
		return this;
	}

	public ColorButton setPalette(List<ColorRGBA> palette) {
		this.palette = palette;
		if (chooser != null) {
			chooser.setPalette(palette);
		}
		return this;
	}

	public ColorButton setTabs(ColorTab... tabs) {
		this.tabs = tabs;
		return this;
	}

	public boolean isModalChooser() {
		return modalChooser;
	}

	public ColorButton setModalChooser(boolean modalChooser) {
		this.modalChooser = modalChooser;
		return this;
	}

	public ColorButton setValue(ColorRGBA value) {
		if (!Objects.equals(value, this.value)) {
			ColorRGBA old = this.value;
			this.value = value == null ? null : value.clone();
			updateControls();
			if (changeSupport != null)
				changeSupport.fireEvent(new UIChangeEvent<ColorButton, ColorRGBA>(this, old, this.value));
		}
		return this;
	}

	public ColorButton unbindChanged(UIChangeListener<ColorButton, ColorRGBA> listener) {
		if (changeSupport != null)
			changeSupport.unbind(listener);
		return this;
	}

	public boolean isShowHexInChooser() {
		return showHexInChooser;
	}

	public ColorButton setShowHexInChooser(boolean showHexInChooser) {
		this.showHexInChooser = showHexInChooser;
		updateControls();
		return this;
	}

	public boolean isIncludeAlpha() {
		return includeAlpha;
	}

	public ColorButton setIncludeAlpha(boolean includeAlpha) {
		this.includeAlpha = includeAlpha;
		updateControls();
		return this;
	}

	public ColorButton setRestrictionType(ColorRestrictionType restrictionType) {
		this.restrictionType = restrictionType;
		if (chooser != null) {
			chooser.setRestrictionType(this.restrictionType);
		}
		return this;
	}

	public boolean isAllowUnset() {
		return allowUnset;
	}

	public ColorButton setAllowUnset(boolean allowUnset) {
		this.allowUnset = allowUnset;
		if (chooser != null)
			chooser.setAllowUnset(allowUnset);
		return this;
	}

	protected void onBeforeShowChooser() {
	}

	protected final void onChangeColorX(ColorRGBA newColor) {

	}

	private void init(ColorRGBA initial, boolean includeAlpha, boolean showChooserHex) {
		value = (initial == null ? ColorRGBA.White : initial).clone();
		this.includeAlpha = includeAlpha;
		this.showHexInChooser = showChooserHex;
		updateControls();
	}

	private void showChooser(float x, float y) {
		if (tabs.length > 0) {
			onBeforeShowChooser();
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
								.fireEvent(new UIChangeEvent<ColorButton, ColorRGBA>(ColorButton.this, old, value));
				}

				@Override
				protected void onCloseWindow() {
					chooser = null;
				}
			};
			if (chooserText != null) {
				chooser.setWindowTitle(chooserText);
			}
			if (palette != null) {
				chooser.setPalette(palette);
			}
			chooser.setDestroyOnHide(true);
			chooser.setColor(value);
			chooser.sizeToContent();
			chooser.setSelectionMode(selectionMode);
			chooser.setResizable(false);
			chooser.setAllowUnset(allowUnset);
			chooser.setRestrictionType(restrictionType);
			float cx = x + 20;
			if (cx + chooser.getWidth() > screen.getWidth()) {
				cx = screen.getWidth() - chooser.getWidth();
			}
			chooser.setPosition(cx, y - (chooser.getHeight() / 2));
			chooser.setModal(modalChooser);
			screen.showElement(chooser);
		} else
			LOG.warning("Cannot show chooser, no tabs have been selected.");
	}

	private void updateControls() {
		ColorRGBA clone = null;
		if(value != null) {
			clone = value.clone();
			clone.clamp();
		}
		getButtonIcon().setDefaultColor(clone);
	}
}
