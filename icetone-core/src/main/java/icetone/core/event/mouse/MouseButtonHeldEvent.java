package icetone.core.event.mouse;

import com.jme3.input.event.MouseButtonEvent;

import icetone.core.UIEventTarget;

public class MouseButtonHeldEvent<E extends UIEventTarget> extends MouseUIButtonEvent<E> {

	private int count;

	public MouseButtonHeldEvent(int btnIndex, boolean pressed, int x, int y, int relx, int rely, int clicks,
			int modifiers, E element) {
		super(btnIndex, pressed, x, y, relx, rely, clicks, modifiers, element);
	}

	public MouseButtonHeldEvent(MouseButtonEvent originator, E element, int modifiers) {
		super(originator, element, modifiers);
	}

	public MouseButtonHeldEvent(MouseButtonEvent originator, int relx, int rely, int clicks, int modifiers, E element) {
		super(originator, relx, rely, clicks, modifiers, element);
	}

	public MouseButtonHeldEvent(MouseButtonEvent originator, int clicks, int modifiers) {
		super(originator, clicks, modifiers);
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
