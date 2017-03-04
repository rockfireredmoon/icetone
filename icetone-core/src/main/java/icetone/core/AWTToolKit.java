package icetone.core;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import com.jme3.app.Application;
import com.jme3.asset.AssetLoader;

public class AWTToolKit extends ToolKit {

	private Clipboard clipboard;

	public AWTToolKit(Application app) {
		super(app);
	}

	/**
	 * Returns the internal clipboard's current stored text
	 * 
	 * @return String text
	 */
	@Override
	public String getClipboardText() {
		try {
			String ret = "";
			clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			Transferable text = clipboard.getContents(null);
			boolean isText = (text != null && text.isDataFlavorSupported(DataFlavor.stringFlavor));
			if (isText) {
				ret = (String) text.getTransferData(DataFlavor.stringFlavor);
			}
			return ret;
		} catch (Exception ex) {
			return super.getClipboardText();
		}
	}

	/**
	 * Sets the current stored text to the internal clipboard.
	 * 
	 * @param text
	 *            The text to store
	 */
	@Override
	public void setClipboardText(String text) {
		try {
			clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			StringSelection stringSelection = new StringSelection(text);
			clipboard.setContents(stringSelection, new ClipboardOwner() {
				@Override
				public void lostOwnership(Clipboard clipboard, Transferable contents) {
				}
			});
		} catch (Exception ex) {
			super.setClipboardText(text);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends AssetLoader> getImageLoader() {
		try {
			return (Class<? extends AssetLoader>) getClass().getClassLoader()
					.loadClass("com.jme3.texture.plugins.AWTLoader");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
