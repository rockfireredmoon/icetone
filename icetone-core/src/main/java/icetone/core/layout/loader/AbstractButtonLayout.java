package icetone.core.layout.loader;

import icetone.controls.buttons.Button;

public abstract class AbstractButtonLayout<B extends Button> extends AbstractElementLayoutPart<B> {
	private float intervalsPerSecond = 0;

	public AbstractButtonLayout() {
	}

	public AbstractButtonLayout(String data) {
	}

	public float getIntervalsPerSecond() {
		return intervalsPerSecond;
	}

	public void setIntervalsPerSecond(float intervalsPerSecond) {
		this.intervalsPerSecond = intervalsPerSecond;
	}

	@Override
	protected void configureThisElement(B el, LayoutContext ctx) {
		super.configureThisElement(el, ctx);
		if (intervalsPerSecond != 0)
			el.setInterval(intervalsPerSecond);
	}

}
