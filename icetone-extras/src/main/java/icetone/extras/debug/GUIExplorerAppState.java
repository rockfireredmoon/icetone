package icetone.extras.debug;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

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
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;

import icetone.controls.buttons.CheckBox;
import icetone.controls.buttons.PushButton;
import icetone.controls.containers.Frame;
import icetone.controls.containers.SlideTray;
import icetone.controls.containers.SplitPanel;
import icetone.controls.containers.TabControl;
import icetone.controls.menuing.Menu;
import icetone.controls.menuing.MenuBar;
import icetone.controls.table.Table;
import icetone.controls.table.Table.ColumnResizeMode;
import icetone.controls.table.TableCell;
import icetone.controls.table.TableRow;
import icetone.controls.text.Label;
import icetone.controls.text.TextField;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Layout.LayoutType;
import icetone.core.Measurement.Unit;
import icetone.core.Orientation;
import icetone.core.BaseScreen;
import icetone.core.Size;
import icetone.core.Element;
import icetone.core.ToolKit;
import icetone.core.ToolTipManager.Mode;
import icetone.core.UIEventTarget;
import icetone.core.ZPriority;
import icetone.core.event.ElementEvent.Type;
import icetone.core.event.mouse.MouseMovementListener;
import icetone.core.event.mouse.MouseUIMotionEvent;
import icetone.core.layout.mig.MigLayout;
import icetone.css.Theme;
import icetone.effects.PulseColorEffect;
import icetone.extras.appstates.FrameManagerAppState;
import icetone.extras.chooser.ColorButton;
import icetone.fontawesome.FontAwesome;

public class GUIExplorerAppState extends AbstractAppState implements RawInputListener {

	private final BaseScreen screen;
	private Table elementTree;
	private Frame panel;
	private TabControl propTabs;
	private Table allProps;
	private Table textProps;
	private Table measurementProps;
	private Table eventProps;
	private boolean picking;
	private SlideTray tools;
	private Application app;
	private PushButton layout;
	private boolean mouseButtonsEnabled;
	private PushButton pack;
	private BaseElement root;
	private PulseColorEffect pce;
	private PushButton reload;
	private PushButton highlight;
	private PushButton show;
	private PushButton hide;
	private PushButton unl;
	private Frame mouseTracker;

	public static GUIExplorerAppState toggle(Application app) {
		GUIExplorerAppState state = app.getStateManager().getState(GUIExplorerAppState.class);
		if (state == null)
			app.getStateManager().attach(new GUIExplorerAppState());
		else
			app.getStateManager().detach(state);
		return state;

	}

	public GUIExplorerAppState() {
		this(BaseScreen.get());
	}

