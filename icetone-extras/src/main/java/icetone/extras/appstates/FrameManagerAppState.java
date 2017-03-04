package icetone.extras.appstates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.font.BitmapFont;
import com.jme3.math.Vector2f;

import icetone.controls.buttons.PushButton;
import icetone.controls.containers.Frame;
import icetone.controls.containers.Frame.Listener;
import icetone.controls.containers.Frame.State;
import icetone.core.BaseElement;
import icetone.core.ElementContainer;
import icetone.core.Layout.LayoutType;
import icetone.core.BaseScreen;
import icetone.core.StyledContainer;
import icetone.core.Element;
import icetone.core.layout.Border;
import icetone.core.layout.BorderLayout;
import icetone.core.layout.FlowLayout;
import icetone.core.layout.ScreenLayoutConstraints;

public class FrameManagerAppState extends AbstractAppState implements Listener {
	final static Logger LOG = Logger.getLogger(FrameManagerAppState.class.getName());

	public enum Mode {
		STEP, CENTRE
	}

	private List<Frame> windows = new ArrayList<Frame>();
	private Map<Frame, PushButton> restorers = new HashMap<Frame, PushButton>();
	private Element windowBar;
	private StyledContainer layer;
	private Frame selectedWindow;
	private boolean used;
	private Stack<Frame> history = new Stack<Frame>();
	private ElementContainer<?, ?> screen;
	private Mode mode = Mode.STEP;
	private Vector2f next = new Vector2f();

	public FrameManagerAppState() {
		this(BaseScreen.get());
	}

	public FrameManagerAppState(ElementContainer<?, ?> screen) {
		this.screen = screen;
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	protected void addToScreen() {
		screen.showElement(layer, ScreenLayoutConstraints.fill);
	}

	protected void removeFromScreen() {
		layer.setDestroyOnHide(true);
		layer.hide();
	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		List<Frame> allWindows = Frame.getWindows();
		for (Frame w : allWindows) {
			if (w.isManagedHint())
				addWindow(w);
		}
		Frame.addGlobalListener(this);

		Frame anySel = null;
		for (Frame w : windows) {
			if (w.isSelected())
				if (anySel == null)
					anySel = w;
				else
					w.setSelected(false);
		}
		if (anySel == null && !allWindows.isEmpty()) {
			allWindows.get(0).setSelected(true);
		}

		if (used)
			throw new IllegalStateException("Cannot reuse a window manager.");

		layer = new StyledContainer(screen.getScreen());
		layer.setLayoutManager(new BorderLayout());
		layer.addStyleClass("window-layer");

		windowBar = new StyledContainer(screen.getScreen());
		windowBar.addStyleClass("window-bar");
		windowBar.setLayoutManager(new FlowLayout(4, BitmapFont.Align.Right));
		layer.addElement(windowBar, Border.SOUTH);

		addToScreen();
	}

	@Override
	public void stateAttached(AppStateManager stateManager) {
	}

	@Override
	public void stateDetached(AppStateManager stateManager) {
		super.stateDetached(stateManager);
		used = true;
		removeFromScreen();
		Frame.removeGlobalListener(this);
	}

	public void deselectAllWindows() {
		for (Frame w : windows) {
			w.setSelected(false);
		}
		selectedWindow = null;
	}

	public Frame getSelectedWindow() {
		return selectedWindow;
	}

	public List<Frame> getWindows() {
		return Collections.unmodifiableList(windows);
	}

	@Override
	public void destroyed(Frame window) {
		windows.remove(window);
		restorers.remove(window);
		history.remove(window);
		if (window.getState() == Frame.State.MINIMIZED) {
			for (BaseElement el : windowBar.getElements()) {
				if (el.getElementUserData().equals(window)) {
					windowBar.removeElement(el);
					break;
				}
			}
		}
		screen.dirtyLayout(false, LayoutType.boundsChange());
		screen.layoutChildren();
	}

	@Override
	public void closed(Frame window) {
		if (window == selectedWindow) {
			if (history.size() > 0)
				history.pop().setSelected(true);
		}
	}

	@Override
	public void stateChanged(Frame window, State oldState, State newState) {
		if (newState.equals(Frame.State.MINIMIZED)) {
			PushButton restorer = new PushButton(screen.getScreen());
			restorer.addStyleClass("restorer");
			restorer.onMouseReleased(evt -> window.restore());
			if (window == selectedWindow) {
				history.remove(selectedWindow);
				if (history.size() > 0) {
					history.pop().setSelected(true);
				}
				selectedWindow = null;
			}
			restorer.setElementUserData(window);
			restorer.setText(window.getDragBar().getText());
			restorers.put(window, restorer);
			windowBar.attachElement(restorer);
			// windowBar.sizeToContent();
			layer.dirtyParent(true, LayoutType.boundsChange());
			layer.layoutChildren();
			restorer.show();
		} else if (newState.equals(Frame.State.NORMAL)) {
			PushButton b = restorers.get(window);
			if (b != null) {
				b.setDestroyOnHide(true);
				b.hide();
				// windowBar.removeElement(b);
				// windowBar.sizeToContent();
				// layer.dirtyParent(true, LayoutType.boundsChange());
				// layer.layoutChildren();
			}
		}
	}

	@Override
	public void windowTitleChanged(Frame window, String oldTitle, String newTitle) {
		PushButton b = restorers.get(window);
		if (b != null) {
			b.setText(newTitle);
		}
	}

	@Override
	public void selected(Frame window) {
		this.selectedWindow = window;
		if (window != null) {
			history.remove(window);
			history.push(window);
			if (LOG.isLoggable(Level.FINE))
				LOG.fine(String.format("%s is now selected (history is now %d).", window, history.size()));
		}
		for (Frame w : windows) {
			if (w != window) {
				if (LOG.isLoggable(Level.FINE))
					LOG.fine(String.format("Deselecting %s", w));
				w.setSelected(false);
			}
		}
	}

	@Override
	public void created(Frame window) {
		if (window.isManagedHint())
			addWindow(window);
	}

	@Override
	public void opened(Frame window) {
	}

	protected void removeWindow(Frame window) {
		windows.remove(window);
	}

	protected void addWindow(Frame window) {
		if (windows.contains(window)) {
			LOG.warning(String.format("Window %s is already managed.", window));
		} else {
			windows.add(window);

			/*
			 * TODO when element position becomes a Position object, use that to
			 * determine if position manually
			 */
			if (window.getPosition().equals(Vector2f.ZERO)) {

				if (mode == Mode.STEP) {
					window.setPosition(next);

					float p = window.getDragBar().calcPreferredSize().y;
					next.x += p;
					next.y += p;
				} else if (mode == Mode.CENTRE) {
					window.centerToParent();
				}
			}
		}
	}
}
