/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icetone.controls.lists;

import java.util.Objects;

import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.buttons.ButtonAdapter;
import icetone.core.Container;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.Screen;
import icetone.core.layout.AbstractLayout;
import icetone.core.layout.LUtil;
import icetone.core.utils.UIDUtil;
import icetone.effects.Effect;

/**
 *
 * @author t0neg0d
 */
public class Slider<V extends Number> extends ButtonAdapter {

	//

	private Element elThumbLock;
	private ButtonAdapter elThumb;

	protected Orientation orientation;
	private MouseButtonEvent trackEvt;
	private Vector2f startPosition;
	private SliderModel<V> model;
	private boolean lockToStep = false;
	private Vector2f thumbStyleSize;
	private float thumbGap;
	private boolean reversed = false;

	public Slider() {
		this(Orientation.HORIZONTAL);
	}

	public Slider(Orientation orientation) {
		this(Screen.get(), orientation);
	}

	public Slider(ElementManager screen, Orientation orientation) {
		this(screen, orientation, false);
	}

	public Slider(ElementManager screen, Orientation orientation, boolean trackSurroundsThumb) {
		this(screen, LUtil.LAYOUT_SIZE, orientation, trackSurroundsThumb);
	}

	public Slider(ElementManager screen, Vector2f dimensions, Vector4f resizeBorders, String defaultImg,
			Orientation orientation, boolean trackSurroundsThumb) {
		this(screen, UIDUtil.getUID(), dimensions, resizeBorders, defaultImg, orientation, trackSurroundsThumb);
	}

	public Slider(ElementManager screen, String UID, Orientation orientation, boolean trackSurroundsThumb) {
		this(screen, UIDUtil.getUID(), LUtil.LAYOUT_SIZE,
				screen.getStyle(getStyleName(orientation)).getVector4f("resizeBorders"),
				screen.getStyle(getStyleName(orientation)).getString("defaultImg"), orientation, trackSurroundsThumb);
	}

	public Slider(ElementManager screen, String UID, String defaultImg, Orientation orientation,
			boolean trackSurroundsThumb) {
		this(screen, UID, LUtil.LAYOUT_SIZE, screen.getStyle(getStyleName(orientation)).getVector4f("resizeBorders"),
				screen.getStyle(getStyleName(orientation)).getString("defaultImg"), orientation, trackSurroundsThumb);
	}

	public Slider(ElementManager screen, String UID, Vector2f dimensions, Vector4f resizeBorders, String defaultImg,
			Orientation orientation, boolean trackSurroundsThumb) {
		this(screen, UID, Vector2f.ZERO, dimensions, resizeBorders, defaultImg, orientation, trackSurroundsThumb);
	}

	/**
	 * Creates a new instance of the Slider control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param orientation
	 *            Slider.Orientation used to establish Horizontal/Vertical
	 *            layout during control configuration
	 * @param trackSurroundsThumb
	 *            Boolean used to determine thumb placement when control is
	 *            configured
	 */
	public Slider(ElementManager screen, Vector2f position, Orientation orientation, boolean trackSurroundsThumb) {
		this(screen, UIDUtil.getUID(), position, LUtil.LAYOUT_SIZE,
				screen.getStyle(getStyleName(orientation)).getVector4f("resizeBorders"),
				screen.getStyle(getStyleName(orientation)).getString("defaultImg"), orientation, trackSurroundsThumb);
	}

	/**
	 * Creates a new instance of the Slider control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 * @param orientation
	 *            Slider.Orientation used to establish Horizontal/Vertical
	 *            layout during control configuration
	 * @param trackSurroundsThumb
	 *            Boolean used to determine thumb placement when control is
	 *            configured
	 */
	public Slider(ElementManager screen, Vector2f position, Vector2f dimensions, Orientation orientation,
			boolean trackSurroundsThumb) {
		this(screen, UIDUtil.getUID(), position, dimensions,
				screen.getStyle(getStyleName(orientation)).getVector4f("resizeBorders"),
				screen.getStyle(getStyleName(orientation)).getString("defaultImg"), orientation, trackSurroundsThumb);
	}

	/**
	 * Creates a new instance of the Slider control
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
	 * @param orientation
	 *            Slider.Orientation used to establish Horizontal/Vertical
	 *            layout during control configuration
	 * @param trackSurroundsThumb
	 *            Boolean used to determine thumb placement when control is
	 *            configured
	 */
	public Slider(ElementManager screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
			String defaultImg, Orientation orientation, boolean trackSurroundsThumb) {
		this(screen, UIDUtil.getUID(), position, dimensions, resizeBorders, defaultImg, orientation,
				trackSurroundsThumb);
	}

