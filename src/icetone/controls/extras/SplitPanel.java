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
package icetone.controls.extras;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.buttons.ButtonAdapter;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.Screen;
import icetone.core.layout.AbstractLayout;
import icetone.core.layout.LUtil;
import icetone.core.utils.UIDUtil;

/**
 * A container that takes two children, and presents a divider bar that may be
 * dragged.
 * Space removed from one side is given to the other and vice versa.
 * <p>
 * Also supported are 'one touch expander' buttons that move the divider to the
 * extremities.
 *
 * @author rockfire
 */
public class SplitPanel extends Element {

	protected Orientation orientation;
	protected Element leftOrTop;
	protected Element rightOrBottom;
	protected float dividerLocation = Float.MIN_VALUE;
	protected float defaultDividerLocationRatio = 0.5f;
	protected final ButtonAdapter divider;
	protected final ButtonAdapter expandLeft;
	protected final ButtonAdapter expandRight;
	protected Float beforeExpand;
	protected boolean useOneTouchExpanders = true;
	protected final int dividerSize;
	protected float dividerGap;

	/**
	 * Creates a new instance of the SplitPanel control
	 *
	 * @param orientation
	 *            The orientation of the split
	 */
	public SplitPanel(Orientation orientation) {
		this(Screen.get(), orientation);
	}

	/**
	 * Creates a new instance of the SplitPanel control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param orientation
	 *            The orientation of the split
	 */
	public SplitPanel(ElementManager screen, Orientation orientation) {
		this(screen, Vector2f.ZERO, LUtil.LAYOUT_SIZE, orientation);
	}

	/**
	 * Creates a new instance of the SplitPanel control
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
	 * @param orientation
	 *            The orientation of the split
	 */
	public SplitPanel(ElementManager screen, Vector2f position, Vector2f dimensions, Orientation orientation) {
		this(screen, position, dimensions, screen.getStyle("SplitPanel").getVector4f("resizeBorders"),
				screen.getStyle("SplitPanel").getString("defaultImg"), orientation);
	}

	/**
	 * Creates a new instance of the SplitPanel control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param resizeBorders
	 *            A Vector4f containing the border information used when
	 *            resizing the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg
	 *            The default image to use for the Button
	 * @param orientation
	 *            The orientation of the split
	 */
	public SplitPanel(ElementManager screen, Vector4f resizeBorders, String defaultImg, Orientation orientation) {
		this(screen, Vector2f.ZERO, LUtil.LAYOUT_SIZE, resizeBorders, defaultImg, orientation);
	}

	/**
	 * Creates a new instance of the SplitPanel control
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
	 *            The default image to use for the Button
	 * @param orientation
	 *            The orientation of the split
	 */
	public SplitPanel(ElementManager screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg,
			Orientation orientation) {
		super(screen, UIDUtil.getUID(), position, dimensions, resizeBorders, defaultImg);
		this.orientation = orientation;

		// TODO get from style
		Vector2f min = screen.getStyle("SplitPanel").getVector2f("minSize");
		if (min != null) {
			setMinDimensions(min);
		}

		setTextPaddingByKey("SplitPanel", "textPadding");
		dividerSize = screen.getStyle("SplitPanel").getInt("dividerSize");
		dividerGap = screen.getStyle("SplitPanel").getFloat("dividerGap");

		// Layout
		setLayoutManager(new SplitPanelLayout());

		// Divider
		divider = new ButtonAdapter(screen) {
			private boolean dragged;

			@Override
			public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
				dragged = false;
			}

			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				if (!dragged) {
					if (beforeExpand != null) {
						dividerLocation = beforeExpand;
						beforeExpand = null;
					} else {
						// Expand to whichever is the closer side
						beforeExpand = dividerLocation;
						if (dividerLocation > (SplitPanel.this.getWidth() / 2f)) {
							dividerLocation = Float.MAX_VALUE;
						} else {
							dividerLocation = 0;
						}
					}
				}
				SplitPanel.this.dirtyLayout(false);
				SplitPanel.this.layoutChildren();
				onExpandLeftOrTop();
			}

