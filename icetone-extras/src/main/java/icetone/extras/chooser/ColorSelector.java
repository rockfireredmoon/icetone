package icetone.extras.chooser;

import java.util.ArrayList;
import java.util.List;

import com.jme3.font.BitmapFont;
import com.jme3.math.ColorRGBA;

import icetone.controls.buttons.PushButton;
import icetone.controls.containers.Frame;
import icetone.controls.containers.TabControl;
import icetone.controls.text.Label;
import icetone.controls.text.TextField;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.layout.FlowLayout;
import icetone.core.layout.mig.MigLayout;
import icetone.extras.util.ExtrasUtil;

public abstract class ColorSelector extends Frame {

	private static final float LIVE_UPDATE_DELAY = 0.8f;
	private List<ColorRGBA> palette;
	private TextField tfHex;
	private PushButton bFinish;
	private final boolean includeAlpha;
	private ColorRGBA color = ColorRGBA.White;
	private List<ColorTabPanel> panels = new ArrayList<ColorTabPanel>();
	private boolean allowUnset;
	private BaseElement buttons;
	private PushButton unset;
	private boolean alwaysUseTabs = false;
	private ColorRestrictionType restrictionType = ColorRestrictionType.getDefaultType();
	private boolean showHex;
	private ChooserSelectionMode selectionMode = ChooserSelectionMode.NORMAL;
	private ColorRGBTab colorRGBTab;
	private ColorPaletteTab colorPaletteTab;
	private ColorWheelTab colorWheelTab;
	private ColorTab[] tabs;
	private Label hexLabel;

	/**
	 * Creates a new instance of the XColorSelector control
	 *
	 * @param screen       The screen control the Element is to be added to
	 * @param position     A Vector2f containing the x/y position of the Element
	 * @param includeAlpha include alpha component
	 * @param showHex      show hex values
	 * @param tabs         the tabs to display
	 */
	public ColorSelector(BaseScreen screen, boolean includeAlpha, boolean showHex, ColorTab... tabs) {
		super(screen, null, true);

		if (tabs.length == 0)
			throw new IllegalArgumentException(
					String.format("Must provide at least one %s in the constructor.", ColorTab.class));

		this.includeAlpha = includeAlpha;
		this.showHex = showHex;
		this.tabs = tabs;
		content.setLayoutManager(new MigLayout(screen, "fill, hidemode 2"));

		// Container element for buttons
		buttons = new BaseElement(screen);
		buttons.setLayoutManager(new FlowLayout(4));

		// Buttons

		hexLabel = new Label(screen);
		hexLabel.setTextVAlign(BitmapFont.VAlign.Center);
		hexLabel.setText("HEX: #");
		buttons.addElement(hexLabel);

		tfHex = new TextField(screen);
		tfHex.onKeyboardReleased(evt -> {
			String text = evt.getElement().getText();
			if (!text.equals("") && text.length() == 6
					&& (Character.isDigit(evt.getKeyChar()) || ((Character.toLowerCase(evt.getKeyChar()) >= 'a')
							&& Character.toLowerCase(evt.getKeyChar()) <= 'f'))) {
				try {
					setColor(ExtrasUtil.fromColorString(text));
				} catch (IllegalArgumentException nfe) {
				}
			}
		});
		tfHex.setType(TextField.Type.ALPHANUMERIC_NOSPACE);
		tfHex.setMaxLength(6);
		buttons.addElement(tfHex);
		buttons.addElement((unset = new PushButton(screen, "Unset") {
			{
				setStyleClass("fancy");
			}
		}).onMouseReleased(evt -> {
			if (selectionMode == ChooserSelectionMode.CLOSE_ON_SELECT) {
				onComplete(null);
				hide();
			} else {
				setColor(null);
			}
		}));

		bFinish = new PushButton(screen, "Done") {
			{
				setStyleClass("fancy");
			}
		};
		bFinish.onMouseReleased(evt -> {
			onComplete(getColor());
			hide();
		});
		buttons.addElement(bFinish);
		// Tabs
		createTabs();

		// Container
		setResizable(true);
		adjustControls();
//		sizeToContent();

	}

	protected void createTabs() {
		panels.clear();
		colorPaletteTab = null;
		colorRGBTab = null;
		colorWheelTab = null;
		if (tabs.length > 1 || alwaysUseTabs) {
			TabControl colorTabs = new TabControl(screen);
			for (ColorTab t : tabs) {
				BaseElement tabComponent = createTab(t);
				if ((tabComponent != null)) {
					panels.add((ColorTabPanel) tabComponent);
					colorTabs.addTab(ExtrasUtil.toEnglish(t), tabComponent);
				}
			}
			content.addElement(colorTabs, "grow,wrap");
		} else {
			BaseElement tabComponent = createTab(tabs[0]);
			if (tabComponent != null) {
				panels.add((ColorTabPanel) tabComponent);
				content.addElement(tabComponent, "grow,wrap");
			}
		}

		// Build containers
		content.addElement(buttons, "dock south, shrink 0");
	}

	public ChooserSelectionMode getSelectionMode() {
		return selectionMode;
	}

