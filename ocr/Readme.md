OCR
===

**create interactor**

```kotlin
ConePeripheral.OCR_ACCESSIS_AI310E_USB.on(context)
interactor = AccessIsInteractor(context)
```

**use it**

```kotlin
val disposable = interactor.listen().subscribe({ data ->
    Timber.d(data)
},{t ->
    // Oups, there is an error
},{
    // Should never be called
})

// When you are done with OCR reader
disposable.dispose() 
```

**disposing ressources**

Do not forget to dispose reader via `Disposable` object gotten from RxJava.
