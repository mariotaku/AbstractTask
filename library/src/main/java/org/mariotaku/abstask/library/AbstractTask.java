package org.mariotaku.abstask.library;

import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Abstract Task class can be used with different implementations
 * Created by mariotaku on 16/2/24.
 */
public abstract class AbstractTask<Params, Result, Callback> {

    TaskController mController;
    private Params mParams;
    private WeakReference<Callback> mCallbackRef;
    private final AtomicBoolean mFinished = new AtomicBoolean(false);
    private final AtomicBoolean mCancelled = new AtomicBoolean(false);

    @WorkerThread
    protected abstract Result doLongOperation(Params params);

    @MainThread
    protected void beforeExecute() {

    }

    @MainThread
    protected void afterExecute(@Nullable Callback callback, Result result) {

    }

    @MainThread
    protected void cancelled(@Nullable Callback callback, @Nullable Result result) {

    }

    public final void setParams(Params params) {
        mParams = params;
    }

    public final Params getParams() {
        return mParams;
    }

    public final AbstractTask<Params, Result, Callback> setCallback(Callback callback) {
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

    public final boolean cancel(boolean mayInterruptIfRunning) {
        if (mCancelled.get()) return false;
        mCancelled.set(true);
        if (mController != null) {
            return mController.cancel(mayInterruptIfRunning);
        } else {
            return true;
        }
    }

    @WorkerThread
    final Result invokeExecute() {
        return doLongOperation(mParams);
    }

    @MainThread
    final void invokeBeforeExecute() {
        beforeExecute();
    }

    @MainThread
    final void invokeAfterExecute(Result result) {
        mFinished.set(true);
        Callback callback = getCallback();
        if (isCancelled()) {
            cancelled(callback, result);
        } else {
            afterExecute(callback, result);
        }
    }

    @MainThread
    final void invokeCancelled(@Nullable Result result) {
        mFinished.set(true);
        Callback callback = getCallback();
        cancelled(callback, result);
    }

    interface TaskController {
        boolean cancel(boolean mayInterruptIfRunning);
    }
}
