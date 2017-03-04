package icetone.examples;

import org.lwjgl.opengl.Display;

import com.jme3.app.SimpleApplication;
import com.jme3.font.LineWrapMode;

import icetone.controls.containers.Frame;
import icetone.controls.lists.SelectList;
import icetone.controls.lists.SelectList.SelectionMode;
import icetone.controls.menuing.Menu;
import icetone.controls.menuing.MenuBar;
import icetone.controls.text.Label;
import icetone.core.ElementContainer;
import icetone.core.BaseScreen;
import icetone.core.Size;
import icetone.core.layout.Border;
import icetone.core.layout.BorderLayout;

/**
 * This example shows some examples of usage of the various types of menu such
 * as {@link MenuBar}, {@link Menu} and {@link SelectList}
 */
public class MenuExamples extends SimpleApplication {

	public static void main(String[] args) {
		MenuExamples app = new MenuExamples();
		app.start();
	}

	@Override
	public void simpleInitApp() {
		Display.setResizable(true);
		/*
		 * We are only using a single screen, so just initialise it (and you
		 * don't need to provide the screen instance to each control).
		 * 
		 * It is passed to the buildExample method in this way to help
		 * ExampleRunner so this example can be run from there and as a
		 * standalone JME application
		 */
		buildExample(BaseScreen.init(this));

	}

	protected void buildExample(ElementContainer<?, ?> container) {

		/*
		 * Menu Bar
		 */
		MenuBar menuBar = new MenuBar();
		menuBar.addMenu(new Menu<String>("File").addMenuItem("Open")
				.addMenuItem(new Menu<String>("Open Recent").addMenuItem("File 1").addMenuItem("File 2")
						.addMenuItem("File 3"))
				.addMenuItem("Save").addMenuItem("Save As").addSeparator().addMenuItem("Close"));
		menuBar.addMenu(new Menu<String>("Edit").addMenuItem("Cut").addMenuItem("Copy").addMenuItem("Paste"));
		menuBar.addMenu(
				new Menu<String>("View").addMenuItem("Full Screen").addMenuItem("Properties").addMenuItem("Details"));
		menuBar.addMenu(new Menu<String>("Help").addMenuItem("Contents").addMenuItem("Web").addMenuItem("About"));
		container.addElement(menuBar);

		/* Frame with a single selection list */
		Frame frame1 = new Frame();
		frame1.setResizable(true);
		frame1.setTitle("SelectList 1");
		frame1.setContentLayoutManager(new BorderLayout());
		frame1.getContentArea().addElement(populateList(new SelectList<String>()), Border.CENTER);
		frame1.getContentArea().addElement(
				new Label("Single select list, click to select").setTextWrap(LineWrapMode.Word), Border.SOUTH);
		frame1.setPreferredDimensions(new Size(160, 240));
		frame1.setPosition(0, 32);

		/* Frame with a multiple selection list */
		Frame frame2 = new Frame();
		frame2.setResizable(true);
		frame2.setTitle("SelectList 2");
		frame2.setContentLayoutManager(new BorderLayout());
		frame2.getContentArea().addElement(
				populateList(new SelectList<String>().setSelectionMode(SelectionMode.MULTIPLE)), Border.CENTER);
		frame2.getContentArea().addElement(
				new Label("Multiple select list, Ctrl+click to select").setTextWrap(LineWrapMode.Word), Border.SOUTH);
		frame2.setPreferredDimensions(new Size(160, 240));
		frame2.setPosition(200, 32);

		/* Frame with a toggle selection list */
		Frame frame3 = new Frame();
		frame3.setResizable(true);
		frame3.setTitle("SelectList 3");
		frame3.setContentLayoutManager(new BorderLayout());
		frame3.getContentArea().addElement(
				populateList(new SelectList<String>().setSelectionMode(SelectionMode.TOGGLE)), Border.CENTER);
		frame3.getContentArea().addElement(
				new Label("Toggle select list, Click to toggle").setTextWrap(LineWrapMode.Word), Border.SOUTH);
		frame3.setPreferredDimensions(new Size(160, 240));
		frame3.setPosition(400, 32);

		// Build the screen
		container.showElement(frame1);
		container.showElement(frame2);
		container.showElement(frame3);

	}

	private SelectList<String> populateList(SelectList<String> list) {
		for (int i = 0; i < 10; i++) {
			list.addListItem(String.valueOf(i));
		}
		return list;
	}

}
