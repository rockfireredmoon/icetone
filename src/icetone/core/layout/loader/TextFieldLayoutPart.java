package icetone.core.layout.loader;

import icetone.controls.text.TextField;
import icetone.core.ElementManager;

public class TextFieldLayoutPart extends AbstractElementLayoutPart<TextField> {
	
	public TextFieldLayoutPart() {
	}
	
	public TextFieldLayoutPart(String data) {
	}

	@Override
	protected TextField createElementObject(ElementManager screen) {
		return new TextField(screen);
	}

}
