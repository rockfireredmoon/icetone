/**
 * ICETONE - A GUI Library for JME3 based on a heavily modified version of 
 * Tonegod's 'Tonegodgui'.  
 * 
 * Copyright (c) 2013, t0neg0d
 * Copyright (c) 2016, Emerald Icemoon
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of the FreeBSD Project.
 */

package icetone.css;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xhtmlrenderer.context.StylesheetFactoryImpl;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.extend.StylesheetFactory;
import org.xhtmlrenderer.css.sheet.FontFaceRule;
import org.xhtmlrenderer.css.sheet.Stylesheet;
import org.xhtmlrenderer.css.sheet.StylesheetInfo;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.FSDerivedValue;

import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.cursors.plugins.JmeCursor;
import com.jme3.font.BitmapFont;

import icetone.core.BaseScreen;
import icetone.core.Layout.LayoutType;
import icetone.core.Element;
import icetone.core.ToolKit;
import icetone.fonts.FontSpec;
import icetone.xhtml.XHTMLUserAgent;

/**
 * @author t0neg0d
 * @author rockfire
 */
public class StyleManager {

	public static enum CursorType {
		HIDDEN, CUSTOM_0, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5, CUSTOM_6, CUSTOM_7, CUSTOM_8, CUSTOM_9, HAND, MOVE, POINTER, RESIZE_CNE, RESIZE_CNW, RESIZE_EW, RESIZE_NS, TEXT, WAIT;

		public boolean isRequired() {
			switch (this) {
			case HIDDEN:
			case CUSTOM_0:
			case CUSTOM_1:
			case CUSTOM_2:
			case CUSTOM_3:
			case CUSTOM_4:
			case CUSTOM_5:
			case CUSTOM_6:
			case CUSTOM_7:
			case CUSTOM_8:
			case CUSTOM_9:
				return false;
			default:
				return true;
			}
		}
	}

	final static Logger LOG = Logger.getLogger(StyleManager.class.getName());

	static {
		CssExtensions.init();
	}

	public static class ThemeInstance {
		private List<BaseScreen> screens = new ArrayList<>();
		private Map<CursorType, JmeCursor> cursors = new HashMap<>();
		private Map<String, String> fonts = new HashMap<>();
		private Collection<Stylesheet> allStylesheets;
		private StylesheetFactory styleFactory;
		private boolean inited;
		private BitmapFont defaultGuiFont;

		protected ThemeInstance() {
			allStylesheets = new ArrayList<>();
		}

		public List<BaseScreen> getScreens() {
			return screens;
		}

		public JmeCursor getCursor(CursorType cursorType) {
			return this.cursors.get(cursorType);
		}

		public boolean isInitialised() {
			return inited;
		}

		public String getFontPath(String family) {
			return fonts.get(family);
		}

		public StylesheetFactory getStylesheetFactory() {
			if (styleFactory == null)
				styleFactory = new StylesheetFactoryImpl(new XHTMLUserAgent());
			return styleFactory;
		}

		public boolean hasCursors() {
			return !cursors.isEmpty();
		}

		public Set<String> getFontFamilies() {
			return fonts.keySet();
		}

		public void install() {
			FontSpec defspec = new FontSpec(fonts.get("default"), "Default", 12);
			defaultGuiFont = defspec.load(ToolKit.get().getApplication().getAssetManager());
			for (BaseScreen screen : screens) {
				install(screen);
			}
		}

		public void install(BaseScreen screen) {
			screen.dirtyLayout(true, LayoutType.reset);
			screen.layoutChildren();
			screen.setCursor(CursorType.POINTER);
		}

		protected void deinit() {
			if (allStylesheets != null) {
				allStylesheets = null;
			}
			inited = false;
			cursors.clear();
			if (styleFactory != null)
				((StylesheetFactoryImpl) styleFactory).flushCachedStylesheets();
		}

		protected void importFontRules(Stylesheet sh) {
			@SuppressWarnings("unchecked")
			List<FontFaceRule> fontFaces = sh.getFontFaceRules();
			for (FontFaceRule rule : fontFaces) {
				CalculatedStyle style = rule.getCalculatedStyle();
				FSDerivedValue src = style.valueByName(CSSName.SRC);
				FSDerivedValue family = style.valueByName(CSSName.FONT_FAMILY);
				LOG.info(String.format("Found found %s (%s)", family.asString(), src.asString().substring(1)));
				fonts.put(family.asString(), src.asString().substring(1));
			}
		}

