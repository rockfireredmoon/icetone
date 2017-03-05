package icetone.extras.appstates;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;

import icetone.controls.buttons.Button;
import icetone.controls.text.Label;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Element;
import icetone.core.Size;
import icetone.core.StyledContainer;
import icetone.core.ToolKit;
import icetone.core.ZPriority;
import icetone.core.layout.ScreenLayoutConstraints;
import icetone.core.layout.XYLayout;
import icetone.core.layout.mig.MigLayout;

/**
 * Displays error, warning and information popup messages. These messages will
 * be visible for a short amout of time, and them will be scrolled offscreen.
 * <p>
 * When a new message comes in before previous messages are hidden, any current
 * messages are shift up to make room. Their timers are also reset so all
 * visible messages will scroll of at the same time.
 */
public class PopupMessageAppState extends AbstractAppState {

	public static final int MAX_MESSAGES_ON_SCREEN = 10;

	public enum Channel {
		INFORMATION, WARNING, ERROR, BROADCAST;

		public String getStyleClass() {
			switch (this) {
			case ERROR:
				return "color-error";
			case WARNING:
				return "color-warning";
			case BROADCAST:
				return "color-success";
			default:
				return "color-information";
			}
		}
	}

	public interface Listener {
		void message(Channel channel, final String message, final Exception exception);
	}

	private List<PopupMessage> queue = new ArrayList<PopupMessage>();
	private List<PopupMessage> messages = new ArrayList<PopupMessage>();
	private BaseScreen screen;
	private BaseElement layer;
	private List<Listener> listeners = new ArrayList<Listener>();
	private Application app;
	private float messageTimeout = 10f;

	public static class PopupMessage extends Element {

		String message;
		Exception exception;
		float time = 0;
		Channel channel;

		PopupMessage(Channel channel, String message, Exception exception) {
			this.message = message;
			this.channel = channel;
			this.exception = exception;
			setStyleClass("popup-message popup-message-" + channel.name().toLowerCase());
			setLayoutManager(new MigLayout(screen, "ins 0", "[shrink 0][]"));
			addElement(new Button(screen) {
				{
					setStyleClass("popup-message-action");
				}
			});
			addElement(new Label(getMessageText(), screen) {
				{
					setStyleClass("popup-message-text " + channel.getStyleClass());
				}
			});
			setDestroyOnHide(true);
		}

		private String getMessageText() {
			return exception == null ? message : message + " " + exception.getMessage();
		}
	}

	public PopupMessageAppState(BaseScreen screen) {
		this.screen = screen;
	}

	public float getMessageTimeout() {
		return messageTimeout;
	}

	public void setMessageTimeout(float messageTimeout) {
		this.messageTimeout = messageTimeout;
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.add(listener);
	}

	public void message(final Channel channel, final String message) {
		message(channel, message, null);
	}

	public void message(final Channel channel, final String message, final Exception exception) {

		if (!ToolKit.get().isSceneThread()) {
			app.enqueue(new Callable<Void>() {
				public Void call() throws Exception {
					message(channel, message, exception);
					return null;
				}
			});
			return;
		}
		PopupMessage msg = new PopupMessage(channel, message, exception);

		if (layer == null) {
			queue.add(msg);
			return;
		}

		// Create new message and add it to screen
		// Vector2f pref = msg.calcPreferredSize();
		// Vector2f pos = new Vector2f((screen.getWidth() - pref.x) / 2f,
		// screen.getHeight() * 0.33f);
		// pref.x = screen.getWidth();
		// msg.setBounds(pos.x, pos.y, pref.x, pref.y);
		popup(msg);
	}

	protected void popup(PopupMessage msg) {
		layer.addElement(msg);
		messages.add(msg);

		// Shift up any existing messages
		for (Iterator<PopupMessage> mIt = messages.iterator(); mIt.hasNext();) {
			PopupMessage m = mIt.next();
			float ny = m.getY() - m.getHeight();
			if (ny > 0)
				m.setY(ny);
			else {
				mIt.remove();
				m.destroy();
			}
		}

		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).message(msg.channel, msg.message, msg.exception);
		}
	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		this.app = app;
		layer = new StyledContainer(screen);
		layer.setLayoutManager(new XYLayout());
		layer.setPriority(ZPriority.POPUP);
		screen.addElement(layer, ScreenLayoutConstraints.fill);
		if (!queue.isEmpty()) {
			for (PopupMessage m : queue) {
				popup(m);
			}
			queue.clear();
		}
	}

	@Override
	public void cleanup() {
		super.cleanup();
		layer.removeFromParent();
	}

	@Override
	public void update(float tpf) {
		super.update(tpf);
		if (!messages.isEmpty()) {
			for (Iterator<PopupMessage> mIt = messages.iterator(); mIt.hasNext();) {
				PopupMessage m = mIt.next();
				m.time += tpf;
				if (m.time > messageTimeout) {
					mIt.remove();
					m.hide();
				}
			}
		}
	}
}
