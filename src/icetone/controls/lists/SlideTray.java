/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icetone.controls.lists;

import java.util.ArrayList;
import java.util.List;

import com.jme3.font.BitmapFont.Align;
import com.jme3.font.BitmapFont.VAlign;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.buttons.ButtonAdapter;
import icetone.core.Container;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.layout.AbstractLayout;
import icetone.core.layout.LUtil;
import icetone.core.utils.UIDUtil;
import icetone.effects.BatchEffect;
import icetone.effects.Effect;
import icetone.framework.core.util.GameTimer;

/**
 *
 * @author t0neg0d
 */
public class SlideTray extends Element {
	/*
	public static enum Orientation {
		VERTICAL,
		HORIZONTAL
	}
	*/

	public static enum ZOrderSort {
		FIRST_TO_LAST, LAST_TO_FIRST
	}

	private Orientation orientation;

	private ZOrderSort sort = ZOrderSort.FIRST_TO_LAST;

	protected ButtonAdapter btnPrevElement, btnNextElement;
	private Element elTray;

	protected List<Element> trayElements = new ArrayList<>();
	protected int currentElementIndex = 0;

	protected float overhang = 5;

	private boolean useSlideEffect = false;
	private Effect slideEffect;

	private BatchEffect batch = null;
	private GameTimer timer;

	private float currentPosition = 0;
	private float lastOffset = 0;

	private Vector2f buttonSize;

	private Element front;

	/**
	 * Creates a new instance of the SlideTray control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param orientation
	 *            The orientation of the SlideTray
	 */
	public SlideTray(ElementManager screen, Orientation orientation) {
		this(screen, UIDUtil.getUID(), Vector2f.ZERO, LUtil.LAYOUT_SIZE,
				screen.getStyle("Tab#SlideTray").getVector4f("resizeBorders"),
				screen.getStyle("Tab#SlideTray").getString("defaultImg"), orientation);
	}

	/**
	 * Creates a new instance of the SlideTray control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param orientation
	 *            The orientation of the SlideTray
	 */
	public SlideTray(ElementManager screen, String UID, Orientation orientation) {
		this(screen, UID, Vector2f.ZERO, LUtil.LAYOUT_SIZE, screen.getStyle("Tab#SlideTray").getVector4f("resizeBorders"),
				screen.getStyle("Tab#SlideTray").getString("defaultImg"), orientation);
	}

	/**
	 * Creates a new instance of the SlideTray control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param orientation
	 *            The orientation of the SlideTray
	 */
	public SlideTray(ElementManager screen, Vector2f position, Orientation orientation) {
		this(screen, UIDUtil.getUID(), position, LUtil.LAYOUT_SIZE, screen.getStyle("Tab#SlideTray").getVector4f("resizeBorders"),
				screen.getStyle("Tab#SlideTray").getString("defaultImg"), orientation);
	}

	/**
	 * Creates a new instance of the SlideTray control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 * @param orientation
	 *            The orientation of the SlideTray
	 */
	public SlideTray(ElementManager screen, Vector2f position, Vector2f dimensions, Orientation orientation) {
		this(screen, UIDUtil.getUID(), position, dimensions, screen.getStyle("Tab#SlideTray").getVector4f("resizeBorders"),
				screen.getStyle("Tab#SlideTray").getString("defaultImg"), orientation);
	}

	/**
	 * Creates a new instance of the SlideTray control
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
	 *            The default image to use for the SlideTray's track
	 * @param orientation
	 *            The orientation of the SlideTray
	 */
	public SlideTray(ElementManager screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg,
			Orientation orientation) {
		this(screen, UIDUtil.getUID(), position, dimensions, resizeBorders, defaultImg, orientation);
	}

	/**
	 * Creates a new instance of the SlideTray control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param orientation
	 *            The orientation of the SlideTray
	 */
	public SlideTray(ElementManager screen, String UID, Vector2f position, Orientation orientation) {
		this(screen, UID, position, LUtil.LAYOUT_SIZE, screen.getStyle("Tab#SlideTray").getVector4f("resizeBorders"),
				screen.getStyle("Tab#SlideTray").getString("defaultImg"), orientation);
	}

