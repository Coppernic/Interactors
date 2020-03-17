[![Download](https://api.bintray.com/packages/coppernic/maven/Interactors/images/download.svg) ](https://bintray.com/coppernic/maven/Interactors/_latestVersion)
[![Build Status](https://travis-ci.org/Coppernic/Interactors.svg?branch=master)](https://travis-ci.org/Coppernic/Interactors)

# Interactors

Interactors implementation for Coppernic's products peripherals.

Each interactor is an adaptation to Coppernic's API and SDK with RxJava. It brings default interactor class to facilitate
integration of coppernic devices. These classes aim to add very simple functionality. To perform more advanced tasks,
then please implements your reader interface with readers libraries.More info on Coppernic's SDK on
[developer.coppernic.fr](https://developer.coppernic.fr)

There are several libraries available. If interactor does not need any special dependency, then it will be implemented in `interactor`
general lib. Otherwise, it will have its own library with its specific dependency.

## Set Up


```groovy
repositories {
    maven { url 'https://dl.bintray.com/coppernic/maven/'}
    maven { url 'https://artifactory.coppernic.fr/artifactory/libs-release'}
}
```

Each interactor dependency will be like this one :


``` groovy
dependencies {
    implementation 'fr.coppernic.lib:interactors:0.3.2'
}
```

## Presentation

### Barcode, Agrident and Picture interactors

**dependency**

``` groovy
dependencies {
    implementation 'fr.coppernic.lib:interactors:0.3.2'
}
```

**Documentation**

- [Readme](https://github.com/Coppernic/Interactors/tree/master/interactors)

### Barcode generator interactor

**dependency**

``` groovy
dependencies {
    implementation 'fr.coppernic.lib.interactors:barcodegenerator:0.3.2'
}
```

**Documentation**

- [Readme](https://github.com/Coppernic/Interactors/tree/master/barcodegenerator)


### MRTD Interactor

**dependency**

``` groovy
dependencies {
    implementation 'fr.coppernic.lib.interactors:mrtd:0.3.2'
}
```

**Documentation**

- [Readme](https://github.com/Coppernic/Interactors/tree/master/mrtd)


### OCR Interactor

**dependency**

``` groovy
dependencies {
    implementation 'fr.coppernic.lib.interactors:ocr:0.3.2'
}
```

**Documentation**

- [Readme](https://github.com/Coppernic/Interactors/tree/master/ocr)


### ICLASS Interactor

**dependency**

``` groovy
dependencies {
    implementation 'fr.coppernic.lib.interactors:iclass:0.3.3'
}
```

**Documentation**

- [Readme](https://github.com/Coppernic/Interactors/tree/master/iclass)


## License

    Copyright (C) 2020 Coppernic

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
