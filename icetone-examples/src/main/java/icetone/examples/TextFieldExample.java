package icetone.examples;

import com.jme3.app.SimpleApplication;

import icetone.controls.containers.Frame;
import icetone.controls.lists.ComboBox;
import icetone.controls.lists.FloatRangeSpinnerModel;
import icetone.controls.lists.IntegerRangeSpinnerModel;
import icetone.controls.lists.Spinner;
import icetone.controls.scrolling.ScrollPanel;
import icetone.controls.text.AutocompleteTextField;
import icetone.controls.text.Label;
import icetone.controls.text.Password;
import icetone.controls.text.TextArea;
import icetone.controls.text.TextField;
import icetone.core.Element;
import icetone.core.ElementContainer;
import icetone.core.Measurement.Unit;
import icetone.core.Screen;
import icetone.core.Size;
import icetone.core.layout.mig.MigLayout;
import icetone.extras.debug.GUIExplorerAppState;

/**
 * This example shows some examples of usage of the {@link TextField} and its
 * relations.
 */
public class TextFieldExample extends SimpleApplication {

	public static void main(String[] args) {
		TextFieldExample app = new TextFieldExample();
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
		getStateManager().attach(new GUIExplorerAppState());
	}

	protected void buildExample(ElementContainer<?, ?> screen) {

		// Frame
		Frame frame1 = new Frame();
		frame1.setTitle("TextField");
		frame1.setMovable(true);
		frame1.setResizable(true);
		Element contentArea = frame1.getContentArea();
		contentArea.setLayoutManager(new MigLayout("wrap 2", "[][:200:]"));

		// /* Text Field */
		contentArea.addElement(new Label("A simple text field"));
		contentArea.addElement(new TextField(""), "growx");

		/* Text Field forcing upper case */
		contentArea.addElement(new Label("Force upper case"));
		contentArea.addElement(new TextField("").setForceUpperCase(true), "growx");

		/* Text Field forcing lower case */
		contentArea.addElement(new Label("Force lower case"));
		contentArea.addElement(new TextField("").setForceLowerCase(true), "growx");

		/* Password Field */
		contentArea.addElement(new Label("Password"));
		contentArea.addElement(new Password(), "growx");

		/* Editable ComboBox */
		contentArea.addElement(new Label("Editable ComboBox"));
		contentArea.addElement(new ComboBox<String>("Option 1", "Option 2", "Option 3", "Or type in the text field"),
				"growx");

		/* Non-Editable ComboBox */
		contentArea.addElement(new Label("Non-Editable ComboBox"));
		contentArea.addElement(new ComboBox<String>("Option 1", "Option 2", "Option 3",
				"A Very very long option that will affect the combobox preferred size of dropdown menu")
						.setEditable(false),
				"growx");

		/* Editable Spinner */
		contentArea.addElement(new Label("Editable Float Spinner"));
		contentArea.addElement(new Spinner<Float>().setSpinnerModel(new FloatRangeSpinnerModel(0, 180, 1f, 0))
				.setFormatterString("%3.0f"), "growx");

		/* Non-Editable Spinner */
		contentArea.addElement(new Label("Non-Editable Integer Spinner"));
		contentArea.addElement(
				new Spinner<Integer>().setSpinnerModel(new IntegerRangeSpinnerModel(0, 9, 1, 0)).setEditable(false));

		/* Autocomplete */
		contentArea.addElement(new Label("Auto-complete (press Ctrl+Space)"));
		contentArea.addElement(new AutocompleteTextField<>("Alpha", "Bravo", "Charlie", "Delta", "Echo", "Foxtrot",
				"Golf", "Hotel", "India", "Juliett", "Lima", "Mike", "November", "Papa", "Quebec", "Romeo", "Sierra",
				"Tango", "Uniform", "Victor", "Whiskey", "Xray", "Yankee"), "growx");

		/* Autocomplete Live */
		contentArea.addElement(new Label("Live Auto-complete"));
		contentArea.addElement(new AutocompleteTextField<>("Alpha", "Bravo", "Charlie", "Delta", "Echo", "Foxtrot",
				"Golf", "Hotel", "India", "Juliett", "Lima", "Mike", "November", "Papa", "Quebec", "Romeo", "Sierra",
				"Tango", "Uniform", "Victor", "Whiskey", "Xray", "Yankee").setLiveHighlight(true), "growx");

		/* Text Area */
		contentArea.addElement(new Label("Text Area 4x20"));
		contentArea.addElement(new TextArea().setRows(4).setMaxRows(4).setCharacterLength(20), "growx");

		/* Text Area 2 */
		contentArea.addElement(new Label("Scrolled Text Area"));
		contentArea.addElement(
				new ScrollPanel(new TextArea()).setPreferredDimensions(new Size(0, 78, Unit.AUTO, Unit.PX)), "growx");

		// Build the screen
		screen.showElement(frame1);

	}

}
