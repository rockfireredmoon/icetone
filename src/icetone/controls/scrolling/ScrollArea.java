/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icetone.controls.scrolling;

import com.jme3.font.BitmapFont;
import com.jme3.font.LineWrapMode;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.Screen;
import icetone.core.layout.AbstractLayout;
import icetone.core.layout.FlowLayout;
import icetone.core.layout.LUtil;
import icetone.core.layout.LayoutManager;
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
public class ScrollArea extends Element implements MouseWheelListener, TouchListener, FlingListener {
	protected Element scrollableArea;
	private boolean isTextOnly = true;
	private boolean isScrollable = true;
	private boolean atBottom = true;
	private VScrollBar vScrollBar;
	protected float scrollSize;
	private boolean scrollHidden = false;
	protected float scrollBarGap = 0;
	protected GameTimer flingTimer;
	private boolean flingEnabled = true;
	float flingSpeed = 0;
	boolean flingDir = true;
	float touchOffsetY = 0;
	float touchStartY = 0;
	float touchEndY = 0;

	/**
	 * Creates a new instance of the ScrollArea control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param isTextOnly
	 *            Boolean defining if the scroll area will contain other
	 *            Elements or use formatted text
	 */
	public ScrollArea() {
		this(false);
	}

	/**
	 * Creates a new instance of the ScrollArea control
	 * 
	 * @param isTextOnly
	 *            Boolean defining if the scroll area will contain other
	 *            Elements or use formatted text
	 */
	public ScrollArea(boolean isTextOnly) {
		this(Screen.get(), isTextOnly);
	}

	/**
	 * Creates a new instance of the ScrollArea control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param isTextOnly
	 *            Boolean defining if the scroll area will contain other
	 *            Elements or use formatted text
	 */
	public ScrollArea(ElementManager screen, boolean isTextOnly) {
		this(screen, UIDUtil.getUID(), Vector2f.ZERO, LUtil.LAYOUT_SIZE, screen.getStyle("ScrollArea").getVector4f("resizeBorders"),
				screen.getStyle("ScrollArea").getString("defaultImg"), isTextOnly);
	}

	/**
	 * Creates a new instance of the ScrollArea control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param isTextOnly
	 *            Boolean defining if the scroll area will contain other
	 *            Elements or use formatted text
	 */
	public ScrollArea(ElementManager screen, Vector2f position, boolean isTextOnly) {
		this(screen, UIDUtil.getUID(), position, LUtil.LAYOUT_SIZE, screen.getStyle("ScrollArea").getVector4f("resizeBorders"),
				screen.getStyle("ScrollArea").getString("defaultImg"), isTextOnly);
	}

	/**
	 * Creates a new instance of the ScrollArea control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 * @param isTextOnly
	 *            Boolean defining if the scroll area will contain other
	 *            Elements or use formatted text
	 */
	public ScrollArea(ElementManager screen, Vector2f position, Vector2f dimensions, boolean isTextOnly) {
		this(screen, UIDUtil.getUID(), position, dimensions, screen.getStyle("ScrollArea").getVector4f("resizeBorders"),
				screen.getStyle("ScrollArea").getString("defaultImg"), isTextOnly);
	}

	/**
	 * Creates a new instance of the ScrollArea control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 * @param resizeBorders
	 *            A Vector4f containg the border information used when resizing
	 *            the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg
	 *            The default image to use for the Slider's track
	 * @param isTextOnly
	 *            Boolean defining if the scroll area will contain other
	 *            Elements or use formatted text
	 */
	public ScrollArea(ElementManager screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg,
			boolean isTextOnly) {
		this(screen, UIDUtil.getUID(), position, dimensions, resizeBorders, defaultImg, isTextOnly);
	}

	/**
	 * Creates a new instance of the ScrollArea control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param isTextOnly
	 *            Boolean defining if the scroll area will contain other
	 *            Elements or use formatted text
	 */
	public ScrollArea(ElementManager screen, String UID, Vector2f position, boolean isTextOnly) {
		this(screen, UID, position, LUtil.LAYOUT_SIZE, screen.getStyle("ScrollArea").getVector4f("resizeBorders"),
				screen.getStyle("ScrollArea").getString("defaultImg"), isTextOnly);
	}

