package icetone.xhtml;

import java.awt.Point;

import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.layout.LayoutContext;

import icetone.core.BaseElement;
import icetone.xhtml.controls.XHTMLFormControl;

public class XHTMLFormControlReplacementElement implements ReplacedElement {

	private int width, height;
	private final XHTMLRenderer renderer;
	private final XHTMLFormControl control;
	private Point position = new Point(0, 0);

	public XHTMLFormControlReplacementElement(XHTMLFormControl control, XHTMLRenderer renderer) {
		this.control = control;
		this.renderer = renderer;
	}

	public XHTMLFormControl getControl() {
		return control;
	}

	@Override
	public void detach(LayoutContext c) {
	}

	@Override
	public int getIntrinsicHeight() {
		return height;
	}

	@Override
	public int getIntrinsicWidth() {
		return width;
	}

	public void calculateSize(LayoutContext c, CalculatedStyle style, int cssWidth, int cssHeight) {
		width = cssWidth;
		height = cssHeight;
		if (width < 0) {
			width = control.getIdealWidth();
		}
		if (height < 0) {
			height = control.getIdealHeight();
		}
		final BaseElement element = control.getUIElement();
		element.setDimensions(width, height);
	}

	@Override
	public Point getLocation() {
		return position;
	}

	@Override
	public void setLocation(int x, int y) {
		position.setLocation(x, y);
		control.getUIElement().setPosition(x, y);
	}

	@Override
	public boolean isRequiresInteractivePaint() {
		return true;
	}

	@Override
	public int getBaseline() {
		return 0;
	}

	@Override
	public boolean hasBaseline() {
		return false;
	}
}
