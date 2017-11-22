package org.mariotaku.abstask.library;

import android.support.annotation.AnyThread;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Abstract Task class can be used with different implementations
 * Created by mariotaku on 16/2/24.
 */
@SuppressWarnings("WeakerAccess")
public abstract class AbstractTask<Params, Result, Throw extends Exception, Callback> {

    TaskEngine.TaskController mController;
    TaskEngine.TaskDispatcher<Params, Result, Throw, Callback> mDispatcher = new TaskEngine.TaskDispatcher<>(this);
    Params mParams;
    private WeakReference<Callback> mCallbackRef;
    final AtomicBoolean mFinished = new AtomicBoolean(false);
    final AtomicBoolean mCancelled = new AtomicBoolean(false);

    @WorkerThread
    protected abstract Result doLongOperation(Params params) throws Throw;

    @MainThread
    protected void beforeExecute() {

    }

    @SuppressWarnings("DeprecatedIsStillUsed")
    @MainThread
    @Deprecated
    protected void afterExecute(@Nullable Callback callback, Result result) {

    }

    @MainThread
    protected void afterExecute(@Nullable Callback callback, Result result, Throw error, boolean cancelled) {
        //noinspection deprecation
        afterExecute(callback, result);
    }

    @AnyThread
    protected void cancelRequested() {

    }

    public final void setParams(Params params) {
        mParams = params;
    }

    public final Params getParams() {
        return mParams;
    }

    public final AbstractTask<Params, Result, Throw, Callback> setCallback(Callback callback) {
        mCallbackRef = new WeakReference<>(callback);
        return this;
    }

    @Nullable
    public final Callback getCallback() {
        if (mCallbackRef == null) return null;
        return mCallbackRef.get();
    }

    public final boolean isFinished() {
        return mFinished.get();
    }

    public final boolean isCancelled() {
        return mCancelled.get();
    }

    public final void execute(TaskEngine engine) {
        engine.execute(this);
    }

    public final boolean cancel(boolean mayInterruptIfRunning) {
        if (mCancelled.get()) return false;
        mCancelled.set(true);
        if (mController != null) {
            return mController.cancel(mayInterruptIfRunning);
        } else {
            return true;
        }
    }

}
