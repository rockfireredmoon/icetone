package icetone.core.layout;

import icetone.core.AbstractGenericLayout;
import icetone.core.ElementContainer;
import icetone.core.Layout;

public class DefaultLayout extends AbstractGenericLayout<ElementContainer<?,?>, Object> {

	public static final Layout SHARED_INSTANCE = new DefaultLayout();

}
