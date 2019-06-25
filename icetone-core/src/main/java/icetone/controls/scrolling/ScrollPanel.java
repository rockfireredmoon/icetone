/**
 * ICETONE - A GUI Library for JME3 based on a heavily modified version of 
 * Tonegod's 'Tonegodgui'.  
 * 
 * Copyright (c) 2013, t0neg0d
 * Copyright (c) 2016, Emerald Icemoon
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of the FreeBSD Project.
 */

package icetone.controls.scrolling;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.css.CSSPrimitiveValue;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;
import org.xhtmlrenderer.css.sheet.StylesheetInfo;

import com.jme3.input.KeyInput;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.buttons.Button;
import icetone.controls.text.TextArea;
import icetone.core.AbstractGenericLayout;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Element;
import icetone.core.Layout;
import icetone.core.Layout.LayoutType;
import icetone.core.Orientation;
import icetone.core.Size;
import icetone.core.event.ElementEvent.Type;
import icetone.core.event.FlingListener;
import icetone.core.event.TouchListener;
import icetone.core.utils.ClassUtil;
import icetone.core.utils.GameTimer;
import icetone.core.utils.MathUtil;
import icetone.css.CssExtensions;
import icetone.css.CssUtil;
import icetone.effects.Interpolation;

/**
 * Accepts one or more child elements and provides a clipped viewport over them.
 * When the total size of the child elements exceeds the size of the view port,
 * scrollbars will appear and allow the view port original to be changed.
 * <p>
 * Content should not be added using the usual addElement method, instead use
 * {@link #addScrollableContent(BaseElement)} and it's relatives. Similar, the
 * layout of the element that holds the children use
 * {@link #setScrollContentLayout(Layout)}.
 * <p>
 * When <strong>Content Paging</strong> is enabled (now the default), any
 * elements that lay outside of the viewport are automatically removed from
 * scene, and are re-added when they become inside the view port. This
 * dramatically improves performance when the scroll panel contains many
 * children.
 * 
 * @author t0neg0d
 * @author rockfire
 */
public class ScrollPanel extends Element {

	public enum MouseScrollMode {
		Disabled, Item, ScrollBar
	}

	public enum ScrollBarMode {
		Always, Auto, Never
	}

	public static enum ScrollDirection {
		Down, Left, Right, Up
	}

	public class ScrollPanelBounds extends Element implements TouchListener, FlingListener {
		public ScrollPanelBounds(BaseScreen screen) {
			super(screen);
			setStyleClass("inner-bounds");
			onMouseWheel(evt -> {
				if (mouseScrollMode != MouseScrollMode.Disabled) {
					float amt = getItemIncrement();
					if (amt > 0) {
						switch (evt.getDirection()) {
						case up:
							if (getVerticalScrollDistance() > 0) {
								scrollYBy(amt);
							}
							evt.setConsumed();
							break;
						case down:
							if (getVerticalScrollDistance() > 0) {
								scrollYBy(-amt);
							}
							evt.setConsumed();
							break;
						default:
							break;
						}
					}
				}
			});
		}

		@Override
		public Size getPreferredDimensions() {
			return scrollableArea.getPreferredDimensions();
		}

		// <editor-fold desc="Android Events">
		@Override
		public void onFling(TouchEvent evt) {
			if (flingEnabled && (evt.getDeltaY() > 0.2f || evt.getDeltaY() < -0.2f)) {
				if (screen.getGUINode().getControl(GameTimer.class) == null) {
					flingTimer.reset(false);
					flingDir = (evt.getDeltaY() < 0) ? true : false;
					flingSpeed = FastMath.abs(evt.getDeltaY());
					screen.getGUINode().addControl(flingTimer);
				}
			}
		}

		@Override
		public void onTouchDown(TouchEvent evt) {
			if (screen.getGUINode().getControl(GameTimer.class) != null) {
				flingTimer.endGameTimer();
				screen.getGUINode().addControl(flingTimer);
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
					touchEndY = getScrollableAreaVerticalPosition();
				}
			}
		}

		@Override
		public void onTouchUp(TouchEvent evt) {
		}

		public void setHorizontalScrollBarMode(ScrollBarMode horizontalScrollBarMode) {
			PropertyDeclaration decl = new PropertyDeclaration(CssExtensions.OVERFLOW_X,
					new PropertyValue(CssUtil.scrollBarModeToIdent(horizontalScrollBarMode)), false,
					StylesheetInfo.USER);
			getCssState().addAllCssDeclaration(decl);
			applyCss(decl);
			layoutChildren();
		}

