package icetone.framework.core;

import com.jme3.collision.CollisionResult;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.FastMath;
import com.jme3.scene.Spatial;

import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Layout.LayoutType;
import icetone.core.UIEventTarget;
import icetone.core.event.mouse.HoverEvent;
import icetone.core.event.mouse.HoverEvent.HoverEventType;
import icetone.core.event.mouse.HoverListener;
import icetone.core.event.mouse.MouseButtonListener;
import icetone.core.event.mouse.MouseUIButtonEvent;

public class AnimScreen extends BaseScreen {
	private AnimManager animManager;
	private AnimElement eventAnimElement = null;
	private float eventAnimOffsetX = 0;
	private float eventAnimOffsetY = 0;
	private AnimQuadData eventQuad = null;
	private float eventQuadOffsetX = 0;
	private float eventQuadOffsetY = 0;
	private AnimElement previousMouseFocusAnimElement = null;
	private AnimElement mouseFocusAnimElement = null;

	{
		animManager = new AnimManager(this);
	}

	public AnimScreen() {
		super();
	}

	public AnimScreen(float width, float height) {
		super(width, height);
	}

	public void removeAnimLayer(AnimLayer animLayer) {
		t0neg0dGUI.removeControl(animLayer);

		dirtyLayout(false, LayoutType.zorder);
		layoutChildren();
		// applyZOrder();

		animLayer.removeFromParent();
		animLayer.cleanup();
	}

	@Override
	public void setSpatial(Spatial spatial) {
		super.setSpatial(spatial);
		if (spatial != null) {
			t0neg0dGUI.addControl(animManager);
		}
	}

	public AnimLayer addAnimLayer() {
		AnimLayer layer = new AnimLayer(this);
		if (!layer.isInitialized()) {
			layer.setInitialized(this);
		}
		t0neg0dGUI.attachChild(layer);
		t0neg0dGUI.addControl(layer);

		return layer;
	}

	public AnimManager getAnimManager() {
		return this.animManager;
	}

	public AnimElement getEventAnimElement() {
		return this.eventAnimElement;
	}

	public float getEventAnimOffsetX() {
		return this.eventAnimOffsetX;
	}

	public float getEventAnimOffsetY() {
		return this.eventAnimOffsetY;
	}

	public AnimQuadData getEventQuad() {
		return this.eventQuad;
	}

	public float getEventQuadOffsetX() {
		return this.eventQuadOffsetX;
	}

	public float getEventQuadOffsetY() {
		return this.eventQuadOffsetY;
	}

	@Override
	public void onMouseMotionEvent(MouseMotionEvent evt) {
		super.onMouseMotionEvent(evt);

		// 2D Framework
		if (getMouseFocusElement() == null) {
			if (!mousePressed) {
				if (previousMouseFocusAnimElement != null) {
					if (previousMouseFocusAnimElement instanceof HoverListener) {
						((HoverListener) previousMouseFocusAnimElement).onFocusChange(
								new HoverEvent(evt, null, null, keyboardModifiers, HoverEventType.leave));
						previousMouseFocusAnimElement = null;
					}
				}
				// getAnimEventTargets(evt.getX(), evt.getY());
				if (eventAnimElement != null) {
					mouseFocusAnimElement = eventAnimElement;
					if (eventAnimElement instanceof HoverListener) {
						((HoverListener) mouseFocusAnimElement).onFocusChange(
								new HoverEvent(evt, null, null, keyboardModifiers, HoverEventType.enter));
					}
					previousMouseFocusAnimElement = mouseFocusAnimElement;
				}
			} else {
				if (eventAnimElement != null) {
					if (eventAnimElement.getIsMovable()) {

					} else if (eventQuad.getIsMovable()) {
						eventQuad.setPosition(getMouseXY().x - eventQuadOffsetX, getMouseXY().y - eventQuadOffsetY);
					}
				}
			}
		}
	}

