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
package icetone.controls.lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.buttons.Button;
import icetone.core.AbstractGenericLayout;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Layout.LayoutType;
import icetone.core.Orientation;
import icetone.core.Element;
import icetone.core.utils.ClassUtil;
import icetone.effects.BatchEffect;
import icetone.effects.EffectList;
import icetone.effects.RunEffect;
import icetone.effects.SlideToEffect;

/**
 *
 * @author t0neg0d
 * @author rockfire
 */
public class SlideTray extends Element {

	public class SlideElementsLayoutManager extends AbstractGenericLayout<BaseElement, Object> {

		@Override
		protected Vector2f calcPreferredSize(BaseElement parent) {
			Vector2f sz = new Vector2f();
			if (orientation == Orientation.HORIZONTAL) {
				for (BaseElement e : parent.getElements()) {
					Vector2f p = e.calcPreferredSize();
					sz.x += p.x;
					sz.y = Math.max(p.y, sz.y);
					;
				}
				sz.x -= Math.max(0, parent.getElements().size() - 1) * SlideTray.this.getIndent();
			} else {
				for (BaseElement e : parent.getElements()) {
					Vector2f p = e.calcPreferredSize();
					sz.y += p.y;
					sz.x = Math.max(p.x, sz.x);
					;
				}
				sz.y -= Math.max(0, parent.getElements().size() - 1) * SlideTray.this.getIndent();
			}
			return sz.addLocal(parent.getTotalPadding());
		}

		@Override
		protected void onLayout(BaseElement container) {
			Vector4f padding = container.getAllPadding();
			Vector2f sz = container.getDimensions().clone().subtractLocal(container.getTotalPadding());

			if (orientation == Orientation.HORIZONTAL) {
				float x = padding.x + currentPosition;
				for (BaseElement e : container.getElements()) {
					Vector2f p = e.calcPreferredSize();
					float y;
					switch (container.getTextVAlign()) {
					case Top:
						y = padding.z;
						break;
					case Bottom:
						y = sz.y - p.y - padding.w;
						break;
					default:
						y = padding.w - padding.z + (sz.y - p.y) / 2f;
						break;
					}
					e.setBounds(x, y, p.x, p.y);
					x += p.x - SlideTray.this.getIndent();
				}
			} else {
				float y = padding.y + currentPosition;
				for (BaseElement e : container.getElements()) {
					Vector2f p = e.calcPreferredSize();
					float x;
					switch (container.getTextAlign()) {
					case Left:
						x = padding.x;
						break;
					case Right:
						x = sz.x - p.x - padding.y;
						break;
					default:
						x = padding.y - padding.x + (sz.x - p.x) / 2f;
						break;
					}
					e.setBounds(x, y, p.x, p.y);
					y += p.y - SlideTray.this.getIndent();
				}
			}
		}
	}

	public class SlideTrayLayoutManager extends AbstractGenericLayout<SlideTray, Object> {

		@Override
		protected Vector2f calcPreferredSize(SlideTray parent) {
			Vector2f traySize = new Vector2f();
			Vector2f nextSize = btnNextElement.calcPreferredSize();
			Vector2f prevSize = btnPrevElement.calcPreferredSize();
			Vector2f elsSize = elTray.calcPreferredSize();
			if (orientation == Orientation.HORIZONTAL) {
				traySize.x += nextSize.x + prevSize.x + elsSize.x;
				traySize.y = Math.max(elsSize.y, Math.max(nextSize.y, prevSize.y));
			} else {
				traySize.y += nextSize.y + prevSize.y + elsSize.y;
				traySize.x = Math.max(elsSize.x, Math.max(nextSize.x, prevSize.x));
			}
			return traySize.addLocal(parent.getTotalPadding());
		}

