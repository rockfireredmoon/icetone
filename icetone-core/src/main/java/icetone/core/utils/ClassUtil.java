package icetone.core.utils;

public class ClassUtil {

	public static String getMainClassName(Class<?> clazz) {
		Class<?> pclazz = clazz.getEnclosingClass();
		Class<?> sclazz = clazz.getSuperclass();
		Class<?> dclazz = clazz.getDeclaringClass();
		String n = clazz.getName();
		int idx = n.indexOf('$');
		while(sclazz != null && idx != -1) {
			clazz = sclazz;
			n = clazz.getName();
			idx = n.indexOf('$');
			sclazz = clazz.getSuperclass();
		}
		if (idx != -1) {
			n = n.substring(0, idx);
		}
		idx = n.lastIndexOf('.');
		if (idx != -1)
			n = n.substring(idx + 1);
		if (n.length() == 0)
			throw new IllegalStateException("Empty class name for " + clazz + " (" + clazz.getSimpleName() + ", "
					+ clazz.getName() + ", " + clazz.getCanonicalName() + ", " + clazz.getTypeName() + ")");
		return n;
	}
}
