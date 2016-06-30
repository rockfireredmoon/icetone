/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icetone.controls.scrolling;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.buttons.ButtonAdapter;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.Screen;
import icetone.core.layout.AbstractScrollLayout;
import icetone.core.layout.LUtil;
import icetone.core.layout.LayoutManager;
import icetone.core.layout.WrappingLayout;
import icetone.core.utils.UIDUtil;
import icetone.framework.animation.Interpolation;
import icetone.framework.core.util.GameTimer;
import icetone.listeners.FlingListener;
import icetone.listeners.MouseWheelListener;
import icetone.listeners.TouchListener;

/**
 *
 * @author t0neg0d
 */
public class ScrollPanel extends Element implements Scrollable {

	public enum ScrollBarMode {
		Always, Never, Auto
	}

	public static enum ScrollDirection {
		Up, Down, Left, Right
	}

	protected ScrollPanelBounds innerBounds;
	protected Element scrollableArea;
	protected ScrollBar vScrollBar;
	protected ScrollBar hScrollBar;
	private float scrollSize = -1;
	private int buttonInc = 1;
	private int trackInc = 10;
	private boolean pagingEnabled = false;
	private boolean flingEnabled = true;
	private GameTimer flingTimer;
	protected float touchStartY = 0;
	protected float touchEndY = 0;
	protected float touchOffsetY = 0;
	protected boolean flingDir = true;
	protected float flingSpeed = 1;
	protected float gap;
	protected ScrollBarMode horizontalScrollBarMode = ScrollBarMode.Auto;
	protected ScrollBarMode verticalScrollBarMode = ScrollBarMode.Auto;

