OCR
===

## create interactor

### C-One

```kotlin
ConePeripheral.OCR_ACCESSIS_AI310E_USB.on(context)
interactor = AccessIsInteractor(context)
```

### ID-Platform

> Please add CpcCore > 1.10 dependency for power management

**build.gradle**

```groovy
dependencies {
    implementation 'fr.coppernic.sdk.core:CpcCore:1.10.2'
}
```

**MyCLass.kt**

```kotlin
IdPlatformPeripheral.OCR.on(context)
interactor = ElyctisInteractor(context)
```

## use it

```kotlin
val interactor = OcrInteractorBuilder.build(context)
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

## disposing ressources

Do not forget to dispose reader via `Disposable` object gotten from RxJava.
