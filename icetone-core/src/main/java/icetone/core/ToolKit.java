package icetone.core;

import java.util.concurrent.Callable;

import com.jme3.app.Application;
import com.jme3.asset.AssetLoader;
import com.jme3.asset.AssetManager;

import icetone.core.layout.loader.LayoutLoader;
import icetone.core.utils.Alarm;
import icetone.css.CssLoader;
import icetone.css.StyleManager;
import icetone.css.ThemeLoader;

public abstract class ToolKit {

	final static float DEFAULT_DOUBLE_CLICK_TIME = 0.5f;
	final static float DEFAULT_MENU_HIDE_DELAY = 0.5f;
	final static float KEY_REPEAT_DELAY = 0.5f;
	final static float KEY_REPEAT_INTERVAL = 0.05f;
	static final float DEFAULT_DOUBLE_CLICK_DISTANCE_THRESHOLD = 4;

	private Application application;

	private static ToolKit defaultInstance;
	private String clipboardText;
	private StyleManager styleManager;
	private Alarm alarm;
	private Thread thread;

	protected ToolkitConfiguration configuration;

	public static ToolKit get() {
		if (defaultInstance == null)
			throw new IllegalStateException("Not inited.");
		return defaultInstance;
	}

	public ToolkitConfiguration getConfiguration() {
		return configuration;
	}

	public Application getApplication() {
		return application;
	}

	public abstract Class<? extends AssetLoader> getImageLoader();

	protected ToolKit(Application application) {
		if (defaultInstance != null)
			throw new IllegalStateException("Toolkit already initialised.");
		defaultInstance = this;
		this.application = application;

		AssetManager assetManager = application.getAssetManager();
		assetManager.registerLoader(LayoutLoader.class, "yaml");
		assetManager.registerLoader(CssLoader.class, "css");
		assetManager.registerLoader(ThemeLoader.class, "theme");

		styleManager = new StyleManager(application);
		configuration = new ToolkitConfiguration() {

			@Override
			public float getMenuHideDelay() {
				return DEFAULT_MENU_HIDE_DELAY;
			}

			@Override
			public float getDoubleClickTime() {
				return DEFAULT_DOUBLE_CLICK_TIME;
			}

			@Override
			public float getRepeatDelay() {
				return KEY_REPEAT_DELAY;
			}

			@Override
			public float getRepeatInterval() {
				return KEY_REPEAT_INTERVAL;
			}

			@Override
			public float getDoubleClickDistanceThreshold() {
				return DEFAULT_DOUBLE_CLICK_DISTANCE_THRESHOLD;
			}
		};

		thread = Thread.currentThread();
	}

	public boolean isSceneThread() {
		return Thread.currentThread().equals(thread);
	}

	public void execute(Runnable r) {
		if (isSceneThread()) {
			try {
				r.run();
			} catch (RuntimeException re) {
				throw re;
			} catch (Exception e) {
				throw new RuntimeException("Failed to execute.", e);
			}
		} else
			getApplication().enqueue(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					r.run();
					return null;
				}
			});
	}

	public Alarm getAlarm() {
		if (alarm == null) {
			alarm = new Alarm(getApplication());
		}
		return alarm;
	}

	public void setClipboardText(String clipboardText) {
		this.clipboardText = clipboardText;
	}

	public String getClipboardText() {
		return clipboardText;
	}

	public StyleManager getStyleManager() {
		return styleManager;
	}

	public static boolean isAndroid() {
		String OS = System.getProperty("java.vendor").toLowerCase();
		return (OS.indexOf("android") >= 0);
	}
	// </editor-fold>

	public static boolean isMac() {
		String OS = System.getProperty("os.name").toLowerCase();
		return (OS.indexOf("mac") >= 0);
	}

	public static boolean isSolaris() {
		String OS = System.getProperty("os.name").toLowerCase();
		return (OS.indexOf("sunos") >= 0);
	}

	public static boolean isUnix() {
		String OS = System.getProperty("os.name").toLowerCase();
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);
	}

	// <editor-fold desc="OS Helpers">
	public static boolean isWindows() {
		String OS = System.getProperty("os.name").toLowerCase();
		return (OS.indexOf("win") >= 0);
	}

	public static void init(Application app) {
		if (ToolKit.isAndroid())
			new AndroidToolKit(app);
		else
			new AWTToolKit(app);
	}

	public static boolean isInited() {
		return defaultInstance != null;
	}

}
