package icetone.examples;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapFont.Align;
import com.jme3.font.LineWrapMode;

import icetone.controls.buttons.CheckBox;
import icetone.controls.buttons.PushButton;
import icetone.controls.containers.Frame;
import icetone.controls.containers.Panel;
import icetone.controls.containers.SplitPanel;
import icetone.controls.containers.TabControl;
import icetone.controls.containers.Window;
import icetone.controls.lists.ComboBox;
import icetone.controls.lists.SlideTray;
import icetone.controls.lists.SlideTray.ZOrderSort;
import icetone.controls.menuing.Menu;
import icetone.controls.text.Label;
import icetone.core.BaseElement;
import icetone.core.ElementContainer;
import icetone.core.Measurement.Unit;
import icetone.core.Orientation;
import icetone.core.Screen;
import icetone.core.Size;
import icetone.core.layout.Border;
import icetone.core.layout.BorderLayout;
import icetone.core.layout.FillLayout;
import icetone.core.layout.mig.MigLayout;
import icetone.extras.appstates.FrameManagerAppState;

/**
 * This example shows examples of various types of container controls, i.e.
 * those that are specifically designed to contain at least 2 child elements.
 */
public class ContainersExample extends SimpleApplication {

	public static void main(String[] args) {
		ContainersExample app = new ContainersExample();
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

		/**
		 * The Window Manager keeps track of which frame is currently active and
		 * provides a stylable dock where frames can be minimized to. When
		 * active, the Frame will default to 'managed' so must be purposely
		 * disabled if you do not want a managed window
		 */
		getStateManager().attach(new FrameManagerAppState(screen));

		/* Unmanaged Frame 1 */
		screen.showElement(new Frame() {
			{
				setTitle("Unmanaged Frame");
				setMovable(true);
				setManagedHint(false);
				getContentArea().addElement(new Label("A Label in an ordinary,\nnon-resizable frame\n"));
				setPosition(0, 120);
			}
		});

		/* Managed Frame 1 */
		screen.showElement(new Frame() {
			{
				setTitle("Managed Frame");
				setMovable(true);
				setResizable(true);
				setMinimizable(true);
				setMaximizable(true);
				getContentArea().addElement(new Label("A Label in a managed frame"));
			}
		});

		/* Managed with menu accessor */
		screen.showElement(new Frame() {
			{
				setTitle("Frame with Menu");
				setMovable(true);
				setHasMenu(true);
				setResizable(true);
				setMinimizable(true);
				setMaximizable(true);
				getContentArea().addElement(new Label("A Label in a frame with a menu"));
			}

			@Override
			protected void createMenu(BaseElement anchor) {
				Menu<String> m = new Menu<>();
				m.addMenuItem("Menu option 1");
				m.addMenuItem("Menu option 2");
				m.addMenuItem("Menu option 3");
				m.addMenuItem("Menu option 4");
				m.showMenu(anchor);
			}

		});

		/* Frame with min and max sizes (wont resize beyond this) */
		screen.showElement(new Frame() {
			{
				setTitle("Frame with min/max");
				setMovable(true);
				setResizable(true);
				setMinimizable(true);
				setMaximizable(true);
				setMinDimensions(new Size(200, 200));
				setMaxDimensions(new Size(400, 400));
				getContentArea().addElement(new Label("This frame has minimum and maximum sizes"));
			}
		});

		/* Panel */
		screen.showElement(new Panel(new FillLayout()) {
			{
				addElement(
						new Label("A simple panel that is both resizable and moveable").setTextWrap(LineWrapMode.Word));
				setPreferredDimensions(new Size(300, 100));
				setPosition(440, 0);
			}
		});

		/* Horizontal Split */
		screen.showElement(new Frame() {
			{
				setResizable(true);
				setTitle("Horizontal Split");
				getContentArea().setLayoutManager(new FillLayout());
				getContentArea().addElement(new SplitPanel() {
					{
						setLeftOrTop(new Label("Left"));
						setRightOrBottom(new Label("Right"));
					}
				});
				setPosition(20, 270);
				setPreferredDimensions(new Size(310, 100));
			}
		});

		/* Vertical Split */
		screen.showElement(new Frame() {
			{
				setTitle("Vertical Split");
				setResizable(true);
				getContentArea().setLayoutManager(new FillLayout());
				getContentArea().addElement(new SplitPanel(Orientation.VERTICAL) {
					{
						setLeftOrTop(new Label("Top"));
						setRightOrBottom(new Label("Bottom"));
					}
				});
				setPosition(490, 280);
				setPreferredDimensions(new Size(290, 206));
			}
		});

		/* Tabs */
		screen.showElement(new Frame(new BorderLayout(8, 8)) {
			{
				setTitle("Tabs");
				setResizable(true);
				getContentArea().addElement(new Label("Demonstrates different types of tabs")
						.setTextWrap(LineWrapMode.Word).setTextAlign(Align.Center));

				/* North */
				getContentArea().addElement(new TabControl(Border.NORTH) {
					{
						addTab("Tab1", new Label("Tab Content 1"));
						addTab("Tab2", new Label("Tab Content 2"));
						addTab("Tab3", new Label("Tab Content 3"));
						addTab("Tab4", new Label("Tab Content 4"));
					}
				}, Border.NORTH);

				/* South */
				getContentArea().addElement(new TabControl(Border.SOUTH) {
					{
						addTab("Tab1", new Label("Tab Content 1"));
						addTab("Tab2", new Label("Tab Content 2"));
						addTab("Tab3", new Label("Tab Content 3"));
						addTab("Tab4", new Label("Tab Content 4"));
					}
				}, Border.SOUTH);

				/* East */
				getContentArea().addElement(new TabControl(Border.EAST) {
					{
						addTab("Tab1", new Label("Tab Content 1"));
						addTab("Tab2", new Label("Tab Content 2"));
						addTab("Tab3", new Label("Tab Content 3"));
						addTab("Tab4", new Label("Tab Content 4"));
					}
				}, Border.EAST);

				/* West */
				getContentArea().addElement(new TabControl(Border.WEST) {
					{
						addTab("Tab1", new Label("Tab Content 1"));
						addTab("Tab2", new Label("Tab Content 2"));
						addTab("Tab3", new Label("Tab Content 3"));
						addTab("Tab4", new Label("Tab Content 4"));
					}
				}, Border.WEST);

				setPosition(20, 390);
				setPreferredDimensions(new Size(440, 340));
			}
		});

		/* SlideTray */
		screen.showElement(new Panel(new MigLayout("fill, wrap 2", "[grow][grow]", "[]")) {
			{
				SlideTray tray = new SlideTray().addTrayElement(new PushButton("Button1"))
						.addTrayElement(new PushButton("Button2")).addTrayElement(new PushButton("Button3"))
						.addTrayElement(new PushButton("Button4")).addTrayElement(new PushButton("Button5"))
						.addTrayElement(new PushButton("Button6")).addTrayElement(new PushButton("Button7"))
						.addTrayElement(new PushButton("Button8")).addTrayElement(new PushButton("Button9"))
						.addTrayElement(new PushButton("Button10")).addTrayElement(new PushButton("Button11"));
				addElement(tray, "span 2");
				addElement(new CheckBox("Animation").setChecked(tray.isUseSlideEffect())
						.onChange((evt) -> tray.setUseSlideEffect(evt.getNewValue())), "span 2");
				addElement(new CheckBox("Reserve Slider Space").setChecked(tray.isReserveSliderSpace())
						.onChange((evt) -> tray.setReserveSliderSpace(evt.getNewValue())), "span 2");
				addElement(new Label("ZOrderSort"));
				addElement(new ComboBox<ZOrderSort>(ZOrderSort.values())
						.onChange((evt) -> tray.setZOrderSorting(evt.getNewValue())));
				setPosition(440, 130);
				setPreferredDimensions(new Size(305, 0, Unit.PX, Unit.AUTO));
			}
		});

		/* Window */
		screen.showElement(new Window() {
			{
				setTitle("Window");
				setResizable(true);
				getContentArea().setLayoutManager(new FillLayout());
				getContentArea().addElement(new Label("Window Content"));
				setPosition(490, 500);
				setPreferredDimensions(new Size(290, 206));
			}
		});
	}

}
