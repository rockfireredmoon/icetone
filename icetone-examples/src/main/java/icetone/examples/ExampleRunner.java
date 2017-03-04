package icetone.examples;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.Display;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.font.LineWrapMode;
import com.jme3.math.Vector2f;

import icetone.controls.buttons.PushButton;
import icetone.controls.containers.SplitPanel;
import icetone.controls.containers.TabControl;
import icetone.controls.extras.Separator;
import icetone.controls.scrolling.ScrollPanel;
import icetone.controls.scrolling.ScrollPanel.ScrollBarMode;
import icetone.controls.text.Label;
import icetone.core.BaseScreen;
import icetone.core.Element;
import icetone.core.ElementContainer;
import icetone.core.Orientation;
import icetone.core.Size;
import icetone.core.StyledContainer;
import icetone.core.ToolKit;
import icetone.core.layout.ScreenLayout;
import icetone.core.layout.ScreenLayoutConstraints;
import icetone.core.layout.mig.MigLayout;
import icetone.extras.debug.GUIExplorerAppState;
import icetone.fontawesome.FontAwesome;
import icetone.xhtml.XHTMLDisplay;

/**
 * This is not an example, it is a front-end for all the other examples :)
 */
public class ExampleRunner extends SimpleApplication {

	static class Example {
		private String description;
		private Class<? extends SimpleApplication> clazz;
		private boolean extras;
		private FontAwesome icon;

		Example(Class<? extends SimpleApplication> clazz, String description, boolean extras, FontAwesome icon) {
			this.description = description;
			this.clazz = clazz;
			this.extras = extras;
			this.icon = icon;
		}
	}

	final static List<Example> EXAMPLES = Arrays.asList(
			new Example(ButtonExample.class, "Various types of buttons, including checkboxes and radio buttons", false,
					FontAwesome.CHECK),
			new Example(ContainersExample.class, "Demonstrates contains such as frame, slide trays, panels and more",
					false, FontAwesome.WINDOW_RESTORE),
			new Example(ChooserExample.class, "Choosers, such as file choosers, image choosers etc", true,
					FontAwesome.EYEDROPPER),
			new Example(TextFieldExample.class,
					"TextField and its relations, such as editable combobox, spinner and more", true,
					FontAwesome.CARET_RIGHT),
			new Example(TextExample.class, "Shows different ways text can be laid out", true, FontAwesome.TEXT_WIDTH),
			new Example(ToolTipExample.class, "Shows different ways tooltips can be configured", true,
					FontAwesome.GITTIP),
			new Example(MenuExamples.class, "Various types of menus and ways to select from lists", true,
					FontAwesome.BEER),
			new Example(RangesExample.class,
					"Different controls for dealing with ranges of values such as sliders, dials and spinners", true,
					FontAwesome.SLIDERS),
			new Example(TableExample.class, "Shows off the flexible table component", true, FontAwesome.BEER),
			new Example(IndicatorExample.class,
					"Demonstrates various configuration of an 'Indicator' (a progress bar).", true,
					FontAwesome.CLOCK_O),
			new Example(XHTMLExample.class, "The XHTML display component for complext text (based on Flying Saucer).",
					true, FontAwesome.LINK));

	public static void main(String[] args) {
		ExampleRunner app = new ExampleRunner();
		app.start();
	}

	private XHTMLDisplay label;

	@Override
	public void simpleInitApp() {
		Display.setResizable(true);

		/*
		 * We are only using a single screen, so just initialise it (and you
		 * don't need to provide the screen instance to each control)
		 */
		BaseScreen screen = BaseScreen.init(this);

		/* Example workspace */
		StyledContainer exampleWorkspace = new StyledContainer();
		exampleWorkspace.setLayoutManager(new ScreenLayout() {
			/*
			 * The examples are not actually in a screen, although we want
			 * similar layout.
			 */

			@Override
			protected Vector2f calcMinimumSize(ElementContainer<?, ?> parent) {
				return Vector2f.ZERO;
			}

			@Override
			protected Vector2f calcMaximumSize(ElementContainer<?, ?> parent) {
				return Size.MAX_SIZE.toVector2f();
			}
		});

		/* Examples menu */
		ScrollPanel examples = new ScrollPanel();
		examples.setHorizontalScrollBarMode(ScrollBarMode.Never);
		MigLayout scrollAreaLayout = new MigLayout("wrap 1, ins 0, fill", "[]", "[]");
		examples.setScrollContentLayout(scrollAreaLayout);
		for (Example ex : EXAMPLES) {
			examples.addScrollableContent(ex.icon.button(32, new PushButton(ex.clazz.getSimpleName()))
					.onMouseReleased(evt -> runExample(ex, exampleWorkspace)), "growx");
			examples.addScrollableContent(new Label(ex.description).setTextWrap(LineWrapMode.Word), "growx");
			examples.addScrollableContent(new Separator(Orientation.HORIZONTAL), "growx");
		}

		/* Source code */
		label = new XHTMLDisplay();
		StyledContainer sourceContainer = new StyledContainer(new MigLayout("wrap 1, fill", "[]", "[shrink 0][grow]"));
		sourceContainer.addElement(new PushButton("Copy To Clipboard").onMouseReleased(evt -> {
			ToolKit.get().setClipboardText(label.getText());
		}));
		sourceContainer.addElement(label, "growx, growy");

		/* Tabs */
		TabControl tabs = new TabControl();
		tabs.addTab("Examples", examples);
		tabs.addTab("Source", sourceContainer);

		/*
		 * Create a split. The left contains the example output, the right
		 * contains the example menu, the current example source and other stuff
		 */
		SplitPanel split = new SplitPanel(Orientation.HORIZONTAL);
		split.setDefaultDividerLocationRatio(0.75f);
		split.setLeftOrTop(exampleWorkspace);
		split.setRightOrBottom(tabs);

		// Build the screen
		screen.showElement(split, ScreenLayoutConstraints.fill);

		 getStateManager().attach(new GUIExplorerAppState());

	}

	private void runExample(Example ex, Element exampleWorkspace) {
		try {
			exampleWorkspace.removeAllChildren();

			SimpleApplication aoo = ex.clazz.newInstance();
			aoo.setTimer(getTimer());

			Field f = Application.class.getDeclaredField("stateManager");
			f.setAccessible(true);
			f.set(aoo, getStateManager());

			f = Application.class.getDeclaredField("inputManager");
			f.setAccessible(true);
			f.set(aoo, getInputManager());

			Method m = ex.clazz.getDeclaredMethod("buildExample", ElementContainer.class);
			m.setAccessible(true);
			m.invoke(aoo, exampleWorkspace);

			URL url = ex.clazz.getResource(ex.clazz.getSimpleName() + ".xhtml");
			if (url == null)
				label.setText("No source for " + ex.clazz.getSimpleName());
			else {
				label.setDocument(url.openStream(), url.toString());
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
