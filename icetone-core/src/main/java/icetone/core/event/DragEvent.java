package icetone.core.event;

import com.jme3.input.event.MouseButtonEvent;

import icetone.controls.extras.DragElement.DragMode;
import icetone.core.BaseElement;
import icetone.core.UIEventTarget;
import icetone.core.event.mouse.MouseUIButtonEvent;

public class DragEvent<E extends UIEventTarget> extends MouseUIButtonEvent<E> {

	public enum DragEventType {
		prepare, start, end, complete, aborted
	}

	private DragEventType type;
	private E target;
	private BaseElement draggedElement;
	private DragMode mode;

	public DragEvent(int btnIndex, boolean pressed, int x, int y, int relx, int rely, int clicks, int modifiers,
			E element, DragEventType type, BaseElement draggedElement, DragMode mode) {
		super(btnIndex, pressed, x, y, relx, rely, clicks, modifiers, element);
		this.type = type;
		this.draggedElement = draggedElement;
		this.mode = mode;
	}

	public DragEvent(MouseButtonEvent originator, E element, int modifiers, DragEventType type, BaseElement draggedElement, DragMode mode) {
		super(originator, element, modifiers);
		this.type = type;
		this.draggedElement = draggedElement;
		this.mode = mode;
	}

	public DragEvent(E element, DragEventType type, BaseElement draggedElement, DragMode mode) {
		super(0, false, 0, 0, 0, 0, 0, 0, element);
		this.draggedElement = draggedElement;
		this.type = type;
		this.mode = mode;
	}

	public DragEvent(MouseButtonEvent originator, int relx, int rely, int clicks, int modifiers, E element,
			DragEventType type, BaseElement draggedElement, DragMode mode) {
		super(originator, relx, rely, clicks, modifiers, element);
		this.type = type;
		this.draggedElement = draggedElement;
		this.mode = mode;
	}

	public DragEvent(MouseButtonEvent originator, int clicks, int modifiers, DragEventType type, BaseElement draggedElement, DragMode mode) {
		super(originator, clicks, modifiers);
		this.type = type;
		this.draggedElement = draggedElement;
		this.mode = mode;
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

	public BaseElement getDraggedElement() {
		return draggedElement;
	}
	
	public DragMode getMode() {
		return mode;
	}


}
