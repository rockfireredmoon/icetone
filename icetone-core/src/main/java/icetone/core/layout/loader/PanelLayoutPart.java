package icetone.core.layout.loader;

import icetone.controls.containers.Panel;
import icetone.core.ElementManager;

public class PanelLayoutPart extends AbstractElementLayoutPart<Panel> {

	public PanelLayoutPart() {
	}
	
	public PanelLayoutPart(String data) {
	}
	@Override
	protected Panel createElementObject(ElementManager screen) {
		return new Panel(screen);
	}

}
