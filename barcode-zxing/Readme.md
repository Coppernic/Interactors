Barcode Generator
=================

```kotlin
val bmp = BarcodeGenerator().generate("123456",
                                      BarcodeFormat.QR_CODE,
                                      100,
                                      100)

bmp.width shouldEqualTo 100
bmp.height shouldEqualTo 100
```