		public void setVerticalScrollBarMode(ScrollBarMode verticalScrollBarMode) {
			PropertyDeclaration decl = new PropertyDeclaration(CssExtensions.OVERFLOW_Y,
					new PropertyValue(CssUtil.scrollBarModeToIdent(verticalScrollBarMode)), false, StylesheetInfo.USER);
			getCssState().addAllCssDeclaration(decl);
			applyCss(decl);
			layoutChildren();
		}

		@Override
		protected void applyCssOverflow(PropertyDeclaration decl) {
			String n = decl.getPropertyName();
			if (n.equals("-it-overflow-x") || n.equals("overflow")) {
				if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
					IdentValue ident = decl.asIdentValue();
					if (ident == IdentValue.AUTO) {
						horizontalScrollBarMode = ScrollBarMode.Auto;
					} else if (ident == IdentValue.SCROLL) {
						horizontalScrollBarMode = ScrollBarMode.Always;
					} else {
						horizontalScrollBarMode = ScrollBarMode.Never;
					}
					dirtyParent(false, LayoutType.boundsChange());
				} else
					throw new UnsupportedOperationException(String.format("Invalid overflow %s", decl.getValue()));
			}
			if (n.equals("-it-overflow-y") || n.equals("overflow")) {
				if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
					IdentValue ident = decl.asIdentValue();
					if (ident == IdentValue.AUTO) {
						verticalScrollBarMode = ScrollBarMode.Auto;
					} else if (ident == IdentValue.SCROLL) {
						verticalScrollBarMode = ScrollBarMode.Always;
					} else {
						verticalScrollBarMode = ScrollBarMode.Never;
					}
					dirtyParent(false, LayoutType.boundsChange());
				} else
					throw new UnsupportedOperationException(String.format("Invalid overflow %s", decl.getValue()));
			}
			super.applyCssOverflow(decl);
		}
	}

	public static class ScrollPanelLayout<C extends ScrollPanel> extends AbstractGenericLayout<C, Object> {

		// @Override
		// protected Vector2f calcMinimumSize(C parent) {
		// ScrollPanel scrollPanel = parent;
		// Vector2f min = scrollPanel.getScrollableArea().calcMinimumSize();
		// Vector4f padding = parent.getAllPadding();
		// min = min.addLocal(padding.x, padding.z).addLocal(padding.y,
		// padding.w);
		// return min;
		// }
		//
		// @Override
		// protected Vector2f calcMaximumSize(C parent) {
		// Vector2f mx = parent.getScrollableArea().calcMaximumSize();
		// Vector4f padding = parent.getAllPadding();
		// mx = mx.addLocal(padding.x, padding.z).addLocal(padding.y,
		// padding.w);
		// return mx;
		// }

		@Override
		protected Vector2f calcPreferredSize(C parent) {

			ScrollPanel sp = parent;
			Vector4f spTextPadding = sp.getAllPadding();

			/*
			 * TODO this needs fixing. When the vertical scrollbar MIGHT show, the initial
			 * preferred size will be wrong. Evident in ZMenu when it exceeds the screen
			 * heigh
			 */

			// Vector2f contentPref =
			// sp.getScrollableArea().calcPreferredSize();
			// Vector2f contentPref =
			// sp.getScrollableArea().calcPreferredSize();
			// return contentPref.addLocal(parent.getTotalTextPadding());

			Vector2f verSize = sp.vScrollBar.calcPreferredSize();
			Vector2f horSize = sp.hScrollBar.calcPreferredSize();

			Vector2f contentPref = sp.getScrollableArea().calcPreferredSize();
			Vector2f viewportSize = new Vector2f(sp.getWidth() - spTextPadding.x - spTextPadding.y,
					sp.getHeight() - spTextPadding.z - spTextPadding.w);

			// Decide if to show the vertical bar
			// boolean showVertical = sp.verticalScrollBarMode ==
			// ScrollBarMode.Always
			// || (sp.verticalScrollBarMode == ScrollBarMode.Auto &&
			// contentPref.y > viewportSize.y);
			boolean showVertical = false;
			if (sp.verticalScrollBarMode == ScrollBarMode.Auto) {
				showVertical = contentPref.y > viewportSize.y;
				// Chicken .. meet egg
			} else if (sp.verticalScrollBarMode == ScrollBarMode.Always) {
				showVertical = true;
			}

			if (showVertical) {
				contentPref.x += verSize.x + sp.getIndent();
			}

			boolean showHorizontal = sp.horizontalScrollBarMode == ScrollBarMode.Always;
			if (showHorizontal) {
				contentPref.y += horSize.y + sp.getIndent();
			}

			return contentPref.addLocal(spTextPadding.x + spTextPadding.y, spTextPadding.z + spTextPadding.w);

		}

		@Override
		protected void onLayout(C childElement) {

			ScrollPanel sp = childElement;
			Vector4f spTextPadding = sp.getAllPadding();

			Vector2f horSize = sp.hScrollBar.calcPreferredSize();
			Vector2f verSize = sp.vScrollBar.calcPreferredSize();

			float wasV = sp.vScrollBar.getRelativeScrollAmount();
			float wasH = sp.hScrollBar.getRelativeScrollAmount();

			// The size of the viewport (if there were no scrollbars visibile)
			// Vector2f viewportSize = sp.getPreferredViewportSize();
			Vector2f viewportSize = new Vector2f(sp.getWidth() - spTextPadding.x - spTextPadding.y,
					sp.getHeight() - spTextPadding.z - spTextPadding.w);

			// Initially set the viewport and the scrollable area to the same
			// size
			sp.innerBounds.setBounds(spTextPadding.x, spTextPadding.z, viewportSize.x, viewportSize.y);
			sp.scrollableArea.setDimensions(viewportSize);
			Vector2f contentPref = sp.scrollableArea.calcPreferredSize();

			// Decide if to show the vertical bar
			boolean showVertical = sp.verticalScrollBarMode == ScrollBarMode.Always
					|| (sp.verticalScrollBarMode == ScrollBarMode.Auto && contentPref.y > sp.innerBounds.getHeight());

			float vx = verSize.x;
			float hy = horSize.y;

			boolean showHorizontal = sp.horizontalScrollBarMode == ScrollBarMode.Always
					|| (sp.horizontalScrollBarMode == ScrollBarMode.Auto
							&& contentPref.x > sp.innerBounds.getWidth() - (showVertical ? vx + sp.getIndent() : 0));

			if (showVertical) {
				viewportSize.x -= vx + sp.getIndent();

				// Update the content preferred size now we know a vertical bar
				// will be present
				sp.innerBounds.setBounds(spTextPadding.x, spTextPadding.z, viewportSize.x, viewportSize.y);
				sp.scrollableArea.setDimensions(viewportSize.x, contentPref.y);
				contentPref = sp.scrollableArea.calcPreferredSize();

				// Test again if to show horizontal

				showHorizontal = sp.horizontalScrollBarMode == ScrollBarMode.Always
						|| (sp.horizontalScrollBarMode == ScrollBarMode.Auto
								&& contentPref.x > sp.innerBounds.getWidth());

				if (showHorizontal)
					sp.vScrollBar.setBounds(sp.getWidth() - vx - spTextPadding.y, spTextPadding.z, vx,
							viewportSize.y - hy);
				else
					sp.vScrollBar.setBounds(sp.getWidth() - vx - spTextPadding.y, spTextPadding.z, vx, viewportSize.y);

				sp.vScrollBar.show();

				// Always dirty, as the bounds probably haven't actually
				// changed, so no layout will occur
				sp.vScrollBar.dirtyLayout(false, LayoutType.children);
				sp.vScrollBar.layoutChildren();
			} else
				sp.vScrollBar.hide();

			if (showHorizontal) {
				// If showing horizontal scrollbar, reduce the height
				viewportSize.y -= hy + sp.getIndent();

				// Update the content preferred size now we know a horizontal
				// bar will be present
				sp.innerBounds.setBounds(spTextPadding.x, spTextPadding.z, viewportSize.x, viewportSize.y);
				sp.scrollableArea.setDimensions(contentPref.x, viewportSize.y);
				contentPref = sp.scrollableArea.calcPreferredSize();

				// Show
				sp.hScrollBar.show();

				// Always dirty, as the bounds probably haven't actually
				// changed, so no layout will occur
				sp.hScrollBar.setBounds(spTextPadding.x, sp.getHeight() - spTextPadding.w - hy, viewportSize.x, hy);
				sp.hScrollBar.dirtyLayout(false, LayoutType.children);
				sp.hScrollBar.layoutChildren();

			} else {
				sp.hScrollBar.hide();
			}

			if (showVertical && showHorizontal) {
				sp.corner.setBounds(sp.getWidth() - vx - spTextPadding.y, sp.getHeight() - spTextPadding.w - hy, vx,
						hy);
				sp.corner.show();
			} else
				sp.corner.hide();

			sp.innerBounds.setBounds(spTextPadding.x, spTextPadding.z, viewportSize.x, viewportSize.y);
			sp.scrollableArea.setDimensions(contentPref.x, contentPref.y);

			if (!showHorizontal && sp.scrollableArea.getX() != 0) {
				sp.scrollableArea.setPosition(0, sp.scrollableArea.getY());
			}

			float wasx = sp.scrollableArea.getX();
			float wasy = sp.scrollableArea.getY();

			if (contentPref.y < viewportSize.y)
				sp.scrollToTop();
			else {
				sp.setScrollAreaPositionTo(wasV, Orientation.VERTICAL, false);
			}

			if (contentPref.x < viewportSize.x)
				sp.scrollToLeft();
			else {
				sp.setScrollAreaPositionTo(wasH, Orientation.HORIZONTAL, false);
			}

			if (sp.scrollableArea.isPaging() && wasx == sp.scrollableArea.getX() && wasy == sp.scrollableArea.getY())
				sp.scrollableArea.scrollContent(null);
		}
	}

	protected boolean flingDir = true;
	protected float flingSpeed = 1;
	protected ScrollBarMode horizontalScrollBarMode;
	protected ScrollBar hScrollBar;
	protected ScrollPanelBounds innerBounds;
	protected ScrollArea scrollableArea;
	protected float touchEndY = 0;

	protected float touchOffsetY = 0;
	protected float touchStartY = 0;

	protected ScrollBarMode verticalScrollBarMode;
	protected ScrollBar vScrollBar;
	private Button corner;
	private boolean flingEnabled = true;
	private GameTimer flingTimer;
	private int maxElements = Integer.MAX_VALUE;

	private MouseScrollMode mouseScrollMode = MouseScrollMode.Item;
	private float itemIncrement = -1;
	private float blockIncrement = -1;

	/**
	 * Creates a new instance of the ScrollPanel control
	 * 
	 * @param screen The screen control the Element is to be added to
	 */
	public ScrollPanel() {
		this(BaseScreen.get());
	}

	/**
	 * Special case for scrolling a single element. When this constructor is used,
	 * this is the ONLY element that is scrolled (any attempt to add further
	 * elements will result in an exception). An additional CSS style class will
	 * also be added with the named 'ScrolledSomething' where something is CSS class
	 * name of the supplied element. This is intended for example for use with a
	 * scrolled {@link TextArea}, where we want different styling for the scroll
	 * panel itself (e.g. make it look like a text area control) and the text area
	 * itself (which should have its borders removed).
	 * 
	 * @param element element to scroll
	 */
	public ScrollPanel(BaseElement element) {
		this(BaseScreen.get(), element);
	}

	/**
	 * Creates a new instance of the ScrollPanel control
	 * 
	 * @param screen The screen control the Element is to be added to
	 */
	public ScrollPanel(BaseScreen screen) {
		super(screen);
	}

	/**
	 * Special case for scrolling a single element. When this constructor is used,
	 * this is the ONLY element that is scrolled (any attempt to add further
	 * elements will result in an exception). An additional CSS style class will
	 * also be added with the named 'ScrolledSomething' where something is CSS class
	 * name of the supplied element. This is intended for example for use with a
	 * scrolled {@link TextArea}, where we want different styling for the scroll
	 * panel itself (e.g. make it look like a text area control) and the text area
	 * itself (which should have its borders removed).
	 * 
	 * @param screen  The screen control the Element is to be added to
	 * @param element element to scroll
	 */
	public ScrollPanel(BaseScreen screen, BaseElement element) {
		this(screen);
		maxElements = 1;
		addScrollableContent(element);
	}

	public ScrollPanel setBlockIncrement(float blockIncrement) {
		this.blockIncrement = blockIncrement;
		return this;
	}

	public float getBlockIncrement() {
		if (blockIncrement != -1) {
			return blockIncrement;
		} else {
			return getViewportHeight();
		}
	}

	public ScrollPanel setItemIncrement(float itemIncrement) {
		this.itemIncrement = itemIncrement;
		return this;
	}

	public float getItemIncrement() {
		if (itemIncrement != -1) {
			return itemIncrement;
		} else {
			if (scrollableArea.getElements().size() > 1) {
				float total = 0;
				for (BaseElement e : scrollableArea.getElements())
					total += e.getHeight();
				return total / (float) scrollableArea.getElements().size();
			} else {
				return 1;
			}
		}
	}

	public ScrollPanel addScrollableContent(BaseElement el) {
		addScrollableContent(el, null);
		return this;
	}

	public ScrollPanel addScrollableContent(BaseElement child, Object constraints) {
		child.addClippingLayer(innerBounds);
		scrollableArea.addElement(child, constraints);
		dirtyLayout(false, LayoutType.boundsChange());
		layoutChildren();
		return this;
	}

	public void dirtyScrollContent() {
		dirtyLayout(false, LayoutType.boundsChange());
		vScrollBar.dirtyLayout(false, LayoutType.boundsChange());
		hScrollBar.dirtyLayout(false, LayoutType.boundsChange());
		corner.dirtyLayout(false, LayoutType.boundsChange());
		innerBounds.dirtyLayout(false, LayoutType.boundsChange());
		scrollableArea.dirtyLayout(false, LayoutType.boundsChange());
		layoutChildren();
	}

	public Element getCorner() {
		return corner;
	}

	public boolean getFlingEnabled() {
		return this.flingEnabled;
	}

	public ScrollBar getHorizontalScrollBar() {
		return this.hScrollBar;
	}

	public ScrollBarMode getHorizontalScrollBarMode() {
		return horizontalScrollBarMode;
	}

	/**
	 * Returns the width difference between the scrollable area's total width and
	 * the scroll panel's bounds.
	 * 
	 * Note: This returns a negative float value if the scrollable area is smaller
	 * than it's bounds.
	 * 
	 * @return
	 */
	public float getHorizontalScrollDistance() {
		float diff = getScrollableAreaWidth() - innerBounds.getWidth();
		return diff;
	}

	public MouseScrollMode getMouseScrollMode() {
		return mouseScrollMode;
	}

	public Vector2f getPreferredViewportSize() {

		Vector2f horSize = hScrollBar.calcPreferredSize();
		Vector2f verSize = vScrollBar.calcPreferredSize();
		Vector4f textPadding = getAllPadding();
		Vector2f viewportSize = MathUtil
				.clampSize(new Size(getWidth() - textPadding.x - textPadding.y,
						getHeight() - textPadding.z - textPadding.w), getMinDimensions(), getMaxDimensions())
				.toVector2f();

		Vector2f contentPref = scrollableArea.calcPreferredSize();

		// Decide if to show the vertical bar
		boolean showVertical = verticalScrollBarMode == ScrollBarMode.Always
				|| (verticalScrollBarMode == ScrollBarMode.Auto && contentPref.y > innerBounds.getHeight());

		float vx = verSize.x;

		boolean showHorizontal = horizontalScrollBarMode == ScrollBarMode.Always
				|| (horizontalScrollBarMode == ScrollBarMode.Auto
						&& contentPref.x > innerBounds.getWidth() - (showVertical ? vx + getIndent() : 0));

		if (showVertical) {
			viewportSize.x -= vx + getIndent();
			showHorizontal = horizontalScrollBarMode == ScrollBarMode.Always
					|| (horizontalScrollBarMode == ScrollBarMode.Auto && contentPref.x > innerBounds.getWidth());

		}

		if (showHorizontal) {
			float hy = horSize.y;
			viewportSize.y -= hy + getIndent();
		}

		return viewportSize;
	}

	/**
	 * The element that contains the actual content to be scrolled. Content should
	 * not usually be added to this directly, instead using
	 * {@link #addScrollableContent(BaseElement)} and
	 * {@link #setScrollContentLayout(Layout)}.
	 * 
	 * @return scrollable area
	 */
	public ScrollArea getScrollableArea() {
		return this.scrollableArea;
	}

	public float getScrollableAreaHeight() {
		return scrollableArea.getHeight();
	}

	public float getScrollableAreaHorizontalPosition() {
		return innerBounds.getWidth() - (scrollableArea.getX() + scrollableArea.getWidth());
	}

	// <editor-fold desc="Vertical Scrolling">
	public float getScrollableAreaVerticalPosition() {
		return Math.abs(scrollableArea.getY());
	}

	public float getScrollableAreaWidth() {
		return scrollableArea.getWidth();
	}

	public Element getScrollBounds() {
		return this.innerBounds;
	}

	public float getScrollBoundsHeight() {
		return this.innerBounds.getHeight();
	}

	public float getScrollBoundsWidth() {
		return this.innerBounds.getWidth() - this.innerBounds.getTotalPadding().x;
	}

	@SuppressWarnings("unchecked")
	public <T extends Layout<?, ?>> T getScrollContentLayout() {
		return (T) scrollableArea.getLayoutManager();
	}

	@Override
	public List<String> getStyleClassNames() {
		List<String> l = new ArrayList<>(super.getStyleClassNames());
		if (maxElements == 1 && !scrollableArea.getElements().isEmpty()) {
			l.add("Scrolled" + ClassUtil.getMainClassName(scrollableArea.getElements().get(0).getClass()));
		}
		return l;
	}

	public ScrollBar getVerticalScrollBar() {
		return this.vScrollBar;
	}

	public ScrollBarMode getVerticalScrollBarMode() {
		return verticalScrollBarMode;
	}

	/**
	 * Returns the height difference between the scrollable area's total height and
	 * the scroll panel's bounds.
	 * 
	 * Note: This returns a negative float value if the scrollable area is smaller
	 * than it's bounds.
	 * 
	 * @return
	 */
	public float getVerticalScrollDistance() {
		float diff = getScrollableAreaHeight() - innerBounds.getHeight();
		return diff;
	}

	/**
	 * Get the size of the visible area of the content.
	 * 
	 * @return viewport port
	 */
	public Vector2f getViewportSize() {
		return new Vector2f(getViewportWidth(), getViewportHeight());
	}

	/**
	 * Get the height of the visible area of the content.
	 * 
	 * @return viewport height
	 */
	public float getViewportHeight() {
		Vector4f totalPadding = innerBounds.getAllPadding();
		return innerBounds.getHeight() - totalPadding.z - totalPadding.w;
	}

	/**
	 * Get the width of the visible area of the content.
	 * 
	 * @return viewport width
	 */
	public float getViewportWidth() {
		Vector4f totalPadding = innerBounds.getAllPadding();
		return innerBounds.getWidth() - totalPadding.x - totalPadding.y;
	}

	public ScrollPanel insertScrollableContent(BaseElement el, int index) {
		scrollableArea.insertChild(el, null, false, index);
		el.setClipPadding(innerBounds.getClipPaddingVec());
		return this;
	}

	public ScrollPanel removeScrollableContent(BaseElement el) {
		scrollableArea.removeElement(el);
		return this;
	}

	public ScrollPanel removeScrollableContent(int index) {
		scrollableArea.removeElement(index);
		return this;
	}

	public ScrollPanel removeAllScrollableContent() {
		scrollableArea.removeAllChildren();
		return this;
	}

	public ScrollPanel scrollToBottom() {
		if (scrollableArea.getY() == 0 && getScrollableAreaHeight() < innerBounds.getHeight()) {
			return this;
		}
		scrollableArea.scrollYTo(-getVerticalScrollDistance(), true);
		scrollableArea.scrollContent(ScrollDirection.Down);
		return this;
	}

	public ScrollPanel scrollToLeft() {
		if (scrollableArea.getX() != 0) {
			scrollableArea.scrollXTo(0, true);
		}
		return this;
	}

	public ScrollPanel scrollToRight() {
		if (scrollableArea.getX() != -getHorizontalScrollDistance()) {
			scrollableArea.scrollXTo(-getHorizontalScrollDistance(), true);
			scrollableArea.scrollContent(ScrollDirection.Right);
		}
		return this;
	}

	public ScrollPanel scrollToTop() {
		if (scrollableArea.getY() == 0 && getScrollableAreaHeight() < innerBounds.getHeight()) {
			return this;
		}
		scrollableArea.scrollYTo(0, true);
		scrollableArea.scrollContent(ScrollDirection.Up);
		return this;
	}

	public ScrollPanel scrollXBy(float incX) {
		float lastX = scrollableArea.getX();
		scrollXTo(lastX + incX);
		return this;
	}

	public ScrollPanel scrollXTo(float x) {
		if (x < 0)
			x = 0;
		else if (x > getHorizontalScrollDistance())
			x = getHorizontalScrollDistance();
		float lastX = scrollableArea.getX();
		scrollableArea.scrollXTo(x, true);
		if (lastX > x)
			scrollableArea.scrollContent(ScrollDirection.Left);
		else
			scrollableArea.scrollContent(ScrollDirection.Right);
		return this;
	}

	public ScrollPanel scrollYBy(float incY) {
		scrollYTo(scrollableArea.getY() - incY);
		return this;
	}

	public ScrollPanel scrollYTo(BaseElement element) {

		// Get the top and bottom of the viewport
		float top = getScrollableAreaVerticalPosition();
		float bottom = top + innerBounds.getHeight();

		float rowTop = element.getY();
		float rowBottom = rowTop + element.getHeight();

		// Scroll up
		if (rowTop < top) {
			scrollYTo(scrollableArea.getY() + -(rowTop - top));
		} else if (rowBottom > bottom) {
			scrollYTo(scrollableArea.getY() + bottom - rowBottom);
		}

		return this;
	}

	public ScrollPanel scrollYTo(float y) {
		float lastY = scrollableArea.getY();
		if (y != lastY) {
			if (y < -getVerticalScrollDistance())
				y = -getVerticalScrollDistance();
			if (y > 0)
				y = 0;
			scrollableArea.scrollYTo(y, true);
		}
		return this;
	}

	public ScrollPanel setFlingEnabled(boolean flingEnabled) {
		this.flingEnabled = flingEnabled;
		return this;
	}

	public ScrollPanel setHorizontalScrollBarMode(ScrollBarMode horizontalScrollBarMode) {
		innerBounds.setHorizontalScrollBarMode(horizontalScrollBarMode);
		return this;
	}

	public ScrollPanel setMouseScrollMode(MouseScrollMode mouseScrollMode) {
		this.mouseScrollMode = mouseScrollMode;
		return this;
	}

	public ScrollPanel setScrollContentLayout(Layout<?, ?> scrollAreaLayout) {
		scrollableArea.setLayoutManager(scrollAreaLayout);
		return this;
	}

	@Override
	public BaseElement setText(String text) {
		if (scrollableArea != null) {
			// scrollableArea.removeTextElement();
			scrollableArea.setText(text);
			dirtyLayout(false, LayoutType.boundsChange());
			layoutChildren();
		}
		return this;
	}

	public ScrollPanel setVerticalScrollBarMode(ScrollBarMode verticalScrollBarMode) {
		innerBounds.setVerticalScrollBarMode(verticalScrollBarMode);
		return this;
	}

	protected void configureScrolledElement() {

	}

	@Override
	protected final void configureStyledElement() {
		setAsContainerOnly();

		layoutManager = createScrollPanelLayout();
		innerBounds = createViewport(screen);

		/* Scrollable area holds the child elements */
		scrollableArea = new ScrollArea(screen) {

			@Override
			public BaseElement insertChild(BaseElement el, Object constraints, boolean hide, int index) {
				if (getElements().size() >= maxElements)
					throw new IllegalStateException("This ScrollPanel is configured to only allow a single element.");
				return super.insertChild(el, constraints, hide, index);
			}

			@Override
			protected void addElement(BaseElement child, Object constraints, boolean hide, int index) {
				if (getElements().size() >= maxElements)
					throw new IllegalStateException("This ScrollPanel is configured to only allow a single element.");
				super.addElement(child, constraints, hide, index);
			}
		};
		scrollableArea.onScrollEvent(evt -> {
			float relX = (FastMath.abs(scrollableArea.getX()) / getHorizontalScrollDistance());
			hScrollBar.runAdjusting(() -> hScrollBar.getScrollThumb().setX(Math
					.round((hScrollBar.getScrollTrack().getWidth() - hScrollBar.getScrollThumb().getWidth()) * relX)));
			float relY = (FastMath.abs(scrollableArea.getY()) / getVerticalScrollDistance());
			float h = (vScrollBar.getScrollTrack().getHeight() - vScrollBar.getScrollThumb().getHeight()) * relY;
			vScrollBar.runAdjusting(() -> vScrollBar.getScrollThumb().setY(h));
		});

		/*
		 * Inner bounds is the 'viewport', i.e. an element overlaying the visible
		 * portion of scrollable children
		 */
		innerBounds.addElement(scrollableArea);
		addElement(innerBounds);

		/* Vertical bar */
		vScrollBar = new ScrollBar(screen, Orientation.VERTICAL) {

			@Override
			public float getItemIncrement() {
				return ScrollPanel.this.getItemIncrement();
			}

			@Override
			public float getBlockIncrement() {
				return ScrollPanel.this.getBlockIncrement();
			}

		};
		vScrollBar.setMaximumValue(innerBounds.getHeight());
		vScrollBar.setThumbValue(scrollableArea.getHeight());
		vScrollBar.onChanged(evt -> {
			if (!evt.getSource().isAdjusting())
				setScrollAreaPositionTo(evt.getNewValue() / vScrollBar.getMaximumValue(), Orientation.VERTICAL,
						evt.isTemporary());
		});
		attachElement(vScrollBar);

		/* Horizontal bar */
		hScrollBar = new ScrollBar(screen, Orientation.HORIZONTAL) {

			@Override
			public float getItemIncrement() {
				return ScrollPanel.this.getItemIncrement();
			}

			@Override
			public float getBlockIncrement() {
				return ScrollPanel.this.getBlockIncrement();
			}

		};
		hScrollBar.setMaximumValue(innerBounds.getWidth());
		hScrollBar.setThumbValue(scrollableArea.getWidth());
		hScrollBar.onChanged(evt -> {
			if (!evt.getSource().isAdjusting())
				setScrollAreaPositionTo(evt.getNewValue() / hScrollBar.getMaximumValue(), Orientation.HORIZONTAL,
						evt.isTemporary());
		});
		attachElement(hScrollBar);

		/* Corner button */
		corner = new Button(screen) {
			{
				styleClass = "scroll-corner";
			}
		};
		attachElement(corner);

		/* Event handling */
		initFlingTimer();

		/* Watch for size changes and adjust scrollbars accordingly */

		innerBounds.onElementEvent(evt -> {
			vScrollBar.setThumbValue(evt.getSource().getHeight());
			hScrollBar.setThumbValue(evt.getSource().getWidth());
		}, Type.RESIZE);

		scrollableArea.onElementEvent(evt -> {
			vScrollBar.setMaximumValue(evt.getSource().getHeight());
			hScrollBar.setMaximumValue(evt.getSource().getWidth());
		}, Type.RESIZE);

		onNavigationKey(evt -> {
			if (evt.getKeyCode() == KeyInput.KEY_HOME && !evt.isShift()) {
				if (evt.isPressed())
					scrollToTop();
				evt.setConsumed();
			} else if (evt.getKeyCode() == KeyInput.KEY_END && !evt.isShift()) {
				if (evt.isPressed())
					scrollToBottom();
				evt.setConsumed();
			} else if (evt.getKeyCode() == KeyInput.KEY_HOME && evt.isShift()) {
				if (evt.isPressed())
					scrollToLeft();
				evt.setConsumed();
			} else if (evt.getKeyCode() == KeyInput.KEY_END && !evt.isShift()) {
				if (evt.isPressed())
					scrollToRight();
				evt.setConsumed();
			} else {
				if (evt.getKeyCode() == KeyInput.KEY_PGDN && evt.isNoModifiers()) {
					if (evt.isPressed())
						scrollYBy(getViewportHeight());
					evt.setConsumed();
				} else if (evt.getKeyCode() == KeyInput.KEY_PGUP && evt.isNoModifiers()) {
					if (evt.isPressed())
						scrollYBy(-getViewportHeight());
					evt.setConsumed();
				} else if (evt.getKeyCode() == KeyInput.KEY_DOWN && evt.isNoModifiers()) {
					if (evt.isPressed())
						scrollYBy(getItemIncrement());
					evt.setConsumed();
				} else if (evt.getKeyCode() == KeyInput.KEY_UP && evt.isNoModifiers()) {
					if (evt.isPressed())
						scrollYBy(-getItemIncrement());
					evt.setConsumed();
				}
			}
		});

		configureScrolledElement();
	}

	protected Layout<?, ?> createScrollPanelLayout() {
		return new ScrollPanelLayout<ScrollPanel>();
	}

	protected ScrollPanelBounds createViewport(BaseScreen screen) {
		return new ScrollPanelBounds(screen);
	}

	@Override
	protected void onPsuedoStateChange() {
		/// TODO is done in a few places now .. need common solution
		dirtyLayout(true, LayoutType.styling);
	}

	// @Override
	// protected final void onAfterLayout() {
	// // if (pagingEnabled)
	// // checkPagedContent(null);
	// setVThumbPositionToScrollArea();
	// onAfterScrollPanelLayout();
	// }
	//
	// protected void onAfterScrollPanelLayout() {
	//
	// }

	private void initFlingTimer() {
		flingTimer = new GameTimer() {
			@Override
			public void onComplete(float time) {

			}

			@Override
			public void timerUpdateHook(float tpf) {
				float currentY = getScrollableAreaVerticalPosition();
				float nextInc = 15 * flingSpeed * (1f - this.getPercentComplete());

				if (flingDir) {
					float nextY = currentY + nextInc;
					if (nextY <= scrollableArea.getHeight() && nextY >= innerBounds.getHeight()) {
						scrollYTo(nextY);
					}
				} else {
					float nextY = currentY - nextInc;
					if (nextY <= scrollableArea.getHeight() && nextY >= innerBounds.getHeight()) {
						scrollYTo(nextY);
					}
				}
			}
		};
		flingTimer.setInterpolation(Interpolation.exp5Out);
	}

	private void setScrollAreaPositionTo(float relativeScrollAmount, Orientation orientation, boolean fx) {
		relativeScrollAmount = FastMath.clamp(relativeScrollAmount, 0, 1);
		if (orientation == Orientation.VERTICAL) {
			float lastY = scrollableArea.getY();
			float vdist = getVerticalScrollDistance();
			float newY = Math.round(-(vdist * relativeScrollAmount));
			if (lastY != newY) {
				scrollableArea.scrollYTo(newY, fx);
				if (lastY > -(getVerticalScrollDistance() * relativeScrollAmount))
					scrollableArea.scrollContent(ScrollDirection.Up);
				else
					scrollableArea.scrollContent(ScrollDirection.Down);
			}
		} else {
			float lastX = scrollableArea.getX();
			float newX = Math.min(0, Math.round(-(getHorizontalScrollDistance() * relativeScrollAmount)));
			if (lastX != newX) {
				scrollableArea.scrollXTo(newX, fx);
				if (lastX < -(getHorizontalScrollDistance() * relativeScrollAmount))
					scrollableArea.scrollContent(ScrollDirection.Left);
				else
					scrollableArea.scrollContent(ScrollDirection.Right);
			}
		}
	}

}
