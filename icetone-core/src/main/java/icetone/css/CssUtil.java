package icetone.css;

import java.util.Arrays;
import java.util.List;

import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.RGBColor;
import org.w3c.dom.css.Rect;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.newmatch.Selector;
import org.xhtmlrenderer.css.parser.FSColor;
import org.xhtmlrenderer.css.parser.FSFunction;
import org.xhtmlrenderer.css.parser.FSRGBColor;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;
import org.xhtmlrenderer.css.sheet.Ruleset;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapFont.Align;
import com.jme3.font.BitmapFont.VAlign;
import com.jme3.font.LineWrapMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector4f;
import com.jme3.texture.Texture.MagFilter;
import com.jme3.texture.Texture.MinFilter;

import icetone.controls.scrolling.ScrollPanel.ScrollBarMode;
import icetone.core.BaseElement;
import icetone.core.BaseElement.TileMode;
import icetone.core.StyledNode;
import icetone.core.layout.GUIConstants;
import icetone.css.StyleManager.CursorType;

public class CssUtil {

	private CssUtil() {
	}
	public static String toString(ColorRGBA col) {
		return String.format("#%02x%02x%02x", (int)(col.r * 255f), (int)(col.g * 255f), (int)(col.b * 255f));
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
		} else if ("transparent".equals(str)) {
			return GUIConstants.DEFAULT_ELEMENT_COLOR;
		} else {
			throw new IllegalArgumentException(String.format("Unparseable colour %s", str));
		}
		return new ColorRGBA(r / 255f, g / 255f, b / 255f, a / 255f);
	}

	public static ColorRGBA toColor(RGBColor rgb) {
		return new ColorRGBA(rgb.getRed().getPrimitiveType() / 255f, rgb.getGreen().getPrimitiveType() / 255f,
				rgb.getBlue().getPrimitiveType() / 255f, 1f);
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

	public static IdentValue vAlignToIdent(BitmapFont.VAlign textVAlign) {
		switch (textVAlign) {
		case Top:
			return IdentValue.TOP;
		case Bottom:
			return IdentValue.BOTTOM;
		default:
			return IdentValue.MIDDLE;
		}
	}

	public static IdentValue alignToIdent(BitmapFont.Align textAlign) {
		switch (textAlign) {
		case Left:
			return IdentValue.LEFT;
		case Right:
			return IdentValue.RIGHT;
		default:
			return IdentValue.MIDDLE;
		}
	}

	public static VAlign identToVAlign(IdentValue ident) {
		if (ident == IdentValue.TOP) {
			return VAlign.Top;
		} else if (ident == IdentValue.BOTTOM) {
			return VAlign.Bottom;
		}
		return VAlign.Center;
	}

	public static Align identToAlign(IdentValue ident) {
		if (ident == IdentValue.LEFT) {
			return Align.Left;
		} else if (ident == IdentValue.RIGHT) {
			return Align.Right;
		}
		return Align.Center;
	}

	public static float getAlpha(CSSPrimitiveValue pv) {
		if (pv.getCssText().endsWith("%")) {
			return pv.getFloatValue(CSSPrimitiveValue.CSS_NUMBER) / 100f;
		} else {
			return pv.getFloatValue(CSSPrimitiveValue.CSS_NUMBER);
		}
	}

	public static Vector4f getPadding(PropertyDeclaration decl) {
		if (decl == null)
			return Vector4f.ZERO;
		Rect rect = decl.getValue().getRectValue();
		return new Vector4f(rect.getLeft().getFloatValue(CSSPrimitiveValue.CSS_PX),
				rect.getRight().getFloatValue(CSSPrimitiveValue.CSS_PX),
				rect.getTop().getFloatValue(CSSPrimitiveValue.CSS_PX),
				rect.getBottom().getFloatValue(CSSPrimitiveValue.CSS_PX));
	}

	public static FSFunction rgbaFunction(ColorRGBA col) {
		return new FSFunction("rgba",
				Arrays.asList((int) (col.r * 255f), (int) (col.g * 255f), (int) (col.b * 255f), col.a));
	}

	public static FSColor rgbaColor(ColorRGBA col) {
		return new FSRGBAColor((int) (col.r * 255f), (int) (col.g * 255f), (int) (col.b * 255f), (int) (col.a * 255f));
	}

	public static LineWrapMode identToLineWrapMode(IdentValue ident) {
		if (ident == IdentValue.PRE_WRAP) {
			return LineWrapMode.Word;
		} else if (ident == IdentValue.NOWRAP) {
			return LineWrapMode.NoWrap;
		} else if (ident == IdentValue.PRE) {
			return LineWrapMode.Character;
		}
		return LineWrapMode.Clip;
	}

	public static IdentValue lineWrapModeToIdent(LineWrapMode ident) {
		switch (ident) {
		case NoWrap:
			return IdentValue.NOWRAP;
		case Word:
			return IdentValue.PRE_WRAP;
		case Character:
			return IdentValue.PRE;
		default:
			return IdentValue.NORMAL;
		}
	}

	public static ColorRGBA toColor(FSColor fsColor) {
		if (fsColor instanceof FSRGBAColor) {
			FSRGBAColor rgba = (FSRGBAColor) fsColor;
			return new ColorRGBA(rgba.getRed() / 255f, rgba.getGreen() / 255f, rgba.getBlue(), rgba.getAlpha() / 255f);
		} else if (fsColor instanceof FSRGBColor) {
			FSRGBColor rgba = (FSRGBColor) fsColor;
			return new ColorRGBA(rgba.getRed() / 255f, rgba.getGreen() / 255f, rgba.getBlue() / 255f, 1);
		}
		throw new IllegalArgumentException("Unsupported color type " + fsColor);
	}

	public static ColorRGBA toFontColor(PropertyDeclaration pd, StyledNode<?, ?> element) {
		if (pd.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
			IdentValue iv = pd.asIdentValue();
			if (iv == IdentValue.INHERIT)
				return null;
		} else if (pd != null && pd.getValue() instanceof PropertyValue) {
			PropertyValue pv = (PropertyValue) pd.getValue();
			return CssUtil.toColor(pv.getFSColor());
		} else if (pd != null)
			return CssUtil.toColor(pd.getValue().getCssText());
		return null;
	}

	public static IdentValue scrollBarModeToIdent(ScrollBarMode verticalScrollBarMode) {
		switch (verticalScrollBarMode) {
		case Always:
			return IdentValue.SCROLL;
		case Auto:
			return IdentValue.AUTO;
		default:
			return IdentValue.FS_INITIAL_VALUE;
		}
	}

	public static CursorType identValueToCursor(IdentValue cursor) {
		if (cursor.equals(IdentValue.AUTO)) {
			return null;
		} else if (cursor.equals(IdentValue.NONE)) {
			return CursorType.HIDDEN;
		} else if (cursor.equals(IdentValue.DEFAULT)) {
			return CursorType.POINTER;
		} else if (cursor.equals(IdentValue.POINTER)) {
			return CursorType.HAND;
		} else if (cursor.equals(IdentValue.MOVE)) {
			return CursorType.MOVE;
		} else if (cursor.equals(IdentValue.TEXT)) {
			return CursorType.TEXT;
		} else if (cursor.equals(IdentValue.WAIT)) {
			return CursorType.WAIT;
		} else if (cursor.equals(CssExtensions.NS_RESIZE)) {
			return CursorType.RESIZE_NS;
		} else if (cursor.equals(CssExtensions.EW_RESIZE)) {
			return CursorType.RESIZE_EW;
		} else if (cursor.equals(CssExtensions.NESW_RESIZE)) {
			return CursorType.RESIZE_CNW;
		} else if (cursor.equals(CssExtensions.NWSE_RESIZE)) {
			return CursorType.RESIZE_CNE;
		}
		return CursorType.POINTER;
	}

	public static PropertyValue cursorToPropertyValue(CursorType cursor) {
		if (cursor.name().startsWith("CUSTOM_")) {
			return new PropertyValue(CSSPrimitiveValue.CSS_URI, "cursor://" + cursor.name(),
					"uri(cursor://" + cursor.name() + ")");
		} else
			return new PropertyValue(cursorToIdent(cursor));
	}

	public static IdentValue cursorToIdent(CursorType cursor) {
		if (cursor == null)
			return IdentValue.AUTO;
		switch (cursor) {
		case HAND:
			return IdentValue.POINTER;
		case MOVE:
			return IdentValue.MOVE;
		case POINTER:
			return IdentValue.DEFAULT;
		case TEXT:
			return IdentValue.TEXT;
		case WAIT:
			return IdentValue.WAIT;
		case RESIZE_NS:
			return CssExtensions.NS_RESIZE;
		case RESIZE_EW:
			return CssExtensions.EW_RESIZE;
		case RESIZE_CNE:
			return CssExtensions.NESW_RESIZE;
		case RESIZE_CNW:
			return CssExtensions.NWSE_RESIZE;
		default:
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static String dumpRuleset(Ruleset r) {
		StringBuilder bui = new StringBuilder();
		for (Selector s : ((List<Selector>) r.getFSSelectors())) {
			if (bui.length() > 0)
				bui.append("\n");
			bui.append("ID:");
			bui.append(s.getSelectorID());
			bui.append(" AXIS:");
			bui.append(s.getAxis());
			bui.append(" SPECB:");
			bui.append(s.getSpecificityB());
			bui.append(" SPECC:");
			bui.append(s.getSpecificityC());
			bui.append(" SPECD:");
			bui.append(s.getSpecificityD());
			if (s.getPseudoElement() != null && s.getPseudoElement().length() > 0) {
				bui.append(" PE: " + s.getPseudoElement());
			}
			bui.append(" ");
			if (s.isPseudoClass(Selector.ACTIVE_PSEUDOCLASS))
				bui.append(":ACTIVE");
			if (s.isPseudoClass(Selector.FOCUS_PSEUDOCLASS))
				bui.append(":FOCUS");
			if (s.isPseudoClass(Selector.HOVER_PSEUDOCLASS))
				bui.append(":HOVER");
			if (s.isPseudoClass(Selector.VISITED_PSEUDOCLASS))
				bui.append(":VISITED");

		}
		for (PropertyDeclaration decl : ((List<PropertyDeclaration>) r.getPropertyDeclarations())) {
			if (bui.length() > 0)
				bui.append("\n");
			bui.append(decl.getPropertyName());
			bui.append(" = ");
			bui.append(decl.getValue());
		}
		return bui.toString();
	}

	public static TileMode identToTileMode(IdentValue iv) {
		if (iv.equals(IdentValue.REPEAT)) {
			return TileMode.REPEAT;
		} else if (iv.equals(IdentValue.REPEAT_X)) {
			return TileMode.REPEAT_X;
		} else if (iv.equals(IdentValue.REPEAT_Y)) {
			return TileMode.REPEAT_Y;
		}
		return TileMode.NONE;
	}

	public static IdentValue tileModeToIdent(TileMode tileMode) {
		switch (tileMode) {
		case REPEAT:
			return IdentValue.REPEAT;
		case REPEAT_X:
			return IdentValue.REPEAT_X;
		case REPEAT_Y:
			return IdentValue.REPEAT_Y;
		default:
			return IdentValue.NO_REPEAT;
		}
	}

	public static IdentValue magFilterToIdent(MagFilter magFilter) {
		switch (magFilter) {
		case Bilinear:
			return CssExtensions.BILINEAR;
		default:
			return CssExtensions.NEAREST;
		}
	}

	public static IdentValue minFilterToIdent(MinFilter minFilter) {
		switch (minFilter) {
		case BilinearNearestMipMap:
			return CssExtensions.BILINEAR_NEAREST_MIP_MAP;
		case NearestLinearMipMap:
			return CssExtensions.NEAREST_LINEAR_MIP_MAP;
		case NearestNearestMipMap:
			return CssExtensions.NEAREST_NEAREST_MIP_MAP;
		case NearestNoMipMaps:
			return CssExtensions.NEAREST_NO_MIP_MAPS;
		case Trilinear:
			return CssExtensions.TRILINEAR;
		default:
			return CssExtensions.BILINEAR_NO_MIP_MAPS;
		}
	}

	public static MinFilter identToMinFilter(IdentValue iv) {
		if (iv.equals(CssExtensions.NEAREST_NO_MIP_MAPS)) {
			return MinFilter.NearestNoMipMaps;
		} else if (iv.equals(CssExtensions.NEAREST_NEAREST_MIP_MAP)) {
			return MinFilter.NearestNearestMipMap;
		} else if (iv.equals(CssExtensions.BILINEAR_NEAREST_MIP_MAP)) {
			return MinFilter.BilinearNearestMipMap;
		} else if (iv.equals(CssExtensions.NEAREST_LINEAR_MIP_MAP)) {
			return MinFilter.NearestLinearMipMap;
		} else if (iv.equals(CssExtensions.TRILINEAR)) {
			return MinFilter.Trilinear;
		} else if (!iv.equals(CssExtensions.BILINEAR_NO_MIP_MAPS)) {
			BaseElement.LOG.warning(String.format("Unknown identy value of min filter '%s'", iv));
		}
		return MinFilter.BilinearNoMipMaps;
	}

	public static MagFilter identToMagFilter(IdentValue iv) {
		if (iv.equals(CssExtensions.BILINEAR)) {
			return MagFilter.Bilinear;
		} else if (!iv.equals(CssExtensions.NEAREST)) {
			BaseElement.LOG.warning(String.format("Unknown identy value of mag filter '%s'", iv));
		}
		return MagFilter.Nearest;
	}

}
