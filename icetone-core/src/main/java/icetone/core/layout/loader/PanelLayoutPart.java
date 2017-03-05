package icetone.core.layout.loader;

import icetone.controls.containers.Panel;
import icetone.core.BaseScreen;

public class PanelLayoutPart extends AbstractElementLayoutPart<Panel> {

	public PanelLayoutPart() {
	}
	
	public PanelLayoutPart(String data) {
	}
	@Override
	protected Panel createElementObject(BaseScreen screen) {
		return new Panel(screen);
	}

}
