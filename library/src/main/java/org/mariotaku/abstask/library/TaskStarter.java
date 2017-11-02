package org.mariotaku.abstask.library;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by mariotaku on 16/2/24.
 */
public class TaskStarter {

    private static AtomicReference<Executor> sDefaultExecutor = new AtomicReference<>();

    public static <Params, Result, Callback> void execute(AbstractTask<Params, Result, Callback> task) {
        final AsyncTaskTask<Params, Result, Callback> asyncTaskTask = new AsyncTaskTask<>(task);
        Executor executor = sDefaultExecutor.get();
        if (executor != null) {
            asyncTaskTask.executeOnExecutor(executor);
        } else {
            asyncTaskTask.execute();
        }
    }

    public static void setDefaultExecutor(@Nullable Executor executor) {
        sDefaultExecutor.set(executor);
    }

    static class AsyncTaskTask<P, R, C> extends AsyncTask<Object, Object, R> {

        private final AbstractTask<P, R, C> task;

        AsyncTaskTask(AbstractTask<P, R, C> task) {
            this.task = task;
            task.mController = new AbstractTask.TaskController() {
                @Override
                public boolean cancel(boolean mayInterruptIfRunning) {
                    return AsyncTaskTask.this.cancel(mayInterruptIfRunning);
                }
            };
        }

        @Override
        protected void onPreExecute() {
            task.invokeBeforeExecute();
        }

        @Override
        protected R doInBackground(Object[] params) {
            return task.invokeExecute();
        }

        @Override
        protected void onPostExecute(R r) {
            task.invokeAfterExecute(r);
        }

        @Override
        protected void onCancelled(R r) {
            task.invokeCancelled(r);
        }
    }

}
