package icetone.core.layout;

import com.jme3.math.Vector2f;

import icetone.core.AbstractGenericLayout;
import icetone.core.BaseElement;
import icetone.core.ElementContainer;

public class ScreenLayout extends AbstractGenericLayout<ElementContainer<?, ?>, ScreenLayoutConstraints> {

	@Override
	protected Vector2f calcMinimumSize(ElementContainer<?, ?> parent) {
		return new Vector2f(parent.getWidth(), parent.getHeight());
	}

	@Override
	protected Vector2f calcMaximumSize(ElementContainer<?, ?> parent) {
		return calcMinimumSize(parent);
	}

	@Override
	protected Vector2f calcPreferredSize(ElementContainer<?, ?> parent) {
		return parent.getDimensions();
	}

	@Override
	protected void onLayout(ElementContainer<?, ?> parent) {
		for (BaseElement el : parent.getElements()) {
			ScreenLayoutConstraints con = constraints.get(el);
			if (ScreenLayoutConstraints.fill == con) {
				el.setBounds(0, 0, parent.getWidth(), parent.getHeight());
			} else if (ScreenLayoutConstraints.center == con) {
				Vector2f pref = el.calcPreferredSize();
				el.setBounds((parent.getWidth() - pref.x) / 2f, (parent.getHeight() - pref.y) / 2f, pref.x, pref.y);
			}
			
			/*
			 * If the screen size has change that can affect the y-flipping so
			 * we need to always update element at the screen level
			 */
			el.updateNodeLocation();
		}
	}

	@Override
	public boolean positionsElement(BaseElement element) {
		ScreenLayoutConstraints con = constraints.get(element);
		return con == ScreenLayoutConstraints.center || con == ScreenLayoutConstraints.fill;
	}
}