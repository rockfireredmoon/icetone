package icetone.examples;

import com.jme3.app.SimpleApplication;
import com.jme3.math.FastMath;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

import icetone.controls.containers.Panel;
import icetone.controls.extras.Indicator;
import icetone.controls.extras.Indicator.DisplayMode;
import icetone.controls.text.Label;
import icetone.core.ElementContainer;
import icetone.core.Orientation;
import icetone.core.Screen;
import icetone.core.layout.mig.MigLayout;
import icetone.effects.Interpolation;

/**
 * This example shows some examples of usage of the {@link Indicator} control, a
 * 'Progress Bar' type component.
 */
public class IndicatorExample extends SimpleApplication {

	public static void main(String[] args) {
		IndicatorExample app = new IndicatorExample();
		app.start();
	}

	@Override
	public void simpleInitApp() {

		
		/*
		 * We are only using a single screen, so just initialise it (and you
		 * don't need to provide the screen instance to each control).
		 * 
		 * It is passed to the buildExample method in this way to help
		 * ExampleRunner so this example can be run from there and as a
		 * standalone JME application
		 */
		buildExample(new Screen(this));
	}

	protected void buildExample(ElementContainer<?, ?> container) {

		/*
		 * A Horizontal Indicator displaying percentages in the normal direction
		 */
		Indicator indicator1 = new Indicator().setDisplayMode(DisplayMode.percentages).setMaxValue(100)
				.setCurrentValue(75);
		indicator1.addControl(new UpdateControl());

		Panel panel1 = new Panel(new MigLayout("fill, wrap 1", "[grow]", "[][]"));
		panel1.setPosition(100, 100);
		panel1.addElement(indicator1, "growx");
		panel1.addElement(new Label("Horizontal, Percentages, Forward"), "ax 50%");

		/*
		 * A Horizontal Indicator displaying the value in the reverse direction
		 */
		Indicator indicator2 = new Indicator().setMaxValue(100).setDisplayMode(DisplayMode.value).setCurrentValue(75)
				.setReverseDirection(true);
		indicator2.addControl(new UpdateControl());

		Panel panel2 = new Panel(new MigLayout("fill, wrap 1", "[grow]", "[][]"));
		panel2.addElement(indicator2, "growx");
		panel2.addElement(new Label("Horizontal, Valuue, Reverse"), "ax 50%");
		panel2.setPosition(200, 200);

		/*
		 * A Vertical Indicator displaying custom text in the forward direction
		 */
		Indicator indicator3 = new Indicator() {
			@Override
			protected void refactorText() {
				getOverlayElement().setText(String.format("Custom text %.3f", getCurrentPercentage()));
				super.refactorText();
			}
		}.setMaxValue(100).setDisplayMode(DisplayMode.none).setCurrentValue(75);
		indicator3.addControl(new UpdateControl());
		Panel panel3 = new Panel(new MigLayout("fill, wrap 1", "[]", "[grow][]"));
		panel3.addElement(indicator3, "growx, ax 50%");
		panel3.addElement(new Label("Vertical,Custom,Forward"), "ax 50%");
		panel3.setPosition(300, 300);

		/*
		 * A Vertical Indicator displaying nothing with animation (jumps every 2
		 * seconds)
		 */
		Indicator indicator4 = new Indicator().setOrientation(Orientation.VERTICAL).setDisplayMode(DisplayMode.none)
				.setMaxValue(100).setCurrentValue(75).setAnimationTime(0.5f).setInterpolation(Interpolation.bounce);
		indicator4.addControl(new AbstractControl() {
			/* This control updates the bar to a random value every 2 seconds */
			float t = 0;

			@Override
			protected void controlUpdate(float tpf) {
				t += tpf;
				if (t > 2) {
					t = 0;
					indicator4.setCurrentValue(FastMath.nextRandomFloat() * indicator4.getMaxValue());
				}
			}

			@Override
			protected void controlRender(RenderManager rm, ViewPort vp) {
			}
		});

		Panel panel4 = new Panel(new MigLayout("fill, wrap 1", "[]", "[:260:,grow][]"));
		panel4.setPosition(400, 400);
		panel4.addElement(indicator4, "growy, ax 50%");
		panel4.addElement(new Label("Vertical,Animation"), "ax 50%");

		// Build the screen
		container.showElement(panel1);
		container.showElement(panel2);
		container.showElement(panel3);
		container.showElement(panel4);
		

	}

	class UpdateControl extends AbstractControl {

		@Override
		protected void controlUpdate(float tpf) {
			Indicator i = (Indicator) spatial;
			i.setCurrentValue(i.getCurrentValue() + tpf);
			if (i.getCurrentValue() >= i.getMaxValue()) {
				i.setCurrentValue(0);
			}
		}

		@Override
		protected void controlRender(RenderManager rm, ViewPort vp) {
		}

	}

}
