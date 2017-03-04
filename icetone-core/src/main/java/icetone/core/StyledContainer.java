package icetone.core;

public class StyledContainer extends Element {
	{
		setAsContainerOnly();
		setIgnoreMouse(true);
	}

	public StyledContainer() {
		super();
	}

	public StyledContainer(ElementManager<?> screen, Layout<?, ?> layoutManager) {
		this(screen);
		setLayoutManager(layoutManager);
	}

	public StyledContainer(Layout<?, ?> layoutManager) {
		super();
		setLayoutManager(layoutManager);
	}

	public StyledContainer(ElementManager<?> screen) {
		super(screen);
	}

}
