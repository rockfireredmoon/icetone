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
import icetone.core.BaseScreen;
import icetone.core.Element;
import icetone.core.Form;
import icetone.core.Orientation;
import icetone.core.ToolKit;
import icetone.core.layout.Border;
import icetone.core.layout.BorderLayout;
import icetone.core.layout.FillLayout;
import icetone.core.layout.GridLayout;
import icetone.core.layout.mig.MigLayout;
import icetone.core.utils.Alarm.AlarmTask;
import icetone.extras.util.ExtrasUtil;

/**
 * @author t0neg0d
 * @author rockfire
 */
public abstract class ColorRGBTab extends Element implements ColorTabPanel {

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
	private Spinner<Integer> hueSpinner;
	private Spinner<Integer> saturationSpinner;
	private Spinner<Integer> brightnessSpinner;
	private ColorRestrictionType restrictionType = ColorRestrictionType.getDefaultType();
	private AlarmTask changeTask;
	private float changeEventDelay = 0;

	/**
	 * Creates a new instance of the XColorWheel control
	 *
	 * @param screen   The screen control the Element is to be added to
	 * @param position A Vector2f containing the x/y position of the Element
	 */
	public ColorRGBTab(BaseScreen screen, boolean includeAlpha) {
		super(screen);
		this.includeAlpha = includeAlpha;

		// Container element for swatch / dial
		primarySelector = new Dial<Integer>(screen);
		primarySelector.onChange(evt -> {
			if (!evt.getSource().isAdjusting()) {
				setHFromWheel();
				HSLToRGB();
				displayFactoredColor();
				triggerChange(getColor());
			}
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
			if (!evt.getSource().isAdjusting()) {
				RGBToHSL();
				updateWheel();
				displayFactoredColor();
				triggerChange(getColor());
			}
		});
		redSlider.addStyleClass("red-slider");
		redSlider.setInterval(25);
		redSlider.setLockToStep(true);
		redSlider.setSliderModel(redModel = new IntegerRangeSliderModel(0, 255, 0, 2));
		redSlider.getMaterial().setColor("Color", new ColorRGBA(1.0f, 0.0f, 0.0f, 1.0f));

		Label lR = new Label(screen);
		lR.setTextVAlign(BitmapFont.VAlign.Center);
		lR.setText("R");

		redSpinner = new Spinner<Integer>(screen, Orientation.HORIZONTAL, false);
		redSpinner.onChange(evt -> {
			if (!evt.getSource().isAdjusting())
				redSlider.setSelectedValue(evt.getNewValue());
		});
		redSpinner.setInterval(25);
		redSpinner.addStyleClass("red-spinner");
		redSpinner.setSpinnerModel(new IntegerRangeSpinnerModel(0, 255, 1, 0));
		redSpinner.getTextField().setType(TextField.Type.NUMERIC);
		redSpinner.getTextField().setMaxLength(5);

		// Green
		greenSlider = new Slider<Integer>(screen, Orientation.HORIZONTAL);
		greenSlider.onChanged(evt -> {
			if (!evt.getSource().isAdjusting()) {
				RGBToHSL();
				updateWheel();
				displayFactoredColor();
				triggerChange(getColor());
			}
		});
		greenSlider.setInterval(25);
		greenSlider.addStyleClass("green-slider");
		greenSlider.setSliderModel(greenModel = new IntegerRangeSliderModel(0, 255, 0, 2));
		greenSlider.setLockToStep(true);
		greenSlider.getMaterial().setColor("Color", new ColorRGBA(0.0f, 1.0f, 0.0f, 1.0f));

		Label lG = new Label(screen);
		lG.setTextVAlign(BitmapFont.VAlign.Center);
		lG.setText("G");

		greenSpinner = new Spinner<Integer>(screen, Orientation.HORIZONTAL, false);
		greenSpinner.onChange(evt -> {
			if (!evt.getSource().isAdjusting())
				greenSlider.setSelectedValue(evt.getNewValue());
		});
		greenSpinner.addStyleClass("green-spinner");
		greenSpinner.setInterval(25);
		greenSpinner.setSpinnerModel(new IntegerRangeSpinnerModel(0, 255, 1, 0));
		greenSpinner.getTextField().setType(TextField.Type.NUMERIC);
		greenSpinner.getTextField().setMaxLength(5);

		// Blue
		blueSlider = new Slider<Integer>(screen, Orientation.HORIZONTAL);
		blueSlider.onChanged(evt -> {
			if (!evt.getSource().isAdjusting()) {
				RGBToHSL();
				updateWheel();
				displayFactoredColor();
				triggerChange(getColor());
			}
		});
		blueSlider.addStyleClass("blue-slider");
		blueSlider.setInterval(25);
		blueSlider.setLockToStep(true);
		blueSlider.setSliderModel(blueModel = new IntegerRangeSliderModel(0, 255, 0, 2));

		blueSlider.getMaterial().setColor("Color", new ColorRGBA(0.0f, 0.0f, 1.0f, 1.0f));

		Label lB = new Label(screen);
		lB.setTextVAlign(BitmapFont.VAlign.Center);
		lB.setText("B");

		blueSpinner = new Spinner<Integer>(screen, Orientation.HORIZONTAL, false);
		blueSpinner.onChange(evt -> {
			if (!evt.getSource().isAdjusting())
				blueSlider.setSelectedValue(evt.getNewValue());
		});
		blueSpinner.setInterval(25);
		blueSpinner.addStyleClass("blue-spinner");
		blueSpinner.setSpinnerModel(new IntegerRangeSpinnerModel(0, 255, 1, 0));
		blueSpinner.getTextField().setType(TextField.Type.NUMERIC);
		blueSpinner.getTextField().setMaxLength(5);

		hueSlider = new Slider<Integer>(screen, Orientation.HORIZONTAL);
		hueSlider.addStyleClass("hue-slider");
		hueSlider.onChanged(evt -> {
			if (!evt.getSource().isAdjusting()) {
				hueSpinner.runAdjusting(() -> hueSpinner.setSelectedValue(evt.getNewValue()));
				HSLToRGB();
				updateWheel();
				displayFactoredColor();
				triggerChange(getColor());
			}
		});
		hueSlider.setLockToStep(true);
		hueSlider.setSliderModel(hueModel = new IntegerRangeSliderModel(0, 100, 0));

		Element hueC = new Element(screen);
//		hueC.setAsContainerOnly();
		hueC.setLayoutManager(new FillLayout());
		hueC.setStyleClass("hue-container");
		hueC.addElement(getHueSliderBG());
		hueC.addElement(hueSlider);

		Label lH = new Label(screen);
		lH.setTextVAlign(BitmapFont.VAlign.Center);
		lH.setText("H");

		hueSpinner = new Spinner<Integer>(screen, Orientation.HORIZONTAL, false);
		hueSpinner.onChange(evt -> {
			if (!evt.getSource().isAdjusting())
				hueSlider.setSelectedValue(evt.getNewValue());
		});
		hueSpinner.setInterval(25);
		hueSpinner.addStyleClass("hue-spinner");
		hueSpinner.setSpinnerModel(new IntegerRangeSpinnerModel(0, 100, 1, 0));
		hueSpinner.getTextField().setType(TextField.Type.NUMERIC);
		hueSpinner.getTextField().setMaxLength(5);

		saturationSlider = new GradientSlider(screen, Orientation.HORIZONTAL);
		saturationSlider.onChanged(evt -> {
			if (!evt.getSource().isAdjusting()) {
				saturationSpinner.setSelectedValue(evt.getNewValue());
				HSLToRGB();
				updateWheel();
				displayFactoredColor();
				triggerChange(getColor());
			}
		});
		saturationSlider.addStyleClass("saturation-slider");
		saturationSlider.setLockToStep(true);
		saturationSlider.setSliderModel(satModel = new IntegerRangeSliderModel(0, 100, 0));
		saturationSlider.getMaterial().setColor("Color", new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
		saturationSlider.getMaterial().setBoolean("VertexColor", true);
		saturationSlider.getModel().setGradientFillHorizontal(ColorRGBA.Gray,
				new ColorRGBA(getRed(), getGreen(), getBlue(), 1.0f));

		saturationSpinner = new Spinner<Integer>(screen, Orientation.HORIZONTAL, false);
		saturationSpinner.onChange(evt -> {
			if (!evt.getSource().isAdjusting())
				saturationSlider.setSelectedValue(evt.getNewValue());
		});
		saturationSpinner.setInterval(25);
		saturationSpinner.addStyleClass("saturation-spinner");
		saturationSpinner.setSpinnerModel(new IntegerRangeSpinnerModel(0, 100, 1, 0));
		saturationSpinner.getTextField().setType(TextField.Type.NUMERIC);
		saturationSpinner.getTextField().setMaxLength(5);

		Label lS = new Label(screen);
		lS.setTextVAlign(BitmapFont.VAlign.Center);
		lS.setText("S");

		brightnessSlider = new GradientSlider(screen, Orientation.HORIZONTAL);
		brightnessSlider.addStyleClass("brightness-container");
		brightnessSlider.onChanged(evt -> {
			if (!evt.getSource().isAdjusting()) {
				brightnessSpinner.setSelectedValue(evt.getNewValue());
				updateWheel();
				HSLToRGB();
				displayFactoredColor();
				triggerChange(getColor());
			}
		});
		brightnessSlider.setLockToStep(true);
		brightnessSlider.setSliderModel(lightModel = new IntegerRangeSliderModel(0, 100, 0));
		brightnessSlider.getMaterial().setBoolean("VertexColor", true);

		brightnessSpinner = new Spinner<Integer>(screen, Orientation.HORIZONTAL, false);
		brightnessSpinner.onChange(evt -> {
			if (!evt.getSource().isAdjusting())
				brightnessSlider.setSelectedValue(evt.getNewValue());
		});
		brightnessSpinner.setInterval(25);
		brightnessSpinner.addStyleClass("brightness-spinner");
		brightnessSpinner.setSpinnerModel(new IntegerRangeSpinnerModel(0, 100, 1, 0));
		brightnessSpinner.getTextField().setType(TextField.Type.NUMERIC);
		brightnessSpinner.getTextField().setMaxLength(5);

		Label lL = new Label(screen);
		lL.setTextVAlign(BitmapFont.VAlign.Center);
		lL.setText("L");

		if (includeAlpha) {
			alphaSlider = new GradientSlider(screen, Orientation.HORIZONTAL);
			alphaSlider.onChanged(evt -> {
				if (!evt.getSource().isAdjusting()) {
					displayFactoredColor();
					triggerChange(getColor());
				}
			});
			alphaSlider.addStyleClass("alpha-slider");
			alphaSlider.setInterval(25);
			alphaSlider.setLockToStep(true);
			alphaSlider.setSliderModel(alphaModel = new IntegerRangeSliderModel(0, 100, 0));
			alphaSlider.getMaterial().setColor("Color", new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
			alphaSlider.getMaterial().setBoolean("VertexColor", true);
			alphaSlider.getModel().setGradientFillHorizontal(getColorNoAlpha(), getColorFullAlpha());

			lA = new Label(screen);
			lA.setTextVAlign(BitmapFont.VAlign.Center);
			lA.setText("A");

			alphaSpinner = new Spinner<Integer>(screen, Orientation.HORIZONTAL, false);
			alphaSpinner.onChange(evt -> {
				if (!evt.getSource().isAdjusting())
					alphaSlider.setSelectedValue(evt.getNewValue());
			});
			alphaSpinner.setSpinnerModel(new IntegerRangeSpinnerModel(0, 255, 1, 0));
			alphaSpinner.addStyleClass("alpha-spinner");
			alphaSpinner.getTextField().setType(TextField.Type.NUMERIC);
			alphaSpinner.getTextField().setMaxLength(5);
			alphaSpinner.setInterval(25);

		}

		// redSlider.runAdjusting(() -> redSlider.setSelectedValue(255));
		// hueSlider.setSelectedValueNoCallback(100);
		// saturationSlider.setSelectedValueNoCallback(100);
		// brightnessSlider.setSelectedValueNoCallback(100);
		// if (includeAlpha) {
		// alphaSlider.setSelectedValueNoCallback(100);
		// }

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
		slidersContainer.addElement(lH);
		slidersContainer.addElement(hueSpinner);

		slidersContainer.addElement(saturationSlider, "growx");
		slidersContainer.addElement(lS);
		slidersContainer.addElement(saturationSpinner);

		slidersContainer.addElement(brightnessSlider, "growx");
		slidersContainer.addElement(lL);
		slidersContainer.addElement(brightnessSpinner);

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

	public ColorRestrictionType getRestrictionType() {
		return restrictionType;
	}

	public void setRestrictionType(ColorRestrictionType restrictionType) {
		this.restrictionType = restrictionType;
		setEnabled(restrictionType == ColorRestrictionType.DEVELOPMENT);
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
		alphaSlider.runAdjusting(() -> alphaSlider.setSelectedValue(Math.max(0, Math.min(100, (int) (alpha * 100)))));
		displayFactoredColor();
	}

	public void setBlue(float blue) {
		blueSlider.runAdjusting(() -> blueSlider.setSelectedValue(Math.max(0, Math.min(255, (int) (blue * 255)))));
		RGBToHSL();
		displayFactoredColor();
	}

	public void setBlue(int blue) {
		blueSlider.runAdjusting(() -> blueSlider.setSelectedValue(Math.max(0, Math.min(255, blue))));
		RGBToHSL();
		displayFactoredColor();
	}

	public void setColor(ColorRGBA color) {
		if (color == null)
			setColor(ColorRGBA.Black);
		else
			setColor(color.r, color.g, color.b, color.a);
	}

	public void setColor(float red, float green, float blue) {
		setColor(red, green, blue, 1f);
	}

	public void setColor(float red, float green, float blue, float alpha) {
		redSlider.runAdjusting(() -> redSlider.setSelectedValue(Math.max(0, Math.min(255, (int) (red * 255)))));
		greenSlider.runAdjusting(() -> greenSlider.setSelectedValue(Math.max(0, Math.min(255, (int) (green * 255)))));
		blueSlider.runAdjusting(() -> blueSlider.setSelectedValue(Math.max(0, Math.min(255, (int) (blue * 255)))));
		if (includeAlpha) {
			alphaSlider
					.runAdjusting(() -> alphaSlider.setSelectedValue(Math.max(0, Math.min(100, (int) (alpha * 100)))));
		}
		RGBToHSL();
		primarySelector.runAdjusting(() -> updateWheel());
		displayFactoredColor();
	}

	public void setColor(int red, int green, int blue) {
		redSlider.runAdjusting(() -> redSlider.setSelectedValue(Math.max(0, Math.min(255, red))));
		greenSlider.runAdjusting(() -> greenSlider.setSelectedValue(Math.max(0, Math.min(255, green))));
		blueSlider.runAdjusting(() -> blueSlider.setSelectedValue(Math.max(0, Math.min(255, blue))));
		if (includeAlpha) {
			alphaSlider.runAdjusting(() -> alphaSlider.setSelectedValue(100));
		}
		RGBToHSL();
		displayFactoredColor();
	}

	public void setGreen(float green) {
		greenSlider.runAdjusting(() -> greenSlider.setSelectedValue(Math.max(0, Math.min(255, (int) (green * 255)))));
		RGBToHSL();
		displayFactoredColor();
	}

	public void setGreen(int green) {
		greenSlider.runAdjusting(() -> greenSlider.setSelectedValue(Math.max(0, Math.min(255, green))));
		RGBToHSL();
		displayFactoredColor();
	}

	public void setHue(float hue) {
		hueSlider.runAdjusting(() -> hueSlider.setSelectedValue(Math.max(0, Math.min(100, (int) (hue * 100)))));
		HSLToRGB();
		displayFactoredColor();
	}

	public void setLight(float light) {
		brightnessSlider
				.runAdjusting(() -> brightnessSlider.setSelectedValue(Math.max(0, Math.min(100, (int) (light * 100)))));
		HSLToRGB();
		displayFactoredColor();
	}

	public void setPalette(List<ColorRGBA> palette) {
	}

	public void setRed(float red) {
		redSlider.runAdjusting(() -> redSlider.setSelectedValue(Math.max(0, Math.min(255, (int) (red * 255)))));
		RGBToHSL();
		displayFactoredColor();
	}

	public void setRed(int red) {
		redSlider.runAdjusting(() -> redSlider.setSelectedValue(Math.max(0, Math.min(255, red))));
		RGBToHSL();
		displayFactoredColor();
	}

	public void setSaturation(float saturation) {
		saturationSlider.runAdjusting(
				() -> saturationSlider.setSelectedValue(Math.max(0, Math.min(100, (int) (saturation * 100)))));
		HSLToRGB();
		displayFactoredColor();
	}

	public float getChangeEventDelay() {
		return changeEventDelay;
	}

	public void setChangeEventDelay(float changeEventDelay) {
		this.changeEventDelay = changeEventDelay;
	}

	protected void triggerChange(ColorRGBA rgba) {
		if (changeEventDelay == 0)
			onChange(rgba);
		else {
			if (changeTask != null)
				changeTask.cancel();
			changeTask = ToolKit.get().getAlarm().timed(() -> onChange(rgba), 0.8f);
		}
	}

	protected void updateGradients() {
		float av = average();
		// Saturation

		float[] hsv = colorToHSL(getColor());
		ColorRGBA start = hslToColor(new float[] { hsv[0], 0, 1.0f });
		ColorRGBA end = hslToColor(new float[] { hsv[0], 1.0f, 1.0f });
		saturationSlider.getModel().setGradientFillHorizontal(start, end);

		// Brightness
		brightnessSlider.getMaterial().setColor("Color", new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
		start = hslToColor(new float[] { hsv[0], hsv[1], 0 });
		end = hslToColor(new float[] { hsv[0], hsv[1], 1.0f });
		brightnessSlider.getModel().setGradientFillHorizontal(start, end);

		// Alpha
		if (includeAlpha) {
			alphaSlider.getMaterial().setColor("Color", new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
			alphaSlider.getModel().setGradientFillHorizontal(getColorNoAlpha(), getColorFullAlpha());
		}
	}

	private float average() {
		float sum = getRed() + getGreen() + getBlue();
		return sum / 3;
	}

	private void displayFactoredColor() {
		redSpinner.runAdjusting(() -> redSpinner.setSelectedValue(redModel.getValue()));
		greenSpinner.runAdjusting(() -> greenSpinner.setSelectedValue((greenModel.getValue())));
		blueSpinner.runAdjusting(() -> blueSpinner.setSelectedValue(blueModel.getValue()));
		if (includeAlpha) {
			alphaSpinner.runAdjusting(() -> alphaSpinner.setSelectedValue((int) (getAlpha() * 100f)));
		}
		ColorRGBA color = getColor();
		colorSwatch.setDefaultColor(color);
		updateGradients();
	}

	private BaseElement getHueSliderBG() {
		Element spectrum = new Element(screen, new GridLayout(6, 1));
		spectrum.addStyleClass("spectrum");

		BaseElement bg1 = new BaseElement(screen);
		bg1.getModel().setGradientFillHorizontal(new ColorRGBA(1, 0, 0, 1), new ColorRGBA(1, 1, 0, 1));
		bg1.getMaterial().setColor("Color", ColorRGBA.White);
		bg1.getMaterial().setBoolean("VertexColor", true);
		spectrum.addElement(bg1);

		BaseElement bg2 = new BaseElement(screen);
		bg2.getModel().setGradientFillHorizontal(new ColorRGBA(1, 1, 0, 1), new ColorRGBA(0, 1, 0, 1));
		bg2.getMaterial().setColor("Color", ColorRGBA.White);
		bg2.getMaterial().setBoolean("VertexColor", true);
		spectrum.addElement(bg2);

		BaseElement bg3 = new BaseElement(screen);
		bg3.getModel().setGradientFillHorizontal(new ColorRGBA(0, 1, 0, 1), new ColorRGBA(0, 1, 1, 1));
		bg3.getMaterial().setColor("Color", ColorRGBA.White);
		bg3.getMaterial().setBoolean("VertexColor", true);
		spectrum.addElement(bg3);

		BaseElement bg4 = new BaseElement(screen);
		bg4.getModel().setGradientFillHorizontal(new ColorRGBA(0, 1, 1, 1), new ColorRGBA(0, 0, 1, 1));
		bg4.getMaterial().setColor("Color", ColorRGBA.White);
		bg4.getMaterial().setBoolean("VertexColor", true);
		spectrum.addElement(bg4);

		BaseElement bg5 = new BaseElement(screen);
		bg5.getModel().setGradientFillHorizontal(new ColorRGBA(0, 0, 1, 1), new ColorRGBA(1, 0, 1, 1));
		bg5.getMaterial().setColor("Color", ColorRGBA.White);
		bg5.getMaterial().setBoolean("VertexColor", true);
		spectrum.addElement(bg5);

		BaseElement bg6 = new BaseElement(screen);
		bg6.getModel().setGradientFillHorizontal(new ColorRGBA(1, 0, 1, 1), new ColorRGBA(1, 0, 0, 1));
		bg6.getMaterial().setColor("Color", ColorRGBA.White);
		bg6.getMaterial().setBoolean("VertexColor", true);
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
		int rgb = Color.HSBtoRGB(getHue(), getSaturation(), getLight());
		redSlider.runAdjusting(() -> redSlider.setSelectedValue((rgb >> 16) & 0xff));
		greenSlider.runAdjusting(() -> greenSlider.setSelectedValue((rgb >> 8) & 0xff));
		blueSlider.runAdjusting(() -> blueSlider.setSelectedValue(rgb & 0xff));
	}

	private void RGBToHSL() {
		float[] hsv = ExtrasUtil.toHSB(getColor());
		hueSlider.runAdjusting(() -> hueSlider.setSelectedValue((int) (hsv[0] * 100)));
		saturationSlider.runAdjusting(() -> saturationSlider.setSelectedValue((int) (hsv[1] * 100)));
		brightnessSlider.runAdjusting(() -> brightnessSlider.setSelectedValue((int) (hsv[2] * 100)));
		hueSpinner.runAdjusting(() -> hueSpinner.setSelectedValue((int) (hsv[0] * 100)));
		saturationSpinner.runAdjusting(() -> saturationSpinner.setSelectedValue((int) (hsv[1] * 100)));
		brightnessSpinner.runAdjusting(() -> brightnessSpinner.setSelectedValue((int) (hsv[2] * 100)));
	}

	private void setHFromWheel() {
		hueSlider.runAdjusting(() -> {
			int hIndex = primarySelector.getSelectedIndex();
			hIndex -= 51;
			if (hIndex < 0) {
				hIndex += 101;
			}
			hueSlider.setSelectedValue(hIndex);
			hueSpinner.setSelectedValue(hIndex);
		});
	}

	private void updateWheel() {
		int hIndex = hueModel.getValue() + 51;
		if (hIndex > 100) {
			hIndex -= 101;
		}
		primarySelector.setSelectedIndex(hIndex);
	}

	class GradientSlider extends Slider<Integer> {

		GradientSlider(BaseScreen screen, Orientation horizontal) {
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
