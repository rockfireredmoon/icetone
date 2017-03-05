package icetone.extras.chooser;

import java.util.ArrayList;
import java.util.List;

import com.jme3.font.BitmapFont;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;

import icetone.controls.buttons.PushButton;
import icetone.controls.containers.Frame;
import icetone.controls.containers.TabControl;
import icetone.controls.text.Label;
import icetone.controls.text.TextField;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.layout.Border;
import icetone.core.layout.BorderLayout;
import icetone.core.layout.FlowLayout;
import icetone.extras.util.ExtrasUtil;

public abstract class ColorSelector extends Frame {

	private List<ColorRGBA> palette;

	public enum ColorTab {

		WHEEL, PALETTE
	}

	public interface ColorTabPanel {

		void onChange(ColorRGBA color);

		void setColor(ColorRGBA color);

		void setPalette(List<ColorRGBA> palette);
	}

	private TextField tfHex;
	private PushButton bFinish;
	private final boolean includeAlpha;
	private ColorRGBA color = ColorRGBA.White;
	private List<ColorTabPanel> panels = new ArrayList<ColorTabPanel>();
	private boolean allowUnset;
	private BaseElement buttons;
	private PushButton unset;
	private boolean alwaysUseTabs = false;

	/**
	 * Creates a new instance of the XColorSelector control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param includeAlpha
	 *            include alpha component
	 * @param showHex
	 *            show hex values
	 * @param tabs
	 *            the tabs to display
	 */
	public ColorSelector(BaseScreen screen, Vector2f position, boolean includeAlpha, boolean showHex,
			ColorTab... tabs) {
		super(screen, position, null, true);

		this.includeAlpha = includeAlpha;
		// content.setLayoutManager(new MigLayout(screen, "ins 0, wrap 1, fill",
		// "[fill,grow]", "[grow][]"));
		content.setLayoutManager(new BorderLayout());

		// Container element for buttons
		buttons = new BaseElement(screen);
		buttons.setLayoutManager(new FlowLayout(8, BitmapFont.Align.Center));

		// Tabs
		if (tabs.length > 1 || alwaysUseTabs) {
			TabControl colorTabs = new TabControl(screen);
			for (ColorTab t : tabs) {
				BaseElement tabComponent = createTab(t);
				panels.add((ColorTabPanel) tabComponent);
				colorTabs.addTab(ExtrasUtil.toEnglish(t), tabComponent);
			}
			content.addElement(colorTabs, Border.CENTER);
		} else {
			BaseElement tabComponent = createTab(tabs[0]);
			panels.add((ColorTabPanel) tabComponent);
			content.addElement(tabComponent, Border.CENTER);
		}

		// Buttons

		if (showHex) {
			Label lHex = new Label(screen);
			lHex.setTextVAlign(BitmapFont.VAlign.Center);
			lHex.setText("HEX: #");
			buttons.addElement(lHex);

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
		}

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

		// Build containers
		content.addElement(buttons, Border.SOUTH);

		// Container
		setResizable(true);
		sizeToContent();

	}

	public void setAllowUnset(boolean allowUnset) {
		if (allowUnset != this.allowUnset) {
			if (allowUnset) {
				buttons.addElement((unset = new PushButton(screen, "Unset") {
					{
						setStyleClass("fancy");
					}
				}).onMouseReleased(evt -> setColor(null)));
			} else {
				buttons.removeElement(unset);
			}
			this.allowUnset = allowUnset;
		}
	}

	private BaseElement createTab(ColorTab tab) {

		switch (tab) {
		case PALETTE:
			return new ColorPaletteTab(screen) {
				@Override
				public void onChange(ColorRGBA color) {
					colorChangedFromTab(this, color);
				}
			};
		case WHEEL:
			return new ColorWheelTab(screen, includeAlpha) {
				@Override
				public void onChange(ColorRGBA color) {
					colorChangedFromTab(this, color);
				}
			};
		}
		throw new IllegalArgumentException();
	}

	public List<ColorRGBA> getPalette() {
		return palette;
	}

	public void setPalette(List<ColorRGBA> palette) {
		this.palette = palette;
		for (ColorTabPanel p : panels) {
			p.setPalette(palette);
		}
		content.layoutChildren();
	}

	public void setColor(ColorRGBA color) {
		this.color = color == null ? null : color.clone();
		for (ColorTabPanel c : panels) {
			c.setColor(this.color);
		}
		updateHEX();
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
		onChange(this.color);
	}
}
