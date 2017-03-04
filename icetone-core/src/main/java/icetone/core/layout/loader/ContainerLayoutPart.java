package icetone.core.layout.loader;

import icetone.core.StyledContainer;
import icetone.core.ElementManager;

public class ContainerLayoutPart extends AbstractElementLayoutPart<StyledContainer> {
	public ContainerLayoutPart() {
	}

	public ContainerLayoutPart(String data) {
	}

	@Override
	protected StyledContainer createElementObject(ElementManager screen) {
		return new StyledContainer(screen);
	}

}
