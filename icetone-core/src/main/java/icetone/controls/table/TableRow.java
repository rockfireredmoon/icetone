package icetone.controls.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.table.Table.SelectionMode;
import icetone.core.AbstractGenericLayout;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Layout.LayoutType;
import icetone.core.PseudoStyles;
import icetone.core.Element;
import icetone.css.CssProcessor.PseudoStyle;

public class TableRow extends Element {

	static class RowLayout extends AbstractGenericLayout<TableRow, Object> {

		@Override
		public void onLayout(TableRow container) {
			Vector4f textPadding = container.getAllPadding();
			final Collection<BaseElement> elements = container.getElements();

			float x = textPadding.x;
			float h = 0;
			for (BaseElement cell : elements) {
				final float ps = cell.calcPreferredSize().y;
				h = Math.max(h, ps);
			}
			Iterator<BaseElement> el = elements.iterator();
			for (TableColumn header : container.table.getColumns()) {
				if (el.hasNext()) {
					if (x > 0) {
						x += container.table.getIndent();
					}
					BaseElement cell = el.next();
					final float width = header.getWidth();
					cell.setBounds(x, textPadding.z, width, h);
					x += width;
				} else {
					break;
				}
			}
		}

		@Override
		protected Vector2f calcMinimumSize(TableRow parent) {
			Vector4f textPadding = parent.getAllPadding();
			Vector2f min = new Vector2f(textPadding.x + textPadding.y, 0);
			for (BaseElement el : parent.getElements()) {
				if (min.x > 0 && parent.table != null)
					min.x += parent.table.getIndent();
				Vector2f cm = el.calcMinimumSize();
				min.x += cm.x;
				min.y = Math.max(min.y, cm.y);
			}
			min.y += textPadding.z + textPadding.w;
			return min;
		}

		@Override
		protected Vector2f calcPreferredSize(TableRow parent) {
			Vector4f textPadding = parent.getAllPadding();
			Vector2f pref = new Vector2f(textPadding.x + textPadding.y, 0);
			for (BaseElement el : parent.getElements()) {
				if (pref.x > 0 && parent.table != null)
					pref.x += parent.table.getIndent();
				Vector2f cm = el.calcPreferredSize();
				pref.x += cm.x;
				pref.y = Math.max(pref.y, cm.y);
			}
			pref.y += textPadding.z + textPadding.w;
			return pref;
		}

	}

	protected List<TableRow> childRows = new ArrayList<TableRow>();
	protected boolean expanded;
	protected boolean leaf = true;
	protected int notLeafCount;
	protected TableRow parentRow;
	protected Table table;

	protected Object value;

	public TableRow() {
		this(BaseScreen.get());
	}

	public TableRow(BaseScreen screen) {
		this(screen, null);
	}

	public TableRow(Object value) {
		this(BaseScreen.get(), value);
	}

	public TableRow(BaseScreen screen, Object value) {
		this(screen, null, value);
	}

	public TableRow(BaseScreen screen, Table table) {
		this(screen, table, null);
	}

	public TableRow(BaseScreen screen, Table table, Object value) {
		super(screen, null, Vector2f.ZERO, null);

		layoutManager = new RowLayout();
		this.value = value;
		this.table = table;
		setMouseFocusable(true);
		setIgnoreMouseButtons(false);
		setIgnoreTouch(false);
		addClippingLayer(table.viewPortClipLayer, null);
		addMouseButtonListener(evt -> {
			if ((evt.isLeft() || (table.selectOnRightClick && evt.isRight())) && evt.isPressed())
				onMouseSelect(evt);
		});

	}

	public TableRow(Table table) {
		this(table.getScreen(), table, null);
	}

