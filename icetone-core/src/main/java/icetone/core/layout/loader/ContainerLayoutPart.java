package icetone.core.layout.loader;

import icetone.core.StyledContainer;
import icetone.core.BaseScreen;

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
