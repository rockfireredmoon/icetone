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
package icetone.controls.containers;

import java.util.ArrayList;
import java.util.List;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.buttons.Button;
import icetone.core.AbstractGenericLayout;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Element;
import icetone.core.Layout.LayoutType;
import icetone.core.Orientation;
import icetone.core.ZPriority;
import icetone.core.event.ChangeSupport;
import icetone.core.event.ElementEvent.Type;
import icetone.core.event.UIChangeEvent;
import icetone.core.event.UIChangeListener;
import icetone.core.utils.ClassUtil;
import icetone.core.utils.MathUtil;

/**
 * A container that takes two children, and presents a divider bar that may be
 * dragged. Space removed from one side is given to the other and vice versa.
 * <p>
 * Also supported are 'one touch expander' buttons that move the divider to the
 * extremities.
 *
 * @author rockfire
 */
public class SplitPanel extends Element {

	protected ChangeSupport<SplitPanel, Float> changeSupport;
	protected Orientation orientation;
	protected BaseElement leftOrTop;
	protected BaseElement rightOrBottom;
	protected float dividerLocation = Float.MIN_VALUE;
	protected float defaultDividerLocationRatio = 0.5f;
	protected final Button divider;
	protected final Button expandLeft;
	protected final Button expandRight;
	protected Float beforeExpand;
	protected boolean useOneTouchExpanders = true;
	protected boolean continuousLayout = false;

	private boolean dragged;
	private boolean settingDivider;
	private ZPriority wasPriority;
	private Vector2f dividerConstrain;

	/**
	 * Creates a new instance of a horizontal SplitPanel control
	 */
	public SplitPanel() {
		this(BaseScreen.get(), Orientation.HORIZONTAL);
	}

	/**
	 * Creates a new instance of the SplitPanel control
	 *
	 * @param orientation The orientation of the split
	 */
	public SplitPanel(Orientation orientation) {
		this(BaseScreen.get(), orientation);
	}

	/**
	 * Creates a new instance of the SplitPanel control
	 *
	 * @param screen      The screen control the Element is to be added to
	 * @param orientation The orientation of the split
	 */
	public SplitPanel(BaseScreen screen, Orientation orientation) {
		super(screen);
		this.orientation = orientation;

		// Layout
		setLayoutManager(new SplitPanelLayout());

		// Divider
		divider = new Button(screen);
		divider.onElementEvent(evt -> {
			dragged = true;
			if (continuousLayout) {
				dividerMoved();
			} else {
				if (wasPriority == null) {
					wasPriority = divider.getPriority();
					dividerConstrain = divider.getPixelPosition().clone();
					divider.setPriority(ZPriority.DRAG);
				} else {
					if (orientation == Orientation.HORIZONTAL) {
						divider.setY(dividerConstrain.y);
					} else {
						divider.setX(dividerConstrain.x);
					}
				}
			}
		}, Type.MOVED);
		divider.setStyleClass("divider");
		divider.onMousePressed(evt -> {
			dragged = false;
		});
		divider.onMouseReleased(evt -> {
			runAdjusting(() -> {
				if (wasPriority != null) {
					divider.setPriority(wasPriority);
					wasPriority = null;
					dividerConstrain = null;
				}

				if (!dragged) {
					if (beforeExpand != null) {
						setDividerLocation(beforeExpand);
						beforeExpand = null;
					} else {
						// Expand to whichever is the closer side
						beforeExpand = dividerLocation;
						if (dividerLocation > (SplitPanel.this.getWidth() / 2f)) {
							setDividerLocation(Float.MAX_VALUE);
						} else {
							setDividerLocation(0);
						}
					}
				} else if (!continuousLayout) {
					beforeExpand = null;
					setDividerLocation(orientation.equals(Orientation.HORIZONTAL) ? divider.getX() : divider.getY());
				}
			});

		});
		divider.setMovable(true);
		divider.addClippingLayer(this);
		addElement(divider);

		// Expand left
		expandLeft = new Button(screen);
		expandLeft.setStyleClass("expander expand-left-or-top");
		expandLeft.onMouseReleased(evt -> {
			runAdjusting(() -> {
				if (beforeExpand != null) {
					setDividerLocation(beforeExpand);
					beforeExpand = null;
				} else {
					beforeExpand = dividerLocation;
					setDividerLocation(0);
				}
			});
		});
		expandLeft.setMovable(false);
		expandLeft.addClippingLayer(this);
		addElement(expandLeft);

		// Expand right
		expandRight = new Button(screen);
		expandRight.setStyleClass("expander expand-right-or-bottom");
		expandRight.onMouseReleased(evt -> {
			runAdjusting(() -> {
				if (beforeExpand != null) {
					setDividerLocation(beforeExpand);
					beforeExpand = null;
				} else {
					beforeExpand = getDividerLocation();
					setDividerLocation(Float.MAX_VALUE);
				}
			});
		});
		expandRight.addClippingLayer(this);
		expandRight.setMovable(false);

		// This
		setIgnoreMouse(true);
		addElement(expandRight);
	}

