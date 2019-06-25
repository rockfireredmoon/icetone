package icetone.core.layout.loader;

import icetone.core.BaseScreen;
import icetone.core.StyledContainer;

public class ContainerLayoutPart extends AbstractElementLayoutPart<StyledContainer> {
	public ContainerLayoutPart() {
	}

	public ContainerLayoutPart(String data) {
	}

	@Override
	protected StyledContainer createElementObject(BaseScreen screen) {
		return new StyledContainer(screen);
	}

}
