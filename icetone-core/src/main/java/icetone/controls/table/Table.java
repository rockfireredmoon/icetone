/**
 * ICETONE - A GUI Library for JME3 based on a heavily modified version of 
 * Tonegod's 'Tonegodgui'.  
 * 
 * Copyright (c) 2013, t0neg0d
 * Copyright (c) 2016, Emerald Icemoon
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of the FreeBSD Project.
 */
package icetone.controls.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.jme3.input.KeyInput;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.scrolling.ScrollPanel;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Element;
import icetone.core.ElementContainer;
import icetone.core.Layout;
import icetone.core.Layout.LayoutType;
import icetone.core.ZOrderComparator;
import icetone.core.event.ChangeSupport;
import icetone.core.event.UIChangeEvent;
import icetone.core.event.UIChangeListener;
import icetone.core.event.keyboard.KeyboardUIEvent;

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
 * Cells are either strings, or you may use any {@link BaseElement}.
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
public class Table extends ScrollPanel {

	public enum ColumnResizeMode {

		AUTO_ALL, AUTO_FIRST, AUTO_LAST, NONE;
	}

	public class Highlight {
		TableColumn col;
		TableRow row;

		Highlight(TableRow row) {
			this.row = row;
		}

		Highlight(TableRow row, TableColumn col) {
			this.row = row;
			this.col = col;
		}
	}

	public enum SelectionMode {

		CELL, MULTIPLE_CELLS, MULTIPLE_ROWS, NONE, ROW;

		public boolean isEnabled() {
			return !this.equals(NONE);
		}

		public boolean isMultiple() {
			return this.equals(MULTIPLE_CELLS) || this.equals(MULTIPLE_ROWS);
		}

		public boolean isSingle() {
			return this.equals(ROW) || this.equals(CELL);
		}
	}

	protected final List<TableRow> allRows = new ArrayList<>();
	protected ChangeSupport<Table, Map<Integer, List<Integer>>> changeSupport;
	protected boolean collapseChildrenOnParentCollapse = true;
	protected final Element columnContainer;
	protected Table.ColumnResizeMode columnResizeMode = Table.ColumnResizeMode.AUTO_ALL;
	protected final List<TableColumn> columns = new ArrayList<TableColumn>();
	protected final BaseElement headerClipLayer;
	protected boolean headersVisible = true;
	// protected List<Element> highlights = new ArrayList<>();
	protected int notLeafCount;
	protected List<TableRow> rows = new ArrayList<>();
	protected Map<Integer, List<Integer>> selectedCells = new HashMap<>();
	protected List<Integer> selectedRows = new ArrayList<>();
	protected Table.SelectionMode selectionMode = Table.SelectionMode.ROW;
	protected boolean selectOnRightClick = true;
	protected boolean sortable;
	protected final BaseElement viewPortClipLayer;
	protected float visibleRowCount = 10;
	protected boolean addOddEvenStyles = true;
	private boolean sizingColumns;

	/**
	 * Creates a new instance of the Table control
	 * 
	 * @param screen The screen control the Element is to be added to
	 */
	public Table() {
		this(BaseScreen.get());
	}

