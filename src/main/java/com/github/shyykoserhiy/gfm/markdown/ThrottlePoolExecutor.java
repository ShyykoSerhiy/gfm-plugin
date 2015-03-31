package com.github.shyykoserhiy.gfm.markdown;

public class ThrottlePoolExecutor {
    private long throttleTimeout;
    private Thread delayedThread;

    public ThrottlePoolExecutor(long throttleTimeout) {
        this.throttleTimeout = throttleTimeout;
    }

    public synchronized void submit(Runnable runnable) {
        clearDelayedThread();
        this.delayedThread = new Thread(new DelayedCancalableTask(runnable, throttleTimeout));
        this.delayedThread.start();
    }

    public synchronized void clearDelayedThread() {
        if (delayedThread != null) {
            delayedThread.interrupt();
            delayedThread = null;
        }
    }

    private class DelayedCancalableTask implements Runnable {
        private Runnable runnable;
        private long delay;

        public DelayedCancalableTask(Runnable runnable, long delay) {
            this.runnable = runnable;
            this.delay = delay;
        }

        @Override
        public void run() {
            try {
                synchronized (this) {
                    wait(delay);
                }
            } catch (InterruptedException e) {
                return;
            }
            runnable.run();
        }
    }
}
