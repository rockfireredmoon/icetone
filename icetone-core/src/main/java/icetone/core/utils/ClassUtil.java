package icetone.core.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ClassUtil {

	public static <T> T constructFromString(Class<T> baseClass, String spec) {
		return constructFromString(baseClass, spec, null);
	}

	@SuppressWarnings("unchecked")
	public static <T> T constructFromString(Class<T> baseClass, String spec, Map<String, Object> aliases) {
		String className = spec;
		int idx = spec.indexOf('(');
		List<String> args = Collections.emptyList();
		if (idx != -1) {
			className = spec.substring(0, idx);
			String argsStr = spec.substring(idx + 1);
			if (!argsStr.endsWith(")")) {
				throw new IllegalArgumentException("Invalid construction spec. Must end with ')'");
			}
			argsStr = argsStr.substring(0, argsStr.length() - 1);
			args = Arrays.asList(argsStr.split(","));
		}
		try {
			Class<T> clazz = (Class<T>) Class.forName(className);
			IllegalArgumentException lastEx = null;
			for (Constructor<?> c : clazz.getConstructors()) {
				if (c.getParameterCount() == args.size()) {
					try {
						Iterator<String> argIt = args.iterator();
						List<Object> argObjects = new ArrayList<>();
						for (Class<?> argClazz : c.getParameterTypes()) {
							String arg = argIt.next();
							if (aliases != null && aliases.containsKey(arg.trim())) {
								Object v = aliases.get(arg.trim());
								if (argClazz.isAssignableFrom(v.getClass())) {
									argObjects.add(v);
								} else
									throw new IllegalArgumentException(
											String.format("Invalid construction spec. '%s' argument is not a %s.'", arg,
													v.getClass()));
							} else if (argClazz.equals(String.class)) {
								if(arg.trim().startsWith("\"") && arg.trim().endsWith("\"")) {
									arg = arg.trim().substring(1, arg.length() - 2);
								}
								argObjects.add(arg);
							} else if (argClazz.equals(Long.class) || argClazz.equals(long.class)) {
								argObjects.add(Long.valueOf(arg.trim()));
							} else if (argClazz.equals(Integer.class) || argClazz.equals(int.class)) {
								argObjects.add(Integer.valueOf(arg.trim()));
							} else if (argClazz.equals(Short.class) || argClazz.equals(short.class)) {
								argObjects.add(Short.valueOf(arg.trim()));
							} else if (argClazz.equals(Byte.class) || argClazz.equals(byte.class)) {
								argObjects.add(Byte.valueOf(arg.trim()));
							} else if (argClazz.equals(Boolean.class) || argClazz.equals(boolean.class)) {
								argObjects.add(Boolean.valueOf(arg.trim()));
							} else if (argClazz.equals(Float.class) || argClazz.equals(float.class)) {
								argObjects.add(Float.valueOf(arg.trim()));
							} else if (argClazz.equals(Double.class) || argClazz.equals(double.class)) {
								argObjects.add(Double.valueOf(arg.trim()));
							} else
								throw new IllegalArgumentException(String.format(
										"Invalid construction spec. '%s' argument with value of %s is not supported.",
										argClazz.getName(), arg));
						}

						// Matches!
						lastEx = null;

						return (T) c.newInstance(argObjects.toArray(new Object[0]));
					} catch (IllegalArgumentException nfe) {
						lastEx = nfe;
						continue;
					}
				}
			}

			if (lastEx != null)
				throw lastEx;
			else
				throw new IllegalArgumentException(String.format("No constructors for layout spec match %s", spec));
		} catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException
				| InstantiationException e) {
			throw new IllegalStateException("Could not construct layout.", e);
		}
	}

	public static String getMainClassName(Class<?> clazz) {
		Class<?> sclazz = clazz.getSuperclass();
		String n = clazz.getName();
		int idx = n.indexOf('$');
		while (sclazz != null && idx != -1) {
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
