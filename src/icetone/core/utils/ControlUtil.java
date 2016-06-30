/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icetone.core.utils;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.text.LabelElement;
import icetone.core.Element;
import icetone.core.ElementManager;

/**
 *
 * @author t0neg0d
 */
public class ControlUtil {
	
	public static Element getContainer(ElementManager screen) {
		Element el = new Element(
			screen,
			UIDUtil.getUID(),
			Vector2f.ZERO,
			Vector2f.ZERO,
			Vector4f.ZERO,
			null
		);
		el.setAsContainerOnly();
		return el;
	}
	
	public static LabelElement getLabel(ElementManager screen, String text) {
		LabelElement te = new LabelElement(screen);
		te.setText(text);
		return te;
	}
	
}
