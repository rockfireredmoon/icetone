package icetone.core.layout.loader;

import icetone.core.Container;
import icetone.core.ElementManager;

public class ContainerLayoutPart extends AbstractElementLayoutPart<Container> {
	public ContainerLayoutPart() {
	}

	public ContainerLayoutPart(String data) {
	}

	@Override
	protected Container createElementObject(ElementManager screen) {
		return new Container(screen);
	}

}
