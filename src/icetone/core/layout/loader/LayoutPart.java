package icetone.core.layout.loader;

import icetone.core.ElementManager;

public interface LayoutPart<T> {

	T createPart(ElementManager screen, LayoutContext context);
}
