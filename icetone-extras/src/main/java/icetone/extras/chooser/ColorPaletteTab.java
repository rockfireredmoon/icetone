package icetone.extras.chooser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.core.AbstractGenericLayout;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Element;
import icetone.extras.util.ExtrasUtil;

public abstract class ColorPaletteTab extends Element implements ColorTabPanel {

	private List<ColorRGBA> palette;
	private ColorRGBA color;
	private ColorRestrictionType restrictionType = ColorRestrictionType.getDefaultType();
	private ColorCell selected;
	private Vector2f cellSize;
	private Element highlight;

	public ColorPaletteTab(BaseScreen screen) {
		super(screen);
		ColorCell cell = new ColorCell(screen, ColorRGBA.White);
		highlight = new Element(screen) {
			{
				setStyleClass("cell-selected");
			}
		};
		cellSize = cell.calcPreferredSize();
		build();
	}

	public ColorRestrictionType getRestrictionType() {
		return restrictionType;
	}

	public void setRestrictionType(ColorRestrictionType restrictionType) {
		this.restrictionType = restrictionType;
		rebuild();
	}

	public void setPalette(List<ColorRGBA> palette) {
		if (palette == null)
			this.palette = null;
		else {
			this.palette = new ArrayList<ColorRGBA>(palette);
			Collections.sort(this.palette, new Comparator<ColorRGBA>() {

				@Override
				public int compare(ColorRGBA o1, ColorRGBA o2) {
					float[] hsv1 = ExtrasUtil.toHSB(o1);
					float[] hsv2 = ExtrasUtil.toHSB(o2);
					int x = Float.valueOf(hsv1[0]).compareTo(hsv2[0]);
					if (x == 0) {
						x = Float.valueOf(hsv1[1]).compareTo(hsv2[1]);
						if (x == 0) {
							x = Float.valueOf(hsv1[2]).compareTo(hsv2[2]);
						}
					}
					return x;
				}
			});
		}
		rebuild();
	}

	public void setColor(ColorRGBA color) {
		this.color = color == null ? null : color.clone();
		rebuild();
	}

	private void rebuild() {
		invalidate();
		removeAllChildren();
		build();
		validate();
	}

	protected void build() {
		addElement(highlight);
		if (palette != null) {
			setLayoutManager(new PaletteLayout());
			ColorRGBA closest = color == null ? null : findClosest(color);
			selected = null;
			for (ColorRGBA c : palette) {
				ColorCell pb = new ColorCell(getScreen(), c);
				if(selected== null && (closest == null || c.equals(closest))) {
					selected = pb;
				}
				pb.onMouseReleased(evt -> {
					setSelected(pb);
					onChange(c);
				});
				addElement(pb);
			}
		}
	}

	protected void setSelected(ColorCell selected) {
		if (!Objects.equals(this.selected, selected)) {
			this.selected = selected;
			dirtyLayout(false);
			layoutChildren();
		}
	}

	private ColorRGBA findClosest(ColorRGBA color) {
		float[] currenthsb = ExtrasUtil.toHSB(color);
		ColorRGBA closesthue = null;
		float closesthuedist = 0;
		float closestbridist = 0;
		for (ColorRGBA el : palette) {
			float[] splotchHSB = ExtrasUtil.toHSB(el);
			float hue = splotchHSB[0];
			float sat = splotchHSB[1];
			float bri = splotchHSB[2];
			float huedist = hue - currenthsb[0];
			float satdist = sat - currenthsb[1];
			float bridist = FastMath.abs(bri - currenthsb[2]);
			huedist = (float) Math.sqrt(huedist * huedist + satdist * satdist);
			if (closesthue == null) {
				closestbridist = bridist;
				closesthuedist = huedist;
				closesthue = el;
			} else if (currenthsb[0] == 0 && currenthsb[1] == 0  && hue == 0 && sat == 0) {
				if(bridist <= closestbridist) {
					closesthue = el;
					closestbridist = bridist;
				}
			} else if (huedist <= closesthuedist) {
				closesthue = el;
				closesthuedist = huedist;
				closestbridist = bridist;
			}
		}
		return closesthue;
	}

	class PaletteLayout extends AbstractGenericLayout<ColorPaletteTab, Object> {

		@Override
		protected Vector2f calcMinimumSize(ColorPaletteTab container) {
			int[] sz = getGridSize(container);
			return new Vector2f(cellSize.x * sz[0], cellSize.y * sz[1]).addLocal(container.getTotalPadding());
		}

		private int[] getGridSize(ColorPaletteTab container) {
			int w = 0;
			int h = 0;
			if (container.palette != null && container.palette.size() > 0) {
				int sqrt = Math.max(1, container.palette == null || container.palette.isEmpty() ? 1
						: (int) Math.sqrt(container.palette.size()));
				w = sqrt;
				h = sqrt;
				if (w * h < container.palette.size())
					w += (((1 + container.palette.size()) - (w * h)) / h);
			}
			return new int[] { w, h };
		}

		@Override
		protected Vector2f calcPreferredSize(ColorPaletteTab container) {
			return calcMinimumSize(container);
		}

		@Override
		protected void onLayout(ColorPaletteTab container) {
			int[] sz = getGridSize(container);
			int idx = 1;
			Vector4f textPaddingVec = container.getAllPadding();
			Vector2f position = new Vector2f(0, textPaddingVec.z);
			Vector2f ts = new Vector2f(sz[0], sz[1]).multLocal(cellSize);
			float sx = (container.getWidth() - ts.x - textPaddingVec.x - textPaddingVec.y) / 2f;
			position.y += (container.getHeight() - ts.y) / 2f;
			for (int y = 0; y < sz[0] && idx < container.getElements().size(); y++) {
				position.x = sx + textPaddingVec.x;
				for (int x = 0; x < sz[0] && idx < container.getElements().size(); x++) {
					BaseElement el = container.getElements().get(idx);
					if ((container.selected == null && idx == 1) || el == container.selected) {
						container.highlight.setBounds(position.x, position.y, cellSize.x, cellSize.y);
					}
					el.setBounds(position.x, position.y, cellSize.x, cellSize.y);
					position.x += cellSize.x;
					idx++;
				}
				position.y += cellSize.y;
			}
		}

	}
}
