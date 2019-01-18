package fr.coppernic.lib.interactors.ocr;

import android.content.Context;

public class OcrInteractorBuilder implements OcrInteractor.Builder, OcrInteractor.ContextBuilder {

    private Context context;

    public static OcrInteractor.ContextBuilder get() {
        return new OcrInteractorBuilder();
    }

    @Override
    public OcrInteractor build() {
        return new OcrInteractorIdPlatform(context);
    }

    @Override
    public OcrInteractor.Builder withContext(Context context) {
        this.context = context;
        return this;
    }
}