	@Override
	public List<String> getStyleClassNames() {
		List<String> l = new ArrayList<>(super.getStyleClassNames());
		l.add((Orientation.HORIZONTAL.equals(getOrientation()) ? "Horizontal" : "Vertical")
				+ ClassUtil.getMainClassName(getClass()));
		return l;
	}

	public SplitPanel unbindChanged(UIChangeListener<SplitPanel, Float> listener) {
		if (changeSupport != null)
			changeSupport.unbind(listener);
		return this;
	}

	public SplitPanel onChange(UIChangeListener<SplitPanel, Float> listener) {
		if (changeSupport == null)
			changeSupport = new ChangeSupport<>();
		changeSupport.bind(listener);
		return this;
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
	 * @param dividerLocation divider location
	 */
	public void setDividerLocation(float dividerLocation) {
		float was = this.dividerLocation;
		setDividerLocationNoCallback(dividerLocation);
		if (was != dividerLocation) {
			if (changeSupport != null)
				changeSupport.fireEvent(new UIChangeEvent<SplitPanel, Float>(this, was, dividerLocation));
		}
	}

	/**
	 * Set the current divider location with no firing of events.
	 *
	 * @param dividerLocation divider location
	 */
	public void setDividerLocationNoCallback(float dividerLocation) {
		// Always layout to keep the movable divider from moving in the opposite
		// axis
		float was = this.dividerLocation;
		this.dividerLocation = dividerLocation;
		dirtyLayout(was != dividerLocation, LayoutType.boundsChange());
		layoutChildren();
	}

	/**
	 * Get the default divider location ratio which determines where the divider
	 * starts. For example, if you have a HORIZONTAL split, and set this value to
	 * <strong>0.25</strong>, the divider will start a quarter of its width from the
	 * left.
	 *
	 * @return default divider location ration
	 * @see #setDefaultDividerLocationRatio(float)
	 */
	public float getDefaultDividerLocationRatio() {
		return defaultDividerLocationRatio;
	}

	/**
	 * Set the default divider location ratio which determines where the divider
	 * starts. For example, if you have a HORIZONTAL split, and set this value to
	 * <strong>0.25</strong>, the divider will start a quarter of its width from the
	 * left.
	 *
	 * @param defaultDividerLocationRatio default divider location ration
	 * @see #setDefaultDividerLocationRatio(float)
	 */
	public void setDefaultDividerLocationRatio(float defaultDividerLocationRatio) {
		this.defaultDividerLocationRatio = defaultDividerLocationRatio;
		dirtyLayout(false, LayoutType.boundsChange());
		layoutChildren();
	}

	/**
	 * Set the element that is placed in the left hand side (for HORIZONTAL
	 * orientation) or the top (for VERTICAL orientation).
	 *
	 * @param leftOrTop left or top element
	 */
	public void setLeftOrTop(BaseElement leftOrTop) {
		if (this.leftOrTop != null) {
			removeElement(leftOrTop);
		}
		this.leftOrTop = leftOrTop;

		leftOrTop.addClippingLayer(leftOrTop);
		addElement(leftOrTop);
	}

	/**
	 * Set the element that is placed in the right hand side (for HORIZONTAL
	 * orientation) or the bottom (for VERTICAL orientation).
	 *
	 * @param rightOrBottom right or bottom element
	 */
	public void setRightOrBottom(BaseElement rightOrBottom) {
		if (this.rightOrBottom != null) {
			removeElement(rightOrBottom);
		}
		this.rightOrBottom = rightOrBottom;
		rightOrBottom.addClippingLayer(rightOrBottom);
		addElement(rightOrBottom);
	}

	/**
	 * Get the element that is placed in the left hand side (for HORIZONTAL
	 * orientation) or the top (for VERTICAL orientation).
	 *
	 * @param leftOrTop left or top element
	 */
	public BaseElement getLeftOrTop() {
		return leftOrTop;
	}

	/**
	 * Get the element that is placed in the right hand side (for HORIZONTAL
	 * orientation) or the bottom (for VERTICAL orientation).
	 *
	 * @return right or bottom element
	 */
	public BaseElement getRightOrBottom() {
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
	 * @param orientation orientation
	 */
	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
		setStyleClass(orientation.name().toLowerCase());
		dirtyLayout(true, LayoutType.contentChange());
		layoutChildren();
	}

	/**
	 * Get whether to layout the panels as the divider is moved or wait until
	 * movement stops.
	 * 
	 * @return continuousLayout
	 */
	public boolean isContinuousLayout() {
		return continuousLayout;
	}

	/**
	 * Set whether to layout the panels as the divider is moved or wait until
	 * movement stops.
	 * 
	 * @param continuousLayout continuous layout
	 */
	public void setContinuousLayout(boolean continuousLayout) {
		this.continuousLayout = continuousLayout;
	}

	/**
	 * Set whether the one touch expander buttons are visible.
	 *
	 * @param useOneTouchExpanders use one touch expanders
	 */
	public void setUseOneTouchExpanders(boolean useOneTouchExpanders) {
		if (useOneTouchExpanders != this.useOneTouchExpanders) {
			this.useOneTouchExpanders = useOneTouchExpanders;
			expandLeft.setVisible(useOneTouchExpanders);
			expandRight.setVisible(useOneTouchExpanders);
			dirtyLayout(false, LayoutType.boundsChange());
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

	public SplitPanel addChangeListener(UIChangeListener<SplitPanel, Float> listener) {
		if (changeSupport != null)
			changeSupport = new ChangeSupport<>();
		changeSupport.addListener(listener);
		return this;
	}

	public SplitPanel removeChangeListener(UIChangeListener<SplitPanel, Float> listener) {
		if (changeSupport != null)
			changeSupport.removeListener(listener);
		return this;
	}

	protected void dividerMoved() {
		if (!isAdjusting() && !settingDivider) {
			beforeExpand = null;
			setDividerLocation(orientation.equals(Orientation.HORIZONTAL) ? divider.getX() : divider.getY());
		}
	}

	protected float calcDividerSize() {
		if (orientation == Orientation.HORIZONTAL)
			return MathUtil.largest(divider.calcPreferredSize(),
					MathUtil.largest(expandLeft.calcPreferredSize(), expandRight.calcPreferredSize())).x;
		else
			return MathUtil.largest(divider.calcPreferredSize(),
					MathUtil.largest(expandLeft.calcPreferredSize(), expandRight.calcPreferredSize())).y;
	}

	public class SplitPanelLayout extends AbstractGenericLayout<SplitPanel, Object> {

		@Override
		protected Vector2f calcPreferredSize(SplitPanel parent) {
			Vector2f pref = new Vector2f();
			if (getLeftOrTop() != null) {
				pref.addLocal(getLeftOrTop().calcPreferredSize());
			}
			float dividerSize = calcDividerSize();
			switch (getOrientation()) {
			case HORIZONTAL:
				pref.x += dividerSize;
				pref.x += getIndent() * 2;
				if (getRightOrBottom() != null) {
					Vector2f p = getRightOrBottom().calcPreferredSize();
					pref.x += p.x;
					pref.y = Math.max(pref.y, p.y);
				}
				break;
			case VERTICAL:
				pref.y += dividerSize;
				pref.y += getIndent() * 2;
				if (getRightOrBottom() != null) {
					Vector2f p = getRightOrBottom().calcPreferredSize();
					pref.y += p.y;
					pref.x = Math.max(pref.x, p.x);
				}
				break;
			}
			return pref.addLocal(parent.getTotalPadding());
		}

		@Override
		protected void onLayout(SplitPanel owner) {

			Vector4f padding = getAllPadding();
			float actualDividerLocation;
			float childSize;

			float advanceX = padding.x;
			float advanceY = padding.z;

			float dividerSize = calcDividerSize();
			Float divIndent = divider.calcIndent();
			if(divIndent == null)
				divIndent = 0f;
			
			if (orientation.equals(Orientation.HORIZONTAL)) {

				// Keep divider within bounds of the whole area
				if (dividerLocation != Float.MIN_VALUE && owner.isInitialized()) {
					if (dividerLocation < padding.x) {
						dividerLocation = padding.x;
					} else if (dividerLocation > owner.getWidth() - dividerSize - padding.y) {
						dividerLocation = owner.getWidth() - dividerSize - padding.y;
					}
					actualDividerLocation = (int) (dividerLocation);
				} else {
					actualDividerLocation = (int) (owner.getWidth() * defaultDividerLocationRatio);
				}

				// Sizes (may get adjusted)
				float leftWidth = actualDividerLocation - padding.x - getIndent();
				float rightWidth = owner.getWidth() - actualDividerLocation - padding.y - dividerSize - getIndent();

				childSize = owner.getHeight() - padding.z - padding.w;

				if (rightOrBottom != null) {
					Vector2f min = rightOrBottom.calcMinimumSize();
					if (rightWidth < min.y) {
						// Keep right bigger than its minimum size
						float adj = min.y - rightWidth;
						rightWidth = min.y;
						leftWidth -= adj;
					}
				}

				// Layout left
				if (leftOrTop != null) {

					Vector2f min = leftOrTop.calcMinimumSize();
					if (leftWidth < min.y) {
						// Keep left bigger than its minimum size
						float adj = min.y - leftWidth;
						leftWidth = min.y;
						rightWidth -= adj;
					}

					if (leftWidth > 0) {
						leftOrTop.show();
						leftOrTop.setBounds(advanceX, advanceY, leftWidth, childSize);
						advanceX += leftWidth;
					} else if (leftOrTop.isVisible()) {
						leftOrTop.hide();
					}
				}
				advanceX += getIndent();

				// Layout divider
				if (useOneTouchExpanders && expandLeft != null && expandRight != null) {
					expandLeft.setBounds(advanceX, advanceY, dividerSize, dividerSize);
					advanceY += dividerSize;
					expandRight.setBounds(advanceX, advanceY, dividerSize, dividerSize);
					advanceY += dividerSize;
				}

				settingDivider = true;
				try {
					float dividerWidth = divider.calcPreferredSize().x;
					divider.setBounds(advanceX + ((dividerSize - dividerWidth) / 2.0f) + divIndent, advanceY, dividerWidth,
							childSize - (useOneTouchExpanders ? dividerSize * 2 : 0));
				} finally {
					settingDivider = false;
				}

				// if (useOneTouchExpanders) {
				// advanceX += (dividerSize * 2);
				// }
				advanceX += dividerSize + getIndent();

				// Layout right
				if (rightOrBottom != null) {
					if (rightWidth > 0) {
						rightOrBottom.show();
						advanceY = padding.z;
						rightOrBottom.setBounds(advanceX, advanceY, rightWidth, childSize);
						advanceX += rightWidth;
					} else if (rightOrBottom.isVisible()) {
						rightOrBottom.hide();
					}
				}
			} else {
				float space = owner.getHeight() - dividerSize;
				// Keep divider within bounds of the whole area
				if (dividerLocation != Float.MIN_VALUE && owner.isInitialized()) {
					if (dividerLocation < padding.y) {
						dividerLocation = padding.y;
					} else if (dividerLocation > owner.getHeight() - dividerSize - padding.w) {
						dividerLocation = owner.getHeight() - dividerSize - padding.w;
					}
					actualDividerLocation = (int) owner.getHeight() - dividerSize - (dividerLocation);
				} else {
					actualDividerLocation = (int) (space - (space * defaultDividerLocationRatio));
				}

				// Sizes
				float bottomHeight = actualDividerLocation - padding.w - getIndent();
				float topHeight = owner.getHeight() - actualDividerLocation - padding.z - dividerSize - getIndent();
				childSize = owner.getWidth() - padding.x - padding.y;

				if (leftOrTop != null) {
					Vector2f min = leftOrTop.calcMinimumSize();
					if (topHeight < min.y) {
						// Keep top bigger than it's minimum size
						float adj = min.y - topHeight;
						topHeight = min.y;
						bottomHeight -= adj;
					}
				}

				if (rightOrBottom != null) {
					Vector2f min = rightOrBottom.calcMinimumSize();
					if (bottomHeight < min.y) {
						// Keep bottom bigger than it's minimum size
						float adj = min.y - bottomHeight;
						bottomHeight = min.y;
						topHeight -= adj;
					}
				}

				// Layout top
				if (leftOrTop != null) {
					leftOrTop.setBounds(advanceX, advanceY, childSize, topHeight);
					advanceY += topHeight;
				}

				advanceY += getIndent();

				// Layout divider
				if (useOneTouchExpanders && expandLeft != null && expandRight != null) {
					expandLeft.setBounds(advanceX, advanceY, dividerSize, dividerSize);
					advanceX += dividerSize;
					expandRight.setBounds(advanceX, advanceY, dividerSize, dividerSize);
					advanceX += dividerSize;
				}

				settingDivider = true;
				try {
					float dividerHeight = divider.calcPreferredSize().x;
					divider.setBounds(advanceX, advanceY + ((dividerSize - dividerHeight) / 2.0f) + divIndent,
							childSize - (useOneTouchExpanders ? dividerSize * 2 : 0), dividerHeight);
				} finally {
					settingDivider = false;
				}

				advanceY += dividerSize + getIndent();
				if (useOneTouchExpanders) {
					advanceX += -(dividerSize * 2);
				}

				// Layout bottom
				if (rightOrBottom != null) {
					rightOrBottom.setBounds(advanceX, advanceY, childSize, bottomHeight);
					advanceY += bottomHeight;
				}
			}
		}

	}
}
