package icetone.xhtml.controls;

import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.xhtml.FormControl;
import org.xhtmlrenderer.simple.xhtml.FormControlAdapter;
import org.xhtmlrenderer.simple.xhtml.controls.CheckControl;

import icetone.controls.buttons.AbstractToggleButton;
import icetone.controls.buttons.CheckBox;
import icetone.controls.buttons.RadioButton;
import icetone.core.BaseElement;
import icetone.xhtml.XHTMLRenderer;

public class TGGCheckControl extends TGGControl {

	public TGGCheckControl(FormControl control, XHTMLRenderer parent, LayoutContext c, CalculatedStyle style,
			UserAgentCallback uac, BlockBox box) {
		super(control, parent, c, style, uac, box);
	}

	@Override
	protected BaseElement createElement(FormControl control, XHTMLRenderer parent, LayoutContext c, CalculatedStyle style,
			UserAgentCallback uac) {
		final CheckControl cc = (CheckControl) control;
		AbstractToggleButton button;
		if (cc.isRadio()) {
			button = new RadioButton<Void>(parent.getScreen());
			button.onChange(evt -> {
				if (!evt.getSource().isAdjusting()) {
					if (!evt.getNewValue() && cc.isRadio()) {
						evt.getSource().runAdjusting(() -> evt.getSource().setState(true));
					} else {
						cc.setSuccessful(evt.getNewValue());
					}
				}
			});
		} else {
			button = new CheckBox(parent.getScreen());
			button.onChange(evt -> {
				if (!evt.getSource().isAdjusting()) {
					if (!evt.getNewValue() && cc.isRadio()) {
						evt.getSource().runAdjusting(() -> evt.getSource().setState(true));
					} else {
						cc.setSuccessful(evt.getNewValue());
					}
				}
			});
		}

		button.setText("");
		button.setState(cc.isSuccessful());
		cc.addFormControlListener(new FormControlAdapter() {
			@Override
			public void successful(FormControl control) {
				button.setState(control.isSuccessful());
			}
		});

		return button;
	}
}
