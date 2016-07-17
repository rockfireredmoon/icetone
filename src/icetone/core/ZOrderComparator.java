package icetone.core;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ZOrderComparator implements Comparator<Element> {
	/**
	 * 
	 */
	private final Map<Element, Integer> elementOrder = new HashMap<>();

	/**
	 * @param element
	 */
	ZOrderComparator(List<Element> elements) {
		int i = 0;
		for(Element e : elements) {
			elementOrder.put(e, i++);
		}
	}

	@Override
	public int compare(Element o1, Element o2) {
		int i = o1.priority.compareTo(o2.priority);
		if(i == 0) {
			i = elementOrder.get(o1).compareTo(elementOrder.get(o2));
		}
		return i;
	}
}