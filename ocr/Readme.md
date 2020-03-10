OCR
===

**create interactor**

```kotlin
ConePeripheral.OCR_ACCESSIS_AI310E_USB.on(context)
interactor = AccessIsInteractor(context)
```

**use it**

```kotlin
interactor.listen().subscribe({ data ->
    Timber.d(data)
},{t ->
    // Oups, there is an error
},{
    // Should never be called
})
```