	/**
	 * Creates a new instance of the Slider control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param orientation
	 *            Slider.Orientation used to establish Horizontal/Vertical
	 *            layout during control configuration
	 * @param trackSurroundsThumb
	 *            Boolean used to determine thumb placement when control is
	 *            configured
	 */
	public Slider(ElementManager screen, String UID, Vector2f position, Orientation orientation,
			boolean trackSurroundsThumb) {
		this(screen, UID, position, LUtil.LAYOUT_SIZE,
				screen.getStyle(getStyleName(orientation)).getVector4f("resizeBorders"),
				screen.getStyle(getStyleName(orientation)).getString("defaultImg"), orientation, trackSurroundsThumb);
	}

	/**
	 * Creates a new instance of the Slider control
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
	 *            Slider.Orientation used to establish Horizontal/Vertical
	 *            layout during control configuration
	 * @param trackSurroundsThumb
	 *            Boolean used to determine thumb placement when control is
	 *            configured
	 */
	public Slider(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Orientation orientation,
			boolean trackSurroundsThumb) {
		this(screen, UID, position, dimensions, screen.getStyle(getStyleName(orientation)).getVector4f("resizeBorders"),
				screen.getStyle(getStyleName(orientation)).getString("defaultImg"), orientation, trackSurroundsThumb);
	}

