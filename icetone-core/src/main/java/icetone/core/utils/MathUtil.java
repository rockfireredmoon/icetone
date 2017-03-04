package icetone.core.utils;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;

import icetone.core.Size;

public class MathUtil {

	public static Vector2f rotatedBoundsLocal(Vector2f rect, float rad) {
		float s = FastMath.sin(rad);
		float c = FastMath.cos(rad);
		float rx = rect.y * FastMath.abs(s) + rect.x * FastMath.abs(c);
		float ry = rect.y * FastMath.abs(c) + rect.x * FastMath.abs(s);
		rect.set(rx, ry);
		return rect;
	}

	public static Vector2f rotatedBounds(Vector2f rect, float rad) {
		float s = FastMath.sin(rad);
		float c = FastMath.cos(rad);
		return new Vector2f(rect.y * FastMath.abs(s) + rect.x * FastMath.abs(c),
				rect.y * FastMath.abs(c) + rect.x * FastMath.abs(s));
	}

	public static Size clampSize(Size sz, Size minDimensions, Size maxDimensions) {
		if (minDimensions != null)
			sz = max(minDimensions, sz);
		if (maxDimensions != null)
			sz = min(maxDimensions, sz);
		return sz;
	}

	public static Vector2f clampSize(Vector2f sz, Vector2f minDimensions, Vector2f maxDimensions) {
		if (minDimensions != null)
			sz = max(minDimensions, sz);
		if (maxDimensions != null)
			sz = min(maxDimensions, sz);
		return sz;
	}

	public static Vector2f largest(Vector2f v1, Vector2f v2) {
		return v1 == null && v2 == null ? null
				: (new Vector2f(Math.max(v1 == null ? Short.MIN_VALUE : v1.x, v2 == null ? Short.MIN_VALUE : v2.x),
						Math.max(v1 == null ? Short.MIN_VALUE : v1.y, v2 == null ? Short.MIN_VALUE : v2.y)));
	}

	public static Vector2f max(Vector2f v1, Vector2f v2) {
		return new Vector2f(Math.max(v1.x, v2.x), Math.max(v1.y, v2.y));
	}

	public static Size max(Size v1, Size v2) {
		return new Size(Math.max(v1.x, v2.x), Math.max(v1.y, v2.y));
	}

	public static Vector2f min(Vector2f v1, Vector2f v2) {
		return new Vector2f(Math.min(v1.x, v2.x), Math.min(v1.y, v2.y));
	}

	public static Size min(Size v1, Size v2) {
		return new Size(Math.min(v1.x, v2.x), Math.min(v1.y, v2.y));
	}

	public static Vector2f union(Vector2f clone, Vector2f containerPreferredDimensions) {
		return new Vector2f(Math.max(clone.x, containerPreferredDimensions.x),
				Math.max(clone.y, containerPreferredDimensions.y));
	}
}