	public GUIExplorerAppState(BaseScreen screen) {
		this.screen = screen;
	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		this.app = app;

		// Panel
		panel = new Frame(screen, true) {
			@Override
			protected void onCloseWindow() {
				app.getStateManager().detach(GUIExplorerAppState.this);
			}
		};
		panel.setResizable(true);
		panel.setTitle("GUIExplorer");
		panel.setStyleId("gui-explorer");
		panel.getContentArea().setLayoutManager(
				new MigLayout(screen, "wrap 1, ins 0, gap 0", "[fill,grow]", "[shrink 0][shrink 0][fill,grow]"));

		if (stateManager.getState(FrameManagerAppState.class) != null) {
			panel.setMaximizable(true);
			panel.setMinimizable(true);
		}

		// Menu
		MenuBar menuBar = new MenuBar(screen);

		menuBar.addMenu(new Menu<String>(screen, "Element").addMenuItem("Show").addMenuItem("Hide")
				.addMenuItem("Remove").addMenuItem("Pack").addMenuItem("Layout"));

		menuBar.addMenu(
				new Menu<String>(screen, "View").addMenuItem("Always On Top", new CheckBox(screen).onChange(evt -> {
					if (evt.getNewValue())
						panel.setPriority(ZPriority.FOREGROUND);
					else
						panel.setPriority(ZPriority.NORMAL);
				}), null).addMenuItem("Mouse Tracker", new CheckBox(screen).onChange(evt -> {
					if (evt.getNewValue())
						showMouseTracker();
					else
						hideMouseTracker();
				}), null));

		menuBar.addMenu(
				new Menu<String>(screen, "Tooltips")
						.addMenuItem("Enabled",
								new CheckBox(screen).setChecked(screen.getToolTipManager().getUseToolTips()).onChange(
										evt -> screen.getToolTipManager().setUseToolTips(evt.getNewValue())),
								null)
						.addMenuItemElement(new Menu<Mode>(screen, "Mode").addMenuItem(Mode.DETACHABLE)
								.addMenuItem(Mode.FOLLOW).addMenuItem(Mode.STATIC).addMenuItem(Mode.STICKY))
						.addMenuItemElement(new Menu<Float>(screen, "Show Delay").addMenuItem(0f).addMenuItem(0.25f)
								.addMenuItem(0.5f).addMenuItem(1f).addMenuItem(5f).addMenuItem(10f)
								.onChanged(evt -> screen.getToolTipManager().getShowDelay()))
						.addMenuItemElement(new Menu<Float>(screen, "Hide Delay").addMenuItem(0f).addMenuItem(0.25f)
								.addMenuItem(0.5f).addMenuItem(1f).addMenuItem(5f).addMenuItem(10f)
								.onChanged(evt -> screen.getToolTipManager().getHideDelay()))
						.addMenuItemElement(new Menu<Float>(screen, "Idle Hide Delay").addMenuItem(0f)
								.addMenuItem(0.25f).addMenuItem(0.5f).addMenuItem(1f).addMenuItem(5f).addMenuItem(10f)
								.onChanged(evt -> screen.getToolTipManager().getIdleHideDelay())));

		Menu<Theme> themes = new Menu<>(screen, "Themes");
		for (Theme t : ToolKit.get().getStyleManager().getThemes()) {
			if (t.getParent() == null || t.getParent().length() == 0)
				themes.addMenuItem(t);
		}
		themes.onChanged(evt -> {
			if (!evt.isTemporary())
				ToolKit.get().getStyleManager().setTheme(evt.getNewValue().getValue());
		});

		menuBar.addMenu(
				new Menu<String>(screen, "Theme").addMenuItem("Reload").addMenuItemElement(themes).onChanged(evt -> {
					if (!evt.isTemporary() && evt.getNewValue().getValue().equals("Reload"))
						ToolKit.get().getStyleManager().reload();
				}));

		panel.getContentArea().addElement(menuBar);

		// Tools
		tools = new SlideTray(screen);
		tools.setStyleClass("toolbar");
		tools.addTrayElement(FontAwesome.EYEDROPPER.button(24, new PushButton(screen, "Pick")).onMouseReleased(evt -> {
			picking = true;
			mouseButtonsEnabled = screen.isMouseButtonsEnabled();
			screen.setMouseButtonsEnabled(false);
			app.getInputManager().addRawInputListener(GUIExplorerAppState.this);
			setAvailable();
		}));
		layout = new PushButton(screen, "Layout");
		FontAwesome.EDIT.button(24, layout);
		tools.addTrayElement(layout.onMouseReleased(evt -> {
			if (elementTree.isAnythingSelected()) {
				BaseElement el = (BaseElement) elementTree.getSelectedRow().getValue();
				el.dirtyLayout(true, LayoutType.all);
				el.layoutChildren();
			}
		}));
		pack = new PushButton(screen, "Pack");
		FontAwesome.COMPRESS.button(24, pack);
		tools.addTrayElement(pack.onMouseReleased(evt -> {
			if (elementTree.isAnythingSelected()) {
				BaseElement el = (BaseElement) elementTree.getSelectedRow().getValue();
				el.sizeToContent();
			}
		}));
		reload = new PushButton(screen, "Reload");
		FontAwesome.REFRESH.button(24, reload);
		tools.addTrayElement(reload.onMouseReleased(evt -> {
			reload();
		}));
		show = new PushButton(screen, "Show");
		FontAwesome.EYE.button(24, show);
		tools.addTrayElement(show.onMouseReleased(evt -> {
			if (elementTree.isAnythingSelected()) {
				BaseElement el = (BaseElement) elementTree.getSelectedRow().getValue();
				el.show();
			}
		}));
		hide = new PushButton(screen, "Hide");
		FontAwesome.EYE_SLASH.button(24, hide);
		tools.addTrayElement(hide.onMouseReleased(evt -> {
			if (elementTree.isAnythingSelected()) {
				BaseElement el = (BaseElement) elementTree.getSelectedRow().getValue();
				el.hide();
			}
		}));
		highlight = new PushButton(screen, "Highlight");
		FontAwesome.PENCIL.button(24, highlight);
		tools.addTrayElement(highlight.onMouseReleased(evt -> {
			highlightSelection();
		}));

		panel.getContentArea().addElement(tools);

		// Tree
		elementTree = new Table(screen);
		elementTree.setStyleClass("element-tree");
		elementTree.addColumn("Class");
		elementTree.setHeadersVisible(false);
		elementTree.getScrollableArea().setPreferredDimensions(new Size(0, 400, Unit.AUTO, Unit.PX));

		// All properties
		allProps = new Table(screen);
		allProps.setSortable(true);
		allProps.setColumnResizeMode(ColumnResizeMode.AUTO_LAST);
		allProps.addColumn("Name").setPreferredDimensions(new Size(150, 0, Unit.PX, Unit.AUTO))
				.setMinDimensions(new Size(100, 0, Unit.PX, Unit.AUTO));
		allProps.addColumn("Value").setPreferredDimensions(new Size(350, 0, Unit.PX, Unit.AUTO))
				.setMinDimensions(new Size(100, 0, Unit.PX, Unit.AUTO));

		// Text properties
		textProps = new Table(screen);
		textProps.setSortable(true);
		textProps.setColumnResizeMode(ColumnResizeMode.AUTO_LAST);
		textProps.addColumn("Name").setPreferredDimensions(new Size(150, 0, Unit.PX, Unit.AUTO))
				.setMinDimensions(new Size(100, 0, Unit.PX, Unit.AUTO));
		textProps.addColumn("Value").setPreferredDimensions(new Size(350, 0, Unit.PX, Unit.AUTO))
				.setMinDimensions(new Size(100, 0, Unit.PX, Unit.AUTO));

		// Measurments properties
		measurementProps = new Table(screen);
		measurementProps.setSortable(true);
		measurementProps.setColumnResizeMode(ColumnResizeMode.AUTO_LAST);
		measurementProps.addColumn("Name").setPreferredDimensions(new Size(150, 0, Unit.PX, Unit.AUTO))
				.setMinDimensions(new Size(100, 0, Unit.PX, Unit.AUTO));
		measurementProps.addColumn("Value").setPreferredDimensions(new Size(350, 0, Unit.PX, Unit.AUTO))
				.setMinDimensions(new Size(100, 0, Unit.PX, Unit.AUTO));

		// Event properties
		eventProps = new Table(screen);
		eventProps.setSortable(true);
		eventProps.setColumnResizeMode(ColumnResizeMode.AUTO_LAST);
		eventProps.addColumn("Name").setPreferredDimensions(new Size(150, 0, Unit.PX, Unit.AUTO))
				.setMinDimensions(new Size(100, 0, Unit.PX, Unit.AUTO));
		eventProps.addColumn("Value").setPreferredDimensions(new Size(350, 0, Unit.PX, Unit.AUTO))
				.setMinDimensions(new Size(100, 0, Unit.PX, Unit.AUTO));

		// Properties tabs
		propTabs = new TabControl(screen);
		propTabs.addTab("All", allProps);
		propTabs.addTab("Text", textProps);
		propTabs.addTab("Measurements", measurementProps);
		propTabs.addTab("Event", eventProps);

		// Split
		SplitPanel split = new SplitPanel(screen, Orientation.HORIZONTAL);
		split.setDefaultDividerLocationRatio(0.25f);
		split.setLeftOrTop(elementTree);
		split.setRightOrBottom(propTabs);

		// Panel
		panel.setMinimizable(true);
		panel.setMaximizable(true);
		panel.getContentArea().addElement(split);
		panel.setLockToParentBounds(false);
		panel.setDimensions(640, 480);
		panel.setPosition(screen.getWidth() - 660, 20);
		screen.showElement(panel);

		setRoot(null);
		setAvailable();

		//
		elementTree.onChanged(evt -> {
			rebuildProps();
			setAvailable();
		});
	}

