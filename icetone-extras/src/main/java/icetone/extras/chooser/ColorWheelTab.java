package icetone.extras.chooser;

import java.util.Objects;

import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.core.AbstractGenericLayout;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Element;
import icetone.core.Layout.LayoutType;
import icetone.core.layout.Border;
import icetone.core.layout.BorderLayout;
import icetone.extras.util.ExtrasUtil;

public abstract class ColorWheelTab extends Element implements ColorTabPanel {

	private static final String CELL = "cell";

	public final static float MAX_BRIGHTNESS_BAR = 13.0f;

	private ColorRGBA color;
	private ColorRestrictionType restrictionType = ColorRestrictionType.getDefaultType();

	private HuePicker huePicker;
	private BrightnessPicker barPalette;
	private float mHue;
	private float mSaturation;
	private float mBrightness;
	private Vector2f cellSize;

	public ColorWheelTab(BaseScreen screen, ColorRestrictionType restrictionType) {
		super(screen);

		setLayoutManager(new BorderLayout(8, 8));
		this.restrictionType = restrictionType;
		addComponents();
		rebuild();
	}

	protected void addComponents() {
		ColorCell cell = new ColorCell(screen, ColorRGBA.White);
		cellSize = cell.calcPreferredSize();

		huePicker = new HuePicker(screen, restrictionType);
		addElement(huePicker, Border.CENTER);
		barPalette = new BrightnessPicker(screen, restrictionType);
		addElement(barPalette, Border.SOUTH);
	}

	public ColorRestrictionType getRestrictionType() {
		return restrictionType;
	}

	public void setRestrictionType(ColorRestrictionType restrictionType) {
		if (!Objects.equals(restrictionType, this.restrictionType)) {
			this.restrictionType = restrictionType;
			invalidate();
			removeAllChildren();
			addComponents();
			validate();
			rebuild();
		}
	}

	public void setColor(ColorRGBA color) {
		this.color = color == null ? null : color.clone();
		rebuild();
	}

	private void rebuild() {
		dirtyLayout(false, LayoutType.boundsChange());
		layoutChildren();
		updateHuePalette();
		findClosest(color == null ? ColorRGBA.White : color);
		updateBrightnessPalette();
	}

	private float[] getPolar(float vX, float vY) {
		return new float[] { FastMath.sqrt(vX * vX + vY * vY), FastMath.atan2(vY, vX) };
	}

	private void updateBrightnessPalette() {
		float startingInitialBrightness = restrictionType.getValueBar();
		for (BaseElement el : barPalette.getElements()) {
			if (el instanceof ColorCell) {
				ColorRGBA rgba = ExtrasUtil.toRGBA(mHue, mSaturation,
						startingInitialBrightness / (MAX_BRIGHTNESS_BAR - 1.0f));
				rgba.clamp();
				el.setDefaultColor(rgba);
				startingInitialBrightness++;
			}
		}
	}

	private ColorCell findClosest(ColorRGBA color) {

		float[] currenthsb = ExtrasUtil.toHSB(color);

		ColorCell closesthue = null;
		float closesthuedist = 0;
		ColorCell closestbrightness = null;
		float closestbrightnessdist = 1000;

		Vector2f cellSize = new ColorCell(screen, ColorRGBA.Black).calcPreferredSize();

		for (BaseElement el : huePicker.getElements()) {
			if (el instanceof ColorCell) {
				ColorCell splotch = (ColorCell) el;

				float[] splotchHSB = ExtrasUtil.toHSB(splotch.getDefaultColor());

				float hue = splotchHSB[0];
				float sat = splotchHSB[1];
				float huedist = hue - currenthsb[0];
				float satdist = sat - currenthsb[1];
				huedist = (float) Math.sqrt(huedist * huedist + satdist * satdist);

				if (closesthue == null) {
					closesthue = splotch;
					closesthuedist = huedist;
				} else if (currenthsb[0] == 0 && currenthsb[1] == 0 && hue == 0 && sat == 0) {
					closesthue = splotch;
					break;
				} else if (huedist <= closesthuedist) {
					closesthue = splotch;
					closesthuedist = huedist;
				}
			}
		}

		float startingInitialBrightness = restrictionType.getValueBar();

		for (BaseElement el : barPalette.getElements()) {
			if (el instanceof ColorCell) {
				ColorCell splotch = (ColorCell) el;
				float bright = (float) (startingInitialBrightness / (MAX_BRIGHTNESS_BAR - 1.0));

				if (FastMath.abs(bright - currenthsb[2]) <= closestbrightnessdist) {
					closestbrightness = splotch;
					closestbrightnessdist = FastMath.abs(bright - currenthsb[2]);
				}

				startingInitialBrightness++;
			}
		}

		huePicker.setSelected(closesthue);

		float[] polar = getCellPolar(closesthue);
		mHue = polar[1] / (FastMath.PI * 2f);
		mSaturation = polar[0] / (cellSize.x * 5.5f);

		updateBrightnessPalette();
		barPalette.setSelected(closestbrightness);

		float[] dat = ExtrasUtil.toHSB(closestbrightness.getDefaultColor());
		mBrightness = dat[2];
		updateHuePalette();

		return closestbrightness;
	}

