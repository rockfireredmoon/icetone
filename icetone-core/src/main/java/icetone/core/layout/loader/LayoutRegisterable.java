package icetone.core.layout.loader;

import icetone.core.BaseElement;

public interface LayoutRegisterable<T extends BaseElement> {
	String getId();

	Class<T> getInstanceClass();
}
