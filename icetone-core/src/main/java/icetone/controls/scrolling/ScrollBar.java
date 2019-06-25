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
import java.util.Objects;

import com.jme3.input.KeyInput;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.buttons.Button;
import icetone.core.AbstractGenericLayout;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Element;
import icetone.core.Layout.LayoutType;
import icetone.core.Orientation;
import icetone.core.event.ChangeSupport;
import icetone.core.event.ElementEvent.Type;
import icetone.core.event.UIChangeEvent;
import icetone.core.event.UIChangeListener;
import icetone.core.event.mouse.MouseUIButtonEvent;
import icetone.core.utils.ClassUtil;

/**
 *
 * @author t0neg0d
 * @author rockfire
 */
public class ScrollBar extends Element {
	private Button btnUp, btnDown, track, thumb;
	private ChangeSupport<ScrollBar, Float> changeSupport;
	private float thumbValue = 50f;
	private float maximumValue = 100f;
	private Orientation orientation;
	private float itemInc = 1f;
	private float blockInc = 10f;
	private float lastPos;
	private boolean held;

	{
		doInitialLayout = false;
	}

	public ScrollBar(BaseScreen screen) {
		this(screen, Orientation.HORIZONTAL);
	}

	public ScrollBar(BaseScreen screen, Orientation orientation) {
		super(screen);
		this.orientation = orientation;
		initControl();
		layoutChildren();
	}

	public ScrollBar(Orientation orientation) {
		this(BaseScreen.get(), orientation);
	}

	public ScrollBar addChangeListener(UIChangeListener<ScrollBar, Float> listener) {
		if (changeSupport == null)
			changeSupport = new ChangeSupport<>();
		changeSupport.addListener(listener);
		return this;
	}

	public Button getButtonScrollDown() {
		return this.btnDown;
	}

	public Button getButtonScrollUp() {
		return this.btnUp;
	}

	public Orientation getOrientation() {
		return orientation;
	}

	public float getCurrentValue() {
		return getRelativeScrollAmount() * maximumValue;
	}

	public float getRelativeScrollAmount() {
		float f;
		if (orientation == Orientation.VERTICAL)
			f = (thumb.getY() / (track.getHeight() - thumb.getHeight()));
		else
			f = (thumb.getX() / (track.getWidth() - thumb.getWidth()));
		if (Float.isNaN(f))
			return 0;
		return f;
	}

	public Button getScrollThumb() {
		return this.thumb;
	}

	public Button getScrollTrack() {
		return this.track;
	}

	@Override
	public List<String> getStyleClassNames() {
		List<String> l = new ArrayList<>(super.getStyleClassNames());
		l.add((Orientation.HORIZONTAL.equals(getOrientation()) ? "Horizontal" : "Vertical")
				+ ClassUtil.getMainClassName(getClass()));
		return l;
	}

	public ScrollBar onChanged(UIChangeListener<ScrollBar, Float> listener) {
		if (changeSupport == null)
			changeSupport = new ChangeSupport<>();
		changeSupport.bind(listener);
		return this;
	}

	public ScrollBar removeChangeListener(UIChangeListener<ScrollBar, Float> listener) {
		if (changeSupport == null)
			changeSupport = new ChangeSupport<>();
		changeSupport.removeListener(listener);
		return this;
	}

	public float getThumbValue() {
		return thumbValue;
	}

	public float getMaximumValue() {
		return maximumValue;
	}

	public ScrollBar setMaximumValue(float value) {
		if (value != this.maximumValue) {
			this.maximumValue = value;
			dirtyLayout(false, LayoutType.boundsChange());
			layoutChildren();
		}
		return this;
	}

	public ScrollBar setThumbValue(float maximum) {
		if (maximum != this.thumbValue) {
			this.thumbValue = maximum;
			dirtyLayout(false, LayoutType.boundsChange());
			layoutChildren();
		}
		return this;
	}

	public float getItemIncrement() {
		return itemInc;
	}

	public ScrollBar setItemIncrement(float itemInc) {
		this.itemInc = itemInc;
		return this;
	}

	public float getBlockIncrement() {
		return blockInc;
	}

	public ScrollBar setBlockIncrement(float blockInc) {
		this.blockInc = blockInc;
		return this;
	}

	public ScrollBar setOrientation(Orientation orientation) {
		if (!Objects.equals(orientation, this.orientation)) {
			removeAllChildren();
			this.orientation = orientation;
			styleClass = orientation.name().toLowerCase();
			dirtyLayout(true, LayoutType.reset);
			initControl();
			layoutChildren();
		}
		return this;
	}

