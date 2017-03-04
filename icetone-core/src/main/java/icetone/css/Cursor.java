package icetone.css;

import java.util.BitSet;

import org.xhtmlrenderer.css.constants.IdentValue;

public class Cursor extends SingleIdent {
	// [ [<uri> ,]* [ auto | crosshair | default | pointer | move | e-resize
	// | ne-resize | nw-resize | n-resize | se-resize | sw-resize | s-resize
	// | w-resize | text | wait | help | progress ] ] | inherit
	private static final BitSet ALLOWED = CssExtensions.setFor(new IdentValue[] { IdentValue.NONE, IdentValue.AUTO,
			IdentValue.CROSSHAIR, IdentValue.DEFAULT, IdentValue.POINTER, IdentValue.MOVE, IdentValue.E_RESIZE,
			IdentValue.NE_RESIZE, IdentValue.NW_RESIZE, IdentValue.N_RESIZE, IdentValue.SE_RESIZE,
			IdentValue.SW_RESIZE, IdentValue.S_RESIZE, IdentValue.W_RESIZE, IdentValue.TEXT, IdentValue.WAIT,
			IdentValue.HELP, IdentValue.PROGRESS, CssExtensions.NS_RESIZE, CssExtensions.EW_RESIZE, CssExtensions.NWSE_RESIZE, CssExtensions.NESW_RESIZE });

	@Override
	protected BitSet getAllowed() {
		return ALLOWED;
	}
}