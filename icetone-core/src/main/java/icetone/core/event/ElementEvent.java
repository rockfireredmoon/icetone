package icetone.core.event;

import com.jme3.input.event.InputEvent;

import icetone.core.BaseElement;

public class ElementEvent<S extends BaseElement> extends InputEvent implements UIEvent {
	
	public enum Type {
		SHOWN, HIDDEN, ABOUT_TO_SHOW, ABOUT_TO_HIDE, MOVED, RESIZE
	}

	private S source;
	private Type type;

	public ElementEvent(S source, Type type) {
		time = System.currentTimeMillis();
		this.type = type;
		this.source = source;
	}

	public S getSource() {
		return source;
	}

	public Type getType() {
		return type;
	}

}