	protected void adjustForTrackEvent(float trackInc, MouseUIButtonEvent<BaseElement> evt) {
		float thumbY = thumb.getY();
		if (orientation == Orientation.VERTICAL) {
			if (evt.getY() - track.getAbsoluteY() < thumbY) {
				runAdjusting(() -> {
					if (thumbY - trackInc > 0) {
						thumb.setY(thumbY - trackInc);
					} else {
						thumb.setY(0);
					}
				});
				scrollScrollableArea(true);
			} else if (evt.getY() - track.getAbsoluteY() > thumbY + thumb.getHeight()) {
				runAdjusting(() -> {
					if (thumbY + trackInc < track.getHeight() - thumb.getHeight()) {
						thumb.setY(thumbY + trackInc);
					} else {
						thumb.setY(track.getHeight() - thumb.getHeight());
					}
				});
				scrollScrollableArea(true);
			}
		} else {
			if (evt.getX() - getAbsoluteX() < thumb.getX()) {
				runAdjusting(() -> {
					if (thumb.getX() - trackInc > 0) {
						thumb.setX(thumb.getX() - trackInc);
					} else {
						thumb.setX(0);
					}
				});
				scrollScrollableArea(true);
			} else if (evt.getX() - getAbsoluteX() > thumb.getX() + thumb.getWidth()) {
				runAdjusting(() -> {
					if (thumb.getX() + trackInc < track.getWidth() - thumb.getWidth()) {
						thumb.setX(thumb.getX() + trackInc);
					} else {
						thumb.setX(track.getWidth() - thumb.getWidth());
					}
				});
				scrollScrollableArea(true);
			}
		}
	}

	@Override
	protected void configureStyledElement() {
		layoutManager = new AbstractGenericLayout<ScrollBar, Void>() {

			@Override
			protected Vector2f calcPreferredSize(ScrollBar container) {
				Vector2f pad = super.calcPreferredSize(container);
				Vector2f upPref = btnUp.calcPreferredSize();
				Vector2f downPref = btnDown.calcPreferredSize();
				Vector2f trackPref = track.calcPreferredSize();
				Vector2f thumbPref = thumb.calcPreferredSize();
				if (orientation == Orientation.VERTICAL)
					return new Vector2f(
							Math.max(Math.max(Math.max(upPref.x, downPref.x), trackPref.x), thumbPref.x) + pad.x,
							upPref.y + container.getIndent() + trackPref.y + container.getIndent() + downPref.y
									+ pad.y);
				else
					return new Vector2f(
							upPref.x + container.getIndent() + trackPref.x + container.getIndent() + downPref.x + pad.x,
							Math.max(Math.max(Math.max(upPref.y, downPref.y), trackPref.y), thumbPref.y) + pad.y);
			}

			@Override
			protected void onLayout(ScrollBar container) {

				Vector4f textPaddingVec = container.getAllPadding();
				Vector2f size = container.getDimensions().subtract(container.getTotalPadding());
				Vector2f position = new Vector2f(textPaddingVec.x, textPaddingVec.z);
				float ratio = getThumbRatio();

				if (orientation == Orientation.VERTICAL) {
					btnUp.setBounds(position.x, position.y, size.x, size.x);
					btnDown.setBounds(position.x, size.y - size.x, size.x, size.x);
					track.setBounds(position.x, position.y + container.getIndent() + size.x, size.x,
							size.y - (container.getIndent() * 2f) - (size.x * 2f));
					if (thumb != null) {
						float h = track.getHeight() * ratio;
						float y = (track.getHeight() - h) * lastPos;
						runAdjusting(() -> {
							thumb.setBounds(position.x, y, size.x, h);
						});
					}
				} else {
					btnDown.setBounds(position.x, position.y, size.y, size.y);
					btnUp.setBounds(size.x - size.y, position.y, size.y, size.y);
					track.setBounds(position.x + container.getIndent() + size.y, position.y,
							size.x - (container.getIndent() * 2f) - (size.y * 2f), size.y);
					if (thumb != null) {
						float w = track.getWidth() * ratio;
						float x = (track.getWidth() - w) * lastPos;
						runAdjusting(() -> {
							thumb.setBounds(x, position.y, w, size.y);
						});
					}
				}
			}

		};

		btnUp = new Button(screen) {
			{
				styleClass = "scroll-button increase";
			}
		};
		btnUp.onMouseHeld(evt -> {
			scrollUp(getItemIncrement());
		});
		btnUp.onMouseReleased(evt -> {
			scrollUp(getItemIncrement());
		});
		btnUp.setInterval(100);

		track = new Button(screen) {

			{
				styleClass = "track";
				layoutManager = new AbstractGenericLayout<BaseElement, Object>() {
				};
			}
		};
		track.onMousePressed(evt -> {
			held = false;
		});
		track.onMouseHeld(evt -> {
			held = true;
			adjustForTrackEvent(getItemIncrement(), evt);
		});
		track.onMouseReleased(evt -> {
			if (!held)
				adjustForTrackEvent(getBlockIncrement(), evt);
		});
		track.setInterval(100);

		thumb = new Button(screen) {
			{
				styleClass = "thumb";
			}
		};
		thumb.onElementEvent(evt -> {
			if (!ScrollBar.this.isAdjusting())
				scrollScrollableArea(false);
			// track.focus();
		}, Type.MOVED);
		thumb.setMovable(true);
		thumb.setKeyboardFocusable(false);
		thumb.setLockToParentBounds(true);
		track.addElement(thumb);

		btnDown = new Button(screen) {
			{
				styleClass = "scroll-button decrease";
			}
		};
		btnDown.onMouseHeld(evt -> {
			scrollDown(getItemIncrement());
		});
		btnDown.onMouseReleased(evt -> {
			scrollDown(getItemIncrement());
		});
		btnDown.setInterval(100);

		onMouseWheel(evt -> {
			switch (evt.getDirection()) {
			case up:
				scrollDown(getItemIncrement());
				evt.setConsumed();
				break;
			case down:
				scrollUp(getItemIncrement());
				evt.setConsumed();
				break;
			default:
				break;
			}

		});
		onNavigationKey(evt -> {
			if (evt.getKeyCode() == KeyInput.KEY_HOME) {
				if (evt.isPressed())
					scrollToTop();
				evt.setConsumed();
			} else if (evt.getKeyCode() == KeyInput.KEY_END) {
				if (evt.isPressed())
					scrollToBottom();
				evt.setConsumed();
			} else {
				if (evt.getKeyCode() == KeyInput.KEY_PGDN && evt.isNoModifiers()) {
					if (evt.isPressed())
						scrollDown(getBlockIncrement());
					evt.setConsumed();
				} else if (evt.getKeyCode() == KeyInput.KEY_PGUP && evt.isNoModifiers()) {
					if (evt.isPressed())
						scrollUp(getBlockIncrement());
					evt.setConsumed();
				} else if (evt.getKeyCode() == KeyInput.KEY_DOWN && evt.isNoModifiers()) {
					if (evt.isPressed())
						scrollDown(getItemIncrement());
					evt.setConsumed();
				} else if (evt.getKeyCode() == KeyInput.KEY_UP && evt.isNoModifiers()) {
					if (evt.isPressed())
						scrollUp(getItemIncrement());
					evt.setConsumed();
				}
			}
		});
		scrollScrollableArea(false);
	}