		@Override
		protected void onLayout(SlideTray container) {

			Vector2f nextSize = btnNextElement.calcPreferredSize();
			Vector2f prevSize = btnPrevElement.calcPreferredSize();
			Vector4f textPadding = container.getAllPadding();

			// currentPosition = 0;

			if (orientation == Orientation.HORIZONTAL) {
				float ny = (container.getHeight() - nextSize.y) / 2f;
				float py = (container.getHeight() - prevSize.y) / 2f;
				switch (container.getTextVAlign()) {
				case Top:
					ny = py = textPadding.z;
					break;
				case Bottom:
					ny = container.getHeight() - nextSize.y - textPadding.w;
					py = container.getHeight() - prevSize.y - textPadding.w;
					break;
				default:
					break;
				}

				elTray.setBounds(textPadding.x + prevSize.x, textPadding.z,
						container.getWidth() - textPadding.x - textPadding.y - prevSize.x - nextSize.x,
						getHeight() - textPadding.z - textPadding.w);
				btnPrevElement.setBounds(textPadding.x, py, prevSize.x, prevSize.y);
				btnNextElement.setBounds(getWidth() - textPadding.y - nextSize.x, ny, nextSize.x, nextSize.y);
			} else {

				float nx = (container.getWidth() - nextSize.x) / 2f;
				float px = (container.getWidth() - prevSize.x) / 2f;
				switch (container.getTextAlign()) {
				case Left:
					nx = px = textPadding.x;
					break;
				case Right:
					nx = container.getWidth() - nextSize.x - textPadding.y;
					px = container.getWidth() - prevSize.y - textPadding.y;
					break;
				default:
					break;
				}

				elTray.setBounds(textPadding.x, textPadding.z + prevSize.y,
						container.getWidth() - textPadding.x - textPadding.y,
						getHeight() - textPadding.z - textPadding.w - prevSize.y - nextSize.y);
				btnPrevElement.setBounds(px, textPadding.z, prevSize.x, prevSize.y);
				btnNextElement.setBounds(nx, getHeight() - textPadding.w, nextSize.x, nextSize.y);
			}
			hideShowButtons();
		}
	}

	public static enum ZOrderSort {
		FIRST_TO_LAST, LAST_TO_FIRST
	}

	protected Button btnPrevElement, btnNextElement;
	protected int currentElementIndex = 0;

	private BatchEffect batch = null;
	private float currentPosition = 0;
	private Element elTray;
	private float lastOffset = 0;
	private Orientation orientation = Orientation.HORIZONTAL;
	private int selectedIndex;
	private ZOrderSort sort = ZOrderSort.FIRST_TO_LAST;
	private boolean useSlideEffect = false;

	{
		styleClass = orientation.name().toLowerCase();
	}

	/**
	 * Creates a new horizontal instance of the SlideTray control to use the
	 * default screen
	 */
	public SlideTray() {
		this(BaseScreen.get());
	}

	/**
	 * Creates a new instance of the SlideTray control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param orientation
	 *            The orientation of the SlideTray
	 */
	public SlideTray(BaseScreen screen) {
		this(screen, Orientation.HORIZONTAL);
	}

	/**
	 * Creates a new instance of the SlideTray control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param orientation
	 *            The orientation of the SlideTray
	 */
	public SlideTray(BaseScreen screen, Orientation orientation) {
		super(screen);
		this.orientation = orientation;
	}

	/**
	 * Creates a new instance of the SlideTray control
	 * 
	 * @param orientation
	 *            The orientation of the SlideTray
	 */
	public SlideTray(Orientation orientation) {
		this(BaseScreen.get(), orientation);
	}

	/**
	 * Adds the provided Element as a tray item
	 * 
	 * @param element
	 */
	public SlideTray addTrayElement(BaseElement element) {
		if (element instanceof Element) {
			((Element) element).addStyleClass("tray-element");
		}
		elTray.showElement(element);
		dirtyLayout(false, LayoutType.boundsChange());
		currentPosition = calcCurrentPosition();
		layoutChildren();
		return this;
	}

	public SlideTray setOrientation(Orientation orientation) {
		if (!Objects.equals(orientation, this.orientation)) {
			this.orientation = orientation;
			dirtyLayout(false, LayoutType.all);
		}
		return this;
	}

	public Orientation getOrientation() {
		return orientation;
	}

	@Override
	public List<String> getStyleClassNames() {
		List<String> l = new ArrayList<>(super.getStyleClassNames());
		l.add((Orientation.HORIZONTAL.equals(getOrientation()) ? "Horizontal" : "Vertical")
				+ ClassUtil.getMainClassName(getClass()));
		return l;
	}

	public BaseElement getNextButton() {
		return btnNextElement;
	}

	public BaseElement getPreviousButton() {
		return btnNextElement;
	}

	public List<BaseElement> getTrayElements() {
		return elTray.getElements();
	}

	public SlideTray nextElement() {
		currentPosition = calcCurrentPosition();
		if (currentElementIndex + 1 < elTray.getElements().size()) {
			float diff = getNextOffset(true);
			if (useSlideEffect)
				slideTabs(FastMath.abs(diff), true);
			else {
				moveTabs(FastMath.abs(diff), true);
				hideShowButtons();
				recalcIndex();
			}
		}
		return this;
	}

