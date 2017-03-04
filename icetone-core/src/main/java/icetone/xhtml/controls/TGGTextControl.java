package icetone.xhtml.controls;

import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.xhtml.FormControl;
import org.xhtmlrenderer.simple.xhtml.FormControlAdapter;
import org.xhtmlrenderer.simple.xhtml.controls.TextControl;

import icetone.controls.text.Password;
import icetone.controls.text.TextField;
import icetone.core.BaseElement;
import icetone.xhtml.XHTMLRenderer;

public class TGGTextControl extends TGGControl {

	private String prototypeText;

	public TGGTextControl(FormControl control, XHTMLRenderer parent, LayoutContext c, CalculatedStyle style,
			UserAgentCallback uac, BlockBox box) {
		super(control, parent, c, style, uac, box);
	}

	@Override
	protected BaseElement createElement(FormControl control, XHTMLRenderer parent, LayoutContext c, CalculatedStyle style,
			UserAgentCallback uac) {
		final TextControl tc = (TextControl) control;
		getPrototypeType(tc);
		TextField text;
		if (tc.isPassword()) {
			text = new Password(parent.getScreen());
		} else {
			text = new TextField(parent.getScreen());
		}
		text.onKeyboardPressed(evt -> text.runAdjusting(() -> tc.setValue(text.getText())));
		if (tc.isReadOnly()) {
			text.setEnabled(false);
		}

		text.setText(control.getInitialValue());
		text.setCharacterLength(tc.getSize());
		text.setMaxLength(tc.getMaxLength());

		tc.addFormControlListener(new FormControlAdapter() {
			@Override
			public void changed(FormControl control) {
				if (!text.isAdjusting()) {
					text.setText(control.getValue());
				}
			}
		});

		return text;
	}

	private void getPrototypeType(final TextControl tc) {
		StringBuilder str = new StringBuilder(tc.getSize());
		for (int i = 0; i < tc.getSize(); i++) {
			str.append('n');
		}
		if (tc.isMultiLine()) {
			for (int i = 1; i < tc.getRows(); i++) {
				str.append("\n");
			}
		}
		prototypeText = str.toString();
	}
}
