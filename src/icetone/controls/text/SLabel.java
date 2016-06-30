/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icetone.controls.text;

import java.util.List;

import com.jme3.math.Vector2f;

import icetone.core.Element;
import icetone.core.Screen;
import icetone.core.layout.AbstractLayout;
import icetone.core.layout.LUtil;

/**
 *
 * @author t0neg0d
 */
public class SLabel extends Element {
	public SLabel(String text) {
		this();
		setText(text);
	}

	public SLabel() {
		super(Screen.get(), "NEW", null, null, null, null);
		setLayoutManager(new LabelLayout());
		setIsResizable(false);
		setIgnoreMouse(true);
	}

	// // Load default font info
	// setTextAlign(BitmapFont.Align.valueOf(screen.getStyle("Label").getString("textAlign")));
	// setTextVAlign(BitmapFont.VAlign.valueOf(screen.getStyle("Label").getString("textVAlign")));
	// setTextWrap(LineWrapMode.valueOf(screen.getStyle("Label").getString("textWrap")));
	// setTextPadding(screen.getStyle("Label").getFloat("textPadding"));
	// setTextClipPadding(screen.getStyle("Label").getFloat("textPadding"));

	public List<ClippingDefine> getClippingLayers() {
		return clippingLayers;
	}

	class LabelLayout extends AbstractLayout {

		public Vector2f minimumSize(Element parent) {
			return null;
		}

		public Vector2f maximumSize(Element parent) {
			return null;
		}

		public Vector2f preferredSize(Element parent) {
			if (getText() != null && !getText().trim().equals("")) {
				return LUtil.getPreferredTextSize(parent);
			}
			return null;
		}

		public void layout(Element childElement) {
			updateTextElement();
		}

		public void remove(Element child) {
		}

		public void constrain(Element child, Object constraints) {
		}
	}
}