	public ColorSelector setSelectionMode(ChooserSelectionMode selectionMode) {
		if (this.selectionMode != selectionMode) {
			this.selectionMode = selectionMode;
			adjustControls();
		}
		return this;
	}

	public ColorSelector setAllowUnset(boolean allowUnset) {
		if (allowUnset != this.allowUnset) {
			this.allowUnset = allowUnset;
			adjustControls();
		}
		return this;
	}

	private BaseElement createTab(ColorTab tab) {
		switch (tab) {
		case WHEEL:
			colorWheelTab = new ColorWheelTab(screen, restrictionType) {
				@Override
				public void onChange(ColorRGBA color) {
					colorChangedFromTab(this, color);
				}
			};
			return colorWheelTab;
		case RGB:
			colorRGBTab = new ColorRGBTab(screen, includeAlpha) {
				@Override
				public void onChange(ColorRGBA color) {
					colorChangedFromTab(this, color);
				}
			};
			colorRGBTab.setChangeEventDelay(selectionMode != ChooserSelectionMode.NORMAL ? LIVE_UPDATE_DELAY : 0f);
			return colorRGBTab;
		case PALETTE:
			if (palette == null)
				return null;
			else {
				colorPaletteTab = new ColorPaletteTab(screen) {
					@Override
					public void onChange(ColorRGBA color) {
						colorChangedFromTab(this, color);
					}
				};
				colorPaletteTab.setPalette(palette);
				return colorPaletteTab;
			}
		}
		throw new IllegalArgumentException();

	}

	public List<ColorRGBA> getPalette() {
		return palette;
	}

	public ColorSelector setRestrictionType(ColorRestrictionType restrictionType) {
		this.restrictionType = restrictionType;
		for (ColorTabPanel p : panels) {
			if (p instanceof ColorWheelTab)
				((ColorWheelTab) p).setRestrictionType(this.restrictionType);
		}
		return this;
	}

	public ColorSelector setPalette(List<ColorRGBA> palette) {
		this.palette = palette;
		if (palette != null) {
			if (colorPaletteTab == null) {
				content.invalidate();
				content.removeAllChildren();
				createTabs();
				content.validate();
			} else {
				colorPaletteTab.setPalette(palette);
			}
		} else {
			if (colorPaletteTab != null) {
				content.invalidate();
				content.removeAllChildren();
				createTabs();
				content.validate();
			}
		}
		content.layoutChildren();
		return this;
	}

	public ColorSelector setColor(ColorRGBA color) {
		this.color = color == null ? null : color.clone();
		for (ColorTabPanel c : panels) {
			c.setColor(this.color);
		}
		updateHEX();
		return this;
	}

	public final ColorRGBA getColorNoAlpha() {
		return new ColorRGBA(getRed(), getGreen(), getBlue(), 0);
	}

	public final ColorRGBA getColorFullAlpha() {
		return new ColorRGBA(getRed(), getGreen(), getBlue(), 1);
	}

	public final ColorRGBA getColor() {
		if (includeAlpha) {
			return color == null ? null : new ColorRGBA(getRed(), getGreen(), getBlue(), getAlpha());
		} else {
			return getColorFullAlpha();
		}
	}

	public final float getRed() {
		return color.r;
	}

	public final float getGreen() {
		return color.g;
	}

	public final float getBlue() {
		return color.b;
	}

	public final float getAlpha() {
		return color.a;
	}

	public boolean isAlwaysUseTabs() {
		return alwaysUseTabs;
	}

	public void setAlwaysUseTabs(boolean alwaysUseTabs) {
		this.alwaysUseTabs = alwaysUseTabs;
	}

	public abstract void onChange(ColorRGBA color);

	public abstract void onComplete(ColorRGBA color);

	protected void adjustControls() {
		if (colorRGBTab != null)
			colorRGBTab.setChangeEventDelay(selectionMode != ChooserSelectionMode.NORMAL ? LIVE_UPDATE_DELAY : 0f);
		hexLabel.setVisibilityAllowed(showHex);
		tfHex.setVisibilityAllowed(showHex);
		bFinish.setVisibilityAllowed(selectionMode == ChooserSelectionMode.NORMAL);
		unset.setVisibilityAllowed(allowUnset);
		buttons.setVisibilityAllowed(selectionMode == ChooserSelectionMode.NORMAL || showHex || allowUnset);
	}

	protected void updateHEX() {
		if (tfHex != null) {
			if (color == null)
				tfHex.setText("");
			else {
				String hex = String.format("%02x%02x%02x", (int) (getRed() * 255), (int) (getGreen() * 255),
						(int) (getBlue() * 255));
				if (includeAlpha) {
					hex += String.format("%02x", (int) (getAlpha() * 100f));
				}
				tfHex.setText(hex);
			}
		}
	}

	private void colorChangedFromTab(ColorTabPanel panel, ColorRGBA color) {
		this.color = color.clone();
		for (ColorTabPanel p : panels) {
			if (!p.equals(panel)) {
				p.setColor(this.color);
			}
		}
		updateHEX();
		if (ChooserSelectionMode.NORMAL == selectionMode)
			onChange(this.color);
		else {
			onComplete(this.color);
			if (selectionMode == ChooserSelectionMode.CLOSE_ON_SELECT)
				hide();
		}
	}
}
