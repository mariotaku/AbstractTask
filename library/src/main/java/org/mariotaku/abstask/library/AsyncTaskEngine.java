package org.mariotaku.abstask.library;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

public class AsyncTaskEngine extends TaskEngine {

    @NonNull
    private final Executor executor;

    public AsyncTaskEngine(@NonNull Executor executor) {
        this.executor = executor;
    }

    @Override
    public <Params, Result, Callback> void execute(AbstractTask<Params, Result, Callback> task) {
        final AsyncTaskTask<Params, Result, Callback> asyncTaskTask = new AsyncTaskTask<>(task);
        asyncTaskTask.executeOnExecutor(executor);
    }

    static class AsyncTaskTask<P, R, C> extends AsyncTask<Object, Object, R> {

        private final AbstractTask<P, R, C> task;

        AsyncTaskTask(AbstractTask<P, R, C> task) {
            this.task = task;
            task.mController = new TaskController() {
                @Override
                public boolean cancel(boolean mayInterruptIfRunning) {
                    return AsyncTaskTask.this.cancel(mayInterruptIfRunning);
                }
            };
        }

        @Override
        protected void onPreExecute() {
            TaskEngine.getDispatcher(task).invokeBeforeExecute();
        }

        @Override
        protected R doInBackground(Object[] params) {
            TaskDispatcher<P, R, C> dispatcher = TaskEngine.getDispatcher(task);
            return dispatcher.invokeExecute();
        }

        @Override
        protected void onPostExecute(R r) {
            TaskEngine.getDispatcher(task).invokeAfterExecute(r);
        }

        @Override
        protected void onCancelled(R r) {
            TaskEngine.getDispatcher(task).invokeCancelled(r);
        }
    }
}
