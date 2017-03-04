package icetone.css;

import java.util.BitSet;

import org.xhtmlrenderer.css.constants.IdentValue;

public class Overflow extends SingleIdent {
	// visible | hidden | scroll | auto | inherit
	private static final BitSet ALLOWED = CssExtensions.setFor(
			new IdentValue[] { IdentValue.VISIBLE, IdentValue.HIDDEN, IdentValue.SCROLL, IdentValue.AUTO });

	// We only support visible or hidden for now

	@Override
	protected BitSet getAllowed() {
		return ALLOWED;
	}
}