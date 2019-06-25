package icetone.examples;

import com.jme3.app.SimpleApplication;

import icetone.controls.containers.Frame;
import icetone.controls.lists.Dial;
import icetone.controls.lists.FloatRangeSliderModel;
import icetone.controls.lists.FloatRangeSpinnerModel;
import icetone.controls.lists.IntegerRangeSpinnerModel;
import icetone.controls.lists.Slider;
import icetone.controls.lists.Spinner;
import icetone.controls.lists.StringRangeSpinnerModel;
import icetone.controls.scrolling.ScrollBar;
import icetone.controls.text.Label;
import icetone.core.BaseElement;
import icetone.core.Element;
import icetone.core.ElementContainer;
import icetone.core.Orientation;
import icetone.core.Screen;
import icetone.core.Size;
import icetone.core.StyledContainer;
import icetone.core.layout.Border;
import icetone.core.layout.BorderLayout;
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
		 * We are only using a single screen, so just initialise it (and you don't need
		 * to provide the screen instance to each control).
		 * 
		 * It is passed to the buildExample method in this way to help ExampleRunner so
		 * this example can be run from there and as a standalone JME application
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

		final String[] clockNumbers = { "Twelve", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight",
				"Nine", "Ten", "Eleven" };

		Dial<Void> basicDial;
		Dial<String> switchDial;
		Dial<Integer> northDial;
		Dial<Integer> westDial;
		Dial<Integer> eastDial;
		Dial<Integer> southDial;
		Dial<String> clockDial;

		Frame dialsFrame = new Frame();
		dialsFrame.setTitle("Dial and Spinner");
		contentArea = dialsFrame.getContentArea();
		contentArea.setLayoutManager(new MigLayout("fill, wrap 4", "[grow][grow][grow][grow]", "[grow][][]"));
		contentArea.addElement(basicDial = new Dial<Void>()
				.onChange(evt -> evt.getSource().setToolTipText("Vol: " + evt.getSource().getSelectedIndex())));
		contentArea.addElement(switchDial = new Dial.StringListDial("Off", "On")
				.onChange(evt -> evt.getSource().setToolTipText(evt.getSource().getSelectedValue())));

		/*
		 * Restricted dials pointing in four directions
		 */
		Element el = new Element(new BorderLayout());
		el.addElement(
				northDial = new Dial.IntegerRangeDial(1, 10).setGapStartAngle(315).setGapEndAngle(45)
						.onChange(evt -> evt.getSource().setToolTipText("Vol: " + evt.getSource().getSelectedValue())),
				Border.NORTH);
		northDial.setPreferredDimensions(new Size(96, 96));
		el.addElement(
				westDial = new Dial.IntegerRangeDial(1, 10).setGapStartAngle(225).setGapEndAngle(315)
						.onChange(evt -> evt.getSource().setToolTipText("Vol: " + evt.getSource().getSelectedValue())),
				Border.WEST);
		westDial.setPreferredDimensions(new Size(96, 96));
		el.addElement(
				eastDial = new Dial.IntegerRangeDial(1, 10).setGapStartAngle(45).setGapEndAngle(135)
						.onChange(evt -> evt.getSource().setToolTipText("Vol: " + evt.getSource().getSelectedValue())),
				Border.EAST);
		eastDial.setPreferredDimensions(new Size(96, 96));
		el.addElement(
				southDial = new Dial.IntegerRangeDial(1, 10).setGapStartAngle(135).setGapEndAngle(225)
						.onChange(evt -> evt.getSource().setToolTipText("Vol: " + evt.getSource().getSelectedValue())),
				Border.SOUTH);
		southDial.setPreferredDimensions(new Size(96, 96));

		contentArea.addElement(el);
		contentArea.addElement(clockDial = new Dial.StringListDial(clockNumbers).onChange(evt -> evt.getSource()
				.setToolTipText(evt.getSource().getSelectedValue())));

		/* Titles */
		contentArea.addElement(new Label("Normal").setToolTipText("Dial with no fixed range"), "ax 50%");
		contentArea.addElement(new Label("Switch").setToolTipText("Dial with only 2 states, e.g. on/off switch"),
				"ax 50%");
		contentArea.addElement(new Label("Restricted (N,W,E,S)").setToolTipText("Dials restricted to angle range"),
				"ax 50%");
		contentArea.addElement(new Label("Stepped Strings").setToolTipText("String list dial"), "ax 50%");

		/* Linked-spinners */
		Spinner<Integer> basicSpinner;
		Spinner<String> switchSpinner;
		Spinner<String> clockSpinner;
		Spinner<Integer> northSpinner;
		Spinner<Integer> westSpinner;
		Spinner<Integer> eastSpinner;
		Spinner<Integer> southSpinner;
		contentArea.addElement(
				basicSpinner = new Spinner<Integer>(
						new IntegerRangeSpinnerModel().setMax(359).setCurrentValue(basicDial.getSelectedIndex())),
				"ax 50%");
		contentArea.addElement(
				switchSpinner = new Spinner<String>(
						new StringRangeSpinnerModel("Off", "On").setCurrentValue(switchDial.getSelectedValue())),
				"ax 50%");

		Element el2 = new Element(new BorderLayout());
		el2.addElement(
				northSpinner = new Spinner<Integer>(
						new IntegerRangeSpinnerModel(1, 10, 1, 1).setCurrentValue(northDial.getSelectedValue())),
				Border.NORTH);
		el2.addElement(
				westSpinner = new Spinner<Integer>(
						new IntegerRangeSpinnerModel(1, 10, 1, 1).setCurrentValue(westDial.getSelectedValue())),
				Border.WEST);
		el2.addElement(
				eastSpinner = new Spinner<Integer>(
						new IntegerRangeSpinnerModel(1, 10, 1, 1).setCurrentValue(eastDial.getSelectedValue())),
				Border.EAST);
		el2.addElement(
				southSpinner = new Spinner<Integer>(
						new IntegerRangeSpinnerModel(1, 10, 1, 1).setCurrentValue(southDial.getSelectedValue())),
				Border.SOUTH);

		contentArea.addElement(el2, "ax 50%");
		contentArea.addElement(
				clockSpinner = new Spinner<String>(
						new StringRangeSpinnerModel(clockNumbers).setCurrentValue(clockDial.getSelectedValue())),
				"ax 50%");

		/* Link dials to spinners */
		basicDial.onChange(evt -> basicSpinner.setSelectedValue(evt.getSource().getSelectedIndex()));
		basicSpinner.onChange(evt -> basicDial.setSelectedIndex(evt.getNewValue()));
		northDial.onChange(evt -> northSpinner.setSelectedValue(evt.getNewValue()));
		northSpinner.onChange(evt -> northDial.setSelectedValue(evt.getNewValue()));
		westDial.onChange(evt -> westSpinner.setSelectedValue(evt.getNewValue()));
		westSpinner.onChange(evt -> westDial.setSelectedValue(evt.getNewValue()));
		eastDial.onChange(evt -> eastSpinner.setSelectedValue(evt.getNewValue()));
		eastSpinner.onChange(evt -> eastDial.setSelectedValue(evt.getNewValue()));
		southDial.onChange(evt -> southSpinner.setSelectedValue(evt.getNewValue()));
		southSpinner.onChange(evt -> southDial.setSelectedValue(evt.getNewValue()));
		switchDial.onChange(evt -> switchSpinner.setSelectedValue(evt.getNewValue()));
		switchSpinner.onChange(evt -> switchDial.setSelectedValue(evt.getNewValue()));
		clockDial.onChange(evt -> clockSpinner.setSelectedValue(evt.getNewValue()));
		clockSpinner.onChange(evt -> clockDial.setSelectedValue(evt.getNewValue()));

		/* Configure and position frame */
		dialsFrame.setResizable(true);
		dialsFrame.setPosition(10, 400);

		/* Panel */

		StyledContainer content = new StyledContainer();
		content.setPreferredDimensions(new Size(256, 256));
		content.setLayoutManager(new MigLayout("fill, wrap 1"));
		Label verLabel = new Label("Content");
		Label horLabel = new Label("Content");
		content.addElement(verLabel, "growx");
		content.addElement(horLabel, "growx");

		Frame scrollbarPanel = new Frame("ScrollBar");
		scrollbarPanel.getContentArea()
				.setLayoutManager(new MigLayout("wrap 2, ins 0, gap 0, fill", "[fill, grow][]", "[fill, grow][]"));
		scrollbarPanel.getContentArea().addElement(content);
		scrollbarPanel.getContentArea().addElement(new ScrollBar(Orientation.VERTICAL)
				.onChanged(evt -> horLabel.setText(String.format("Ver: %3.2f", evt.getNewValue()))));
		scrollbarPanel.getContentArea().addElement(new ScrollBar(Orientation.HORIZONTAL)
				.onChanged(evt -> verLabel.setText(String.format("Hor: %3.2f", evt.getNewValue()))));
		scrollbarPanel.setPosition(500, 400);

		// Build the screen
		screen.showElement(sliders);
		screen.showElement(dialsFrame);
		screen.showElement(scrollbarPanel);

	}

}
