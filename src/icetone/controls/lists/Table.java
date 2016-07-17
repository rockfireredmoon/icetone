package icetone.controls.lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.jme3.font.BitmapFont;
import com.jme3.font.LineWrapMode;
import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.buttons.Button;
import icetone.controls.buttons.ButtonAdapter;
import icetone.controls.scrolling.ScrollPanel;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.Screen;
import icetone.core.event.MouseUIButtonEvent;
import icetone.core.layout.AbstractLayout;
import icetone.core.layout.LUtil;
import icetone.core.utils.BitmapTextUtil;
import icetone.core.utils.UIDUtil;
import icetone.listeners.KeyboardListener;
import icetone.listeners.MouseButtonListener;
import icetone.listeners.MouseFocusListener;
import icetone.listeners.TabFocusListener;
import icetone.style.Style;
import icetone.style.StyleManager;

/**
 * A table control that can act like a tree, a table, a tree table or a list,
 * depending on configuration.
 * <p>
 * Features include :-
 * <ul>
 * <li>Sortable columns</li>
 * <li>Resizable columns</li>
 * <li>Auto-size columns</li>
 * <li>Single / multiple, row / cell selection</li>
 * <li>Nested rows possible, which activates tree features</li>
 * <li>Hideable header</li>
 * <li>Keyboard navigation</li>
 * </ul>
 * </p>
 * <p>
 * Cells are either strings, or you may use any {@link Element}.
 * <h4>Example of a simple Table using string cells</h4> <code>
 * <pre>
 * Panel panel = new Panel(screen, "Panel",
 *          new Vector2f(8, 8), new Vector2f(372f, 300));
 * 
 * final Table table = new Table(screen, new Vector2f(10, 40)) {
 *     public void onChange() {
 *          // Invoked when selection changes.
 *     }
 * };
 * table.setColumnResizeMode(Table.ColumnResizeMode.AUTO_FIRST);
 * table.addColumn("Column 1");
 * table.addColumn("Column 2");
 * table.addColumn("Column 3");
 * for (int i = 0; i &lt; 20; * i++) {
 *     Table.TableRow row = new Table.TableRow(screen, table);
 *     row.addCell(String.format("Row %d, Cell 1", i), i);
 *     row.addCell(String.format("Row %d, Cell 2", i), i);
 *     row.addCell(String.format("Row %d, Cell 3", i), i);
 *     table.addRow(row);
 * }
 * panel.addChild(table);
 * </pre> </code>
 * </p>
 * <h4>Example of a Tree Table</h4>
 * <p>
 * To configure as a <i>Tree Table</i> features, you need set a row as NOT being
 * a 'leaf', and then add child rows to the other rows :-
 * 
 * <code>
 * <pre>
 *     final Table table = new TreeTable(screen, new Vector2f(10, 40)) {
 *         public void onChange() {
 *         }
 *     };
 *     table.addColumn("Column 1");
 *     table.addColumn("Column 2");
 *     table.addColumn("Column 3");
 * 
 *     Table.TableRow parentRow = new Table.TableRow(screen, table); 
 *     parentRow.setLeaf(false); * 
 *     parentRow.addCell("A", "1");
 *     parentRow.addCell("B", "2");
 *     parentRow.addCell("C", "3");
 *     table.addRow(parentRow);
 * 
 *     Table.TableRow childRow = new Table.TableRow(screen, table);
 *     childRow.addCell("AA", "11");
 *     childRow.addCell("Bb", "22");
 *     childRow.addCell("CC", "33");
 *     parentRow.addRow(childRow);
 * 
 * </pre>
 * </code>
 * 
 * <h4>Example of a Tree</h4>
 * <p>
 * To configure as a <i>Tree</i> , it is much the same as a Tree Table, except
 * just add a single column, and hide the headers. :-
 * 
 * <code>
 * <pre>
 *     final Table table = new Table(screen, new Vector2f(10, 40)) {
 *         public void onChange() {
 *         }
 *     };
 *     table.setHeadersVisible(false);
 *     table.addColumn("Column");
 * 
 *     Table.TableRow parentRow = new Table.TableRow(screen, table); 
 *     parentRow.setLeaf(false);
 *     parentRow.addCell("A", "1");
 *     table.addRow(parentRow);
 * 
 *     Table.TableRow childRow = new Table.TableRow(screen, table);
 *     childRow.addCell("AA", "11");
 *     parentRow.addRow(childRow);
 * 
 * </pre>
 * </code>
 * 
 * <h4>Example of a List</h4>
 * <p>
 * As an alternative to the building in lists, you can use this control, turn
 * off the headers and add on a single column, to a single depth. :-
 * 
 * <code>
 * <pre>
 *     final Table table = new Table(screen, new Vector2f(10, 40)) {
 *         public void onChange() {
 *         }
 *     };
 *     table.setHeadersVisible(false);
 *     table.addColumn("Column");
 * 
 *     Table.TableRow row1 = new Table.TableRow(screen, table); 
 *     table.addCell("A", "1");
 *     table.addRow(row1);
 * 
 *     Table.TableRow row2 = new Table.TableRow(screen, table); 
 *     parentRow.addCell("B", "2");
 *     table.addRow(row2);
 * 
 * 
 * </pre>
 * </code>
 * 
 * @author rockfire
 * @author t0neg0d
 */
public class Table extends ScrollPanel implements TabFocusListener, KeyboardListener, MouseFocusListener {

	public enum ColumnResizeMode {

		NONE, AUTO_ALL, AUTO_FIRST, AUTO_LAST;
	}

	public enum SelectionMode {

		NONE, ROW, MULTIPLE_ROWS, CELL, MULTIPLE_CELLS;

		public boolean isEnabled() {
			return !this.equals(NONE);
		}

		public boolean isSingle() {
			return this.equals(ROW) || this.equals(CELL);
		}

		public boolean isMultiple() {
			return this.equals(MULTIPLE_CELLS) || this.equals(MULTIPLE_ROWS);
		}
	}

	public class Highlight {
		TableRow row;
		TableColumn col;

		Highlight(TableRow row) {
			this.row = row;
		}

		Highlight(TableRow row, TableColumn col) {
			this.row = row;
			this.col = col;
		}
	}

	protected List<Table.TableRow> rows = new ArrayList<>();
	protected final List<Table.TableRow> allRows = new ArrayList<>();
	protected List<Integer> selectedRows = new ArrayList<>();
	protected Map<Integer, List<Integer>> selectedCells = new HashMap<>();
	protected List<Element> highlights = new ArrayList<>();
	protected Table.SelectionMode selectionMode = Table.SelectionMode.ROW;
	protected ColorRGBA highlightColor;
	protected boolean shift = false, ctrl = false;
	protected final List<Table.TableColumn> columns = new ArrayList<Table.TableColumn>();
	protected final Element columnContainer;
	protected boolean headersVisible = true;
	protected final float headerHeight;
	protected Table.ColumnResizeMode columnResizeMode = Table.ColumnResizeMode.AUTO_ALL;
	protected boolean sortable;
	protected String arrowUpImg;
	protected String arrowDownImg;
	protected String noArrowImg;
	protected Vector2f arrowSize;
	protected float headerGap = 3;
	protected boolean collapseChildrenOnParentCollapse = true;
	protected boolean enableKeyboardNavigation = false;
	protected int notLeafCount;
	protected String highlightImg;
	protected Vector4f highlightResizeBorders = Vector4f.ZERO;
	protected final Element viewPortClipLayer;
	protected final Element headerClipLayer;
	protected boolean selectOnRightClick = true;
	protected float visibleRowCount = 10;

	public static class TableCell extends Element implements Comparable<Table.TableCell> {

		private Object value;
		private String expandImg;
		private String collapseImg;
		private Vector2f cellArrowSize;
		private String cellArrowImg;
		private Vector4f cellArrowResizeBorders;
		protected BitmapFont.VAlign valign = BitmapFont.VAlign.Center;
		protected BitmapFont.Align halign = BitmapFont.Align.Center;
		protected ButtonAdapter expanderButton;

		/**
		 * Constructor for cell with no text (you probably want to
		 * {@link #addChild} instead)
		 * 
		 * @param screen
		 *            screen
		 * @param value
		 *            arbitrary value to associate with cell
		 */
		public TableCell(Object value) {
			this(Screen.get(), value);
		}

		/**
		 * Constructor for cell with no text (you probably want to
		 * {@link #addChild} instead)
		 * 
		 * @param screen
		 *            screen
		 * @param value
		 *            arbitrary value to associate with cell
		 */
		public TableCell(ElementManager screen, Object value) {
			this(screen, null, value);
		}

		/**
		 * Constructor for cell with text. If you use {@link #addChild} the text
		 * will be underneath any children.
		 * 
		 * @param screen
		 *            screen
		 * @param value
		 *            arbitrary value to associate with cell
		 */
		public TableCell(ElementManager screen, String label, Object value) {
			super(screen, UIDUtil.getUID() + "Cell", Vector2f.ZERO, LUtil.LAYOUT_SIZE,
					screen.getStyle("Table#Cell").getVector4f("resizeBorders"),
					screen.getStyle("Table#Cell").getString("defaultImg"));
			init(label, value);
		}

		public TableCell(ElementManager screen, String label, Object value, Vector2f dimensions, Vector4f resizeBorders,
				String texturePath) {
			super(screen, UIDUtil.getUID(), Vector2f.ZERO, dimensions, resizeBorders, texturePath);
			init(label, value);
		}

		/**
		 * Get the button element used for expanding the cells row. Will only be
		 * available on the first column and if the row contains non-leaf
		 * children.
		 * 
		 * @return expander button
		 */
		public Button getExpanderButton() {
			return expanderButton;
		}

		public BitmapFont.Align getHalign() {
			return halign;
		}

		public void setHAlign(BitmapFont.Align halign) {
			this.halign = halign;
			dirtyLayout(true);
			layoutChildren();
		}

		public BitmapFont.VAlign getVAlign() {
			return valign;
		}

		public void setVAlign(BitmapFont.VAlign valign) {
			this.valign = valign;
			dirtyLayout(true);
			layoutChildren();
		}

		// public void pack() {
		// final Table.TableRow row = (Table.TableRow) getElementParent();
		// if (row != null) {
		// layoutChildren();
		// positionText();
		// if (expanderButton != null) {
		// setExpanderIcon();
		// }
		// }
		// }

		protected List<Element> getActualChildren() {
			List<Element> l = new ArrayList<Element>();
			for (Element e : getElements()) {
				if (!e.equals(expanderButton)) {
					l.add(e);
				}
			}
			return l;
		}

		protected int getDepth(Table.TableRow row) {
			int depth = 0;
			if (row != null) {
				final int cellIndex = new ArrayList<Element>(row.getElements()).indexOf(this);
				Table.TableRow r = row;
				if (cellIndex == 0) {
					// Find the depth of row (this determines indent). Only need
					// to do this on first column
					while (r.parentRow != null) {
						r = r.parentRow;
						depth++;
					}
				}
			}
			return depth;
		}

