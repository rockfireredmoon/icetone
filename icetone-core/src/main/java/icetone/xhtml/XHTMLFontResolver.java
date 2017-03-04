package icetone.xhtml;

import java.awt.Toolkit;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.value.FontSpecification;
import org.xhtmlrenderer.extend.FontResolver;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.render.FSFont;
import org.xhtmlrenderer.util.Configuration;

import com.jme3.font.BitmapFont;

import icetone.core.ElementManager;

/**
 * Responsible for locating fonts. This is done through the
 * <strong>fontmap.properties</string> classpath resource which is in
 * {@link Properties} format. This file contains a list of normalised font names
 * (all spaces, '_' and '-' removed, lower cased) as the keys and the font
 * family names as the values (as defined in the themes @font-face rules). The
 * font paths may point to other font names by using '!' followed by the font
 * name. Each font has 4 variants, normal, bold, italic and bold+italic. You can
 * point all variants to another variant by suffixing the key with a '*'.
 * <h2>Example fontmap.properties</h2>
 * 
 * <pre>
 * <code>
 * default*=!sans
 *
 * # Monospaced
 * monospace=monospace20
 * monospaceBold=monospaceBold20
 * monospaceItalic=monospaceItalic20
 * monospaceBoldItalic=monospaceBoldItalic20
 * monospaced*=!monospace
 * couriernew*=!monospace
 *
 * # Sans
 * sans=sans20
 * sansBold=sansBold20
 * sansItalic=sansItalic20
 * sansBoldItalic=sansBoldItalic20
 * arial*=!sans
 * tahoma*=!sans
 * verdana*=!sans
 * sansSerif*=!sans
 *
 * # Serif
 * serif=serif20
 * serifBold=serifBold20
 * serifBoldItalic=serifBoldItalic20
 * serifItalic=serifItalic20
 * serifBoldItalic=serifBoldItalic20
 * times*=!serif
 * timesnewroman*=!serif
 * </code>
 * </pre>
 */
public class XHTMLFontResolver implements FontResolver {
	final static Logger LOG = Logger.getLogger(XHTMLFontResolver.class.getName());

	private final ElementManager screen;
	private final float ppp;
	private Map<String, XHTMLFSFont> fontCache = new HashMap<String, XHTMLFSFont>();
	private Properties map;
	private final static Map<String, String> fixed = new HashMap<>();;

	public XHTMLFontResolver(ElementManager screen) {
		this.screen = screen;
		// TODO bad to use AWT here
		ppp = 72f / Toolkit.getDefaultToolkit().getScreenResolution();
		init();
	}

	public static void mapFont(String logicalName, String assetPath) {
		fixed.put(processLogicalFontName(logicalName), assetPath);
	}

	private void init() {
		map = new Properties();
		String loc = Configuration.valueFor("xr.load.font-mapping", "/Styles/Default/Fonts/fontmap.properties");
		try {
			InputStream in = getClass().getResourceAsStream(loc);
			if (in == null)
				throw new FileNotFoundException();
			try {
				map.load(in);
				LOG.info("Font map loaded from " + loc);
			} finally {
				in.close();
			}
		} catch (IOException ioe) {
			throw new RuntimeException("Cannot load font map. Need a resource called " + loc, ioe);
		}

		// Expand wildcards
		for (String k : map.stringPropertyNames()) {
			if (k.endsWith("*")) {
				String v = map.getProperty(k);
				map.remove(k);
				k = k.substring(0, k.length() - 1);
				map.setProperty(k, v);
				map.setProperty(k + "Bold", v + "Bold");
				map.setProperty(k + "Italic", v + "Italic");
				map.setProperty(k + "BoldItalic", v + "BoldItalic");
			}
		}

		// Add fixed maps
		for (Map.Entry<String, String> en : fixed.entrySet()) {
			String k = en.getKey();
			String v = en.getValue();
			map.setProperty(k, v);
			if (!k.endsWith("Bold") && !k.endsWith("Italic") && !k.endsWith("BoldItalic")) {
				map.setProperty(k + "Bold", v + "Bold");
				map.setProperty(k + "Italic", v + "Italic");
				map.setProperty(k + "BoldItalic", v + "BoldItalic");
			}
		}

		// Early sanity check
		String fn = getFontFamilyOrNull("default");
		if (fn == null) {
			fn = "default";
		}
		String path = screen.getThemeInstance().getFontPath(fn);
		if (path == null)
			throw new RuntimeException("Could not find default font. Checck @font-face rules in "
					+ "your stylesheets and fontmap.properties.");
	}

