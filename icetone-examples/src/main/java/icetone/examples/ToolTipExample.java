package icetone.examples;

import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector2f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

import icetone.controls.buttons.CheckBox;
import icetone.controls.buttons.PushButton;
import icetone.controls.containers.Frame;
import icetone.controls.extras.Separator;
import icetone.controls.lists.ComboBox;
import icetone.controls.lists.FloatRangeSpinnerModel;
import icetone.controls.lists.Spinner;
import icetone.controls.text.Label;
import icetone.controls.text.ToolTip;
import icetone.controls.text.XHTMLToolTipProvider;
import icetone.core.BaseElement;
import icetone.core.Element;
import icetone.core.ElementContainer;
import icetone.core.Orientation;
import icetone.core.Screen;
import icetone.core.ToolTipManager;
import icetone.core.ToolTipProvider;
import icetone.core.layout.mig.MigLayout;
import icetone.fontawesome.FontAwesome;

/**
 * This example shows some examples of usage of the <i>Tooltips</i>. All
 * extensions of {@link BaseElement} have {@link BaseElement#setToolTipText(String)} for
 * simple text tips, and {@link BaseElement#setToolTipProvider(ToolTipProvider)} if
 * you want to create you own tooltip controls on demand.
 */
public class ToolTipExample extends SimpleApplication {

	public static void main(String[] args) {
		ToolTipExample app = new ToolTipExample();
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

	protected void buildExample(ElementContainer<?, ?> container) {

		/* Standard tooltip on an icon button */
		PushButton l1 = new PushButton();
		FontAwesome.CLOSE.button(24, l1);
		l1.setToolTipText("Standard tooltip");

		/* Changing tooltip */
		PushButton l1c = new PushButton("Changing 00");
		l1c.setToolTipText("Changing tooltip 00");
		l1c.addControl(new AbstractControl() {
			float time = 0;
			int idx = 0;

			@Override
			protected void controlUpdate(float tpf) {
				time += tpf;
				if (time >= 1) {
					l1c.setText("Changing " + idx);
					l1c.setToolTipText("Changing tooltip " + idx);
					idx++;
					if (idx == 100)
						idx = 1;
					time = 0;
				}
			}

			@Override
			protected void controlRender(RenderManager rm, ViewPort vp) {
			}
		});

		Label l2 = new Label("Label Standard");
		l2.setToolTipText("Label Standard tooltip");

		/* Custom */
		PushButton l3 = new PushButton("Extended");
		l3.setToolTipProvider(new ToolTipProvider() {

			@Override
			public BaseElement createToolTip(Vector2f mouseXY, BaseElement el) {
				ToolTip tt = new ToolTip();
				tt.setLayoutManager(new MigLayout());
				tt.addElement(new PushButton("Button"), "wrap");
				tt.addElement(new Label("Label"), "wrap");
				tt.addElement(new CheckBox(), "wrap");
				tt.sizeToContent();
				return tt;
			}
		});

		/* Large Normal */
		Label l4 = new Label("Large");
		l4.setToolTipText(
				"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");

		/* Large XHTML */
		final Label l5 = new Label("LrgXHTML");
		l5.setToolTipText("<h1>Lorem Ipsum</h1><p>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed "
				+ "do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis "
				+ "nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute "
				+ "irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. "
				+ "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit "
				+ "anim id est laborum.</p>");
		l5.setToolTipProvider(new XHTMLToolTipProvider());

		/* Small XHTML */
		Label l6 = new Label("SmlXHTML");
		l6.setToolTipText("<span style=\"color: red;\">Small</span> " + "<span style=\"color: green;\">XHTML</span> "
				+ "<span style=\"color: blue;\">Tooltip</span>");
		l6.setToolTipProvider(new XHTMLToolTipProvider());

		/* Simple text with newlines */
		Label l7 = new Label("New Lines");
		l7.setToolTipText("This is line 1\nThis is line 2\nThis is line 3\nThis is line 4");

		/* Frame for demonstrating tooltips */
		Frame frame1 = new Frame();
		frame1.setTitle("Tooltips");
		frame1.setPosition(100, 100);
		Element contentArea = frame1.getContentArea();
		contentArea.setLayoutManager(new MigLayout());
		contentArea.addElement(l1);
		contentArea.addElement(new Separator(Orientation.VERTICAL));
		contentArea.addElement(l1c);
		contentArea.addElement(new Separator(Orientation.VERTICAL));
		contentArea.addElement(l2);
		contentArea.addElement(new Separator(Orientation.VERTICAL));
		contentArea.addElement(l3);
		contentArea.addElement(new Separator(Orientation.VERTICAL));
		contentArea.addElement(l4);
		contentArea.addElement(new Separator(Orientation.VERTICAL));
		contentArea.addElement(l5);
		contentArea.addElement(new Separator(Orientation.VERTICAL));
		contentArea.addElement(l6);
		contentArea.addElement(new Separator(Orientation.VERTICAL));
		contentArea.addElement(l7);

		/* Frame for configuring tooltip options */

		Frame frame2 = new Frame();
		frame2.setPosition(100, 500);
		frame2.getContentArea().setLayoutManager(new MigLayout("wrap 2"));
		frame2.getContentArea().addElement(new Label("Mode"));
		ToolTipManager toolTipManager = container.getScreen().getToolTipManager();
		frame2.getContentArea()
				.addElement(new ComboBox<>(ToolTipManager.Mode.values()).setSelectedByValue(toolTipManager.getMode())
						.onChange((evt) -> toolTipManager.setMode(evt.getNewValue())));
		frame2.getContentArea().addElement(new Label("Idle Hide Delay"));
		frame2.getContentArea()
				.addElement(new Spinner<Float>()
						.setSpinnerModel(new FloatRangeSpinnerModel(0, 100f, 0.05f, toolTipManager.getIdleHideDelay()))
						.onChange((evt) -> toolTipManager.setIdleHideDelay(evt.getNewValue())));
		frame2.getContentArea().addElement(new Label("Show Delay"));
		frame2.getContentArea()
				.addElement(new Spinner<Float>()
						.setSpinnerModel(new FloatRangeSpinnerModel(0, 100f, 0.05f, toolTipManager.getShowDelay()))
						.onChange((evt) -> toolTipManager.setShowDelay(evt.getNewValue())));
		frame2.getContentArea().addElement(new Label("Hide Delay"));
		frame2.getContentArea()
				.addElement(new Spinner<Float>()
						.setSpinnerModel(new FloatRangeSpinnerModel(0, 100f, 0.05f, toolTipManager.getHideDelay()))
						.onChange((evt) -> toolTipManager.setHideDelay(evt.getNewValue())));

		/* Screen */
		container.addElement(frame1);
		container.addElement(frame2);

	}

}