		protected void setExpanderIcon() {
			// Decide whether to show an expander button, and how much to indent
			// text by
			final Table.TableRow row = (Table.TableRow) getElementParent();
			if (row != null && !row.isLeaf()) {
				final int cellIndex = new ArrayList<Element>(row.getElements()).indexOf(this);
				if (cellIndex == 0) {

					// Should we actually show a button?
					boolean shouldShow = row.table.notLeafCount > 0;
					if (shouldShow) {
						if (expanderButton != null) {
							removeChild(expanderButton);
						}
						expanderButton = new ButtonAdapter(screen, Vector2f.ZERO, LUtil.LAYOUT_SIZE,
								cellArrowResizeBorders, cellArrowImg) {
							@Override
							public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
								row.setExpanded(!row.isExpanded());
							}
						};
						expanderButton.addClippingLayer(row.table.viewPortClipLayer);
						if (row.isExpanded())
							expanderButton.setButtonIcon(cellArrowSize.x, cellArrowSize.y, collapseImg);
						else
							expanderButton.setButtonIcon(cellArrowSize.x, cellArrowSize.y, expandImg);
						addChild(expanderButton);
						positionText();
					} else if (!shouldShow && expanderButton != null) {
						removeExpanderButton();
					}
				}
			}
		}

		private void init(String label, Object value) {

			layoutManager = new CellLayout();
			// setMinDimensions(screen.getStyle("Table#Cell").getVector2f("defaultSize"));

			expandImg = screen.getStyle("Table#Cell").getString("expandImg");
			collapseImg = screen.getStyle("Table#Cell").getString("collapseImg");
			cellArrowSize = screen.getStyle("Table#Cell").getVector2f("arrowSize");
			cellArrowResizeBorders = screen.getStyle("Table#Cell").getVector4f("arrowResizeBorders");
			cellArrowImg = screen.getStyle("Table#Cell").getString("arrowImg");

			// Load default font info
			setFontColor(screen.getStyle("Table#Cell").getColorRGBA("fontColor"));
			setFontSize(screen.getStyle("Table#Cell").getFloat("fontSize"));
			setTextAlign(BitmapFont.Align.valueOf(screen.getStyle("Table#Cell").getString("textAlign")));
			setTextVAlign(BitmapFont.VAlign.valueOf(screen.getStyle("Table#Cell").getString("textVAlign")));
			setTextWrap(LineWrapMode.valueOf(screen.getStyle("Table#Cell").getString("textWrap")));
			setTextPadding(screen.getStyle("Table#Cell").getFloat("textPadding"));
			setTextClipPadding(screen.getStyle("Table#Cell").getFloat("textPadding"));

			setText(label);
			setIgnoreMouse(true);
			this.value = value;

			addClippingLayer(this);
			// setAsContainerOnly();
		}

		public Object getValue() {
			return value;
		}

		@SuppressWarnings("unchecked")
		@Override
		public int compareTo(Table.TableCell o) {
			if (value instanceof Comparable && o.value instanceof Comparable) {
				return ((Comparable<Object>) value).compareTo((Comparable<Object>) o.value);
			}
			return toString().compareTo(o.toString());
		}

		private void removeExpanderButton() {
			if (expanderButton != null) {
				removeChild(expanderButton);
				setTextPosition(0, getTextPosition().y);
				updateTextElement();
				expanderButton = null;
			}
		}

		protected void positionText() {
			if (getTextElement() != null) {
				final Table.TableRow row = (Table.TableRow) getElementParent();
				if (row != null) {
					final int cellIndex = new ArrayList<Element>(row.getElements()).indexOf(this);
					if (cellIndex == 0) {
						int depth = getDepth(row);
						float tx = (row.table.notLeafCount > 0 ? cellArrowSize.x : 0) + (depth * cellArrowSize.x);
						float ty = getTextPosition().y;

						if (textElement != null) {
//							if (textVAlign == BitmapFont.VAlign.Center) {
//								// This is a work around for the bad vertical
//								// centering you
//								// get
//								// from BitmapText (standard Button does a
//								// similar thing).
//								// The text
//								// is instead aligned to top, and it's position
//								// adjusted by
//								// the
//								// amount BitmapText would have offset it.
//								textElement.setVerticalAlignment(BitmapFont.VAlign.Top);
//								float height = BitmapTextUtil.getTextLineHeight(this, text);
//								setTextPosition(tx, ty
//										+ (int) (((getHeight() - textPadding.w - textPadding.z) / 2f) - (height / 2f)));
//							} else {
//								setTextPosition(tx, ty);
//							}
							setTextPosition(tx, ty);
						} else
							setTextPosition(tx, ty);

						updateTextElement();
					}
				}
			}
		}

		class CellLayout extends AbstractLayout {

			public Vector2f minimumSize(Element parent) {
				return null;
			}

			public Vector2f maximumSize(Element parent) {
				return null;
			}

			public Vector2f preferredSize(Element parent) {
				Vector2f prefCell = new Vector2f();
				if (getText() != null && !getText().trim().equals("")) {
					prefCell.addLocal(LUtil.getPreferredTextSize(parent));
				}
				if (!getActualChildren().isEmpty()) {
					for (Element e : getActualChildren()) {
						prefCell = LUtil.max(prefCell, LUtil.getPreferredSize(e));
					}
				}
				if (expanderButton != null) {
					prefCell.x += LUtil.getPreferredWidth(expanderButton);
					prefCell.y = Math.max(prefCell.y, LUtil.getPreferredHeight(expanderButton));
				}
				prefCell.addLocal(getTextPadding(), getTextPadding());
				return prefCell;
			}

			public void layout(Element container) {
				float x = 0, y = 0;
				for (Element e : getActualChildren()) {
					Vector2f ps = LUtil.getPreferredSize(e);
					ps.x = Math.min(container.getWidth(), ps.x);
					if (valign.equals(BitmapFont.VAlign.Bottom)) {
						y = 0;
					} else if (valign.equals(BitmapFont.VAlign.Top)) {
						y = container.getHeight() - ps.y;
					} else if (valign.equals(BitmapFont.VAlign.Center)) {
						y = (int) ((container.getHeight() - ps.y) / 2);
					}

					if (halign.equals(BitmapFont.Align.Left)) {
						x = 0;
					} else if (halign.equals(BitmapFont.Align.Right)) {
						x = container.getWidth() - ps.x;
					} else if (halign.equals(BitmapFont.Align.Center)) {
						x = (container.getWidth() - ps.x) / 2;
					}

					LUtil.setBounds(e, x, y, ps.x, ps.y);
				}
				positionText();
				if (expanderButton != null) {
					float bx = x;
					Table.TableRow row = (Table.TableRow) container.getElementParent();
					if (row != null && container.getTextElement() != null
							&& row.getElementList().indexOf(container) == 0) {
						bx = (row.table.notLeafCount > 0 ? cellArrowSize.x : 0)
								+ ((getDepth(row) - 1) * cellArrowSize.x);
					}
					LUtil.setBounds(expanderButton, bx, (container.getHeight() - cellArrowSize.y) / 2f, cellArrowSize.x,
							cellArrowSize.y);
				}
			}

			public void constrain(Element child, Object constraints) {
			}

