package icetone.core;

import org.w3c.dom.css.RGBColor;

import com.jme3.font.BitmapFont.Align;
import com.jme3.font.BitmapFont.VAlign;
import com.jme3.math.ColorRGBA;

public class CssUtil {

	private CssUtil() {
	}

	public static ColorRGBA toColor(String str) {
		while (str.startsWith("#")) {
			str = str.substring(1);
		}
		int r, g, b, a = 255;
		if (str.length() == 6 || str.length() == 8) {
			r = Integer.parseInt(str.substring(0, 2), 16);
			g = Integer.parseInt(str.substring(2, 4), 16);
			b = Integer.parseInt(str.substring(4, 6), 16);
			if (str.length() == 8)
				a = Integer.parseInt(str.substring(6, 8), 16);
		} else if (str.length() == 3 || str.length() == 4) {
			r = Integer.parseInt(str.substring(0, 1), 16) * 16;
			g = Integer.parseInt(str.substring(1, 2), 16) * 16;
			b = Integer.parseInt(str.substring(2, 3), 16) * 16;
			if (str.length() == 4)
				a = Integer.parseInt(str.substring(3, 4), 16) * 16;
		} else {
			throw new IllegalArgumentException(String.format("Unparseable colour %s", str));
		}
		return new ColorRGBA((float) r / 255f, (float) g / 255f, (float) b / 255f, (float) a / 255f);
	}

	public static ColorRGBA toColor(RGBColor rgb) {
		return new ColorRGBA((float) rgb.getRed().getPrimitiveType() / 255f, (float) rgb.getGreen().getPrimitiveType() / 255f,
				(float) rgb.getBlue().getPrimitiveType() / 255f, 1f);
	}

	public static Align toAlign(String align) {
		if (align.equalsIgnoreCase("center")) {
			return Align.Center;
		} else if (align.equalsIgnoreCase("right")) {
			return Align.Right;
		}
		return Align.Left;
	}

	public static VAlign toVAlign(String align) {
		if (align.equalsIgnoreCase("middle")) {
			return VAlign.Center;
		} else if (align.equalsIgnoreCase("bottom")) {
			return VAlign.Bottom;
		}
		return VAlign.Top;
	}

}
