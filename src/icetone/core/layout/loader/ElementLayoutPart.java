package icetone.core.layout.loader;

import icetone.core.Element;
import icetone.core.ElementManager;

public class ElementLayoutPart extends AbstractElementLayoutPart<Element> {
	public ElementLayoutPart() {
	}
	
	public ElementLayoutPart(String data) {
	}
	protected Element createElementObject(ElementManager screen) {
		return new Element(screen);
	}

}
