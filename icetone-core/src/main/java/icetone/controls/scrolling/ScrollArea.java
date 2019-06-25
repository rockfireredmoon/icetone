/**
 * ICETONE - A GUI Library for JME3 based on a heavily modified version of 
 * Tonegod's 'Tonegodgui'.  
 * 
 * Copyright (c) 2018, Emerald Icemoon
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

import java.util.List;

import com.jme3.math.Vector2f;

import icetone.controls.scrolling.ScrollPanel.ScrollDirection;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Element;
import icetone.core.Layout.LayoutType;
import icetone.core.Orientation;
import icetone.core.layout.WrappingLayout;
import icetone.css.CssEvent;
import icetone.css.CssEventTrigger;
import icetone.effects.AbstractPositionedEffect;
import icetone.effects.EffectChannel;
import icetone.effects.IEffect;

public class ScrollArea extends Element implements ScrollViewport {

	public final static CssEvent SCROLL_Y = new CssEvent("scroll-y");
	public final static CssEvent SCROLL_X = new CssEvent("scroll-x");

	private boolean paging = true;
	private ScrollEventSupport scrollEventSupport;

	{
		styleClass = "scrollable-area";
		/*
		 * Default scrollable child layout wraps vertically (i.e. runs horizontally to
		 * the edges then wraps until all vertical space is taken at which point the
		 * content becomes vertically scrollable. If the content does not fit
		 * horizontally, the horizontal bar will also be shown. This behaviour can be
		 * changed by clients accessing the layout and reconfiguring it.
		 */

		WrappingLayout wrappingLayout = new WrappingLayout();
		wrappingLayout.setOrientation(Orientation.VERTICAL);
		layoutManager = wrappingLayout;
	}

	public ScrollArea(BaseScreen screen) {
		super(screen);
	}

	public ScrollArea removeScrollListener(ScrollEventListener l) {
		if (scrollEventSupport != null)
			scrollEventSupport.removeListener(l);
		return this;
	}

	public ScrollArea addScreenListener(ScrollEventListener l) {
		if (scrollEventSupport == null)
			scrollEventSupport = new ScrollEventSupport();
		scrollEventSupport.addListener(l);
		return this;
	}

	public ScrollArea onScrollEvent(ScrollEventListener l) {
		return onScrollEvent(l, null);
	}

	public ScrollArea onScrollEvent(ScrollEventListener l, ScrollDirection direction) {
		if (scrollEventSupport == null)
			scrollEventSupport = new ScrollEventSupport();
		scrollEventSupport.bind(l, direction);
		return this;
	}

	public ScrollArea setPaging(boolean pagingEnabled) {
		this.paging = pagingEnabled;
		checkPagedContent(null);
		return this;
	}

	public boolean isPaging() {
		return paging;
	}

	public void scrollContent(ScrollDirection direction) {
		checkPagedContent(direction);
		dirtyLayout(false, LayoutType.clipping);
		if (scrollEventSupport != null)
			scrollEventSupport.fireEvent(new ScrollEvent(this, direction));
		layoutChildren();
	}

	public void scrollXTo(float newX, boolean fx) {
		BaseElement innerBounds = getElementParent();
		List<IEffect> ef = screen.getEffectManager().getEffectsFor(this, EffectChannel.fx);
		IEffect effect = null;
		for (int i = ef.size() - 1; i >= 0; i--) {
			IEffect ieffect = ef.get(i);
			if (ieffect instanceof AbstractPositionedEffect) {
				Vector2f dest = ((AbstractPositionedEffect) ieffect).getEffectDestination();
				Vector2f pp = getPixelPosition();
				Vector2f diff = pp.subtract(dest);
				if (!diff.equals(Vector2f.ZERO)) {
					effect = ieffect;
					effect.setIsActive(false);
					newX -= diff.x;
					break;
				}
			}
		}
		float vpw = getWidth() - innerBounds.getWidth();
		if (newX > 0)
			newX = 0;
		else if (newX < -vpw)
			newX = -vpw;
		float lastX = getX();
		try {
			if (fx && isInStyleHierarchy()) {
				Vector2f target = new Vector2f(newX, getY());
				if (triggerCssEvent(new CssEventTrigger<AbstractPositionedEffect>(SCROLL_Y, (evt) -> {
					evt.setEffectDestination(target);
					evt.setReset(false);
//					dirtyLayout(false, LayoutType.styling, LayoutType.clipping);
//					layoutChildren();
				})).isProcessed())
					return;
			}
			setX(newX);
		} finally {
			if (lastX > newX)
				scrollContent(ScrollDirection.Left);
			else
				scrollContent(ScrollDirection.Right);
		}
	}

	public void scrollYTo(float newY, boolean fx) {
		BaseElement innerBounds = getElementParent();
		List<IEffect> ef = screen.getEffectManager().getEffectsFor(this, EffectChannel.fx);
		IEffect effect = null;
		for (int i = ef.size() - 1; i >= 0; i--) {
			IEffect ieffect = ef.get(i);
			if (ieffect instanceof AbstractPositionedEffect) {
				Vector2f dest = ((AbstractPositionedEffect) ieffect).getEffectDestination();
				Vector2f pp = getPixelPosition();
				Vector2f diff = pp.subtract(dest);
				if (!diff.equals(Vector2f.ZERO)) {
					effect = ieffect;
					effect.setIsActive(false);
					newY -= diff.y;
					break;
				}
			}
		}
		float vph = getHeight() - innerBounds.getHeight();
		if (newY > 0)
			newY = 0;
		else if (newY < -vph)
			newY = -vph;
		float lastY = getY();
		try {
			if (fx && isInStyleHierarchy()) {
				Vector2f target = new Vector2f(getX(), newY);
				if (triggerCssEvent(new CssEventTrigger<AbstractPositionedEffect>(SCROLL_Y, (evt) -> {
					evt.setEffectDestination(target);
					evt.setReset(false);
					dirtyLayout(false, LayoutType.styling, LayoutType.clipping);
					layoutChildren();
				})).isProcessed())
					return;
			}
			setY(newY);
		} finally {
			if (lastY > newY)
				scrollContent(ScrollDirection.Down);
			else
				scrollContent(ScrollDirection.Up);
		}
	}

	protected void checkPagedContent(ScrollDirection direction) {
		if (!paging)
			return;

		BaseElement innerBounds = getElementParent();

		// TODO don't use hide/show . have own mechanism so paging doesnt
		// interfere with user visibility requirements
		for (BaseElement el : getElements()) {
			if (direction == null) {
				if ((el.getY() + el.getHeight() + getY() < 0 || el.getY() + getY() > innerBounds.getHeight())
						|| el.getX() + el.getWidth() + getX() < 0 || el.getX() + getX() > innerBounds.getWidth()) {
					el.setVisibilityAllowed(false);
				} else {
					el.setVisibilityAllowed(true);
				}
			} else if (direction == ScrollDirection.Up || direction == ScrollDirection.Down) {
				if (el.getY() + el.getHeight() + getY() < 0 || el.getY() + getY() > innerBounds.getHeight()) {
					el.setVisibilityAllowed(false);
				} else {
					el.setVisibilityAllowed(true);
				}
			} else if (direction == ScrollDirection.Left || direction == ScrollDirection.Right) {
				if (el.getX() + el.getWidth() + getX() < 0 || el.getX() + getX() > innerBounds.getWidth()) {
					el.setVisibilityAllowed(false);
				} else {
					el.setVisibilityAllowed(true);
				}
			}
		}
	}
}
