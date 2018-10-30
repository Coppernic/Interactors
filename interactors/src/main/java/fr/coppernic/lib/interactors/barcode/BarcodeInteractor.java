package fr.coppernic.lib.interactors.barcode;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import fr.coppernic.lib.interactors.ReaderInteractor;
import fr.coppernic.sdk.utils.core.CpcDefinitions;
import fr.coppernic.sdk.utils.core.CpcResult;
import fr.coppernic.sdk.utils.core.CpcResult.RESULT;
import fr.coppernic.sdk.utils.debug.L;
import fr.coppernic.sdk.utils.helpers.CpcOs;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

import static fr.coppernic.lib.interactors.BuildConfig.DEBUG;

public class BarcodeInteractor implements ReaderInteractor<String> {

    private static final String TAG = "BarcodeInteractor";
    private final Context context;
    private ObservableEmitter<String> emitter;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            processIntent(intent);
        }
    };
    private final ObservableOnSubscribe<String> observableOnSubscribe = new ObservableOnSubscribe<String>() {
        @Override
        public void subscribe(ObservableEmitter<String> e) throws Exception {
            setEmitter(e);
            registerReceiver();
        }
    };

    @SuppressWarnings("WeakerAccess")
    @Inject
    public BarcodeInteractor(Context context) {
        this.context = context;
    }

    /**
     * Trig a barcode read.
     * <p> If {@link BarcodeInteractor#listen()} is not called, then it is a no op
     * <p> If this interactor has not yet registered to barcode event, then it is a no op.
     * <p> For instance, if {@link Observable#retry()} is used, there is a moment in the process where
     * {@link Context#registerReceiver(BroadcastReceiver, IntentFilter)} and then
     * {@link Context#unregisterReceiver(BroadcastReceiver)} are called in a row. If a trig occurs between these
     * calls, then it is ignored. No error are thrown.
     */
    @Override
    public void trig() {
        Intent scanIntent = new Intent();
        scanIntent.setPackage(CpcOs.getSystemServicePackage(context));
        scanIntent.setAction(CpcDefinitions.INTENT_ACTION_SCAN);
        scanIntent.putExtra(CpcDefinitions.KEY_PACKAGE, context.getPackageName());
        ComponentName info = context.startService(scanIntent);
        if (info == null) {
            handleError(new CpcResult.ResultException(RESULT.SERVICE_NOT_FOUND));
        }
    }

    /**
     * Listen to barcode event.
     * <p>It is registering to intents sent by barcode service. To stop listening, you may want to call
     * {@link Disposable#dispose()} method to unregister the internal {@link BroadcastReceiver}
     * <p>You may also want to link this observable to {@link Observable#retry()} in case of {@link RESULT#TIMEOUT}
     * or {@link RESULT#CANCELLED} are sent. You can retrieve error code from a
     * {@link fr.coppernic.sdk.utils.core.CpcResult.ResultException} sent via
     * {@link io.reactivex.Observer#onError(Throwable)}. Both these exception are minor ones, and can be ignored
     * according to your business logic.
     *
     * @return String observable.
     */
    @Override
    public Observable<String> listen() {
        return Observable.create(observableOnSubscribe);
    }

    private void setEmitter(ObservableEmitter<String> e) {
        L.mt(TAG, DEBUG, e.toString());
        // End previous observer and start new one
        if (emitter != null && !emitter.isDisposed()) {
            emitter.onComplete();
        }
        emitter = e;
        emitter.setDisposable(new Disposable() {
            private final AtomicBoolean disposed = new AtomicBoolean(false);

            @Override
            public void dispose() {
                L.mt(TAG, DEBUG);
                unregisterReceiver();
                disposed.set(true);
            }

            @Override
            public boolean isDisposed() {
                return disposed.get();
            }
        });
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(CpcDefinitions.ACTION_SCAN_SUCCESS);
        filter.addAction(CpcDefinitions.ACTION_SCAN_ERROR);
        context.registerReceiver(receiver, filter);
    }

    private void unregisterReceiver() {
        try {
            context.unregisterReceiver(receiver);
        } catch (Exception e) {
            Timber.v(e.toString());
        }
    }

    private void handleError(Throwable t) {
        unregisterReceiver();
        if (emitter != null && !emitter.isDisposed()) {
            emitter.onError(t);
        }
    }

    private void processIntent(Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            Timber.e("Action of %s is null", intent);
            return;
        }

        if (emitter == null || emitter.isDisposed()) {
            unregisterReceiver();
            return;
        }

        if (action.equals(CpcDefinitions.ACTION_SCAN_SUCCESS)) {
            Bundle extras = intent.getExtras();
            if (extras == null) {
                Timber.e("No extras for ACTION_SCAN_SUCCESS");
                return;
            }
            String data = extras.getString(CpcDefinitions.KEY_BARCODE_DATA, "");
            emitter.onNext(data);
        } else if (intent.getAction().equals(CpcDefinitions.ACTION_SCAN_ERROR)) {
            int res = intent.getIntExtra(CpcDefinitions.KEY_RESULT, RESULT.ERROR.ordinal());
            RESULT result = RESULT.values()[res];
            emitter.onError(result.toException());
        }
    }
}