	protected void androidTouchUpEvent(TouchEvent evt) {
		// handleAndroidMenuState(target);
		if (eventAnimElement != null) {
			if (eventAnimElement instanceof MouseButtonListener) {
				eventAnimElement = eventCaster.fireMouseButtonEvent(eventAnimElement,
						new MouseUIButtonEvent<AnimElement>(
								new MouseButtonEvent(0, true, (int) getTouchXY().x, (int) getTouchXY().y),
								getClickCount(), keyboardModifiers))
						.getTargetElement();

			}
			evt.setConsumed();
		}

		if (evt.isConsumed())
			mousePressed = false;
		else
			super.androidTouchUpEvent(evt);

	}

	protected void s3dOnMouseMotionEvent(MouseMotionEvent evt, boolean guiFocus) {
		super.s3dOnMouseMotionEvent(evt, guiFocus || mouseFocusAnimElement != null);
	}

	protected void androidTouchDownEvent(TouchEvent evt) {
		mousePressed = true;

		// 2D Framework
		if (eventElement == null) {
			if (eventAnimElement != null) {
				setAnimElementZOrder();
				eventAnimElement = eventCaster.fireMouseButtonEvent(eventAnimElement,
						new MouseUIButtonEvent<AnimElement>(
								new MouseButtonEvent(0, true, (int) getTouchXY().x, (int) getTouchXY().y),
								getClickCount(), keyboardModifiers))
						.getTargetElement();
				evt.setConsumed();
			}
		}

		if (!evt.isConsumed()) {
			super.androidTouchDownEvent(evt);
		}
	}

	@Override
	protected void onMouseLeftPressed(MouseButtonEvent evt, EventCheckType check) {
		super.onMouseLeftPressed(evt, check);

		// 2D Framework
		if (eventElement == null) {
			if (eventAnimElement != null) {
				setAnimElementZOrder();
				MouseUIButtonEvent<UIEventTarget> mevt = new MouseUIButtonEvent<UIEventTarget>(evt, eventAnimElement,
						keyboardModifiers);

				if (mouseButtonSupport != null)
					mouseButtonSupport.fireEvent(mevt);

				eventAnimElement = (AnimElement) eventCaster.fireMouseButtonEvent(eventAnimElement, mevt)
						.getTargetElement();
				evt.setConsumed();
			}
		}
	}

	protected void onMouseLeftReleased(MouseButtonEvent evt) {
		super.onMouseLeftReleased(evt);
		if (eventElement == null || !eventElement.isEnabled()) {
			if (eventAnimElement != null) {
				MouseUIButtonEvent<UIEventTarget> mevt = new MouseUIButtonEvent<UIEventTarget>(evt, eventAnimElement,
						keyboardModifiers);

				if (mouseButtonSupport != null)
					mouseButtonSupport.fireEvent(mevt);

				if (!mevt.isConsumed()) {
					eventAnimElement = (AnimElement) eventCaster.fireMouseButtonEvent(eventAnimElement, mevt)
							.getTargetElement();
					mevt.setConsumed();
					evt.setConsumed();
				}
			}
		}
	}

	protected void onMouseWheelPressed(MouseButtonEvent evt) {
		if (eventElement == null || !eventElement.isEnabled()) {
			// 2D Framework
			if (eventAnimElement != null) {
				MouseUIButtonEvent<UIEventTarget> mevt = new MouseUIButtonEvent<UIEventTarget>(evt, eventAnimElement,
						keyboardModifiers);

				if (mouseButtonSupport != null)
					mouseButtonSupport.fireEvent(mevt);

				if (!mevt.isConsumed()) {
					setAnimElementZOrder();
					eventAnimElement = (AnimElement) eventCaster.fireMouseButtonEvent(eventAnimElement, mevt)
							.getTargetElement();
					evt.setConsumed();
				}
			}
		} else
			super.onMouseWheelPressed(evt);
	}

	protected void onMouseWheelReleased(MouseButtonEvent evt) {
		if (eventElement == null || !eventElement.isEnabled()) {
			if (eventAnimElement != null) {
				MouseUIButtonEvent<UIEventTarget> mevt = new MouseUIButtonEvent<UIEventTarget>(evt, eventAnimElement,
						keyboardModifiers);

				if (mouseButtonSupport != null)
					mouseButtonSupport.fireEvent(mevt);

				if (!mevt.isConsumed()) {
					eventAnimElement = (AnimElement) eventCaster.fireMouseButtonEvent(eventAnimElement, mevt)
							.getTargetElement();
					evt.setConsumed();
				}
			}
		} else
			super.onMouseWheelReleased(evt);
	}

