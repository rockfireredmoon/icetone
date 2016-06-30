package icetone.xhtml.controls;

import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.xhtml.FormControl;
import org.xhtmlrenderer.simple.xhtml.FormControlAdapter;
import org.xhtmlrenderer.simple.xhtml.controls.CheckControl;

import com.jme3.input.event.MouseButtonEvent;

import icetone.controls.buttons.AbstractCheckBox;
import icetone.controls.buttons.CheckBox;
import icetone.controls.buttons.RadioButton;
import icetone.core.Element;
import icetone.xhtml.TGGRenderer;

public class TGGCheckControl extends TGGControl {

	public TGGCheckControl(FormControl control, TGGRenderer parent, LayoutContext c, CalculatedStyle style, UserAgentCallback uac,
			BlockBox box) {
		super(control, parent, c, style, uac, box);
	}

	protected Element createElement(FormControl control, TGGRenderer parent, LayoutContext c, CalculatedStyle style,
			UserAgentCallback uac) {
		final CheckControl cc = (CheckControl) control;
		AbstractCheckBox button;
		if (cc.isRadio()) {
			button = new RadioButton(parent.getScreen()) {
				@Override
				public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
					super.onButtonMouseLeftUp(evt, toggled);
					if (!toggled && cc.isRadio()) {
						setIsCheckedNoCallback(true);
					} else {
						cc.setSuccessful(toggled);
					}
				}
			};
		} else {
			button = new CheckBox(parent.getScreen()) {
				@Override
				public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
					super.onButtonMouseLeftUp(evt, toggled);
					if (!toggled && cc.isRadio()) {
						setIsCheckedNoCallback(true);
					} else {
						cc.setSuccessful(toggled);
					}
				}
			};
		}

		button.setText("");
		button.setIsCheckedNoCallback(cc.isSuccessful());
		final AbstractCheckBox fb = button;
		cc.addFormControlListener(new FormControlAdapter() {
			@Override
			public void successful(FormControl control) {
				fb.setIsCheckedNoCallback(control.isSuccessful());
			}
		});

		return button;
	}

	public int getIdealHeight() {
		return (int) renderer.getScreen().getStyle(((CheckControl) control).isRadio() ? "RadioButton" : "CheckBox")
				.getVector2f("defaultSize").y;
	}

	public int getIdealWidth() {
		return (int) renderer.getScreen().getStyle(((CheckControl) control).isRadio() ? "RadioButton" : "CheckBox")
				.getVector2f("defaultSize").x;
	}
}
