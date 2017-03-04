package icetone.extras.chooser;

import java.util.List;

import com.jme3.math.ColorRGBA;

import icetone.controls.buttons.PushButton;
import icetone.core.ElementManager;
import icetone.core.Form;
import icetone.extras.chooser.ColorSelector.ColorTab;

public class ColorButton extends PushButton {

	private ColorSelector chooser;
	private ColorRGBA value;
	private boolean includeAlpha;
	private String chooserText = "Choose Color";
	private List<ColorRGBA> palette;
	private ColorTab[] tabs = new ColorTab[] { ColorTab.WHEEL };
	private boolean showChooserHex;

	public ColorButton(ElementManager<?> screen, ColorRGBA initial) {
		this(screen, initial, false);
	}

	public ColorButton(ElementManager<?> screen, ColorRGBA initial, boolean includeAlpha) {
		this(screen, initial, includeAlpha, false);
	}

	public ColorButton(ElementManager<?> screen, ColorRGBA initial, boolean includeAlpha, boolean showChooserHex) {
		this(screen, null, initial, includeAlpha, showChooserHex);
	}

	public ColorButton(ElementManager<?> screen, String styleId, ColorRGBA initial) {
		this(screen, styleId, initial, false);
	}

	public ColorButton(ElementManager<?> screen, String styleId, ColorRGBA initial, boolean includeAlpha) {
		this(screen, styleId, initial, includeAlpha, false);
	}

	public ColorButton(ElementManager<?> screen, String styleId, ColorRGBA initial, boolean includeAlpha,
			boolean showChooserHex) {
		super(screen, styleId);
		onMouseReleased(evt -> showChooser(evt.getX(), evt.getY()));
		init(initial, includeAlpha, showChooserHex);
	}

	public ColorTab[] getTabs() {
		return tabs;
	}

	public void setTabs(ColorTab... tabs) {
		this.tabs = tabs;
	}

	private void init(ColorRGBA initial, boolean includeAlpha, boolean showChooserHex) {
		value = initial;
		this.includeAlpha = includeAlpha;
		this.showChooserHex = showChooserHex;
		updateControls();
	}

	public void addToForm(Form form) {
		form.addFormElement(this);
	}

	public void setValue(ColorRGBA value) {
		this.value = value;
		updateControls();
	}

	public ColorRGBA getValue() {
		return value;
	}

	public String getChooserText() {
		return chooserText;
	}

	public void setChooserText(String chooserText) {
		this.chooserText = chooserText;
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
		chooser = new ColorSelector(screen, this.getPosition(), includeAlpha, showChooserHex, tabs) {
			@Override
			public void onChange(ColorRGBA crgba) {
				value = crgba;
				updateControls();
			}

			@Override
			public void onComplete(ColorRGBA crgba) {
				value = crgba;
				updateControls();
				onChangeColor(value);
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
		chooser.setResizable(false);
		float cx = x + 20;
		if (cx + chooser.getWidth() > screen.getWidth()) {
			cx = screen.getWidth() - chooser.getWidth();
		}
		chooser.setPosition(cx, y - (chooser.getHeight() / 2));
		chooser.setModal(true);
		screen.showElement(chooser);
	}

	private void updateControls() {
		ColorRGBA clone = value.clone();
		clone.clamp();
		getButtonIcon().setDefaultColor(clone);
	}

	public void setPalette(List<ColorRGBA> palette) {
		this.palette = palette;
		if (chooser != null) {
			chooser.setPalette(palette);
		}
	}
}
