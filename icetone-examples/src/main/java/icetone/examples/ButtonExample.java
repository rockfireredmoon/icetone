package icetone.examples;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapFont.Align;
import com.jme3.input.KeyInput;

import icetone.controls.buttons.Button;
import icetone.controls.buttons.ButtonGroup;
import icetone.controls.buttons.CheckBox;
import icetone.controls.buttons.PushButton;
import icetone.controls.buttons.RadioButton;
import icetone.controls.buttons.ToggleButton;
import icetone.controls.containers.Frame;
import icetone.controls.lists.ComboBox;
import icetone.controls.text.Label;
import icetone.controls.text.TextField;
import icetone.core.BaseElement;
import icetone.core.ElementContainer;
import icetone.core.Screen;
import icetone.core.Size;
import icetone.core.StyledContainer;
import icetone.core.layout.Border;
import icetone.core.layout.BorderLayout;
import icetone.core.layout.FlowLayout;
import icetone.core.layout.mig.MigLayout;
import icetone.extras.debug.GUIExplorerAppState;
import icetone.fontawesome.FontAwesome;

/**
 * This example shows some examples of usage of various different types of
 * {@link PushButton}
 */
public class ButtonExample extends SimpleApplication {

	public static void main(String[] args) {
		ButtonExample app = new ButtonExample();
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
		
		stateManager.attach(new GUIExplorerAppState());

	}

	protected void buildExample(ElementContainer<?, ?> container) {
		/* Create 3 different styles of button */

		container.showElement(
				createWindow("Button", true).setPosition(20, 20));
		container.showElement(
				createWindow("CheckBox", false).setPosition(430, 20));
		container.showElement(
				createWindow("RadioButton", false).setPosition(430, 330));

	}

	protected Frame createWindow(String type, boolean allowIcons) {
		/* Some options to change how the buttons look */
		CheckBox fill = new CheckBox();
		fill.setChecked(true);
		fill.setText("Fill");

		/* Show some example icons */
		CheckBox icons = new CheckBox();
		icons.setChecked(allowIcons);
		icons.setEnabled(allowIcons);
		icons.setText("Icons");

		/* Icon alignment */
		ComboBox<Align> iconAlign = new ComboBox<Align>();
		iconAlign.setEditable(false);
		for (BitmapFont.Align a : BitmapFont.Align.values()) {
			iconAlign.addComboItem(a.name(), a);
		}
		iconAlign.setSelectedByValue(Align.Left);

		/* Style **/
		TextField style = new TextField();
		style.setCharacterLength(10);

		/* Tools */
		StyledContainer tools = new StyledContainer();
		tools.setLayoutManager(new FlowLayout(8));
		tools.addElement(fill);
		tools.addElement(icons);
		tools.addElement(iconAlign);
		tools.addElement(new Label("Style"));
		tools.addElement(style);

		/* Container for buttons themselves */
		StyledContainer buttons = new StyledContainer();

		// Frame
		Frame frame1 = new Frame();
		frame1.setResizable(true);
		frame1.setTitle(type);
		frame1.getContentArea().setLayoutManager(new BorderLayout());
		frame1.getContentArea().addElement(buttons, Border.CENTER);
		frame1.getContentArea().addElement(tools, Border.SOUTH);
		rebuildButtons(frame1, buttons, type, fill.isChecked(), icons.isChecked(), iconAlign.getSelectedValue(),
				style.getText());

		/* Bind to control change events */
		iconAlign.onChange(evt -> rebuildButtons(frame1, buttons, type, fill.isChecked(), icons.isChecked(),
				iconAlign.getSelectedValue(), style.getText()));

		fill.onChange(evt -> rebuildButtons(frame1, buttons, type, fill.isChecked(), icons.isChecked(),
				iconAlign.getSelectedValue(), style.getText()));

		icons.onChange(evt -> rebuildButtons(frame1, buttons, type, fill.isChecked(), icons.isChecked(),
				iconAlign.getSelectedValue(), style.getText()));

		style.onKeyboardReleased(evt -> {
			if (evt.getKeyCode() == KeyInput.KEY_RETURN)
				rebuildButtons(frame1, buttons, type, fill.isChecked(), icons.isChecked(), iconAlign.getSelectedValue(),
						style.getText());

		});

		return frame1;
	}

