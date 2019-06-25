package icetone.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ZOrderComparator implements Comparator<BaseElement> {
	private final static ZOrderComparator DEFAULT = new ZOrderComparator();

	private final Map<BaseElement, Integer> elementOrder = new HashMap<>();

	ZOrderComparator() {
	}

	/**
	 * @param element
	 */
	ZOrderComparator(List<BaseElement> elements) {
		int i = 0;
		for (BaseElement e : elements) {
			elementOrder.put(e, i++);
		}
	}

	@Override
	public int compare(BaseElement o1, BaseElement o2) {
		int i = o1.priority.compareTo(o2.priority);
		if (i == 0 && (o1.getZOrder() != 0 || o2.getZOrder() != 0)) {
			i = Float.valueOf(o1.zOrder).compareTo(Float.valueOf(o2.zOrder));
		}
		if (i == 0)
			i = elementOrder.get(o1).compareTo(elementOrder.get(o2));
		return i;
	}

	public static List<BaseElement> sortChildren(BaseElement el) {
		synchronized (DEFAULT) {
			List<BaseElement> sorted = new ArrayList<>(el.getElements().size());
			DEFAULT.elementOrder.clear();
			BaseElement key;
			for (int i = 0; i < el.getElements().size(); i++) {
				key = el.getElements().get(i);
				DEFAULT.elementOrder.put(key, i);
				sorted.add(key);
			}
			Collections.sort(sorted, DEFAULT);
			return sorted;
		}
	}
}