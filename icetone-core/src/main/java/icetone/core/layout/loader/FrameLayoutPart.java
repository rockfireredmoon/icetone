package icetone.core.layout.loader;

import icetone.controls.containers.Frame;
import icetone.core.ElementManager;

public class FrameLayoutPart extends AbstractElementLayoutPart<Frame> {

	private String title;
	private boolean closeable;
	private boolean resizable;
	private boolean movable;

	public boolean isMovable() {
		return movable;
	}

	public void setMovable(boolean movable) {
		this.movable = movable;
	}

	public boolean isResizable() {
		return resizable;
	}

	public void setResizable(boolean resizable) {
		this.resizable = resizable;
	}

	public boolean isCloseable() {
		return closeable;
	}

	public void setCloseable(boolean closeable) {
		this.closeable = closeable;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	protected Frame createElementObject(ElementManager<?> screen) {
		return new Frame(screen, closeable);
	}

	@Override
	public Frame createPart(ElementManager<?> screen, LayoutContext ctx) {
		Frame win = super.createPart(screen, ctx);
		return win;
	}

	@Override
	protected void configureLayout(Frame el, LayoutContext ctx) {
		if (layout != null)
			el.setContentLayoutManager(layout.createPart(el.getScreen(), ctx));
	}

	@Override
	protected void addChildren(ElementManager<?> screen, LayoutContext ctx, Frame thisEl) {
		for (AbstractElementLayoutPart<?> part : children) {
			thisEl.getContentArea().addElement(part.createPart(screen, ctx));
		}
	}

	@Override
	protected void configureThisElement(Frame el, LayoutContext ctx) {
		super.configureThisElement(el, ctx);
		el.setTitle(title);
		el.setResizable(resizable);
		el.setMovable(movable);
	}

}
