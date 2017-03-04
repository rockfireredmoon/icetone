package icetone.core.layout.loader;

import icetone.controls.scrolling.ScrollPanel;
import icetone.core.ElementManager;

public class ScrollPanelLayoutPart extends AbstractElementLayoutPart<ScrollPanel> {
	public ScrollPanelLayoutPart() {
	}

	public ScrollPanelLayoutPart(String data) {
	}

	@Override
	protected void configureLayout(ScrollPanel el, LayoutContext ctx) {
		if (layout != null)
			el.setScrollContentLayout(layout.createPart(el.getScreen(), ctx));
	}

	@Override
	protected void addChildren(ElementManager screen, LayoutContext ctx, ScrollPanel thisEl) {
		for (AbstractElementLayoutPart<?> part : children) {
			thisEl.addScrollableContent(part.createPart(screen, ctx));
		}
	}

	@Override
	protected ScrollPanel createElementObject(ElementManager screen) {
		return new ScrollPanel(screen);
	}

}
