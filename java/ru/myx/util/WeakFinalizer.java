package ru.myx.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.function.Consumer;

import ru.myx.ae3.report.Report;

/** @author myx
 *
 * @param <T> */
public class WeakFinalizer<T> extends WeakReference<T> {

	private static ReferenceQueue<Object> QUEUE = new ReferenceQueue<>();

	static {
		new Thread("WEAK-FINALIZER") {

			{
				this.setDaemon(true);
			}

			@Override
			public void run() {

				for (;;) {
					WeakFinalizer<?> reference;
					try {
						reference = (WeakFinalizer<?>) WeakFinalizer.QUEUE.remove();
					} catch (final InterruptedException e) {
						System.err.println("Transfer " + this + " exits (interrupted).");
						return;
					}
					if (reference == null) {
						System.err.println("Transfer " + this + " exits (exhausted).");
						return;
					}
					try {
						reference.clean();
					} catch (final Throwable t) {
						Report.exception("DEFER-CLOSE-THREAD", "Close fail", t);
					}
				}
			}

		}.start();

	}

	/** @param <T>
	 * @param t
	 * @param f
	 * @return */
	@SuppressWarnings("unused")
	public static <T> T register(final T t, final Consumer<T> f) {

		new WeakFinalizer<>(t, f);
		return t;
	}

	final Consumer<T> f;

	private WeakFinalizer(final T t, final Consumer<T> f) {

		super(t, WeakFinalizer.QUEUE);
		this.f = f;
	}

	private void clean() {

		this.f.accept(this.get());
	}
}
