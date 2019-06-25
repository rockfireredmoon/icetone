package icetone.extras.chooser;

import com.jme3.math.ColorRGBA;

import icetone.core.BaseScreen;
import icetone.core.Element;
import icetone.core.layout.FillLayout;

public class ColorCell extends Element {

	public ColorCell(BaseScreen screen, ColorRGBA col) {
		super(screen);
		setDefaultColor(col);
		setIgnoreMouse(false);
		setLayoutManager(new FillLayout());
	}
}