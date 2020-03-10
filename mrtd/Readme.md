MRTD
====

**powering and interactor instanciation**

```kotlin
InteractorsDefines.setVerbose(true)
interactor = MrtdInteractor()

Timber.v("Powering up")
ConePeripheral.RFID_ELYCTIS_LF214_USB.on(InstrumentationRegistry.getInstrumentation().targetContext)
Timber.v("Powered up")
```


**Listening for data**

```kotlin

val mrz = Mrz("P<UTOTRAVELLER<<JANE<<<<<<<<<<<<<<<<<<<<<<<<00000000<0UTO7804115F131201211041978<<<<<<32")
interactor.listen(activityRule.activity, mrz.key).subscribe({datagroup ->
    // Do something interesting here, like logging
    Timber.d("$dataGroup")
}
},{ t ->
   // Oups, there is an error
}
})

```