	/**
	 * Creates a new instance of the Table control
	 * 
	 * @param screen The screen control the Element is to be added to
	 */
	public Table(BaseScreen screen) {
		super(screen);

		setLayoutManager(new TableLayout());
		setScrollContentLayout(new TableContentLayout(this));
		setIgnoreMouseButtons(false);

		// Mouse some mouse events bubble up (except scrolling)
		scrollableArea.setKeyboardFocusable(false);

		// Dedicated clip layer
		viewPortClipLayer = new BaseElement(screen);
		viewPortClipLayer.setAsContainerOnly();
		addElement(viewPortClipLayer);

		// Dedicated clip layer for header
		headerClipLayer = new BaseElement(screen);
		headerClipLayer.setAsContainerOnly();
		addElement(headerClipLayer);

		// A container for the column headers (we do our own sizing and
		// positioning for this)
		columnContainer = new Element(screen);
		columnContainer.setLayoutManager(new TableHeaderLayout());
		columnContainer.setStyleClass("columns");
		columnContainer.setAsContainerOnly();
		columnContainer.setVisibilityAllowed(headersVisible);
		columnContainer.addClippingLayer(headerClipLayer);

		// Watch for scrolling and adjust headers
		scrollableArea.onScrollEvent(evt -> sizeColumnContainer());

		addElement(columnContainer);

		onNavigationKey(evt -> {
			if (!selectionMode.equals(Table.SelectionMode.NONE)) {

				if (evt.getKeyCode() == KeyInput.KEY_SPACE) {
					if (!selectionMode.equals(Table.SelectionMode.NONE)) {
						if (evt.isPressed()) {
							List<TableRow> selRows = getSelectedRows();
							if (!selRows.isEmpty()) {
								selRows.get(0).setExpanded(!selRows.get(0).isExpanded());
							}
						}
						evt.setConsumed();
					}
				} else if (evt.getKeyCode() == KeyInput.KEY_A && evt.isCtrl()
						&& (selectionMode.isMultiple() || getRowCount() == 1)) {
					if (evt.isPressed()) {
						selectAll();
					}
					evt.setConsumed();
				} else {

					int newRow = -1;

					if (!selectionMode.equals(Table.SelectionMode.NONE)) {

						if (evt.getKeyCode() == KeyInput.KEY_LEFT) {
							if (evt.isPressed()) {
								newRow = selectLeft(evt);
							}
							evt.setConsumed();

						} else if (evt.getKeyCode() == KeyInput.KEY_RIGHT) {
							if (evt.isPressed()) {
								newRow = selectRight(evt);
							}
							evt.setConsumed();
						} else if (evt.getKeyCode() == KeyInput.KEY_DOWN) {
							if (evt.isPressed()) {
								newRow = selectDown(evt);
							}
							evt.setConsumed();
						} else if (evt.getKeyCode() == KeyInput.KEY_UP) {
							if (evt.isPressed()) {
								newRow = selectUp(evt);
							}
							evt.setConsumed();
						}
					}

					// If new row is selected, scroll to it
					if (evt.isPressed()) {
						if (newRow >= 0 && newRow < allRows.size()) {
							scrollToRow(newRow);
						}
					}
				}
			}
		});
	}

	public Table addChangeListener(UIChangeListener<Table, Map<Integer, List<Integer>>> listener) {
		if (changeSupport != null)
			changeSupport = new ChangeSupport<>();
		changeSupport.addListener(listener);
		return this;
	}

	/**
	 * Add a new column.
	 * 
	 * @param columnName column name
	 */
	public TableColumn addColumn(String columnName) {
		TableColumn header = new TableColumn(this, screen);
		header.setText(columnName);
		addColumn(header);
		return header;
	}

	/**
	 * Add a new column control. Using this as opposed the simple string varient
	 * allows custom controls to be used for the header.
	 * 
	 * @param column column
	 */
	public TableColumn addColumn(TableColumn column) {
		column.setStyleClass("no-sort");
//		column.addClippingLayer(columnContainer);
		columns.add(column);
		column.setIsSortable(sortable);
		columnContainer.addElement(column);
		// if (column.getDimensions().equals(Vector2f.ZERO)) {
		// column.sizeToContent();
		// }
		reconfigureHeaders();
		layoutChildren();
		return column;
	}

	/**
	 * Convenience method to add a single with a single column. Useful when using
	 * the table as a list.
	 * 
	 * @param label cell label
	 * @param value cell and row value
	 * @return row
	 */
	public TableRow addListRow(String label, Object value) {
		if (columns.size() != 1) {
			throw new IllegalArgumentException(
					"May only use this method if the table is configured to have a single column");
		}
		TableRow r = new TableRow(screen, this, value);
		r.addCell(label, value);
		addRow(r);
		return r;
	}

	/**
	 * Adds a TableRow to the Table and calls {@link #pack()} to recalculate layout.
	 * 
	 * @param row row
	 */
	public int addRow(TableRow row) {
		// this.getVerticalScrollBar().hide();
		this.rows.add(row);
		dirtyLayout(false, LayoutType.items);
		layoutChildren();
		return rows.size() - 1;
	}

	/**
	 * Adds specific cells of the specified row to the list of selected indexes
	 * 
	 * @param rowIndex    row index
	 * @param columnIndex column
	 */
	public void addSelectedCellIndexes(Integer rowIndex, Integer... columnIndexes) {
		if (columnIndexes.length == 0) {
			throw new IllegalArgumentException("Must supply at least one column index.");
		}
		Map<Integer, List<Integer>> was = getCellSelection();
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
		fireChanged(was);
	}

