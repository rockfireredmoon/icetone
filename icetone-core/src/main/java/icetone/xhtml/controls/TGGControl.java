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

import icetone.core.BaseElement;
import icetone.xhtml.XHTMLFSFont;
import icetone.xhtml.XHTMLRenderer;

public abstract class TGGControl implements TGGFormControl {

	protected FormControl control;
	protected BaseElement tgElement;
	protected final XHTMLRenderer renderer;
	protected final LayoutContext context;
	protected final BlockBox box;
	protected final CalculatedStyle style;

	public TGGControl(FormControl control, XHTMLRenderer parent, LayoutContext c, CalculatedStyle style,
			UserAgentCallback uac, BlockBox box) {
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
				tgElement.setEnabled(control.isEnabled());
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

	public final XHTMLFSFont getFont() {
		FSFont font = getStyle().getFSFont(getContext());
		if (font instanceof XHTMLFSFont) {
			return ((XHTMLFSFont) font);
		}
		return null;
	}

	@Override
	public BaseElement getUIElement() {
		return tgElement;
	}

	@Override
	public FormControl getFormControl() {
		return control;
	}

	@Override
	public int getIdealHeight() {
		return (int) getUIElement().calcPreferredSize().y;
	}

	@Override
	public int getIdealWidth() {
		return (int) getUIElement().calcPreferredSize().x;
	}

	protected abstract BaseElement createElement(FormControl control, XHTMLRenderer parent, LayoutContext c,
			CalculatedStyle style, UserAgentCallback uac);

	protected void configureCommonAttributes(CalculatedStyle style, FormControl control) {
		final XHTMLFSFont font = getFont();
		tgElement.setFontFamily(font.getFontFamily());
		tgElement.setFontSize(font.getSize2D());
		if (style.getColor() != null) {
			tgElement.setFontColor(convertColor(style.getColor()));
		}
		if (style.getBackgroundColor() != null) {
			tgElement.setDefaultColor(convertColor(style.getColor()));
		}
		if (!control.isEnabled()) {
			tgElement.setEnabled(false);
		}
		String title = control.getElement().getAttribute("title");
		if (title.length() != 0) {
			tgElement.setToolTipText(title);
		}
	}

	private static ColorRGBA convertColor(FSColor fsColor) {
		if (fsColor instanceof FSRGBColor) {
			FSRGBColor fsrgbcolor = ((FSRGBColor) fsColor);
			return new ColorRGBA(fsrgbcolor.getRed() / 255f, fsrgbcolor.getGreen() / 255f,
					fsrgbcolor.getBlue() / 255f, 1f);
		} else {
			throw new IllegalArgumentException("Don't currently support CMYK in Icetone rendering");
		}
	}
}
