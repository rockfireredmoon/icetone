package icetone.extras.designer;

import java.util.HashMap;
import java.util.Map;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.ColorRGBA;

import icetone.controls.buttons.PushButton;
import icetone.controls.containers.Frame;
import icetone.controls.containers.Panel;
import icetone.controls.containers.SplitPanel;
import icetone.controls.extras.DragElement;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Element;
import icetone.core.ElementContainer;
import icetone.core.Layout;
import icetone.core.Orientation;
import icetone.core.event.DragEvent;
import icetone.core.event.ElementEvent;
import icetone.core.event.mouse.MouseUIButtonEvent;
import icetone.core.layout.FillLayout;
import icetone.core.layout.ScreenLayoutConstraints;
import icetone.core.layout.WrappingLayout;
import icetone.core.layout.mig.MigLayout;

public class DesignerAppState extends AbstractAppState {

	private BaseScreen screen;
	private SplitPanel workspace;
	private Element tools;
	private Element canvasStack;
	private BaseElement dragLayer;
	private BaseElement canvas;
	private Map<BaseElement, PaletteTool<?>> elementTools = new HashMap<>();

	public DesignerAppState(BaseScreen screen) {
		this.screen = screen;
	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		createWorkspace();

		screen.addElement(workspace);
	}

	protected void createWorkspace() {

		createTools();
		createCanvas();

		workspace = new SplitPanel(Orientation.HORIZONTAL);
		workspace.setDefaultDividerLocationRatio(0.75f);
		workspace.setConstraints(ScreenLayoutConstraints.fill);
		workspace.setLeftOrTop(canvasStack);
		workspace.setRightOrBottom(tools);
	}

	protected void createCanvas() {

//		dragLayer = new Element(screen);
//		dragLayer.setStyleId("drag-layer");
//		dragLayer.setDefaultColor(ColorRGBA.Gray);
//		dragLayer.setElementAlpha(0.5f);
//		dragLayer.setDragDropDropElement(true);

		canvas = new Element(screen);
		canvas.setStyleId("canvas");

		canvasStack = new Element(screen);
		canvasStack.setStyleId("canvas");
		canvasStack.setLayoutManager(new FillLayout());
		canvasStack.addElement(canvas);
	}

	protected void createTools() {
		Element palette = new Element(screen, new WrappingLayout().setOrientation(Orientation.HORIZONTAL));
		palette.setIndent(4);
		palette.addElement(configurePaletteTool(new ButtonTool(screen)));
		palette.addElement(configurePaletteTool(new PanelTool(screen)));
		palette.addElement(configurePaletteTool(new FrameTool(screen)));
		palette.addElement(configurePaletteTool(new FillLayoutTool(screen)));

		tools = new Element(screen, new MigLayout());
		tools.addElement(palette);
	}

	private PaletteTool<?> configurePaletteTool(PaletteTool<?> tool) {
		tool.onStart(evt -> {
			startDragPaletteTool(evt, tool);
		});
		tool.onEnd(evt -> {
			endDragPaletteTool(evt, tool);
		});
		return tool;
	}

	private void startDragPaletteTool(DragEvent<BaseElement> evt, PaletteTool<?> tool) {
//		canvasStack.addElement(dragLayer);
	}