	/**
	 * Adds all cells of the specified row to the list of selected indexes
	 * 
	 * @param row row index
	 */
	public void addSelectedRowIndex(Integer row) {
		Map<Integer, List<Integer>> was = getCellSelection();
		selectedCells.remove(row);
		if (!selectedRows.contains(row) && row > -1) {
			selectedRows.add(row);
		}
		createHighlights();
		fireChanged(was);
	}

	public Table onChanged(UIChangeListener<Table, Map<Integer, List<Integer>>> listener) {
		if (changeSupport == null)
			changeSupport = new ChangeSupport<>();
		changeSupport.bind(listener);
		return this;
	}

	/**
	 * Get the height of the visible area of the content.
	 * 
	 * @return viewport height
	 */
	@Override
	public float getViewportHeight() {
		return super.getViewportHeight() - (headersVisible ? (calcHeaderHeight() + getIndent()) : 0);
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
	 * Expand all rows leading up to the one provided.
	 * 
	 * @param row row to expand
	 */
	public void expandRow(TableRow row) {
		TableRow p = row;
		while (p != null) {
			p.setExpanded(true);
			p = p.getParentRow();
		}
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
	 * Get the columns.
	 * 
	 * @return columns
	 */
	public List<TableColumn> getColumns() {
		return columns;
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
	 * Get the co-ordinates of the last selected cell. First element in array is the
	 * row, the second is the column. <code>null</code> will be returned if nothing
	 * is selected.
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
	 * Returns the TableRow at the specified index
	 * 
	 * @param index int
	 * @return TableRow
	 */
	public TableRow getRow(int index) {
		checkAllRows();
		if (index >= 0 && index < allRows.size()) {
			return allRows.get(index);
		} else {
			return null;
		}
	}

	protected void checkAllRows() {
		if (allRows.isEmpty() && !rows.isEmpty()) {
			rebuildAllRows();
		}
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
	public List<TableRow> getRows() {
		return this.rows;
	}

	/**
	 * Get the co-ordinates of the first selected cell. First element in array is
	 * the row, the second is the column. <code>null</code> will be returned if
	 * nothing is selected.
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
	 * Returns a List containing all the <srong>value</strong> attributes of all the
	 * rows that correspond to the list of selectedIndexes. This can be useful for
	 * taking a snapshot of the current selection, adjusting the table somehow (that
	 * would destroy selection), then resetting the selection.
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
	 * Returns the first (or only) row in the list of those selected
	 * 
	 * @return int
	 */
	public TableRow getSelectedRow() {
		checkAllRows();
		if (allRows.isEmpty() || selectedRows.isEmpty()) {
			return null;
		} else {
			return allRows.get(selectedRows.get(0));
		}
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
	 * Returns the entire list of selected indexes
	 * 
	 * @return List<Integer>
	 */
	public List<Integer> getSelectedRowIndexes() {
		return this.selectedRows;
	}

	/**
	 * Returns a List containing all ListItems corresponding to the list of
	 * selectedIndexes
	 * 
	 * @return List<ListItem>
	 */
	public List<TableRow> getSelectedRows() {
		List<TableRow> ret = new ArrayList<>();
		for (Integer i : selectedRows) {
			ret.add(getRow(i));
		}
		return ret;
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
	 * Get the number of visible rows used to calculate the preferred size.
	 * 
	 * @return visible row count
	 */
	public float getVisibleRowCount() {
		return visibleRowCount;
	}

	/**
	 * Inserts a new row at the provided index and calls {@link #pack()} to
	 * recalculate layout. See
	 * {@link #insertRow(int, icetone.controls.lists.Table.TableRow, boolean) } for
	 * an explanation of the impact of always packing when you insert items.
	 * 
	 * @param index The index to insert into
	 * @param row   The row to insert
	 */
	public void insertRow(int index, TableRow row) {
		insertRow(index, row, true);
	}

	/**
	 * Inserts a new row at the provided index and optionally calls {@link #pack() }
	 * to recalculate layout. Note, if you have lots of rows to insert, it is much
	 * faster to insert them all, then call {@link #pack() } once when you are done.
	 * 
	 * @param index The index to insert into
	 * @param row   The row to insert
	 * @param pack  recalculate layout
	 */
	public void insertRow(int index, TableRow row, boolean pack) {
		if (index >= 0 && index <= rows.size()) {
			this.rows.add(index, row);
			dirtyLayout(false, LayoutType.items);
			layoutChildren();
		}
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
	 * Get whether child rows are collapsed when a parent is collapsed.
	 * 
	 * @return collapse children on parent collapse
	 */
	public boolean isCollapseChildrenOnParentCollapse() {
		return collapseChildrenOnParentCollapse;
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
	 * Get whether rows / cells should be selected on right click.
	 * 
	 * @return select on right click
	 */
	public boolean isSelectOnRightClick() {
		return selectOnRightClick;
	}

	void pack() {
		rebuildAllRows();
		scrollableArea.invalidate();
		scrollableArea.removeAllChildren();
		// highlights.clear();
		for (int i = allRows.size() - 1; i >= 0; i--) {
			TableRow mi = allRows.get(i);
			mi.setVisibilityAllowed(true);
			if (addOddEvenStyles) {
				mi.invalidate();
				if (i % 2 == 0) {
					mi.removeStyleClass("odd");
					mi.addStyleClass("even");
				} else {
					mi.removeStyleClass("even");
					mi.addStyleClass("odd");
				}
				mi.validate();
			}
			addScrollableContent(mi);
		}

		createHighlights();
		columnContainer.dirtyLayout(true, LayoutType.boundsChange());
		dirtyScrollContent();
		scrollableArea.validate();
		layoutChildren();
		scrollableArea.scrollContent(null);
	}

	/**
	 * Remoe all columns (also removes all rows)
	 */
	public void removeAllColumns() {
		removeAllRows();
		for (TableColumn col : new ArrayList<TableColumn>(columns)) {
			removeColumn(col);
		}
		dirtyLayout(false, LayoutType.items);
		layoutChildren();
		// sizeColumns();
		// sizeScrollArea();
		// controlResizeHook();
	}

	/**
	 * Remove all rows.
	 */
	public void removeAllRows() {
		rows.clear();
		selectedRows.clear();
		selectedCells.clear();
		dirtyLayout(false, LayoutType.items);
		layoutChildren();
	}

	public Table removeChangeListener(UIChangeListener<Table, Map<Integer, List<Integer>>> listener) {
		if (changeSupport != null)
			changeSupport.removeListener(listener);
		return this;
	}

	/**
	 * Remove a table column
	 * 
	 * @param column
	 */
	public void removeColumn(TableColumn column) {
		int index = columns.indexOf(column);
		columnContainer.removeElement(column);
		columns.remove(column);
		for (TableRow row : rows) {
			row.removeColumn(index);
		}
		// sizeColumns();
		// layoutC
		dirtyLayout(false, LayoutType.items);
		layoutChildren();
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
	 * Remove the row at the provided index and calls {@link #pack()} to recalculate
	 * layout. See {@link #removeRow(int, boolean) } for an explanation of the
	 * impact of always packing when you remove items.
	 * 
	 * @param index int
	 */
	public void removeRow(int index) {
		removeRow(index, true);
	}

	/**
	 * Remove the row at the provided index and optionally calls {@link #pack() } to
	 * recalculate layout. Note, if you have lots of rows to remove, it is much
	 * faster to remove them all, then call {@link #pack() } once when you are done.
	 * 
	 * @param index int
	 * @param pack  recalculate layout
	 */
	public void removeRow(int index, boolean pack) {
		selectedCells.remove(index);
		selectedRows.remove((Integer) index);
		// this.getVerticalScrollBar().hide();
		if (!rows.isEmpty()) {
			if (index >= 0 && index < rows.size()) {
				rows.remove(index);
				dirtyLayout(false, LayoutType.items);
				layoutChildren();
			}
		}
	}

	/**
	 * Remove the row and calls {@link #pack()} to recalculate layout. See
	 * {@link #removeRow(int, boolean) } for an explanation of the impact of always
	 * packing when you remove items.
	 * 
	 * @param index int
	 * @param pack  recalculate layout
	 */
	public void removeRow(TableRow row) {
		removeRow(row, true);
	}

	/**
	 * Remove the row and optionally calls {@link #pack() } to recalculate layout.
	 * Note, if you have lots of rows to remove, it is much faster to insert them
	 * all, then call {@link #pack() } once when you are done.
	 * 
	 * @param index int
	 * @param pack  recalculate layout
	 */
	public void removeRow(TableRow row, boolean pack) {
		checkAllRows();
		int index = allRows.indexOf(row);
		if (index != -1) {
			removeRow(index, pack);
		}
	}

	/**
	 * Removes the specified cells from the list of selected indexes
	 * 
	 * @param index int
	 */
	public void removeSelectedCellIndexes(Integer rowIndex, Integer... columnIndexes) {
		if (columnIndexes.length == 0) {
			throw new IllegalArgumentException("Must supply at least one column index.");
		}
		Map<Integer, List<Integer>> was = getCellSelection();
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
		fireChanged(was);
	}

	/**
	 * Removes the specified index from the list of selected indexes
	 * 
	 * @param index int
	 */
	public void removeSelectedRowIndex(Integer index) {
		Map<Integer, List<Integer>> was = getCellSelection();
		selectedCells.remove(index);
		selectedRows.remove(index);
		createHighlights();
		fireChanged(was);
	}

	/**
	 * Scroll to a row.
	 * 
	 * @param rIndex row index
	 */
	public void scrollToRow(int rIndex) {

		// Get the top and bottom of the row
		if (rIndex >= 0 && rIndex < allRows.size()) {

			// Get the top and bottom of the viewport
			float adjust = headersVisible ? (calcHeaderHeight() + getIndent()) : 0;
			float sa = getScrollableAreaVerticalPosition();
			float top = sa + adjust;
			float bottom = top + getViewPortArea().y;

			TableRow row = allRows.get(rIndex);
			float rowTop = row.getY();
			float rowBottom = rowTop + row.getHeight();

			// Scroll up
			if (rowTop < top) {
				scrollYBy(rowTop - top);
			} else if (rowBottom > bottom) {
				scrollYBy(-(bottom - rowBottom));
			}
		}

	}

	/**
	 * Scroll to the first selected row.
	 */
	public void scrollToSelected() {
		scrollToRow(getSelectedRowIndex());
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

	/**
	 * Get whether to add 'odd' and 'even' style classes to each row.
	 * 
	 * @return add 'odd' and 'even' style classes to each row
	 */
	public boolean isAddOddEvenStyles() {
		return addOddEvenStyles;
	}

	/**
	 * Set whether to add 'odd' and 'even' style classes to each row.
	 * 
	 * @param addOddEvenStyles add 'odd' and 'even' style classes to each row
	 */
	public void setAddOddEvenStyles(boolean addOddEvenStyles) {
		this.addOddEvenStyles = addOddEvenStyles;
	}

	/**
	 * Set whether child rows are collapsed when a parent is collapsed.
	 * 
	 * @param collapseChildrenOnParentCollapse collapse children on parent collapse
	 */
	public void setCollapseChildrenOnParentCollapse(boolean collapseChildrenOnParentCollapse) {
		this.collapseChildrenOnParentCollapse = collapseChildrenOnParentCollapse;
	}

	/**
	 * Set the column resize mode.
	 * 
	 * @param columnResizeMode column resize mode
	 */
	public void setColumnResizeMode(Table.ColumnResizeMode columnResizeMode) {
		if (!Objects.equals(columnResizeMode, this.columnResizeMode)) {
			this.columnResizeMode = columnResizeMode;
			reconfigureHeaders();
			// sizeColumns();
			dirtyLayout(false, LayoutType.boundsChange());
			// innerBounds.dirtyLayout(false, LayoutType.all);
			// scrollableArea.dirtyLayout(false, LayoutType.all);
			layoutChildren();
		}
	}

	/**
	 * Set whether the headers are visible
	 * 
	 * @param headersVisible headers visible
	 */
	public void setHeadersVisible(boolean headersVisible) {
		if (this.headersVisible != headersVisible) {
			this.headersVisible = headersVisible;
			columnContainer.setVisibilityAllowed(headersVisible);
			if (headersVisible) {
				scrollYBy(-getIndent());
			}
			dirtyLayout(false, LayoutType.items);
			layoutChildren();
		}
	}

	/**
	 * Select an entire column
	 * 
	 * @param column column
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
	 * Sets the current selected row and colum indexes
	 * 
	 * @param index int
	 */
	public void setSelectedCellIndexes(Integer rowIndex, Integer... columnIndexes) {
		checkAllRows();
		Map<Integer, List<Integer>> was = getCellSelection();
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
		fireChanged(was);
	}

	/**
	 * Sets the current selected row index for single select Table
	 * 
	 * @param index int
	 */
	public void setSelectedRowIndex(Integer index) {
		checkAllRows();
		if (index < 0) {
			index = 0;
		} else {
			if (index > allRows.size() - 1) {
				index = allRows.size() - 1;
			}
		}

		if (selectedRows.size() == 1 && selectedRows.get(0) == index)
			return;
		Map<Integer, List<Integer>> was = getCellSelection();

		selectedRows.clear();
		selectedCells.clear();
		if (index > -1) {
			selectedRows.add(index);
		}
		createHighlights();
		fireChanged(was);
	}

	/**
	 * Sets the current list of selected indexes to the specified indexes
	 * 
	 * @param indexes
	 */
	public void setSelectedRowIndexes(Integer... indexes) {
		Map<Integer, List<Integer>> was = getCellSelection();
		selectedCells.clear();
		for (int i = 0; i < indexes.length; i++) {
			if (!selectedRows.contains(indexes[i])) {
				selectedRows.add(indexes[i]);
			}
		}
		createHighlights();
		fireChanged(was);
	}

	/**
	 * Set the row selection given a list of objects that may equal the
	 * <strong>value</strong> attibrute of each row. This can be useful for taking a
	 * snapshot of the current selection, adjusting the table somehow (that would
	 * destroy selection), then resetting the selection.
	 * 
	 * @param sel selected objects
	 * @see #getSelectedObjects()
	 */
	public void setSelectedRowObjects(List<?> sel) {
		checkAllRows();
		List<TableRow> selRows = new ArrayList<>();
		for (TableRow r : allRows) {
			if (!r.isLeaf() && !r.isExpanded())
				addSelectedRowObjects(r, sel, selRows);
			if (r.getValue() != null && sel.contains(r.getValue())) {
				selRows.add(r);
			}
		}
		for (TableRow r : selRows) {
			if (r.getParentRow() != null) {
				expandRow(r.getParentRow());
			}
		}
		setSelectedRows(selRows);
	}

	/**
	 * Set the selected table rows
	 * 
	 * @param rows selected rows
	 */
	public void setSelectedRows(List<TableRow> rows) {
		Map<Integer, List<Integer>> was = getCellSelection();
		selectedCells.clear();
		selectedRows.clear();
		for (TableRow r : rows) {
			int idx = allRows.indexOf(r);
			if (idx != -1) {
				selectedRows.add(idx);
			}
		}
		createHighlights();
		fireChanged(was);
	}

	protected void fireChanged(Map<Integer, List<Integer>> was) {
		if (changeSupport != null)
			changeSupport
					.fireEvent(new UIChangeEvent<Table, Map<Integer, List<Integer>>>(this, was, getCellSelection()));
	}

	protected Map<Integer, List<Integer>> getCellSelection() {
		if (selectedCells.isEmpty()) {
			Map<Integer, List<Integer>> m = new LinkedHashMap<>();
			for (Integer r : selectedRows) {
				m.put(r, Collections.emptyList());
			}
			return m;
		} else
			return new LinkedHashMap<>(selectedCells);
	}

	/**
	 * Set the selection mode. See {@link SelectionMode}.
	 * 
	 * @param selectionMode selection mode.
	 */
	public void setSelectionMode(Table.SelectionMode selectionMode) {
		this.selectionMode = selectionMode;
		if (navigationKeySupport == null && !selectionMode.isMultiple() && isKeyboardFocussed()) {
			defocus();
		}
		selectedRows.clear();
		selectedCells.clear();
		createHighlights();
	}

	/**
	 * Set whether rows / cells should be selected on right click.
	 * 
	 * @param selectOnRightClick select on right click
	 */
	public void setSelectOnRightClick(boolean selectOnRightClick) {
		this.selectOnRightClick = selectOnRightClick;
	}

	/**
	 * Set whether the table is sortable.
	 * 
	 * @param sortable sortable
	 */
	public void setSortable(boolean sortable) {
		this.sortable = sortable;
		for (TableColumn column : columns) {
			column.setIsSortable(sortable);
		}
	}

	/**
	 * Set the number of visible rows used to calculate the preferred size.
	 * 
	 * @param visibleRowCount visible row count
	 */
	public void setVisibleRowCount(float visibleRowCount) {
		this.visibleRowCount = visibleRowCount;
		layoutChildren();
	}

	/**
	 * Sort a column.
	 * 
	 * @param column
	 * @param ascending
	 */
	public void sort(TableColumn column, boolean ascending) {
		// Sort rows
		final int columnIndex = columns.indexOf(column);
		selectedRows.clear();
		Collections.sort(rows, new Comparator<TableRow>() {
			@SuppressWarnings("unchecked")
			@Override
			public int compare(TableRow o1, TableRow o2) {
				BaseElement e1 = new ArrayList<BaseElement>(o1.getElements()).get(columnIndex);
				BaseElement e2 = new ArrayList<BaseElement>(o2.getElements()).get(columnIndex);
				if (e1 instanceof Comparable) {
					return ((Comparable<Object>) e1).compareTo(e2);
				}
				return e1.toString().compareTo(e2.toString());
			}
		});
		if (!ascending) {
			Collections.reverse(rows);
		}

		// Set header button images
		for (TableColumn tc : columns) {
			if (tc == column) {
				if (ascending) {
					tc.removeStyleClass("no-sort descending");
					tc.addStyleClass("ascending");
				} else {
					tc.removeStyleClass("ascending no-sort");
					tc.addStyleClass("descending");
				}
			} else {
				tc.removeStyleClass("ascending descending");
				tc.addStyleClass("no-sort");
			}
		}

		dirtyLayout(false, LayoutType.items);
		layoutChildren();
	}

	public Table unbindChanged(UIChangeListener<Table, Map<Integer, List<Integer>>> listener) {
		if (changeSupport != null)
			changeSupport.unbind(listener);
		return this;
	}

	@Override
	protected Collection<BaseElement> getZSortedChildren() {
		// TODO this should be a feature of Element
		return ZOrderComparator.sortChildren(this);
	}

	protected void addSelectedRowObjects(TableRow row, List<?> sel, List<TableRow> selRows) {
		for (TableRow r : row.childRows) {
			if (r.getValue() != null && sel.contains(r.getValue())) {
				selRows.add(r);
			}
			if (!r.isLeaf() && !r.isExpanded()) {
				addSelectedRowObjects(r, sel, selRows);
			}
		}
	}

	protected float calcHeaderHeight() {
		return columnContainer.calcPreferredSize().y;
	}

	protected void createHighlights() {
		dirtyLayout(true, LayoutType.styling);
		layoutChildren();
	}

	protected float getHeightOfAllRows() {
		float h = 0;
		for (TableRow mi : rows) {
			h += mi.getTotalRowHeight();
		}
		return h;
	}

	protected float getTotalColumnWidth() {
		float x = 0;
		for (TableColumn col : columns) {
			x += col.getWidth();
		}
		return x;
	}

	protected Vector2f getViewPortArea() {
		Vector4f textPadding = getAllPadding();
		return new Vector2f(getWidth() - (textPadding.x + textPadding.y),
				getHeight() - (textPadding.z + textPadding.w) - (headersVisible ? calcHeaderHeight() : 0));
	}

	protected void rebuildAllRows() {
		// Build up a list of ALL the rows, drilling down into child rows if
		// there are any
		allRows.clear();
		for (TableRow mi : rows) {
			addRows(mi);
		}
	}

	protected int selectDown(KeyboardUIEvent evt) {
		int newRow = -1;
		switch (selectionMode) {
		case ROW:
		case MULTIPLE_ROWS:
			int selRow = getSelectedRowIndex();
			int lastRow = selectedRows.isEmpty() ? 0 : selectedRows.get(selectedRows.size() - 1);
			newRow = lastRow + 1;
			if (evt.isShift() && selectionMode.equals(Table.SelectionMode.MULTIPLE_ROWS)) {
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
			if (evt.isShift() && selectionMode.equals(Table.SelectionMode.MULTIPLE_CELLS)) {
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

	protected int selectLeft(KeyboardUIEvent evt) {
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
					if (selectionMode.equals(Table.SelectionMode.CELL) || !evt.isShift()) {
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
					if (evt.isShift() && selectionMode.equals(Table.SelectionMode.MULTIPLE_CELLS)) {
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

	protected int selectRight(KeyboardUIEvent evt) {
		checkAllRows();
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
					if (selectionMode.equals(Table.SelectionMode.CELL) || !evt.isShift()) {
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
					if (evt.isShift() && selectionMode.equals(Table.SelectionMode.MULTIPLE_CELLS)) {
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

	protected int selectUp(KeyboardUIEvent evt) {
		int selRow = getSelectedRowIndex();
		int lastRow = Math.max(0, selectedRows.isEmpty() ? 0 : selectedRows.get(selectedRows.size() - 1));
		int newRow = Math.max(0, lastRow - 1);
		switch (selectionMode) {
		case ROW:
		case MULTIPLE_ROWS:
			if (evt.isShift() && selectionMode.equals(Table.SelectionMode.MULTIPLE_ROWS)) {
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
			if (evt.isShift() && selectionMode.equals(Table.SelectionMode.MULTIPLE_CELLS)) {
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

	protected void sizeColumnContainer() {
		Vector4f scrollTextPaddingVec = scrollableArea.getAllPadding();
		Vector4f textPaddingVec = getAllPadding();
		columnContainer.setBounds(scrollableArea.getX() + textPaddingVec.x + scrollTextPaddingVec.x,
				textPaddingVec.z + scrollTextPaddingVec.z, scrollableArea.getWidth(), calcHeaderHeight());
	}

	protected void sizeColumns() {
		if (sizingColumns)
			return;
		sizingColumns = true;
		try {

			if (columnContainer == null)
				return;

			sizeColumnContainer();

			int zero = 0;
			for (TableColumn c : columns) {
				if (c.getWidth() == 0)
					zero++;
			}
			if (zero == columns.size()) {
				for (TableColumn c : columns) {
					float w = c.calcPreferredSize().x;
					if (w > 0)
						c.setWidth(w);
				}
			}

			float x = 0;
			float y = 0;
			int tw;
			if (!columns.isEmpty()) {
				float headerHeight = calcHeaderHeight();
				Vector4f textPaddingVec = scrollableArea.getAllPadding();
				float availableWidth = innerBounds.getWidth() - (textPaddingVec.x + textPaddingVec.y);
				switch (columnResizeMode) {
				case AUTO_ALL:
					tw = (Math.round(availableWidth / columns.size()));
					for (int i = 0; i < columns.size(); i++) {
						if (i > 0) {
							x += getIndent();
						}
						TableColumn header = columns.get(i);
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
						tw = (int) (availableWidth - (getIndent() * (columns.size() - 1)));
						for (int i = 1; i < columns.size(); i++) {
							tw -= columns.get(i).getWidth();
						}
						TableColumn header = columns.get(0);
						header.setPosition(x, y);
						header.setDimensions(tw, headerHeight);
						x += tw;
						for (int i = 1; i < columns.size(); i++) {
							x += getIndent();
							header = columns.get(i);
							header.setPosition(x, y);
							header.setHeight(headerHeight);
							x += header.getWidth();
						}
					}
					break;
				case AUTO_LAST:
					if (columns.size() > 0) {
						tw = (int) availableWidth;
						for (int i = 0; i < columns.size() - 1; i++) {
							final TableColumn header = columns.get(i);
							header.setPosition(x, y);
							x += header.getWidth();
							x += getIndent();
							header.setHeight(headerHeight);
						}
						TableColumn header = columns.get(columns.size() - 1);
						header.setPosition(x, y);
						header.setDimensions(tw - x, headerHeight);
					}
					break;
				case NONE:
					for (TableColumn header : columns) {
						header.setPosition(x, y);
						header.setHeight(headerHeight);
						x += header.getWidth();
						x += getIndent();
					}
					if (x >= columnContainer.getWidth()) {
						dirtyScrollContent();
					}
					break;
				}
			}

		} finally {
			sizingColumns = false;
		}

	}

	@Override
	@SuppressWarnings("unchecked")
	protected void layoutThis(ElementContainer<?, ?> container, LayoutType type) {
		if (type == LayoutType.items) {
			pack();
		} else {
			((Layout<ElementContainer<?, ?>, ?>) layoutManager).layout(container, type);
		}
	}

	private void addRows(TableRow row) {
		allRows.add(row);
		if (!row.isLeaf() && row.isExpanded()) {
			for (TableRow r : row.getChildRows()) {
				addRows(r);
			}
		}
	}

	private List<Integer> getAllColumnIndexes() {
		List<Integer> l = new ArrayList<>();
		for (int i = 0; i < columns.size(); i++) {
			l.add(i);
		}
		return l;
	}

	private void reconfigureHeaders() {
		for (TableColumn header : columns) {
			header.reconfigure();
		}
	}
}