	protected void onMouseRightPressed(MouseButtonEvent evt) {
		if (eventElement == null || !eventElement.isEnabled()) {
			// 2D Framework
			if (eventAnimElement != null) {
				MouseUIButtonEvent<UIEventTarget> mevt = new MouseUIButtonEvent<UIEventTarget>(evt, eventAnimElement,
						keyboardModifiers);

				if (mouseButtonSupport != null) {
					mouseButtonSupport.fireEvent(mevt);
				}

				if (!mevt.isConsumed()) {
					setAnimElementZOrder();
					eventAnimElement = (AnimElement) eventCaster.fireMouseButtonEvent(eventAnimElement, mevt)
							.getTargetElement();
					evt.setConsumed();
				}
			}
		} else
			super.onMouseRightPressed(evt);
	}

	protected void onMouseRightReleased(MouseButtonEvent evt) {
		if (eventElement == null || !eventElement.isEnabled()) {
			if (eventAnimElement != null) {
				MouseUIButtonEvent<UIEventTarget> mevt = new MouseUIButtonEvent<UIEventTarget>(evt, eventAnimElement,
						keyboardModifiers);

				if (mouseButtonSupport != null)
					mouseButtonSupport.fireEvent(mevt);

				if (!mevt.isConsumed()) {
					eventAnimElement = (AnimElement) eventCaster.fireMouseButtonEvent(eventAnimElement, mevt)
							.getTargetElement();
					evt.setConsumed();
				}
			}
		} else
			super.onMouseRightReleased(evt);
	}

	protected BaseElement getEventElement(float x, float y, EventCheckType check) {

		BaseElement el = super.getEventElement(x, y, check);
		if (el == null) {
			// 2D Framework
			eventAnimElement = null;
			eventQuad = null;
			for (CollisionResult result : results) {
				boolean discard = false;
				if (result.getGeometry().getParent() instanceof AnimElement) {
					AnimElement testAnimEl = (AnimElement) result.getGeometry().getParent();
					if (testAnimEl.getClippingPosition() != null
							&& (result.getContactPoint().getX() < testAnimEl.getClippingPosition().getX()
									|| result.getContactPoint().getX() > testAnimEl.getClippingPosition().getZ()
									|| result.getContactPoint().getY() < testAnimEl.getClippingPosition().getY()
									|| result.getContactPoint().getY() > testAnimEl.getClippingPosition().getW())) {
						discard = true;
					}
					if (!discard) {
						eventAnimElement = (AnimElement) result.getGeometry().getParent();
						if (!eventAnimElement.getIgnoreMouse()) {
							eventAnimOffsetX = x - eventAnimElement.getPositionX();
							eventAnimOffsetY = y - eventAnimElement.getPositionY();
							try {
								eventQuad = (AnimQuadData) eventAnimElement
										.getQuad((int) FastMath.floor(result.getTriangleIndex() / 2));
								eventQuadOffsetX = x - eventQuad.getPositionX();
								eventQuadOffsetY = y - eventQuad.getPositionY();
							} catch (Exception e) {
								e.printStackTrace();
								eventAnimElement = null;
								eventQuad = null;
							}
							break;
						} else {
							eventAnimElement = null;
							eventQuad = null;
						}
					}
				}
			}
		}
		return el;
	}

	private void setAnimElementZOrder() {
		if (eventAnimElement != null) {
			if (eventAnimElement.getZOrderEffect() == AnimElement.ZOrderEffect.Self
					|| eventAnimElement.getZOrderEffect() == AnimElement.ZOrderEffect.Both)
				if (eventAnimElement.getParentLayer() != null)
					eventAnimElement.getParentLayer().bringAnimElementToFront(eventAnimElement);
			if (eventAnimElement.getZOrderEffect() == AnimElement.ZOrderEffect.Child
					|| eventAnimElement.getZOrderEffect() == AnimElement.ZOrderEffect.Both)
				eventAnimElement.bringQuadToFront(eventQuad);
		}
	}
}
