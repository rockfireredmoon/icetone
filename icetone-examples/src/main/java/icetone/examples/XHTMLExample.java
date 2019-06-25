package icetone.examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;

import org.xhtmlrenderer.event.DocumentListener;
import org.xhtmlrenderer.simple.xhtml.XhtmlForm;
import org.xhtmlrenderer.util.XRLog;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;

import icetone.controls.buttons.PushButton;
import icetone.controls.containers.Frame;
import icetone.controls.lists.ComboBox;
import icetone.controls.text.Label;
import icetone.controls.text.TextField;
import icetone.core.BaseElement;
import icetone.core.ElementContainer;
import icetone.core.Screen;
import icetone.core.Size;
import icetone.core.layout.mig.MigLayout;
import icetone.fontawesome.FontAwesome;
import icetone.xhtml.XHTMLDisplay;
import icetone.xhtml.XHTMLUserAgent;

/**
 * This example shows some examples of usage of the {@link TextField} and its
 * relations.
 */
public class XHTMLExample extends SimpleApplication {

	public final static String HOME = "/demos/cursors.xhtml";
	private final static LinkedHashMap<String, String> demoFiles = new LinkedHashMap<String, String>();

	static {
		// Read in the demo file list if it is there
		InputStream in = XHTMLExample.class.getResourceAsStream("/demos/file-list.txt");
		if (in != null) {
			try {
				BufferedReader r = new BufferedReader(new InputStreamReader(in));
				String l;
				try {
					while ((l = r.readLine()) != null) {
						int idx = l.indexOf(",");
						demoFiles.put(l.substring(0, idx), l.substring(idx + 1));
					}
				} finally {
					r.close();
				}
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}
		}
	}
	private XHTMLDisplay xhtml;
	private Stack<String> history = new Stack<String>();
	private Stack<String> future = new Stack<String>();
	private Label status;

	public static void main(String[] args) {
		XHTMLExample app = new XHTMLExample();
		app.start();
	}

	@Override
	public void simpleInitApp() {
		/*
		 * We are only using a single screen, so just initialise it (and you don't need
		 * to provide the screen instance to each control).
		 * 
		 * It is passed to the buildExample method in this way to help ExampleRunner so
		 * this example can be run from there and as a standalone JME application
		 */
		buildExample(new Screen(this));

	}

