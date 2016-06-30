package icetone.xhtml.controls;

import org.xhtmlrenderer.css.parser.FSColor;
import org.xhtmlrenderer.css.parser.FSRGBColor;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.FSFont;
import org.xhtmlrenderer.simple.xhtml.FormControl;
import org.xhtmlrenderer.simple.xhtml.FormControlAdapter;

import com.jme3.math.ColorRGBA;

import icetone.core.Element;
import icetone.xhtml.TGGFSFont;
import icetone.xhtml.TGGRenderer;
import icetone.xhtml.hacks.ElementHack;

public abstract class TGGControl implements TGGFormControl {

    protected FormControl control;
    protected Element tgElement;
    protected final TGGRenderer renderer;
    protected final LayoutContext context;
    protected final BlockBox box;
    protected final CalculatedStyle style;

    public TGGControl(FormControl control, TGGRenderer parent,
            LayoutContext c, CalculatedStyle style, UserAgentCallback uac,
            BlockBox box) {
        renderer = parent;
        this.style = style;
        this.context = c;
        this.box = box;
        this.control = control;

        tgElement = createElement(control, parent, c, style, uac);
        configureCommonAttributes(style, control);

        control.addFormControlListener(new FormControlAdapter() {
            @Override
            public void enabled(FormControl control) {
                tgElement.setIsEnabled(control.isEnabled());
            }
        });
    }

    public BlockBox getBox() {
        return box;
    }

    public LayoutContext getContext() {
        return context;
    }

    public CalculatedStyle getStyle() {
        return getBox().getStyle();
    }

    public final TGGFSFont getFont() {
        FSFont font = getStyle().getFSFont(getContext());
        if (font instanceof TGGFSFont) {
            return ((TGGFSFont) font);
        }
        return null;
    }

    public Element getUIElement() {
        return tgElement;
    }

    public FormControl getFormControl() {
        return control;
    }

    protected abstract Element createElement(FormControl control,
            TGGRenderer parent, LayoutContext c, CalculatedStyle style,
            UserAgentCallback uac);

    protected void configureCommonAttributes(CalculatedStyle style, FormControl control) {
        final TGGFSFont font = getFont();
        ElementHack.setFont(font.getFontPath(), tgElement);
        tgElement.setFontSize(font.getSize2D());
        tgElement.setScaleEW(false);
        tgElement.setScaleNS(false);
        tgElement.setDocking(null);
        if (style.getColor() != null) {
            tgElement.setFontColor(convertColor(style.getColor()));
        }
        if (style.getBackgroundColor() != null) {
            tgElement.getElementMaterial().setColor("Color", convertColor(style.getColor()));
        }
        if (!control.isEnabled()) {
            tgElement.setIsEnabled(false);
        }
        String title = control.getElement().getAttribute("title");
        if (title.length() != 0) {
            tgElement.setToolTipText(title);
        }
    }

    private static ColorRGBA convertColor(FSColor fsColor) {
        if (fsColor instanceof FSRGBColor) {
            FSRGBColor fsrgbcolor = ((FSRGBColor) fsColor);
            return new ColorRGBA((float) fsrgbcolor.getRed() / 255f, (float) fsrgbcolor.getGreen() / 255f,
                    (float) fsrgbcolor.getBlue() / 255f, 1f);
        } else {
            throw new IllegalArgumentException("Don't currently support CMYK in SWT rendering");
        }
    }
}