	@Override
	public void stateDetached(AppStateManager stateManager) {
		super.stateDetached(stateManager);
		if (panel.isVisible())
			panel.hide();
	}

	protected void highlightSelection() {
		if (pce != null) {
			pce.setIsActive(false);
			pce = null;
		}
		TableRow selectedRow = elementTree.getSelectedRow();
		BaseElement el = selectedRow == null ? null : (BaseElement) selectedRow.getValue();
		if (el != null) {
			pce = new PulseColorEffect(0.25f, ColorRGBA.Red);
			pce.setElement(el);
			screen.getEffectManager().applyEffect(pce);
		}
	}

	public void setRoot(BaseElement root) {
		this.root = root;
		reload();
	}

	public void reload() {
		elementTree.invalidate();
		elementTree.removeAllRows();
		for (BaseElement e : (root == null ? screen.getElements() : root.getElements())) {
			loadRows(e, null);
		}
		elementTree.validate();
	}

	protected void hideMouseTracker() {
		if (mouseTracker != null)
			mouseTracker.hide();
	}

	protected void showMouseTracker() {
		if (mouseTracker == null || !mouseTracker.isShowing()) {
			mouseTracker = new Frame(screen, true);
			Element content = mouseTracker.getContentArea();
			content.setLayoutManager(new MigLayout(screen, "wrap 4", "[][:32:][][:32:]", "[][]"));
			Label xLabel = new Label(screen, "0000");
			Label yLabel = new Label(screen, "0000");
			content.addElement(new Label(screen, "X:"));
			content.addElement(xLabel);
			content.addElement(new Label(screen, "Y:"));
			content.addElement(yLabel);
			content.addElement(new Label(screen, "Pseudo:"));
			Label pLabel = new Label(screen, "0000");
			content.addElement(pLabel, "span 3, growx");
			screen.showElement(mouseTracker);
			mouseTracker.setDestroyOnHide(true);
			MouseMovementListener<UIEventTarget> l = new MouseMovementListener<UIEventTarget>() {

				@Override
				public void onMouseMove(MouseUIMotionEvent<UIEventTarget> evt) {
					xLabel.setText(String.format("%d", evt.getX()));
					yLabel.setText(String.format("%d", evt.getY()));
					// UIEventTarget el = evt.getElement();
					BaseElement el = screen.getMouseFocusElement();
					if (el instanceof Element) {
						pLabel.setText(String.valueOf(((Element) el).getPseudoStyles()));
					} else
						pLabel.setText("");

				}
			};
			screen.addMouseMovementListener(l);
			mouseTracker.onElementEvent(evt -> screen.removeMouseMovementListener(l), Type.HIDDEN);
		}
	}

