package icetone.controls.buttons;

import icetone.core.ElementManager;
import icetone.core.BaseScreen;

public class SelectableItem extends AbstractToggleButton {

	public SelectableItem() {
		this(BaseScreen.get());
	}

	public SelectableItem(ElementManager<?> screen) {
		super(screen);
	}
}
