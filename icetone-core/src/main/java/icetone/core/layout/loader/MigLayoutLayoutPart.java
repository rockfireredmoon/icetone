package icetone.core.layout.loader;

import icetone.core.BaseScreen;
import icetone.core.Layout;
import icetone.core.layout.mig.MigLayout;

public class MigLayoutLayoutPart extends LayoutLayoutPart {
	
	private String constraints;
	private String column;
	private String row;

	public MigLayoutLayoutPart() {
	}

	public MigLayoutLayoutPart(String o) {
		// TODO why?
	}

	@Override
	public Layout createPart(BaseScreen screen, LayoutContext context) {
		return new MigLayout(screen, constraints, column, row);
	}

	public String getConstraints() {
		return constraints;
	}

	public void setConstraints(String constraints) {
		this.constraints = constraints;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getRow() {
		return row;
	}

	public void setRow(String row) {
		this.row = row;
	}

}
