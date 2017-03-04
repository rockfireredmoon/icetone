package icetone.extras.layout;

import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.constructor.Constructor;

import icetone.core.layout.loader.FrameLayoutPart;
import icetone.core.layout.loader.LayoutPartRegisterable;

public class UILayoutPartRegisterables implements LayoutPartRegisterable {

	@Override
	public void register(Constructor contructor) {
//		contructor.addTypeDescription(new TypeDescription(FancyButtonLayoutPart.class, "!fancyButton"));
	}

}
