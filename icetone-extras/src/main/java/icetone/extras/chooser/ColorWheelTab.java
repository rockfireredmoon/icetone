/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icetone.extras.chooser;

import java.awt.Color;
import java.util.List;

import com.jme3.font.BitmapFont;
import com.jme3.math.ColorRGBA;

import icetone.controls.containers.Bordered;
import icetone.controls.lists.Dial;
import icetone.controls.lists.IntegerRangeSliderModel;
import icetone.controls.lists.IntegerRangeSpinnerModel;
import icetone.controls.lists.Slider;
import icetone.controls.lists.Spinner;
import icetone.controls.text.Label;
import icetone.controls.text.TextField;
import icetone.core.BaseElement;
import icetone.core.ElementManager;
import icetone.core.Form;
import icetone.core.Orientation;
import icetone.core.Element;
import icetone.core.layout.Border;
import icetone.core.layout.BorderLayout;
import icetone.core.layout.FillLayout;
import icetone.core.layout.GridLayout;
import icetone.core.layout.mig.MigLayout;

/**
 *
 * @author t0neg0d
 */
public abstract class ColorWheelTab extends Element implements ColorSelector.ColorTabPanel {

	private IntegerRangeSliderModel alphaModel;
	private Spinner<Integer> alphaSpinner;
	private final IntegerRangeSliderModel blueModel;
	private Spinner<Integer> blueSpinner;
	private Element colorSwatch;
	private final Form colourForm;
	private final IntegerRangeSliderModel greenModel;
	private Spinner<Integer> greenSpinner;
	private final IntegerRangeSliderModel hueModel;
	private final boolean includeAlpha;
	private Label lA;
	private final IntegerRangeSliderModel lightModel;
	private Dial<Integer> primarySelector;
	private final IntegerRangeSliderModel redModel;
	private Slider<Integer> redSlider, greenSlider, blueSlider, hueSlider, brightnessSlider, saturationSlider,
			alphaSlider;
	private Spinner<Integer> redSpinner;
	private final IntegerRangeSliderModel satModel;

