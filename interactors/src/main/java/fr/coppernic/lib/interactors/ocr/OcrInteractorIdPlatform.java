package fr.coppernic.lib.interactors.ocr;

import android.content.Context;
import android.os.SystemClock;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import fr.coppernic.external.elyctismrz.MrzScanner;
import fr.coppernic.sdk.power.PowerManager;
import fr.coppernic.sdk.power.api.PowerListener;
import fr.coppernic.sdk.power.api.peripheral.Peripheral;
import fr.coppernic.sdk.power.impl.idplatform.IdPlatformPeripheral;
import fr.coppernic.sdk.utils.core.CpcResult;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import timber.log.Timber;

public class OcrInteractorIdPlatform implements OcrInteractor, PowerListener, MrzScanner.Listener {
    private Context context;

    // MRZ reader
    // Elyctis ID Platform
    private MrzScanner mrzReader;
    private SingleEmitter<String> emitter;
    private SingleEmitter<Boolean> powerEmitter;
    private AtomicBoolean isReading = new AtomicBoolean(false);

    @Inject
    OcrInteractorIdPlatform(Context context) {
        this.context = context;
    }

    @Override
    public Single<Boolean> power(final Boolean power) {

        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(SingleEmitter<Boolean> emitter) throws Exception {
                powerEmitter = emitter;
                // Power management
                if (power) {
                    IdPlatformPeripheral.OCR.on(context);
                } else {
                    IdPlatformPeripheral.OCR.off(context);
                }
            }
        });
    }

    @Override
    public void setUp() {
        PowerManager.get().registerListener(this, IdPlatformPeripheral.OCR);
        mrzReader = new MrzScanner(context, this);
    }

    @Override
    public void dispose() {
        mrzReader.close();
        power(false);
        PowerManager.get().unregisterAll();
    }

    @Override
    public Single<String> readData(final long timeoutMs) {
        isReading.set(true);

        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(SingleEmitter<String> e) {
                emitter = e;
                try {
                    String mrz = mrzReader.readMrz(false);

                    emitter.onSuccess(mrz);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean isReading() {
        return isReading.get();
    }

    @Override
    public void stopReading() {
        isReading.set(false);
    }

    //MRZ listener
    @Override
    public void onContinuousReadModeCallback(String s) {
        Timber.d("onContinuousReadModeCallback : %s", s);

        if (s.length() < 88) {
            try {
                mrzReader.setContinuousReadMode(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        emitter.onSuccess(s);
    }

    @Override
    public void onPresenceDetectModeCallback(byte b) {
        Timber.d("Document detected");
    }

    //Power listener
    @Override
    public void onPowerUp(CpcResult.RESULT res, Peripheral peripheral) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Timber.d("onPowerUp");

                while (!mrzReader.open()) {
                    Timber.d("Failed to open");
                    SystemClock.sleep(100);
                }

                //listener.onPowered(true);
                powerEmitter.onSuccess(true);
            }
        }).start();
    }

    @Override
    public void onPowerDown(CpcResult.RESULT res, Peripheral peripheral) {
        powerEmitter.onSuccess(true);
    }

    private void onError(Throwable throwable) {
        if (!emitter.isDisposed()) {
            emitter.onError(throwable);
        }
    }
}

