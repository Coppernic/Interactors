ICLASS
====

**Powering and interactor instanciation**

```kotlin
val context = InstrumentationRegistry.getInstrumentation().targetContext
InteractorsDefines.setVerbose(true)
interactor = IclassInteractor(context)

Timber.v("Powering up")
ConePeripheral.RFID_HID_ICLASSPROX_GPIO.on(context)
Timber.v("Powered up")
```


**Listening for data**

```kotlin

interactor.listen().subscribe({data ->
    // Do something interesting here, like logging
    Timber.d("$data")
}
},{ t ->
   // Oups, there is an error
}
})

```
