package icetone.controls.table;

import icetone.controls.buttons.PushButton;
import icetone.core.ElementManager;
import icetone.core.Layout.LayoutType;

public class TableColumn extends PushButton {

	protected boolean resized;
	protected boolean resizing;
	protected Table table;
	private Boolean sort;
	private boolean sortable;

	public TableColumn(Table table, ElementManager<?> screen) {
		super(screen);
		init(table);
		setResizeN(false);
		setResizeS(false);
		onMouseReleased(evt -> {
			resizing = false;
			if (!resized && sortable) {
				if (sort == null) {
					sort = true;
				} else {
					sort = !sort;
				}
				table.sort(this, sort);
			}
		});
		onMousePressed(evt -> {
			resized = false;
			resizing = true;
		});
	}

	@Override
	public void controlResizeHook() {
		// This flag is to stop sort events when actually resizing
		resized = true;
		// if (resizing) {
		table.getScrollableArea().dirtyLayout(true, LayoutType.boundsChange());
		table.layoutChildren();
		table.sizeColumns();
		// }
	}

	public void setIsSortable(boolean sortable) {
		this.sortable = sortable;
		if (!sortable) {
			removeStyleClass("ascending descending");
			addStyleClass("no-sort");
			// icon
		}
	}

	private void init(Table table) {
		addStyleClass("no-sort");
		this.table = table;
		reconfigure();
	}

	void reconfigure() {
		setResizable(!table.columnResizeMode.equals(Table.ColumnResizeMode.AUTO_ALL));
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
}