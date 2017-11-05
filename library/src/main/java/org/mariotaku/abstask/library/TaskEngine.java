package org.mariotaku.abstask.library;

import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

@SuppressWarnings("WeakerAccess")
public abstract class TaskEngine {
    public abstract <Params, Result, Callback> void execute(AbstractTask<Params, Result, Callback> task);

    protected static <P, R, C> TaskDispatcher getDispatcher(AbstractTask<P, R, C> task) {
        return task.mDispatcher;
    }

    protected interface TaskController {
        boolean cancel(boolean mayInterruptIfRunning);
    }

    protected final static class TaskDispatcher<Params, Result, Callback> {

        private final AbstractTask<Params, Result, Callback> mTask;

        public TaskDispatcher(AbstractTask<Params, Result, Callback> task) {
            mTask = task;
        }

        @WorkerThread
        public Result invokeExecute() {
            return mTask.doLongOperation(mTask.mParams);
        }

        @MainThread
        public void invokeBeforeExecute() {
            mTask.beforeExecute();
        }

        @MainThread
        public void invokeAfterExecute(Result result) {
            mTask.mFinished.set(true);
            Callback callback = mTask.getCallback();
            mTask.afterExecute(callback, result, mTask.isCancelled());
        }

        @MainThread
        public void invokeCancelled(@Nullable Result result) {
            mTask.mCancelled.set(true);
            mTask.cancelRequested();
        }

    }
}
