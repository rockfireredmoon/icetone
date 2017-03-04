package icetone.core;

public class Container extends BaseElement {
	{
		setAsContainerOnly();
	}

	public Container() {
		super();
	}

	public Container(ElementManager screen, Layout<?, ?> layoutManager) {
		super(screen, layoutManager);
	}

	public Container(ElementManager screen, String styleId, Size dimensions) {
		super(screen, styleId, dimensions);
	}

	public Container(ElementManager screen, Size dimensions) {
		super(screen, dimensions);
	}

	public Container(ElementManager screen) {
		super(screen);
	}

	public Container(Layout<?, ?> layoutManager) {
		super(layoutManager);
	}

}
