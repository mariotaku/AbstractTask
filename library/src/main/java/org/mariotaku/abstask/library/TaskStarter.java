package org.mariotaku.abstask.library;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;

@Deprecated
public class TaskStarter {

    private static AtomicReference<TaskEngine> sDefaultEngine = new AtomicReference<>();

    public static <Params, Result, Callback> void execute(AbstractTask<Params, Result, Callback> task) {
        task.execute(obtainDefaultEngine());
    }

    public static void setDefaultExecutor(@Nullable Executor executor) {
    }

    private static TaskEngine obtainDefaultEngine() {
        TaskEngine engine = sDefaultEngine.get();
        if (engine != null) return engine;
        AsyncTaskEngine defEngine = new AsyncTaskEngine(AsyncTask.SERIAL_EXECUTOR);
        sDefaultEngine.set(defEngine);
        return defEngine;
    }
}
