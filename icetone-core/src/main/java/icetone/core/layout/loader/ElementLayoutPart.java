package icetone.core.layout.loader;

import icetone.core.BaseElement;
import icetone.core.ElementManager;

public class ElementLayoutPart extends AbstractElementLayoutPart<BaseElement> {
	public ElementLayoutPart() {
	}
	
	public ElementLayoutPart(String data) {
	}
	@Override
	protected BaseElement createElementObject(ElementManager screen) {
		return new BaseElement(screen);
	}

}