	protected void rebuildTabProps(String[] props, Table table) {
		TableRow selectedElement = elementTree.getSelectedRow();
		table.invalidate();
		table.removeAllRows();
		if (selectedElement != null) {
			BaseElement el = (BaseElement) selectedElement.getValue();
			if (el != null) {
				for (String a : props) {
					TableCell valCell = getValueForElementAttribute(el, a);
					TableRow propRow = new TableRow(screen, table, a);
					propRow.addCell(a, a);
					propRow.addElement(valCell);
					table.addRow(propRow);
				}
			}
		}

		table.sort(table.getColumns().get(0), true);
		table.validate();

	}

	protected void rebuildProps() {
		// All
		rebuildTabProps(new String[] { "absolute", "localAlpha", "elementAlpha", "globalAlpha", "styleId", "class",
				"styleClass", "styleClassNames", "dimensions", "position", "initialized", "borderHandles",
				"effectParent", "effectZOrder", "orientation", "hasFocus", "enabled", "modal", "globalModal", "movable",
				"resizable", "visible", "dragDropDragElement", "dragDropDropElement", "lockToParentBounds",
				"minDimensions", "origin", "resizeN", "resizeS", "resizeE", "resizeW", "tabIndex", "tileMode",
				"atlasCoords", "useLocalAtlas", "useLocalTexture", "worldTranslation", "localTranslation", "zOrder",
				"priority", "preferredDimensions", "maxDimensions", "minDimensions", "isContainerOnly",
				"elementTexture", "pseudoStyles", "currentStyles", "defaultColor", "fontColor", "stylesheets",
				"cssDeclarations", "horizontalScrollBarMode", "verticalScrollBarMode", "indent", "cursor",
				"textPadding", "margin", "backgroundPosition", "backgroundDimensions", "effects", "layoutData" },
				allProps);

		// Text
		rebuildTabProps(
				new String[] { "text", "textAlign", "textVAlign", "textPadding", "totalPadding", "textClipPadding",
						"textOffset", "textPosition", "textWrap", "toolTipText", "font", "fontColor", "lineHeight" },
				textProps);

		// Measurement
		rebuildTabProps(
				new String[] { "absolute", "clipPaddingVec", "clippingBounds", "clippingEnabled",
						"clipped", "clippingLayers", "zOrder", "margin", "insets", "handlePositions", "borderOffset",
						"bounds", "minDimensions", "preferredDimensions", "maxDimensions", "backgroundPosition",
						"backgroundDimensions", "calcMinimumSize", "calcMaximumSize", "calcPreferredSize",
						"calcFontColor", "calcFont" },
				measurementProps);

		// Events
		rebuildTabProps(new String[] { "ignoreFling", "ignoreMouse", "ignoreMouseButtons", "ignoreMouseMovement",
				"ignoreMouseLeftButton", "ignoreMouseRightButton", "ignoreMouseWheel", "ignoreMouseWheelClick",
				"ignoreMouseWheelMove", "ignoreTouch", "ignoreTouchEvents", "ignoreTouchMove", }, eventProps);
	}

