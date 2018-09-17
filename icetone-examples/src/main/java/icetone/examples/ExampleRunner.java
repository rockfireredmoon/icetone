package icetone.examples;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.Display;

import com.jme3.app.LegacyApplication;
import com.jme3.app.SimpleApplication;
import com.jme3.font.LineWrapMode;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Box;

import icetone.controls.buttons.PushButton;
import icetone.controls.containers.SplitPanel;
import icetone.controls.containers.TabControl;
import icetone.controls.extras.Separator;
import icetone.controls.scrolling.ScrollPanel;
import icetone.controls.scrolling.ScrollPanel.ScrollBarMode;
import icetone.controls.text.Label;
import icetone.core.Element;
import icetone.core.ElementContainer;
import icetone.core.Orientation;
import icetone.core.Screen;
import icetone.core.Size;
import icetone.core.StyledContainer;
import icetone.core.layout.ScreenLayout;
import icetone.core.layout.ScreenLayoutConstraints;
import icetone.core.layout.mig.MigLayout;
import icetone.extras.appstates.PopupMessageAppState;
import icetone.extras.appstates.PopupMessageAppState.Channel;
import icetone.extras.debug.GUIExplorerAppState;
import icetone.fontawesome.FontAwesome;

/**
 * This is not an example, it is a front-end for all the other examples :)
 */
public class ExampleRunner extends SimpleApplication {

	static class Example {
		private String description;
		private Class<? extends SimpleApplication> clazz;
		private boolean extras;
		private FontAwesome icon;
		private String[] tips;

		Example(Class<? extends SimpleApplication> clazz, String description, boolean extras, FontAwesome icon,
				String... tips) {
			this.description = description;
			this.clazz = clazz;
			this.extras = extras;
			this.icon = icon;
			this.tips = tips;
		}
	}

	final static List<Example> EXAMPLES = Arrays.asList(
			new Example(ButtonExample.class, "Various types of buttons, including checkboxes and radio buttons", false,
					FontAwesome.CHECK, "Use Tab and Shift+Tab to cycle through focus of top level frame."),
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
					true, FontAwesome.LINK),
			new Example(EffectsExample.class,
					"Demonstrates various special effects including CSS effects and Tonegod Emitter (OGRE compatibilty library) effects.",
					true, FontAwesome.LINK));

	public static void main(String[] args) {
		ExampleRunner app = new ExampleRunner();
		app.start();
	}

	@Override
	public void simpleInitApp() {
		Display.setResizable(true);

		/*
		 * Lets have the traditional blue cube rotating in the background
		 */

		flyCam.setMoveSpeed(10);
		flyCam.setDragToRotate(true);

		Box b = new Box(1, 1, 1);
		Geometry geom = new Geometry("Box", b);

		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom.setMaterial(mat);
		rootNode.attachChild(geom);
		geom.addControl(new AbstractControl() {
			@Override
			protected void controlUpdate(float tpf) {
				getSpatial().rotate(FastMath.QUARTER_PI * tpf, FastMath.QUARTER_PI * tpf, FastMath.QUARTER_PI * tpf);
			}

			@Override
			protected void controlRender(RenderManager rm, ViewPort vp) {
			}
		});

		/*
		 * We are only using a single screen, so just initialise it (and you
		 * don't need to provide the screen instance to each control)
		 */
		Screen screen = new Screen(this);

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

		/* Tools */
		ScrollPanel tools = new ScrollPanel();
		MigLayout toolsScrollAreaLayout = new MigLayout("wrap 1, ins 0, fill", "[]", "[]");
		tools.setScrollContentLayout(toolsScrollAreaLayout);
		tools.addScrollableContent(new Label(
				"<i>GUIExplorerAppState</i> may be " + "attached to your application to help debug your UI. It "
						+ "is also useful for designing skins. See the Help menu for " + "more information.")
								.setTextWrap(LineWrapMode.Word).setParseTextTags(true),
				"growx");
		tools.addScrollableContent(new PushButton("Open/Close").onMouseReleased(evt -> {
			GUIExplorerAppState.toggle(ExampleRunner.this);
		}), "growx");
		tools.addScrollableContent(new Separator(Orientation.HORIZONTAL), "growx");

		/* Tabs */
		TabControl tabs = new TabControl();
		tabs.addTab("Examples", examples);
		tabs.addTab("Tools", tools);

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

		/* Popup messages */
		stateManager.attach(new PopupMessageAppState(screen));

	}

	private void runExample(Example ex, Element exampleWorkspace) {
		try {
			if (ex.tips.length > 0)
				stateManager.getState(PopupMessageAppState.class).message(Channel.INFORMATION,
						ex.tips[(int) (Math.random() * ex.tips.length)]);

			exampleWorkspace.removeAllChildren();

			SimpleApplication aoo = ex.clazz.newInstance();
			aoo.setTimer(getTimer());

			setPrivateAppField("stateManager", getStateManager(), aoo, LegacyApplication.class);
			setPrivateAppField("inputManager", getInputManager(), aoo, LegacyApplication.class);
			setPrivateAppField("assetManager", getAssetManager(), aoo, LegacyApplication.class);
			setPrivateAppField("renderManager", getRenderManager(), aoo, LegacyApplication.class);
			setPrivateAppField("renderer", getRenderer(), aoo, LegacyApplication.class);
			setPrivateAppField("settings", settings, aoo, LegacyApplication.class);
			setPrivateAppField("rootNode", rootNode, aoo, SimpleApplication.class);
			setPrivateAppField("guiNode", guiNode, aoo, SimpleApplication.class);
			setPrivateAppField("flyCam", flyCam, aoo, SimpleApplication.class);
			setPrivateAppField("guiFont", guiFont, aoo, SimpleApplication.class);
			setPrivateAppField("fpsText", fpsText, aoo, SimpleApplication.class);

			Method m = ex.clazz.getDeclaredMethod("buildExample", ElementContainer.class);
			m.setAccessible(true);
			m.invoke(aoo, exampleWorkspace);

			// URL url = ex.clazz.getResource(ex.clazz.getSimpleName() +
			// ".xhtml");
			// if (url == null)
			// label.setText("No source for " + ex.clazz.getSimpleName());
			// else {
			// label.setDocument(url.openStream(), url.toString());
			// }

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void setPrivateAppField(String name, Object val, SimpleApplication aoo, Class<?> clazz)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field f = clazz.getDeclaredField(name);
		f.setAccessible(true);
		f.set(aoo, val);
	}

}
