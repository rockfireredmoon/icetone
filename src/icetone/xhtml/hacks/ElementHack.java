package icetone.xhtml.hacks;

import java.lang.reflect.Field;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;

import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.ElementQuadGrid;

/**
 * Some static utility methods that would be better actual in {@link Element}.
 */
public class ElementHack {

    public static void setColorMap(Image colorMap, Element element) {
        Texture color;
        ElementManager screen = element.getScreen();
        final Boolean useLocalTexture = getInaccessibleField(boolean.class, "useLocalTexture", element, Element.class);
        if (screen.getUseTextureAtlas() && !useLocalTexture) {
            if (element.getElementTexture() != null) {
                color = element.getElementTexture();
            } else {
                color = screen.getAtlasTexture();
            }
        } else {
            color = new Texture2D(colorMap);
            color.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
            color.setMagFilter(Texture.MagFilter.Nearest);
            color.setWrap(Texture.WrapMode.Clamp);
        }

        setInaccessibleField(color, "defaultTex", element, Element.class);

        if (!screen.getUseTextureAtlas() || useLocalTexture) {
            float imgWidth = color.getImage().getWidth();
            float imgHeight = color.getImage().getHeight();
            float pixelWidth = 1f / imgWidth;
            float pixelHeight = 1f / imgHeight;
            ElementQuadGrid model = new ElementQuadGrid(element.getDimensions(), element.borders, imgWidth, imgHeight, pixelWidth, pixelHeight, 0, 0, imgWidth, imgHeight);
            element.getGeometry().setMesh(model);
            setInaccessibleField(model, "model", element, Element.class);
        } else {
            throw new UnsupportedOperationException();
        }

        element.getElementMaterial().setTexture("ColorMap", color);
        element.getElementMaterial().setColor("Color", ColorRGBA.White);
    }

    public static void setFont(String fontPath, Element element) {

        // font = app.getAssetManager().loadFont(fontPath);

        // This was copied from constructor code that sets up font
        BitmapFont tempFont = element.getScreen().getApplication().getAssetManager().loadFont(fontPath);
        BitmapFont font = new BitmapFont();
        font.setCharSet(element.getScreen().getApplication().getAssetManager().loadFont(fontPath).getCharSet());
        Material[] pages = new Material[tempFont.getPageSize()];
        for (int i = 0; i < pages.length; i++) {
            pages[i] = tempFont.getPage(i).clone();
        }
        font.setPages(pages);
        // -- end of new bit
        setInaccessibleField(font, "font", element, Element.class);
        BitmapText textElement = element.getTextElement();
//        TextElement textElement = element.getTextElement();
        if (textElement != null) {
            String text = element.getText();
            textElement.removeFromParent();
            textElement = null;
            element.setText(text);            
            setInaccessibleField(textElement, "textElement", element, Element.class);
        }
    }

    public static <T> T getInaccessibleField(Class<T> clazz, String fieldName, Object object, Class elementName) {
        try {
            Field f = elementName.getDeclaredField(fieldName);
            f.setAccessible(true);
            return (T) f.get(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void setInaccessibleField(Object value, String fieldName, Object object, Class elementName) {
        try {
            Field f = elementName.getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(object, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
