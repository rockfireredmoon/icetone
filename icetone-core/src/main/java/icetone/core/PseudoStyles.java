package icetone.core;

import java.util.LinkedHashSet;

import icetone.css.CssProcessor.PseudoStyle;

public class PseudoStyles extends LinkedHashSet<PseudoStyle> {
	
	public static PseudoStyles get(PseudoStyles instance) {
		if(instance == null)
			return new PseudoStyles();
		return instance;
	}

	public PseudoStyle[] asArray() {
		return toArray(new PseudoStyle[0]);
	}

	public PseudoStyles addStyle(PseudoStyle link) {
		add(link);
		return this;
	}

}