			@Override
			public void controlMoveHook() {
				dragged = true;
				dividerMoved();
			}
		};
		divider.setIsMovable(true);
		divider.setMinDimensions(new Vector2f(dividerSize, dividerSize));
		divider.addClippingLayer(divider);
		addChild(divider);

		// Expand left
		expandLeft = new ButtonAdapter(screen, Vector2f.ZERO, new Vector2f(dividerSize, dividerSize)) {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				if (beforeExpand != null) {
					dividerLocation = beforeExpand;
					beforeExpand = null;
				} else {
					beforeExpand = dividerLocation;
					dividerLocation = 0;
				}
				SplitPanel.this.dirtyLayout(false);
				SplitPanel.this.layoutChildren();
				onExpandLeftOrTop();
			}
		};
		expandLeft.setIsMovable(false);
		expandLeft.setMinDimensions(new Vector2f(dividerSize, dividerSize));
		expandLeft.addClippingLayer(expandLeft);
		addChild(expandLeft);

		// Expand right
		expandRight = new ButtonAdapter(screen, Vector2f.ZERO, new Vector2f(dividerSize, dividerSize)) {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				if (beforeExpand != null) {
					dividerLocation = beforeExpand;
					beforeExpand = null;
				} else {
					beforeExpand = getDividerLocation();
					dividerLocation = Float.MAX_VALUE;
				}
				SplitPanel.this.dirtyLayout(false);
				SplitPanel.this.layoutChildren();
				onExpandRightOrBottom();
			}
		};
		expandRight.setIsMovable(false);
		expandRight.setMinDimensions(new Vector2f(dividerSize, dividerSize));
		expandRight.addClippingLayer(expandRight);

		// This
		setIgnoreMouse(true);
		addChild(expandRight);
		reconfigureDivider(orientation);
	}

	public float getDividerGap() {
		return dividerGap;
	}

	public void setDividerGap(float dividerGap) {
		this.dividerGap = dividerGap;
		dirtyLayout(false);
		layoutChildren();
	}

	@Override
	public void controlResizeHook() {
		super.controlResizeHook();
	}

	/**
	 * Get the current divider location.
	 *
	 * @return divider location
	 */
	public float getDividerLocation() {
		return dividerLocation;
	}

	/**
	 * Set the current divider location.
	 *
	 * @param dividerLocation
	 *            divider location
	 */
	public void setDividerLocation(float dividerLocation) {
		this.dividerLocation = dividerLocation;
		dirtyLayout(false);
		layoutChildren();
	}

	/**
	 * Get the default divider location ratio which determines where the divider
	 * starts.
	 * For example, if you have a HORIZONTAL split, and set this value to
	 * <strong>0.25</strong>, the divider will start a quarter of its width from
	 * the left.
	 *
	 * @return default divider location ration
	 * @see #setDefaultDividerLocationRatio(float)
	 */
	public float getDefaultDividerLocationRatio() {
		return defaultDividerLocationRatio;
	}

	/**
	 * Set the default divider location ratio which determines where the divider
	 * starts.
	 * For example, if you have a HORIZONTAL split, and set this value to
	 * <strong>0.25</strong>, the divider will start a quarter of its width from
	 * the left.
	 *
	 * @param defaultDividerLocationRatio
	 *            default divider location ration
	 * @see #setDefaultDividerLocationRatio(float)
	 */
	public void setDefaultDividerLocationRatio(float defaultDividerLocationRatio) {
		this.defaultDividerLocationRatio = defaultDividerLocationRatio;
		dirtyLayout(false);
		layoutChildren();
	}

	/**
	 * Set the element that is placed in the left hand side (for HORIZONTAL
	 * orientation)
	 * or the top (for VERTICAL orientation).
	 *
	 * @param leftOrTop
	 *            left or top element
	 */
	public void setLeftOrTop(Element leftOrTop) {
		if (this.leftOrTop != null) {
			removeChild(leftOrTop);
		}
		this.leftOrTop = leftOrTop;

		leftOrTop.addClippingLayer(leftOrTop);
		addChild(leftOrTop);
	}

	/**
	 * Set the element that is placed in the right hand side (for HORIZONTAL
	 * orientation)
	 * or the bottom (for VERTICAL orientation).
	 *
	 * @param rightOrBottom
	 *            right or bottom element
	 */
	public void setRightOrBottom(Element rightOrBottom) {
		if (this.rightOrBottom != null) {
			removeChild(rightOrBottom);
		}
		this.rightOrBottom = rightOrBottom;
		rightOrBottom.addClippingLayer(rightOrBottom);
		addChild(rightOrBottom);
	}

	/**
	 * Get the element that is placed in the left hand side (for HORIZONTAL
	 * orientation)
	 * or the top (for VERTICAL orientation).
	 *
	 * @param leftOrTop
	 *            left or top element
	 */
	public Element getLeftOrTop() {
		return leftOrTop;
	}

	/**
	 * Get the element that is placed in the right hand side (for HORIZONTAL
	 * orientation)
	 * or the bottom (for VERTICAL orientation).
	 *
	 * @return right or bottom element
	 */
	public Element getRightOrBottom() {
		return rightOrBottom;
	}

	/**
	 * Get the orientation.
	 *
	 * @return orientation
	 */
	public Orientation getOrientation() {
		return orientation;
	}

	/**
	 * Set the orientation.
	 *
	 * @param orientation
	 *            orientation
	 */
	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
		reconfigureDivider(orientation);
		dirtyLayout(false);
		layoutChildren();
	}

	/**
	 * Set whether the one touch expander buttons are visible.
	 *
	 * @param useOneTouchExpanders
	 *            use one touch expanders
	 */
	public void setUseOneTouchExpanders(boolean useOneTouchExpanders) {
		if (useOneTouchExpanders != this.useOneTouchExpanders) {
			this.useOneTouchExpanders = useOneTouchExpanders;
			expandLeft.setIsVisible(useOneTouchExpanders);
			expandRight.setIsVisible(useOneTouchExpanders);
			dirtyLayout(false);
			layoutChildren();
		}
	}

	/**
	 * Get whether the one touch expander buttons are visible.
	 *
	 * @return use one touch expanders
	 */
	public boolean getUseOneTouchExpanders(boolean useOneTouchExpanders) {
		return useOneTouchExpanders;
	}

	/**
	 * Invoked when the expanders are used to expand to the right or bottom.
	 *
	 */
	protected void onExpandRightOrBottom() {
		// For sub-classes to override
	}

	/**
	 * Invoked when the expanders are used to expand to the left or top
	 */
	protected void onExpandLeftOrTop() {
		// For sub-classes to override
	}

	/**
	 * Invoked when the divider is moved
	 */
	protected void onDividerMoved() {
		// For sub-classes to override
	}

	protected void dividerMoved() {
		beforeExpand = null;
		dividerLocation = orientation.equals(Orientation.HORIZONTAL) ? divider.getX() : LUtil.getY(divider);
		dirtyLayout(false);
		layoutChildren();
		onDividerMoved();
	}

	private void reconfigureDivider(Orientation orientation) {
		final boolean v = orientation.equals(Orientation.VERTICAL);
		divider.setResizeN(v);
		divider.setResizeS(v);
		divider.setResizeE(!v);
		divider.setResizeW(!v);
		expandLeft.setButtonIcon(dividerSize / 2f, dividerSize / 2f,
				screen.getStyle("Common").getString(v ? "arrowDown" : "arrowLeft"));
		expandRight.setButtonIcon(dividerSize / 2f, dividerSize / 2f,
				screen.getStyle("Common").getString(v ? "arrowUp" : "arrowRight"));
		String styleName = "SplitPanel#Divider#" + orientation.name();
		divider.borders.set(screen.getStyle(styleName).getVector4f("resizeBorders"));
		divider.setColorMap(screen.getStyle(styleName).getString("defaultImg"));
		divider.setButtonHoverInfo(screen.getStyle(styleName).getString("hoverImg"),
				screen.getStyle(styleName).getColorRGBA("hoverColor"));
		divider.setButtonPressedInfo(screen.getStyle(styleName).getString("pressedImg"),
				screen.getStyle(styleName).getColorRGBA("pressedrColor"));
	}

	public class SplitPanelLayout extends AbstractLayout {

		public Vector2f minimumSize(Element parent) {
			return null;
		}

		public Vector2f maximumSize(Element parent) {
			return null;
		}

		public Vector2f preferredSize(Element parent) {
			Vector2f pref = new Vector2f();
			Vector4f margins = getTextPaddingVec();
			if (getLeftOrTop() != null) {
				pref.addLocal(LUtil.getBoundPreferredSize(getLeftOrTop()));
			}
			switch (getOrientation()) {
			case HORIZONTAL:
				pref.x += dividerSize;
				pref.x += dividerGap *2;
				if (getRightOrBottom() != null) {
					Vector2f p = LUtil.getBoundPreferredSize(getRightOrBottom());
					pref.x += p.x;
					pref.y = Math.max(pref.y, p.y);
				}
				break;
			case VERTICAL:
				pref.y += dividerSize;
				pref.y += dividerGap *2;
				if (getRightOrBottom() != null) {
					Vector2f p = LUtil.getBoundPreferredSize(getRightOrBottom());
					pref.y += p.y;
					pref.x = Math.max(pref.x, p.x);
				}
				break;
			}
			if (margins != null) {
				pref.addLocal(margins.y + margins.z, margins.x + margins.w);
			}
			return pref;
		}

		public void layout(Element owner) {

			Vector4f margins = getTextPaddingVec();
			float actualDividerLocation;
			float childSize;

			float advanceX = margins.x;
			float advanceY = margins.y;

			if (orientation.equals(Orientation.HORIZONTAL)) {
				// Keep divider within bounds of the whole area
				if (dividerLocation != Float.MIN_VALUE && owner.getInitialized()) {
					if (dividerLocation < margins.y) {
						dividerLocation = margins.y;
					} else if (dividerLocation > owner.getWidth() - dividerSize - margins.z) {
						dividerLocation = owner.getWidth() - dividerSize - margins.z;
					}
					actualDividerLocation = (int) (dividerLocation);
				} else {
					actualDividerLocation = (int) (owner.getWidth() * defaultDividerLocationRatio);
				}

				// Sizes (may get adjusted)
				float leftWidth = actualDividerLocation - margins.y - dividerGap;
				float rightWidth = owner.getWidth() - actualDividerLocation - margins.z - dividerSize - dividerGap;

				childSize = owner.getHeight() - margins.x - margins.w;

				if (rightOrBottom != null) {
					Vector2f min = LUtil.getMinimumSize(rightOrBottom);
					if (rightWidth < min.y) {
						// Keep right bigger than its minimum size
						float adj = min.y - rightWidth;
						rightWidth = min.y;
						leftWidth -= adj;
					}
				}

				// Layout left
				if (leftOrTop != null) {

					Vector2f min = LUtil.getMinimumSize(leftOrTop);
					if (leftWidth < min.y) {
						// Keep left bigger than its minimum size
						float adj = min.y - leftWidth;
						leftWidth = min.y;
						rightWidth -= adj;
					}

					if (leftWidth > 0) {
						leftOrTop.show();
						LUtil.setBounds(leftOrTop, advanceX, advanceY, leftWidth, childSize);
						advanceX += leftWidth;
					} else if (leftOrTop.getIsVisible()) {
						leftOrTop.hide();
					}
				}
				advanceX += dividerGap;
				
				// Layout divider
				if (useOneTouchExpanders && expandLeft != null && expandRight != null) {
					LUtil.setBounds(expandLeft, advanceX, advanceY, dividerSize, dividerSize);
					advanceY += dividerSize;
					LUtil.setBounds(expandRight, advanceX, advanceY, dividerSize, dividerSize);
					advanceY += dividerSize;
				}
				
				LUtil.setBounds(divider, advanceX, advanceY, dividerSize, childSize - (useOneTouchExpanders ? dividerSize * 2 : 0));

				// if (useOneTouchExpanders) {
				// advanceX += (dividerSize * 2);
				// }
				advanceX += dividerSize + dividerGap;

				// Layout right
				if (rightOrBottom != null) {
					if (rightWidth > 0) {
						rightOrBottom.show();
						advanceY = margins.y;
						LUtil.setBounds(rightOrBottom, advanceX, advanceY, rightWidth, childSize);
						advanceX += rightWidth;
					} else if (rightOrBottom.getIsVisible()) {
						rightOrBottom.hide();
					}
				}
			} else {
				float space = owner.getHeight() - dividerSize;
				// Keep divider within bounds of the whole area
				if (dividerLocation != Float.MIN_VALUE && owner.getInitialized()) {
					if (dividerLocation < margins.x) {
						dividerLocation = margins.x;
					} else if (dividerLocation > owner.getHeight() - dividerSize - margins.w) {
						dividerLocation = owner.getHeight() - dividerSize - margins.w;
					}
					actualDividerLocation = (int) owner.getHeight() - dividerSize - (dividerLocation);
				} else {
					actualDividerLocation = (int) (space - (space * defaultDividerLocationRatio));
				}

				// Sizes
				float bottomHeight = actualDividerLocation - margins.x - dividerGap;
				float topHeight = owner.getHeight() - actualDividerLocation - margins.w - dividerSize - dividerGap;
				childSize = owner.getWidth() - margins.y - margins.z;

				if (leftOrTop != null) {
					Vector2f min = LUtil.getMinimumSize(leftOrTop);
					if (topHeight < min.y) {
						// Keep top bigger than it's minimum size
						float adj = min.y - topHeight;
						topHeight = min.y;
						bottomHeight -= adj;
					}
				}

				if (rightOrBottom != null) {
					Vector2f min = LUtil.getMinimumSize(rightOrBottom);
					if (bottomHeight < min.y) {
						// Keep bottom bigger than it's minimum size
						float adj = min.y - bottomHeight;
						bottomHeight = min.y;
						topHeight -= adj;
					}
				}

				// Layout top
				if (leftOrTop != null) {
					LUtil.setBounds(leftOrTop, advanceX, advanceY, childSize, topHeight);
					advanceY += topHeight;
				}

				advanceY += dividerGap;
				
				// Layout divider
				if (useOneTouchExpanders && expandLeft != null && expandRight != null) {
					LUtil.setBounds(expandLeft, advanceX, advanceY, dividerSize, dividerSize);
					advanceX += dividerSize;
					LUtil.setBounds(expandRight, advanceX, advanceY, dividerSize, dividerSize);
					advanceX += dividerSize;
				}
				
				LUtil.setBounds(divider, advanceX, advanceY, childSize - (useOneTouchExpanders ? dividerSize * 2 : 0), dividerSize);

				advanceY += dividerSize + dividerGap;
				if (useOneTouchExpanders) {
					advanceX += -(dividerSize * 2);
				}

				// Layout bottom
				if (rightOrBottom != null) {
					LUtil.setBounds(rightOrBottom, advanceX, advanceY, childSize, bottomHeight);
					advanceY += bottomHeight;
				}
			}
			owner.updateClippingLayers();
		}

		public void remove(Element child) {
		}

		public void constrain(Element child, Object constraints) {
		}
	}
}
