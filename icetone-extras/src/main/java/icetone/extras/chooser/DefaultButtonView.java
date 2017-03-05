package icetone.extras.chooser;

import icetone.controls.buttons.SelectableItem;
import icetone.core.BaseScreen;
import icetone.core.BaseScreen;

public class DefaultButtonView<I> extends AbstractButtonView<I> {

	public DefaultButtonView() {
		this(BaseScreen.get());
	}

	public DefaultButtonView(BaseScreen screen) {
		super("button-view", screen);
	}

	@Override
	protected void configureButton(SelectableItem button, I path) {
		super.configureButton(button, path);
		button.setText(getChooser().getResources().getLabel(path));
	}

}
