package icetone.extras.chooser;

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

	private static ColorRestrictionType defaultType = ColorRestrictionType.DEVELOPMENT;

	public static ColorRestrictionType getDefaultType() {
		return defaultType;
	}

	public void setDefaultType(ColorRestrictionType defaultType) {
		ColorRestrictionType.defaultType = defaultType;
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