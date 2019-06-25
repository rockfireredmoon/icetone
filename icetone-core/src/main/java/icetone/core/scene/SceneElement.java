package icetone.core.scene;

import java.util.Map;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;

public interface SceneElement {

	SceneMesh getMesh();

	void setPositionX(float x);

	void setPositionY(float y);

	void setPositionZ(float z);

	void setPosition(float x, float y);

	void setPosition(Vector2f pos);

	void setRotation(float rotation);

	void setScale(float x, float y);

	void setScale(Vector2f scale);

	void setScaleX(float scaleX);

	void setScaleY(float scaleY);

	void setOrigin(Vector2f origin);

	void setOrigin(float x, float y);

	void setOriginX(float originX);

	void setOriginY(float originY);

	void setDimensions(Vector2f dim);

	void setDimensions(float w, float h);

	Vector2f getPosition();

	float getPositionX();

	float getPositionY();

	float getPositionZ();

	float getRotation();

	Vector2f getScale();

	float getScaleX();

	float getScaleY();

	Vector2f getOrigin();

	float getOriginX();

	float getOriginY();

	Vector2f getDimensions();

	float getWidth();

	float getHeight();

	Map<String, QuadData> getQuads();

	void setColor(ColorRGBA color);

	void setColorR(float r);

	void setColorG(float g);

	void setColorB(float b);

	void setColorA(float a);

	void setTCOffsetX(float x);

	void setTCOffsetY(float y);

	void setWidth(float w);

	void setHeight(float h);

	void setSkew(Vector2f skew);

	void setSkew(float x, float y);

	void setSkewX(float x);

	void setSkewY(float y);

	ColorRGBA getColor();

	float getColorR();

	float getColorG();

	float getColorB();

	float getColorA();

	Vector2f getTCOffset();

	float getTCOffsetX();

	float getTCOffsetY();

	Vector2f getSkew();

	float getSkewX();

	float getSkewY();

	SceneElement getSceneParent();
}
