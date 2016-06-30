package icetone.core.layout;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.KeyInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.buttons.ButtonAdapter;
import icetone.controls.buttons.CheckBox;
import icetone.controls.buttons.ToolBar;
import icetone.controls.extras.SplitPanel;
import icetone.controls.lists.Table;
import icetone.controls.lists.Table.TableRow;
import icetone.controls.text.TextField;
import icetone.controls.windows.Panel;
import icetone.controls.windows.TabControl;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.layout.mig.MigLayout;

public class GUIExplorerAppState extends AbstractAppState implements RawInputListener {

	private final ElementManager screen;
	private Table elementTree;
	private Panel panel;
	private TabControl propTabs;
	private Table allProps;
	private Table textProps;
	private Table clippingProps;
	private Table eventProps;
	private boolean picking;
	private ToolBar tools;
	private Application app;
	private ButtonAdapter layout;
	private boolean mouseButtonsEnabled;

	public GUIExplorerAppState(ElementManager screen) {
		this.screen = screen;
	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		this.app = app;

		// Panel
		panel = new Panel(screen);
		panel.setLayoutManager(new MigLayout(screen, "wrap 1, ins 0, gap 0", "[fill,grow]", "[shrink 0][fill,grow]"));

		// Tools
		tools = new ToolBar(screen);
		tools.addTrayElement(new ButtonAdapter(screen, "Pick") {

			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				picking = true;
				mouseButtonsEnabled = screen.isMouseButtonsEnabled();
				screen.setMouseButtonsEnabled(false);
				app.getInputManager().addRawInputListener(GUIExplorerAppState.this);
				setAvailable();
			}
		});
		tools.addTrayElement(layout = new ButtonAdapter(screen, "Layout") {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
			}
		});
		panel.addChild(tools);

		// Tree
		elementTree = new Table(screen) {
			@Override
			public void onChange() {
				rebuildProps();
				setAvailable();
			}
		};
		elementTree.addColumn("Class");
		elementTree.setHeadersVisible(false);

		// All properties
		allProps = new Table(screen);
		allProps.addColumn("Name");
		allProps.addColumn("Value");
		allProps.setColumnResizeMode(Table.ColumnResizeMode.AUTO_ALL);

		// Text properties
		textProps = new Table(screen);
		textProps.addColumn("Name");
		textProps.addColumn("Value");
		textProps.setColumnResizeMode(Table.ColumnResizeMode.AUTO_ALL);

		// Clipping properties
		clippingProps = new Table(screen);
		clippingProps.addColumn("Name");
		clippingProps.addColumn("Value");
		clippingProps.setColumnResizeMode(Table.ColumnResizeMode.AUTO_ALL);

		// Event properties
		eventProps = new Table(screen);
		eventProps.addColumn("Name");
		eventProps.addColumn("Value");
		eventProps.setColumnResizeMode(Table.ColumnResizeMode.AUTO_ALL);

		// Properties tabs
		propTabs = new TabControl(screen);
		propTabs.addTab("All");
		propTabs.addTabChild(0, allProps);
		propTabs.addTab("Text");
		propTabs.addTabChild(1, textProps);
		propTabs.addTab("Clipping");
		propTabs.addTabChild(2, clippingProps);
		propTabs.addTab("Event");
		propTabs.addTabChild(3, eventProps);

		// Split
		SplitPanel split = new SplitPanel(screen, Vector4f.ZERO, null, Element.Orientation.HORIZONTAL);
		split.setDefaultDividerLocationRatio(0.25f);
		split.setLeftOrTop(elementTree);
		split.setRightOrBottom(propTabs);

		// Panel
		panel.addChild(split);

		screen.addElement(panel);
		LUtil.setDimensions(panel, new Vector2f(600, 480));
		panel.layoutChildren();

		setRoot(null);
		setAvailable();
	}

	public void setRoot(Element root) {
		elementTree.removeAllRows();
		for (Element e : (root == null ? screen.getElements() : root.getElements())) {
			loadRows(e, null);
		}
		elementTree.pack();
	}

	protected void rebuildTabProps(String[] props, Table table) {
		Table.TableRow selectedElement = elementTree.getSelectedRow();
		table.removeAllRows();
		if (selectedElement != null) {
			Element el = (Element) selectedElement.getValue();
			if (el != null) {
				for (String a : props) {
					Table.TableCell valCell = getValueForElementAttribute(el, a);

					Table.TableRow propRow = new Table.TableRow(screen, table, (Object) a);
					propRow.addCell(a, a);
					propRow.addChild(valCell);
					table.addRow(propRow, false);

				}
			}
		}

		table.pack();
	}

	protected void rebuildProps() {
		// All
		rebuildTabProps(new String[] { "name", "UID", "class", "dimensions", "position", "initialized", "borders", "borderHandles",
				"orgPosition", "orgDimensions", "effectParent", "effectAbsoluteParent", "effectZOrder", "docking", "hasFocus",
				"enabled", "modal", "globalModal", "moveable", "resizable", "visible", "dragDropDragElement",
				"dragDropDropElemenet", "lockToParentBounds", "minDimensions", "origin", "resetKeyboardFocus", "resizeN", "resizeS",
				"resizeE", "resizeW", "scaleNS", "scaleEW", "tabIndex", "tileImage", "atlasCoords", "useLocalAtlas",
				"useLocalTexture", "worldTranslation", "localTranslation", "zOrder" }, allProps);

		// Text
		rebuildTabProps(
				new String[] { "text", "textAlign", "textVAlign", "textClipPadding", "textPadding", "textPaddingVec",
						"textClipPaddingVec", "textPosition", "textWrap", "toolTipText", "font", "fontColor", "fontSize", },
				textProps);

		// Clipping
		rebuildTabProps(new String[] { "clippingBounds", "clippingBoundsVec", "clipped", "hasClippingLayers", "zOrder" },
				clippingProps);

		// Events
		rebuildTabProps(new String[] { "ignoreFling", "ignoreMouse", "ignoreMouseButtons", "ignoreMouseFocus",
				"ignoreMouseLeftButton", "ignoreMouseRightButton", "ignoreMouseWheel", "ignoreMouseWheelClick",
				"ignoreMouseWheelMove", "ignoreTouch", "ignoreTouchEvents", "ignoreTouchMove", }, eventProps);
	}

	private static String cameliseName(String attribute) {
		return Character.toUpperCase(attribute.charAt(0)) + attribute.substring(1);
	}

	protected Table.TableCell getValueForElementAttribute(Element el, String attribute) {
		// First we need to get the value. This might be a field, or it might be
		// an
		// accessor method
		Class<? extends Element> elClass = el.getClass();
		Object val = null;
		try {
			Field f = elClass.getField(attribute);
			f.setAccessible(true);
			val = f.get(el);
		} catch (Exception e) {
			// No field, maybe a get accessor?
			try {
				Method m = elClass.getMethod("get" + cameliseName(attribute));
				m.setAccessible(true);
				val = m.invoke(el);
			} catch (Exception e2) {
				// No field, maybe a getIs accessor?
				try {
					Method m = elClass.getMethod("getIs" + cameliseName(attribute));
					m.setAccessible(true);
					val = m.invoke(el);
				} catch (Exception e3) {
					// No field, maybe a is accessor?
					try {
						Method m = elClass.getMethod("is" + cameliseName(attribute));
						m.setAccessible(true);
						val = m.invoke(el);
					} catch (Exception e4) {
						// No idea...
					}
				}
			}
		}

		// We now have a value, create the best renderer for it
		Table.TableCell valCel = new Table.TableCell(screen, val);
		if (val != null && val instanceof Boolean) {
			final CheckBox checkBox = new CheckBox(screen);
			checkBox.setIsEnabled(false);
			checkBox.setIsChecked(Boolean.TRUE.equals(val));
			valCel.addChild(checkBox);
		} else if (val != null && val instanceof String) {
			final TextField textField = new TextField(screen);
			textField.setIsEnabled(false);
			textField.setLockToParentBounds(true);
			textField.setText(val.toString());
			valCel.addChild(textField);
		} else {
			valCel.setText(val == null ? "<null>" : val.toString());
		}
		return valCel;

	}

	protected void loadRows(Element el, Table.TableRow parent) {
		if (!el.equals(panel)) {
			Table.TableRow row = new Table.TableRow(screen, elementTree, el);
			row.setToolTipText(el.getClass().toString());

			///
			Table.TableCell cell1 = new Table.TableCell(screen, getLabel(el), el);
			row.addChild(cell1);

			if (parent == null) {
				elementTree.addRow(row, false);
			} else {
				parent.addRow(row, false);
			}
			if (!el.getElements().isEmpty()) {
				row.setLeaf(false);
				for (Element e : el.getElements()) {
					loadRows(e, row);
				}
			}
		}
	}

	@Override
	public void cleanup() {
		super.cleanup();
		screen.removeElement(panel);
	}

	private void setAvailable() {
		elementTree.setIsEnabled(!picking);
		propTabs.setIsEnabled(!picking);
		tools.setIsEnabled(!picking);
		layout.setIsEnabled(elementTree.isAnythingSelected());
	}

	private String getLabel(Element el) {
		String simpleName = el.getClass().getSimpleName();
		if (simpleName.equals("")) {
			simpleName = el.getClass().getName();
			int idx = simpleName.lastIndexOf('.');
			if (idx != -1) {
				simpleName = simpleName.substring(idx + 1);
			}
		}
		return simpleName;
	}

	@Override
	public void beginInput() {
		// TODO Auto-generated method stub

	}

	@Override
	public void endInput() {
	}

	@Override
	public void onJoyAxisEvent(JoyAxisEvent evt) {
	}

	@Override
	public void onJoyButtonEvent(JoyButtonEvent evt) {
	}

	@Override
	public void onMouseMotionEvent(MouseMotionEvent evt) {
	}

	@Override
	public void onMouseButtonEvent(MouseButtonEvent evt) {
		System.out.println(evt);
		if (evt.getButtonIndex() == 0) {
			Element mouseFocusElement = screen.getMouseFocusElement();
			if (mouseFocusElement != null) {
				elementTree.setSelectedRowObjects(Arrays.asList(mouseFocusElement));
				for (TableRow r : elementTree.getSelectedRows()) {
					elementTree.scrollToRow(r.getRowIndex());
					break;
				}
			}
			System.out.println(mouseFocusElement);
		}
		endPick();
		evt.setConsumed();
	}

	@Override
	public void onKeyEvent(KeyInputEvent evt) {
		if (evt.getKeyCode() == KeyInput.KEY_ESCAPE && evt.isReleased()) {
			endPick();
			evt.setConsumed();
		}
	}

	@Override
	public void onTouchEvent(TouchEvent evt) {
	}

	protected void endPick() {
		picking = false;
		app.getInputManager().removeRawInputListener(this);
		setAvailable();
		screen.setMouseButtonsEnabled(mouseButtonsEnabled);
	}
}
