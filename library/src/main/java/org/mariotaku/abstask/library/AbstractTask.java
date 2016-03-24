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

    @WorkerThread
    protected abstract Result doLongOperation(Params params);

    @MainThread
    protected void beforeExecute(Params params) {

    }

    @MainThread
    protected void afterExecute(Result result) {

    }

    @MainThread
    protected void afterExecute(Callback callback, Result result) {

    }

    public final void setParams(Params params) {
        mParams = params;
    }

    public final AbstractTask<Params, Result, Callback> setResultHandler(Callback callback) {
        mCallbackRef = new WeakReference<>(callback);
        return this;
    }

    @Nullable
    protected final Callback getCallback() {
        if (mCallbackRef == null) return null;
        return mCallbackRef.get();
    }

    protected final Params getParams() {
        return mParams;
    }

    @MainThread
    final void invokeBeforeExecute() {
        beforeExecute(mParams);
    }

    @MainThread
    final void invokeAfterExecute(Result result) {
        Callback callback = getCallback();
        if (callback != null) {
            afterExecute(callback, result);
        } else {
            afterExecute(result);
        }
    }

    @WorkerThread
    final Result invokeExecute() {
        return doLongOperation(mParams);
    }
}
