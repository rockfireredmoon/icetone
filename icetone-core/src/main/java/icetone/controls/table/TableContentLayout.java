package icetone.controls.table;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.table.Table.ColumnResizeMode;
import icetone.core.AbstractGenericLayout;
import icetone.core.BaseElement;

public class TableContentLayout extends AbstractGenericLayout<BaseElement, Object> {
	private Table table;

	public TableContentLayout(Table table) {
		this.table = table;
	}

	@Override
	protected Vector2f calcPreferredSize(BaseElement parent) {
		Vector4f tableTextPadding = table.getAllPadding();
		Vector4f textPadding = parent.getAllPadding();
		float y = table.getHeightOfAllRows() + textPadding.z + textPadding.w + (table.headersVisible
				? (table.columnContainer == null ? 0 : table.columnContainer.getHeight()) + table.getIndent() : 0);
		float x = 0;
		if (table.getColumnResizeMode() == ColumnResizeMode.NONE) {
			Vector2f pref = new Vector2f(
					x + textPadding.x + textPadding.y + ((table.getColumns().size() - 1) * table.getIndent()), y);
			for (TableColumn c : table.getColumns()) {
				pref.x += c.getWidth();
			}
			return pref;
		} else {
			if (y > table.getScrollBoundsHeight())
				x -= table.getIndent() + table.getVerticalScrollBar().calcPreferredSize().x;
			return new Vector2f(
					x + table.getWidth() - tableTextPadding.x - tableTextPadding.y - textPadding.x - textPadding.y,
					y);
		}

		// Vector2f pref = new Vector2f(
		// x + textPadding.x + textPadding.y + ((table.getColumns().size() -
		// 1) * table.getIndent()), y);
		// for (TableColumn c : table.getColumns()) {
		// pref.x += c.calcPreferredSize().x;
		// }
		// return pref;

	}

	@Override
	protected void onLayout(BaseElement container) {
		Vector4f textPadding = container.getAllPadding();
		float y = textPadding.z;
		if (table.headersVisible) {
			y += table.calcHeaderHeight();
		}

		// Total width of all columns
		float tw = 0;
		for (TableColumn c : table.getColumns()) {
			if (tw != 0)
				tw += table.getIndent();
			tw += c.getWidth();
		}

		for (TableRow el : table.allRows) {
			Vector2f h = el.calcPreferredSize();
			el.setBounds(textPadding.x, y, tw, h.y);
			y += h.y;
		}

		// for (Element el : table.highlights) {
		// Highlight h = (Highlight) el.getElementUserData();
		// if (h.col == null) {
		// // Row highlight
		// el.setBounds(table.scrollableArea.getAllPadding().x,
		// h.row.getY(), table.scrollableArea.getWidth(),
		// h.row.getHeight());
		// } else {
		// // Cell highlight
		// el.setBounds(table.scrollableArea.getAllPadding().x +
		// h.col.getX(), h.row.getY(), h.col.getWidth(),
		// h.row.getHeight());
		// }
		// }
	}

}