	/**
	 * Creates a new instance of the ScrollArea control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 * @param isTextOnly
	 *            Boolean defining if the scroll area will contain other
	 *            Elements or use formatted text
	 */
	public ScrollArea(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, boolean isTextOnly) {
		this(screen, UID, position, dimensions, screen.getStyle("ScrollArea").getVector4f("resizeBorders"),
				screen.getStyle("ScrollArea").getString("defaultImg"), isTextOnly);
	}

	/**
	 * Creates a new instance of the ScrollArea control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 * @param resizeBorders
	 *            A Vector4f containg the border information used when resizing
	 *            the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg
	 *            The default image to use for the Slider's track
	 * @param isTextOnly
	 *            Boolean defining if the scroll area will contain other
	 *            Elements or use formatted text
	 */
	public ScrollArea(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
			String defaultImg, boolean isTextOnly) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);
		setContainerDimensions(getDimensions());

		this.isTextOnly = isTextOnly;

		setIgnoreMouseWheelMove(false);

		// Load default font info
		setFontColor(screen.getStyle("ScrollArea").getColorRGBA("fontColor"));
		setFontSize(screen.getStyle("ScrollArea").getFloat("fontSize"));
		setTextAlign(BitmapFont.Align.valueOf(screen.getStyle("ScrollArea").getString("textAlign")));
		setTextVAlign(BitmapFont.VAlign.valueOf(screen.getStyle("ScrollArea").getString("textVAlign")));
		setTextWrap(LineWrapMode.valueOf(screen.getStyle("ScrollArea").getString("textWrap")));

		scrollBarGap = screen.getStyle("ScrollArea#VScrollBar").getFloat("gap");
		scrollSize = screen.getStyle("ScrollArea#VScrollBar").getFloat("defaultControlSize");

		// orgWidth = getWidth();

		setWidth(getWidth() - scrollSize);

		if (!isTextOnly) {
			createScrollableArea();
		} else {
			setTextPaddingByKey("ScrollArea", "textPadding");
			setTextClipPaddingByKey("ScrollArea", "textPadding");
			setText(" ");
		}

		vScrollBar = new VScrollBar(screen, UID + ":vScroll", new Vector2f(getWidth() + scrollBarGap, 0),
				new Vector2f(scrollSize, getHeight()));

		addChild(vScrollBar);

		setVScrollBar(vScrollBar);

		flingTimer = new GameTimer() {
			@Override
			public void timerUpdateHook(float tpf) {
				float currentY = getScrollablePosition();
				float nextInc = 15 * flingSpeed * (1f - this.getPercentComplete());

				if (flingDir) {
					float nextY = currentY + nextInc;
					if (nextY <= getScrollableHeight() && nextY >= getHeight() - getPadding()) {
						scrollYTo(nextY);
						vScrollBar.setThumbByPosition();
					}
				} else {
					float nextY = currentY - nextInc;
					if (nextY <= getScrollableHeight() && nextY >= getHeight() - getPadding()) {
						scrollYTo(nextY);
						vScrollBar.setThumbByPosition();
					}
				}
			}

			@Override
			public void onComplete(float time) {

			}
		};
		flingTimer.setInterpolation(Interpolation.exp5Out);
		setClipPadding(screen.getStyle("ScrollArea").getFloat("scrollAreaPadding"));
	}

	public LayoutManager getScrollAreaLayout() {
		return scrollableArea.getLayoutManager();
	}

	public void setScrollAreaLayout(LayoutManager scrollAreaLayout) {
		scrollableArea.setLayoutManager(scrollAreaLayout);
	}

	public boolean isAtBottom() {
		return atBottom;
	}

	protected void onAtBottom(boolean atBottom) {
	}

	public void setScrollBarWidth(float width) {
		this.scrollSize = width;
		this.getVScrollBar().setWidth(width);
		this.getVScrollBar().getScrollTrack().setWidth(width);
		this.getVScrollBar().getScrollTrack().setHeight(getHeight() - (width * 2));
		this.getVScrollBar().getScrollTrack().setY(width);
		this.getVScrollBar().getScrollButtonUp().setDimensions(width, width);
		this.getVScrollBar().getScrollButtonUp().setY(getHeight() - width);
		this.getVScrollBar().getScrollThumb().setWidth(width);
		this.getVScrollBar().getScrollButtonDown().setDimensions(width, width);

		this.getVScrollBar().getScrollButtonUp().getButtonIcon().centerToParent();
		this.getVScrollBar().getScrollButtonDown().getButtonIcon().centerToParent();

		if (getVScrollBar().getIsVisible())
			this.setWidth(this.orgDimensions.x - width);
		else
			this.setWidth(this.orgDimensions.x);
		// this.adjustWidthForScroll();
	}

	private void createScrollableArea() {
		final float insets = screen.getStyle("ScrollArea").getFloat("scrollAreaInsets");

		FlowLayout scrollContentLayout = new FlowLayout(0, BitmapFont.VAlign.Top);
		((FlowLayout) scrollContentLayout).setFill(true);

		scrollableArea = new Element(screen, getUID() + ":scrollable", new Vector2f(0, 0), LUtil.LAYOUT_SIZE,
				new Vector4f(insets, insets, insets, insets), null);
		scrollableArea.setLayoutManager(scrollContentLayout);
		scrollableArea.setIsResizable(false);
		scrollableArea.setIgnoreMouse(true);
		scrollableArea.setIsMovable(false);

		// Load default font info
		scrollableArea.setFontColor(screen.getStyle("ScrollArea").getColorRGBA("fontColor"));
		scrollableArea.setFontSize(screen.getStyle("ScrollArea").getFloat("fontSize"));
		scrollableArea.setTextAlign(BitmapFont.Align.valueOf(screen.getStyle("ScrollArea").getString("textAlign")));
		scrollableArea.setTextVAlign(BitmapFont.VAlign.valueOf(screen.getStyle("ScrollArea").getString("textVAlign")));
		scrollableArea.setTextWrap(LineWrapMode.valueOf(screen.getStyle("ScrollArea").getString("textWrap")));
		scrollableArea.setTextPaddingByKey("ScrollArea", "textPadding");
		scrollableArea.setTextClipPaddingByKey("ScrollArea", "textPadding");

		scrollableArea.addClippingLayer(this);

		this.addChild(scrollableArea);
	}

	/**
	 * Returns the Element that was created as a scrollable area for ScrollArea
	 * NOT flagged as isTextOnly
	 * 
	 * @return Element
	 */
	public Element getScrollableArea() {
		return this.scrollableArea;
	}

	/**
	 * Returns if the ScrollArea is text only
	 * 
	 * @return boolean
	 */
	public boolean getIsTextOnly() {
		return isTextOnly;
	}

	public void setFlingEnabled(boolean enabled) {
		this.flingEnabled = enabled;
	}

	public boolean getFlingEnabled() {
		return this.flingEnabled;
	}

	private void setVScrollBar(VScrollBar vScrollBar) {
		this.vScrollBar = vScrollBar;
		vScrollBar.setScrollableArea(this);
	}

	/**
	 * Returns the Vertical Scroll Bar
	 * 
	 * @return VScrollBar
	 */
	public VScrollBar getVScrollBar() {
		return this.vScrollBar;
	}

	/**
	 * Adds an Element as a child to the ScrollArea. This is usable by
	 * ScrollAreas NOT flagged for isTextOnly
	 * 
	 * @param child
	 *            Element
	 */
	public void addScrollableChild(Element child) {
		boolean atBottomBeforeNewChild = atBottom;
		scrollableArea.addChild(child);
		if (atBottomBeforeNewChild) {
			scrollYTo(0);
		}
		child.setIgnoreMouseWheel(true);
	}

	/**
	 * Sets the padding for the ScrollArea
	 * 
	 * @param padding
	 *            float
	 */
	public void setPadding(float padding) {
		if (isTextOnly) {
			setTextPadding(padding);
			setTextClipPadding(padding);
		} else {
			scrollableArea.setTextPadding(padding);
			scrollableArea.setTextClipPadding(padding);
		}
	}

	/**
	 * Returns the padding used for the ScollArea
	 * 
	 * @return float
	 */
	public float getPadding() {
		if (isTextOnly) {
			return getTextPadding();
		} else {
			return scrollableArea.getTextPadding();
		}
	}

	/**
	 * Returns the current height of the scrollable area
	 * 
	 * @return float
	 */
	public float getScrollableHeight() {
		if (isTextOnly) {
			return textElement.getHeight() + getTextPaddingVec().z + getTextPaddingVec().w;
		} else {
			return scrollableArea.getHeight() + getTextPaddingVec().z + getTextPaddingVec().w;
		}
	}

	/**
	 * Returns the current y position of the scrollable area
	 * 
	 * @return float
	 */
	public float getScrollablePosition() {
		if (isTextOnly) {
			return textElement.getLocalTranslation().y;
		} else {
			return scrollableArea.getY();
		}
	}

	@Override
	public void controlResizeHook() {
		if (vScrollBar != null) {
			vScrollBar.setThumbScale();
		}
		adjustWidthForScroll();
		if (scrollableArea != null)
			if (scrollableArea.getY() > getResizeBorderNorthSize()
					&& getScrollableHeight() + getResizeBorderNorthSize() > getHeight())
				scrollToBottom();
	}

	@Override
	public void setControlClippingLayer(Element clippingLayer) {
		for (Element el : elementChildren.values()) {
			el.setControlClippingLayer(clippingLayer);
		}
	}

	/**
	 * Internal use - Used to readjust the width of the scrollarea when
	 * hiding/showing scroll bars
	 */
	public final void adjustWidthForScroll() {
		if (vScrollBar.getParent() == null && !scrollHidden) {
			setActualWidth(getWidth() + vScrollBar.getWidth() + scrollBarGap);
			scrollHidden = true;
		} else if (vScrollBar.getParent() != null && scrollHidden) {
			setActualWidth(getWidth() - vScrollBar.getWidth() - scrollBarGap);
			vScrollBar.setX(getWidth() + scrollBarGap);
			scrollHidden = false;
		} else if (vScrollBar.getParent() != null) {
			vScrollBar.setX(getWidth() + scrollBarGap);
		}
		onAdjustWidthForScroll();
	}

	/**
	 * Scrolls the scrollbar thumb to the specified Y coord
	 * 
	 * @param y
	 */
	public void scrollThumbYTo(float y) {
		adjustWidthForScroll();
		vScrollBar.scrollYTo(y);
	}

	/**
	 * Scrolls the Scrollable Area to the specified Y coord
	 * 
	 * @param y
	 *            float
	 */
	public void scrollYTo(float y) {
		adjustWidthForScroll();
		if (scrollableArea == null) {
			textElement.setLocalTranslation(textElement.getLocalTranslation().setY((int) y));
		} else {
			scrollableArea.setY(0);
			scrollableArea.setY((int) y);
		}
		controlScrollHook();
		boolean nowAtBottom = y == 0 || getVScrollBar().getParent() == null;
		if (nowAtBottom != atBottom) {
			atBottom = nowAtBottom;
			onAtBottom(atBottom);
		}
	}

	/**
	 * Overridable method for hooking the scroll event
	 */
	public void controlScrollHook() {
	}

	/**
	 * To be used with interval calls. Scrolls the Scrollable Area by the
	 * provided value
	 * 
	 * @param yInc
	 *            float
	 */
	public void scrollYBy(float yInc) {
		adjustWidthForScroll();
		if (scrollableArea == null) {
			float nextY = textElement.getLocalTranslation().getY() + yInc;
			textElement.setLocalTranslation(textElement.getLocalTranslation().setY(yInc));
		} else {
			scrollableArea.setY(scrollableArea.getY() + yInc);
		}
	}

	/**
	 * Scrolls to the bottom of the Scrollable Area
	 */
	public void scrollToBottom() {
		adjustWidthForScroll();
		vScrollBar.scrollToBottom();
	}

	/**
	 * Scrolls to the top of the Scrollable Area
	 */
	public void scrollToTop() {
		adjustWidthForScroll();
		vScrollBar.scrollToTop();
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
		if (vScrollBar != null) {
			vScrollBar.scrollByYInc(-vScrollBar.getTrackInc());
		}
		evt.setConsumed();
	}

	@Override
	public void onMouseWheelDown(MouseMotionEvent evt) {
		if (vScrollBar != null) {
			vScrollBar.scrollByYInc(vScrollBar.getTrackInc());
		}
		evt.setConsumed();
	}

	protected void onAdjustWidthForScroll() {
		// Hook called when width adjusts because of scrollbar visibility
	}

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
			touchStartY = getScrollablePosition();
			touchOffsetY = evt.getY() - touchStartY;
		}
	}

	@Override
	public void onTouchMove(TouchEvent evt) {
		if (flingEnabled) {
			float nextY = evt.getY() - touchOffsetY;
			if (nextY <= getScrollableHeight() && nextY >= getHeight() - (this.getPadding())) {
				scrollYTo(nextY);
				vScrollBar.setThumbByPosition();
				touchEndY = getScrollablePosition();
			}
		}
	}

	@Override
	public void onTouchUp(TouchEvent evt) {

	}

	class ScrollAreaLayout extends AbstractLayout {

		public Vector2f minimumSize(Element parent) {
			return null;
		}

		public Vector2f maximumSize(Element parent) {
			return null;
		}

		public Vector2f preferredSize(Element parent) {
			Vector2f sz = null;
			if (LUtil.LAYOUT_SIZE.equals(getOrgDimensions()) && prefDimensions == null) {
				if (getIsTextOnly()) {
					float scale = 1;
					float preferredHeight = (font.getCharSet().getLineHeight() * scale);
					float preferredWidth;
					if (getTextElement() != null && getTextElement().getLineCount() > 1) {
						preferredHeight = preferredHeight * getTextElement().getLineCount();
						preferredWidth = getTextElement().getLineWidth() + borders.z + borders.y + (getPadding() * 2);
					} else {
						preferredWidth = (font.getLineWidth(getText()) * scale) + borders.x + borders.w + (getPadding() * 2);
					}
					preferredHeight += (getPadding() * 2);
					sz = new Vector2f(preferredWidth, preferredHeight);
				} else {
					if (getScrollAreaLayout() != null) {
						sz = getScrollAreaLayout().preferredSize(parent);
					}
				}
			}
			if (sz != null && sz.y > getHeight()) {
				sz.x += scrollBarGap + scrollSize;
			}
			return sz;
		}

		public void layout(Element childElement) {
			boolean wasAtBottom = isAtBottom();
			if (getScrollAreaLayout() != null && !getIsTextOnly()) {
				// TODO - i think this is a bug in ScrollArea, it should be
				// setting clipPadding
				scrollableArea.setClipPadding(screen.getStyle("ScrollArea").getFloat("scrollAreaPadding"));
				final Vector2f preferredSize = LUtil.getPreferredSize(childElement).clone();
				preferredSize.x = getWidth() - borders.y - borders.z;
				LUtil.setBounds(scrollableArea, borders.y, borders.x, preferredSize.x, preferredSize.y);
				getScrollableArea().layoutChildren();
				scrollableArea.addClippingLayer(ScrollArea.this);
				scrollableArea.updateClipping();
			} else if (getIsTextOnly()) {
			}

			if (getVScrollBar() != null) {
				getVScrollBar().setThumbScale();
			}
			adjustWidthForScroll();
			LUtil.positionScrollbars(ScrollArea.this, getContainerDimensions());

			if (wasAtBottom) {
				scrollToBottom();
			}
		}

		public void remove(Element child) {
		}

		public void constrain(Element child, Object constraints) {
		}
	}
}