	@Override
	protected void onInitialized() {
		if (table == null) {
			BaseElement ep = getElementParent();
			if (ep instanceof Table)
				table = (Table) ep;
		}
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
	public TableCell addCell(String label, Object value) {
		TableCell tableCell;
		if (getElements().isEmpty())
			tableCell = new TableCell(screen, label, value);
		else {
			tableCell = (TableCell) getElements().get(0).clone();
			tableCell.removeClippingLayer(getElements().get(0));
			tableCell.setText(label);
			tableCell.value = value;
		}
		addElement(tableCell);
		return tableCell;
	}

	/**
	 * Adds a child TableRow to the Table and calls {@link #pack()} to
	 * recalculate layout. See
	 * {@link #addRow(icetone.controls.lists.Table.TableRow, boolean) } for an
	 * explanation of the impact of always packing when you add items.
	 * <p>
	 * Note you cannot add child rows unless the row is not a leaf. Use
	 * {@link #setLeaf(boolean) }.
	 * 
	 * @param row
	 *            row
	 */
	public int addRow(TableRow row) {
		return addRow(row, true);
	}

	/**
	 * Adds a child TableRow to this row and optionally calls {@link #pack() }
	 * to recalculate layout. Note, if you have lots of rows to add, it is much
	 * faster to add them all, then call {@link #pack() } once when you are
	 * done.
	 * <p>
	 * Note you cannot add child rows unless the row is not a leaf. Use
	 * {@link #setLeaf(boolean) }.
	 * 
	 * @param row
	 *            row
	 * @param pack
	 *            recalculate layout
	 */
	public int addRow(TableRow row, boolean pack) {
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
	 * Get the cell at the specified index.
	 * 
	 * @param i
	 *            index of cell
	 * @return cell
	 */
	public TableCell getCell(int i) {
		return (TableCell) new ArrayList<BaseElement>(getElements()).get(i);
	}

	/**
	 * Get all of the child rows (if any). Row must not be a leaf for this to be
	 * able to contain any child rows.
	 * 
	 * @return child rows
	 */
	public List<TableRow> getChildRows() {
		return childRows;
	}

	/**
	 * Get the parent row (if any). This will only be non-null once this (as a
	 * child row) has been added to the parent using
	 * {@link #addRow(icetone.controls.lists.Table.TableRow) } or similar.
	 * 
	 * @return parent row
	 */
	public TableRow getParentRow() {
		return parentRow;
	}

	public int getRowIndex() {
		return table.rows.indexOf(this);
	}

	public int getAllRowIndex() {
		return table.allRows.indexOf(this);
	}

	public Table getTable() {
		return table;
	}

	public float getTotalRowHeight() {
		float h = calcPreferredSize().y;
		if (expanded) {
			for (TableRow r : childRows) {
				h += r.getTotalRowHeight();
			}
		}
		return h;
	}

	public Object getValue() {
		return value;
	}

	public boolean isSelected() {
		return table != null && table.selectedRows.contains(getAllRowIndex());
	}

	public boolean isExpanded() {
		return expanded;
	}

	public boolean isLeaf() {
		return leaf;
	}

	/**
	 * Pack the row. The height of the row will be calculated.
	 */
	@Override
	public void onBeforeLayout() {
		// Now layout those cells within that height
		for (BaseElement child : getElements()) {
			((TableCell) child).setExpanderIcon();
		}
	}

	/**
	 * Remove the cell for a particular column index.
	 * 
	 * @param index
	 *            column index
	 */
	public void removeColumn(int index) {
		BaseElement el = new ArrayList<BaseElement>(getElements()).get(index);
		removeElement(el);
	}

	public void setExpanded(boolean expanded) {
		if (this.expanded && !expanded) {
			if (table.collapseChildrenOnParentCollapse)
				collapse();
			table.dirtyLayout(false, LayoutType.items);
			table.layoutChildren();
		} else if (expanded && !this.expanded) {
			this.expanded = expanded;
			table.dirtyLayout(false, LayoutType.items);
			table.layoutChildren();
		}
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
			for (BaseElement el : getElements()) {
				if (el instanceof TableCell) {
					((TableCell) el).setExpanderIcon();
				}
			}
		}
	}

	public void setValue(Object value) {
		this.value = value;
	}

	protected void onMouseSelect(MouseButtonEvent evt) {
		if (!table.isEnabled()) {
			return;
		}
		// table.focus();
		int currentRowIndex = table.allRows.indexOf(this);
		int currentColumnIndex = 0;
		int i = 0;
		for (BaseElement el : getElements()) {
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

	@Override
	public PseudoStyles getPseudoStyles() {
		PseudoStyles pseudoStyles = super.getPseudoStyles();
		if (isSelected()) {
			if (table != null
					&& (table.selectionMode == SelectionMode.MULTIPLE_ROWS || table.selectionMode == SelectionMode.ROW))
				pseudoStyles = PseudoStyles.get(pseudoStyles).addStyle(PseudoStyle.active);
		}
		return pseudoStyles;
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
		for (TableRow r : childRows) {
			r.collapse(true);
		}
	}

}