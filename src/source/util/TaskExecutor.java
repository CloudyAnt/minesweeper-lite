package source.util;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.IntConsumer;

public class TaskExecutor {
    private static ExecutorService executorService;
    private static final ArrayList<Thread> stoppableThreads = new ArrayList<>();

    private TaskExecutor() {
    }

    private static ExecutorService getExecutorService() {
        if (executorService == null || executorService.isShutdown()) {
            executorService = Executors.newCachedThreadPool();
        }
        return executorService;
    }

    public static void execute(Runnable task) {
        getExecutorService().execute(task);
    }


    public static void execute(Runnable task, boolean stoppable) {
        execute(() -> {
            if (stoppable) {
                stoppableThreads.add(Thread.currentThread());
            }
            task.run();
        });
    }

    public static void shutdown() {
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }

    public static void interruptThreads() {
        for (Thread thread : stoppableThreads) {
            thread.interrupt();
        }
        stoppableThreads.clear();
    }

    public static void interval(Runnable begin, int interval, int totalFrames,
                                IntConsumer frameRender, Runnable end, boolean stoppable) {
        execute(() -> setInterval(begin, interval, totalFrames, frameRender, end), stoppable);
    }

    public static void interval(Runnable begin, int interval, int totalFrames,
                                IntConsumer frameRender, Runnable end) {
        execute(() -> setInterval(begin, interval, totalFrames, frameRender, end));
    }

    public static void interval(int interval, int totalFrames, IntConsumer frameRender) {
        execute(() -> setInterval(interval, totalFrames, frameRender));
    }

    private static void setInterval(Runnable begin, int interval, int totalFrames,
                                    IntConsumer frameRender, Runnable end) {
        if (begin != null) {
            begin.run();
        }

        setInterval(interval, totalFrames, frameRender);

        if (end != null) {
            end.run();
        }
    }

    private static void setInterval(int interval, int totalFrames, IntConsumer frameRender) {
        for (int i = 0; i < totalFrames; i++) {
            // Sleep
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Render
            frameRender.accept(i);
        }
    }
}
