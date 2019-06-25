package icetone.core.event.mouse;

import icetone.core.event.UIEvent;

public interface MouseUIEvent extends UIEvent {

	int getX();

	int getY();

	int getRelx();

	int getRely();
}
