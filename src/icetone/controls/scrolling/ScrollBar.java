/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icetone.controls.scrolling;

import java.util.Objects;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.buttons.ButtonAdapter;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.layout.BorderLayout;
import icetone.core.layout.LUtil;
import icetone.core.utils.UIDUtil;
import icetone.effects.Effect;

/**
 *
 * @author t0neg0d
 */
public class ScrollBar extends Element {
	private ButtonAdapter btnUp, btnDown, track, thumb;
	private MouseButtonEvent trackEvent = null;
	private Scrollable scrollPanel = null;
	private Orientation orientation;

	public ScrollBar(ElementManager screen, Scrollable scrollPanel, Orientation orientation) {
		super(screen, UIDUtil.getUID(), Vector2f.ZERO, LUtil.LAYOUT_SIZE, Vector4f.ZERO, null);
		this.scrollPanel = scrollPanel;
		setAsContainerOnly();
		setLayoutManager(new BorderLayout());
		setOrientation(orientation);
	}

	public void setOrientation(Orientation orientation) {
		if (!Objects.equals(orientation, this.orientation)) {
			removeAllChildren();
			this.orientation = orientation;
			initControl();
		}
	}

	@Override
	protected void onAfterLayout() {
		float ratio = getThumbRatio();
		if (thumb != null) {
			if (orientation == Orientation.VERTICAL) {
				thumb.setX(0);
				thumb.setWidth(getWidth());
				thumb.setHeight(track.getHeight() * ratio);
			} else {
				thumb.setHeight(getHeight());
				thumb.setWidth(track.getWidth() * ratio);
				if (thumb.getWidth() + thumb.getX() >= track.getWidth()) {
					thumb.setX(track.getWidth() - thumb.getWidth());
					// scrollScrollableArea();
				}
				LUtil.setY(thumb, 0);
			}
		}
	}

	private float getThumbRatio() {
		float ratio = scrollPanel.getScrollBounds(orientation) / scrollPanel.getScrollableArea(orientation);
		if (ratio > 1f)
			ratio = 1f;
		return ratio;
	}

	private void initControl() {
		String styleName = getStyleName();

		btnUp = new ButtonAdapter(screen, getUID() + ":btnInc", new Vector2f(0, 0), LUtil.LAYOUT_SIZE,
				screen.getStyle(styleName).getVector4f("btnIncResizeBorders"), screen.getStyle(styleName).getString("btnIncImg")) {
			@Override
			public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
				if (orientation == Orientation.VERTICAL) {
					float thumbY = LUtil.getY(thumb);
					if (thumbY > 0) {
						LUtil.setY(thumb, thumbY - scrollPanel.getButtonInc());
					}
				} else {
					if (thumb.getX() < track.getWidth() - thumb.getWidth()) {
						thumb.setX(thumb.getX() - scrollPanel.getButtonInc());
					}

				}
				scrollScrollableArea();
			}

			@Override
			public void onButtonStillPressedInterval() {
				if (orientation == Orientation.VERTICAL) {
					float thumbY = LUtil.getY(thumb);
					if (thumbY > 0) {
						LUtil.setY(thumb, thumbY - scrollPanel.getButtonInc());
					}
				} else {

					if (thumb.getX() < (track.getWidth() - thumb.getWidth())) {
						thumb.setX(thumb.getX() + scrollPanel.getButtonInc());
					}
				}
				scrollScrollableArea();
			}
		};
		btnUp.setInterval(100);

		track = new ButtonAdapter(screen, getUID() + ":track", screen.getStyle(styleName).getVector4f("trackResizeBorders"),
				screen.getStyle(styleName).getString("trackImg")) {
			@Override
			public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
				trackEvent = evt;
				float thumbY = LUtil.getY(thumb);
				System.out.println("ty: " + trackEvent.getY() + " ay: " + LUtil.getAbsoluteY(this));
				if (orientation == Orientation.VERTICAL) {
					if (trackEvent.getY() - LUtil.getAbsoluteY(this) < thumbY) {
						if (thumbY - scrollPanel.getTrackInc() > 0) {
							LUtil.setY(thumb, thumbY - scrollPanel.getTrackInc());
						} else {
							LUtil.setY(thumb, 0);
						}
						scrollScrollableArea();
					} else if (trackEvent.getY() - LUtil.getAbsoluteY(this) > thumbY + thumb.getHeight()) {
						if (thumbY + scrollPanel.getTrackInc() < track.getHeight() - thumb.getHeight()) {
							LUtil.setY(thumb, thumbY + scrollPanel.getTrackInc());
						} else {
							LUtil.setY(thumb, track.getHeight() - thumb.getHeight());
						}
						scrollScrollableArea();
					}
				} else {
					if (trackEvent.getX() - getAbsoluteX() < thumb.getX()) {
						if (thumb.getX() - scrollPanel.getTrackInc() > 0) {
							thumb.setX(thumb.getX() - scrollPanel.getTrackInc());
						} else {
							thumb.setX(0);
						}
						scrollScrollableArea();
					} else if (trackEvent.getX() - getAbsoluteX() > thumb.getX() + thumb.getWidth()) {
						if (thumb.getX() + scrollPanel.getTrackInc() < track.getWidth() - thumb.getWidth()) {
							thumb.setX(thumb.getX() + scrollPanel.getTrackInc());
						} else {
							thumb.setX(track.getWidth() - thumb.getWidth());
						}
						scrollScrollableArea();
					}
				}
			}

