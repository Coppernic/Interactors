package fr.coppernic.lib.interactors.barcode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import fr.coppernic.lib.interactors.robolectric.RobolectricTest;
import io.reactivex.disposables.Disposable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

public class BarcodeInteractorTest extends RobolectricTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    private BarcodeInteractor interactor;
    @Mock
    private Context context;

    @Before
    public void setUp() throws Exception {
        interactor = new BarcodeInteractor(context);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void dispose() {
        Disposable d = interactor.listen().subscribe();
        verify(context).registerReceiver(any(BroadcastReceiver.class), any(IntentFilter.class));
        d.dispose();
        verify(context).unregisterReceiver(any(BroadcastReceiver.class));
    }
}