package icetone.controls.scrolling;

import icetone.core.Element.Orientation;

public interface Scrollable {

	float getScrollBounds(Orientation orientation);

	float getScrollableArea(Orientation orientation);

	int getButtonInc();

	void setScrollAreaPositionTo(float relativeScrollAmount, Orientation orientation);

	int getTrackInc();

}
