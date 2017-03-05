package icetone.controls.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.jme3.font.BitmapFont;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.buttons.Button;
import icetone.controls.table.Table.SelectionMode;
import icetone.core.AbstractGenericLayout;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Layout.LayoutType;
import icetone.core.PseudoStyles;
import icetone.core.Element;
import icetone.core.utils.MathUtil;
import icetone.css.CssProcessor.PseudoStyle;
import icetone.framework.core.AnimText;

public class TableCell extends Element implements Comparable<TableCell> {

	static class CellLayout extends AbstractGenericLayout<TableCell, Object> {

		@Override
		public Vector2f calcPreferredSize(TableCell parent) {
			Vector2f prefCell = new Vector2f();
			if (!parent.getActualChildren().isEmpty()) {
				for (BaseElement e : parent.getActualChildren()) {
					prefCell = MathUtil.max(prefCell, e.calcPreferredSize());
				}
			}
			if (parent.expanderButton != null) {
				Vector2f p = parent.expanderButton.calcPreferredSize();
				prefCell.x += p.x;
				prefCell.y = Math.max(prefCell.y, p.y);
			}
			prefCell.addLocal(parent.getTotalPadding());
			return prefCell;
		}

		@Override
		public void onLayout(TableCell container) {
			float x = 0, y = 0;
			for (BaseElement e : container.getActualChildren()) {
				Vector2f ps = e.calcPreferredSize();
				ps.x = Math.min(container.getWidth(), ps.x);
				if (container.valign.equals(BitmapFont.VAlign.Bottom)) {
					y = 0;
				} else if (container.valign.equals(BitmapFont.VAlign.Top)) {
					y = container.getHeight() - ps.y;
				} else if (container.valign.equals(BitmapFont.VAlign.Center)) {
					y = (int) ((container.getHeight() - ps.y) / 2);
				}

				if (container.halign.equals(BitmapFont.Align.Left)) {
					x = 0;
				} else if (container.halign.equals(BitmapFont.Align.Right)) {
					x = container.getWidth() - ps.x;
				} else if (container.halign.equals(BitmapFont.Align.Center)) {
					x = (container.getWidth() - ps.x) / 2;
				}

				e.setBounds(x, y, ps.x, ps.y);
			}
			// positionText();
			if (container.expanderButton != null) {
				float bx = x;
				TableRow row = (TableRow) container.getElementParent();
				Vector2f cellArrowSize = container.expanderButton.calcPreferredSize();
				if (row != null && container.getTextElement() != null && row.getElements().indexOf(container) == 0) {
					bx = (row.table.notLeafCount > 0 ? cellArrowSize.x : 0)
							+ ((container.getDepth(row) - 1) * cellArrowSize.x);
				}
				container.expanderButton.setBounds(bx, (container.getHeight() - cellArrowSize.y) / 2f, cellArrowSize.x,
						cellArrowSize.y);
			}
		}

		@Override
		protected Vector4f calcTextOffset(TableCell element, AnimText textElement, Vector4f textPadding) {
			Vector4f to = super.calcTextOffset(element, textElement, textPadding);
			final TableRow row = (TableRow) element.getElementParent();
			if (row != null) {
				final int cellIndex = row.getElements().indexOf(element);
				if (cellIndex == 0) {
					int depth = element.getDepth(row);
					Vector2f cellArrowSize;
					TableCell expanderCell;
					if (row.getParentRow() == null)
						expanderCell = element;
					else
						expanderCell = ((TableCell) row.getParentRow().getElements().get(0));
					cellArrowSize = expanderCell.expanderButton == null ? Vector2f.ZERO
							: expanderCell.expanderButton.calcPreferredSize();
					float tx = (row.table.notLeafCount > 0 ? cellArrowSize.x : 0) + (depth * cellArrowSize.x);
					to = to.clone();
					to.x += tx;
				}
			}
			return to;
		}

	}

	protected Button expanderButton;
	protected BitmapFont.Align halign = BitmapFont.Align.Center;
	protected BitmapFont.VAlign valign = BitmapFont.VAlign.Center;
	Object value;

