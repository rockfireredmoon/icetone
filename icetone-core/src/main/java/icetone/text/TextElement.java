package icetone.text;

import com.jme3.font.BitmapFont.Align;
import com.jme3.font.BitmapFont.VAlign;
import com.jme3.font.LineWrapMode;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Node;

public interface TextElement {

	Vector2f getDimensions();

	FontSpec getFont();

	Vector4f[] getLetterPositions();

	float getLineWidth();

	Vector3f getLocalTranslation();

	Material getMaterial();

	Node getParent();

	String getText();

	Align getTextAlign();

	float getTotalHeight();

	boolean removeFromParent();

	void setClippingBounds(Vector4f clippingBounds);

	void setDimensions(Vector2f dimensions);

	void setFixedLineHeight(float f);

	void setFont(FontSpec font);

	void setFontColor(ColorRGBA calcFontColor);

	void setMargin(Vector4f margin);

	void setOriginOffset(float x, float y);

	void setRotation(float tr);

	void setScale(float x, float y);

	void setSubStringColor(int i, int length, ColorRGBA fontColor);

	void setText(String text);

	void setTextAlign(Align textAlign);

	void setTextVAlign(VAlign textVAlign);

	void setTextWrap(LineWrapMode textWrap);

	void setUseClip(boolean clip);

	void updateTextState(boolean force);
}
