package icetone.controls.table;

import com.jme3.math.Vector2f;

import icetone.core.AbstractGenericLayout;
import icetone.core.BaseElement;

public class TableHeaderLayout extends AbstractGenericLayout<BaseElement, Object> {

	@Override
	protected Vector2f calcMinimumSize(BaseElement container) {
		return super.calcPreferredSize(container);
	}

	@Override
	protected Vector2f calcPreferredSize(BaseElement container) {
		Vector2f t = new Vector2f();
		for (BaseElement e : container.getElements()) {
			Vector2f sz = e.calcPreferredSize();
			t.x += sz.x;
			t.y = Math.max(t.y, sz.y);
		}
		return t.addLocal(container.getTotalPadding());
	}

}