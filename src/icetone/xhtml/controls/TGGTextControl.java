package icetone.xhtml.controls;

import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.xhtml.FormControl;
import org.xhtmlrenderer.simple.xhtml.FormControlAdapter;
import org.xhtmlrenderer.simple.xhtml.controls.TextControl;

import com.jme3.input.event.KeyInputEvent;

import icetone.controls.text.Password;
import icetone.controls.text.TextField;
import icetone.core.Element;
import icetone.xhtml.TGGRenderer;

public class TGGTextControl extends TGGControl {

    private String prototypeText;
    private boolean adjusting = false;

    public TGGTextControl(FormControl control, TGGRenderer parent,
            LayoutContext c, CalculatedStyle style, UserAgentCallback uac,
            BlockBox box) {
        super(control, parent, c, style, uac, box);
    }

    protected Element createElement(FormControl control,
            TGGRenderer parent, LayoutContext c, CalculatedStyle style,
            UserAgentCallback uac) {
        final TextControl tc = (TextControl) control;
        getPrototypeType(tc);
        TextField text;
        if (tc.isPassword()) {
            text = new Password(parent.getScreen()) {
                @Override
                public void controlKeyPressHook(KeyInputEvent evt, String text) {
                    adjusting = true;
                    tc.setValue(text);
                }
            };
        } else {
            text = new TextField(parent.getScreen()) {
                @Override
                public void controlKeyPressHook(KeyInputEvent evt, String text) {
                    adjusting = true;
                    tc.setValue(text);
                }
            };
        }
        if (tc.isReadOnly()) {
            text.setIsEnabled(false);
        }

        text.setText(control.getInitialValue());

        if (tc.getMaxLength() >= 0) {
            text.setMaxLength(tc.getMaxLength());
        }

        final TextField fText = text;
        tc.addFormControlListener(new FormControlAdapter() {
            @Override
            public void changed(FormControl control) {
                if (!adjusting) {
                    fText.setText(control.getValue());
                }
                adjusting = false;
            }
        });

        return text;
    }

    public int getIdealWidth() {
        return (int) (((TextField) getUIElement()).getFont().getLineWidth(prototypeText)
                * getFont().getFontScale());
    }

    public int getIdealHeight() {
        TextField text = (TextField) getUIElement();
        int lines = 1;
        if (((TextControl) control).isMultiLine()) {
            lines = ((TextControl) control).getRows();
        }
        int height = (int) ((float) text.getFont().getCharSet().getLineHeight() * getFont().getFontScale() * (float) lines);
        
        // Tonegod doesn't really have a way of getting the ideal size without actually
        // sizing it, so instead lets just add any borders instead. This should mostly
        // work for now
        height += text.borders.x + text.borders.w;
        
        return height;
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
