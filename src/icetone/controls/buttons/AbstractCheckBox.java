/**
 * ICETONE - A GUI Library for JME3 based on a heavily modified version of 
 * Tonegod's 'Tonegodgui'.  
 * 
 * Copyright (c) 2013, t0neg0d
 * Copyright (c) 2016, Emerald Icemoon
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of the FreeBSD Project.
 */
package icetone.controls.buttons;

import com.jme3.font.BitmapFont;
import com.jme3.font.LineWrapMode;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.text.AbstractTextLayout;
import icetone.controls.text.Label;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.Screen;
import icetone.core.event.MouseUIButtonEvent;
import icetone.core.layout.LUtil;
import icetone.effects.Effect;
import icetone.listeners.MouseButtonListener;
import icetone.listeners.MouseFocusListener;

/**
 *
 * @author t0neg0d
 */
public class AbstractCheckBox extends ButtonAdapter {

	protected ClickableLabel label;
	protected float labelFontSize;
	protected Vector2f checkDimensions;
	protected float gap = 0;
	protected Vector2f checkSize;

	protected AbstractCheckBox(ElementManager screen, String UID, Vector2f position, Vector2f dimensions,
			Vector4f resizeBorders, String defaultImg, String styleName) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);
		this.styleName = styleName;

		this.checkDimensions = screen.getStyle(styleName).getVector2f("defaultSize");

		// I wanted the ability to define gap between icon and text
		gap = screen.getStyle(styleName).getObject("gap") == null ? 0 : screen.getStyle(styleName).getFloat("gap");

		layoutManager = new CheckLayout();
		setMinDimensions(Vector2f.ZERO);
		setTextPaddingByKey(styleName, "textPadding");

		this.clearAltImages();
		this.removeEffect(Effect.EffectEvent.Hover);
		this.removeEffect(Effect.EffectEvent.Press);
		this.removeEffect(Effect.EffectEvent.LoseFocus);

		labelFontSize = screen.getStyle(styleName).getFloat("fontSize");

		label = new ClickableLabel(this, screen, UID + ":Label");
		label.setIgnoreMouse(false);

		label.setFontColor(screen.getStyle(styleName).getColorRGBA("fontColor"));
		label.setFontSize(labelFontSize);
		label.setTextAlign(BitmapFont.Align.valueOf(screen.getStyle(styleName).getString("textAlign")));
		label.setTextVAlign(BitmapFont.VAlign.valueOf(screen.getStyle(styleName).getString("textVAlign")));
		label.setTextWrap(LineWrapMode.valueOf(screen.getStyle(styleName).getString("textWrap")));

		this.setIsToggleButton(true);

		if (screen.getStyle(styleName).getString("hoverImg") != null) {
			setButtonHoverInfo(screen.getStyle(styleName).getString("hoverImg"),
					screen.getStyle(styleName).getColorRGBA("hoverColor"));
		}
		if (screen.getStyle(styleName).getString("pressedImg") != null) {
			setButtonPressedInfo(screen.getStyle(styleName).getString("pressedImg"),
					screen.getStyle(styleName).getColorRGBA("pressedColor"));
		}

		populateEffects(styleName);
		if (Screen.isAndroid()) {
			removeEffect(Effect.EffectEvent.Hover);
			removeEffect(Effect.EffectEvent.TabFocus);
			removeEffect(Effect.EffectEvent.LoseTabFocus);
		}
	}

	public void setCheckSize(Vector2f checkDimensions) {
		this.checkDimensions = checkDimensions;
		dirtyLayout(false);
		layoutChildren();
	}

	@Override
	public void setFont(String fontPath) {
		super.setFont(fontPath);
		if (label != null) {
			label.setFont(fontPath);
		}
	}

	@Override
	public void setFontSize(float fontSize) {
		super.setFontSize(fontSize);
		if (label != null) {
			label.setFontSize(fontSize);
		}
	}

	@Override
	public void setFontColor(ColorRGBA fontColor) {
		super.setFontColor(fontColor);
		if (label != null) {
			label.setFontColor(fontColor);
		}
	}

	/**
	 * Sets text for the check Label
	 * 
	 * @param text
	 */
	public void setLabelText(String text) {
		if (label.getParent() != null) {
			elementChildren.remove(label.getUID());
			label.removeFromParent();
		}

		label.setFontColor(screen.getStyle(styleName).getColorRGBA("fontColor"));
		label.setFontSize(labelFontSize);
		label.setTextAlign(BitmapFont.Align.valueOf(screen.getStyle(styleName).getString("textAlign")));
		label.setTextVAlign(BitmapFont.VAlign.valueOf(screen.getStyle(styleName).getString("textVAlign")));
		label.setTextWrap(LineWrapMode.valueOf(screen.getStyle(styleName).getString("textWrap")));

		label.setText(text);

		addChild(label);
	}

	/**
	 * Checks/unchecks the checkbox
	 * 
	 * @param isChecked
	 */
	public void setIsChecked(boolean isChecked) {
		setIsToggled(isChecked);
	}

	public void setIsCheckedNoCallback(boolean isChecked) {
		setIsToggledNoCallback(isChecked);
	}

	public Label getLabel() {
		return this.label;
	}

	/**
	 * Returns if the checkbox is checked/unchecked
	 * 
	 * @return boolean
	 */
	public boolean getIsChecked() {
		return this.getIsToggled();
	}

	@Override
	public Vector2f getPreferredDimensions() {
		return prefDimensions == null ? (getOrgDimensions().equals(LUtil.LAYOUT_SIZE) ? null : getOrgDimensions())
				: prefDimensions;
	}

	public class ClickableLabel extends Label implements MouseButtonListener, MouseFocusListener {
		Button owner;

		public ClickableLabel(Button owner, ElementManager screen, String UID) {
			super(screen, UID);
			this.owner = owner;
		}

		@Override
		public void onMouseButton(MouseUIButtonEvent evt) {
			MouseButtonEvent nEvt = new MouseButtonEvent(evt.getButtonIndex(), evt.isPressed(),
					(int) owner.getAbsoluteX(), (int) owner.getAbsoluteY());
			owner.onMouseButton(new MouseUIButtonEvent(nEvt, this, evt.getModifiers()));
		}

		@Override
		public void onGetFocus(MouseMotionEvent evt) {
			MouseMotionEvent nEvt = new MouseMotionEvent((int) owner.getAbsoluteX(), (int) owner.getAbsoluteY(), 0, 0,
					0, 0);
			owner.onGetFocus(nEvt);
		}

		@Override
		public void onLoseFocus(MouseMotionEvent evt) {
			MouseMotionEvent nEvt = new MouseMotionEvent(
					Screen.isAndroid() ? (int) screen.getTouchXY().x : (int) screen.getMouseXY().x,
					Screen.isAndroid() ? (int) screen.getTouchXY().y : (int) screen.getMouseXY().y, 0, 0, 0, 0);
			owner.onLoseFocus(nEvt);
		}
	}

	class CheckLayout extends AbstractTextLayout {

		public Vector2f minimumSize(Element parent) {
			Vector2f min = label.getParent() == null ? new Vector2f() : textSize(label);
			Vector2f checkSize = AbstractCheckBox.this.checkSize;
			if (checkSize == null) {
				checkSize = LUtil.getPreferredSizeFromTexture(parent);
			}
			if (checkSize != null) {
				min.x += checkSize.x;
				min.y = Math.max(min.y, checkSize.y);
			}
			return min;
		}

		public Vector2f preferredSize(Element parent) {
			Vector2f pref = new Vector2f();
			if (label.getParent() != null) {
				pref.set(LUtil.getPreferredTextSize(label));
				pref.x += gap;
			}
			Vector2f checkSize = checkDimensions;
			if (checkSize == null) {
				checkSize = LUtil.getPreferredSizeFromTexture(parent);
			}
			if (checkSize != null) {
				pref.x += checkSize.x;
				pref.y = Math.max(pref.y, checkSize.y);
			}
			return pref;
		}

		public void layout(Element childElement) {

			Vector2f checkSize = checkDimensions;
			if (checkSize == null) {
				checkSize = LUtil.getPreferredSizeFromTexture(childElement);
			}

			setActualDimensions(checkSize);
			setActualPosition(childElement.getTextPaddingVec().x + containerPosition.x,
					Math.round((childElement.getHeight() - checkSize.y) / 2f) + containerPosition.y);

			if (label != null && label.getText() != null && !label.getText().equals("")) {
				Vector2f labelSize = new Vector2f(
						childElement.getContainerDimensions().x - checkSize.x - gap - textPadding.y - textPadding.x,
						checkSize.y);
				LUtil.setBounds(label, checkSize.x + gap + textPadding.x, textPadding.z, labelSize.x, labelSize.y);
			}

		}

		public void remove(Element child) {
		}

		public void constrain(Element child, Object constraints) {
		}
	}

}
