package icetone.controls.scrolling;

import com.jme3.input.event.InputEvent;

import icetone.controls.scrolling.ScrollPanel.ScrollDirection;
import icetone.core.event.UIEvent;

public class ScrollEvent extends InputEvent implements UIEvent {
	
	private ScrollArea source;
	private ScrollDirection type;

	public ScrollEvent(ScrollArea source, ScrollDirection type) {
		time = System.currentTimeMillis();
		this.type = type;
		this.source = source;
	}

	public ScrollArea getSource() {
		return source;
	}

	public ScrollDirection getDirection() {
		return type;
	}

}
