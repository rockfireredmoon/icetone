package icetone.examples;

import com.jme3.app.SimpleApplication;

import icetone.controls.buttons.CheckBox;
import icetone.controls.containers.Frame;
import icetone.controls.lists.ComboBox;
import icetone.controls.table.Table;
import icetone.controls.table.TableCell;
import icetone.controls.table.TableRow;
import icetone.core.ElementContainer;
import icetone.core.BaseScreen;
import icetone.core.Element;
import icetone.core.layout.mig.MigLayout;
import icetone.extras.debug.GUIExplorerAppState;

/**
 * This example shows some examples of usage of the {@link Table} control.
 */
public class TableExample extends SimpleApplication {
	private static final int ROWS = 100;

	public static void main(String[] args) {
		TableExample app = new TableExample();
		app.start();
	}

	private Table table;
	private boolean treeMode;
	private boolean singleColumn;

	@Override
	public void simpleInitApp() {
		/*
		 * We are only using a single screen, so just initialise it (and you
		 * don't need to provide the screen instance to each control).
		 * 
		 * It is passed to the buildExample method in this way to help
		 * ExampleRunner so this example can be run from there and as a
		 * standalone JME application
		 */
		buildExample(BaseScreen.init(this));
		
		getStateManager().attach(new GUIExplorerAppState());

	}

	protected void buildExample(ElementContainer<?, ?> container) {

		table = new Table();
		table.setColumnResizeMode(Table.ColumnResizeMode.AUTO_FIRST);

		// Resize mode
		ComboBox<Table.ColumnResizeMode> resizeMode = new ComboBox<Table.ColumnResizeMode>(
				Table.ColumnResizeMode.values());
		resizeMode.setSelectedByValue(table.getColumnResizeMode());
		resizeMode.onChange(evt -> table.setColumnResizeMode(evt.getNewValue()));

		// Selection mode
		ComboBox<Table.SelectionMode> selectionMode = new ComboBox<Table.SelectionMode>(Table.SelectionMode.values());
		selectionMode.setSelectedByValue(table.getSelectionMode());
		selectionMode.onChange(evt -> table.setSelectionMode(evt.getNewValue()));

		//
		CheckBox sortable = new CheckBox();
		sortable.setChecked(table.getIsSortable());
		sortable.onChange(evt -> table.setSortable(evt.getNewValue()));
		sortable.setText("Sortable");

		//
		CheckBox headers = new CheckBox("Show headers");
		headers.setChecked(table.isHeadersVisible());
		headers.onChange(evt -> table.setHeadersVisible(evt.getNewValue()));

		final CheckBox debug = new CheckBox("Debug");
		debug.onChange(evt -> table.getScrollableArea().setTexture(evt.getNewValue() ? "/bgw.jpg" : null));

		//
		CheckBox tree = new CheckBox("Tree Mode");
		tree.onChange(evt -> {
			treeMode = evt.getNewValue();
			rebuild();
		});

		//
		CheckBox single = new CheckBox("Single Column");
		single.onChange(evt -> {
			singleColumn = evt.getNewValue();
			rebuild();
		});
		CheckBox keyboard = new CheckBox("Keyboard");
		keyboard.onChange(evt -> table.setEnableKeyboardNavigation(evt.getNewValue()));
		keyboard.setChecked(table.isEnableKeyboardNavigation());

		// Frame
		Frame frame1 = new Frame();
		frame1.setResizable(true);
		Element contentArea = frame1.getContentArea();

		contentArea.setLayoutManager(new MigLayout("wrap 3, fill", "[][][]", "[grow][shrink 0][shrink 0][shrink 0]"));
		contentArea.addElement(table, "growx,growy,span 3");

		contentArea.addElement(resizeMode);
		contentArea.addElement(selectionMode, "span 2");

		contentArea.addElement(debug);
		contentArea.addElement(keyboard);
		contentArea.addElement(sortable);

		contentArea.addElement(headers);
		contentArea.addElement(single);
		contentArea.addElement(tree);

		rebuild();
		frame1.sizeToContent();

		// Build the screen
		container.showElement(frame1);

	}

	private void rebuild() {
		table.invalidate();
		table.removeAllColumns();
		table.addColumn("Column 1");
		if (!singleColumn) {
			table.addColumn("Column 2");
			table.addColumn("Column 3");
		}
		for (int i = 0; i < ROWS; i++) {
			TableRow row = new TableRow(table);

			// Set that the row can have children
			row.setLeaf(!treeMode);

			row.addCell(String.format("Row %d, Cell 1", i), i);

			if (!singleColumn) {
				// row.addCell(String.format("Row %d, Cell 2", i), i, dopack);

				// Active
				TableCell c = new TableCell(i);
				c.setLayoutManager(new MigLayout("gap 0, ins 0, fill", "[]", "[]"));
				CheckBox active = new CheckBox();
				active.onChange(evt -> {
					boolean toggled = evt.getNewValue();
					for (TableRow cr : row.getChildRows()) {
						TableCell ce = cr.getCell(1);
						CheckBox cb = (CheckBox) ce.getChild(1);
						cb.runAdjusting(() -> cb.setChecked(toggled));
					}
				});
				// active.setBackgroundDimensions(new Vector2f(14, 14));
				c.addElement(active, "ax 50%");
				row.addElement(c);

				row.addCell(String.format("Row %d, Cell 3", i), i);
			}

			table.addRow(row);

			// Add some child rows
			if (treeMode) {
				for (int j = 0; j < 3; j++) {

					TableRow childRow = new TableRow(table);

					childRow.addCell(String.format("Child %d, Cell 1", j), j);
					if (!singleColumn) {
						childRow.addCell(String.format("Child %d, Cell 2", j), j);
						childRow.addCell(String.format("Child %d, Cell 3", j), j);
					}

					if (j == 1) {
						// For one of the child rows, lets add a further level
						childRow.setLeaf(false);

						TableRow childChildRow = new TableRow(table);

						childChildRow.addCell(String.format("Child-Child %d, Cell 1", 99), 99);
						if (!singleColumn) {
							childChildRow.addCell(String.format("Child-Child %d, Cell 2", 99), 99);
							childChildRow.addCell(String.format("Child-Child %d, Cell 3", 99), 99);
						}

						childRow.addRow(childChildRow);
					}

					row.addRow(childRow);
				}
			}
		}
		table.validate();
	}

}
