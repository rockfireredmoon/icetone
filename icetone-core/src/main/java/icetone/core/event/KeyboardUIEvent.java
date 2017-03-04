package icetone.core.event;

import com.jme3.input.event.KeyInputEvent;

import icetone.core.BaseElement;

public class KeyboardUIEvent extends KeyInputEvent implements UIEvent {

	private BaseElement element;
	private KeyInputEvent originator;
	private int modifiers;
	public final static int ALT_MASK = 1024;
	public final static int CTRL_MASK = 512;
	public final static int L_ALT_MASK = 4;
	public final static int L_CTRL_MASK = 2;
	public final static int L_META_MASK = 8;
	public final static int L_SHIFT_MASK = 1;
	public final static int META_MASK = 2048;
	public final static int R_ALT_MASK = 64;
	public final static int R_CTRL_MASK = 32;
	public final static int R_META_MASK = 128;
	public final static int R_SHIFT_MASK = 16;
	public final static int SHIFT_MASK = 256;

	public KeyboardUIEvent(KeyInputEvent originator, int modifiers, BaseElement element) {
		this(originator.getKeyCode(), originator.getKeyChar(), originator.isPressed(), originator.isRepeating(),
				modifiers, element);
		this.originator = originator;
	}

	public KeyboardUIEvent(int keyCode, char keyChar, boolean pressed, boolean repeating, int modifiers,
			BaseElement element) {
		super(keyCode, keyChar, pressed, repeating);
		this.element = element;
		this.modifiers = modifiers;
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

	public boolean isMeta() {
		return (modifiers & KeyboardUIEvent.META_MASK) != 0;
	}

	public int getModifiers() {
		return modifiers;
	}

	public BaseElement getElement() {
		return element;
	}

	@Override
	public void setConsumed() {
		super.setConsumed();
		if (originator != null)
			originator.setConsumed();
	}

	public boolean isNoModifiers() {
		return !isAlt() && !isCtrl() && !isShift();
	}

	@Override
	public String toString() {
		return "KeyboardUIEvent [element=" + element + ", originator=" + originator + ", modifiers=" + modifiers
				+ ", getKeyChar()=" + getKeyChar() + ", getKeyCode()=" + getKeyCode() + ", isPressed()=" + isPressed()
				+ ", isRepeating()=" + isRepeating() + ", isReleased()=" + isReleased() + ", getTime()=" + getTime()
				+ ", isConsumed()=" + isConsumed() + "]";
	}

}