			@Override
			public void onButtonStillPressedInterval() {
				if (orientation == Orientation.VERTICAL) {
					float thumbY = LUtil.getY(thumb);
					if (trackEvent.getY() - LUtil.getAbsoluteY(this) < thumbY) {
						if (thumbY - scrollPanel.getTrackInc() > 0) {
							LUtil.setY(thumb, (thumbY - scrollPanel.getTrackInc()));
						} else {
							LUtil.setY(thumb, 0);
						}
						scrollScrollableArea();
					} else if (trackEvent.getY() - LUtil.getAbsoluteY(this) > thumbY + thumb.getHeight()) {
						if (thumbY + scrollPanel.getTrackInc() < thumb.getHeight() - thumb.getHeight()) {
							LUtil.setY(thumb, thumbY + scrollPanel.getTrackInc());
						} else {
							LUtil.setY(thumb, track.getHeight() - thumb.getHeight());
						}
						scrollScrollableArea();
					}
				} else {

					if (trackEvent.getX() - getAbsoluteX() < thumb.getX()) {
						if (thumb.getX() - scrollPanel.getTrackInc() > 0) {
							thumb.setX(thumb.getX() - scrollPanel.getTrackInc());
						} else {
							thumb.setX(0);
						}
						scrollScrollableArea();
					} else if (trackEvent.getX() - getAbsoluteX() > thumb.getX() + thumb.getWidth()) {
						if (thumb.getX() + scrollPanel.getTrackInc() < thumb.getWidth() - thumb.getWidth()) {
							thumb.setX(thumb.getX() + scrollPanel.getTrackInc());
						} else {
							thumb.setX(track.getWidth() - thumb.getWidth());
						}
						scrollScrollableArea();
					}
				}
			}
		};
		track.setInterval(100);
		track.setTileImageByKey(styleName, "tileTrackImg");

		track.removeEffect(Effect.EffectEvent.Hover);
		track.removeEffect(Effect.EffectEvent.Press);
		track.removeEffect(Effect.EffectEvent.Release);

		thumb = new ButtonAdapter(screen, getUID() + ":thumb", screen.getStyle(styleName).getVector4f("thumbResizeBorders"),
				screen.getStyle(styleName).getString("thumbImg")) {
			@Override
			public void controlMoveHook() {
				scrollScrollableArea();
			}
		};
		thumb.setIsMovable(true);
		thumb.setLockToParentBounds(true);
		track.addChild(thumb);

		btnDown = new ButtonAdapter(screen, getUID() + ":btnDec", Vector2f.ZERO, LUtil.LAYOUT_SIZE,
				screen.getStyle(styleName).getVector4f("btnDecResizeBorders"), screen.getStyle(styleName).getString("btnDecImg")) {
			@Override
			public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
				if (orientation == Orientation.VERTICAL) {
					float thumbY = LUtil.getY(thumb);
					if (thumbY < (track.getHeight() - thumb.getHeight())) {
						LUtil.setY(thumb, thumbY + scrollPanel.getButtonInc());
					}
				} else {
					if (thumb.getX() > 0) {
						thumb.setX(thumb.getX() - scrollPanel.getButtonInc());
					}

				}
				scrollScrollableArea();
			}

			@Override
			public void onButtonStillPressedInterval() {
				if (orientation == Orientation.VERTICAL) {
					float thumbY = LUtil.getY(thumb);
					if (thumbY < (track.getHeight() - thumb.getHeight())) {
						LUtil.setY(thumb, thumbY + scrollPanel.getButtonInc());
					}
				} else {

					if (thumb.getX() > 0) {
						thumb.setX(thumb.getX() - scrollPanel.getButtonInc());
					}
				}
				scrollScrollableArea();
			}

		};
		btnDown.setInterval(100);

		if (orientation == Orientation.VERTICAL) {
			if (screen.getStyle(styleName).getBoolean("useBtnDecArrowIcon")) {
				btnDown.setButtonIcon(-1, -1, screen.getStyle("Common").getString("arrowDown"));
			}
			if (screen.getStyle(styleName).getBoolean("useBtnIncArrowIcon")) {
				btnUp.setButtonIcon(-1, -1, screen.getStyle("Common").getString("arrowUp"));
			}
		} else {
			if (screen.getStyle(styleName).getBoolean("useBtnDecArrowIcon")) {
				btnDown.setButtonIcon(-1, -1, screen.getStyle("Common").getString("arrowLeft"));
			}
			if (screen.getStyle(styleName).getBoolean("useBtnIncArrowIcon")) {
				btnUp.setButtonIcon(-1, -1, screen.getStyle("Common").getString("arrowRight"));
			}
		}
		btnDown.setButtonHoverInfo(screen.getStyle(styleName).getString("btnDecHoverImg"), ColorRGBA.White);
		btnDown.setButtonPressedInfo(screen.getStyle(styleName).getString("btnDecPressedImg"), ColorRGBA.Gray);
		btnDown.setTextPaddingByKey(styleName, "btnDecTextPadding");
		btnUp.setButtonHoverInfo(screen.getStyle(styleName).getString("btnIncHoverImg"), ColorRGBA.White);
		btnUp.setButtonPressedInfo(screen.getStyle(styleName).getString("btnIncPressedImg"), ColorRGBA.Gray);
		btnUp.setTextPaddingByKey(styleName, "btnIncTextPadding");
		thumb.setButtonHoverInfo(screen.getStyle(styleName).getString("thumbHoverImg"), ColorRGBA.White);
		thumb.setButtonPressedInfo(screen.getStyle(styleName).getString("thumbPressedImg"), ColorRGBA.Gray);

		if (orientation == Orientation.VERTICAL) {
			this.addChild(btnUp, BorderLayout.Border.NORTH);
			this.addChild(track, BorderLayout.Border.CENTER);
			this.addChild(btnDown, BorderLayout.Border.SOUTH);
		} else {
			this.addChild(btnDown, BorderLayout.Border.WEST);
			this.addChild(track, BorderLayout.Border.CENTER);
			this.addChild(btnUp, BorderLayout.Border.EAST);
		}
	}

	protected String getStyleName() {
		String styleName = orientation.equals(Orientation.HORIZONTAL) ? "ScrollPanel#HScrollBar" : "ScrollPanel#VScrollBar";
		return styleName;
	}

	public float getRelativeScrollAmount() {
		float f;
		if (orientation == Orientation.VERTICAL)
			f = (LUtil.getY(thumb) / (track.getHeight() - thumb.getHeight()));
		else
			f = (thumb.getX() / (track.getWidth() - thumb.getWidth()));
		if (Float.isNaN(f))
			return 0;
		return f;
	}

	private void scrollScrollableArea() {
		scrollPanel.setScrollAreaPositionTo(getRelativeScrollAmount(), orientation);
	}

	public ButtonAdapter getButtonScrollUp() {
		return this.btnUp;
	}

	public ButtonAdapter getButtonScrollDown() {
		return this.btnDown;
	}

	public ButtonAdapter getScrollTrack() {
		return this.track;
	}

	public ButtonAdapter getScrollThumb() {
		return this.thumb;
	}

	@Override
	public void controlResizeHook() {
	}

	public void setScrollSize(float scrollSize) {
		btnUp.setMinDimensions(scrollSize == -1 ? null : new Vector2f(scrollSize, scrollSize));
		btnDown.setMinDimensions(scrollSize == -1 ? null : new Vector2f(scrollSize, scrollSize));
		btnUp.setMaxDimensions(scrollSize == -1 ? null : new Vector2f(scrollSize, scrollSize));
		btnDown.setMaxDimensions(scrollSize == -1 ? null : new Vector2f(scrollSize, scrollSize));
		btnUp.setPreferredDimensions(scrollSize == -1 ? null : new Vector2f(scrollSize, scrollSize));
		btnDown.setPreferredDimensions(scrollSize == -1 ? null : new Vector2f(scrollSize, scrollSize));
	}
}
