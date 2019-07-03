[![Download](https://api.bintray.com/packages/coppernic/maven/Interactors/images/download.svg) ](https://bintray.com/coppernic/maven/Interactors/_latestVersion)
[![Build Status](https://travis-ci.org/Coppernic/Interactors.svg?branch=master)](https://travis-ci.org/Coppernic/Interactors)

# Interactors
Interactors implementation for Coppernic's products peripherals

## Set Up


```groovy
repositories {
    maven { url 'https://dl.bintray.com/coppernic/maven/'}
    maven { url 'https://artifactory.coppernic.fr/artifactory/libs-release'}
}

dependencies {
    implementation 'fr.coppernic.lib:interactors:0.0.4'
}
```

## Presentation

Lib that implements interactor pattern for Coppernic's devices peripherals.

It brings default interactor class to facilitate integration of coppernic devices. These classes aim to add very simple functionality.
To perform more advanced tasks, then please implements your reader interface with readers libraries. More info
[here](http://coppernic.github.io)

## Barcode

- Create Barcode interactor class

```java
BarcodeInteractor interactor = new BarcodeInteractor(context);
```

This class is eligible for dependency injection with dagger. It needs an AndroidContext module.

- start listening to barcode events

```java
interactor.listen().subscribe(new Observer<String>() {
                                              @Override
                                              public void onSubscribe(Disposable d) {
                                                  // Use this disposable to dispose interactor when done
                                              }

                                              @Override
                                              public void onNext(String s) {
                                                  // Get the string read by barcode reader
                                              }

                                              @Override
                                              public void onError(Throwable e) {
                                                  // Receives errors, including timeouts
                                              }

                                              @Override
                                              public void onComplete() {
                                                  // Should never be called
                                              }
                                          });
```

- To ease timeout handling, it is advised to add a retry observable.

```java
        interactor.listen()
            .retry(new TimeoutRetryPredicate())
            .subscribe();
```

- To trig a read, just call trig ;-)

```java
interactor.trig();
```