	/**
	 * Creates a new instance of the Slider control
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
	 * @param orientation
	 *            Slider.Orientation used to establish Horizontal/Vertical
	 *            layout during control configuration
	 * @param trackSurroundsThumb
	 *            Boolean used to determine thumb placement when control is
	 *            configured
	 */
	@SuppressWarnings("unchecked")
	public Slider(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
			String defaultImg, Orientation orientation, @Deprecated boolean trackSurroundsThumb) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);

		setLayoutManager(new SliderLayout());
		setMinDimensions(Vector2f.ZERO);

		this.orientation = orientation;

		thumbStyleSize = screen.getStyle(getStyleName(orientation)).getVector2f("thumbSize");
		thumbGap = screen.getStyle(getStyleName(orientation)).getFloat("thumbGap");

		elThumbLock = new Container(screen, UID + ":Thumb") {

			// @Override
			public void moveTo(float x, float y) {

				float rat = getRatioForPosition(x, y);

				V oldVal = model.getValue();
				V val = model.getAsRatioOfRange(rat, lockToStep);

				if (lockToStep) {
					// The number of major steps from the start
					if (Slider.this.orientation == Slider.Orientation.HORIZONTAL) {
						x = valueToIndex(val.doubleValue()) + thumbGap;
					} else {
						y = valueToIndex(val.doubleValue());
						System.out.println("val to index of " + val.doubleValue() + " is " + y);
					}
				}

				if (!val.equals(oldVal)) {
					updateValue(val);
				}

				if (getLockToParentBounds()) {
					if (x < thumbGap) {
						x = thumbGap;
					}
					if (y < 0) {
						y = 0;
					}
					if (x > getElementParent().getWidth() - thumbGap) {
						x = getElementParent().getWidth() - thumbGap;
					}
					if (y > getElementParent().getHeight() - (thumbGap * 2)) {
						y = getElementParent().getHeight() - (thumbGap * 2);
					}
				}

				if (Slider.this.orientation == Slider.Orientation.HORIZONTAL) {
					setX(x);
				} else {
					LUtil.setY(this, y);
				}
			}
		};
		elThumbLock.setLockToParentBounds(true);
		addChild(elThumbLock);

		elThumb = new ButtonAdapter(screen, UID + ":Thumb", Vector2f.ZERO, LUtil.LAYOUT_SIZE,
				screen.getStyle(getStyleName(orientation)).getVector4f("thumbResizeBorders"),
				screen.getStyle(getStyleName(orientation)).getString("thumbImg")) {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				screen.setTabFocusElement(getElementParent());
			}
		};
		elThumb.setButtonHoverInfo(screen.getStyle(getStyleName(orientation)).getString("thumbHoverImg"), null);
		elThumb.setButtonPressedInfo(screen.getStyle(getStyleName(orientation)).getString("thumbPressedImg"), null);
		elThumbLock.addChild(elThumb);

		elThumb.setIsMovable(true);
		elThumb.setEffectParent(true);

		setTextPaddingByKey(getStyleName(orientation), "textPadding");

		removeEffect(Effect.EffectEvent.Hover);
		removeEffect(Effect.EffectEvent.Press);
		removeEffect(Effect.EffectEvent.LoseFocus);

		this.setInterval(100);

		setSliderModel((SliderModel<V>) new FloatRangeSliderModel(0, 100, 0));
	}

	public boolean isReversed() {
		return reversed;
	}

	public void setReversed(boolean reversed) {
		this.reversed = reversed;
		layoutChildren();
	}

	/**
	 * Set whether the slider should lock to whole steps. The slider will set a
	 * value and visually position itself to the next closest step (see
	 * {@link SliderModel#getStep()}.
	 * 
	 * @param lockToStep
	 */
	public void setLockToStep(boolean lockToStep) {
		this.lockToStep = lockToStep;
		// updateSliderPosition();
		layoutChildren();
	}

	/**
	 * Get whether the slider should lock to whole steps. The slider will set a
	 * value and visually position itself to the next closest step (see
	 * {@link SliderModel#getStep()}.
	 * 
	 * @return lockToStep
	 */
	public boolean isLockToStep() {
		return lockToStep;
	}

	/**
	 * Set the value of the slider and update it's position. No call to
	 * {@link #onChange(int, java.lang.Object) } will be made.
	 * 
	 * @param value
	 *            new value
	 */
	public void setSelectedValue(V value) {
		model.setValue(value);
		dirtyLayout(false);
		layoutChildren();
		// updateSliderPosition();
	}

	/**
	 * Set the value of the slider, update it's position and invoke
	 * {@link #onChange(int, java.lang.Object) }.
	 * 
	 * @param value
	 *            new value
	 */
	public void setSelectedValueWithCallback(V value) {
		setSelectedValue(value);
		onChange(model.getValue());
	}

	/**
	 * Set the slider model. The slider will be initialised with the current
	 * model. No call to {@link #onChange(java.lang.Object) } will be made.
	 * 
	 * @param model
	 *            slider model
	 */
	public void setSliderModel(SliderModel<V> model) {
		this.model = model;
		this.setInterval(1);
		setSelectedValue(model.getValue());
		layoutChildren();
	}

	/**
	 * Get the slider model. Do not set values on this, instead use
	 * {@link #setSelectedValue(double) } and
	 * {@link #setSelectedValueWithCallback(double) }
	 * 
	 * @return model
	 */
	public SliderModel<V> getSliderModel() {
		return model;
	}

	/**
	 * Convenience method to get the value.
	 * 
	 * @return value
	 */
	public V getSelectedValue() {
		return model.getValue();
	}

	/**
	 * Event called when the Slider's selectedIndex changes.
	 * 
	 * @param value
	 *            the new value of the slider, will be of the same type provided
	 *            by the model.
	 */
	public void onChange(V value) {

	}

	@Override
	public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
		trackEvt = evt;
		startPosition = elThumbLock.getPosition().clone();
		updateThumbByTrackClick();
	}

	@Override
	public void onButtonStillPressedInterval() {
		updateThumbByTrackClick();
		screen.updateToolTipLocation();
	}

	//

	public double stepPerIndex() {
		if (orientation == Orientation.HORIZONTAL) {
			return (getWidth() - (thumbGap * 2)) / getRange();
		} else {
			return getLength() / getRange();
		}
	}

	// Tab focus
	@Override
	public void setTabFocus() {
		screen.setKeyboardElement(this);
		Effect effect = elThumb.getEffect(Effect.EffectEvent.TabFocus);
		if (effect != null) {
			effect.setColor(ColorRGBA.DarkGray);
			screen.getEffectManager().applyEffect(effect);
		}
	}

	@Override
	public void resetTabFocus() {
		screen.setKeyboardElement(null);
		Effect effect = elThumb.getEffect(Effect.EffectEvent.LoseTabFocus);
		if (effect != null) {
			effect.setColor(ColorRGBA.White);
			screen.getEffectManager().applyEffect(effect);
		}
	}

	// Default keyboard interaction
	@Override
	public void onKeyPress(KeyInputEvent evt) {
		if (evt.getKeyCode() == KeyInput.KEY_LEFT) {
			trackEvt = new MouseButtonEvent(0, true, (int) this.getAbsoluteX(), (int) this.getAbsoluteY());
			updateThumbByTrackClick();
		} else if (evt.getKeyCode() == KeyInput.KEY_RIGHT) {
			trackEvt = new MouseButtonEvent(0, true, (int) this.getAbsoluteWidth() + 1,
					(int) this.getAbsoluteHeight() + 1);
			updateThumbByTrackClick();
		}
	}

	@Override
	public void onKeyRelease(KeyInputEvent evt) {

	}

	@Override
	public void setToolTipText(String tip) {
		this.elThumb.setToolTipText(tip);
	}

	@Override
	public String getToolTipText() {
		return this.elThumb.getToolTipText();
	}

	//
	
	protected static String getStyleName(Orientation orientation) {
		return orientation.equals(Orientation.HORIZONTAL) ? "HorizontalSlider" : "VerticalSlider";
	}

	protected int valueToIndex(double val) {
		final double stepPerIndex = stepPerIndex();
		int v = (int) ((val - model.getMin().doubleValue()) * stepPerIndex);
		if(reversed)
			v = (int)getLength() - v; 
		return v;
	}

	protected void updateSliderPosition() {
		final double val = model.getValue().doubleValue();
		final int indexOfValue = valueToIndex(val);
		if (orientation == Slider.Orientation.HORIZONTAL) {
			LUtil.setY(elThumbLock, 0);
			elThumbLock.setX(indexOfValue);
		} else {
			elThumbLock.setX(0);
			LUtil.setY(elThumbLock, indexOfValue);
		}
	}

	private void updateValue(V newValue) {
		if (!Objects.equals(model.getValue(), newValue)) {
			model.setValue(newValue);
			onChange(model.getValue());
		}
	}

	private double getRange() {
		return model.getMax().doubleValue() - model.getMin().doubleValue();
	}

	private float getRatioForPosition(float x, float y) {
		float val;
		if (orientation.equals(Orientation.HORIZONTAL)) {
			val = Math.min(1f, Math.max(0f, (x - thumbGap) / (getWidth() - (thumbGap * 2))));
		} else {
			val = Math.min(1f, Math.max(0f, y / getLength()));
		}
		if(reversed)
			val = 1f - val;
		return val;
	}

	private float getLength() {
		return getHeight() - (thumbGap * 2);
	}

	private void updateStep(boolean forward) {
		model.step(forward);
		// updateSliderPosition();
		layoutChildren();
		onChange(model.getValue());
	}

	private void updateThumbByTrackClick() {
		if (orientation == Orientation.HORIZONTAL) {
			if (elThumbLock.getX() > trackEvt.getX() - getAbsoluteX()
					&& startPosition.x > trackEvt.getX() - getAbsoluteX()) {
				updateStep(false);
			} else if (elThumbLock.getX() < trackEvt.getX() - getAbsoluteX()
					&& startPosition.x < trackEvt.getX() - getAbsoluteX()) {
				updateStep(true);
			}
		} else {
			if (LUtil.getY(elThumbLock) > trackEvt.getY() - LUtil.getAbsoluteY(this)
					&& startPosition.y > trackEvt.getY() - LUtil.getAbsoluteY(this)) {
				updateStep(false);
			} else if (LUtil.getY(elThumbLock) < trackEvt.getY() - LUtil.getAbsoluteY(this)
					&& startPosition.y < trackEvt.getY() - LUtil.getAbsoluteY(this)) {
				updateStep(true);
			}
		}
	}

	class SliderLayout extends AbstractLayout {

		public Vector2f minimumSize(Element parent) {
			Vector2f min = new Vector2f();
			if (orientation == Slider.Orientation.VERTICAL) {
				min.x = thumbStyleSize.x;
				min.y = min.x * 3;
			} else {
				min.y = thumbStyleSize.y;
				min.x = min.y * 3;
			}
			min.x += textPadding.x + textPadding.y;
			min.y += textPadding.z + textPadding.w;
			return min;
		}

		public Vector2f maximumSize(Element parent) {
			return null;
		}

		public Vector2f preferredSize(Element parent) {
			Vector2f ps = new Vector2f();
			if (orientation == Slider.Orientation.VERTICAL) {
				ps.x = thumbStyleSize.x;
				ps.y = (int) ((getSliderModel().getMax().doubleValue() - getSliderModel().getMin().doubleValue())
						/ getSliderModel().getStep().doubleValue());
			} else {
				ps.x = (int) ((getSliderModel().getMax().doubleValue() - getSliderModel().getMin().doubleValue())
						/ getSliderModel().getStep().doubleValue());
				ps.y = thumbStyleSize.y;
			}
			ps.x += textPadding.x + textPadding.y;
			ps.y += textPadding.z + textPadding.w;
			return ps;
		}

		public void layout(Element childElement) {
			float controlWidth = childElement.getWidth();
			float controlHeight = childElement.getHeight();
			V val = model.getValue();
			float i = valueToIndex(val.doubleValue());
			if (orientation == Slider.Orientation.HORIZONTAL) {
				LUtil.setBounds(elThumbLock, i + thumbGap, textPadding.z, 1,
						controlHeight - textPadding.w - textPadding.z);
				LUtil.setBounds(elThumb, -(thumbStyleSize.x / 2), 0, thumbStyleSize.x,
						controlHeight - textPadding.w - textPadding.z);
			} else {
				LUtil.setBounds(elThumbLock, textPadding.x, i, controlWidth - textPadding.x - textPadding.y, 1);
				LUtil.setBounds(elThumb, 0, thumbStyleSize.y / 2, controlWidth - textPadding.x - textPadding.y,
						thumbStyleSize.y);
			}
		}

		public void remove(Element child) {
		}

		public void constrain(Element child, Object constraints) {
		}
	}
}
