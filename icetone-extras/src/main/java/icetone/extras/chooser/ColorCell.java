package icetone.extras.chooser;

import com.jme3.math.ColorRGBA;

import icetone.core.ElementManager;
import icetone.core.Element;
import icetone.core.layout.FillLayout;

public class ColorCell extends Element {

	public ColorCell(ElementManager<?> screen, ColorRGBA col) {
		super(screen);
		setDefaultColor(col);
		setIgnoreMouse(false);
		setLayoutManager(new FillLayout());
	}

}