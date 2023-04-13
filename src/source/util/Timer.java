package source.util;

import java.util.function.IntConsumer;

public class Timer {
    private State state;
    private int time = 0;
    private int millis = 0;
    private static final int INTERVAL = 100;
    private final IntConsumer secondConsumer;

    public Timer(IntConsumer secondConsumer) {
        this.secondConsumer = secondConsumer;
        state = State.PAUSED;
        TaskExecutor.execute(this::run, true);
    }

    public void start() {
        state = State.RUNNING;
    }

    public void pause() {
        state = State.PAUSED;
    }

    public void end() {
        state = State.FINALIZED;
    }

    private void run() {
        sleepInterval();
        while (state != State.FINALIZED) {
            judge();
            sleepInterval();
        }
    }

    private void judge() {
        switch (state) {
            case FINALIZED:
            case PAUSED: return;
            case RUNNING:
                millis += INTERVAL;
                int t = millis / 1000;
                if (t != time) {
                    time = t;
                    secondConsumer.accept(time);
                }
        }
    }

    private static void sleepInterval() {
        // Sleep
        try {
            Thread.sleep(INTERVAL);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    enum State {
        RUNNING,
        PAUSED,
        FINALIZED
    }
}
