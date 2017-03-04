package icetone.core.event;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;

import icetone.core.UIEventTarget;

public class MouseUIButtonEvent<E extends UIEventTarget> extends MouseButtonEvent implements MouseUIEvent {

	public static final int ALL = Integer.MAX_VALUE;
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	public static final int WHEEL = 2;

	private int clicks;
	private E element;
	private MouseButtonEvent originator;
	private int modifiers;
	private Vector2f rel = new Vector2f();
	private int repeatCount;

	public MouseUIButtonEvent(MouseButtonEvent originator, int clicks, int modifiers) {
		this(originator, 0, 0, clicks, modifiers, null);
	}

	public MouseUIButtonEvent(MouseButtonEvent originator, E element, int modifiers) {
		this(originator.getButtonIndex(), originator.isPressed(), originator.getX(), originator.getY(),
				(int) (originator.getX() - (element == null ? 00 : element.getAbsoluteX())),
				(int) (originator.getY() - (element == null ? 00 : element.getAbsoluteY())),
				element == null ? 1 : element.getScreen().getClickCount(), modifiers, element);

		this.originator = originator;
	}

	public MouseUIButtonEvent(MouseButtonEvent originator, int relx, int rely, int clicks, int modifiers,
			E element) {
		this(originator.getButtonIndex(), originator.isPressed(), originator.getX(), originator.getY(), relx, rely,
				clicks, modifiers, element);

		this.originator = originator;
	}

	public MouseUIButtonEvent(int btnIndex, boolean pressed, int x, int y, int relx, int rely, int clicks,
			int modifiers, E element) {
		super(btnIndex, pressed, x, y);
		this.clicks = clicks;
		this.element = element;
		this.rel.x = relx;
		this.rel.y = rely;
		this.modifiers = modifiers;
	}

	public int getRepeatCount() {
		return repeatCount;
	}

	public MouseUIButtonEvent<E> setRepeatCount(int repeatCount) {
		this.repeatCount = repeatCount;
		return this;
	}

	@Override
	public int getRely() {
		return (int) rel.y;
	}

	@Override
	public int getRelx() {
		return (int) rel.x;
	}

	public Vector2f getRel() {
		return rel;
	}

	public boolean isShift() {
		return (modifiers & KeyboardUIEvent.SHIFT_MASK) != 0;
	}

	public boolean isCtrl() {
		return (modifiers & KeyboardUIEvent.CTRL_MASK) != 0;
	}

	public boolean isAlt() {
		return (modifiers & KeyboardUIEvent.ALT_MASK) != 0;
	}

	public int getModifiers() {
		return modifiers;
	}

	public int getClicks() {
		return clicks;
	}

	public E getElement() {
		return element;
	}

	@Override
	public void setConsumed() {
		super.setConsumed();
		if (originator != null)
			originator.setConsumed();
	}

	public boolean isRight() {
		return getButtonIndex() == RIGHT;
	}

	public boolean isWheel() {
		return getButtonIndex() == WHEEL;
	}

	public boolean isLeft() {
		return getButtonIndex() == LEFT;
	}

	@Override
	public String toString() {
		return "MouseUIButtonEvent [clicks=" + clicks + ", element=" + element + ", originator=" + originator
				+ ", modifiers=" + modifiers + ", rely=" + rel.y + ", relx=" + rel.x + ", getButtonIndex()="
				+ getButtonIndex() + ", isPressed()=" + isPressed() + ", getX()=" + getX() + ", getY()=" + getY()
				+ ", getTime()=" + getTime() + ", isConsumed()=" + isConsumed() + "]";
	}

}
