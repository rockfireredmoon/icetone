package icetone.extras.actions;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;

import icetone.core.BaseScreen;

public class ActionAppState extends AbstractAppState {

	private BaseScreen screen;
	private ActionMenuBar menuBar;

	public ActionAppState(BaseScreen screen) {
		this.screen = screen;

		menuBar = new ActionMenuBar() {
			
		};
	}

	public ActionMenuBar getMenuBar() {
		return menuBar;
	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		addMenuBarToScene();
	}

	@Override
	public void cleanup() {
		super.cleanup();
		removeMenuBarFromScene();
	}

	protected void addMenuBarToScene() {
		screen.addElement(menuBar);
	}

	protected void removeMenuBarFromScene() {
		screen.removeElement(menuBar);
	}
}
