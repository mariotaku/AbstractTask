package org.mariotaku.abstask.library;

import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import java.lang.ref.WeakReference;

/**
 * Abstract Task class can be used with different implementations
 * Created by mariotaku on 16/2/24.
 */
public abstract class AbstractTask<Params, Result, Callback> {

    private Params mParams;
    private WeakReference<Callback> mCallbackRef;
    private boolean mIsFinished;

    @WorkerThread
    protected abstract Result doLongOperation(Params params);

    @MainThread
    protected void beforeExecute() {

    }

    @MainThread
    protected void afterExecute(@Nullable Callback callback, Result result) {

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
    protected final Callback getCallback() {
        if (mCallbackRef == null) return null;
        return mCallbackRef.get();
    }

    public final boolean isFinished() {
        return mIsFinished;
    }

    @MainThread
    final void invokeBeforeExecute() {
        beforeExecute();
    }

    @MainThread
    final void invokeAfterExecute(Result result) {
        mIsFinished = true;
        Callback callback = getCallback();
        afterExecute(callback, result);
    }

    @WorkerThread
    final Result invokeExecute() {
        return doLongOperation(mParams);
    }
}
