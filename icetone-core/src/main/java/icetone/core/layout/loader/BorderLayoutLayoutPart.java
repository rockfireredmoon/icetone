package icetone.core.layout.loader;

import icetone.core.ElementManager;
import icetone.core.Layout;
import icetone.core.layout.BorderLayout;

public class BorderLayoutLayoutPart extends LayoutLayoutPart {

	private int hgap;
	private int vgap;

	public BorderLayoutLayoutPart() {

	}

	public BorderLayoutLayoutPart(String o) {
		// TODO why?
	}

	@Override
	public Layout createPart(ElementManager screen, LayoutContext context) {
		return new BorderLayout(hgap, vgap);
	}

	public int getHgap() {
		return hgap;
	}

	public void setHgap(int hgap) {
		this.hgap = hgap;
	}

	public int getVgap() {
		return vgap;
	}

	public void setVgap(int vgap) {
		this.vgap = vgap;
	}

}