	public SlideTray prevElement() {
		float diff = getNextOffset(false);
		if (useSlideEffect)
			slideTabs(diff, false);
		else
			moveTabs(diff, false);
		if (!useSlideEffect) {
			recalcIndex();
			hideShowButtons();
		}
		currentPosition = calcCurrentPosition();
		return this;
	}

	public void removeAllTrayElements() {
		elTray.removeAllChildren();
		dirtyLayout(false, LayoutType.boundsChange());
		layoutChildren();
	}

	/**
	 * Remome the provided Element fromm the tray
	 * 
	 * @param element
	 */
	public SlideTray removeTrayElement(BaseElement element) {
		if (element.isDestroyOnHide())
			element.hide();
		else
			elTray.removeElement(element);
		selectedIndex = elTray.getElements().indexOf(element);
		dirtyLayout(false, LayoutType.boundsChange());
		layoutChildren();
		return this;
	}

	/**
	 * Enables/disables the use of the SlideTo effect when using next/previous
	 * buttons
	 * 
	 * @param useSlideEffect
	 */
	public SlideTray setUseSlideEffect(boolean useSlideEffect) {
		this.useSlideEffect = useSlideEffect;
		return this;
	}

	public SlideTray setZOrderSorting(ZOrderSort sort) {
		this.sort = sort;
		dirtyLayout(false, LayoutType.boundsChange());
		layoutChildren();
		return this;
	}

	public void toFront(Button selectedTab) {
		selectedIndex = elTray.getElements().indexOf(selectedTab);
		dirtyLayout(false, LayoutType.boundsChange());
		layoutChildren();
	}

	protected float calcCurrentPosition() {
		if (orientation == Orientation.HORIZONTAL)
			return elTray.getElements().get(0).getX();
		else
			return elTray.getHeight() - elTray.getElements().get(0).getY();
	}

	@Override
	protected void configureStyledElement() {

		btnPrevElement = new Button(screen) {
			{
				setStyleClass("slide slide-previous");
			}
		};
		btnPrevElement.onMouseReleased(evt -> {
			if (batch == null)
				prevElement();
			else if (!batch.getIsActive())
				prevElement();
		});

		btnNextElement = new Button(screen) {
			{
				setStyleClass("slide slide-next");
			}
		};
		btnNextElement.onMouseReleased(evt -> {
			if (batch == null)
				nextElement();
			else if (!batch.getIsActive())
				nextElement();
		});

		elTray = new Element(screen) {
			{
				setStyleClass("tray");
				setLayoutManager(new SlideElementsLayoutManager());
			}

			@Override
			protected Collection<BaseElement> getZSortedChildren() {
				List<BaseElement> sorted = new ArrayList<>(super.getZSortedChildren());
				Collections.sort(sorted, new Comparator<BaseElement>() {
					@Override
					public int compare(BaseElement o1, BaseElement o2) {
						Integer i1 = childList.indexOf(o1);
						if (i1 < selectedIndex)
							i1 = Integer.MAX_VALUE;
						Integer i2 = childList.indexOf(o2);
						if (i2 < selectedIndex)
							i2 = Integer.MAX_VALUE;
						if (sort == ZOrderSort.LAST_TO_FIRST) {
							return i1.compareTo(i2) * -1;
						}
						return i1.compareTo(i2);
					}
				});
				return sorted;
			}
		};

		layoutManager = new SlideTrayLayoutManager();

		addElement(btnPrevElement);
		addElement(elTray);
		attachElement(btnNextElement);
		hideShowButtons();
	}

	protected void slideDone(float to, boolean dir) {
		moveTabs(FastMath.abs(to), dir);
		currentPosition = calcCurrentPosition();
		recalcIndex();
		hideShowButtons();
	}