	/**
	 * Creates a new instance of the ScrollPanel control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public ScrollPanel() {
		this(Screen.get());
	}

	/**
	 * Creates a new instance of the ScrollPanel control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public ScrollPanel(ElementManager screen) {
		this(screen, UIDUtil.getUID(), Vector2f.ZERO, LUtil.LAYOUT_SIZE,
				screen.getStyle("ScrollPanel").getVector4f("resizeBorders"),
				screen.getStyle("ScrollPanel").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the ScrollPanel control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 */
	public ScrollPanel(ElementManager screen, Vector2f position) {
		this(screen, UIDUtil.getUID(), position, LUtil.LAYOUT_SIZE, screen.getStyle("ScrollPanel").getVector4f("resizeBorders"),
				screen.getStyle("ScrollPanel").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the ScrollPanel control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 */
	public ScrollPanel(ElementManager screen, Vector2f position, Vector2f dimensions) {
		this(screen, UIDUtil.getUID(), position, dimensions, screen.getStyle("ScrollPanel").getVector4f("resizeBorders"),
				screen.getStyle("ScrollPanel").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the ScrollPanel control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 * @param resizeBorders
	 *            A Vector4f containing the border information used when
	 *            resizing the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg
	 *            The default image to use for the ScrollPanel's background
	 */
	public ScrollPanel(ElementManager screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		this(screen, UIDUtil.getUID(), position, dimensions, resizeBorders, defaultImg);
	}

	/**
	 * Creates a new instance of the ScrollPanel control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 */
	public ScrollPanel(ElementManager screen, String UID, Vector2f position) {
		this(screen, UID, position, LUtil.LAYOUT_SIZE, screen.getStyle("ScrollPanel").getVector4f("resizeBorders"),
				screen.getStyle("ScrollPanel").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the ScrollPanel control
	 * 
	 * @param screen
	 *            The screen control
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 */
	public ScrollPanel(ElementManager screen, String UID, Vector2f position, Vector2f dimensions) {
		this(screen, UID, position, dimensions, screen.getStyle("ScrollPanel").getVector4f("resizeBorders"),
				screen.getStyle("ScrollPanel").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the ScrollPanel control
	 * 
	 * @param screen
	 *            The screen control
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 * @param resizeBorders
	 *            A Vector4f containing the border information used when
	 *            resizing the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg
	 *            The default image to use for the ScrollPanel's background
	 */
	public ScrollPanel(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
			String defaultImg) {
		super(screen, UID, position, dimensions, Vector4f.ZERO, null);
		// setAsContainerOnly();

		layoutManager = new ScrollPanelLayout();

		gap = screen.getStyle("ScrollPanel").getFloat("gap");
		scrollSize = screen.getStyle("ScrollPanel").getFloat("scrollSize");

		innerBounds = new ScrollPanelBounds(screen, UID + "innerBounds", Vector2f.ZERO, dimensions, resizeBorders, defaultImg);
		innerBounds.setClipPaddingByKey("ScrollPanel", "scrollBoundsClipPadding");
		innerBounds.updateClippingLayers();

		WrappingLayout wrappingLayout = new WrappingLayout();
		wrappingLayout.setOrientation(Orientation.VERTICAL);

		scrollableArea = new Element(screen, UID + "scrollableArea", Vector2f.ZERO, LUtil.LAYOUT_SIZE, Vector4f.ZERO, null);
		scrollableArea.setLayoutManager(wrappingLayout);
		scrollableArea.setTextPaddingByKey("ScrollPanel", "scrollPadding");
		scrollableArea.setTextClipPaddingByKey("ScrollPanel", "scrollPadding");
		scrollableArea.setClipPaddingByKey("ScrollPanel", "scrollClipPadding");

		innerBounds.addChild(scrollableArea);
		scrollableArea.addClippingLayer(innerBounds);
		addChild(innerBounds, null, false, false);

		vScrollBar = new ScrollBar(screen, this, Orientation.VERTICAL);
		vScrollBar.setScrollSize(scrollSize);
		addChild(vScrollBar, null, true, false);
		hScrollBar = new ScrollBar(screen, this, Orientation.HORIZONTAL);
		hScrollBar.setScrollSize(scrollSize);
		addChild(hScrollBar, null, true, false);

		setTextPaddingByKey("ScrollPanel", "textPadding");

		addClippingLayer(this);

		initFlingTimer();

		layoutChildren();
	}

	public void scrollToTop() {
		if (LUtil.getY(scrollableArea) == 0 && getScrollableAreaHeight() < innerBounds.getHeight()) {
			return;
		}
		LUtil.setY(scrollableArea, 0);
		setVThumbPositionToScrollArea();
		onScrollContent(ScrollDirection.Up);
	}

	public void scrollToBottom() {
		if (LUtil.getY(scrollableArea) == 0 && getScrollableAreaHeight() < innerBounds.getHeight()) {
			return;
		}
		LUtil.setY(scrollableArea, -getVerticalScrollDistance());
		setVThumbPositionToScrollArea();
		onScrollContent(ScrollDirection.Down);
	}

	public void scrollYTo(float y) {
		float lastY = LUtil.getY(scrollableArea);
		if (y != lastY) {
			LUtil.setY(scrollableArea, y);
			setVThumbPositionToScrollArea();
			if (lastY > y)
				onScrollContent(ScrollDirection.Down);
			else
				onScrollContent(ScrollDirection.Up);
		}
	}

	public void scrollYBy(float incY) {
		ButtonAdapter vThumb = vScrollBar.getScrollThumb();
		float thumbY = LUtil.getY(vThumb);
		if (incY < 0) {
			if (thumbY > 0) {
				if (thumbY + incY < 0)
					incY -= thumbY + incY;
				LUtil.setY(vThumb, thumbY + incY);
			}
		} else {
			float scrollHeight = vScrollBar.getScrollTrack().getHeight() - vThumb.getHeight();
			if (thumbY < scrollHeight) {
				if (thumbY + incY > scrollHeight)
					incY -= thumbY + incY - scrollHeight;
				LUtil.setY(vThumb, thumbY + incY);
			}
		}
		setScrollAreaPositionTo(vScrollBar.getRelativeScrollAmount(), Orientation.VERTICAL);
	}

	public void scrollToLeft() {
		if (scrollableArea.getX() != 0) {
			scrollableArea.setX(0);
			setHThumbPositionToScrollArea();
			onScrollContent(ScrollDirection.Left);
		}
	}

	public void scrollToRight() {
		if (scrollableArea.getX() != -getHorizontalScrollDistance()) {
			scrollableArea.setX(-getHorizontalScrollDistance());
			setHThumbPositionToScrollArea();
			onScrollContent(ScrollDirection.Right);
		}
	}

	public void scrollXTo(float x) {
		float lastX = scrollableArea.getX();
		scrollableArea.setX(x);
		if (lastX > x)
			onScrollContent(ScrollDirection.Left);
		else
			onScrollContent(ScrollDirection.Right);
	}

	public void scrollXBy(float incX) {
		float lastX = scrollableArea.getX();
		scrollableArea.setX(lastX + incX);
		if (lastX > lastX + incX)
			onScrollContent(ScrollDirection.Left);
		else
			onScrollContent(ScrollDirection.Right);
	}

	public void insertScrollableContent(Element el, int index) {
		scrollableArea.insertChild(el, null, false, true, index);
		el.addClippingLayer(innerBounds);
		el.setClipPadding(innerBounds.getClipPaddingVec());
	}

	public void addScrollableContent(Element el) {
		addScrollableContent(el, true);
	}

	public void addScrollableContent(Element el, boolean reshape) {
		addScrollableContent(el, reshape, null);
	}

	public void addScrollableContent(Element child, Object constraints) {
		addScrollableContent(child, true, constraints);
	}

	public void addScrollableContent(Element el, boolean reshape, Object constraints) {
		scrollableArea.addChild(el, constraints, false, reshape);
		el.addClippingLayer(innerBounds);
		el.setClipPadding(innerBounds.getClipPaddingVec());
		dirtyLayout(false);
		if (reshape) {
			layoutChildren();
		}
	}

	public void removeScrollableContent(Element el) {
		removeScrollableContent(el, true);
	}

	public void removeScrollableContent(Element el, boolean reshape) {
		scrollableArea.removeChild(el, reshape);
	}

	@Deprecated
	public void reshape() {
		dirtyLayout(false);
		layoutChildren();
	}

	public ScrollBarMode getHorizontalScrollBarMode() {
		return horizontalScrollBarMode;
	}

	public void setHorizontalScrollBarMode(ScrollBarMode horizontalScrollBarMode) {
		this.horizontalScrollBarMode = horizontalScrollBarMode;
		dirtyLayout(true);
		layoutChildren();
	}

	public ScrollBarMode getVerticalScrollBarMode() {
		return verticalScrollBarMode;
	}

	public void setVerticalScrollBarMode(ScrollBarMode verticalScrollBarMode) {
		this.verticalScrollBarMode = verticalScrollBarMode;
		dirtyLayout(true);
		layoutChildren();
	}

	public float getGap() {
		return gap;
	}

	public void setGap(float gap) {
		this.gap = gap;
	}

	@Deprecated
	public void setUseVerticalWrap(boolean verticalWrap) {
		// setHorizontalScrollBarMode(ScrollBarMode.Never);
		// layoutChildren();
	}

	public boolean getUseVerticalWrap() {
		// return horizontalScrollBarMode.equals(ScrollBarMode.Never);
		return false;
	}

	public void setScrollSize(float scrollSize) {
		this.scrollSize = scrollSize;
		hScrollBar.setScrollSize(scrollSize);
		vScrollBar.setScrollSize(scrollSize);
		dirtyLayout(false);
		layoutChildren();
	}

	public float getScrollSize() {
		return this.scrollSize;
	}

	@Override
	public void setText(String text) {
		scrollableArea.removeTextElement();
		scrollableArea.setText(text);
		dirtyLayout(false);
		layoutChildren();
	}

	// <editor-fold desc="Vertical Scrolling">
	public float getScrollableAreaVerticalPosition() {
		return innerBounds.getHeight() - (LUtil.getY(scrollableArea) + scrollableArea.getHeight());
	}

	public LayoutManager getScrollContentLayout() {
		return scrollableArea.getLayoutManager();
	}

	public void setScrollContentLayout(LayoutManager scrollAreaLayout) {
		scrollableArea.setLayoutManager(scrollAreaLayout);
	}

	public float getScrollBoundsHeight() {
		return this.innerBounds.getHeight();
	}

	/**
	 * Returns the height difference between the scrollable area's total height
	 * and the
	 * scroll panel's bounds.
	 * 
	 * Note: This returns a negative float value if the scrollable area is
	 * smaller than it's bounds.
	 * 
	 * @return
	 */
	public float getVerticalScrollDistance() {
		float diff = getScrollableAreaHeight() - innerBounds.getHeight();
		return diff;
	}

	// </editor-fold>

	// <editor-fold desc="Horizontal Scrolling">

	public Vector2f getPreferredViewportSize() {

		Vector2f horSize = LUtil.getPreferredSize(hScrollBar);
		Vector2f verSize = LUtil.getPreferredSize(vScrollBar);

		Vector2f viewportSize = new Vector2f(getWidth() - textPadding.x - textPadding.y,
				getHeight() - textPadding.z - textPadding.w);

		Vector2f contentPref = LUtil.getPreferredSize(scrollableArea);

		// Decide if to show the vertical bar
		boolean showVertical = verticalScrollBarMode == ScrollBarMode.Always
				|| (verticalScrollBarMode == ScrollBarMode.Auto && contentPref.y > innerBounds.getHeight());

		float vx = scrollSize == -1 ? verSize.x : scrollSize;

		boolean showHorizontal = horizontalScrollBarMode == ScrollBarMode.Always || (horizontalScrollBarMode == ScrollBarMode.Auto
				&& contentPref.x > innerBounds.getWidth() - (showVertical ? vx + gap : 0));

		if (showVertical) {
			viewportSize.x -= vx + gap;
			showHorizontal = horizontalScrollBarMode == ScrollBarMode.Always
					|| (horizontalScrollBarMode == ScrollBarMode.Auto && contentPref.x > innerBounds.getWidth());

		}

		if (showHorizontal) {
			float hy = scrollSize == -1 ? horSize.y : scrollSize;
			viewportSize.y -= hy + gap;
		}

		return viewportSize;
	}

	public float getScrollableAreaHorizontalPosition() {
		return innerBounds.getWidth() - (scrollableArea.getX() + scrollableArea.getWidth());
	}

	public float getScrollBoundsWidth() {
		return this.innerBounds.getWidth();
	}

	public float getScrollableAreaHeight() {
		return scrollableArea.getHeight();
	}

	public float getScrollableAreaWidth() {
		return scrollableArea.getWidth();
	}

	@Override
	public float getScrollBounds(Orientation orientation) {
		return orientation == Orientation.VERTICAL ? getScrollBoundsHeight() : getScrollBoundsWidth();
	}

	@Override
	public float getScrollableArea(Orientation orientation) {
		return orientation == Orientation.VERTICAL ? getScrollableAreaHeight() : getScrollableAreaWidth();
	}

	@Override
	public void setScrollAreaPositionTo(float relativeScrollAmount, Orientation orientation) {
		relativeScrollAmount = FastMath.clamp(relativeScrollAmount, 0, 1);
		if (orientation == Orientation.VERTICAL) {
			float lastY = LUtil.getY(scrollableArea);
			float newY = Math.round(-(getVerticalScrollDistance() * relativeScrollAmount));
			if (lastY != newY) {
				LUtil.setY(scrollableArea, newY);
				if (lastY > -(getVerticalScrollDistance() * relativeScrollAmount))
					onScrollContent(ScrollDirection.Up);
				else
					onScrollContent(ScrollDirection.Down);
			}
		} else {
			float lastX = scrollableArea.getX();
			float newX = Math.min(0, Math.round(-(getHorizontalScrollDistance() * relativeScrollAmount)));
			if (lastX != newX) {
				LUtil.setX(scrollableArea, newX);
				if (lastX < -(getHorizontalScrollDistance() * relativeScrollAmount))
					onScrollContent(ScrollDirection.Left);
				else
					onScrollContent(ScrollDirection.Right);
			}
		}
	}

	/**
	 * Returns the width difference between the scrollable area's total width
	 * and the
	 * scroll panel's bounds.
	 * 
	 * Note: This returns a negative float value if the scrollable area is
	 * smaller than it's bounds.
	 * 
	 * @return
	 */
	public float getHorizontalScrollDistance() {
		float diff = getScrollableAreaWidth() - innerBounds.getWidth();
		return diff;
	}

	public void setVThumbPositionToScrollArea() {
		float relY = (FastMath.abs(LUtil.getY(scrollableArea)) / getVerticalScrollDistance());
		float h = (vScrollBar.getScrollTrack().getHeight() - vScrollBar.getScrollThumb().getHeight()) * relY;
		LUtil.setY(vScrollBar.getScrollThumb(), h);
	}

	public void setHThumbPositionToScrollArea() {
		float relX = (FastMath.abs(scrollableArea.getX()) / getHorizontalScrollDistance());
		hScrollBar.getScrollThumb()
				.setX(Math.round((hScrollBar.getScrollTrack().getWidth() - hScrollBar.getScrollThumb().getWidth()) * relX));
	}

	// </editor-fold>

	public void setUseContentPaging(boolean pagingEnabled) {
		this.pagingEnabled = pagingEnabled;
		checkPagedContent(null);
	}

	public boolean getUseContentPaging() {
		return pagingEnabled;
	}

	public void setButtonInc(int buttonInc) {
		this.buttonInc = buttonInc;
	}

	public int getButtonInc() {
		return this.buttonInc;
	}

	public void setTrackInc(int trackInc) {
		this.trackInc = trackInc;
	}

	public int getTrackInc() {
		return this.trackInc;
	}

	public ScrollBar getVerticalScrollBar() {
		return this.vScrollBar;
	}

	public ScrollBar getHorizontalScrollBar() {
		return this.hScrollBar;
	}

	public Element getScrollBounds() {
		return this.innerBounds;
	}

	public Element getScrollableArea() {
		return this.scrollableArea;
	}

	public void setFlingEnabled(boolean flingEnabled) {
		this.flingEnabled = flingEnabled;
	}

	public boolean getFlingEnabled() {
		return this.flingEnabled;
	}

	public void onScrollContentHook(ScrollDirection direction) {
	}

	private void onScrollContent(ScrollDirection direction) {
		if (pagingEnabled) {
			checkPagedContent(direction);
		}
		onScrollContentHook(direction);
	}

	protected void dirtyScrollContent() {
		dirtyLayout(false);
		vScrollBar.dirtyLayout(false);
		hScrollBar.dirtyLayout(false);
		innerBounds.dirtyLayout(false);
		scrollableArea.dirtyLayout(false);
		layoutChildren();
	}

	@Override
	protected final void onAfterLayout() {
		// if (pagingEnabled)
		// checkPagedContent(null);
		setVThumbPositionToScrollArea();
		onAfterScrollPanelLayout();
	}

	protected void onAfterScrollPanelLayout() {

	}

	protected void checkPagedContent(ScrollDirection direction) {
		// TODO don't use hide/show . have own mechanism so paging doesnt
		// interfere with user visibility requirements
		for (Element el : scrollableArea.getElementsAsMap().values()) {
			if (direction == null || direction == ScrollDirection.Up || direction == ScrollDirection.Down) {
				if (LUtil.getY(el) + el.getHeight() + LUtil.getY(scrollableArea) < 0
						|| LUtil.getY(el) + LUtil.getY(scrollableArea) > innerBounds.getHeight()) {
					if (el.getIsVisible())
						el.hide();
				} else {
					if (!el.getIsVisible())
						el.show();
				}
			}
			if (direction == null || direction == ScrollDirection.Left || direction == ScrollDirection.Right) {
				if (el.getX() + el.getWidth() + scrollableArea.getX() < 0
						|| el.getX() + scrollableArea.getX() > innerBounds.getWidth()) {
					if (el.getIsVisible())
						el.hide();
				} else {
					if (!el.getIsVisible())
						el.show();
				}
			}
		}
	}

	private void initFlingTimer() {
		flingTimer = new GameTimer() {
			@Override
			public void timerUpdateHook(float tpf) {
				float currentY = getScrollableAreaVerticalPosition();
				float nextInc = 15 * flingSpeed * (1f - this.getPercentComplete());

				if (flingDir) {
					float nextY = currentY + nextInc;
					if (nextY <= scrollableArea.getHeight() && nextY >= innerBounds.getHeight()) {
						scrollYTo(nextY);
						setVThumbPositionToScrollArea();
					}
				} else {
					float nextY = currentY - nextInc;
					if (nextY <= scrollableArea.getHeight() && nextY >= innerBounds.getHeight()) {
						scrollYTo(nextY);
						setVThumbPositionToScrollArea();
					}
				}
			}

			@Override
			public void onComplete(float time) {

			}
		};
		flingTimer.setInterpolation(Interpolation.exp5Out);
	}

	public class ScrollPanelBounds extends Element implements MouseWheelListener, TouchListener, FlingListener {
		public ScrollPanelBounds(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
				String defaultImg) {
			super(screen, UID, position, dimensions, resizeBorders, defaultImg);
			setIgnoreMouseWheelMove(false);
			// setLayoutManager(new XYLayoutManager());
		}

		@Override
		public Vector2f getPreferredDimensions() {
			return scrollableArea.getPreferredDimensions();
		}

		@Override
		public void onMouseWheelPressed(MouseButtonEvent evt) {
			evt.setConsumed();
		}

		@Override
		public void onMouseWheelReleased(MouseButtonEvent evt) {
			evt.setConsumed();
		}

		@Override
		public void onMouseWheelUp(MouseMotionEvent evt) {
			if (getVerticalScrollDistance() > 0) {
				scrollYBy(getTrackInc());
				evt.setConsumed();
			}
		}

		@Override
		public void onMouseWheelDown(MouseMotionEvent evt) {
			if (getVerticalScrollDistance() > 0) {
				scrollYBy(-getTrackInc());
				evt.setConsumed();
			}
		}

		// <editor-fold desc="Android Events">
		@Override
		public void onFling(TouchEvent evt) {
			if (flingEnabled && (evt.getDeltaY() > 0.2f || evt.getDeltaY() < -0.2f)) {
				if (!screen.getAnimManager().hasGameTimer(flingTimer)) {
					flingTimer.reset(false);
					flingDir = (evt.getDeltaY() < 0) ? true : false;
					flingSpeed = FastMath.abs(evt.getDeltaY());
					screen.getAnimManager().addGameTimer(flingTimer);
				}
			}
		}

		@Override
		public void onTouchDown(TouchEvent evt) {
			if (screen.getAnimManager().hasGameTimer(flingTimer)) {
				flingTimer.endGameTimer();
				screen.getAnimManager().removeGameTimer(flingTimer);
			}
			if (flingEnabled) {
				touchStartY = getScrollableAreaVerticalPosition();
				touchOffsetY = evt.getY() - touchStartY;
			}
		}

		@Override
		public void onTouchMove(TouchEvent evt) {
			if (flingEnabled) {
				float nextY = evt.getY() - touchOffsetY;
				if (nextY <= getScrollableAreaHeight() && nextY >= innerBounds.getHeight()) {
					scrollYTo(nextY);
					setVThumbPositionToScrollArea();
					touchEndY = getScrollableAreaVerticalPosition();
				}
			}
		}

		@Override
		public void onTouchUp(TouchEvent evt) {
		}
	}

	public static class ScrollPanelLayout extends AbstractScrollLayout {

		public Vector2f minimumSize(Element parent) {
			Vector2f min = LUtil.getContainerMinimumDimensions(((ScrollPanel) parent).getScrollableArea());
			min = min.addLocal(parent.getTextPaddingVec().x, parent.getTextPaddingVec().z).addLocal(parent.getTextPaddingVec().y,
					parent.getTextPaddingVec().w);
			return min;
		}

		public Vector2f maximumSize(Element parent) {
			Vector2f mx = LUtil.getContainerMaximumDimensions(((ScrollPanel) parent).getScrollableArea());
			mx = mx.addLocal(parent.getTextPaddingVec().x, parent.getTextPaddingVec().z).addLocal(parent.getTextPaddingVec().y,
					parent.getTextPaddingVec().w);
			return mx;
		}

		public Vector2f preferredSize(Element parent) {

			ScrollPanel sp = (ScrollPanel) parent;

			/*  TODO this needs fixing. When the vertical scrollbar MIGHT show, the initial preferred size will be 
			 wrong. Evident in ZMenu when it exceeds  the screen heigh */

			Vector2f horSize = LUtil.getPreferredSize(sp.hScrollBar);
			Vector2f verSize = LUtil.getPreferredSize(sp.vScrollBar);

			Vector2f contentPref = LUtil.getContainerPreferredDimensions(sp.getScrollableArea());
			Vector2f viewportSize = new Vector2f(sp.getWidth() - sp.textPadding.x - sp.textPadding.y,
					sp.getHeight() - sp.textPadding.z - sp.textPadding.w);

			// Decide if to show the vertical bar
			boolean showVertical = sp.verticalScrollBarMode == ScrollBarMode.Always
					|| (sp.verticalScrollBarMode == ScrollBarMode.Auto && contentPref.y > viewportSize.y);

			if (showVertical) {
				contentPref.x += verSize.x + sp.gap;
			}

			return contentPref.addLocal(sp.getTextPaddingVec().x + sp.getTextPaddingVec().y,
					sp.getTextPaddingVec().z + sp.getTextPaddingVec().w);

		}

		public void layout(Element childElement) {

			ScrollPanel sp = (ScrollPanel) childElement;

			Vector2f horSize = LUtil.getPreferredSize(sp.hScrollBar);
			Vector2f verSize = LUtil.getPreferredSize(sp.vScrollBar);

			float wasV = sp.vScrollBar.getRelativeScrollAmount();
			float wasH = sp.hScrollBar.getRelativeScrollAmount();

			// The size of the viewport (if there were no scrollbars visibile)
			// Vector2f viewportSize = sp.getPreferredViewportSize();
			Vector2f viewportSize = new Vector2f(sp.getWidth() - sp.textPadding.x - sp.textPadding.y,
					sp.getHeight() - sp.textPadding.z - sp.textPadding.w);

			// Initially set the viewport and the scrollable area to the same
			// size
			LUtil.setBounds(sp.innerBounds, sp.textPadding.x, sp.textPadding.z, viewportSize.x, viewportSize.y);
			LUtil.setDimensions(sp.scrollableArea, viewportSize);
			Vector2f contentPref = LUtil.getPreferredSize(sp.scrollableArea);

			// Decide if to show the vertical bar
			boolean showVertical = sp.verticalScrollBarMode == ScrollBarMode.Always
					|| (sp.verticalScrollBarMode == ScrollBarMode.Auto && contentPref.y > sp.innerBounds.getHeight());

			float vx = sp.scrollSize == -1 ? verSize.x : sp.scrollSize;

			boolean showHorizontal = sp.horizontalScrollBarMode == ScrollBarMode.Always
					|| (sp.horizontalScrollBarMode == ScrollBarMode.Auto
							&& contentPref.x > sp.innerBounds.getWidth() - (showVertical ? vx + sp.gap : 0));

			if (showVertical) {
				viewportSize.x -= vx + sp.gap;

				// Update the content preferred size now we know a vertical bar
				// will be present
				LUtil.setBounds(sp.innerBounds, sp.textPadding.x, sp.textPadding.z, viewportSize.x, viewportSize.y);
				LUtil.setDimensions(sp.scrollableArea, viewportSize);
				contentPref = LUtil.getContainerPreferredDimensions(sp.scrollableArea);

				// Test again if to show horizontal

				showHorizontal = sp.horizontalScrollBarMode == ScrollBarMode.Always
						|| (sp.horizontalScrollBarMode == ScrollBarMode.Auto && contentPref.x > sp.innerBounds.getWidth());

				sp.vScrollBar.show();
				LUtil.setBounds(sp.vScrollBar, sp.getWidth() - vx - sp.textPadding.y, sp.textPadding.z, vx, viewportSize.y);
			} else
				sp.vScrollBar.hide();

			if (showHorizontal) {
				// If showing horizontal scrollbar, reduce the height
				float hy = sp.scrollSize == -1 ? horSize.y : sp.scrollSize;
				viewportSize.y -= hy + sp.gap;

				// Update the content preferred size now we know a horizontal
				// bar will be present
				LUtil.setBounds(sp.innerBounds, sp.textPadding.x, sp.textPadding.z, viewportSize.x, viewportSize.y);
				LUtil.setDimensions(sp.scrollableArea, viewportSize);
				contentPref = LUtil.getPreferredSize(sp.scrollableArea);

				// Show the bar and shape it
				sp.hScrollBar.show();
				LUtil.setBounds(sp.hScrollBar, sp.textPadding.x, sp.getHeight() - sp.textPadding.w - hy, viewportSize.x, hy);
			} else {
				sp.hScrollBar.hide();
			}

			LUtil.setBounds(sp.innerBounds, sp.textPadding.x, sp.textPadding.z, viewportSize.x, viewportSize.y);
			LUtil.setDimensions(sp.scrollableArea, contentPref.x, contentPref.y);

			if (!showHorizontal && sp.scrollableArea.getX() != 0) {
				LUtil.setPosition(sp.scrollableArea, 0, LUtil.getY(sp.scrollableArea));
			}

			if (contentPref.y < viewportSize.y)
				sp.scrollToTop();
			else
				sp.setScrollAreaPositionTo(wasV, Orientation.VERTICAL);

			if (contentPref.x < viewportSize.x)
				sp.scrollToLeft();
			else
				sp.setScrollAreaPositionTo(wasH, Orientation.HORIZONTAL);

		}

		public void remove(Element child) {
		}

		public void constrain(Element child, Object constraints) {
		}
	}

}
