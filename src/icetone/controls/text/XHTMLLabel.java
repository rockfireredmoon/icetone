package icetone.controls.text;

import org.xhtmlrenderer.extend.UserAgentCallback;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.core.ElementManager;
import icetone.core.Screen;
import icetone.xhtml.TGGUserAgent;
import icetone.xhtml.TGGXHTMLRenderer;

public class XHTMLLabel extends TGGXHTMLRenderer {

	public XHTMLLabel(ElementManager screen, String uid, Vector2f position, Vector2f dimension, Vector4f borders,
			String defaultImg, UserAgentCallback uac) {
		super(screen, uid, position, dimension, borders, defaultImg, uac);
		init();
	}

	public XHTMLLabel(ElementManager screen, String uid, Vector4f borders, String defaultImg, UserAgentCallback uac) {
		super(screen, uid, borders, defaultImg, uac);
		init();
	}

	public XHTMLLabel(ElementManager screen, UserAgentCallback uac) {
		super(screen, uac);
		init();
	}

	public XHTMLLabel(ElementManager screen, Vector2f position, Vector2f dimension, UserAgentCallback uac) {
		super(screen, position, dimension, uac);
		init();
	}

	public XHTMLLabel(ElementManager screen, Vector4f borders, String defaultImg, UserAgentCallback uac) {
		super(screen, borders, defaultImg, uac);
		init();
	}

	public XHTMLLabel(String text) {
		super(Screen.get());
		init();
		setText(text);
	}

	public XHTMLLabel(UserAgentCallback uac) {
		super(uac);
		init();
	}

	public XHTMLLabel(ElementManager screen) {
		super(screen, Vector4f.ZERO, null, new TGGUserAgent(screen));
		init();
	}

	public XHTMLLabel(String text, ElementManager screen) {
		super(screen, Vector4f.ZERO, null, new TGGUserAgent(screen));
		setText(text);
		init();
	}

	@Override
	public void setText(String text) {
		setDocumentFromString(wrapTextInXHTML(text, getFontColor()), "label://" + getUID());
		if (getElementParent() != null) {
			getElementParent().dirtyLayout(true);
			getElementParent().layoutChildren();
		}
	}

	public static String wrapTextInXHTML(String text, ColorRGBA col) {
		final StringBuilder bui = new StringBuilder();
		bui.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		bui.append("<!DOCTYPE html>\n");
		bui.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
		bui.append(String.format(
				"<body style=\"background-color: inherit; color: #%02x%02x%02x; text-align: center;\">\n",
				(int) (col.getRed() * 255), (int) (col.getGreen() * 255), (int) (col.getBlue() * 255)));
		bui.append(text);
		bui.append("</body>\n");
		bui.append("</html>\n");
		System.out.println(bui.toString());
		return bui.toString();
	}

	private void init() {
		setVerticalScrollBarMode(ScrollBarMode.Never);
		setHorizontalScrollBarMode(ScrollBarMode.Never);
		setTextClipPaddingByKey("Label", "textPadding");
	}

}
