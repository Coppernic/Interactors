package fr.coppernic.lib.interactors.ocr;

import android.content.Context;

import io.reactivex.Single;

public interface OcrInteractor {
    /**
     * Sets up interactor
     */
    void setUp();

    /**
     * Disposes OCR interactor
     */
    void dispose();

    /**
     * Powers on/off OCR reader
     * @param power Power state to be applied
     */
    Single<Boolean> power(Boolean power);

    /**
     * Starts reading data
     * @param timeoutMs Read time out
     * @return String single
     */
    Single<String> readData(long timeoutMs);

    /**
     * Indicates wether or not the OCR reader is reading data
     * @return Reading state
     */
    boolean isReading();

    /**
     * Stops OCR reading
     */
    void stopReading();

    interface Builder {
        OcrInteractor build();
    }

    interface ContextBuilder {
        Builder withContext(Context context);
    }
}
