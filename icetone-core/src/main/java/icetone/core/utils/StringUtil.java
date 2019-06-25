package icetone.core.utils;

import java.util.HashMap;
import java.util.Map;

public class StringUtil {

	public static Map<String, String> parseProperties(String properties) {
		if (properties == null)
			return null;
		Map<String, String> p = new HashMap<>();
		if (properties.length() > 0) {
			for (String part : properties.split(",")) {
				int idx = part.indexOf('=');
				if (idx == -1)
					p.put(part, null);
				else
					p.put(part.substring(0, idx), part.substring(idx + 1));
			}
		}
		return p;
	}

	public static String toString(Map<String, String> properties) {
		if(properties == null)
			return null;
		StringBuilder b = new StringBuilder();
		for(Map.Entry<String, String> en : properties.entrySet()) {
			if(b.length() > 0)
				b.append(',');
			b.append(en.getKey());
			if(en.getValue() != null) {
				b.append('=');
				b.append(en.getValue());
			}
		}
		return b.toString();
	}

	public static int count(char c, String text) {
		int i = 0;
		for(char ch : text.toCharArray())
			if(ch == c)
				i++;
		return i;
	}
}
