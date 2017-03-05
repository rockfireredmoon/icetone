package icetone.core;

public class StyledContainer extends Element {
	{
		setAsContainerOnly();
		setIgnoreMouse(true);
	}

	public StyledContainer() {
		super();
	}

	public StyledContainer(BaseScreen screen, Layout<?, ?> layoutManager) {
		this(screen);
		setLayoutManager(layoutManager);
	}

	public StyledContainer(Layout<?, ?> layoutManager) {
		super();
		setLayoutManager(layoutManager);
	}

	public StyledContainer(BaseScreen screen) {
		super(screen);
	}

}