	private float getNextOffset(boolean dir) {
		float diff = 0;
		final BaseElement endEl = elTray.getElements().get(elTray.getElements().size() - 1);
		final BaseElement thisEl = elTray.getElements().get(currentElementIndex);
		if (orientation == Orientation.HORIZONTAL) {
			if (dir) {
				if (lastOffset != 0)
					diff = (int) FastMath.abs(thisEl.getX());
				else {
					for (int i = currentElementIndex; i < elTray.getElements().size(); i++) {
						BaseElement nextTrayEl = elTray.getElements().get(i);
						float edge = nextTrayEl.getAbsoluteX() + nextTrayEl.getWidth();
						if (edge > btnNextElement.getAbsoluteX()) {
							diff = edge - btnNextElement.getAbsoluteX();
							break;
						}
					}
				}

				if ((endEl.getX() + endEl.getWidth()) - diff < elTray.getWidth()) {
					diff = FastMath.abs(elTray.getWidth() - (endEl.getX() + endEl.getWidth()));
					lastOffset = diff;
				}
			} else {
				// NOTE: Not 100% sure about this, it doesn't seem to cause any
				// problems
				// if (lastOffset != 0) {
				diff = (int) FastMath.abs(thisEl.getX());
				lastOffset = 0;
				// }
			}
		} else {
			if (dir) {
				if (lastOffset != 0)
					diff = (int) ((elTray.getHeight() - thisEl.getY()) - (thisEl.getHeight() + getIndent()));
				else {
					for (int i = 0; i < elTray.getElements().size(); i++) {
						BaseElement nextTrayEl = elTray.getElements().get(i);
						if (nextTrayEl.getY() < 0) {
							diff = FastMath.abs(nextTrayEl.getY());
							break;
						}
					}
				}

				if (endEl.getY() + diff > 0) {
					diff -= (endEl.getY() + diff);
					lastOffset = diff;
				}
			} else {
				// NOTE: Not 100% sure about this, it doesn't seem to cause any
				// problems
				if (lastOffset != 0) {
					diff = -(int) ((elTray.getHeight() - elTray.getElements().get(currentElementIndex - 1).getY())
							- (elTray.getElements().get(currentElementIndex - 1).getHeight() + getIndent()));
					lastOffset = 0;
				} else {
					diff = FastMath.abs(getHeight() - (thisEl.getY() + thisEl.getHeight()));
					if (diff == 0) {
						BaseElement prevEl = elTray.getElements().get(currentElementIndex - 1);
						diff = FastMath.abs(getHeight() - (prevEl.getY() + prevEl.getHeight()));
					}
				}
			}
		}
		return diff;
	}

	private void hideShowButtons() {
		if (elTray.getElements().isEmpty())
			return;
		BaseElement lastEl = elTray.getElements().get(elTray.getElements().size() - 1);
		if (orientation == Orientation.HORIZONTAL) {
			if (currentElementIndex == 0 && Math.round(calcCurrentPosition()) == 0)
				btnPrevElement.hide();
			else
				btnPrevElement.show();
			if (lastEl.getAbsoluteX() + lastEl.getWidth() <= btnNextElement.getAbsoluteX())
				btnNextElement.hide();
			else
				btnNextElement.show();
		} else {
			if (currentElementIndex == 0 && Math.round(calcCurrentPosition()) == getHeight())
				btnPrevElement.hide();
			else
				btnPrevElement.show();
			if (lastEl.getY() >= 0)
				btnNextElement.hide();
			else
				btnNextElement.show();
		}
	}

	private void moveTabs(float diff, boolean dir) {
		for (BaseElement el : elTray.getElements()) {
			if (orientation == Orientation.HORIZONTAL) {
				float nextX = (!dir) ? el.getX() + diff : el.getX() - diff;
				el.setX(nextX);
			} else {
				float nextY = (!dir) ? el.getY() - diff : el.getY() + diff;
				el.setY(nextY);
			}
		}
	}

	private void recalcIndex() {
		// Recalc current element index
		if (orientation == Orientation.HORIZONTAL) {
			for (int i = 0; i < elTray.getElements().size(); i++) {
				final BaseElement el = elTray.getElements().get(i);
				if (el.getX() + el.getWidth() >= 0) {
					currentElementIndex = i;
					return;
				}
			}
		} else {
			for (int i = elTray.getElements().size() - 1; i >= 0; i--) {
				final BaseElement el = elTray.getElements().get(i);
				if (el.getY() + el.getHeight() >= getHeight()) {
					currentElementIndex = i;
					return;
				}
			}
		}
		currentElementIndex = 0;
	}

	private void slideTabs(float diff, boolean dir) {
		batch = new BatchEffect();
		BaseElement first = null;
		for (BaseElement el : elTray.getElements()) {
			if (first == null)
				first = el;
			if (orientation == Orientation.HORIZONTAL)
				batch.addEffect(
						new SlideToEffect(0.25f, new Vector2f((!dir) ? el.getX() + diff : el.getX() - diff, el.getY()))
								.setElement(el));
			else
				batch.addEffect(
						new SlideToEffect(0.25f, new Vector2f(el.getX(), (!dir) ? el.getY() - diff : el.getY() + diff))
								.setElement(el));
		}
		screen.getEffectManager().applyEffect(new EffectList(batch, new RunEffect(() -> slideDone(diff, dir))));
	}

}