	/**
	 * Creates a new instance of the SlideTray control
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
	 * @param orientation
	 *            The orientation of the SlideTray
	 */
	public SlideTray(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Orientation orientation) {
		this(screen, UID, position, dimensions, screen.getStyle("Tab#SlideTray").getVector4f("resizeBorders"),
				screen.getStyle("Tab#SlideTray").getString("defaultImg"), orientation);
	}

	/**
	 * Creates a new instance of the SlideTray control
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
	 *            The default image to use for the SlideTray's track
	 * @param orientation
	 *            The orientation of the SlideTray
	 */
	public SlideTray(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
			String defaultImg, Orientation orientation) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);
		this.orientation = orientation;
		setIgnoreMouse(true);
		initControl();
	}

	private void initControl() {

		buttonSize = screen.getStyle("Tab#SlideTray").getVector2f("buttonSize");
		Vector2f arrowSize = screen.getStyle("Tab#SlideTray").getVector2f("arrowSize");
		if (arrowSize == null) {
			arrowSize = screen.getStyle("Button").getVector2f("defaultSize");
			arrowSize = arrowSize.divide(2f);
		}

		slideEffect = new Effect(Effect.EffectType.SlideTo, Effect.EffectEvent.Show, .25f);

		// Note to tonegod

		// Must take longer than the slide itself or the buttons will still be
		// in the right place.
		//
		// Is there no way to execute code when an event ACTUALLY finishes
		// instead of
		// relying on a separate timed event?
		//
		// Rockfire

		//
		// NOTE 2 - It seems .1 of a second is not good enough either. I
		// Have changed to 0.5 :\
		timer = new GameTimer(.5f) {
			@Override
			public void onComplete(float time) {
				if (orientation == Orientation.HORIZONTAL)
					currentPosition = trayElements.get(0).getX();
				else
					currentPosition = elTray.getHeight() - trayElements.get(0).getY();
				recalcIndex();
				hideShowButtons();
			}
		};

		btnPrevElement = new ButtonAdapter(screen, getUID() + ":btnPrevElement", Vector2f.ZERO, buttonSize, Vector4f.ZERO, null) {
			@Override
			public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean isToggled) {
				if (batch == null)
					prevElement();
				else if (!batch.getIsActive())
					prevElement();
			}
		};
		if (orientation == Orientation.HORIZONTAL)
			btnPrevElement.setButtonIcon(arrowSize.x, arrowSize.y, screen.getStyle("Common").getString("arrowLeft"));
		else
			btnPrevElement.setButtonIcon(arrowSize.x, arrowSize.y, screen.getStyle("Common").getString("arrowUp"));
		btnPrevElement.clearAltImages();

		btnNextElement = new ButtonAdapter(screen, getUID() + ":btnNextElement", Vector2f.ZERO, buttonSize, Vector4f.ZERO, null) {
			@Override
			public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean isToggled) {
				if (batch == null)
					nextElement();
				else if (!batch.getIsActive())
					nextElement();
			}
		};
		if (orientation == Orientation.HORIZONTAL)
			btnNextElement.setButtonIcon(arrowSize.x, arrowSize.y, screen.getStyle("Common").getString("arrowRight"));
		else
			btnNextElement.setButtonIcon(arrowSize.x, arrowSize.y, screen.getStyle("Common").getString("arrowDown"));
		btnNextElement.clearAltImages();

		setOverhang(screen.getStyle("Tab#SlideTray").getFloat("gap"));

		elTray = new Container(screen, getUID() + ":elTray", Vector4f.ZERO, "Interface/bgy.jpg") {
			@Override
			public void controlResizeHook() {
				// if (orientation != Orientation.HORIZONTAL) {
				// if (!trayElements.isEmpty()) {
				// float nextY = currentPosition;
				// int index = 0;
				// for (Element el : trayElements) {
				// if (index > 0)
				// nextY += el.getHeight() + trayPadding;
				// el.setY(elTray.getHeight() - nextY);
				// index++;
				// }
				// }
				// }
				hideShowButtons();
			}
		};

		setLayoutManager(new SlideTrayLayoutManager());

		addChild(btnPrevElement);
		addChild(elTray);
		addChild(btnNextElement);
	}

	public List<Element> getTrayElements() {
		return trayElements;
	}

	public void toFront(Element front) {
		this.front = front;
		layoutChildren();
	}

	public Vector2f getButtonSize() {
		return buttonSize;
	}

	public void setButtonSize(Vector2f buttonSize) {
		this.buttonSize = buttonSize;
		layoutChildren();
	}

	public void setButtonSize(float size) {
		buttonSize.set(size, size);
		layoutChildren();
	}

	public void alignButtonsV(VAlign vAlign) {
		if (vAlign == VAlign.Top) {
			btnPrevElement.setY(getHeight() - btnPrevElement.getHeight());
			btnNextElement.setY(getHeight() - btnNextElement.getHeight());
		} else if (vAlign == VAlign.Center) {
			btnPrevElement.centerToParentV();
			btnNextElement.centerToParentV();
		} else if (vAlign == VAlign.Center) {
			btnPrevElement.setY(0);
			btnNextElement.setY(0);
		}
	}

	public void alignButtonsH(Align align) {
		if (align == Align.Right) {
			btnPrevElement.setX(getWidth() - btnPrevElement.getWidth());
			btnNextElement.setX(getWidth() - btnNextElement.getWidth());
		} else if (align == Align.Center) {
			btnPrevElement.centerToParentH();
			btnNextElement.centerToParentH();
		} else if (align == Align.Left) {
			btnPrevElement.setX(0);
			btnNextElement.setX(0);
		}
	}

	public void setZOrderSorting(ZOrderSort sort) {
		this.sort = sort;
		layoutChildren();
	}

	/**
	 * Returns the current slide tray padding value
	 * 
	 * @return
	 */
	public float getOverhang() {
		return this.overhang;
	}

	/**
	 * Sets the padding between slide tray elements
	 * 
	 * @param overhang
	 */
	public void setOverhang(float overhang) {
		this.overhang = overhang;
	}

	/**
	 * Enables/disables the use of the SlideTo effect when using next/previous
	 * buttons
	 * 
	 * @param useSlideEffect
	 */
	public void setUseSlideEffect(boolean useSlideEffect) {
		this.useSlideEffect = useSlideEffect;
	}

	/**
	 * Adds the provided Element as a tray item
	 * 
	 * @param element
	 */
	public void addTrayElement(Element element) {
		element.addClippingLayer(elTray);
		trayElements.add(element);
		elTray.addChild(element);
		hideShowButtons();
		if (orientation == Orientation.HORIZONTAL) {
			currentPosition = 0;
		} else {
			currentPosition = elTray.getHeight() - trayElements.get(0).getY();
		}
	}

	@Override
	protected void onBeforeLayout() {
		btnPrevElement.setIsVisible(currentElementIndex > 0);
	}

	@Override
	protected void onAfterLayout() {
		// TODO needed?
		elTray.updateClippingLayers();
		hideShowButtons();
	}

	private void recalcIndex() {
		// Recalc current element index
		if (orientation == Orientation.HORIZONTAL) {
			for (int i = 0; i < trayElements.size(); i++) {
				final Element el = trayElements.get(i);
				if (el.getX() + el.getWidth() >= 0) {
					currentElementIndex = i;
					return;
				}
			}
		} else {
			for (int i = trayElements.size() - 1; i >= 0; i--) {
				final Element el = trayElements.get(i);
				if (el.getY() + el.getHeight() >= getHeight()) {
					currentElementIndex = i;
					return;
				}
			}
		}
		currentElementIndex = 0;
	}

	public void nextElement() {
		if (currentElementIndex + 1 < trayElements.size()) {
			float diff = getNextOffset(true);
			if (useSlideEffect)
				slideTabs(FastMath.abs(diff), true);
			else
				moveTabs(FastMath.abs(diff), true);
			if (useSlideEffect) {
				timer.reset(false);
				screen.getAnimManager().addGameTimer(timer);
			} else {
				hideShowButtons();
				recalcIndex();
			}
		}
		if (orientation == Orientation.HORIZONTAL)
			currentPosition = trayElements.get(0).getX();
		else
			currentPosition = elTray.getHeight() - trayElements.get(0).getY();
	}

	public void prevElement() {
		float diff = getNextOffset(false);
		if (useSlideEffect)
			slideTabs(diff, false);
		else
			moveTabs(diff, false);
		recalcIndex();
		if (useSlideEffect) {
			timer.reset(false);
			screen.getAnimManager().addGameTimer(timer);
		} else {
			recalcIndex();
			hideShowButtons();
		}
		if (orientation == Orientation.HORIZONTAL)
			currentPosition = trayElements.get(0).getX();
		else
			currentPosition = elTray.getHeight() - trayElements.get(0).getY();
	}

	private float getNextOffset(boolean dir) {
		float diff = 0;
		final Element endEl = trayElements.get(trayElements.size() - 1);
		final Element thisEl = trayElements.get(currentElementIndex);
		if (orientation == Orientation.HORIZONTAL) {
			if (dir) {
				if (lastOffset != 0)
					diff = (int) FastMath.abs(thisEl.getX());
				else {
					for (int i = currentElementIndex; i < trayElements.size(); i++) {
						Element nextTrayEl = trayElements.get(i);
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
					diff = (int) ((elTray.getHeight() - thisEl.getY()) - (thisEl.getHeight() + overhang));
				else {
					for (int i = 0; i < trayElements.size(); i++) {
						Element nextTrayEl = trayElements.get(i);
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
					diff = -(int) ((elTray.getHeight() - trayElements.get(currentElementIndex - 1).getY())
							- (trayElements.get(currentElementIndex - 1).getHeight() + overhang));
					lastOffset = 0;
				} else {
					diff = FastMath.abs(getHeight() - (thisEl.getY() + thisEl.getHeight()));
					if (diff == 0) {
						Element prevEl = trayElements.get(currentElementIndex - 1);
						diff = FastMath.abs(getHeight() - (prevEl.getY() + prevEl.getHeight()));
					}
				}
			}
		}
		return diff;
	}

	private void slideTabs(float diff, boolean dir) {
		batch = new BatchEffect();
		for (Element el : trayElements) {
			if (orientation == Orientation.HORIZONTAL) {
				float nextX = (!dir) ? el.getX() + diff : el.getX() - diff;
				Vector2f destination = new Vector2f(nextX, el.getY());
				Effect effect = slideEffect.clone();
				effect.setElement(el);
				effect.setEffectDestination(destination);
				batch.addEffect(effect);
			} else {
				float nextY = (!dir) ? el.getY() - diff : el.getY() + diff;
				Vector2f destination = new Vector2f(el.getX(), nextY);
				Effect effect = slideEffect.clone();
				effect.setElement(el);
				effect.setEffectDestination(destination);
				batch.addEffect(effect);
			}
		}
		screen.getEffectManager().applyBatchEffect(batch);
	}

	private void moveTabs(float diff, boolean dir) {
		for (Element el : trayElements) {
			if (orientation == Orientation.HORIZONTAL) {
				float nextX = (!dir) ? el.getX() + diff : el.getX() - diff;
				el.setX(nextX);
			} else {
				float nextY = (!dir) ? el.getY() - diff : el.getY() + diff;
				el.setY(nextY);
			}
		}
	}

	private void hideShowButtons() {
		if (trayElements.isEmpty())
			return;
		Element lastEl = trayElements.get(trayElements.size() - 1);
		final Element firstEl = trayElements.get(0);
		if (orientation == Orientation.HORIZONTAL) {
			if (currentElementIndex == 0 && Math.round(firstEl.getX()) == 0)
				btnPrevElement.hide();
			else
				btnPrevElement.show();
			if (lastEl.getAbsoluteX() + lastEl.getWidth() <= btnNextElement.getAbsoluteX())
				btnNextElement.hide();
			else
				btnNextElement.show();
		} else {
			if (currentElementIndex == 0 && Math.round(firstEl.getY()) == getHeight() - firstEl.getHeight())
				btnPrevElement.hide();
			else
				btnPrevElement.show();
			if (lastEl.getY() >= 0)
				btnNextElement.hide();
			else
				btnNextElement.show();
		}
	}

	// private float getNextPosition() {
	// float ret = 0;
	// for (Element el : trayElements) {
	// if (orientation == Orientation.HORIZONTAL) {
	// ret += el.getWidth() + trayPadding;
	// } else {
	// ret += el.getHeight() + trayPadding;
	// }
	// }
	// return ret;
	// }

	@Override
	public void setControlClippingLayer(Element clippingLayer) {
		addClippingLayer(clippingLayer);
	}

	public class SlideTrayLayoutManager extends AbstractLayout {

		@Override
		public Vector2f minimumSize(Element parent) {
			Vector2f traySize = new Vector2f();
			if (orientation == Orientation.HORIZONTAL) {
				traySize.x += buttonSize.x * 2;
				traySize.y = buttonSize.y;
				for (int i = 0; i < elTray.getElementList().size(); i++) {
					if (i > 0)
						traySize.x += overhang;
					traySize.x += LUtil.getMinimumWidth(elTray.getElementList().get(i));
					traySize.y = Math.max(traySize.y, LUtil.getMinimumHeight(elTray.getElementList().get(i)));
				}
			} else {
				traySize.y += buttonSize.y * 2;
				traySize.x = buttonSize.x;
				for (int i = 0; i < elTray.getElementList().size(); i++) {
					if (i > 0)
						traySize.y += overhang;
					traySize.x = Math.max(traySize.x, LUtil.getMinimumWidth(elTray.getElementList().get(i)));
					traySize.y += LUtil.getMinimumHeight(elTray.getElementList().get(i));
				}
			}
			return traySize.addLocal(parent.getTextPaddingVec().x + parent.getTextPaddingVec().y,
					parent.getTextPaddingVec().z + parent.getTextPaddingVec().w);
//			return traySize;
		}

		@Override
		public Vector2f maximumSize(Element parent) {
//			Vector2f traySize = new Vector2f();
//			if (orientation == Orientation.HORIZONTAL) {
//				traySize.x += buttonSize.x * 2;
//				traySize.y = buttonSize.y;
//				for (int i = 0; i < elTray.getElementList().size(); i++) {
//					if (i > 0)
//						traySize.x += overhang;
//					traySize.x += LUtil.getMaximumWidth(elTray.getElementList().get(i));
//					traySize.y = Math.max(traySize.y, LUtil.getMaximumHeight(elTray.getElementList().get(i)));
//				}
//			} else {
//				traySize.y += buttonSize.y * 2;
//				traySize.x = buttonSize.x;
//				for (int i = 0; i < elTray.getElementList().size(); i++) {
//					if (i > 0)
//						traySize.y += overhang;
//					traySize.x = Math.max(traySize.x, LUtil.getMaximumWidth(elTray.getElementList().get(i)));
//					traySize.y += LUtil.getMaximumHeight(elTray.getElementList().get(i));
//				}
//			}
//			return traySize;
			return LUtil.DEFAULT_MAX_SIZE;
		}

		@Override
		public Vector2f preferredSize(Element parent) {
			Vector2f traySize = new Vector2f();
			if (orientation == Orientation.HORIZONTAL) {
				traySize.x += buttonSize.x * 2;
				traySize.y = buttonSize.y;
				for (int i = 0; i < elTray.getElementList().size(); i++) {
					if (i > 0)
						traySize.x += overhang;
					traySize.x += LUtil.getPreferredWidth(elTray.getElementList().get(i));
					traySize.y = Math.max(traySize.y, LUtil.getPreferredHeight(elTray.getElementList().get(i)));
				}
			} else {
				traySize.y += buttonSize.y * 2;
				traySize.x = buttonSize.x;
				for (int i = 0; i < elTray.getElementList().size(); i++) {
					if (i > 0)
						traySize.y += overhang;
					traySize.x = Math.max(traySize.x, LUtil.getPreferredWidth(elTray.getElementList().get(i)));
					traySize.y += LUtil.getPreferredHeight(elTray.getElementList().get(i));
				}
			}
			return traySize.addLocal(parent.getTextPaddingVec().x + parent.getTextPaddingVec().y,
					parent.getTextPaddingVec().z + parent.getTextPaddingVec().w);
		}

		@Override
		public void layout(Element container) {

			Vector2f sz = new Vector2f();
			Vector2f cps = preferredSize(container);

			LUtil.setDimensions(btnPrevElement, buttonSize);
			LUtil.setDimensions(btnNextElement, buttonSize);

			if (orientation == Orientation.HORIZONTAL) {

				float x = currentPosition;
				// Do the elements first (so we can get height)
				for (Element el : elTray.getElementList()) {
					Vector2f ps = LUtil.getPreferredSize(el).clone();
					ps.y = container.getHeight() - container.getTextPaddingVec().z - container.getTextPaddingVec().w;
					sz.y = Math.max(sz.y, ps.y);
					LUtil.setBounds(el, x, container.getTextPaddingVec().z, ps.x, ps.y);
					el.layoutChildren();
					x += (ps.x + overhang);
				}
//				resort();

				sz.y = Math.max(sz.y, buttonSize.y);

				LUtil.setPosition(btnPrevElement, new Vector2f(0, (sz.y - buttonSize.y) / 2f));
				LUtil.setPosition(btnNextElement, container.getWidth() - buttonSize.x, (sz.y - buttonSize.y) / 2f);
				LUtil.setBounds(elTray, buttonSize.x, container.getHeight() - cps.y, container.getWidth() - (buttonSize.x * 2),
						cps.y);
			} else {
				float y = currentPosition;
				// Do the elements first (so we can get height)
				for (Element el : elTray.getElementList()) {
					Vector2f ps = LUtil.getPreferredSize(el).clone();
					ps.x = container.getWidth() - container.getTextPaddingVec().x - container.getTextPaddingVec().y;
					sz.x = Math.max(sz.x, ps.x);
					LUtil.setBounds(el, container.getTextPaddingVec().x, y, ps.x, ps.y);
					el.layoutChildren();
					y += (ps.y + overhang);
				}
//				resort();

				sz.x = Math.max(sz.x, buttonSize.x);
				LUtil.setPosition(btnPrevElement, new Vector2f((sz.x - buttonSize.x) / 2f, 0f));

				LUtil.setPosition(btnNextElement, 0, container.getHeight() - buttonSize.y);
				LUtil.setBounds(elTray, 0, buttonSize.y, buttonSize.x, container.getHeight() - (buttonSize.y * 2));
			}
		}

		@Override
		public void constrain(Element child, Object constraints) {
		}

		@Override
		public void remove(Element child) {
		}

//		protected void resort() {
//			float zi = zStep / (childList.size() + 1);
//			float step = screen.getZOrderStepMinor();
//			if (sort == ZOrderSort.FIRST_TO_LAST) {
//				for (int i = 0; i < trayElements.size(); i++) {
//					Element el = trayElements.get(i);
//					el.setLocalTranslation(el.getLocalTranslation().setZ(step));
//					step += screen.getZOrderStepMinor();
//				}
//			} else if (sort == ZOrderSort.LAST_TO_FIRST) {
//				for (int i = trayElements.size() - 1; i >= 0; i--) {
//					Element el = trayElements.get(i);
//					if (el != front) {
//						el.setLocalTranslation(el.getLocalTranslation().setZ(step));
//						step += screen.getZOrderStepMinor();
//					}
//				}
//			}
//			if (front != null)
//				front.setLocalTranslation(front.getLocalTranslation().setZ(step));
//		}
	}
}