	private void rebuildButtons(Frame frame, StyledContainer buttons, String type, boolean fill, boolean icons,
			Align iconAlign, String style) {

		/* Create a group group for one of the example types */
		ButtonGroup<Button> buttonGroup = new ButtonGroup<Button>();

		/* Rebuild the buttons */
		buttons.removeAllChildren();
		buttons.setLayoutManager(
				new MigLayout("wrap 3, fill", fill ? "[fill,grow][fill,grow][fill,grow]" : "[][][]", "[]"));
		Label l1;
		for (String s : new String[] { "Normal", "Toggle", "Grouped" }) {

			if (!type.equals("Button") && s.equals("Toggle")) {
				/* Not applicable */
				continue;
			}

			l1 = new Label(s);
			buttons.addElement(l1, "span 3");
			createAndAddButton(buttons, type, s, "LeftTop", BitmapFont.Align.Left, BitmapFont.VAlign.Top, iconAlign,
					fill, icons, buttonGroup, style);
			createAndAddButton(buttons, type, s, "LeftCenter", BitmapFont.Align.Left, BitmapFont.VAlign.Center,
					iconAlign, fill, icons, buttonGroup, style);
			createAndAddButton(buttons, type, s, "LeftBottom", BitmapFont.Align.Left, BitmapFont.VAlign.Bottom,
					iconAlign, fill, icons, buttonGroup, style);
			createAndAddButton(buttons, type, s, "CenterTop", BitmapFont.Align.Center, BitmapFont.VAlign.Top, iconAlign,
					fill, icons, buttonGroup, style);
			createAndAddButton(buttons, type, s, "CenterCenter", BitmapFont.Align.Center, BitmapFont.VAlign.Center,
					iconAlign, fill, icons, buttonGroup, style);
			createAndAddButton(buttons, type, s, "CenterBottom", BitmapFont.Align.Center, BitmapFont.VAlign.Bottom,
					iconAlign, fill, icons, buttonGroup, style);
			createAndAddButton(buttons, type, s, "RightTop", BitmapFont.Align.Right, BitmapFont.VAlign.Top, iconAlign,
					fill, icons, buttonGroup, style);
			createAndAddButton(buttons, type, s, "RightCenter", BitmapFont.Align.Right, BitmapFont.VAlign.Center,
					iconAlign, fill, icons, buttonGroup, style);
			createAndAddButton(buttons, type, s, "RightBottom", BitmapFont.Align.Right, BitmapFont.VAlign.Bottom,
					iconAlign, fill, icons, buttonGroup, style);
		}

		/* Resize the frame */
		frame.sizeToContent();
	}

	private void createAndAddButton(BaseElement p, String type, String control, String text, BitmapFont.Align h,
			BitmapFont.VAlign v, Align iconAlign, boolean fill, boolean icons, ButtonGroup<Button> buttonGroup,
			String styleClass) {
		Button b1 = null;

		/* Create appropriate button type */
		if (type.equals("Button")) {
			/* Ordinary buttons */
			if (control.equals("Toggle"))
				b1 = new ToggleButton();
			else
				b1 = new PushButton();
		} else if (type.equals("CheckBox")) {
			/* Check Box buttons */
			if (control.equals("Normal") || control.equals("Grouped"))
				b1 = new CheckBox();
		} else if (type.equals("RadioButton")) {
			/* Radio buttons */
			if (control.equals("Normal") || control.equals("Grouped"))
				b1 = new RadioButton<>();
		}

		/* If grouped, add to group */
		if (control.equals("Grouped"))
			buttonGroup.addButton(b1);

		b1.setStyleClass(styleClass);
		b1.setText(text);
		b1.setTextAlign(h);
		b1.setTextVAlign(v);
		if (icons)
			FontAwesome.values()[(int) (Math.random() * FontAwesome.values().length)].button(24, b1);
		else
			FontAwesome.clear(b1.getButtonIcon());
		b1.setButtonIconAlign(iconAlign);
		p.addElement(b1, fill ? "growx, growy" : "");
	}

}
