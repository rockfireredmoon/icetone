package icetone.core;

public class Container extends BaseElement {
	{
		setAsContainerOnly();
	}

	public Container() {
		super();
	}

	public Container(BaseScreen screen, Layout<?, ?> layoutManager) {
		super(screen, layoutManager);
	}

	public Container(BaseScreen screen, String styleId, Size dimensions) {
		super(screen, styleId, dimensions);
	}

	public Container(BaseScreen screen, Size dimensions) {
		super(screen, dimensions);
	}

	public Container(BaseScreen screen) {
		super(screen);
	}

	public Container(Layout<?, ?> layoutManager) {
		super(layoutManager);
	}

}
