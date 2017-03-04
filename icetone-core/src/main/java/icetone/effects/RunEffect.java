package icetone.effects;

public class RunEffect extends AbstractEffect {
	private Runnable runnable;

	public RunEffect(Runnable runnable) {
		this.runnable = runnable;
	}

	@Override
	public boolean isConflict(IEffect effect) {
		/* By default this should always run */
		return false;
	}

	public Runnable getRunnable() {
		return runnable;
	}

	@Override
	public void update(float tpf) {
		runnable.run();
		isActive = false;
	}
}