	protected void buildExample(ElementContainer<?, ?> container) {

		// Tools
		BaseElement c = new BaseElement(
				new MigLayout("fill", "[shrink 0][shrink 0][shrink 0][][grow][shrink 0]", "[]"));

		// Back
		c.addElement(FontAwesome.BACKWARD.button(24, new PushButton()).setToolTipText("Back").onMouseReleased(evt -> {
			if (!history.isEmpty()) {
				String uri = history.pop();
				future.push(uri);
				xhtml.setDocument(uri);
			}
		}));

		// Forward
		c.addElement(FontAwesome.FORWARD.button(24, new PushButton()).setToolTipText("Forward").onMouseReleased(evt -> {
			if (!future.isEmpty()) {
				String uri = future.pop();
				history.push(uri);
				xhtml.setDocument(uri);
			}
		}));

		// Home
		c.addElement(FontAwesome.HOME.button(24, new PushButton()).setToolTipText("Home").onMouseReleased(evt -> {
			history.push(HOME);
			future.clear();
			xhtml.setDocument(HOME);
		}));

		ComboBox<Map.Entry<String, String>> files = new ComboBox<>();
		for (Map.Entry<String, String> en : demoFiles.entrySet()) {
			files.addComboItem(en.getKey(), en);
		}
		files.setEditable(false);
		files.onChange(evt -> {
			history.push(evt.getNewValue().getValue());
			xhtml.setDocument(evt.getNewValue().getValue());
		});
		c.addElement(files);

		// Forward
		final TextField address = new TextField();
		address.onKeyboardReleased((evt) -> {
			if (evt.getKeyCode() == KeyInput.KEY_RETURN) {
				history.push(evt.getElement().getText());
				xhtml.setDocument(evt.getElement().getText());
			}
		});
		c.addElement(address, "growx");

		// Reload
		c.addElement(FontAwesome.REFRESH.button(24, new PushButton()).setToolTipText("Reload").onMouseReleased(evt -> {
			if (history.size() > 0) {
				xhtml.setDocument(history.peek());
			}
		}));

		xhtml = new XHTMLDisplay(new XHTMLUserAgent()) {
			private int currentDemo;

			@Override
			protected void onFormSubmit(XhtmlForm form) {
				// Do something with form
			}

			@Override
			protected void onFormReset(XhtmlForm form) {
				super.onFormReset(form); // To change body of generated methods,
											// choose Tools | Templates.
			}

			@Override
			protected void onHover(org.w3c.dom.Element el) {
				// Override this method to get notified when a link is hovered
				// over

				if (el != null) {
					Link uri = findLink(el);
					if (uri != null) {
						status.setText(uri.getUri());
					} else {
						status.setText(" ");
					}
				} else {
					status.setText(" ");
				}
			}

			@Override
			protected void linkClicked(Link uri) {
				// Override this method for special handling of clicked links.
				// For example,
				// here you can intercept the URI and deal with special schemes.
				try {
					URI uriObject = new URI(uri.getUri());
					if (uriObject.getScheme() != null) {
						if (uriObject.getScheme().equals("demoNav") && !demoFiles.isEmpty()) {
							List<String> demoList = new ArrayList<String>(demoFiles.keySet());
							List<String> valList = new ArrayList<String>(demoFiles.values());
							currentDemo = valList.indexOf(xhtml.getSharedContext().getBaseURL());
							if (uriObject.getSchemeSpecificPart().equals("forward")) {
								currentDemo++;
								if (currentDemo >= demoList.size()) {
									currentDemo = 0;
								}
							} else if (uriObject.getSchemeSpecificPart().equals("back")) {
								currentDemo--;
								if (currentDemo < 0) {
									currentDemo = demoList.size() - 1;
								}

							} else {
								throw new Exception("Unknown demo URI.");
							}

							String resourcePath = demoFiles.get(demoList.get(currentDemo));
							uri = new Link(resourcePath);
						}
					}
					super.linkClicked(uri);
					history.push(uri.getUri());
					future.clear();
				} catch (Exception e) {
					errorPage(e);
				}
			}
		};
		// xhtml.setContentIndents(screen.getStyle("XHTML").getVector4f("contentIndents"));
		xhtml.setSelectable(true);

		// Status
		status = new Label("Status Bar");

		//

		// Window 1
		final Frame window1 = new Frame();
		window1.setTitle("XHTML - Not a browser :)");
		window1.getContentArea()
				.setLayoutManager(new MigLayout("wrap 1", "[fill, grow]", "[shrink 0][fill, grow][shrink 0]"));
		window1.setMinimizable(true);
		window1.setMaximizable(true);
		window1.setResizable(true);
		window1.getContentArea().setPreferredDimensions(new Size(600, 400));
		window1.getContentArea().addElement(c);
		window1.getContentArea().addElement(xhtml);
		window1.getContentArea().addElement(status);

		// Listen for events from the document
		xhtml.addDocumentListener(new DocumentListener() {
			public void documentStarted() {
			}

			public void documentLoaded() {
				String documentURI = xhtml.getSharedContext().getBaseURL();
				window1.getDragBar().setText(xhtml.getDocumentTitle());
				if (documentURI == null) {
					address.setText("");
				} else {
					documentURI = documentURI.replace(System.getProperty("user.dir"), ".");
					address.setText(documentURI);
					address.setCaretPositionToEnd();
				}
				for(Map.Entry<String, String> en : files.getValues()) {
					if(en.getValue().equals(documentURI))
						files.setSelectedByValue(en);
				}
			}

			public void onLayoutException(Throwable thrwbl) {
			}

			public void onRenderException(Throwable thrwbl) {
			}
		});

		try {
			xhtml.setDocument(HOME);
			history.push(HOME);
		} catch (Exception ex) {
			XRLog.load(Level.SEVERE, "Failed to parse URI.", ex);
			XRLog.log(INPUT_MAPPING_EXIT, Level.OFF, INPUT_MAPPING_EXIT);
		}

		container.showElement(window1);
	}

}
