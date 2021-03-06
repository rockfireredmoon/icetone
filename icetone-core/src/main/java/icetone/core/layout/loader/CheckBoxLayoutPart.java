package icetone.core.layout.loader;

import icetone.controls.buttons.CheckBox;
import icetone.core.BaseScreen;

public class CheckBoxLayoutPart extends AbstractButtonLayout<CheckBox> { 

	private boolean checked;

	public CheckBoxLayoutPart() {
	}

	public CheckBoxLayoutPart(String data) {
	}

	@Override
	protected void configureThisElement(CheckBox el, LayoutContext ctx) {
		super.configureThisElement(el, ctx);
		if (checked)
			el.setChecked(true);
	}

	@Override
	protected CheckBox createElementObject(BaseScreen screen) {
		return new CheckBox(screen);
	}

}
