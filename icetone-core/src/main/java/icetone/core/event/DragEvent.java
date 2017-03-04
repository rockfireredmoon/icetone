package icetone.core.event;

import com.jme3.input.event.MouseButtonEvent;

import icetone.core.UIEventTarget;

public class DragEvent<E extends UIEventTarget> extends MouseUIButtonEvent<E> {

	public enum DragEventType {
		prepare, start, end, complete, aborted
	}

	private DragEventType type;
	private E target;

	public DragEvent(int btnIndex, boolean pressed, int x, int y, int relx, int rely, int clicks, int modifiers,
			E element, DragEventType type) {
		super(btnIndex, pressed, x, y, relx, rely, clicks, modifiers, element);
		this.type = type;
	}

	public DragEvent(MouseButtonEvent originator, E element, int modifiers, DragEventType type) {
		super(originator, element, modifiers);
		this.type = type;
	}

	public DragEvent(E element, DragEventType type) {
		super(0, false, 0, 0, 0, 0, 0, 0, element);
		this.type = type;
	}

	public DragEvent(MouseButtonEvent originator, int relx, int rely, int clicks, int modifiers, E element,
			DragEventType type) {
		super(originator, relx, rely, clicks, modifiers, element);
		this.type = type;
	}

	public DragEvent(MouseButtonEvent originator, int clicks, int modifiers, DragEventType type) {
		super(originator, clicks, modifiers);
		this.type = type;
	}

	public DragEventType getType() {
		return type;
	}

	public E getTarget() {
		return target;
	}

	public DragEvent<E> setTarget(E target) {
		this.target = target;
		return this;
	}

}
