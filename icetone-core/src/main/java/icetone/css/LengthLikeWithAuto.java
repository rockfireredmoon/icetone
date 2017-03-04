package icetone.css;

import java.util.BitSet;

import org.xhtmlrenderer.css.constants.IdentValue;

class LengthLikeWithAuto extends LengthLikeWithIdent {
	// <length> | <percentage> | auto | inherit
	private static final BitSet ALLOWED = CssExtensions.setFor(new IdentValue[] { IdentValue.AUTO });

	protected BitSet getAllowed() {
		return ALLOWED;
	}
}