package icetone.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import com.jme3.scene.Node;

import icetone.core.Layout.LayoutType;


// WIP
public abstract class AbstractElementContainer<T extends ElementContainer<?, ET>, ET extends UIEventTarget> implements ElementContainer<T, ET> {

	private Set<LayoutType> dirty = new HashSet<>();
	private List<BaseElement> childList = new CopyOnWriteArrayList<BaseElement>();
	private boolean layingOut;
	private Layout<?, ?> layoutManager;
	private long layoutCounter;

	@Override
	public Layout<?, ?> getLayoutManager() {
		return layoutManager;
	}

	
	@Override
	public T setLayoutManager(Layout<?, ?> layout) {
//		return this;
		return null;
	}


	@Override
	public long getLayoutCounter() {
		return layoutCounter;
	}


	@SuppressWarnings("unchecked")
	@Override
	public final void layoutChildren() {
		if (layingOut) {
			return;
		}
		layingOut = true;

		layoutCounter++;
		try {
			while (layoutManager != null && !dirty.isEmpty()) {
				List<LayoutType> d = new ArrayList<>(dirty);
				Collections.sort(d);
				dirty.clear();
				if (d.contains(LayoutType.all)) {
					((Layout<ElementContainer<?,?>, ?>) layoutManager).layout(this, LayoutType.all);

					/*
					 * Only styling should cause dirtying of most types, and
					 * this is done first, followed by the individual layout
					 * types, so nothing should actually be dirty at this point
					 */
					dirty.clear();
				} else {
					while (d.size() > 0) {
						LayoutType type = d.remove(0);
						((Layout<ElementContainer<?,?>, ?>) layoutManager).layout(this, type);

						/*
						 * If anything was dirtied that we are going to process
						 * anyway, remove it from the list now so it doesn't get
						 * done twice needlessly
						 */
						d.removeAll(dirty);

					}
				}
			}
			layoutHeirarchy(null);
		} finally {
			layingOut = false;
		}

		// try {
		// Set<LayoutType> d = new LinkedHashSet<>(dirty);
		// dirty.clear();
		// if (d.contains(LayoutType.all)) {
		// layoutManager.layout(this, LayoutType.all);
		// } else {
		// for (LayoutType t : d) {
		// layoutManager.layout(this, t);
		// }
		// }
		// layoutHeirarchy(null);
		// dirty.clear();
		// } finally {
		// layingOut = false;
		// }

	}

	@Override
	public void dirtyLayout(boolean doChildren, LayoutType... layoutType) {
		// doChildren = true;
		// layoutType = new LayoutType[] { LayoutType.all };
		if (layoutType.length == 0)
			dirty.add(LayoutType.all);
		else
			for (LayoutType t : layoutType)
				dirty.add(t);

		// Normally each child should dirty it's own layout when it actually
		// changes (e.g. by parent layout manager)
		if (doChildren) {
			// System.err.println("NOTE: A component requests dirtying of
			// children. This is inefficient. The component should be fixed
			// to use LUtil methods to change bounds so layout is
			// automatically changed, or other mechanisms.");
			for (BaseElement e : childList)
				e.dirtyLayout(doChildren, layoutType);
		}
	}
	
	protected void layoutHeirarchy(Node s) {
		applyZOrder();
		for (BaseElement el : childList) {
			el.layoutChildren();
		}
	}
}