	/**
	 * Creates a new instance of the XColorWheel control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 */
	public ColorWheelTab(ElementManager<?> screen, boolean includeAlpha) {
		super(screen);
		this.includeAlpha = includeAlpha;

		// Container element for swatch / dial
		primarySelector = new Dial<Integer>(screen);
		primarySelector.onChange(evt -> {
			setHFromWheel();
			HSLToRGB();
			displayFactoredColor();
			ColorWheelTab.this.onChange(getColor());
		});
		primarySelector.setStyleClass("wheel");

		colorSwatch = new Element(screen);
		colorSwatch.setStyleClass("swatch");
		colorSwatch.setIgnoreGlobalAlpha(true);

		//
		Element swatchContainer = new Element(screen, new BorderLayout(8, 8)) {
			{
				styleClass = "swatch-container";
			}
		};
		swatchContainer.addElement(primarySelector, Border.NORTH);
		swatchContainer.addElement(new Bordered(colorSwatch), Border.CENTER);

		//
		// Sliders
		//

		redSlider = new Slider<Integer>(screen, Orientation.HORIZONTAL);
		redSlider.onChanged(evt -> {
			RGBToHSL();
			updateWheel();
			displayFactoredColor();
			ColorWheelTab.this.onChange(getColor());
		});
		redSlider.addStyleClass("red-slider");
		redSlider.setInterval(25);
		redSlider.setLockToStep(true);
		redSlider.setSliderModel(redModel = new IntegerRangeSliderModel(0, 255, 0, 2));
		redSlider.getElementMaterial().setColor("Color", new ColorRGBA(1.0f, 0.0f, 0.0f, 1.0f));

		Label lR = new Label(screen);
		lR.setTextVAlign(BitmapFont.VAlign.Center);
		lR.setText("R");

		redSpinner = new Spinner<Integer>(screen, Orientation.HORIZONTAL, false);
		redSpinner.onChange(evt -> redSlider.setSelectedValue(evt.getNewValue()));
		redSpinner.setInterval(25);
		redSpinner.addStyleClass("red-spinner");
		redSpinner.setSpinnerModel(new IntegerRangeSpinnerModel(0, 255, 1, 0));
		redSpinner.getTextField().setType(TextField.Type.NUMERIC);
		redSpinner.getTextField().setMaxLength(5);

		// Green
		greenSlider = new Slider<Integer>(screen, Orientation.HORIZONTAL);
		greenSlider.onChanged(evt -> {
			RGBToHSL();
			updateWheel();
			displayFactoredColor();
			ColorWheelTab.this.onChange(getColor());
		});
		greenSlider.setInterval(25);
		greenSlider.addStyleClass("green-slider");
		greenSlider.setSliderModel(greenModel = new IntegerRangeSliderModel(0, 255, 0, 2));
		greenSlider.setLockToStep(true);
		greenSlider.getElementMaterial().setColor("Color", new ColorRGBA(0.0f, 1.0f, 0.0f, 1.0f));

		Label lG = new Label(screen);
		lG.setTextVAlign(BitmapFont.VAlign.Center);
		lG.setText("G");

		greenSpinner = new Spinner<Integer>(screen, Orientation.HORIZONTAL, false);
		greenSpinner.onChange(evt -> greenSlider.setSelectedValue(evt.getNewValue()));
		greenSpinner.addStyleClass("green-spinner");
		greenSpinner.setInterval(25);
		greenSpinner.setSpinnerModel(new IntegerRangeSpinnerModel(0, 255, 1, 0));
		greenSpinner.getTextField().setType(TextField.Type.NUMERIC);
		greenSpinner.getTextField().setMaxLength(5);

		// Blue
		blueSlider = new Slider<Integer>(screen, Orientation.HORIZONTAL);
		blueSlider.onChanged(evt -> {
			RGBToHSL();
			updateWheel();
			displayFactoredColor();
			ColorWheelTab.this.onChange(getColor());
		});
		blueSlider.addStyleClass("blue-slider");
		blueSlider.setInterval(25);
		blueSlider.setLockToStep(true);
		blueSlider.setSliderModel(blueModel = new IntegerRangeSliderModel(0, 255, 0, 2));

		blueSlider.getElementMaterial().setColor("Color", new ColorRGBA(0.0f, 0.0f, 1.0f, 1.0f));

		Label lB = new Label(screen);
		lB.setTextVAlign(BitmapFont.VAlign.Center);
		lB.setText("B");

		blueSpinner = new Spinner<Integer>(screen, Orientation.HORIZONTAL, false);
		blueSpinner.onChange(evt -> blueSlider.setSelectedValue(evt.getNewValue()));
		blueSpinner.setInterval(25);
		blueSpinner.addStyleClass("blue-spinner");
		blueSpinner.setSpinnerModel(new IntegerRangeSpinnerModel(0, 255, 1, 0));
		blueSpinner.getTextField().setType(TextField.Type.NUMERIC);
		blueSpinner.getTextField().setMaxLength(5);

		hueSlider = new Slider<Integer>(screen, Orientation.HORIZONTAL);
		hueSlider.addStyleClass("hue-slider");
		hueSlider.onChanged(evt -> {
			HSLToRGB();
			updateWheel();
			displayFactoredColor();
			ColorWheelTab.this.onChange(getColor());
		});
		hueSlider.setLockToStep(true);
		hueSlider.setSliderModel(hueModel = new IntegerRangeSliderModel(0, 100, 0));

		Element hueC = new Element(screen);
		hueC.setAsContainerOnly();
		hueC.setLayoutManager(new FillLayout());
		hueC.setStyleClass("hue-container");
		hueC.addElement(getHueSliderBG());
		hueC.addElement(hueSlider);

		Label lH = new Label(screen);
		lH.setTextVAlign(BitmapFont.VAlign.Center);
		lH.setText("H");

		saturationSlider = new GradientSlider(screen, Orientation.HORIZONTAL);
		saturationSlider.onChanged(evt -> {
			HSLToRGB();
			updateWheel();
			displayFactoredColor();
			ColorWheelTab.this.onChange(getColor());
		});
		saturationSlider.addStyleClass("saturation-slider");
		saturationSlider.setLockToStep(true);
		saturationSlider.setSliderModel(satModel = new IntegerRangeSliderModel(0, 100, 0));
		saturationSlider.getElementMaterial().setColor("Color", new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
		saturationSlider.getElementMaterial().setBoolean("VertexColor", true);
		saturationSlider.getModel().setGradientFillHorizontal(ColorRGBA.Gray,
				new ColorRGBA(getRed(), getGreen(), getBlue(), 1.0f));

		Label lS = new Label(screen);
		lS.setTextVAlign(BitmapFont.VAlign.Center);
		lS.setText("S");

		brightnessSlider = new GradientSlider(screen, Orientation.HORIZONTAL);
		brightnessSlider.addStyleClass("brightness-container");
		brightnessSlider.onChanged(evt -> {
			updateWheel();
			HSLToRGB();
			displayFactoredColor();
			ColorWheelTab.this.onChange(getColor());
		});
		brightnessSlider.setLockToStep(true);
		brightnessSlider.setSliderModel(lightModel = new IntegerRangeSliderModel(0, 100, 0));
		brightnessSlider.getElementMaterial().setBoolean("VertexColor", true);

		Label lL = new Label(screen);
		lL.setTextVAlign(BitmapFont.VAlign.Center);
		lL.setText("L");

		if (includeAlpha) {
			alphaSlider = new GradientSlider(screen, Orientation.HORIZONTAL);
			alphaSlider.onChanged(evt -> {
				displayFactoredColor();
				ColorWheelTab.this.onChange(getColor());
			});
			alphaSlider.addStyleClass("alpha-slider");
			alphaSlider.setInterval(25);
			alphaSlider.setLockToStep(true);
			alphaSlider.setSliderModel(alphaModel = new IntegerRangeSliderModel(0, 100, 0));
			alphaSlider.getElementMaterial().setColor("Color", new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
			alphaSlider.getElementMaterial().setBoolean("VertexColor", true);
			alphaSlider.getModel().setGradientFillHorizontal(getColorNoAlpha(), getColorFullAlpha());

			lA = new Label(screen);
			lA.setTextVAlign(BitmapFont.VAlign.Center);
			lA.setText("A");

			alphaSpinner = new Spinner<Integer>(screen, Orientation.HORIZONTAL, false);
			alphaSpinner.onChange(evt -> alphaSlider.setSelectedValue(evt.getNewValue()));
			alphaSpinner.setSpinnerModel(new IntegerRangeSpinnerModel(0, 255, 1, 0));
			alphaSpinner.addStyleClass("alpha-spinner");
			alphaSpinner.getTextField().setType(TextField.Type.NUMERIC);
			alphaSpinner.getTextField().setMaxLength(5);
			alphaSpinner.setInterval(25);

		}

		redSlider.setSelectedValueNoCallback(255);
		hueSlider.setSelectedValueNoCallback(100);
		saturationSlider.setSelectedValueNoCallback(100);
		brightnessSlider.setSelectedValueNoCallback(100);
		if (includeAlpha) {
			alphaSlider.setSelectedValueNoCallback(100);
		}

		Element slidersContainer = new Element(screen) {
			{
				styleClass = "sliders";
				layoutManager = new MigLayout(screen, "hidemode 2, wrap 3", "[:128:,grow][][shrink 0]", "[shrink 0]");
			}
		};
		slidersContainer.addElement(redSlider, "growx");
		slidersContainer.addElement(lR);
		slidersContainer.addElement(redSpinner);

		slidersContainer.addElement(greenSlider, "growx");
		slidersContainer.addElement(lG);
		slidersContainer.addElement(greenSpinner);

		slidersContainer.addElement(blueSlider, "growx");
		slidersContainer.addElement(lB);
		slidersContainer.addElement(blueSpinner);

		slidersContainer.addElement(hueC, "growx");
		slidersContainer.addElement(lH, "span 2");

		slidersContainer.addElement(saturationSlider, "growx");
		slidersContainer.addElement(lS, "span 2");

		slidersContainer.addElement(brightnessSlider, "growx");
		slidersContainer.addElement(lL, "span 2");

		if (includeAlpha) {
			slidersContainer.addElement(alphaSlider, "growx");
			slidersContainer.addElement(lA);
			slidersContainer.addElement(alphaSpinner);
		}

		// Form
		colourForm = new Form(screen);
		colourForm.addFormElement(primarySelector);
		colourForm.addFormElement(redSlider);
		colourForm.addFormElement(greenSlider);
		colourForm.addFormElement(blueSlider);
		colourForm.addFormElement(hueSlider);
		colourForm.addFormElement(saturationSlider);
		colourForm.addFormElement(brightnessSlider);
		colourForm.addFormElement(redSpinner);
		colourForm.addFormElement(greenSpinner);
		colourForm.addFormElement(blueSpinner);
		if (alphaSpinner != null) {
			colourForm.addFormElement(alphaSpinner);
		}

		// Build containers
		layoutManager = new BorderLayout();
		addElement(swatchContainer, Border.WEST);
		addElement(slidersContainer, Border.CENTER);
	}

	public final float getAlpha() {
		return (float) alphaModel.getValue() / 100f;
	}

	public final float getBlue() {
		return (float) blueModel.getValue() / 255f;
	}

	public final ColorRGBA getColor() {
		if (includeAlpha) {
			return new ColorRGBA(getRed(), getGreen(), getBlue(), getAlpha());
		} else {
			return getColorFullAlpha();
		}
	}

	public final ColorRGBA getColorFullAlpha() {
		return new ColorRGBA(getRed(), getGreen(), getBlue(), 1);
	}

	public final ColorRGBA getColorNoAlpha() {
		return new ColorRGBA(getRed(), getGreen(), getBlue(), 0);
	}

	public final float getGreen() {
		return (float) greenModel.getValue() / 255f;
	}

	public float getHue() {
		return ((float) hueModel.getValue()) / 100f;
	}

	public float getLight() {
		return (float) lightModel.getValue() / 100f;
	}

	public final float getRed() {
		return (float) redModel.getValue() / 255;
	}

	public float getSaturation() {
		return (float) satModel.getValue() / 100f;
	}

	public abstract void onChange(ColorRGBA color);

	public void setAlpha(float alpha) {
		if (alphaSlider == null) {
			throw new IllegalStateException("No alpha");
		}
		alphaSlider.setSelectedValueNoCallback(Math.max(0, Math.min(100, (int) (alpha * 100))));
		displayFactoredColor();
	}

	public void setBlue(float blue) {
		blueSlider.setSelectedValueNoCallback(Math.max(0, Math.min(255, (int) (blue * 255))));
		RGBToHSL();
		displayFactoredColor();
	}

	public void setBlue(int blue) {
		blueSlider.setSelectedValueNoCallback(Math.max(0, Math.min(255, blue)));
		RGBToHSL();
		displayFactoredColor();
	}

	public void setColor(ColorRGBA color) {
		redSlider.setSelectedValueNoCallback(color == null ? 0 : Math.max(0, Math.min(255, (int) (color.r * 255))));
		greenSlider.setSelectedValueNoCallback(color == null ? 0 : Math.max(0, Math.min(255, (int) (color.g * 255))));
		blueSlider.setSelectedValueNoCallback(color == null ? 0 : Math.max(0, Math.min(255, (int) (color.b * 255))));
		if (includeAlpha) {
			alphaSlider
					.setSelectedValueNoCallback(color == null ? 0 : Math.max(0, Math.min(100, (int) (color.a * 100))));
		}
		RGBToHSL();
		updateWheel();
		displayFactoredColor();
	}

	public void setColor(float red, float green, float blue) {
		redSlider.setSelectedValueNoCallback(Math.max(0, Math.min(255, (int) (red * 255))));
		greenSlider.setSelectedValueNoCallback(Math.max(0, Math.min(255, (int) (green * 255))));
		blueSlider.setSelectedValueNoCallback(Math.max(0, Math.min(255, (int) (blue * 255))));
		if (includeAlpha) {
			alphaSlider.setSelectedValueNoCallback(100);
		}
		RGBToHSL();
		displayFactoredColor();
	}

	public void setColor(float red, float green, float blue, float alpha) {
		redSlider.setSelectedValueNoCallback(Math.max(0, Math.min(255, (int) (red * 255))));
		greenSlider.setSelectedValueNoCallback(Math.max(0, Math.min(255, (int) (green * 255))));
		blueSlider.setSelectedValueNoCallback(Math.max(0, Math.min(255, (int) (blue * 255))));
		if (includeAlpha) {
			alphaSlider.setSelectedValueNoCallback(Math.max(0, Math.min(100, (int) (alpha * 100))));
		}
		RGBToHSL();
		displayFactoredColor();
	}

	public void setColor(int red, int green, int blue) {
		redSlider.setSelectedValueNoCallback(Math.max(0, Math.min(255, red)));
		greenSlider.setSelectedValueNoCallback(Math.max(0, Math.min(255, green)));
		blueSlider.setSelectedValueNoCallback(Math.max(0, Math.min(255, blue)));
		if (includeAlpha) {
			alphaSlider.setSelectedValueNoCallback(100);
		}
		RGBToHSL();
		displayFactoredColor();
	}

	public void setGreen(float green) {
		greenSlider.setSelectedValueNoCallback(Math.max(0, Math.min(255, (int) (green * 255))));
		RGBToHSL();
		displayFactoredColor();
	}

	public void setGreen(int green) {
		greenSlider.setSelectedValueNoCallback(Math.max(0, Math.min(255, green)));
		RGBToHSL();
		displayFactoredColor();
	}

	public void setHue(float hue) {
		hueSlider.setSelectedValueNoCallback(Math.max(0, Math.min(100, (int) (hue * 100))));
		HSLToRGB();
		displayFactoredColor();
	}

	public void setLight(float light) {
		brightnessSlider.setSelectedValueNoCallback(Math.max(0, Math.min(100, (int) (light * 100))));
		HSLToRGB();
		displayFactoredColor();
	}

	public void setPalette(List<ColorRGBA> palette) {
	}

	public void setRed(float red) {
		redSlider.setSelectedValueNoCallback(Math.max(0, Math.min(255, (int) (red * 255))));
		RGBToHSL();
		displayFactoredColor();
	}

	public void setRed(int red) {
		redSlider.setSelectedValueNoCallback(Math.max(0, Math.min(255, red)));
		RGBToHSL();
		displayFactoredColor();
	}

	public void setSaturation(float saturation) {
		saturationSlider.setSelectedValueNoCallback(Math.max(0, Math.min(100, (int) (saturation * 100))));
		HSLToRGB();
		displayFactoredColor();
	}

	protected void updateGradients() {
		float av = average();
		// Saturation

		float[] hsv = colorToHSL(getColor());
		ColorRGBA start = hslToColor(new float[] { hsv[0], 0, 1.0f });
		ColorRGBA end = hslToColor(new float[] { hsv[0], 1.0f, 1.0f });
		saturationSlider.getModel().setGradientFillHorizontal(start, end);

		// Brightness
		brightnessSlider.getElementMaterial().setColor("Color", new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
		start = hslToColor(new float[] { hsv[0], hsv[1], 0 });
		end = hslToColor(new float[] { hsv[0], hsv[1], 1.0f });
		brightnessSlider.getModel().setGradientFillHorizontal(start, end);

		// Alpha
		if (includeAlpha) {
			alphaSlider.getElementMaterial().setColor("Color", new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
			alphaSlider.getModel().setGradientFillHorizontal(getColorNoAlpha(), getColorFullAlpha());
		}
	}

	private float average() {
		float sum = getRed() + getGreen() + getBlue();
		return sum / 3;
	}

	private void displayFactoredColor() {
		redSpinner.setSelectedValue((int) (getRed() * 255f));
		greenSpinner.setSelectedValue((int) (getGreen() * 255f));
		blueSpinner.setSelectedValue((int) (getBlue() * 255f));
		if (includeAlpha) {
			alphaSpinner.setSelectedValue((int) (getAlpha() * 100f));
		}
		ColorRGBA color = getColor();
		colorSwatch.setDefaultColor(color);
		updateGradients();
	}

	private BaseElement getHueSliderBG() {
		Element spectrum = new Element(screen, new GridLayout(6, 1));
		spectrum.setStyleClass("spectrum");

		BaseElement bg1 = new BaseElement(screen);
		bg1.getModel().setGradientFillHorizontal(new ColorRGBA(1, 0, 0, 1), new ColorRGBA(1, 0, 1, 1));
		bg1.getElementMaterial().setColor("Color", ColorRGBA.White);
		bg1.getElementMaterial().setBoolean("VertexColor", true);
		spectrum.addElement(bg1);

		BaseElement bg2 = new BaseElement(screen);
		bg2.getModel().setGradientFillHorizontal(new ColorRGBA(1, 0, 1, 1), new ColorRGBA(0, 0, 1, 1));
		bg2.getElementMaterial().setColor("Color", ColorRGBA.White);
		bg2.getElementMaterial().setBoolean("VertexColor", true);
		spectrum.addElement(bg2);

		BaseElement bg3 = new BaseElement(screen);
		bg3.getModel().setGradientFillHorizontal(new ColorRGBA(0, 0, 1, 1), new ColorRGBA(0, 1, 1, 1));
		bg3.getElementMaterial().setColor("Color", ColorRGBA.White);
		bg3.getElementMaterial().setBoolean("VertexColor", true);
		spectrum.addElement(bg3);

		BaseElement bg4 = new BaseElement(screen);
		bg4.getModel().setGradientFillHorizontal(new ColorRGBA(0, 1, 1, 1), new ColorRGBA(0, 1, 0, 1));
		bg4.getElementMaterial().setColor("Color", ColorRGBA.White);
		bg4.getElementMaterial().setBoolean("VertexColor", true);
		spectrum.addElement(bg4);

		BaseElement bg5 = new BaseElement(screen);
		bg5.getModel().setGradientFillHorizontal(new ColorRGBA(0, 1, 0, 1), new ColorRGBA(1, 1, 0, 1));
		bg5.getElementMaterial().setColor("Color", ColorRGBA.White);
		bg5.getElementMaterial().setBoolean("VertexColor", true);
		spectrum.addElement(bg5);

		BaseElement bg6 = new BaseElement(screen);
		bg6.getModel().setGradientFillHorizontal(new ColorRGBA(1, 1, 0, 1), new ColorRGBA(1, 0, 0, 1));
		bg6.getElementMaterial().setColor("Color", ColorRGBA.White);
		bg6.getElementMaterial().setBoolean("VertexColor", true);
		spectrum.addElement(bg6);

		return spectrum;
	}

	private ColorRGBA hslToColor(float[] hsl) {
		int rgb = Color.HSBtoRGB(hsl[0], hsl[1], hsl[2]);
		float r = ((rgb >> 16) & 0xff);
		float g = ((rgb >> 8) & 0xff);
		float b = (rgb & 0xff);
		return new ColorRGBA(r / 255f, g / 255f, b / 255f, 1.0f);
	}

	private float[] colorToHSL(ColorRGBA col) {
		float[] hsv = new float[3];
		Color.RGBtoHSB(redModel.getValue(), greenModel.getValue(), blueModel.getValue(), hsv);
		return hsv;
	}

	private void HSLToRGB() {
		int rgb = Color.HSBtoRGB(100 - getHue(), getSaturation(), getLight());
		redSlider.setSelectedValueNoCallback((rgb >> 16) & 0xff);
		greenSlider.setSelectedValueNoCallback((rgb >> 8) & 0xff);
		blueSlider.setSelectedValueNoCallback(rgb & 0xff);
	}

	private void RGBToHSL() {
		float[] hsv = new float[3];
		hsv = Color.RGBtoHSB(redModel.getValue(), greenModel.getValue(), blueModel.getValue(), hsv);
		hueSlider.setSelectedValueNoCallback((int) (hsv[0] * 100));
		saturationSlider.setSelectedValueNoCallback((int) (hsv[1] * 100));
		brightnessSlider.setSelectedValueNoCallback((int) (hsv[2] * 100));
	}

	private void setHFromWheel() {
		int hIndex = primarySelector.getSelectedIndex();
		hIndex -= 51;
		if (hIndex < 0) {
			hIndex += 101;
		}
		hueSlider.setSelectedValueNoCallback(hIndex);
	}

	private void updateWheel() {
		int hIndex = hueModel.getValue() + 51;
		if (hIndex > 100) {
			hIndex -= 101;
		}
		primarySelector.setSelectedIndex(hIndex);
	}

	class GradientSlider extends Slider<Integer> {

		GradientSlider(ElementManager screen, Orientation horizontal) {
			super(screen, horizontal);
			setLayoutManager(new SliderLayout<Integer>() {

				@Override
				protected void onLayoutBackground(Slider<Integer> container) {
					super.onLayoutBackground(container);
					updateGradients();
				}

			});
		}

	}
}
