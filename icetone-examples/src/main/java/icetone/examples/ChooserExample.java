package icetone.examples;

import java.io.File;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;

import icetone.controls.buttons.CheckBox;
import icetone.controls.buttons.PushButton;
import icetone.controls.containers.Frame;
import icetone.controls.text.Label;
import icetone.core.Element;
import icetone.core.ElementContainer;
import icetone.core.Screen;
import icetone.core.layout.mig.MigLayout;
import icetone.extras.chooser.ChooserDialog;
import icetone.extras.chooser.ColorFieldControl;
import icetone.extras.chooser.FileChooserModel;

/**
 * This example shows some examples of usage of a {@link ChooserDialog}
 */
public class ChooserExample extends SimpleApplication {

	public static void main(String[] args) {
		ChooserExample app = new ChooserExample();
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

		/* A frame to show results of thing chooser events */
		Frame thingFrame = new Frame();
		Label selectedThing = new Label();
		Label currentThing = new Label();
		PushButton openThingChooser = new PushButton("Open Thing Chooser");
		Element thingChooser = thingFrame.getContentArea();
		thingChooser.setLayoutManager(new MigLayout("", "[][:128:][][:128:][]"));
		thingChooser.addElement(new Label("Selected:"));
		thingChooser.addElement(selectedThing, "growx");
		thingChooser.addElement(new Label("Current:"));
		thingChooser.addElement(currentThing, "growx");
		thingChooser.addElement(openThingChooser);

		/* The chooser itself */
		ChooserDialog<File> thingChooserDialog = new ChooserDialog<File>("Chooser Example", new FileChooserModel());
		thingChooserDialog.onChange(evt -> {
			File newValue = evt.getNewValue();
			if (evt.isTemporary())
				/* Temporary event when item is clicked */
				currentThing.setText(newValue == null ? null : newValue.getName());
			else {
				/* Permanent event when item is double clicked */
				selectedThing.setText(newValue == null ? null : newValue.getName());
				thingChooserDialog.hide();
				openThingChooser.setEnabled(true);
			}
		});
		thingChooserDialog.setPosition(200, 200);
		openThingChooser.onMouseReleased(evt -> {
			thingChooserDialog.show();
			openThingChooser.setEnabled(false);
		});

		/*
		 * We want to re-use the thing chooser, so make sure it isn't destroyed
		 * when it's hidden
		 */
		thingChooserDialog.setDestroyOnHide(false);

		/*
		 * A frame to show the colour field control. This itself uses a colour
		 * chooser dialog (that you can use separately if you want)
		 */
		Frame colourChooserFrame = new Frame();
		colourChooserFrame.setPosition(300, 300);
		ColorFieldControl colour = new ColorFieldControl(ColorRGBA.Red);
		Element content = colourChooserFrame.getContentArea();
		content.addElement(colour);
		content.addElement(new CheckBox("Include Alpha").onChange((evt) -> {
			colour.setIncludeAlpha(evt.getNewValue());
		}));
		content.addElement(new CheckBox("Show Hex In Chooser").onChange((evt) -> {
			colour.setShowHexInChooser(evt.getNewValue());
		}));

		/* Build screen */
		screen.showElement(thingFrame);
		screen.showElement(colourChooserFrame);
		screen.attachElement(thingChooserDialog);

	}

}