	protected void scrollDown(float amt) {
		float buttonInc = (amt / maximumValue)
				* (orientation == Orientation.VERTICAL ? track.getHeight() : track.getWidth());
		runAdjusting(() -> {
			if (orientation == Orientation.VERTICAL) {
				float thumbY = thumb.getY();
				if (thumbY < (track.getHeight() - thumb.getHeight())) {
					thumb.setY(thumbY + buttonInc);
				}
			} else {

				if (thumb.getX() > 0) {
					thumb.setX(thumb.getX() - buttonInc);
				}
			}
		});
		scrollScrollableArea(true);
	}

	protected void scrollToTop() {
		runAdjusting(() -> {
			if (orientation == Orientation.VERTICAL) {
				thumb.setY(0);
			} else {
				thumb.setX(0);
			}
		});
		scrollScrollableArea(true);
	}

	protected void scrollToBottom() {
		runAdjusting(() -> {
			if (orientation == Orientation.VERTICAL) {
				thumb.setY(track.getHeight() - thumb.getHeight());
			} else {
				thumb.setX(track.getWidth() - thumb.getWidth());
			}
		});
		scrollScrollableArea(true);
	}

	protected void scrollUp(float amt) {
		float buttonInc = (amt / maximumValue)
				* (orientation == Orientation.VERTICAL ? track.getHeight() : track.getWidth());
		runAdjusting(() -> {
			if (orientation == Orientation.VERTICAL) {
				float thumbY = thumb.getY();
				if (thumbY > 0) {
					thumb.setY(thumbY - buttonInc);
				}
			} else {

				if (thumb.getX() < (track.getWidth() - thumb.getWidth())) {
					thumb.setX(thumb.getX() + buttonInc);
				}
			}
		});
		scrollScrollableArea(true);
	}

	private float getThumbRatio() {
		float ratio = thumbValue / maximumValue;
		if (ratio > 1f)
			ratio = 1f;
		return ratio;
	}

	private void initControl() {

		if (orientation == Orientation.VERTICAL) {
			addElement(btnUp);
			addElement(track);
			addElement(btnDown);
		} else {
			addElement(btnDown);
			addElement(track);
			addElement(btnUp);
		}
	}

	private void scrollScrollableArea(boolean temp) {
		float newAmt = getRelativeScrollAmount();
		if (newAmt != lastPos) {
			float oldAmt = lastPos;
			lastPos = newAmt;
			if (changeSupport != null) {
				changeSupport
						.fireEvent(new UIChangeEvent<ScrollBar, Float>(this, maximumValue * oldAmt, getCurrentValue())
								.setTemporary(temp));
			}
		}
	}

}
