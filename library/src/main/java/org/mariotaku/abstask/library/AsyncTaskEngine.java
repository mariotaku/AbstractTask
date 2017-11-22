package org.mariotaku.abstask.library;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.concurrent.Executor;

@SuppressWarnings("WeakerAccess")
public class AsyncTaskEngine extends TaskEngine {

    @NonNull
    private final Executor executor;

    public AsyncTaskEngine(@NonNull Executor executor) {
        this.executor = executor;
    }

    @Override
    public <Params, Result, Throw extends Exception, Callback> void execute(@NonNull AbstractTask<Params, Result, Throw, Callback> task) {
        final AsyncTaskTask<Params, Result, Throw, Callback> asyncTaskTask = new AsyncTaskTask<>(task);
        asyncTaskTask.executeOnExecutor(executor);
    }

    static class AsyncTaskTask<P, R, T extends Exception, C> extends AsyncTask<Object, Object, AsyncTaskResult<R, T>> {

        private final AbstractTask<P, R, T, C> task;

        AsyncTaskTask(final AbstractTask<P, R, T, C> task) {
            this.task = task;
            setController(task, new TaskController() {
                @Override
                public boolean cancel(boolean mayInterruptIfRunning) {
                    getDispatcher(task).invokeCancelRequested();
                    return AsyncTaskTask.this.cancel(mayInterruptIfRunning);
                }
            });
        }

        @Override
        protected void onPreExecute() {
            getDispatcher(task).invokeBeforeExecute();
        }

        @Override
        protected AsyncTaskResult<R, T> doInBackground(Object[] params) {
            TaskDispatcher<P, R, T, C> dispatcher = getDispatcher(task);
            try {
                return new AsyncTaskResult<>(dispatcher.invokeExecute(), null);
            } catch (Exception t) {
                //noinspection unchecked
                return new AsyncTaskResult<>(null, (T) t);
            }
        }

        @Override
        protected void onPostExecute(AsyncTaskResult<R, T> r) {
            getDispatcher(task).invokeAfterExecute(r.result, r.error);
        }

        @Override
        protected void onCancelled(@Nullable AsyncTaskResult<R, T> r) {
            TaskDispatcher<P, R, T, C> dispatcher = getDispatcher(task);
            if (r != null) {
                dispatcher.invokeAfterExecute(r.result, r.error);
            } else {
                dispatcher.invokeAfterExecute(null, null);
            }
        }
    }

    static class AsyncTaskResult<Result, Throw extends Exception> {
        Result result;
        Throw error;

        public AsyncTaskResult(Result result, Throw error) {
            this.result = result;
            this.error = error;
        }
    }
}