	private void endDragPaletteTool(DragEvent<BaseElement> evt, PaletteTool<?> tool) {
		BaseElement target = evt.getTarget();
		System.out.println("Target is " + target);
		
//		
//		screen.removeElement(evt.getDraggedElement());
		if (tool instanceof LayoutTool) {

			Layout<ElementContainer<?, ?>, Object> layout = ((LayoutTool)tool).createElement();
			target.setLayoutManager(layout);
			tool.bindToDroppable(null);
			System.out.println("Set layout of " + target + " to " + layout);
		}
		else if (tool instanceof ControlTool) {
			tool.bindToDroppable(canvas);
//			canvasStack.removeElement(dragLayer);
			evt.setConsumed();
//		BaseElement el = tool.createElement();

//		el.onElementEvent(e -> {
			/*
			 * When moved, take the element out of it's current parent and put it back in
			 * the drag layer
			 */

//			List<BaseElement> els = screen.getElementsAt(el.getAbsoluteX() + (el.getWidth() / 2f),
//					el.getAbsoluteY() + (el.getHeight() / 2f));
//
//			/* Remove the element we are moving */
//			els.remove(e.getSource());
//
//			/*
//			 * Find the canvas layer in the list and remove it from the list and any
//			 * preceeding elements
//			 */
//			int idx = els.indexOf(canvas);
//			if (idx != -1) {
//				for (int i = 0; i < idx + 1; i++) {
//					els.remove(0);
//				}
//			}

			/* Go through the rest of the elements and look for the tool that created it */
//			for (BaseElement bel : els) {
//				PaletteTool t = elementTools.get(bel);
//				if (t != null) {
//					t.handle(e, bel);
//				}
//			}
//
//			System.out.println(els);
//
//		}, Type.MOVED);
//		el.sizeToContent();
//		Vector2f pos = new Vector2f(evt.getX(), evt.getY());
//		pos.subtract(canvas.getAbsolute());
//		el.setMovable(true);
//		el.setBringToFrontOnClick(true);
//		el.setLockToParentBounds(true);
//		el.setPosition(pos);
//		if (evt.getTarget() == dragLayer) {
//
//		}
//		elementTools.put(el, tool);
//		canvas.addElement(el);
		}
	}

	abstract class PaletteTool<T> extends DragElement {
		PaletteTool(BaseScreen screen) {
			super(screen);
			setUseSpringBackEffect(true);
			setDragMode(DragMode.Copy);
			getStyleClassNames().add("PushButton");
		}

		public void handle(ElementEvent<BaseElement> e, BaseElement bel) {
		}

		public abstract T createElement();
	}

	abstract class ControlTool extends PaletteTool<BaseElement> {

		ControlTool(BaseScreen screen) {
			super(screen);
		}

		@Override
		protected BaseElement createCopy(MouseUIButtonEvent<BaseElement> evt) {
			return createElement();
		}
	}

	abstract class LayoutTool extends PaletteTool<Layout<ElementContainer<?, ?>, Object>> {
		LayoutTool(BaseScreen screen) {
			super(screen);
			setUseSpringBackEffect(true);
			setDragMode(DragMode.Copy);
			getStyleClassNames().add("PushButton");
		}

		@Override
		protected BaseElement createCopy(MouseUIButtonEvent<BaseElement> evt) {
			return new PushButton(screen, getText());
		}

		public void handle(ElementEvent<BaseElement> e, BaseElement bel) {
		}
	}

	class FillLayoutTool extends LayoutTool {
		FillLayoutTool(BaseScreen screen) {
			super(screen);
			setText("FillLayout");
		}

		@Override
		public Layout<ElementContainer<?, ?>, Object> createElement() {
			return new FillLayout();
		}
	}

	class ButtonTool extends ControlTool {
		ButtonTool(BaseScreen screen) {
			super(screen);
			setText("PushButton");
		}

		@Override
		public BaseElement createElement() {
			return new PushButton(screen, "NewB");
		}
	}

	class PanelTool extends ControlTool {
		PanelTool(BaseScreen screen) {
			super(screen);
			setText("Panel");
		}

		@Override
		public BaseElement createElement() {
			return new Panel(screen);
		}
	}

	class FrameTool extends ControlTool {
		FrameTool(BaseScreen screen) {
			super(screen);
			setText("Frame");
		}

		@Override
		public BaseElement createElement() {
			Frame window = new Frame(screen);
			window.setDimensions(100, 100);
			window.setWindowTitle("Frame");
			return window;
		}

		public void handle(ElementEvent<BaseElement> e, BaseElement bel) {
			e.getSource().removeFromParent();
			((Frame) bel).addElement(e.getSource());
		}
	}

}
