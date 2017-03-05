package icetone.core.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.app.Application;

public class Alarm {

	private static final Logger LOG = Logger.getLogger(Alarm.class.getName());

	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1, new ThreadFactory() {
		@Override
		public Thread newThread(Runnable r) {
			/* Want the executor to just die when the JVM shutsdown */
			Thread t = Executors.defaultThreadFactory().newThread(r);
			t.setDaemon(true);
			return t;
		}
	});;

	private final Application app;

	public final class AlarmTask implements Runnable {

		private Callable<Void> callable;
		private boolean cancelled;
		private final ScheduledFuture<?> future;

		private AlarmTask(Callable<Void> callable, float seconds) {
			this.callable = callable;

			future = executor.schedule(this, (long) (seconds * 1000f), TimeUnit.MILLISECONDS);
		}

		public void cancel() {
			cancelled = true;
			future.cancel(false);
		}

		public void cancelAndWait() {
			cancelled = true;
			future.cancel(false);
			try {
				future.get();
			} catch (CancellationException ce) {
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "Exception witing for alarm task to cancel.", e);
			}
		}

		@Override
		public void run() {
			if (!cancelled) {
				app.enqueue(callable);
			}
		}
	}

	public Alarm(Application app) {
		this.app = app;
	}

	public void stop() {
		executor.shutdown();
	}

	public AlarmTask timed(final Runnable runnable, float seconds) {
		return timed(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				runnable.run();
				return null;
			}}, seconds);
	}

	public AlarmTask timed(final Callable<Void> callable, float seconds) {
		AlarmTask task = new AlarmTask(callable, seconds);
		return task;
	}
}
