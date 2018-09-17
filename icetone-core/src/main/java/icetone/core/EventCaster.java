package icetone.core;

import icetone.core.event.MouseUIButtonEvent;
import icetone.core.event.HoverEvent;
import icetone.core.event.HoverListener;
import icetone.core.event.MouseUIMotionEvent;

public class EventCaster {

	public HoverEvent fireMouseFocusEvent(BaseElement target, HoverEvent event) {

		while (target != null && !event.isConsumed()) {
			if (target.isHoverable()) {
				target.hoverChanged(event);
				if (target.hoverSupport != null && !event.isConsumed())
					target.hoverSupport.fireEvent(event);
				if (!event.isConsumed() && target instanceof HoverListener) {
					((HoverListener) target).onFocusChange(event);
				}
			}
			target = target.getElementParent();
		}
		return event;
	}

	public <E extends UIEventTarget> MouseUIButtonEvent<E> fireMouseButtonEvent(E target, MouseUIButtonEvent<E> event) {
		while (target != null && !event.isConsumed()) {
			if (target.getMouseButtonSupport() != null && ((event.isLeft() && !target.isIgnoreMouseLeftButton())
					|| (event.isRight() && !target.isIgnoreMouseRightButton())
					|| (!event.isLeft() && !event.isRight())))
				target.getMouseButtonSupport().fireEvent(event);
			target = (E) target.getEventTargetParent();
		}
		return event;
	}

	public <E extends UIEventTarget> MouseUIMotionEvent<E> fireMouseMotionEvent(E target, MouseUIMotionEvent<E> event) {
		while (target != null && !event.isConsumed()) {
			if (!target.isIgnoreMouseMovement() && target.getMouseMovementSupport() != null)
				target.getMouseMovementSupport().fireEvent(event);
			target = (E) target.getEventTargetParent();
		}
		return event;
	}

}
