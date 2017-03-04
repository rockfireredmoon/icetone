package icetone.extras.chooser;

import java.util.List;

import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;

import icetone.core.AbstractGenericLayout;
import icetone.core.BaseElement;
import icetone.core.ElementManager;
import icetone.core.Layout.LayoutType;
import icetone.core.Element;
import icetone.core.layout.Border;
import icetone.core.layout.BorderLayout;
import icetone.core.layout.FixedLayout;
import icetone.extras.util.ExtrasUtil;

public abstract class ColorPaletteTab extends Element implements ColorSelector.ColorTabPanel {
	public final static float MAX_BRIGHTNESS_BAR = 13.0f;

	public enum ColorRestrictionType {
		DEVELOPMENT, REFASHION, CHARACTER_CREATION;

		public int getWheelLevel() {
			switch (this) {
			case DEVELOPMENT:
				return 0;
			default:
				return 1;
			}
		}

		public int getValueBar() {
			switch (this) {
			case DEVELOPMENT:
				return 0;
			case REFASHION:
				return 1;
			default:
				return 3;
			}
		}
	}

	private List<ColorRGBA> palette;
	private ColorRGBA color;
	private ColorRestrictionType restrictionType = ColorRestrictionType.DEVELOPMENT;

	private HuePicker huePicker;
	private BrightnessPicker barPalette;
	private float mHue;
	private float mSaturation;
	private float mBrightness;
	private Vector2f cellSize;
	private Element selectedCell;

	public ColorPaletteTab(ElementManager<?> screen) {
		super(screen);

		setLayoutManager(new BorderLayout(8, 8));

		ColorCell cell = new ColorCell(screen, ColorRGBA.White);
		cellSize = cell.calcPreferredSize();

		selectedCell = new Element(screen);
		selectedCell.setStyleClass("cell-selected");

		huePicker = new HuePicker(screen, restrictionType);
		addElement(huePicker, Border.CENTER);
		barPalette = new BrightnessPicker(screen, restrictionType);
		addElement(barPalette, Border.SOUTH);
		rebuild();
	}

	public ColorRestrictionType getRestrictionType() {
		return restrictionType;
	}

	public void setRestrictionType(ColorRestrictionType restrictionType) {
		this.restrictionType = restrictionType;
		rebuild();
	}

	public void setPalette(List<ColorRGBA> palette) {
		this.palette = palette;
		rebuild();
	}

	public void setColor(ColorRGBA color) {
		this.color = color == null ? null : color.clone();
		rebuild();
	}

	private void rebuild() {
		dirtyLayout(false, LayoutType.boundsChange());
		layoutChildren();
		findClosest(color == null ? ColorRGBA.Green : color);
	}

	private float[] getPolar(float vX, float vY) {
		return new float[] { FastMath.sqrt(vX * vX + vY * vY), FastMath.atan2(vY, vX) };
	}

	private void updateBrightnessPalette() {
		float startingInitialBrightness = restrictionType.getValueBar();
		for (BaseElement el : barPalette.getElements()) {
			ColorRGBA rgba = ExtrasUtil.toRGBA(mHue, mSaturation, startingInitialBrightness / (MAX_BRIGHTNESS_BAR - 1.0f));
			rgba.clamp();
			el.setDefaultColor(rgba);
			startingInitialBrightness++;
		}
	}

