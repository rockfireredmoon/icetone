package icetone.core.layout.loader;

import icetone.core.Element;

public interface LayoutRegisterable<T extends Element> {
	String getId();

	Class<T> getInstanceClass();
}
