package fr.coppernic.lib.interactors.barcode;

import android.support.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import fr.coppernic.lib.interactors.BuildConfig;
import fr.coppernic.lib.interactors.TestBase;
import fr.coppernic.sdk.utils.debug.L;
import io.reactivex.Notification;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.TestObserver;
import timber.log.Timber;

public class BarcodeInteractorAndroidTest extends TestBase {

    private static final String TAG = "BarcodeInteractorTest";
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private BarcodeInteractor interactor;

    @Before
    public void setUp() throws Exception {
        interactor = new BarcodeInteractor(InstrumentationRegistry.getTargetContext());

        Timber.plant(new Timber.DebugTree());
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void listenEmpty() {
        final TestObserver<String> observer = interactor.listen().test();

        observer.assertValueCount(0);
    }

    @Test(timeout = 15000)
    public void listen() {
        TestObserver<String> observer = new TestObserver<>();

        Timber.d("listen");
        interactor.listen()
            .doOnEach(new Consumer<Notification<String>>() {
                @Override
                public void accept(Notification<String> stringNotification) throws Exception {
                    L.mt(TAG, DEBUG, stringNotification.toString());
                    unblock();
                }
            })
            .subscribe(observer);

        Timber.d("trig");
        interactor.trig();

        block();

        Timber.d("assert");
        observer.assertValueCount(1);
        observer.assertNoErrors();

        observer.dispose();
    }

    @Test
    public void retry() {
        int nbRetry = 3;

        TestObserver<String> observer = new TestObserver<>();
        Timber.d("listen");
        interactor.listen()
            // Catch error before retry
            .doOnEach(new Consumer<Notification<String>>() {
                @Override
                public void accept(Notification<String> stringNotification) throws Exception {
                    L.mt(TAG, DEBUG, stringNotification.toString());
                    unblockIn(1, TimeUnit.SECONDS);
                }
            })
            // subscribe again in case of error
            .retry(nbRetry)
            .subscribe(observer);

        for (int i = 0; i < nbRetry; i++) {
            Timber.d("trig");
            interactor.trig();
            block(5, TimeUnit.SECONDS);
        }

        Timber.d("assert");
        observer.assertNoErrors();
        observer.dispose();
    }


}