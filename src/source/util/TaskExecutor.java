package source.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskExecutor {
    private static ExecutorService executorService;

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

    public static void shutdown() {
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }
}
