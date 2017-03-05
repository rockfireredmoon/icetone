package icetone.controls.buttons;

import icetone.core.BaseScreen;

public class SelectableItem extends AbstractToggleButton {

	public SelectableItem() {
		this(BaseScreen.get());
	}

	public SelectableItem(BaseScreen screen) {
		super(screen);
	}
}
