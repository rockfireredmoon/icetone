package icetone.css;

class NonNegativeLengthLike extends LengthLike {
	@Override
	protected boolean isNegativeValuesAllowed() {
		return false;
	}
}