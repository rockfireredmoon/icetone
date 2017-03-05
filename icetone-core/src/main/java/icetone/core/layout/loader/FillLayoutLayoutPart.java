package icetone.core.layout.loader;

import icetone.core.BaseScreen;
import icetone.core.Layout;
import icetone.core.layout.FillLayout;

public class FillLayoutLayoutPart extends LayoutLayoutPart {

	public FillLayoutLayoutPart() {
	}

	public FillLayoutLayoutPart(String data) {
	}

	@Override
	public Layout createPart(BaseScreen screen, LayoutContext context) {
		return new FillLayout();
	}

}