			public void remove(Element child) {
			}
		}
	}

	public static class TableColumn extends ButtonAdapter {

		protected Table table;
		private Boolean sort;
		protected boolean resized;
		private boolean sortable;
		protected boolean resizing;

		public TableColumn(Table table, ElementManager screen) {
			this(table, screen, UIDUtil.getUID());
		}

		public TableColumn(Table table, ElementManager screen, String UID) {
			super(screen, UID, Vector2f.ZERO, screen.getStyle("Table#Header").getVector2f("defaultSize"),
					screen.getStyle("Table#Header").getVector4f("resizeBorders"),
					screen.getStyle("Table#Header").getString("defaultImg"));
			init(table);
		}

		public TableColumn(Table table, ElementManager screen, String UID, Vector2f dimensions, Vector4f resizeBorders,
				String texturePath) {
			super(screen, UID, Vector2f.ZERO, dimensions, resizeBorders, texturePath);
			init(table);
		}

		@Override
		public void controlResizeHook() {
			// This flag is to stop sort events when actually resizing
			resized = true;
			if (resizing) {
				table.dirtyLayout(true);
				table.layoutChildren();
				table.controlResizeHook();
				table.updateClippingLayers();
			}
		}

		@Override
		public void onMouseButton(MouseUIButtonEvent evt) {
			if (evt.isLeft() && evt.isPressed()) {
				resized = false;
				resizing = true;
			}
			super.onMouseButton(evt);
		}

		@Override
		public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
			super.onButtonMouseLeftUp(evt, toggled);
			resizing = false;
			if (!resized && sortable) {
				if (sort == null) {
					sort = true;
				} else {
					sort = !sort;
				}
				table.sort(this, sort);
			}

		}

		private void init(Table table) {

			// Load default font info
			setFontColor(screen.getStyle("Table#Header").getColorRGBA("fontColor"));
			setFontSize(screen.getStyle("Table#Header").getFloat("fontSize"));
			setTextAlign(BitmapFont.Align.valueOf(screen.getStyle("Table#Header").getString("textAlign")));
			setTextVAlign(BitmapFont.VAlign.valueOf(screen.getStyle("Table#Header").getString("textVAlign")));
			setTextWrap(LineWrapMode.valueOf(screen.getStyle("Table#Header").getString("textWrap")));
			setTextPadding(screen.getStyle("Table#Header").getFloat("textPadding"));
			setTextClipPadding(screen.getStyle("Table#Header").getFloat("textPadding"));

			// TODO weird bug that shows when removeAllColumns() is used.
			setButtonIcon(table.arrowSize.x, table.arrowSize.y, table.noArrowImg); // start
																					// with
																					// the
																					// blank
																					// icon
			getButtonIcon().setX(getWidth() - getButtonIcon().getWidth() - textPadding.z - getTextPadding());

			this.table = table;
			if (screen.getStyle("Table#Header").getString("hoverImg") != null) {
				setButtonHoverInfo(screen.getStyle("Table#Header").getString("hoverImg"),
						screen.getStyle("Table#Header").getColorRGBA("hoverColor"));
			}
			if (screen.getStyle("Table#Header").getString("pressedImg") != null) {
				setButtonPressedInfo(screen.getStyle("Table#Header").getString("pressedImg"),
						screen.getStyle("Table#Header").getColorRGBA("pressedColor"));
			}
			setDocking(null);
			setResizeN(false);
			setResizeS(false);
			reconfigure();
		}

		private void reconfigure() {
			setIsResizable(!table.columnResizeMode.equals(Table.ColumnResizeMode.AUTO_ALL));
			int index = table.columns.indexOf(this);
			if (index != -1) {
				switch (table.columnResizeMode) {
				case AUTO_FIRST:
					setResizeE(false);
					setResizeW(index > 0 && index < table.columns.size());
					break;
				case AUTO_LAST:
					setResizeE(true);
					setResizeW(index > 1 && index < table.columns.size());
					break;
				case NONE:
					setResizeE(true);
					setResizeW(false);
					break;
				default:
					break;
				}
			}
		}

		public void setIsSortable(boolean sortable) {
			this.sortable = sortable;
			if (!sortable) {
				setButtonIcon(table.arrowSize.x, table.arrowSize.y, table.noArrowImg); // start
																						// with
																						// the
																						// blank
																						// icon
			}
		}
	}

	/**
	 * Creates a new instance of the Table control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public Table() {
		this(Screen.get());
	}

	/**
	 * Creates a new instance of the Table control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public Table(ElementManager screen) {
		this(screen, UIDUtil.getUID(), Vector2f.ZERO, LUtil.LAYOUT_SIZE,
				screen.getStyle("Table").getVector4f("resizeBorders"),
				screen.getStyle("Table").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the Table control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 */
	public Table(ElementManager screen, Vector2f position) {
		this(screen, UIDUtil.getUID(), position, LUtil.LAYOUT_SIZE,
				screen.getStyle("Table").getVector4f("resizeBorders"),
				screen.getStyle("Table").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the Table control
	 * 
	 * @param screen
	 *            The screen control the Table is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 */
	public Table(ElementManager screen, Vector2f position, Vector2f dimensions) {
		this(screen, UIDUtil.getUID(), position, dimensions, screen.getStyle("Table").getVector4f("resizeBorders"),
				screen.getStyle("Table").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the Table control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 * @param resizeBorders
	 *            A Vector4f containg the border information used when resizing
	 *            the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg
	 *            The default image to use for the Menu
	 */
	public Table(ElementManager screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
			String defaultImg) {
		this(screen, UIDUtil.getUID(), position, dimensions, resizeBorders, defaultImg);
	}

	/**
	 * Creates a new instance of the Table control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 */
	public Table(ElementManager screen, String UID, Vector2f position) {
		this(screen, UID, position, LUtil.LAYOUT_SIZE, screen.getStyle("Table").getVector4f("resizeBorders"),
				screen.getStyle("Table").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the Table control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 */
	public Table(ElementManager screen, String UID, Vector2f position, Vector2f dimensions) {
		this(screen, UID, position, dimensions, screen.getStyle("Table").getVector4f("resizeBorders"),
				screen.getStyle("Table").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the Table control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 * @param resizeBorders
	 *            A Vector4f containg the border information used when resizing
	 *            the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg
	 *            The default image to use for the Slider's track
	 */
	public Table(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
			String defaultImg) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);

		setLayoutManager(new TableLayout());
		setScrollContentLayout(new TableContentLayout(this));

		final Style tableStyle = screen.getStyle("Table");
		final Style tableHeaderStyle = screen.getStyle("Table#Header");

		arrowSize = tableHeaderStyle.getVector2f("arrowSize");
		arrowUpImg = tableHeaderStyle.getString("arrowUpImg");
		arrowDownImg = tableHeaderStyle.getString("arrowDownImg");
		noArrowImg = tableHeaderStyle.getString("noArrowImg");

		setTextPadding(tableStyle.getVector4f("textPadding"));
		setGap(tableStyle.getFloat("gap"));
		headerGap = tableStyle.getFloat("headerGap");

		headerHeight = tableHeaderStyle.getVector2f("defaultSize").y;

		if (tableStyle.getObject("highlightImg") != null) {
			highlightImg = tableStyle.getString("highlightImg");
		}
		if (tableStyle.getObject("highlightResizeBorders") != null) {
			highlightResizeBorders = tableStyle.getVector4f("highlightResizeBorders");
		}
		if (tableStyle.getObject("highlightColor") != null) {
			highlightColor = tableStyle.getColorRGBA("highlightColor");
		}

		scrollableArea.setTextPadding(tableStyle.getVector4f("tablePadding"));
		scrollableArea.setTextClipPadding(0);

		// Mouse some mouse events bubble up (execpt scrolling)
		innerBounds.setIgnoreMouseButtons(true);
		innerBounds.setIgnoreMouseFocus(true);
		scrollableArea.setIgnoreMouse(true);
		scrollableArea.setIgnoreMouseFocus(true);

		// ??
		innerBounds.setClipPadding(getTextClipPaddingVec());

		// Dedicated clip layer
		viewPortClipLayer = new Element(screen, getUID() + ":clipLayer", Vector4f.ZERO, null);
		viewPortClipLayer.setAsContainerOnly();
		viewPortClipLayer.setIgnoreMouse(true);
		addChild(viewPortClipLayer);

		// Dedicated clip layer for header
		headerClipLayer = new Element(screen, getUID() + ":headerClipLayer", Vector4f.ZERO, null);
		headerClipLayer.setAsContainerOnly();
		headerClipLayer.setIgnoreMouse(true);
		addChild(headerClipLayer);

		// A container for the column headers (we do our own sizing and
		// positioning for this)
		columnContainer = new Element(screen, UIDUtil.getUID(), Vector2f.ZERO, LUtil.LAYOUT_SIZE, Vector4f.ZERO, null);
		columnContainer.setAsContainerOnly();
		columnContainer.setIsVisible(headersVisible);
		columnContainer.addClippingLayer(headerClipLayer);

		addChild(columnContainer);
	}

	/**
	 * Get whether rows / cells should be selected on right click.
	 * 
	 * @return select on right click
	 */
	public boolean isSelectOnRightClick() {
		return selectOnRightClick;
	}

	/**
	 * Set whether rows / cells should be selected on right click.
	 * 
	 * @param selectOnRightClick
	 *            select on right click
	 */
	public void setSelectOnRightClick(boolean selectOnRightClick) {
		this.selectOnRightClick = selectOnRightClick;
	}

	/**
	 * Get whether keyboard navigation is enabled.
	 * 
	 * @return keyboard navigation enabled
	 */
	public boolean isEnableKeyboardNavigation() {
		return enableKeyboardNavigation;
	}

	/**
	 * Set whether keyboard navigation is enabled.
	 * 
	 * @param enableKeyboardNavigation
	 *            keyboard navigation enabled
	 */
	public void setEnableKeyboardNavigation(boolean enableKeyboardNavigation) {
		this.enableKeyboardNavigation = enableKeyboardNavigation;
	}

	/**
	 * Get whether child rows are collapsed when a parent is collapsed.
	 * 
	 * @return collapse children on parent collapse
	 */
	public boolean isCollapseChildrenOnParentCollapse() {
		return collapseChildrenOnParentCollapse;
	}

	/**
	 * Set whether child rows are collapsed when a parent is collapsed.
	 * 
	 * @param collapseChildrenOnParentCollapse
	 *            collapse children on parent collapse
	 */
	public void setCollapseChildrenOnParentCollapse(boolean collapseChildrenOnParentCollapse) {
		this.collapseChildrenOnParentCollapse = collapseChildrenOnParentCollapse;
	}

	/**
	 * Get if the table is sortable.
	 * 
	 * @return sortable
	 */
	public boolean getIsSortable() {
		return sortable;
	}

	/**
	 * Set whether the table is sortable.
	 * 
	 * @param sortable
	 *            sortable
	 */
	public void setSortable(boolean sortable) {
		this.sortable = sortable;
		for (Table.TableColumn column : columns) {
			column.setIsSortable(sortable);
		}
	}

	/**
	 * Get the columns.
	 * 
	 * @return columns
	 */
	public List<Table.TableColumn> getColumns() {
		return columns;
	}

	/**
	 * Sort a column.
	 * 
	 * @param column
	 * @param ascending
	 */
	public void sort(Table.TableColumn column, boolean ascending) {
		// Sort rows
		final int columnIndex = columns.indexOf(column);
		selectedRows.clear();
		Collections.sort(rows, new Comparator<Table.TableRow>() {
			@SuppressWarnings("unchecked")
			@Override
			public int compare(Table.TableRow o1, Table.TableRow o2) {
				Element e1 = new ArrayList<Element>(o1.getElements()).get(columnIndex);
				Element e2 = new ArrayList<Element>(o2.getElements()).get(columnIndex);
				if (e1 instanceof Comparable) {
					return ((Comparable<Object>) e1).compareTo((Comparable<Object>) e2);
				}
				return e1.toString().compareTo(e2.toString());
			}
		});
		if (!ascending) {
			Collections.reverse(rows);
		}

		// Set header button images
		for (Table.TableColumn tc : columns) {
			if (tc == column) {
				tc.getButtonIcon().setTexture((ascending) ? arrowDownImg : arrowUpImg);
			} else {
				tc.getButtonIcon().setTexture(noArrowImg);
			}
		}

		pack();
	}

	/**
	 * Get the number of visible rows used to calculate the preferred size.
	 * 
	 * @return visible row count
	 */
	public float getVisibleRowCount() {
		return visibleRowCount;
	}

	/**
	 * Set the number of visible rows used to calculate the preferred size.
	 * 
	 * @param visibleRowCount
	 *            visible row count
	 */
	public void setVisibleRowCount(float visibleRowCount) {
		this.visibleRowCount = visibleRowCount;
		layoutChildren();
	}

	/**
	 * Get the column resize mode.
	 * 
	 * @return column resize mode
	 */
	public Table.ColumnResizeMode getColumnResizeMode() {
		return columnResizeMode;
	}

	/**
	 * Get whether headers are visible.
	 * 
	 * @return headers visible
	 */
	public boolean isHeadersVisible() {
		return headersVisible;
	}

	/**
	 * Set whether the headers are visible
	 * 
	 * @param headersVisible
	 *            headers visible
	 */
	public void setHeadersVisible(boolean headersVisible) {
		if (this.headersVisible != headersVisible) {
			this.headersVisible = headersVisible;
			columnContainer.setIsVisible(headersVisible);
			if (headersVisible) {
				scrollYBy(-headerGap);
			}
			// if (!headersVisible) {
			// scrollYBy(headerHeight);
			// }
			// else {
			//
			// }
			pack();
			// scrollYBy(headerHeight);
		}
	}

	/**
	 * Set the column resize mode.
	 * 
	 * @param columnResizeMode
	 *            column resize mode
	 */
	public void setColumnResizeMode(Table.ColumnResizeMode columnResizeMode) {
		if (!Objects.equals(columnResizeMode, this.columnResizeMode)) {
			this.columnResizeMode = columnResizeMode;
			reconfigureHeaders();
			dirtyLayout(false);
			layoutChildren();
			controlResizeHook();
		}
	}

	/**
	 * Remoe all columns (also removes all rows)
	 */
	public void removeAllColumns() {
		removeAllRows();
		for (Table.TableColumn col : new ArrayList<Table.TableColumn>(columns)) {
			removeColumn(col);
		}
		pack();
		// sizeColumns();
		// sizeScrollArea();
		// controlResizeHook();
	}

	/**
	 * Remove a table column
	 * 
	 * @param column
	 */
	public void removeColumn(Table.TableColumn column) {
		int index = columns.indexOf(column);
		columnContainer.removeChild(column);
		columns.remove(column);
		for (Table.TableRow row : rows) {
			row.removeColumn(index);
		}
		// sizeColumns();
		// layoutC
		pack();
		sizeScrollArea();
		controlResizeHook();
	}

	/**
	 * Add a new column.
	 * 
	 * @param columnName
	 *            column name
	 */
	public TableColumn addColumn(String columnName) {
		Table.TableColumn header = new Table.TableColumn(this, screen, getUID() + ":col:" + columnName);
		header.setText(columnName);
		addColumn(header);
		return header;
	}

	/**
	 * Add a new column control. Using this as opposed the simple string varient
	 * allows custom controls to be used for the header.
	 * 
	 * @param column
	 *            column
	 */
	public TableColumn addColumn(Table.TableColumn column) {
		column.getButtonIcon().setTexture(noArrowImg);
		column.addClippingLayer(columnContainer);
		columns.add(column);
		column.setIsSortable(sortable);
		column.addClippingLayer(columnContainer);
		columnContainer.addChild(column);
		reconfigureHeaders();
		layoutChildren();
		sizeScrollArea();
		return column;
	}

	/**
	 * Set the selection mode. See {@link SelectionMode}.
	 * 
	 * @param selectionMode
	 *            selection mode.
	 */
	public void setSelectionMode(Table.SelectionMode selectionMode) {
		this.selectionMode = selectionMode;
		if (!enableKeyboardNavigation && !selectionMode.isMultiple() && screen.getKeyboardElement() == this) {
			screen.setKeyboardElement(null);
		}
		selectedRows.clear();
		selectedCells.clear();
		createHighlights();
	}

	/**
	 * Get the selection mode. See {@link SelectionMode}.
	 * 
	 * @return selection mode.
	 */
	public Table.SelectionMode getSelectionMode() {
		return selectionMode;
	}

	/**
	 * Convenience method to add a single with a single column. Useful when
	 * using the table as a list.
	 * 
	 * @param label
	 *            cell label
	 * @param value
	 *            cell and row value
	 * @return row
	 */
	public TableRow addListRow(String label, Object value) {
		return addListRow(label, value, true);
	}

	/**
	 * Convenience method to add a single with a single column. Useful when
	 * using the table as a list.
	 * 
	 * @param label
	 *            cell label
	 * @param value
	 *            cell and row value
	 * @param pack
	 *            pack the table
	 * @return row
	 */
	public TableRow addListRow(String label, Object value, boolean pack) {
		if (columns.size() != 1) {
			throw new IllegalArgumentException(
					"May only use this method if the table is configured to have a single column");
		}
		TableRow r = new TableRow(screen, this, value);
		r.addCell(label, value);
		addRow(r, pack);
		return r;
	}

	/**
	 * Adds a TableRow to the Table and optionally calls {@link #pack() } to
	 * recalculate layout. Note, if you have lots of rows to add, it is much
	 * faster to add them all, then call {@link #pack() } once when you are
	 * done.
	 * 
	 * @param row
	 *            row
	 * @param pack
	 *            recalculate layout
	 */
	public int addRow(Table.TableRow row, boolean pack) {
		// this.getVerticalScrollBar().hide();
		this.rows.add(row);
		if (pack) {
			pack();
		}
		return rows.size() - 1;
	}

	/**
	 * Adds a TableRow to the Table and calls {@link #pack()} to recalculate
	 * layout. See
	 * {@link #addRow(icetone.controls.lists.Table.TableRow, boolean) } for an
	 * explanation of the impact of always packing when you add items.
	 * 
	 * @param row
	 *            row
	 */
	public int addRow(Table.TableRow row) {
		return addRow(row, true);
	}

	/**
	 * Inserts a new row at the provided index and optionally calls
	 * {@link #pack() } to recalculate layout. Note, if you have lots of rows to
	 * insert, it is much faster to insert them all, then call {@link #pack() }
	 * once when you are done.
	 * 
	 * @param index
	 *            The index to insert into
	 * @param row
	 *            The row to insert
	 * @param pack
	 *            recalculate layout
	 */
	public void insertRow(int index, Table.TableRow row, boolean pack) {
		if (index >= 0 && index <= rows.size()) {
			this.rows.add(index, row);
			if (pack) {
				pack();
			}
		}
	}

	/**
	 * Inserts a new row at the provided index and calls {@link #pack()} to
	 * recalculate layout. See
	 * {@link #insertRow(int, icetone.controls.lists.Table.TableRow, boolean) }
	 * for an explanation of the impact of always packing when you insert items.
	 * 
	 * @param index
	 *            The index to insert into
	 * @param row
	 *            The row to insert
	 */
	public void insertRow(int index, Table.TableRow row) {
		insertRow(index, row, true);
	}

	/**
	 * Remove the row at the provided index and optionally calls
	 * {@link #pack() } to recalculate layout. Note, if you have lots of rows to
	 * remove, it is much faster to remove them all, then call {@link #pack() }
	 * once when you are done.
	 * 
	 * @param index
	 *            int
	 * @param pack
	 *            recalculate layout
	 */
	public void removeRow(int index, boolean pack) {
		selectedCells.remove((Integer) index);
		selectedRows.remove((Integer) index);
		// this.getVerticalScrollBar().hide();
		if (!rows.isEmpty()) {
			if (index >= 0 && index < rows.size()) {
				rows.remove(index);
				if (pack) {
					pack();
				}
			}
		}
	}

	/**
	 * Remove the row and calls {@link #pack()} to recalculate layout. See
	 * {@link #removeRow(int, boolean) } for an explanation of the impact of
	 * always packing when you remove items.
	 * 
	 * @param index
	 *            int
	 * @param pack
	 *            recalculate layout
	 */
	public void removeRow(Table.TableRow row) {
		removeRow(row, true);
	}

	/**
	 * Remove the row and optionally calls {@link #pack() } to recalculate
	 * layout. Note, if you have lots of rows to remove, it is much faster to
	 * insert them all, then call {@link #pack() } once when you are done.
	 * 
	 * @param index
	 *            int
	 * @param pack
	 *            recalculate layout
	 */
	public void removeRow(Table.TableRow row, boolean pack) {
		int index = allRows.indexOf(row);
		if (index != -1) {
			removeRow(index, pack);
		}
	}

	/**
	 * Remove the row at the provided index and calls {@link #pack()} to
	 * recalculate layout. See {@link #removeRow(int, boolean) } for an
	 * explanation of the impact of always packing when you remove items.
	 * 
	 * @param index
	 *            int
	 */
	public void removeRow(int index) {
		removeRow(index, true);
	}

	/**
	 * Removes the first row in the Table
	 */
	public int removeFirstRow() {
		if (!rows.isEmpty()) {
			removeRow(0);
			return 0;
		} else {
			return -1;
		}
	}

	/**
	 * Removes the last TableRow in the Table
	 */
	public int removeLastRow() {
		if (!rows.isEmpty()) {
			removeRow(rows.size() - 1);
			return rows.size();
		} else {
			return -1;
		}
	}

	/**
	 * Remove all rows.
	 */
	public void removeAllRows() {
		rows.clear();
		selectedRows.clear();
		selectedCells.clear();
		pack();
	}

	/**
	 * Select an entire column
	 * 
	 * @param column
	 *            column
	 */
	public void setSelectColumn(int column) {
		selectedCells.clear();
		selectedRows.clear();
		for (int i = 0; i < rows.size(); i++) {
			selectedRows.add(i);
			selectedCells.put(i, new ArrayList<Integer>(Arrays.asList(column)));
		}
		createHighlights();
	}

	/**
	 * Sets the current selected row index for single select Table
	 * 
	 * @param index
	 *            int
	 */
	public void setSelectedRowIndex(Integer index) {
		if (index < 0) {
			index = 0;
		} else {
			if (index > allRows.size() - 1) {
				index = allRows.size() - 1;
			}
		}

		if (selectedRows.size() == 1 && selectedRows.get(0) == index)
			return;

		selectedRows.clear();
		selectedCells.clear();
		if (index > -1) {
			selectedRows.add(index);
		}
		createHighlights();
		updateClippingLayers();
		onChange();
	}

	/**
	 * Sets the current selected row and colum indexes
	 * 
	 * @param index
	 *            int
	 */
	public void setSelectedCellIndexes(Integer rowIndex, Integer... columnIndexes) {
		if (rowIndex < 0) {
			rowIndex = 0;
		} else {
			if (rowIndex >= allRows.size()) {
				rowIndex = allRows.size() - 1;
			}
		}
		selectedRows.clear();
		selectedCells.clear();
		if (columnIndexes.length > 0) {
			selectedCells.put(rowIndex, new ArrayList<>(Arrays.asList(columnIndexes)));
			selectedRows.add(rowIndex);
		}
		createHighlights();
		onChange();
	}

	/**
	 * Sets the current list of selected indexes to the specified indexes
	 * 
	 * @param indexes
	 */
	public void setSelectedRowIndexes(Integer... indexes) {
		selectedCells.clear();
		for (int i = 0; i < indexes.length; i++) {
			if (!selectedRows.contains(indexes[i])) {
				selectedRows.add(indexes[i]);
			}
		}
		createHighlights();
		onChange();
	}

	/**
	 * Adds specific cells of the specified row to the list of selected indexes
	 * 
	 * @param rowIndex
	 *            row index
	 * @param columnIndex
	 *            column
	 */
	public void addSelectedCellIndexes(Integer rowIndex, Integer... columnIndexes) {
		if (columnIndexes.length == 0) {
			throw new IllegalArgumentException("Must supply at least one column index.");
		}
		List<Integer> selectedColumns = selectedCells.get(rowIndex);
		if (selectedColumns == null) {
			selectedColumns = new ArrayList<Integer>();
			selectedCells.put(rowIndex, selectedColumns);
		}
		for (Integer col : columnIndexes) {
			if (!selectedColumns.contains(col)) {
				selectedColumns.add(col);
			}
		}
		if (!selectedRows.contains(rowIndex) && !selectedColumns.isEmpty()) {
			selectedRows.add(rowIndex);
		}
		createHighlights();
		onChange();
	}

	/**
	 * Adds all cells of the specified row to the list of selected indexes
	 * 
	 * @param row
	 *            row index
	 */
	public void addSelectedRowIndex(Integer row) {
		selectedCells.remove(row);
		if (!selectedRows.contains(row) && row > -1) {
			selectedRows.add(row);
		}
		createHighlights();
		onChange();
	}

	/**
	 * Removes the specified index from the list of selected indexes
	 * 
	 * @param index
	 *            int
	 */
	public void removeSelectedRowIndex(Integer index) {
		selectedCells.remove(index);
		selectedRows.remove(index);
		createHighlights();
		onChange();
	}

	/**
	 * Removes the specified cells from the list of selected indexes
	 * 
	 * @param index
	 *            int
	 */
	public void removeSelectedCellIndexes(Integer rowIndex, Integer... columnIndexes) {
		if (columnIndexes.length == 0) {
			throw new IllegalArgumentException("Must supply at least one column index.");
		}
		List<Integer> selectedColumns = selectedCells.get(rowIndex);
		if (selectedColumns != null) {
			selectedColumns.removeAll(Arrays.asList(columnIndexes));
			if (selectedColumns.isEmpty()) {
				selectedCells.remove(rowIndex);
			}
			if (selectedColumns.isEmpty()) {
				selectedRows.remove(rowIndex);
			}
		} else {
			if (columnIndexes.length == columns.size()) {
				selectedRows.remove(rowIndex);
			}
		}
		createHighlights();
		onChange();
	}

	/**
	 * Get if anything is selected (rows or cells)
	 * 
	 * @return select
	 */
	public boolean isAnythingSelected() {
		return !selectedRows.isEmpty();
	}

	/**
	 * Returns the first (or only) row in the list of selected indexes
	 * 
	 * @return int
	 */
	public int getSelectedRowIndex() {
		if (selectedRows.isEmpty()) {
			return -1;
		} else {
			return selectedRows.get(0);
		}
	}

	/**
	 * Returns the first (or only) row in the list of those selected
	 * 
	 * @return int
	 */
	public Table.TableRow getSelectedRow() {
		if (allRows.isEmpty() || selectedRows.isEmpty()) {
			return null;
		} else {
			return allRows.get(selectedRows.get(0));
		}
	}

	/**
	 * Get the list of column indexes that are selected for the row.
	 * 
	 * @return List<Integer>
	 */
	public List<Integer> getSelectedColumnIndexes(int rowIndex) {
		if (selectedCells.containsKey(rowIndex)) {
			return selectedCells.get(rowIndex);
		} else if (selectedRows.contains(rowIndex)) {
			return getAllColumnIndexes();
		}
		return Collections.emptyList();
	}

	/**
	 * Returns the entire list of selected indexes
	 * 
	 * @return List<Integer>
	 */
	public List<Integer> getSelectedRowIndexes() {
		return this.selectedRows;
	}

	/**
	 * Returns the TableRow at the specified index
	 * 
	 * @param index
	 *            int
	 * @return TableRow
	 */
	public Table.TableRow getRow(int index) {
		if (!allRows.isEmpty()) {
			if (index >= 0 && index < allRows.size()) {
				return allRows.get(index);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * Get the co-ordinates of the first selected cell. First element in array
	 * is the row, the second is the column. <code>null</code> will be returned
	 * if nothing is selected.
	 * 
	 * @return first selected cell
	 */
	public int[] getSelectedCell() {
		int r = getSelectedRowIndex();
		if (r == -1) {
			return null;
		}
		List<Integer> cols = getSelectedColumnIndexes(r);
		if (cols.isEmpty()) {
			return null;
		}
		return new int[] { r, cols.get(0) };
	}

	/**
	 * Get the co-ordinates of the last selected cell. First element in array is
	 * the row, the second is the column. <code>null</code> will be returned if
	 * nothing is selected.
	 * 
	 * @return first selected cell
	 */
	public int[] getLastSelectedCell() {
		int r = selectedRows.get(selectedRows.size() - 1);
		if (r == -1) {
			return null;
		}
		List<Integer> cols = getSelectedColumnIndexes(r);
		if (cols.isEmpty()) {
			return null;
		}
		return new int[] { r, cols.get(cols.size() - 1) };
	}

	/**
	 * Returns a List containing all ListItems corresponding to the list of
	 * selectedIndexes
	 * 
	 * @return List<ListItem>
	 */
	public List<Table.TableRow> getSelectedRows() {
		List<Table.TableRow> ret = new ArrayList<>();
		for (Integer i : selectedRows) {
			ret.add(getRow(i));
		}
		return ret;
	}

	/**
	 * Returns a List containing all the <srong>value</strong> attributes of all
	 * the rows that correspond to the list of selectedIndexes. This can be
	 * useful for taking a snapshot of the current selection, adjusting the
	 * table somehow (that would destroy selection), then resetting the
	 * selection.
	 * 
	 * @return List<Object>
	 * @see #setSelectedRowObjects(java.util.List)
	 */
	public List<Object> getSelectedObjects() {
		List<Object> ret = new ArrayList<>();
		for (Integer i : selectedRows) {
			ret.add(getRow(i).getValue());
		}
		return ret;
	}

	/**
	 * Set the row selection given a list of objects that may equal the
	 * <strong>value</strong> attibrute of each row. This can be useful for
	 * taking a snapshot of the current selection, adjusting the table somehow
	 * (that would destroy selection), then resetting the selection.
	 * 
	 * @param sel
	 *            selected objects
	 * @see #getSelectedObjects()
	 */
	public void setSelectedRowObjects(List<?> sel) {
		List<Table.TableRow> selRows = new ArrayList<>();
		for (Table.TableRow r : allRows) {
			if (!r.isLeaf() && !r.isExpanded())
				addSelectedRowObjects(r, sel, selRows);
			if (r.getValue() != null && sel.contains(r.getValue())) {
				selRows.add(r);
			}
		}
		for (Table.TableRow r : selRows) {
			if (r.getParentRow() != null) {
				expandRow(r.getParentRow());
			}
		}
		setSelectedRows(selRows);
	}

	/**
	 * Expand all rows leading up to the one provided.
	 * 
	 * @param row
	 *            row to expand
	 */
	public void expandRow(TableRow row) {
		TableRow p = row;
		while (p != null) {
			p.setExpanded(true);
			p = p.getParentRow();
		}
	}

	protected void addSelectedRowObjects(TableRow row, List<?> sel, List<Table.TableRow> selRows) {
		for (TableRow r : row.childRows) {
			if (r.getValue() != null && sel.contains(r.getValue())) {
				selRows.add(r);
			}
			if (!r.isLeaf() && !r.isExpanded()) {
				addSelectedRowObjects(r, sel, selRows);
			}
		}
	}

	/**
	 * Set the selected table rows
	 * 
	 * @param rows
	 *            selected rows
	 */
	public void setSelectedRows(List<Table.TableRow> rows) {
		selectedCells.clear();
		selectedRows.clear();
		for (Table.TableRow r : rows) {
			int idx = allRows.indexOf(r);
			if (idx != -1) {
				selectedRows.add(idx);
			}
		}
		createHighlights();
		onChange();
	}

	/**
	 * Get the number of rows in the table.
	 * 
	 * @return row count
	 */
	public int getRowCount() {
		return rows.size();
	}

	/**
	 * Get the root rows.
	 * 
	 * @return root row elements
	 */
	public List<Table.TableRow> getRows() {
		return this.rows;
	}

	public void pack() {
		rebuildAllRows();
		scrollableArea.removeAllChildren();
		highlights.clear();
		for (int i = allRows.size() - 1; i >= 0; i--) {
			Table.TableRow mi = allRows.get(i);
			addScrollableContent(mi, false, null);
			// mi.bringToFront();
			// TODO this CANT be right .. surely. It's the only thing I can get
			// to
			// work. I must be misunderstanding clipping majorly somewhere
			// mi.removeClippingLayer(viewPortClipLayer);
			// mi.addClippingLayer(viewPortClipLayer);
		}

		setVThumbPositionToScrollArea();
		setHThumbPositionToScrollArea();

		createHighlights();
		columnContainer.dirtyLayout(false);
		dirtyScrollContent();
		controlResizeHook();
	}

	@Override
	public void onKeyPress(KeyInputEvent evt) {
		// Modifiers are used for mouse selection too
		if (evt.getKeyCode() == KeyInput.KEY_LCONTROL || evt.getKeyCode() == KeyInput.KEY_RCONTROL) {
			ctrl = true;
		} else if (evt.getKeyCode() == KeyInput.KEY_LSHIFT || evt.getKeyCode() == KeyInput.KEY_RSHIFT) {
			shift = true;
		}

		if (enableKeyboardNavigation && getIsEnabled()) {
			if (selectionMode.equals(Table.SelectionMode.NONE)) {
				return;
			}
			evt.setConsumed();
		}
	}

	@Override
	public void onKeyRelease(KeyInputEvent evt) {
		if (evt.getKeyCode() == KeyInput.KEY_LCONTROL || evt.getKeyCode() == KeyInput.KEY_RCONTROL) {
			ctrl = false;
		} else if (evt.getKeyCode() == KeyInput.KEY_LSHIFT || evt.getKeyCode() == KeyInput.KEY_RSHIFT) {
			shift = false;
		}

		if (enableKeyboardNavigation && getIsEnabled()) {
			if (selectionMode.equals(Table.SelectionMode.NONE)) {
				return;
			}

			int newRow = -1;
			if (evt.getKeyCode() == KeyInput.KEY_SPACE) {
				if (!selectionMode.equals(Table.SelectionMode.NONE)) {
					List<Table.TableRow> selRows = getSelectedRows();
					if (!selRows.isEmpty()) {
						selRows.get(0).setExpanded(!selRows.get(0).isExpanded());
					}
				}
			} else if (evt.getKeyCode() == KeyInput.KEY_A && ctrl
					&& (selectionMode.isMultiple() || getRowCount() == 1)) {
				selectAll();
			} else {
				if (evt.getKeyCode() == KeyInput.KEY_LEFT) {
					newRow = selectLeft(evt);
				} else if (evt.getKeyCode() == KeyInput.KEY_RIGHT) {
					newRow = selectRight(evt);
				} else if (evt.getKeyCode() == KeyInput.KEY_DOWN) {
					newRow = selectDown(evt);
				} else if (evt.getKeyCode() == KeyInput.KEY_UP) {
					newRow = selectUp(evt);
				}

				if (newRow == -1) {
					// Return now se we don't consume
					return;
				}
			}

			// If new row is selected, scroll to it
			if (newRow >= 0 && newRow < allRows.size()) {
				scrollToRow(newRow);
			}

			evt.setConsumed();
		}
	}

	/**
	 * Clear selection
	 */
	public void clearSelection() {
		selectedCells.clear();
		selectedRows.clear();
		createHighlights();
	}

	/**
	 * Select everything
	 */
	public void selectAll() {
		selectedCells.clear();
		selectedRows.clear();
		List<Integer> l = new ArrayList<>();
		for (int i = 0; i < rows.size(); i++) {
			l.add(i);
		}
		selectedRows.addAll(l);
		createHighlights();
	}

	@Override
	public void setTabFocus() {
		if (enableKeyboardNavigation || selectionMode.isMultiple()) {
			screen.setKeyboardElement(this);
		}
	}

	@Override
	public void resetTabFocus() {
		if (enableKeyboardNavigation || selectionMode.isMultiple()) {
			screen.setKeyboardElement(null);
		}
	}

	@Override
	public boolean getResetKeyboardFocus() {
		// TODO Auto-generated method stub
		return super.getResetKeyboardFocus();
	}

	@Override
	public void onGetFocus(MouseMotionEvent evt) {
		if (isEnabled) {
			if (!getHasFocus()) {
				screen.setCursor(StyleManager.CursorType.HAND);
				onTableFocus(evt);
			}
			setHasFocus(true);
		}
	}

	@Override
	public void onLoseFocus(MouseMotionEvent evt) {
		if (isEnabled) {
			if (getHasFocus()) {
				screen.setCursor(StyleManager.CursorType.POINTER);
				onTableLostFocus(evt);
			}
			setHasFocus(false);
		}
	}

	/**
	 * Scroll to a row.
	 * 
	 * @param rIndex
	 *            row index
	 */
	public void scrollToRow(int rIndex) {

		// Get the top and bottom of the row
		if (rIndex >= 0 && rIndex < allRows.size()) {

			// Get the top and bottom of the viewport
			float adjust = headersVisible ? (headerHeight + headerGap) : 0;
			float sa = getScrollableAreaVerticalPosition();
			float top = getScrollableAreaHeight() + sa - adjust - textPadding.z;
			float bottom = top - getViewPortArea().y;

			TableRow row = allRows.get(rIndex);
			float rowTop = LUtil.getY(row) + row.getHeight();
			float rowBottom = rowTop - row.getHeight();

			// Scroll up
			if (rowTop > top) {
				scrollYByPixels(top - rowTop);
			} else if (rowBottom < bottom) {
				scrollYByPixels(bottom - rowBottom);
			}
		}

	}

	/**
	 * This is an alternative to {@link ScrollPanel#scrollYBy(float)} which
	 * appears to scroll the thumb, not the content.
	 * 
	 * @param y
	 *            number of pixels to scroll bar
	 */
	public void scrollYByPixels(float y) {
		scrollYTo(LUtil.getY(scrollableArea) + y);
		setVThumbPositionToScrollArea();
	}

	/**
	 * Scroll to the first selected row.
	 */
	public void scrollToSelected() {
		scrollToRow(getSelectedRowIndex());
	}

	/**
	 * Invoked when there is any change in the current selected.
	 */
	protected void onChange() {
		// For subclasses to override
	}

	@Override
	public void onScrollContentHook(ScrollPanel.ScrollDirection direction) {
		sizeColumnContainer();
		updateClippingLayers();
	}

	// @Override
	// protected void setParent(Node parent) {
	// if(this.getParent() == null && parent != null) {
	// app.getInputManager().addRawInputListener(this);
	// }
	// super.setParent(parent);
	// }
	//
	// @Override
	// public boolean removeFromParent() {
	// // TODO Auto-generated method stub
	// return super.removeFromParent();
	// }

	protected void onTableFocus(MouseMotionEvent evt) {
	}

	protected void onTableLostFocus(MouseMotionEvent evt) {
	}

	protected float getTotalColumnWidth() {
		float x = 0;
		for (Table.TableColumn col : columns) {
			x += col.getWidth();
		}
		return x;
	}

	protected float getHeightOfAllRows() {
		float h = 0;
		for (Table.TableRow mi : rows) {
			h += mi.getTotalRowHeight();
		}
		return h;
	}

	@Deprecated
	protected void sizeScrollArea() {
		// float x = getTotalColumnWidth() + (tablePadding.x + tablePadding.y) +
		// borders.y + borders.z;
		// getScrollableArea().setWidth(x);
		// float currentHeight = getHeightOfAllRows() + (tablePadding.z +
		// tablePadding.w) + borders.x + borders.w;
		// if (headersVisible) {
		// currentHeight += headerHeight;
		// }
		// for (Table.TableRow mi : allRows) {
		// mi.setWidth(scrollableArea.getWidth());
		// }
		// scrollableArea.setHeight(currentHeight);
	}

	protected Vector2f getViewPortArea() {
		return new Vector2f(getWidth() - (textPadding.x + textPadding.y),
				getHeight() - (textPadding.z + textPadding.w) - (headersVisible ? headerHeight : 0));
	}

	protected void sizeColumns() {
		if (columnContainer == null)
			return;

		sizeColumnContainer();

		float x = 0;
		float y = 0;
		int tw;
		if (!columns.isEmpty()) {
			Vector4f textPaddingVec = scrollableArea.getTextPaddingVec();
			float availableWidth = innerBounds.getWidth() - (textPaddingVec.x + textPaddingVec.y);
			switch (columnResizeMode) {
			case AUTO_ALL:
				tw = (int) (Math.round(availableWidth / columns.size()));
				for (int i = 0; i < columns.size(); i++) {
					if (i > 0) {
						x += headerGap;
					}
					Table.TableColumn header = columns.get(i);
					header.setPosition(x, y);
					if (i == columns.size() - 1)
						// Because of rounding
						header.setDimensions(availableWidth - x, headerHeight);
					else
						header.setDimensions(tw, headerHeight);
					x += tw;
				}
				break;
			case AUTO_FIRST:
				if (columns.size() > 0) {
					tw = (int) (availableWidth - (headerGap * (columns.size() - 1)));
					for (int i = 1; i < columns.size(); i++) {
						tw -= columns.get(i).getWidth();
					}
					Table.TableColumn header = columns.get(0);
					header.setPosition(x, y);
					header.setDimensions(tw, headerHeight);
					x += tw;
					for (int i = 1; i < columns.size(); i++) {
						x += headerGap;
						header = columns.get(i);
						header.setPosition(x, y);
						x += header.getWidth();
					}
				}
				break;
			case AUTO_LAST:
				if (columns.size() > 0) {
					tw = (int) availableWidth;
					for (int i = 0; i < columns.size() - 1; i++) {
						final Table.TableColumn header = columns.get(i);
						header.setPosition(x, y);
						x += header.getWidth();
						x += headerGap;
					}
					Table.TableColumn header = columns.get(columns.size() - 1);
					header.setPosition(x, y);
					header.setDimensions(tw - x, headerHeight);
				}
				break;
			case NONE:
				for (Table.TableColumn header : columns) {
					header.setPosition(x, y);
					x += header.getWidth();
					x += headerGap;
				}
				break;
			}

			for (Table.TableColumn col : columns) {
				col.getButtonIcon().setX(col.getWidth() - col.getButtonIcon().getWidth() - col.getTextPaddingVec().z
						- col.getTextPadding());
			}

			// synchronized (allRows) {
			// for (Table.TableRow r : allRows) {
			// r.layoutChildren();
			// }
			// }

		}

	}

	protected void sizeColumnContainer() {
		Vector4f textPaddingVec = scrollableArea.getTextPaddingVec();
		LUtil.setBounds(columnContainer, scrollableArea.getX() + textPadding.x + textPaddingVec.x,
				textPadding.z + textPaddingVec.z, scrollableArea.getWidth(), headerHeight);
	}

	protected int selectUp(KeyInputEvent evt) {
		int selRow = getSelectedRowIndex();
		int lastRow = Math.max(0, selectedRows.isEmpty() ? 0 : selectedRows.get(selectedRows.size() - 1));
		int newRow = Math.max(0, lastRow - 1);
		switch (selectionMode) {
		case ROW:
		case MULTIPLE_ROWS:
			if (shift && selectionMode.equals(Table.SelectionMode.MULTIPLE_ROWS)) {
				if (selRow >= lastRow) {
					addSelectedRowIndex(newRow);
				} else {
					removeSelectedRowIndex(lastRow);
				}
			} else {
				setSelectedRowIndex(newRow);
			}
			break;
		case MULTIPLE_CELLS:
		case CELL:
			final List<Integer> selectedColumnIndexes = getSelectedColumnIndexes(lastRow);
			if (shift && selectionMode.equals(Table.SelectionMode.MULTIPLE_CELLS)) {
				if (selRow >= lastRow) {
					addSelectedCellIndexes(newRow, selectedColumnIndexes.toArray(new Integer[0]));
				} else {
					removeSelectedCellIndexes(lastRow, selectedColumnIndexes.toArray(new Integer[0]));
				}
			} else {
				setSelectedCellIndexes(newRow, selectedColumnIndexes.toArray(new Integer[0]));
			}
			break;
		default:
			break;
		}
		return newRow;
	}

	protected int selectDown(KeyInputEvent evt) {
		int newRow = -1;
		switch (selectionMode) {
		case ROW:
		case MULTIPLE_ROWS:
			int selRow = getSelectedRowIndex();
			int lastRow = selectedRows.isEmpty() ? 0 : selectedRows.get(selectedRows.size() - 1);
			newRow = lastRow + 1;
			if (shift && selectionMode.equals(Table.SelectionMode.MULTIPLE_ROWS)) {
				if (lastRow >= selRow) {
					addSelectedRowIndex(newRow);
				} else {
					if (selRow > lastRow) {
						removeSelectedRowIndex(lastRow);
					} else {
						removeSelectedRowIndex(selRow);
					}
				}
			} else {
				setSelectedRowIndex(newRow);
			}
			break;
		case MULTIPLE_CELLS:
		case CELL:
			lastRow = selectedRows.isEmpty() ? 0 : selectedRows.get(selectedRows.size() - 1);
			final List<Integer> selectedColumnIndexes = getSelectedColumnIndexes(lastRow);
			if (shift && selectionMode.equals(Table.SelectionMode.MULTIPLE_CELLS)) {
				selRow = getSelectedRowIndex();
				if (lastRow >= selRow) {
					newRow = lastRow + 1;
					addSelectedCellIndexes(newRow, selectedColumnIndexes.toArray(new Integer[0]));
				} else {
					if (selRow > lastRow) {
						removeSelectedCellIndexes(lastRow, selectedColumnIndexes.toArray(new Integer[0]));
					} else {
						removeSelectedCellIndexes(selRow, selectedColumnIndexes.toArray(new Integer[0]));
					}
				}
			} else {
				newRow = lastRow + 1;
				setSelectedCellIndexes(newRow, selectedColumnIndexes.toArray(new Integer[0]));
			}
			break;
		default:
			break;
		}
		return newRow;
	}

	protected int selectRight(KeyInputEvent evt) {
		int newRow = -1;
		switch (selectionMode) {
		case ROW:
		case MULTIPLE_ROWS:
			// Return now se we don't consume
			return newRow;
		case CELL:
		case MULTIPLE_CELLS:
			if (isAnythingSelected()) {

				int[] sel = getSelectedCell();
				int[] lastSel = getLastSelectedCell();
				newRow = sel[0];
				if (sel[1] > lastSel[1]) {
					for (int r : getSelectedRowIndexes()) {
						removeSelectedCellIndexes(r, lastSel[1]);
					}
				} else {
					int col = lastSel[1];
					col++;
					if (selectionMode.equals(Table.SelectionMode.CELL) || !shift) {
						if (col >= columns.size()) {
							col = 0;
							newRow++;
						}
						if (newRow >= allRows.size()) {
							newRow = allRows.size() - 1;
							col = 0;
						}
					} else {
						if (col >= columns.size()) {
							col = columns.size() - 1;
						}
					}
					if (shift && selectionMode.equals(Table.SelectionMode.MULTIPLE_CELLS)) {
						for (int r : getSelectedRowIndexes()) {
							addSelectedCellIndexes(r, col);
						}
					} else {
						setSelectedCellIndexes(newRow, col);
					}
				}
			} else if (getRowCount() > 0) {
				newRow = 0;
				setSelectedCellIndexes(0, 0);
			}
			break;
		default:
			break;
		}
		return newRow;
	}

	protected int selectLeft(KeyInputEvent evt) {
		int newRow = -1;
		switch (selectionMode) {
		case ROW:
		case MULTIPLE_ROWS:
			// Return now se we don't consume
			return newRow;
		case MULTIPLE_CELLS:
		case CELL:
			if (isAnythingSelected()) {
				int[] sel = getSelectedCell();
				int[] lastSel = getLastSelectedCell();
				newRow = sel[0];

				// Work out which side of the selection we adjust
				if (sel[1] < lastSel[1]) {
					for (int r : getSelectedRowIndexes()) {
						removeSelectedCellIndexes(r, lastSel[1]);
					}
				} else {
					int col = lastSel[1];
					col--;
					if (selectionMode.equals(Table.SelectionMode.CELL) || !shift) {
						if (col < 0) {
							col = columns.size() - 1;
							newRow--;
						}
						if (newRow < 0) {
							newRow = 0;
							col = 0;
						}
					} else {
						if (col < 0) {
							col = 0;
						}
					}
					if (shift && selectionMode.equals(Table.SelectionMode.MULTIPLE_CELLS)) {
						for (int r : getSelectedRowIndexes()) {
							addSelectedCellIndexes(r, col);
						}
					} else {
						setSelectedCellIndexes(newRow, col);
					}
				}

			} else if (getRowCount() > 0) {
				newRow = 0;
				setSelectedCellIndexes(0, 0);
			}
			break;
		default:
			break;
		}
		return newRow;
	}

	protected void createRowHighlight(Integer r, TableRow row) {
		Element highlight = createHighlight(r, 0);
		highlight.setElementUserData(new Highlight(row));
		highlight
				.setWidth(Math.max(getScrollableAreaWidth(), getScrollBoundsWidth()) - (textPadding.x + textPadding.y));
		// scrollableArea.addChild(highlight);
		addScrollableContent(highlight);
		highlights.add(highlight);
		// highlight.bringToFront();
	}

	protected void repositionHighlights() {
		for (Element el : highlights) {
			Highlight h = (Highlight) el.getElementUserData();
			if (h.col == null) {
				// Row highlight
				LUtil.setBounds(el, scrollableArea.getTextPaddingVec().x, LUtil.getY(h.row), scrollableArea.getWidth(),
						h.row.getHeight());
			} else {
				// Cell highlight
				LUtil.setBounds(el, scrollableArea.getTextPaddingVec().x + h.col.getX(), LUtil.getY(h.row),
						h.col.getWidth(), h.row.getHeight());
			}
		}

		// highlight.setWidth(column.getWidth());
		// highlight.setPosition(column.getX() + textPadding.x, row.getY());
	}

	protected void createCellHighlights(List<Integer> cells, Integer r, TableRow row) {
		for (Integer columnIndex : cells) {
			Table.TableColumn column = columns.get(columnIndex);
			Element highlight = createHighlight(r, columnIndex);
			highlight.setElementUserData(new Highlight(row, column));
			addScrollableContent(highlight);
			highlights.add(highlight);
		}
	}

	protected Element createHighlight(int index, int index2) {
		Element highlight = new Element(screen, getUID() + ":Highlight" + index + ":" + index2, highlightResizeBorders,
				highlightImg);
		highlight.addClippingLayer(viewPortClipLayer);
		highlight.setIgnoreMouse(true);
		if (highlightColor != null) {
			highlight.getElementMaterial().setColor("Color", highlightColor);
		}
		return highlight;
	}

	private void addRows(Table.TableRow row) {
		allRows.add(row);
		if (!row.isLeaf() && row.isExpanded()) {
			for (Table.TableRow r : row.getChildRows()) {
				addRows(r);
			}
		}
	}

	protected void rebuildAllRows() {
		// Build up a list of ALL the rows, drilling down into child rows if
		// there are any
		allRows.clear();
		for (Table.TableRow mi : rows) {
			addRows(mi);
		}
	}

	protected void createHighlights() {
		for (Element h : highlights) {
			removeScrollableContent(h);
			// scrollableArea.removeChild(h);
		}
		highlights.clear();

		for (Integer r : selectedRows) {
			if (r >= allRows.size()) {
				System.err.println("WARNING: A row is selected that doesn't exist. Rockfire... FIX IT!");
			} else {
				TableRow row = allRows.get(r);
				List<Integer> cells = selectedCells.get(r);
				if (cells == null) {
					createRowHighlight(r, row);
				} else {
					createCellHighlights(cells, r, row);
				}
			}
		}
		repositionHighlights();
	}

	private void reconfigureHeaders() {
		for (Table.TableColumn header : columns) {
			header.reconfigure();
		}
	}

	private List<Integer> getAllColumnIndexes() {
		List<Integer> l = new ArrayList<>();
		for (int i = 0; i < columns.size(); i++) {
			l.add(i);
		}
		return l;
	}

	public static class TableRow extends Element implements MouseButtonListener {

		protected Table table;
		protected boolean expanded;
		protected boolean leaf = true;
		protected List<Table.TableRow> childRows = new ArrayList<Table.TableRow>();
		protected Table.TableRow parentRow;
		protected Object value;
		protected int notLeafCount;

		public TableRow(Table table) {
			this(table.getScreen(), table, null);
		}

		public TableRow(ElementManager screen, Table table) {
			this(screen, table, null);
		}

		public TableRow(ElementManager screen, Table table, Object value) {
			this(screen, table, UIDUtil.getUID(), value);
		}

		public TableRow(ElementManager screen, Table table, String UID, Object value) {
			this(screen, table, UID, LUtil.LAYOUT_SIZE, Vector4f.ZERO, null, value);
		}

		public TableRow(ElementManager screen, Table table, String UID, Vector2f dimensions, Vector4f resizeBorders,
				String texturePath) {
			this(screen, table, UID, dimensions, resizeBorders, texturePath, null);
		}

		public TableRow(ElementManager screen, Table table, String UID, Vector2f dimensions, Vector4f resizeBorders,
				String texturePath, Object value) {
			super(screen, UID, Vector2f.ZERO, dimensions, resizeBorders, texturePath);
			init(table, value);
		}

		public int getRowIndex() {
			return table.rows.indexOf(this);
		}

		private void init(Table table, Object value) {
			layoutManager = new RowLayout();
			this.value = value;
			this.table = table;
			setIgnoreMouseWheel(true);
			setIgnoreMouseFocus(true);
			addClippingLayer(table.viewPortClipLayer);
		}

		public float getTotalRowHeight() {
			int h = LUtil.getPreferredHeight(this);
			if (expanded) {
				for (TableRow r : childRows) {
					h += r.getTotalRowHeight();
				}
			}
			return h;
		}

		/**
		 * Get the parent row (if any). This will only be non-null once this (as
		 * a child row) has been added to the parent using
		 * {@link #addRow(icetone.controls.lists.Table.TableRow) } or similar.
		 * 
		 * @return parent row
		 */
		public Table.TableRow getParentRow() {
			return parentRow;
		}

		public Table getTable() {
			return table;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		public Object getValue() {
			return value;
		}

		public boolean isExpanded() {
			return expanded;
		}

		public void setExpanded(boolean expanded) {
			if (this.expanded && !expanded) {
				if (table.collapseChildrenOnParentCollapse)
					collapse();
				table.pack();
			} else if (expanded && !this.expanded) {
				this.expanded = expanded;
				table.pack();
			}
		}

		public boolean isLeaf() {
			return leaf;
		}

		public void setLeaf(boolean leaf) {
			if (leaf && !this.leaf && !childRows.isEmpty()) {
				throw new IllegalStateException("Cannot make a leaf it there are already children.");
			}
			if (leaf != this.leaf) {
				this.leaf = leaf;
				if (leaf) {
					table.notLeafCount--;
					if (parentRow != null) {
						parentRow.notLeafCount--;
					}
				} else {
					table.notLeafCount++;
					if (parentRow != null) {
						parentRow.notLeafCount++;
					}
				}

				// Reconfigure expander icons now leaf state has changed
				for (Element el : getElements()) {
					if (el instanceof Table.TableCell) {
						((Table.TableCell) el).setExpanderIcon();
					}
				}
			}
		}

		/**
		 * Adds a child TableRow to this row and optionally calls
		 * {@link #pack() } to recalculate layout. Note, if you have lots of
		 * rows to add, it is much faster to add them all, then call
		 * {@link #pack() } once when you are done.
		 * <p>
		 * Note you cannot add child rows unless the row is not a leaf. Use
		 * {@link #setLeaf(boolean) }.
		 * 
		 * @param row
		 *            row
		 * @param pack
		 *            recalculate layout
		 */
		public int addRow(Table.TableRow row, boolean pack) {
			if (leaf) {
				throw new IllegalStateException("Cannot add child rows to leaf rows");
			}
			row.parentRow = this;
			this.childRows.add(row);
			if (pack) {
				layoutChildren();
			}
			return childRows.size() - 1;
		}

		/**
		 * Adds a child TableRow to the Table and calls {@link #pack()} to
		 * recalculate layout. See
		 * {@link #addRow(icetone.controls.lists.Table.TableRow, boolean) } for
		 * an explanation of the impact of always packing when you add items.
		 * <p>
		 * Note you cannot add child rows unless the row is not a leaf. Use
		 * {@link #setLeaf(boolean) }.
		 * 
		 * @param row
		 *            row
		 */
		public int addRow(Table.TableRow row) {
			return addRow(row, true);
		}

		/**
		 * Adds a default cell to this row.
		 * 
		 * @param label
		 *            label of cell
		 * @param value
		 *            value of cell
		 * @return the cell element
		 */
		public Table.TableCell addCell(String label, Object value) {
			final Table.TableCell tableCell = new Table.TableCell(screen, label, value);
			addChild(tableCell);
			return tableCell;
		}

		/**
		 * Get the cell at the specified index.
		 * 
		 * @param i
		 *            index of cell
		 * @return cell
		 */
		public Table.TableCell getCell(int i) {
			return (Table.TableCell) new ArrayList<Element>(getElements()).get(i);
		}

		/**
		 * Get all of the child rows (if any). Row must not be a leaf for this
		 * to be able to contain any child rows.
		 * 
		 * @return child rows
		 */
		public List<Table.TableRow> getChildRows() {
			return childRows;
		}

		/**
		 * Remove the cell for a particular column index.
		 * 
		 * @param index
		 *            column index
		 */
		public void removeColumn(int index) {
			Element el = new ArrayList<Element>(getElements()).get(index);
			removeChild(el);
		}

		/**
		 * Pack the row. The height of the row will be calculated.
		 */
		@Override
		public void onBeforeLayout() {
			// Now layout those cells within that height
			for (Element child : getElements()) {
				((Table.TableCell) child).setExpanderIcon();
			}
		}

		@Override
		public void onMouseButton(MouseUIButtonEvent evt) {
			if ((evt.isLeft() || (table.selectOnRightClick && evt.isRight())) && evt.isReleased())
				onMouseSelect(evt);
			// if (table instanceof MouseButtonListener) {
			// ((MouseButtonListener) table).onMouseButton(evt);
			// }
			// table.fire
		}

		private void collapse() {
			collapse(false);
		}

		private void collapse(boolean deselect) {
			expanded = false;

			// Remove any selection for this row
			if (deselect) {
				int rowInd = table.allRows.indexOf(this);
				if (table.selectedRows.contains(rowInd)) {
					table.selectedRows.remove((Integer) rowInd);
				}
				if (table.selectedCells.containsKey(rowInd)) {
					table.selectedCells.remove(rowInd);
				}
			}

			// Collapse all child (and so remove their selection too)
			for (Table.TableRow r : childRows) {
				r.collapse(true);
			}
		}

		// protected void layoutRow() {
		// final Collection<Element> elements = getElements();
		// if (elements.size() != table.columns.size()) {
		// throw new IllegalStateException(String.format(
		// "All rows must have the same number of column cells as the "
		// + "table has columns. This row has %d, but there are %d " + "table
		// columns.",
		// elements.size(), table.columns.size()));
		// }
		// Iterator<Element> el = elements.iterator();
		// float x = 0;
		// // Set the heights and widths of the header
		// float h = 0;
		// for (Table.TableColumn header : table.columns) {
		// Element cell = el.next();
		// final float width = header.getWidth();
		// cell.setX(x);
		// cell.setWidth(width);
		// x += width;
		// h = Math.max(0, cell.getHeight());
		// }
		// setHeight(h);
		// }

		protected void onMouseSelect(MouseButtonEvent evt) {
			if (!table.getIsEnabled()) {
				return;
			}
			table.setTabFocus();
			int currentRowIndex = table.allRows.indexOf(this);
			int currentColumnIndex = 0;
			int i = 0;
			for (Element el : getElements()) {
				if (evt.getX() >= el.getAbsoluteX() && evt.getX() < el.getAbsoluteX() + el.getWidth()) {
					currentColumnIndex = i;
					break;
				}
				i++;
			}
			switch (table.selectionMode) {
			case MULTIPLE_ROWS:
				if (table.ctrl) {
					if (!table.selectedRows.contains(currentRowIndex)) {
						table.addSelectedRowIndex(currentRowIndex);
					} else {
						table.removeSelectedRowIndex(currentRowIndex);
					}
				} else if (table.shift) {
					int lastRow = table.selectedRows.get(table.selectedRows.size() - 1);
					if (currentRowIndex > lastRow) {
						for (i = lastRow + 1; i <= currentRowIndex; i++) {
							table.addSelectedRowIndex(i);
						}
					} else {
						for (i = lastRow - 1; i >= currentRowIndex; i--) {
							table.addSelectedRowIndex(i);
						}
					}
				} else {
					table.setSelectedRowIndex(currentRowIndex);
				}
				break;
			case ROW:
				if (currentRowIndex >= 0 && currentRowIndex < table.allRows.size()) {
					table.setSelectedRowIndex(currentRowIndex);
				} else {
					table.selectedRows.clear();
				}
				break;
			case MULTIPLE_CELLS:
				if (table.ctrl) {
					if (!table.getSelectedColumnIndexes(currentRowIndex).contains(currentColumnIndex)) {
						table.addSelectedCellIndexes(currentRowIndex, currentColumnIndex);
					} else {
						table.removeSelectedCellIndexes(currentRowIndex, currentColumnIndex);
					}
				} else if (table.shift) {
					int[] lastSel = table.getLastSelectedCell();
					int lastRow = lastSel[0];
					List<Integer> cols = new ArrayList<Integer>(table.getSelectedColumnIndexes(lastRow));
					if (currentColumnIndex > lastSel[1]) {
						for (i = lastSel[1] + 1; i <= currentColumnIndex; i++) {
							cols.add(i);
						}
					} else if (currentColumnIndex < lastSel[1]) {
						for (i = currentColumnIndex; i <= lastSel[1] - 1; i++) {
							cols.add(i);
						}
					}
					int startRow = Math.min(Math.min(table.getSelectedRowIndex(), lastRow), currentRowIndex);
					int endRow = Math.max(Math.max(table.getSelectedRowIndex(), lastRow), currentRowIndex);
					for (i = startRow; i <= endRow; i++) {
						table.addSelectedCellIndexes(i, cols.toArray(new Integer[0]));
					}
				} else {
					table.setSelectedCellIndexes(currentRowIndex, currentColumnIndex);
				}
				break;
			case CELL:
				if (currentColumnIndex >= 0 && currentColumnIndex < table.columns.size() && currentRowIndex >= 0
						&& currentRowIndex < table.allRows.size()) {
					table.setSelectedCellIndexes(currentRowIndex, currentColumnIndex);
				} else {
					table.selectedCells.clear();
				}
				break;
			default:
				break;
			}
		}

		class RowLayout extends AbstractLayout {

			public Vector2f minimumSize(Element parent) {
				Vector2f min = new Vector2f(textPadding.x + textPadding.y, 0);
				for (Element el : parent.getElements()) {
					if (min.x > 0)
						min.x += table.headerGap;
					Vector2f cm = LUtil.getMinimumSize(el);
					min.x += cm.x;
					min.y = Math.max(min.y, cm.y);
				}
				min.y += textPadding.z + textPadding.w;
				return min;
			}

			public Vector2f maximumSize(Element parent) {
				// Vector2f max = new Vector2f(0, 0);
				// for (Element el : parent.getElements()) {
				// if (max.x > 0)
				// max.x += table.headerGap;
				// Vector2f cm = LUtil.getMaximumSize(el);
				// max.x += cm.x;
				// max.y = Math.max(max.y, cm.y);
				// }
				// return max;
				return LUtil.DEFAULT_MAX_SIZE;
			}

			public Vector2f preferredSize(Element parent) {
				Vector2f pref = new Vector2f(textPadding.x + textPadding.y, 0);
				for (Element el : parent.getElements()) {
					if (pref.x > 0)
						pref.x += table.headerGap;
					Vector2f cm = LUtil.getPreferredSize(el);
					pref.x += cm.x;
					pref.y = Math.max(pref.y, cm.y);
				}
				pref.y += textPadding.z + textPadding.w;
				return pref;
			}

			public void layout(Element container) {
				final Collection<Element> elements = container.getElements();

				Iterator<Element> el = elements.iterator();
				float x = textPadding.x;

				float h = 0;
				for (Table.TableColumn header : table.getColumns()) {
					if (el.hasNext()) {
						if (x > 0) {
							x += table.headerGap;
						}
						Element cell = el.next();
						final float width = header.getWidth();
						final int ps = LUtil.getPreferredHeight(cell);
						LUtil.setPosition(cell, x, textPadding.z);
						x += width;
						h = Math.max(h, ps);
					} else {
						break;
					}
				}
				el = elements.iterator();
				for (Table.TableColumn header : table.getColumns()) {
					if (el.hasNext()) {
						Element cell = el.next();
						final float width = header.getWidth();
						LUtil.setDimensions(cell, width, h);
					} else {
						break;
					}
				}
			}

			public void constrain(Element child, Object constraints) {
			}

			public void remove(Element child) {
			}
		}
	}

	static class TableContentLayout extends AbstractLayout {

		private Table table;

		public TableContentLayout(Table table) {
			this.table = table;
		}

		public Vector2f minimumSize(Element parent) {
			return null;
		}

		public Vector2f maximumSize(Element parent) {
			return null;
		}

		public Vector2f preferredSize(Element parent) {
			Vector4f textPadding = parent.getTextPaddingVec();
			float y = table.getHeightOfAllRows() + textPadding.z + textPadding.w + (table.headersVisible
					? (table.columnContainer == null ? 0 : table.columnContainer.getHeight()) + table.gap : 0);
			float x = 0;
			if (table.getColumnResizeMode() == ColumnResizeMode.NONE) {
				Vector2f pref = new Vector2f(
						x + textPadding.x + textPadding.y + ((table.getColumns().size() - 1) * table.headerGap), y);
				for (TableColumn c : table.getColumns()) {
					pref.x += c.getWidth();
				}
				return pref;
			} else {
				if (y > table.getScrollBoundsHeight())
					x -= table.gap + LUtil.getPreferredWidth(table.vScrollBar);
				return new Vector2f(x + table.getWidth() - table.textPadding.x - table.textPadding.y - textPadding.x
						- textPadding.y, y);
			}

		}

		public void layout(Element container) {
			Vector4f textPadding = container.getTextPaddingVec();
			float y = textPadding.z;
			if (table.headersVisible) {
				y += table.headerHeight;
			}
			Vector2f p = preferredSize(container);
			for (TableRow el : table.allRows) {
				Vector2f h = LUtil.getBoundPreferredSize(el);
				LUtil.setBounds(el, textPadding.x, y, p.x, h.y);
				el.updateClippingLayers();
				y += h.y;
			}
		}

		public void constrain(Element child, Object constraints) {
		}

		public void remove(Element child) {
		}
	}

	static class TableLayout extends ScrollPanelLayout {

		public void layout(Element childElement) {
			Table table = (Table) childElement;

			super.layout(childElement);

			table.sizeColumns();

			if (table.viewPortClipLayer == null)
				return;

			Vector4f outerPadding = table.getTextPaddingVec();
			Vector4f padding = table.getScrollableArea().getTextPaddingVec();

			if (table.isHeadersVisible()) {
				float hh = table.columnContainer.getHeight() + table.gap;
				LUtil.setBounds(table.viewPortClipLayer, padding.x + outerPadding.x, padding.z + hh + outerPadding.z,
						table.innerBounds.getWidth() - padding.x - padding.y,
						table.innerBounds.getHeight() - padding.w - padding.z - hh);

				LUtil.setBounds(table.headerClipLayer, padding.x + outerPadding.x, padding.z + outerPadding.z,
						table.innerBounds.getWidth() - padding.x - padding.y, hh - table.gap);
			} else {
				LUtil.setBounds(table.viewPortClipLayer, padding.x + outerPadding.x, padding.z + outerPadding.z,
						table.innerBounds.getWidth() - padding.x - padding.y,
						table.innerBounds.getHeight() - padding.w - padding.z);
			}

			table.repositionHighlights();

			table.sizeColumns();
		}

		public Vector2f minimumSize(Element parent) {
			Table table = (Table) parent;
			return table.getScrollContentLayout().minimumSize(parent);
		}

		public void constrain(Element child, Object constraints) {
		}

		public void remove(Element child) {
		}

		public Vector2f maximumSize(Element parent) {
			Table table = (Table) parent;
			return table.getScrollContentLayout().maximumSize(parent);
		}

		public Vector2f preferredSize(Element parent) {
			Table table = (Table) parent;
			Vector2f p = LUtil.getContainerPreferredDimensions(table.getScrollableArea());
			p.x += table.textPadding.x + table.textPadding.y;
			p.y += table.textPadding.w + table.textPadding.z;
			return p;
			// return table.getScrollContentLayout().preferredSize(parent);
		}
	}
}