	private void updateHuePalette() {
		for (BaseElement el : huePicker.getElements()) {
			if (el instanceof ColorCell) {
				ColorCell splotch = (ColorCell) el;
				float[] polar = getCellPolar(splotch);
				ColorRGBA rgba = ExtrasUtil.toRGBA(polar[1] / (FastMath.PI * 2f), polar[0] / (cellSize.x * 5.5f),
						mBrightness);
				rgba.clamp();
				splotch.setDefaultColor(rgba);
			}
		}
	}

	protected float[] getCellPolar(ColorCell splotch) {
		float cx = 6 * cellSize.x - 6 * (cellSize.x / 2);
		float cy = 6 * getIndent();
		Vector2f offset = getOffset();
		Vector2f pos = splotch.getUserData(CELL);
		return getPolar(pos.x - cx - offset.x, pos.y - cy - offset.y);
	}

	public Vector2f getOffset() {
		int wheelLevel = restrictionType.getWheelLevel();
		float startY = wheelLevel;
		float startX = wheelLevel;
		Vector2f off = new Vector2f(Short.MAX_VALUE, Short.MAX_VALUE);
		float fx = startX * cellSize.x + ((6 - startY) * (cellSize.x / 2f));
		float fy = startY * ColorWheelTab.this.getIndent();
		float tx = (int) (fx);
		float ty = (int) (fy);
		off.x = Math.min(tx, off.x);
		off.y = Math.min(ty, off.y);
		if (off.x == Short.MAX_VALUE)
			off.x = 0;
		if (off.y == Short.MAX_VALUE)
			off.y = 0;
		return off;
	}

	class AbstractPalette extends Element {

		protected ColorRestrictionType restrictionType;
		protected ColorCell selected;
		protected Element highlight;

		AbstractPalette(BaseScreen screen, ColorRestrictionType restrictionType) {
			super(screen);
			this.restrictionType = restrictionType;
			highlight = new Element(screen) {
				{
					setStyleClass("cell-selected");
				}
			};
			addElement(highlight);
		}

		public void setSelected(ColorCell cell) {
			if (!Objects.equals(cell, selected)) {
				selected = cell;
				dirtyLayout(false);
				layoutChildren();
			}
		}
	}

	class HueLayout extends AbstractGenericLayout<HuePicker, Vector2f> {

		@Override
		protected Vector2f calcMinimumSize(HuePicker container) {
			int wheelLevel = restrictionType.getWheelLevel();
			float startY = wheelLevel;
			int endY = 13 - wheelLevel;
			return new Vector2f((13 - wheelLevel) * cellSize.x, (endY - startY) * ColorWheelTab.this.getIndent());

		}

		@Override
		protected Vector2f calcPreferredSize(HuePicker container) {
			return calcMinimumSize(container);
		}

