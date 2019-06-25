package icetone.core.event.mouse;

import com.jme3.input.event.MouseMotionEvent;

import icetone.core.UIEventTarget;

public class HoverEvent<E extends UIEventTarget> extends MouseUIMotionEvent<E> {

	public enum HoverEventType {
		leave, enter
	}

	private E other;
	private HoverEventType type;

	public HoverEvent(int x, int y, int dx, int dy, int wheel, int deltaWheel, int relx, int rely, int modifiers,
			E element, E other, HoverEventType type) {
		super(x, y, dx, dy, wheel, deltaWheel, relx, rely, modifiers, element);
		this.other = other;
		this.type = type;
	}

	public HoverEvent(MouseMotionEvent originator, E element, E other, int modifiers, HoverEventType type) {
		super(originator, element, modifiers);
		this.other = other;
		this.type = type;
	}

	public HoverEvent(MouseMotionEvent originator, int relx, int rely, E element, E other, int modifiers,
			HoverEventType type) {
		super(originator, relx, rely, element, modifiers);
		this.other = other;
		this.type = type;
	}

	public HoverEventType getEventType() {
		return type;
	}

	public E getOther() {
		return other;
	}

	@Override
	public String toString() {
		return "MouseUIFocusEvent [other=" + other + ", type=" + type + ", getOriginator()=" + getOriginator()
				+ ", getModifiers()=" + getModifiers() + ", getRelx()=" + getRelx() + ", getRely()=" + getRely()
				+ ", getElement()=" + getElement() + ", getDeltaWheel()=" + getDeltaWheel() + ", getDX()=" + getDX()
				+ ", getDY()=" + getDY() + ", getWheel()=" + getWheel() + ", getX()=" + getX() + ", getY()=" + getY()
				+ ", getTime()=" + getTime() + ", isConsumed()=" + isConsumed() + "]";
	}
}
