package icetone.examples;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;

import org.lwjgl.opengl.Display;

import com.jme3.app.LegacyApplication;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetNotFoundException;
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
import icetone.controls.containers.Frame;
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
import icetone.core.ToolKit;
import icetone.core.layout.ScreenLayout;
import icetone.core.layout.ScreenLayoutConstraints;
import icetone.core.layout.WrappingLayout;
import icetone.core.layout.mig.MigLayout;
import icetone.css.Theme;
import icetone.extras.appstates.PopupMessageAppState;
import icetone.extras.appstates.PopupMessageAppState.Channel;
import icetone.extras.debug.GUIExplorerAppState;
import icetone.extras.util.ExtrasUtil;
import icetone.fontawesome.FontAwesome;
import icetone.text.Font;
import icetone.xhtml.XHTMLDisplay;

/**
 * This is not an example, it is a front-end for all the other examples :)
 */
public class ExampleRunner extends SimpleApplication {

	private final static Preferences PREFS = Preferences.userRoot().node("IcetoneExamples");

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
			new Example(InformationExample.class,
					"Shows different ways to show information such as tooltips and popup messages", true,
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
			new Example(DnDExample.class, "A simple game of tic-tac-toe implemented using drag and drop.", true,
					FontAwesome.GAMEPAD),
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
		 * We are only using a single screen, so just initialise it (and you don't need
		 * to provide the screen instance to each control)
		 */
		Screen screen = new Screen(this);
		String defaultTheme = PREFS.get("defaultTheme", "");
		if (defaultTheme.length() > 0)
			ToolKit.get().getStyleManager().setTheme(defaultTheme);

		/* Example workspace */
		StyledContainer exampleWorkspace = new StyledContainer();
		exampleWorkspace.setLayoutManager(new ScreenLayout() {
			/*
			 * The examples are not actually in a screen, although we want similar layout.
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
		examples.getScrollableArea().setIndent(4);
		examples.getScrollableArea().setMargin(4, 4, 4, 4);

		WrappingLayout wrapLayout = examples.getScrollContentLayout();
		wrapLayout.setOrientation(Orientation.HORIZONTAL);
		wrapLayout.setFill(true);

		for (Example ex : EXAMPLES) {
			examples.addScrollableContent(ex.icon.button(32, new PushButton(ex.clazz.getSimpleName()))
					.onMouseReleased(evt -> runExample(ex, exampleWorkspace)), "growx");
			examples.addScrollableContent(new Label(ex.description).setTextWrap(LineWrapMode.Word), "growx");
			examples.addScrollableContent(new Separator(Orientation.HORIZONTAL), "growx");
		}

		/* Tools */
		ScrollPanel tools = new ScrollPanel();
		wrapLayout = tools.getScrollContentLayout();
		wrapLayout.setOrientation(Orientation.HORIZONTAL);
		wrapLayout.setFill(true);
		tools.addScrollableContent(
				new Label("GUIExplorerAppState> may be attached to your application to help debug your UI. It "
						+ "is also useful for designing skins. See the Help menu for " + "more information.")
								.setTextWrap(LineWrapMode.Word),
				"growx");
		tools.addScrollableContent(new PushButton("Open/Close").onMouseReleased(evt -> {
			GUIExplorerAppState.toggle(ExampleRunner.this);
		}), "growx");
		tools.addScrollableContent(new Separator(Orientation.HORIZONTAL), "growx");

		/* Themes */
		ScrollPanel themes = new ScrollPanel();
		themes.getScrollableArea().setIndent(4);
		themes.getScrollableArea().setMargin(4, 4, 4, 4);
		wrapLayout = themes.getScrollContentLayout();
		wrapLayout.setOrientation(Orientation.HORIZONTAL);
		wrapLayout.setFill(true);
		for (Theme t : ToolKit.get().getStyleManager().getThemes()) {
			if (!t.isPseudo()) {
				if (!themes.getScrollableArea().getElements().isEmpty())
					themes.addScrollableContent(new Separator(Orientation.HORIZONTAL), "growx");
				themes.addScrollableContent(new ThemePanel(t));
			}
		}

		/* Tabs */
		TabControl tabs = new TabControl();
		tabs.addTab("Examples", examples);
		tabs.addTab("Themes", themes);
		tabs.addTab("Tools", tools);

		/*
		 * Create a split. The left contains the example output, the right contains the
		 * example menu, the current example source and other stuff
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

			String resourcePath = ex.clazz.getName().replace('.', File.separatorChar) + ".html";
			File file = new File(new File(new File(new File("target"), "site"), "xref"), resourcePath);
			URL loc = null;
			/* Look for file. 'mvn site' must have been run */
			if (file.exists()) {
				loc = file.toURI().toURL();
			} else {
				/* Look for classpath resource */
				loc = ex.clazz.getResource("/" + resourcePath);
			}
			final URL floc = loc;

			if (loc != null) {
				PushButton viewSource = new PushButton("View Source");
				viewSource.setPosition(10, 10);
				viewSource.setAlwaysOnTop(true);
				viewSource.onMousePressed(evt -> {
					Frame frame = new Frame(resourcePath);
					frame.setCloseable(true);
					XHTMLDisplay l = new XHTMLDisplay();
					frame.getContentArea().addElement(l);
					try (InputStream in = floc.openStream()) {
						l.setDocument(in, floc.toString());
					} catch (Exception e) {
					}
					exampleWorkspace.showElement(frame, ScreenLayoutConstraints.fill);
				});
				exampleWorkspace.showElement(viewSource, ScreenLayoutConstraints.preferred);
			}

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

	class ThemePanel extends Element {
		ThemePanel(Theme theme) {
			setLayoutManager(new MigLayout("fill, wrap 2", "[][grow]"));
			try {
				String path = ExtrasUtil.getDirname(theme.getPath()) + "/" + theme.getName() + ".png";
				addElement(
						new Element(path).setBackgroundDimensions(Size.FILL).setPreferredDimensions(new Size(128, 96)),
						"span 2, ax 50%");
			} catch (AssetNotFoundException anfe) {
			}
			addElement(new Label("Name:"));
			addElement(Font.bold(new Label(theme.getName())).setTextWrap(LineWrapMode.Word), "growx");
			addElement(new Label("Author:"));
			addElement(Font.bold(new Label(theme.getAuthor())).setTextWrap(LineWrapMode.Word), "growx");
			addElement(Font.bold(new Label(theme.getDescription())).setTextWrap(LineWrapMode.Word), "span 2, growx");
			addElement(new PushButton("Activate").onMouseReleased((evt) -> {
				ToolKit.get().getStyleManager().setTheme(theme);
				PREFS.put("defaultTheme", theme.getName());
			}), "span 2, ax 50%");
		}
	}

}
