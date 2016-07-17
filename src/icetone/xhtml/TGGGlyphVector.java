package icetone.xhtml;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import org.xhtmlrenderer.extend.FSGlyphVector;
import org.xhtmlrenderer.extend.OutputDevice;

import com.jme3.math.Vector2f;

import icetone.controls.text.TextElement;
import icetone.core.layout.LUtil;

public class TGGGlyphVector implements FSGlyphVector {
	private String string;
	private TGGFSFont font;

	public TGGGlyphVector(OutputDevice outputDevice, TGGFSFont font, String string) {
		this.string = string;
		this.font = font;
	}

	public String getString() {
		return string;
	}

	public Rectangle getGlyphPixelBounds(int index, TGGCanvas gc, float x, float y) {

		Rectangle2D rect = getGlyphVisualBounds(index, gc);
		int l = (int) Math.floor(rect.getX() + x);
		int t = (int) Math.floor(rect.getY() + y);
		int r = (int) Math.ceil(rect.getMaxX() + x);
		int b = (int) Math.ceil(rect.getMaxY() + y);
		return new Rectangle(l, t, r - l, b - t);
	}

	private Rectangle2D getGlyphVisualBounds(int index, TGGCanvas gc) {
		// TODO make this more efficient and create a single text element (than can also be drawn)
		

		TGGFSFont wasfont = gc.getDrawFont();
		gc.setDrawFont(font);

		// Get the total size of the text block
		TextElement text = gc.createText(string);
		Vector2f pref = LUtil.getBoundPreferredSize(text);

		// Get the size of the text block from the index
		String tstr = string.substring(index);
		TextElement ttext = gc.createText(tstr);
		Vector2f tpref = LUtil.getBoundPreferredSize(ttext);

		// Get the size of the first character
		String fstr = string.substring(index, index + 1);
		TextElement ftext = gc.createText(fstr);
		Vector2f fpref = LUtil.getBoundPreferredSize(ftext);

		// The position of that character is the difference between the two
		float offsetx = pref.x - tpref.x;

		Rectangle r = new Rectangle((int)offsetx, 0, (int)fpref.x, (int)fpref.y);
		gc.setDrawFont(wasfont);
		return r;
	}

	public TGGFSFont getFont() {
		return font;
	}
}
