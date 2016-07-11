package icetone.controls.text;

import com.jme3.math.Vector2f;

import icetone.controls.util.XHTMLToolTip;
import icetone.core.Element;
import icetone.core.ToolTipProvider;

public final class XHTMLToolTipProvider implements ToolTipProvider {


	@Override
	public Element createToolTip(Vector2f mouseXY, Element el) {
		XHTMLToolTip tip = new XHTMLToolTip(el.getToolTipText(), el.getScreen());
		tip.sizeToContent();
		return tip;
	}
}