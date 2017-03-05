package icetone.core.layout.loader;

import icetone.core.BaseScreen;

public interface LayoutPart<T> {

	T createPart(BaseScreen screen, LayoutContext context);
}
