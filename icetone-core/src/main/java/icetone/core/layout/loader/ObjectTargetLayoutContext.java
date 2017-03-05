package icetone.core.layout.loader;

import icetone.core.BaseElement;
import icetone.core.BaseScreen;

public class ObjectTargetLayoutContext extends DefaultLayoutContent {

	private Object target;

	public ObjectTargetLayoutContext(BaseScreen screen, Object target) {
		super(screen);
		this.target = target;
	}

	@Override
	public void end(BaseElement root) {
		super.end(root);
		root.bind(target);
	}

}
