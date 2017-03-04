package icetone.core.layout.loader;

import icetone.core.ElementManager;
import icetone.core.Layout;
import icetone.core.layout.FillLayout;

public class FillLayoutLayoutPart extends LayoutLayoutPart {

	public FillLayoutLayoutPart() {
	}

	public FillLayoutLayoutPart(String data) {
	}

	@Override
	public Layout createPart(ElementManager screen, LayoutContext context) {
		return new FillLayout();
	}

}