	private ColorCell findClosest(ColorRGBA color) {
		float[] currenthsb = ExtrasUtil.toHSB(color);

		ColorCell closesthue = null;
		float closesthuedist = 0;
		ColorCell closestbrightness = null;
		float closestbrightnessdist = 1000;

		Vector2f cellSize = new ColorCell(screen, ColorRGBA.Black).calcPreferredSize();
		float cx = 6 * cellSize.x - 6 * (cellSize.x / 2);
		float cy = 6 * getIndent();

		for (BaseElement el : huePicker.getElements()) {
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

		float startingInitialBrightness = restrictionType.getValueBar();

		for (BaseElement el : barPalette.getElements()) {
			ColorCell splotch = (ColorCell) el;
			float bright = (float) (startingInitialBrightness / (MAX_BRIGHTNESS_BAR - 1.0));

			if (FastMath.abs(bright - currenthsb[2]) <= closestbrightnessdist) {
				closestbrightness = splotch;
				closestbrightnessdist = FastMath.abs(bright - currenthsb[2]);
			}

			startingInitialBrightness++;
		}

		huePicker.setSelected(closesthue);

		cx = 6 * cellSize.x - 6 * (cellSize.x / 2);
		cy = 6 * getIndent();

		Vector2f pos = closesthue.getPosition();
		Vector2f offset = ((HueLayout) huePicker.getLayoutManager()).getOffset(huePicker);
		float[] polar = getPolar(pos.x - cx - offset.x, pos.y - cy - offset.y);
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
		Vector2f offset = ((HueLayout) huePicker.getLayoutManager()).getOffset(huePicker);
		for (BaseElement el : huePicker.getElements()) {
			ColorCell splotch = (ColorCell) el;
			Vector2f cellSize = splotch.getDimensions();
			float cx = 6 * cellSize.x - 6 * (cellSize.x / 2);
			float cy = 6 * getIndent();
			Vector2f pos = splotch.getPosition();
			float[] polar = getPolar(pos.x - cx - offset.x, pos.y - cy - offset.y);
			ColorRGBA rgba = ExtrasUtil.toRGBA(polar[1] / (FastMath.PI * 2f), polar[0] / (cellSize.x * 5.5f), mBrightness);
			rgba.clamp();
			splotch.setDefaultColor(rgba);
		}

	}

	class AbstractPalette extends Element {

		protected ColorCell selected = null;
		protected ColorRestrictionType restrictionType;

		AbstractPalette(ElementManager<?> screen, ColorRestrictionType restrictionType) {
			super(screen);
			this.restrictionType = restrictionType;
			setLayoutManager(new FixedLayout());
		}

		protected void setSelected(ColorCell cell) {
			if (selected != null) {
				selected.removeAllChildren();
			}
			if (cell != null) {
				BaseElement el = new BaseElement(screen);
				el.setTexture(selectedCell.getElementTexture());
				el.setTextPadding(selectedCell.getTextPadding());
				el.setMargin(selectedCell.getMargin());
				el.setResizeBorders(selectedCell.getResizeBorders());
				selected = cell;
				selected.addElement(el);
			}
		}
	}

	class HueLayout extends AbstractGenericLayout<HuePicker, Object> {

		@Override
		protected Vector2f calcMinimumSize(HuePicker container) {
			int wheelLevel = restrictionType.getWheelLevel();
			float startY = wheelLevel;
			int endY = 13 - wheelLevel;
			return new Vector2f((13 - wheelLevel) * cellSize.x, (endY - startY) * ColorPaletteTab.this.getIndent());

		}

		public Vector2f getOffset(HuePicker container) {
			int wheelLevel = restrictionType.getWheelLevel();
			float startY = wheelLevel;
			float startX = wheelLevel;
			Vector2f sz = calcMinimumSize(container);
			Vector2f off = new Vector2f(Short.MAX_VALUE, Short.MAX_VALUE);
			float fx = startX * cellSize.x + ((6 - startY) * (cellSize.x / 2f));
			float fy = startY * ColorPaletteTab.this.getIndent();
			float tx = (int) (((container.getWidth() - sz.x) / 2f) + fx);
			float ty = (int) (((container.getHeight() - sz.y) / 2f) + fy);
			off.x = Math.min(tx, off.x);
			off.y = Math.min(ty, off.y);
			if (off.x == Short.MAX_VALUE)
				off.x = 0;
			if (off.y == Short.MAX_VALUE)
				off.y = 0;
			return off;
		}

		@Override
		protected Vector2f calcPreferredSize(HuePicker container) {
			return calcMinimumSize(container);
		}

		@Override
		protected void onLayout(HuePicker container) {
			int wheelLevel = restrictionType.getWheelLevel();
			float startY = wheelLevel;
			int endY = 13 - wheelLevel;
			int midY = (int) endY / 2;
			float startX = wheelLevel;
			int cellIdx = 0;
			Vector2f sz = calcMinimumSize(container);
			for (float y = startY; y < endY; y++) {
				int m = midY + 1 + (int) y - wheelLevel;
				if (y > midY) {
					m = endY - ((int) y - midY);
				}
				for (float x = startX; x < m; x++) {
					ColorCell cell = (ColorCell) container.getElements().get(cellIdx++);
					float fx = 0;
					if (y < 6) {
						fx = x * cellSize.x + ((6 - y) * (cellSize.x / 2f));
					} else {
						fx = x * cellSize.x + ((y - 6) * (cellSize.x / 2));
					}
					float fy = y * ColorPaletteTab.this.getIndent();
					cell.setBounds((int) (((container.getWidth() - sz.x) / 2f) + fx),
							(int) (((container.getHeight() - sz.y) / 2f) + fy), cellSize.x, cellSize.y);
				}
			}
			updateHuePalette();
		}
	}

	class HuePicker extends AbstractPalette {
		HuePicker(ElementManager<?> screen, ColorRestrictionType restrictionType) {
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
					cell.onMouseReleased(evt -> {
						Vector2f offset = ((HueLayout) getLayoutManager()).getOffset(huePicker);
						huePicker.setSelected(cell);
						float cx = 6 * cellSize.x - 6 * (cellSize.x / 2);
						float cy = 6 * ColorPaletteTab.this.getIndent();
						Vector2f pos = cell.getPosition();
						float[] polar = getPolar(pos.x - cx - offset.x, pos.y - cy - offset.y);
						mHue = polar[1] / (FastMath.PI * 2f);
						mSaturation = polar[0] / (cellSize.x * 5.5f);
						updateBrightnessPalette();
						onChange(cell.getDefaultColor());
					});
					addElement(cell);
				}
			}
			layoutChildren();
		}
	}

	class BrightnessLayout extends AbstractGenericLayout<BrightnessPicker, Object> {

		@Override
		protected Vector2f calcMinimumSize(BrightnessPicker container) {
			return new Vector2f(cellSize.x * container.getElements().size(), cellSize.y)
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
			int tw = (int) (container.getElements().size() * cellSize.x);
			for (float x = start; x < end; x++)
				container.getElements().get((int) (x - start)).setBounds(
						((container.getWidth() - tw) / 2) + x * cellSize.x, (container.getHeight() - cellSize.y) / 2,
						cellSize.x, cellSize.y);
		}

	}

	class BrightnessPicker extends AbstractPalette {

		BrightnessPicker(ElementManager<?> screen, ColorRestrictionType restrictionType) {
			super(screen, restrictionType);
			setLayoutManager(new BrightnessLayout());
			for (int i = 0; i < MAX_BRIGHTNESS_BAR - (restrictionType.getValueBar() * 2); i++) {
				ColorCell cell = new ColorCell(screen, ColorRGBA.White);
				cell.onMousePressed(evt -> {
					barPalette.setSelected(cell);
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
