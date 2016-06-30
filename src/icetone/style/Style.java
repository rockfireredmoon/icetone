/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icetone.style;

import java.util.HashMap;
import java.util.Map;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;

import icetone.effects.Effect;

/**
 *
 * @author t0neg0d
 */
public class Style {
	Map<String, Object> styleTags = new HashMap();
	
	public Style() {  }
	
	public void putTag(String key, Object value) {
		styleTags.put(key, value);
	}
	
        public boolean hasStyle(String key) {
            return styleTags.containsKey(key);
        }
	
	public String getString(String key) {
		return (String)styleTags.get(key);
	}
	
	public float getFloat(String key) {
                final Float f = (Float)styleTags.get(key);
		return f == null ? 0 : (f).floatValue();
	}
	
	public int getInt(String key) {
                final Integer i = (Integer)styleTags.get(key);
		return i == null ? 0 : (i).intValue();
	}
	
	public boolean getBoolean(String key) {
                final Boolean b = (Boolean)styleTags.get(key);
		return b == null ? false : (b).booleanValue();
	}
	
	public Vector2f getVector2f(String key) {
                final Vector2f v2 = (Vector2f)styleTags.get(key);
                return v2 == null ? null : (v2).clone();
	}
	
	public Vector3f getVector3f(String key) {
                final Vector3f v3 = (Vector3f)styleTags.get(key);
		return v3 == null ? null : (v3).clone();
	}
	
	public Vector4f getVector4f(String key) {
                final Vector4f v4 = (Vector4f)styleTags.get(key);
		return v4== null ? null :(v4).clone();
	}
	
	public ColorRGBA getColorRGBA(String key) {
                final ColorRGBA color = (ColorRGBA)styleTags.get(key);
                return color == null ? null : (color).clone();
	}
	
	public Effect getEffect(String key) {
		return (Effect)styleTags.get(key);
	}
	
	public Object getObject(String key) {
		return styleTags.get(key);
	}
        
}
