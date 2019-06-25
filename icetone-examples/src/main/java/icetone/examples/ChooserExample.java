package icetone.examples;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;

import icetone.controls.buttons.CheckBox;
import icetone.controls.buttons.PushButton;
import icetone.controls.containers.Frame;
import icetone.controls.lists.ComboBox;
import icetone.controls.text.Label;
import icetone.core.Element;
import icetone.core.ElementContainer;
import icetone.core.Screen;
import icetone.core.layout.mig.MigLayout;
import icetone.extras.chooser.ChooserDialog;
import icetone.extras.chooser.ChooserSelectionMode;
import icetone.extras.chooser.ColorButton;
import icetone.extras.chooser.ColorField;
import icetone.extras.chooser.ColorRestrictionType;
import icetone.extras.chooser.ColorTab;
import icetone.extras.chooser.FileChooserModel;
import icetone.text.Font;

/**
 * This example shows some examples of usage of a {@link ChooserDialog}
 */
public class ChooserExample extends SimpleApplication {

	private final static String[] FURS_PALETTE = { "d30e15", "e76b6b", "d8bdc2", "a00c0c", "a44f48", "9f7f82", "6e0107",
			"6e3031", "7a525a", "c88743", "c7a673", "d3ccc4", "9c6c2e", "9c865f", "a8a294", "6a4e1c", "776245",
			"75675a", "f3e300", "e9e164", "e7e5b2", "b1a305", "afa947", "aeab88", "645d05", "6f6728", "726e3e",
			"09bc08", "5de88b", "aee7be", "09a30f", "4c975c", "779387", "065100", "1e5f37", "365f51", "00e1d6",
			"63ede0", "c3e3e2", "009492", "3a9391", "659391", "054b49", "284b44", "3b5450", "006ae2", "66a2e8",
			"bcd0d9", "034aa4", "3c729e", "79889d", "003363", "273e67", "495b73", "753f7d", "b886cb", "ccc2da",
			"5e3182", "83648d", "908c9b", "451f5a", "54455c", "6d626a", "641952", "d37cc1", "d8c1d5", "952176",
			"a0628b", "9d8794", "611a54", "693e5c", "746061" };

	private final static String[] RAINBOW_PALETTE = { "d60e0e", "e06e6e", "dcbdbd", "c58b31", "c5a572", "d5ccbf",
			"f5e300", "e8e06a", "e8e5b4", "0aba0e", "6be28a", "b4e3c0", "a10a0a", "a14f4f", "9d7f7f", "9b6d27",
			"9c8560", "a9a194", "b8a800", "aea852", "aeab87", "07a40b", "459c57", "74977b", "6d0707", "6c3131",
			"775454", "6f4d1b", "786241", "766857", "665d02", "6e6824", "726e3f", "035105", "235e30", "3f5d47",
			"00dfd7", "64ebe8", "c2e6e5", "006adc", "62a3e6", "bdcfe0", "853fab", "b28ac7", "cfc4d5", "be2d97",
			"d380bd", "d9c3d3", "02928c", "3b9391", "689190", "004ba1", "416fa0", "78899a", "632f81", "816491",
			"938898", "912373", "9a5e8a", "9c8696", "014d49", "214d4a", "385554", "002e66", "204265", "485b70",
			"462259", "564261", "6b5e71", "641a50", "693e5d", "765d6f", "333333", "4a4a4a", "616161", "787878",
			"8f8f8f", "a6a6a6", "bdbdbd", "d4d4d4", "ebebeb" };

