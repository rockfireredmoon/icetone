package icetone.controls.table;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.scrolling.ScrollPanel.ScrollPanelLayout;

public class TableLayout extends ScrollPanelLayout<Table> {

	@Override
	protected Vector2f calcMaximumSize(Table parent) {
		return parent.getScrollableArea().calcMaximumSize();
	}

	@Override
	protected Vector2f calcMinimumSize(Table parent) {
		return parent.getScrollableArea().calcMinimumSize();
	}

	@Override
	protected Vector2f calcPreferredSize(Table parent) {
		Vector4f tableTextPadding = parent.getAllPadding();
		Vector2f p = parent.getScrollableArea().calcPreferredSize();
		p.x += tableTextPadding.x + tableTextPadding.y;
		p.y += tableTextPadding.w + tableTextPadding.z;
		return p;
	}

	@Override
	protected void onLayout(Table table) {

		super.onLayout(table);

		if (table.viewPortClipLayer == null)
			return;

		Vector4f outerPadding = table.getAllPadding();
		Vector4f padding = table.getScrollableArea().getAllPadding();

		if (table.isHeadersVisible()) {
			float hh = table.columnContainer.getHeight() + table.getIndent();
			table.viewPortClipLayer.setBounds(padding.x + outerPadding.x, padding.z + hh + outerPadding.z,
					table.getScrollBounds().getWidth() - padding.x - padding.y,
					table.getScrollBounds().getHeight() - padding.w - padding.z - hh);

			table.headerClipLayer.setBounds(padding.x + outerPadding.x, padding.z + outerPadding.z,
					table.getScrollBounds().getWidth() - padding.x - padding.y, hh - table.getIndent());
		} else {
			table.viewPortClipLayer.setBounds(padding.x + outerPadding.x, padding.z + outerPadding.z,
					table.getScrollBounds().getWidth() - padding.x - padding.y,
					table.getScrollBounds().getHeight() - padding.w - padding.z);
		}

		table.sizeColumns();
	}
}