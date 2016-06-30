package icetone.xhtml;

import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
 * <strong>/resources/conf/fontmap.properties</string>
 * classpath resource which is in {@link Properties} format. This file contains a list of
 * normalised font names (all spaces, '_' and '-' removed, lower cased) as the keys and
 * the font paths as the values. The font paths may point to other font names by using '!'
 * followed by the font name. Each font has 4 varients, normal, bold, italic and
 * bold+italic. You can point all varients to another varient by suffixing the key with a
 * '*'.
 * <h2>Example fontmap.properties</h2>
 * <pre>
 * <code>
 * default*=!sans
 *
 * # Monospaced
 * monospace=tonegod/gui/xhtml/styles/def/monospace20.fnt
 * monospaceBold=tonegod/gui/xhtml/styles/def/monospaceBold20.fnt
 * monospaceItalic=tonegod/gui/xhtml/styles/def/monospaceItalic20.fnt
 * monospaceBoldItalic=tonegod/gui/xhtml/styles/def/monospaceBoldItalic20.fnt
 * monospaced*=!monospace
 * couriernew*=!monospace
 *
 * # Sans
 * sans=tonegod/gui/xhtml/styles/def/sans20.fnt
 * sansBold=tonegod/gui/xhtml/styles/def/sansBold20.fnt
 * sansItalic=tonegod/gui/xhtml/styles/def/sansItalic20.fnt
 * sansBoldItalic=tonegod/gui/xhtml/styles/def/sansBoldItalic20.fnt
 * arial*=!sans
 * tahoma*=!sans
 * verdana*=!sans
 * sansSerif*=!sans
 *
 * # Serif
 * serif=tonegod/gui/xhtml/styles/def/serif20.fnt
 * serifBold=tonegod/gui/xhtml/styles/def/serifBold20.fnt
 * serifBoldItalic=tonegod/gui/xhtml/styles/def/serifBoldItalic20.fnt
 * serifItalic=tonegod/gui/xhtml/styles/def/serifItalic20.fnt
 * serifBoldItalic=tonegod/gui/xhtml/styles/def/serifBoldItalic20.fnt
 * times*=!serif
 * timesnewroman*=!serif
 * </code>
 * </pre>
 */
public class TGGFontResolver implements FontResolver {

    private final ElementManager screen;
    private final float ppp;
    private Map<String, TGGFSFont> fontCache = new HashMap<String, TGGFSFont>();
    private TGGFSFont defaultFont;
    private Properties map;

    public TGGFontResolver(ElementManager screen) {
        this.screen = screen;
        // TODO bad to use AWT here
        ppp = 72f / Toolkit.getDefaultToolkit().getScreenResolution();
        init();
    }

    private void init() {
        map = new Properties();
        try {
            InputStream in = getClass().getResourceAsStream(Configuration.valueFor("xr.load.font-mapping", "/icetone/xhtml/styles/def/fontmap.properties"));
            try {
                map.load(in);
            } finally {
                in.close();
            }
        } catch (IOException ioe) {
            throw new RuntimeException("Cannot load font map. Need a resource call resources/conf/fontmap.properties", ioe);
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

        // Determine default font
        String fn = getFontFamilyOrNull("default");
        BitmapFont def;
        if (fn == null) {
            // Use default Tonegod font
            fn = screen.getStyle("Font").getString("defaultFont");
            def = screen.getApplication().getAssetManager().loadFont(fn);
            if (def == null) {
                throw new RuntimeException("Must have a defaultFont in Font style.");
            }
        } else {
            def = screen.getApplication().getAssetManager().loadFont(fn);
        }
        defaultFont = new TGGFSFont(screen.getApplication().getAssetManager(), fn, def, def.getCharSet().getLineHeight());
    }

    @Override
    public void flushCache() {
        fontCache.clear();
    }

    @Override
    public FSFont resolveFont(SharedContext renderingContext, FontSpecification spec) {
        if (spec.families != null) {
            for (String f : spec.families) {
                FSFont font = resolveFont(renderingContext, f, spec.size,
                        spec.fontWeight, spec.fontStyle, spec.variant);
                if (font != null) {
                    return font;
                }
            }
        }
        final TGGFSFont font = getFont(spec.size, spec.variant, renderingContext, "default", spec.fontWeight, spec.fontStyle);
        if (font == null) {
            throw new RuntimeException(String.format("No font found at all, either %s or %s", "default", Arrays.asList(spec.families)));
        }
        return font;
    }

    private TGGFSFont resolveFont(SharedContext ctx, String font, float size, IdentValue weight,
            IdentValue style, IdentValue variant) {
        if (font.startsWith("\"")) {
            font = font.substring(1);
        }
        if (font.endsWith("\"")) {
            font = font.substring(0, font.length() - 1);
        }
        font = font.toLowerCase();
        font = font.replace("-", "");
        font = font.replace(" ", "");
        font = font.replace("_", "");

        return getFont(size, variant, ctx, font, weight, style);
    }

    private String getFontFamilyOrNull(String name) {
        String fn = map.getProperty(name);
        if (fn != null && fn.startsWith("!")) {
            fn = getFontFamilyOrNull(fn.substring(1));
        }
        return fn;
    }

    private static String getFontKey(String name, int size, IdentValue weight,
            IdentValue style, IdentValue variant) {
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

    private TGGFSFont getFont(float size, IdentValue variant, SharedContext ctx, String font, IdentValue weight, IdentValue style) {
        int nSize = getActualSize(size, variant, ctx);

        String fontName = getFontKey(font, nSize, weight, style, variant);
        if (fontCache.containsKey(fontName)) {
            return (TGGFSFont) fontCache.get(fontName);
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
                BitmapFont def = screen.getApplication().getAssetManager().loadFont(fn);
                TGGFSFont fnt = new TGGFSFont(screen.getApplication().getAssetManager(), fn, def, nSize);
                fontCache.put(fontName, fnt);
                return fnt;
            }
        }
        String fn = getFontFamilyOrNull(font);
        if (fn != null) {
            BitmapFont fd = screen.getApplication().getAssetManager().loadFont(fn);
            TGGFSFont fnt = new TGGFSFont(screen.getApplication().getAssetManager(), fn, fd, nSize);
            fnt.setBold(bold);
            fnt.setItalic(italic);
            fontCache.put(fontName, fnt);
            return fnt;
        }

        return null;
    }
}