	public static void main(String[] args) {
		ChooserExample app = new ChooserExample();
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

		/* A frame to show results of thing chooser events */
		Frame thingFrame = new Frame();
		thingFrame.setTitle("Thing Chooser");
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
		 * We want to re-use the thing chooser, so make sure it isn't destroyed when
		 * it's hidden
		 */
		thingChooserDialog.setDestroyOnHide(false);

		/* Palette */
		List<ColorRGBA> pal = new ArrayList<>();
		for (String rgbHex : RAINBOW_PALETTE) {
			pal.add(new ColorRGBA((float) Integer.valueOf(rgbHex.substring(0, 2), 16) / 255f,
					(float) Integer.valueOf(rgbHex.substring(2, 4), 16) / 255f,
					(float) Integer.valueOf(rgbHex.substring(4, 6), 16) / 255f, 1f));
		}

		/*
		 * A frame to show the colour field control. This itself uses a colour chooser
		 * dialog (that you can use separately if you want)
		 */
		Frame colourChooserFrame = new Frame();
		colourChooserFrame.setTitle("Colour Chooser");
		colourChooserFrame.setPosition(200, 200);

		ColorField colourField = new ColorField(ColorRGBA.Red);
		colourField.setPalette(pal);
		ColorButton colourButton = new ColorButton(ColorRGBA.Red);
		colourButton.setPalette(pal);

		colourField.onChange(evt -> colourButton.setValue(evt.getNewValue()));
		colourButton.onChange(evt -> colourField.setValue(evt.getNewValue()));

		Element content = colourChooserFrame.getContentArea();
		content.setLayoutManager(new MigLayout("wrap 2, fill", "[][]", "[][][][]"));

		content.addElement(Font.bold(new Label("Components")), "span 2");

		// Components
		content.addElement(new Label("ColorButton"), "al 50%, gapleft 10");
		content.addElement(new Label("ColorField"), "al 50%, gapleft 10");

		content.addElement(colourButton, "al 50%, gapleft 10");
		content.addElement(colourField, "al 50%, gapleft 10");

		// Tabs
		content.addElement(Font.bold(new Label("Tabs")), "span 2");
		List<ColorTab> tabs = new ArrayList<ColorTab>(Arrays.asList(ColorTab.RGB));
		content.addElement(new CheckBox("RGB").setChecked(true).onChange((evt) -> {
			if (evt.getNewValue())
				tabs.add(ColorTab.RGB);
			else
				tabs.remove(ColorTab.RGB);
			colourField.setTabs(tabs.toArray(new ColorTab[0]));
			colourButton.setTabs(tabs.toArray(new ColorTab[0]));
		}), "span 2, gapleft 10");
		content.addElement(new CheckBox("Wheel").onChange((evt) -> {
			if (evt.getNewValue())
				tabs.add(ColorTab.WHEEL);
			else
				tabs.remove(ColorTab.WHEEL);
			colourField.setTabs(tabs.toArray(new ColorTab[0]));
			colourButton.setTabs(tabs.toArray(new ColorTab[0]));
		}), "span 2, gapleft 10");
		content.addElement(new CheckBox("Palette").onChange((evt) -> {
			if (evt.getNewValue())
				tabs.add(ColorTab.PALETTE);
			else
				tabs.remove(ColorTab.PALETTE);
			colourField.setTabs(tabs.toArray(new ColorTab[0]));
			colourButton.setTabs(tabs.toArray(new ColorTab[0]));
		}), "span 2, gapleft 10");

		// Options
		content.addElement(Font.bold(new Label("Options")), "span 2");
		content.addElement(new CheckBox("Include Alpha").onChange((evt) -> {
			colourField.setIncludeAlpha(evt.getNewValue());
			colourButton.setIncludeAlpha(evt.getNewValue());
		}), "span 2, gapleft 10");
		content.addElement(new CheckBox("Modal Chooser").onChange((evt) -> {
			colourField.setModalChooser(evt.getNewValue());
			colourButton.setModalChooser(evt.getNewValue());
		}), "span 2, gapleft 10");
		content.addElement(new CheckBox("Show Hex In Chooser").onChange((evt) -> {
			colourField.setShowHexInChooser(evt.getNewValue());
			colourButton.setShowHexInChooser(evt.getNewValue());
		}), "span 2, gapleft 10");
		content.addElement(new CheckBox("Allow Unset").onChange((evt) -> {
			colourField.setAllowUnset(evt.getNewValue());
			colourButton.setAllowUnset(evt.getNewValue());
		}), "span 2, gapleft 10");

		content.addElement(new Label("Color Restriction:"));
		content.addElement(new ComboBox<>(ColorRestrictionType.values()).onChange((evt) -> {
			colourField.setRestrictionType(evt.getNewValue());
			colourButton.setRestrictionType(evt.getNewValue());
		}), "span 2, gapleft 10");

		content.addElement(new Label("Selection Mode:"));
		content.addElement(new ComboBox<>(ChooserSelectionMode.values()).onChange((evt) -> {
			colourField.setSelectionMode(evt.getNewValue());
			colourButton.setSelectionMode(evt.getNewValue());
		}), "span 2, gapleft 10");

		/* Build screen */
		screen.showElement(thingFrame);
		screen.showElement(colourChooserFrame);
		screen.attachElement(thingChooserDialog);

	}

}
