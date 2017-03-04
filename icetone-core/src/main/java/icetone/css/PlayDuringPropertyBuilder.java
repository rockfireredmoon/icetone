package icetone.css;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.css.CSSPrimitiveValue;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.parser.CSSParseException;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.parser.property.AbstractPropertyBuilder;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;

public class PlayDuringPropertyBuilder extends AbstractPropertyBuilder {

	@Override
	public List<PropertyDeclaration> buildDeclarations(CSSName cssName, List values, int origin, boolean important,
			boolean inheritAllowed) {
		List<PropertyDeclaration> result = new ArrayList<>();

		PropertyDeclaration sound = null;
		// PropertyDeclaration backgroundRepeat = null;

		for (int i = 0; i < values.size(); i++) {
			PropertyValue value = (PropertyValue) values.get(i);
			checkInheritAllowed(value, false);

			short type = value.getPrimitiveType();
			if (type == CSSPrimitiveValue.CSS_IDENT) {

				IdentValue ident = checkIdent(CSSName.BACKGROUND_SHORTHAND, value);

				// if
				// (PrimitivePropertyBuilders.BACKGROUND_REPEATS.get(ident.FS_ID))
				// {
				// if (backgroundRepeat != null) {
				// throw new CSSParseException("A play-during-repeat value
				// cannot be set twice", -1);
				// }
				//
				// backgroundRepeat = new
				// PropertyDeclaration(CSSName.BACKGROUND_REPEAT, value,
				// important, origin);
				// }

				if (ident == IdentValue.NONE) {
					if (sound != null) {
						throw new CSSParseException("A background-image value cannot be set twice", -1);
					}

					sound = new PropertyDeclaration(CssExtensions.PLAY_DURING_SOUND, value, important, origin);
				}

				// if
				// (PrimitivePropertyBuilders.BACKGROUND_POSITIONS.get(ident.FS_ID))
				// {
				// processingBackgroundPosition = true;
				// }
			} else if (type == CSSPrimitiveValue.CSS_URI) {
				if (sound != null) {
					throw new CSSParseException("A play-during value cannot be set twice", -1);
				}

				sound = new PropertyDeclaration(CssExtensions.PLAY_DURING_SOUND, value, important, origin);
			}

		}

		if (sound == null) {
			sound = new PropertyDeclaration(CssExtensions.PLAY_DURING_SOUND, new PropertyValue(IdentValue.NONE), important,
					origin);
		}

		// if (backgroundRepeat == null) {
		// backgroundRepeat = new
		// PropertyDeclaration(CSSName.BACKGROUND_REPEAT,
		// new PropertyValue(IdentValue.REPEAT), important, origin);
		// }

		result = new ArrayList<PropertyDeclaration>(5);
		result.add(sound);
		// result.add(backgroundRepeat);

		return result;
	}
}