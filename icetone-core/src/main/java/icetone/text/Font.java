package icetone.text;

import icetone.core.Element;

public class Font {

	public static <E extends Element> E bold(E element) {
		element.addStyleClass("default-bold");
		return element;
	}

	public static <E extends Element> E highlight(E element) {
		element.addStyleClass("default-highlight");
		return element;
	}
}