		protected void importCursors() {
			for (CursorType t : CursorType.values()) {

				Element el = new Element(null, "cursor-" + t.name().toLowerCase().replace("_", "-"), null, null) {
					{
						getDimensions().set(999, 999);
					}
				};
				el.setThemeInstance(this);
				String cursorPath = el.getLayoutData();
				if (cursorPath != null) {
					JmeCursor cursor = ToolKit.get().getApplication().getAssetManager()
							.loadAsset(new AssetKey<JmeCursor>(cursorPath));
					if (el.getPosition().x != 999 && el.getPosition().y != 999) {
						cursor.setxHotSpot((int) el.getPosition().x);
						cursor.setyHotSpot((int) el.getPosition().y);
					}
					cursors.put(t, cursor);
					LOG.fine(String.format("Cursor %s is %s (%d,%d)", t, cursorPath, (int) el.getPixelPosition().x,
							(int) el.getPixelPosition().y));
				} else if (t.isRequired())
					LOG.warning(String.format("No cursor definition for %s", t));
			}
		}

		@SuppressWarnings("unchecked")
		private List<Stylesheet> readAndParseAll(List<StylesheetInfo> infos, String medium) {
			List<Stylesheet> result = new ArrayList<Stylesheet>(infos.size() + 15);
			for (StylesheetInfo info : infos) {
				if (info.appliesToMedia(medium)) {
					Stylesheet sheet = info.getStylesheet();
					if (sheet == null) {
						sheet = getStylesheetFactory().getStylesheet(info);
					}
					if (sheet != null) {
						if (sheet.getImportRules().size() > 0) {
							result.addAll(readAndParseAll(sheet.getImportRules(), medium));
						}
						result.add(sheet);
					} else {
						LOG.warning(String.format("Unable to load CSS from %s", info.getUri()));
					}
				}
			}
			return result;
		}

		public Collection<? extends Stylesheet> getStylesheets() {
			return allStylesheets;
		}

		public BitmapFont getDefaultGUIFont() {
			return defaultGuiFont;
		}

		@Override
		public String toString() {
			return "ThemeInstance [screens=" + screens + ", cursors=" + cursors + ", fonts=" + fonts
					+ ", allStylesheets=" + allStylesheets + ", styleFactory=" + styleFactory + ", inited=" + inited
					+ "]";
		}

		@SuppressWarnings("unchecked")
		public void load(List<Stylesheet> allMainSheets) {

			// Read in the imports
			for (Stylesheet s : allMainSheets) {
				allStylesheets.add(s);
				LOG.info(String.format("Reading styles from %s", s.getURI()));
				List<Stylesheet> all = readAndParseAll(s.getImportRules(), "all");
				Collections.reverse(all);
				allStylesheets.addAll(all);
			}

			// Add sheets to screen and import fonts for that sheet
			for (Stylesheet s : allStylesheets) {
				LOG.info(String.format("Adding stylesheet %s", s.getURI()));
				// screen.addStylesheet(s);
				importFontRules(s);
			}

			// Import cursors from all sheets
			importCursors();
		}
	}

	private Theme selectedTheme;
	private List<Theme> themes = new ArrayList<>();
	private ThemeInstance defaultStyle;

	public StyleManager() {

		// Add any themes on theme classpath
		try {
			Enumeration<URL> e = getClass().getClassLoader().getResources("META-INF/themes.list");
			while (e.hasMoreElements()) {
				BufferedReader r = new BufferedReader(new InputStreamReader(e.nextElement().openStream()));
				try {
					String line = null;
					while ((line = r.readLine()) != null) {
						line = line.trim();
						if (!line.startsWith("#") && !line.equals(""))
							addTheme(ToolKit.get().getApplication().getAssetManager()
									.loadAsset(new AssetKey<Theme>(line)));
					}
				} finally {
					r.close();
				}
			}
		} catch (IOException ioe) {
			throw new IllegalStateException("Failed to read theme list.", ioe);
		}
	}

	public ThemeInstance getDefaultInstance() {
		init();
		return defaultStyle;
	}

	public void addTheme(String path) {
		addTheme(ToolKit.get().getApplication().getAssetManager().loadAsset(new AssetKey<Theme>(path)));
	}

	public void addTheme(Theme theme) {
		if (themes.contains(theme))
			throw new IllegalArgumentException(String.format("Theme %s already exists.", theme.getName()));

		LOG.info(String.format("Adding theme %s", theme.getName()));
		themes.add(theme);
	}

	public Theme getTheme() {
		return selectedTheme;
	}