	private static String cameliseName(String attribute) {
		return Character.toUpperCase(attribute.charAt(0)) + attribute.substring(1);
	}

	protected TableCell getValueForElementAttribute(BaseElement el, String attribute) {
		// First we need to get the value. This might be a field, or it might be
		// an
		// accessor method
		Class<? extends BaseElement> elClass = el.getClass();
		Object val = null;
		String cameliseName = cameliseName(attribute);
		;
		Class<?> retClass = null;
		try {
			Field f = elClass.getField(attribute);
			f.setAccessible(true);
			val = f.get(el);
		} catch (Exception e) {
			// No field, maybe a get accessor?
			try {
				Method m = elClass.getMethod("get" + cameliseName);
				m.setAccessible(true);
				retClass = m.getReturnType();
				val = m.invoke(el);
			} catch (Exception e2) {
				// No field, maybe a getIs accessor?
				try {
					Method m = elClass.getMethod("getIs" + cameliseName);
					m.setAccessible(true);
					retClass = m.getReturnType();
					val = m.invoke(el);
				} catch (Exception e3) {
					// No field, maybe a is accessor?
					try {
						Method m = elClass.getMethod("is" + cameliseName);
						m.setAccessible(true);
						retClass = m.getReturnType();
						val = m.invoke(el);
					} catch (Exception e4) {
						// Maybe as is
						try {
							Method m = elClass.getMethod(attribute);
							m.setAccessible(true);
							retClass = m.getReturnType();
							val = m.invoke(el);
						} catch (Exception e5) {
							try {
								Method m = elClass.getDeclaredMethod(attribute);
								m.setAccessible(true);
								retClass = m.getReturnType();
								val = m.invoke(el);
							} catch (Exception e6) {
								// No idea...
							}
						}
					}
				}
			}
		}

		// Find a matching setter
		boolean rw = false;
		if (retClass != null) {
			try {
				Method m = elClass.getMethod("set" + cameliseName, retClass);
				m.setAccessible(true);
				val = m.invoke(el);
				rw = true;
			} catch (Exception e2) {
				// No field, maybe a getIs accessor?
				try {
					Method m = elClass.getMethod("setIs" + cameliseName, retClass);
					m.setAccessible(true);
					val = m.invoke(el);
					rw = true;
				} catch (Exception e3) {
				}
			}
		}

		// We now have a value, create the best renderer for it
		TableCell valCel = new TableCell(screen, val);
		if (val != null && val instanceof Boolean) {
			final CheckBox checkBox = new CheckBox(screen);
			checkBox.setEnabled(false);
			checkBox.setChecked(Boolean.TRUE.equals(val));
			valCel.addElement(checkBox);
		} else if (val != null && val instanceof String) {
			if (rw) {
				final TextField textField = new TextField(screen);
				textField.setCharacterLength(20);
				textField.setEnabled(false);
				textField.setLockToParentBounds(true);
				textField.setText(val.toString());
				valCel.addElement(textField);
			} else {
				Element textEl = new Element(screen);
				textEl.setText((String) val);
				valCel.addElement(textEl);
			}
		} else if (val instanceof Texture) {
			Element imgEl = new Element(screen);
			imgEl.setTexture((Texture) val);
			imgEl.setBackgroundDimensions(new Size(Unit.AUTO));
			valCel.addElement(imgEl);
		} else if (val instanceof ColorRGBA) {
			// valCel.addElement(new ColorFieldControl(screen, (ColorRGBA) val,
			// true, true, false), null, false);
			valCel.addElement(new ColorButton(screen, (ColorRGBA) val));
		} else if (val instanceof Collection) {
			StringBuilder bui = new StringBuilder();
			for (Object o : ((Collection) val)) {
				if (bui.length() > 0) {
					bui.append("\n");
				}
				bui.append(String.valueOf(o));
			}
			Element textEl = new Element(screen);
			textEl.setText(bui.toString());
			valCel.addElement(textEl);
		} else {
			valCel.setText(val == null ? "<null>" : val.toString());
		}
		return valCel;

	}