	/**
	 * Constructor for cell with no text (you probably want to {@link #addChild}
	 * instead)
	 * 
	 * @param screen
	 *            screen
	 * @param value
	 *            arbitrary value to associate with cell
	 */
	public TableCell(BaseScreen screen, Object value) {
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
	public TableCell(BaseScreen screen, String label, Object value) {
		super(screen, null, Vector2f.ZERO, null);
		init(label, value);
	}

	/**
	 * Constructor for cell with no text (you probably want to {@link #addChild}
	 * instead)
	 * 
	 * @param screen
	 *            screen
	 * @param value
	 *            arbitrary value to associate with cell
	 */
	public TableCell(Object value) {
		this(BaseScreen.get(), value);
	}

	@Override
	public PseudoStyles getPseudoStyles() {
		PseudoStyles pseudoStyles = super.getPseudoStyles();
		if (isSelected()) {
			final TableRow row = (TableRow) getElementParent();
			if (row != null && row.table != null && (row.table.selectionMode == SelectionMode.MULTIPLE_CELLS
					|| row.table.selectionMode == SelectionMode.CELL)) {
				pseudoStyles = PseudoStyles.get(pseudoStyles).addStyle(PseudoStyle.active);
			}
		}
		return pseudoStyles;
	}

	public boolean isSelected() {
		final TableRow row = (TableRow) getElementParent();
		if (row != null && row.table != null) {
			List<Integer> selCells = row.table.selectedCells.get(row.getAllRowIndex());
			return selCells == null ? false : selCells.contains(getCellIndex());
		}
		return false;

	}

	public int getCellIndex() {
		final TableRow row = (TableRow) getElementParent();
		return row != null ? row.getElements().indexOf(this) : -1;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int compareTo(TableCell o) {
		if (value instanceof Comparable && o.value instanceof Comparable) {
			Object o1 = value;
			Object o2 = o.value;
			if (!Objects.equals(o1.getClass(), o2.getClass())) {
				o1 = String.valueOf(o1);
				o2 = String.valueOf(o2);
			}

			return ((Comparable<Object>) o1).compareTo(o2);
		}
		return toString().compareTo(o.toString());
	}

	@Override
	public TableCell clone() {
		cloning.set(cloning.get() + 1);
		try {
			TableCell cell = new TableCell(screen, value);
			configureClone(cell);
			cell.halign = halign;
			cell.valign = valign;
			;
			if (expanderButton != null)
				cell.expanderButton = expanderButton.clone();
			return cell;
		} finally {
			cloning.set(cloning.get() - 1);
		}
	}

	/**
	 * Get the button element used for expanding the cells row. Will only be
	 * available on the first column and if the row contains non-leaf children.
	 * 
	 * @return expander button
	 */
	public Button getExpanderButton() {
		return expanderButton;
	}

	public BitmapFont.Align getHalign() {
		return halign;
	}

	public BitmapFont.VAlign getVAlign() {
		return valign;
	}

	public Object getValue() {
		return value;
	}

	public void setHAlign(BitmapFont.Align halign) {
		this.halign = halign;
		dirtyLayout(false, LayoutType.children, LayoutType.text);
		layoutChildren();
	}

	public void setVAlign(BitmapFont.VAlign valign) {
		this.valign = valign;
		dirtyLayout(false, LayoutType.children, LayoutType.text);
		layoutChildren();
	}

	protected List<BaseElement> getActualChildren() {
		List<BaseElement> l = new ArrayList<BaseElement>();
		for (BaseElement e : getElements()) {
			if (!e.equals(expanderButton)) {
				l.add(e);
			}
		}
		return l;
	}

	protected int getDepth(TableRow row) {
		int depth = 0;
		if (row != null) {
			final int cellIndex = new ArrayList<BaseElement>(row.getElements()).indexOf(this);
			TableRow r = row;
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
		final TableRow row = (TableRow) getElementParent();
		if (row != null && !row.isLeaf()) {
			final int cellIndex = new ArrayList<BaseElement>(row.getElements()).indexOf(this);
			if (cellIndex == 0) {

				// Should we actually show a button?
				boolean shouldShow = row.table.notLeafCount > 0;
				boolean isShowing = expanderButton != null && expanderButton.getElementParent() != null;
				if (shouldShow) {
					if (expanderButton == null) {
						expanderButton = new Button(screen);
						expanderButton.onMousePressed(evt -> row.setExpanded(!row.isExpanded()));
//						expanderButton.addClippingLayer(row.table.viewPortClipLayer);
					}
					expanderButton.setStyleClass("expander " + (row.isExpanded() ? "expanded" : "collapsed"));
					if (expanderButton.getElementParent() == null)
						addElement(expanderButton);
					// positionText();
				} else if (!shouldShow && isShowing) {
					removeExpanderButton();
				}
			}
		}
	}

	private void init(String label, Object value) {
		layoutManager = new CellLayout();
		setText(label);
		this.value = value;
	}

	private void removeExpanderButton() {
		if (expanderButton != null) {
			removeElement(expanderButton);
		}
	}
}