	public Theme getTheme(String name) {
		for (Theme t : themes) {
			if (t.getName().equals(name)) {
				return t;
			}
		}
		return null;
	}

	public Collection<Theme> getThemes() {
		return themes;
	}

	@SuppressWarnings("unchecked")
	public boolean init() {
		if (defaultStyle == null) {

			if (selectedTheme == null) {
				if (themes.isEmpty())
					throw new IllegalStateException("No themes found. Please add a theme pack to your classpath.");
				String defTheme = System.getProperty("icetone.defaultTheme", "");
				if (!defTheme.equals("")) {
					selectedTheme = getTheme(defTheme);
					if (selectedTheme == null) {
						LOG.warning(
								String.format("Chosen Default theme does not exist, reverting to default.", defTheme));
					}
				}

				if (selectedTheme == null) {
					for (Theme t : themes) {
						if (t.getParent() == null || t.getParent().equals("")) {
							selectedTheme = t;
							break;
						}
					}
					if (selectedTheme == null)
						throw new IllegalStateException(
								"No primary themes found (although theme extensions were found). Please add a primary theme pack to your classpath.");
				}
			}

			if (selectedTheme != null) {

				// Setup Flying Saucer XHTML (MUST do this before any
				// interaction with FS)
				setupXHTML();

				defaultStyle = new ThemeInstance();
				load(defaultStyle);

			} else {
				throw new IllegalStateException("No default theme is selected.");
			}

			defaultStyle.install();

			return true;
		}
		return false;
	}

	protected void load(ThemeInstance inst) {

		List<Stylesheet> allMainSheets = new ArrayList<>();

		// Any contributed sheets from extension modules
		for (Theme t : themes) {
			if (t != selectedTheme && matches(t.getParent(), selectedTheme.getName())) {
				allMainSheets
						.add(ToolKit.get().getApplication().getAssetManager().loadAsset(new AssetKey<>(t.getPath())));
			}
		}

		// The primary sheet
		allMainSheets.add(
				ToolKit.get().getApplication().getAssetManager().loadAsset(new AssetKey<>(selectedTheme.getPath())));

		inst.load(allMainSheets);
	}

	public void setTheme(String... theme) {
		for (String n : theme) {
			Theme t = getTheme(n);
			if (t != null) {
				setTheme(t);
				return;
			}
		}
		throw new IllegalArgumentException("None of the themes provided exist.");

	}

	public void reload() {
		if (selectedTheme == null)
			throw new IllegalStateException("Cannot reload when no theme is selected.");
		if (defaultStyle != null) {
			defaultStyle.deinit();
		}
		AssetManager mgr = ToolKit.get().getApplication().getAssetManager();
		if (mgr instanceof DesktopAssetManager) {
			((DesktopAssetManager) mgr).clearCache();
		}
		init();
	}

	public void setTheme(Theme theme) {
		if (!Objects.equals(selectedTheme, theme)) {
			selectedTheme = theme;

			List<BaseScreen> screens = null;

			if (defaultStyle != null) {
				defaultStyle.deinit();
				screens = defaultStyle.screens;
				defaultStyle = null;
			}

			init();

			if (screens != null && defaultStyle != null) {
				for (BaseScreen e : new ArrayList<>(screens))
					e.setThemeInstance(defaultStyle);
			}
		}
	}

	private boolean matches(String pattern, String name) {
		return pattern == null ? false : name.matches(pattern);
	}

	private void setupXHTML() {
		// For XHTML configuration
		String xrconf = System.getProperty("xr.conf");
		if (xrconf == null) {
			String path = selectedTheme.getPath();
			int idx = path.lastIndexOf('/');
			if (idx != -1) {
				path = path.substring(0, idx);
			}
			path += "/xmlrender.conf";
			URL in = getClass().getResource(path);
			if (in == null && !selectedTheme.getName().equals("Default")) {
				Theme def = getTheme("Default");
				if (def != null) {
					path = def.getPath();
					idx = path.lastIndexOf('/');
					if (idx != -1) {
						path = path.substring(0, idx);
					}
					path += "/xmlrender.conf";
					in = getClass().getResource(path);
				}
			}
			if (in == null) {
				LOG.log(Level.WARNING, "No xmlrender.conf could be located. XHTML is unlikely to work correctly.");
			} else {
				LOG.info(String.format("Setting up XHTML from %s", in));
				// TODO somehow allow FS to be reconfigured
				System.setProperty("xr.conf", in.toString());
			}
		}

	}
}
