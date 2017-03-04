package icetone.core;

import icetone.core.event.MouseButtonListener;
import icetone.core.event.MouseMovementListener;
import icetone.core.event.MouseUIButtonEvent;
import icetone.core.event.MouseUIFocusEvent;
import icetone.core.event.MouseUIFocusListener;
import icetone.core.event.MouseUIMotionEvent;
import icetone.framework.core.AnimElement;

public class EventCaster {

	public MouseUIFocusEvent fireMouseFocusEvent(BaseElement target, MouseUIFocusEvent event) {

		while (target != null && !event.isConsumed()) {
			if (target.isMouseFocusable()) {
				target.focusChanged(event);
				if (target.mouseFocusSupport != null && !event.isConsumed())
					target.mouseFocusSupport.fireEvent(event);
				if (!event.isConsumed() && target instanceof MouseUIFocusListener) {
					((MouseUIFocusListener) target).onFocusChange(event);
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
