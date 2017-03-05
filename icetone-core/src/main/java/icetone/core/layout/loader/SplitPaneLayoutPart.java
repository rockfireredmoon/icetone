package icetone.core.layout.loader;

import icetone.controls.containers.SplitPanel;
import icetone.core.BaseScreen;
import icetone.core.Orientation;

public class SplitPaneLayoutPart extends AbstractElementLayoutPart<SplitPanel> {

	private Orientation orientation = Orientation.HORIZONTAL;
	private float dividerLocation;
	private boolean useOneTouchExpanders = true;

	public SplitPaneLayoutPart() {
	}

	public SplitPaneLayoutPart(String data) {
	}

	public Orientation getOrientation() {
		return orientation;
	}

	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
	}

	public float getDividerLocation() {
		return dividerLocation;
	}

	public void setDividerLocation(float dividerLocation) {
		this.dividerLocation = dividerLocation;
	}

	public boolean isUseOneTouchExpanders() {
		return useOneTouchExpanders;
	}

	public void setUseOneTouchExpanders(boolean useOneTouchExpanders) {
		this.useOneTouchExpanders = useOneTouchExpanders;
	}

	@Override
	protected void configureThisElement(SplitPanel el, LayoutContext ctx) {
		super.configureThisElement(el, ctx);
		if (dividerLocation > 0)
			el.setDividerLocationNoCallback(dividerLocation);
		if (!useOneTouchExpanders)
			el.setUseOneTouchExpanders(false);

	}

	@Override
	protected void addChildren(BaseScreen screen, LayoutContext ctx, SplitPanel thisEl) {
		if (children.size() != 2)
			throw new IllegalArgumentException(String.format("%s expects exactly 2 children.", SplitPanel.class));
		thisEl.setLeftOrTop(children.get(0).createPart(screen, ctx));
		thisEl.setRightOrBottom(children.get(1).createPart(screen, ctx));
	}

	@Override
	protected SplitPanel createElementObject(BaseScreen screen) {
		return new SplitPanel(screen, orientation);
	}

}