	@Override
	public void flushCache() {
		fontCache.clear();
	}

	@Override
	public FSFont resolveFont(SharedContext renderingContext, FontSpecification spec) {
		if (spec.families != null) {
			for (String f : spec.families) {
				FSFont font = resolveFont(renderingContext, f, spec.size, spec.fontWeight, spec.fontStyle,
						spec.variant);
				if (font != null) {
					return font;
				}
			}
		}
		final XHTMLFSFont font = getFont(spec.size, spec.variant, renderingContext, "default", spec.fontWeight,
				spec.fontStyle);
		if (font == null) {
			throw new RuntimeException(
					String.format("No font found at all, either %s or %s", "default", Arrays.asList(spec.families)));
		}
		return font;
	}

	private XHTMLFSFont resolveFont(SharedContext ctx, String font, float size, IdentValue weight, IdentValue style,
			IdentValue variant) {
		if (font.startsWith("\"")) {
			font = font.substring(1);
		}
		if (font.endsWith("\"")) {
			font = font.substring(0, font.length() - 1);
		}
		font = processLogicalFontName(font);

		return getFont(size, variant, ctx, font, weight, style);
	}

	private static String processLogicalFontName(String font) {
		font = font.toLowerCase();
		font = font.replace("-", "");
		font = font.replace(" ", "");
		font = font.replace("_", "");
		return font;
	}

	private String getFontFamilyOrNull(String name) {
		String fn = map.getProperty(name);
		if (fn != null && fn.startsWith("!")) {
			fn = getFontFamilyOrNull(fn.substring(1));
		}
		return fn;
	}

	private static String getFontKey(String name, int size, IdentValue weight, IdentValue style, IdentValue variant) {
		return name + "-" + size + "-" + weight + "-" + style + "-" + variant;
	}

	private boolean isBold(IdentValue weight) {
		final boolean bold = weight != null
				&& (weight == IdentValue.BOLD || weight == IdentValue.BOLDER || weight == IdentValue.FONT_WEIGHT_700
						|| weight == IdentValue.FONT_WEIGHT_800 || weight == IdentValue.FONT_WEIGHT_900);
		return bold;
	}

	private boolean isItalic(IdentValue style) {
		return style != null && (style == IdentValue.ITALIC || style == IdentValue.OBLIQUE);
	}

	private int getActualSize(float size, IdentValue variant, SharedContext ctx) {
		size *= ppp;
		if (variant != null && variant == IdentValue.SMALL_CAPS) {
			size *= 0.6;
		}
		size *= ctx.getTextRenderer().getFontScale();
		int nSize = Math.round(size);
		return nSize;
	}

	private XHTMLFSFont getFont(float size, IdentValue variant, SharedContext ctx, String font, IdentValue weight,
			IdentValue style) {
		int nSize = getActualSize(size, variant, ctx);

		String fontName = getFontKey(font, nSize, weight, style, variant);
		if (fontCache.containsKey(fontName)) {
			return fontCache.get(fontName);
		}
		boolean lookForAlternate = false;
		String altFont = font;
		boolean bold = isBold(weight);
		if (bold) {
			altFont += "Bold";
			lookForAlternate = true;
		}
		final boolean italic = isItalic(style);
		if (italic) {
			altFont += "Italic";
			lookForAlternate = true;
		}
		if (lookForAlternate) {
			String fn = getFontFamilyOrNull(altFont);
			if (fn != null) {
				String path = screen.getThemeInstance().getFontPath(fn);
				if (path == null)
					LOG.warning(String.format("No font family %s", fn));
				else {
					BitmapFont def = screen.getApplication().getAssetManager().loadFont(path);
					XHTMLFSFont fnt = new XHTMLFSFont(screen, fn, def, nSize);
					fontCache.put(fontName, fnt);
					return fnt;
				}
			}
		}
		String fn = getFontFamilyOrNull(font);
		if (fn != null) {
			String path = screen.getThemeInstance().getFontPath(fn);
			if (path == null)
				LOG.warning(String.format("No font family %s", fn));
			else {
				BitmapFont fd = screen.getApplication().getAssetManager().loadFont(path);
				XHTMLFSFont fnt = new XHTMLFSFont(screen, fn, fd, nSize);
				fnt.setBold(bold);
				fnt.setItalic(italic);
				fontCache.put(fontName, fnt);
				return fnt;
			}
		}

		return null;
	}
}
