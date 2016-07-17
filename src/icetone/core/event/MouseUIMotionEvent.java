package icetone.core.event;

import com.jme3.input.event.MouseMotionEvent;

import icetone.core.Element;
import icetone.core.layout.LUtil;

public class MouseUIMotionEvent extends MouseMotionEvent  implements MouseUIEvent {

	private int relx;
	private int rely;
	private Element element;
	private int modifiers;
	private MouseMotionEvent originator;

	public MouseUIMotionEvent(MouseMotionEvent originator, int modifiers) {
		this(originator, 0, 0, null, modifiers);
	}

	public MouseUIMotionEvent(MouseMotionEvent originator, Element element, int modifiers) {
		this(originator, (int) (originator.getX() - element.getAbsoluteX()),
				(int) (originator.getY() - LUtil.getAbsoluteY(element)), element, modifiers);

	}

	public MouseUIMotionEvent(MouseMotionEvent originator, int relx, int rely, Element element, int modifiers) {
		this(originator.getX(), originator.getY(), originator.getDX(), originator.getDY(), originator.getWheel(),
				originator.getDeltaWheel(), relx, rely, modifiers, element);
		this.originator = originator;
	}

	public MouseUIMotionEvent(int x, int y, int dx, int dy, int wheel, int deltaWheel, int relx, int rely,
			int modifiers, Element element) {
		super(x, y, dx, dy, wheel, deltaWheel);
		this.relx = relx;
		this.rely = rely;
		this.element = element;
		this.modifiers = modifiers;
	}

	public MouseMotionEvent getOriginator() {
		return originator;
	}

	public int getModifiers() {
		return modifiers;
	}

	public int getRelx() {
		return relx;
	}

	public int getRely() {
		return rely;
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

	@Override
	public String toString() {
		return "MouseUIMotionEvent [relx=" + relx + ", rely=" + rely + ", element=" + element + ", modifiers="
				+ modifiers + ", originator=" + originator + ", getDeltaWheel()=" + getDeltaWheel() + ", getDX()="
				+ getDX() + ", getDY()=" + getDY() + ", getWheel()=" + getWheel() + ", getX()=" + getX() + ", getY()="
				+ getY() + ", getTime()=" + getTime() + ", isConsumed()=" + isConsumed() + "]";
	}
}
