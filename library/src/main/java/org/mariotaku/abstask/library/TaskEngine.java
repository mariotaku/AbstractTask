package org.mariotaku.abstask.library;

import android.support.annotation.AnyThread;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

@SuppressWarnings("WeakerAccess")
public abstract class TaskEngine {
    public abstract <Params, Result, Throw extends Exception, Callback> void execute(@NonNull AbstractTask<Params, Result, Throw, Callback> task);

    @NonNull
    protected static <P, R, T extends Exception, C> TaskDispatcher<P, R, T, C> getDispatcher(@NonNull AbstractTask<P, R, T, C> task) {
        return task.mDispatcher;
    }

    protected static <P, R, T extends Exception, C> void setController(@NonNull AbstractTask<P, R, T, C> task, @Nullable TaskController controller) {
        task.mController = controller;
    }


    protected interface TaskController {
        boolean cancel(boolean mayInterruptIfRunning);
    }

    protected final static class TaskDispatcher<Params, Result, Throw extends Exception, Callback> {

        @NonNull
        private final AbstractTask<Params, Result, Throw, Callback> mTask;

        public TaskDispatcher(@NonNull AbstractTask<Params, Result, Throw, Callback> task) {
            mTask = task;
        }

        @WorkerThread
        public Result invokeExecute() throws Throw {
            return mTask.doLongOperation(mTask.mParams);
        }

        @MainThread
        public void invokeBeforeExecute() {
            mTask.beforeExecute();
        }

        @MainThread
        public void invokeAfterExecute(@Nullable Result result, @Nullable Throw error) {
            mTask.mFinished.set(true);
            Callback callback = mTask.getCallback();
            mTask.afterExecute(callback, result, error, mTask.isCancelled());
        }

        @AnyThread
        public void invokeCancelRequested() {
            mTask.cancelRequested();
        }

    }
}