		@Override
		protected void onLayout(HuePicker container) {
			Vector2f sz = calcMinimumSize(container);
			for (BaseElement e : container.getElements()) {
				if (e instanceof ColorCell) {
					ColorCell cell = (ColorCell) e;
					Vector2f c = cell.getUserData(CELL);
					if (Objects.equals(cell, container.selected)) {
						container.highlight.setBounds((int) (((container.getWidth() - sz.x) / 2f) + c.x),
								(int) (((container.getHeight() - sz.y) / 2f) + c.y), cellSize.x, cellSize.y);
					}
					cell.setBounds((int) (((container.getWidth() - sz.x) / 2f) + c.x),
							(int) (((container.getHeight() - sz.y) / 2f) + c.y), cellSize.x, cellSize.y);
				}
			}

			updateHuePalette();
		}
	}

	class HuePicker extends AbstractPalette {

		HuePicker(BaseScreen screen, ColorRestrictionType restrictionType) {
			super(screen, restrictionType);
			setLayoutManager(new HueLayout());
			int wheelLevel = restrictionType.getWheelLevel();
			float startY = wheelLevel;
			int endY = 13 - wheelLevel;
			int midY = (int) endY / 2;
			float startX = wheelLevel;
			for (float y = startY; y < endY; y++) {
				int m = midY + 1 + (int) y - wheelLevel;
				if (y > midY) {
					m = endY - ((int) y - midY);
				}
				for (float x = startX; x < m; x++) {
					ColorCell cell = new ColorCell(screen, ColorRGBA.White);
					float fx = 0;
					if (y < 6) {
						fx = x * cellSize.x + ((6 - y) * (cellSize.x / 2f));
					} else {
						fx = x * cellSize.x + ((y - 6) * (cellSize.x / 2));
					}
					cell.setUserData(CELL, new Vector2f(fx, y * ColorWheelTab.this.getIndent()));
					cell.onMouseReleased(evt -> {
						setSelected((ColorCell) evt.getElement());
						float[] colpolar = getCellPolar(cell);
						mHue = colpolar[1] / (FastMath.PI * 2f);
						mSaturation = colpolar[0] / (cellSize.x * 5.5f);
						updateBrightnessPalette();
						onChange(cell.getDefaultColor());
					});
					addElement(cell);
				}
			}
		}
	}

	class BrightnessLayout extends AbstractGenericLayout<BrightnessPicker, Object> {

		@Override
		protected Vector2f calcMinimumSize(BrightnessPicker container) {
			return new Vector2f(cellSize.x * (container.getElements().size() - 1), cellSize.y)
					.addLocal(container.getTotalPadding());
		}

		@Override
		protected Vector2f calcPreferredSize(BrightnessPicker container) {
			return calcMinimumSize(container);
		}

		@Override
		protected void onLayout(BrightnessPicker container) {
			float start = 0;
			float end = MAX_BRIGHTNESS_BAR;
			start = start + restrictionType.getValueBar();
			end = end - restrictionType.getValueBar();
			int tw = (int) ((container.getElements().size() - 1) * cellSize.x);
			Vector4f b = new Vector4f();
			for (float x = start; x < end; x++) {
				BaseElement el = container.getElements().get(1 + (int) (x - start));
				b.set(((container.getWidth() - tw) / 2f) + ((x - start) * cellSize.x),
						(container.getHeight() - cellSize.y) / 2f, cellSize.x, cellSize.y);
				if (Objects.equals(el, container.selected)) {
					container.highlight.setBounds(b);
				}
				el.setBounds(b);
			}
		}

	}

	class BrightnessPicker extends AbstractPalette {

		BrightnessPicker(BaseScreen screen, ColorRestrictionType restrictionType) {
			super(screen, restrictionType);
			setLayoutManager(new BrightnessLayout());
			for (int i = 0; i < MAX_BRIGHTNESS_BAR - (restrictionType.getValueBar() * 2); i++) {
				ColorCell cell = new ColorCell(screen, ColorRGBA.White);
				cell.onMousePressed(evt -> {
					setSelected(cell);
					mBrightness = ExtrasUtil.toHSB(cell.getDefaultColor())[2];
					updateHuePalette();
					updateBrightnessPalette();
					onChange(cell.getDefaultColor());
				});
				addElement(cell);
			}

		}
	}
}
