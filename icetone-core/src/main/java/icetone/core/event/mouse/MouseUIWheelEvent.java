package icetone.core.event.mouse;

import com.jme3.input.event.MouseMotionEvent;

import icetone.core.UIEventTarget;

public class MouseUIWheelEvent<E extends UIEventTarget> extends MouseUIMotionEvent<E> {
	
	public enum Direction {
		up, down, left, right
	}

	private Direction direction;

	public MouseUIWheelEvent(int x, int y, int dx, int dy, int wheel, int deltaWheel, int relx, int rely, int modifiers,
			E element) {
		super(x, y, dx, dy, wheel, deltaWheel, relx, rely, modifiers, element);
		direction = deltaWheel < 0 ? Direction.up : Direction.down;
	}

	public MouseUIWheelEvent(MouseMotionEvent originator, E element, int modifiers) {
		super(originator, element, modifiers);
		direction = originator.getDeltaWheel() < 0 ? Direction.up : Direction.down;
	}

	public MouseUIWheelEvent(MouseMotionEvent originator, int relx, int rely, E element, int modifiers) {
		super(originator, relx, rely, element, modifiers);
		direction = originator.getDeltaWheel() < 0 ? Direction.up : Direction.down;
	}

	public MouseUIWheelEvent(MouseMotionEvent originator, int modifiers) {
		super(originator, modifiers);
		direction = originator.getDeltaWheel() < 0 ? Direction.up : Direction.down;
	}
	
	public Direction getDirection() {
		return direction;
	}

}
