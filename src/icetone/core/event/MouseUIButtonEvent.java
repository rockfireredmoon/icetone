package icetone.core.event;

import com.jme3.input.event.MouseButtonEvent;

import icetone.core.Element;
import icetone.core.Screen;
import icetone.core.layout.LUtil;

public class MouseUIButtonEvent extends MouseButtonEvent implements MouseUIEvent {

	public static final int ALL = 0;
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	public static final int WHEEL = 2;

	private int clicks;
	private Element element;
	private MouseButtonEvent originator;
	private int modifiers;
	private int rely;
	private int relx;

	public MouseUIButtonEvent(MouseButtonEvent originator, int clicks, int modifiers) {
		this(originator, 0, 0, clicks, modifiers, null);
	}

	public MouseUIButtonEvent(MouseButtonEvent originator, Element element, int modifiers) {
		this(originator.getButtonIndex(), originator.isPressed(), originator.getX(), originator.getY(),
				(int) (originator.getX() - element.getAbsoluteX()),
				(int) (originator.getY() - LUtil.getAbsoluteY(element)), element.getScreen().getClickCount(), modifiers,
				element);
		
		this.originator = originator;
	}

	public MouseUIButtonEvent(MouseButtonEvent originator, int relx, int rely, int clicks, int modifiers,
			Element element) {
		this(originator.getButtonIndex(), originator.isPressed(), originator.getX(), originator.getY(), relx, rely,
				clicks, modifiers, element);
		
		this.originator = originator;
	}

	public MouseUIButtonEvent(int btnIndex, boolean pressed, int x, int y, int relx, int rely, int clicks,
			int modifiers, Element element) {
		super(btnIndex, pressed, x, y);
		
		this.element = element;
		this.relx = relx;
		this.rely = rely;
		this.modifiers = modifiers;
	}

	public int getRely() {
		return rely;
	}

	public int getRelx() {
		return relx;
	}

	public boolean isShift() {
		return (modifiers & Screen.SHIFT_MASK) != 0;
	}

	public boolean isCtrl() {
		return (modifiers & Screen.CTRL_MASK) != 0;
	}

	public boolean isAlt() {
		return (modifiers & Screen.ALT_MASK) != 0;
	}

	public int getModifiers() {
		return modifiers;
	}

	public int getClicks() {
		return clicks;
	}

	public Element getElement() {
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

}
