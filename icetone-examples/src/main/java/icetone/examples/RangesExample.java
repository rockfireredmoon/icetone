package icetone.examples;

import com.jme3.app.SimpleApplication;

import icetone.controls.containers.Frame;
import icetone.controls.containers.Panel;
import icetone.controls.lists.Dial;
import icetone.controls.lists.FloatRangeSliderModel;
import icetone.controls.lists.Slider;
import icetone.controls.lists.Spinner;
import icetone.controls.scrolling.ScrollBar;
import icetone.controls.text.Label;
import icetone.core.Element;
import icetone.core.ElementContainer;
import icetone.core.Orientation;
import icetone.core.Screen;
import icetone.core.StyledContainer;
import icetone.core.layout.mig.MigLayout;

/**
 * This example shows some examples of usage of the various controls that deal
 * with a range of values such as {@link Slider}, {@link Dial} and
 * {@link Spinner}
 */
public class RangesExample extends SimpleApplication {

	public static void main(String[] args) {
		RangesExample app = new RangesExample();
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

	protected void buildExample(ElementContainer<?, ?> screen) {

		Label l1 = new Label("-X1-");
		Label l2 = new Label("-X2-");
		Label l3 = new Label("-X3-");
		Label l4 = new Label("-X4-");
		Label l5 = new Label("-X5-");
		Label l6 = new Label("-X6-");

		// Vert slider 1
		Slider<Float> sl1 = new Slider<Float>(Orientation.VERTICAL);
		sl1.setLockToStep(true);
		sl1.setSliderModel(new FloatRangeSliderModel(0f, 100f, 50f, 20f));
		sl1.onChanged(evt -> l1.setText(String.format("%3.2f", evt.getNewValue())));

		// Vert slider 2
		Slider<Float> sl2 = new Slider<Float>(Orientation.VERTICAL);
		sl2.setSliderModel(new FloatRangeSliderModel(0f, 100f, 50f, 20f));
		sl2.setReversed(true);
		sl2.setLockToStep(true);
		sl2.onChanged(evt -> l2.setText(String.format("%3.2f", evt.getNewValue())));

		// Vert slider 3
		Slider<Float> sl3 = new Slider<Float>(Orientation.VERTICAL);
		sl3.setSliderModel(new FloatRangeSliderModel(0f, 1f, 0.50f, 0.05f));
		sl3.setLockToStep(true);
		sl3.onChanged(evt -> l3.setText(String.format("%3.2f", evt.getNewValue())));

		// Hor Slider 1 (reversed)
		Slider<Float> sl4 = new Slider<Float>();
		sl4.setReversed(true);
		sl4.setSliderModel(new FloatRangeSliderModel(0f, 100f, 100f, 20f));
		sl4.onChanged(evt -> l4.setText(String.format("%3.2f", evt.getNewValue())));

		// Hor Slider 2
		Slider<Float> sl5 = new Slider<Float>();
		sl5.setSliderModel(new FloatRangeSliderModel(0f, 100f, 100f, 20f));
		sl5.setLockToStep(true);
		sl5.onChanged(evt -> l5.setText(String.format("%3.2f", evt.getNewValue())));

		// Hor Slider 3
		Slider<Float> sl6 = new Slider<Float>();
		sl6.setSliderModel(new FloatRangeSliderModel(0f, 1f, 0.50f, 0.05f));
		sl6.setLockToStep(false);
		sl6.onChanged(evt -> l6.setText(String.format("%3.2f", evt.getNewValue())));

		/* Sliders Frame */

		Frame sliders = new Frame();
		sliders.setTitle("Slider");
		sliders.setMovable(true);
		sliders.setResizable(true);

		Element contentArea = sliders.getContentArea();
		contentArea
				.setLayoutManager(new MigLayout("fill", "[:32:][:200:][:72:][:72:][:72:]", "[:32:][:72:][:72:][:72:]"));
		contentArea.addElement(new Label(), "span 2");
		contentArea.addElement(l1);
		contentArea.addElement(l2);
		contentArea.addElement(l3, "wrap");
		contentArea.addElement(l4);
		contentArea.addElement(sl4, "growx");
		contentArea.addElement(sl1, "ax 50%, spany 3, growy");
		contentArea.addElement(sl2, "ax 50%, spany 3, growy");
		contentArea.addElement(sl3, "ax 50%, spany 3, growy, wrap");
		contentArea.addElement(l5);
		contentArea.addElement(sl5, "growx, wrap");
		contentArea.addElement(l6);
		contentArea.addElement(sl6, "growx");

		/* Dials */
		Frame dialsFrame = new Frame();
		dialsFrame.setTitle("Dials");
		contentArea = dialsFrame.getContentArea();
		contentArea.setLayoutManager(new MigLayout("", "[fill,grow]", "[fill,grow]"));
		contentArea.addElement(new Dial<Void>()
				.onChange(evt -> evt.getSource().setToolTipText("Vol: " + evt.getSource().getSelectedIndex())));
		contentArea.addElement(new Dial.IntegerRangeDial(1, 5)
				.onChange(evt -> evt.getSource().setToolTipText("Vol: " + evt.getSource().getSelectedIndex())));
		contentArea.addElement(new Dial.StringListDial("North", "South", "East", "West")
				.onChange(evt -> evt.getSource().setToolTipText("Vol: " + evt.getSource().getSelectedIndex())), "wrap");
		contentArea.addElement(new Label("Normal"));
		contentArea.addElement(new Label("Stepped Integer"));
		contentArea.addElement(new Label("Stepped Strings"));
		dialsFrame.setResizable(true);
		dialsFrame.setPosition(10, 400);
		
		/* Panel */
		Panel scrollbarPanel = new Panel();
		scrollbarPanel.setLayoutManager(new MigLayout("wrap 2, ins 0, gap 0, fill", "[fill, grow][]", "[fill, grow][]"));
		StyledContainer content = new StyledContainer();
		content.setLayoutManager(new MigLayout("fill, wrap 1"));
		Label verLabel = new Label("Content");
		Label horLabel = new Label("Content");
		content.addElement(verLabel, "growx");
		content.addElement(horLabel, "growx");
		scrollbarPanel.addElement(content);
		scrollbarPanel.addElement(new ScrollBar(Orientation.VERTICAL)
				.onChanged(evt -> horLabel.setText(String.format("Ver: %3.2f", evt.getNewValue()))));
		scrollbarPanel.addElement(new ScrollBar(Orientation.HORIZONTAL)
				.onChanged(evt -> verLabel.setText(String.format("Hor: %3.2f", evt.getNewValue()))));
		scrollbarPanel.setPosition(500, 400);

		// Build the screen
		screen.showElement(sliders);
		screen.showElement(dialsFrame);
		screen.showElement(scrollbarPanel);

	}

}
