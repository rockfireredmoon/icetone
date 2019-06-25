package icetone.xhtml.controls;

import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.xhtml.FormControl;
import org.xhtmlrenderer.simple.xhtml.FormControlAdapter;
import org.xhtmlrenderer.simple.xhtml.controls.TextControl;

import icetone.controls.text.PasswordField;
import icetone.controls.text.TextArea;
import icetone.controls.text.TextAreaScrollPanel;
import icetone.controls.text.TextField;
import icetone.controls.text.TextInput;
import icetone.core.BaseElement;
import icetone.xhtml.XHTMLRenderer;

public class XHTMLTextControl extends XHTMLControl {

	public XHTMLTextControl(FormControl control, XHTMLRenderer parent, LayoutContext c, CalculatedStyle style,
			UserAgentCallback uac, BlockBox box) {
		super(control, parent, c, style, uac, box);
	}

	@Override
	protected BaseElement createElement(FormControl control, XHTMLRenderer parent, LayoutContext c,
			CalculatedStyle style, UserAgentCallback uac) {
		final TextControl tc = (TextControl) control;
		BaseElement text;
		if (tc.isPassword()) {
			text = new PasswordField(parent.getScreen());
			((PasswordField) text)
					.onChange(evt -> evt.getSource().runAdjusting(() -> tc.setValue(evt.getSource().getText())));
			configureCommonTextField(tc, (TextField) text);
		} else if (tc.isMultiLine()) {
			text = new TextArea(parent.getScreen());
			((TextArea) text).setRows(tc.getRows());
			((TextArea) text)
					.onChange(evt -> evt.getSource().runAdjusting(() -> tc.setValue(evt.getSource().getText())));
			configureCommonTextField(tc, (TextArea) text);

			// Wrap in scroller
			text = new TextAreaScrollPanel((TextArea) text);
		} else {
			text = new TextField(parent.getScreen());
			((TextField) text)
					.onChange(evt -> evt.getSource().runAdjusting(() -> tc.setValue(evt.getSource().getText())));
			configureCommonTextField(tc, (TextField) text);
		}
		if (tc.isReadOnly()) {
			text.setEnabled(false);
		}
		text.setText(control.getInitialValue());

		tc.addFormControlListener(new FormControlAdapter() {
			@Override
			public void changed(FormControl control) {
				if (!tgElement.isAdjusting()) {
					tgElement.setText(control.getValue());
				}
			}
		});

		return text;
	}

	protected void configureCommonTextField(final TextControl tc, TextInput textField) {
		textField.setCharacterLength(tc.getSize());
		textField.setMaxLength(tc.getMaxLength() == -1 ? 0 : tc.getMaxLength());
	}

}