	protected void loadRows(BaseElement el, TableRow parent) {
		// if (!el.equals(panel)) {
		TableRow row = new TableRow(screen, elementTree, el);
		row.setToolTipText(el.getClass().toString());

		///
		TableCell cell1 = new TableCell(screen, getLabel(el), el);
		row.addElement(cell1);

		if (parent == null) {
			elementTree.addRow(row);
		} else {
			parent.addRow(row, false);
		}
		if (!el.getElements().isEmpty()) {
			row.setLeaf(false);
			for (BaseElement e : el.getElements()) {
				loadRows(e, row);
			}
		}
		// }
	}

	@Override
	public void cleanup() {
		super.cleanup();
		screen.removeElement(panel);
	}

	private void setAvailable() {
		reload.setEnabled(!picking);
		pack.setEnabled(!picking);
		highlight.setEnabled(!picking);
		elementTree.setEnabled(!picking);
		propTabs.setEnabled(!picking);
		tools.setEnabled(!picking);
		layout.setEnabled(elementTree.isAnythingSelected());
	}

	private String getLabel(BaseElement el) {
		Class<?> clazz = el.getClass();
		String simpleName = clazz.getSimpleName();
		if (simpleName.indexOf('$') != -1) {
			clazz = clazz.getSuperclass();
			simpleName = clazz.getSimpleName();
		}
		if (simpleName.equals("")) {
			simpleName = clazz.getName();
			if (simpleName.indexOf('$') != -1) {
				clazz = clazz.getSuperclass();
				simpleName = clazz.getSimpleName();
			}
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
		if (evt.getButtonIndex() == 0) {
			BaseElement mouseFocusElement = screen.getMouseFocusElement();
			if (mouseFocusElement != null) {
				elementTree.setSelectedRowObjects(Arrays.asList(mouseFocusElement));
				highlightSelection();
				for (TableRow r : elementTree.getSelectedRows()) {
					elementTree.scrollToRow(r.getRowIndex());
					break;
				}
			}
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
