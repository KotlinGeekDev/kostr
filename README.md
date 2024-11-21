# Kostr

A Kotlin Multiplatform library for working with Nostr, with support for JVM, Android, Linux, MacOS/iOS.

Note: This is still in development and very incomplete.


## What is Nostr?
* An introduction or description of Nostr can be found [here](https://github.com/nostr-protocol/nostr).
* The Nostr protocol specs can be found [here](https://github.com/nostr-protocol/nips).

## How to include the libary
  Inside your root-level `build.gradle(.kts)` file, you should add `jitpack`:
  ``` kotlin
// build.gradle.kts
allprojects {
    repositories {
        // ...
        maven { setUrl("https://jitpack.io") }
    }
    // ...
}
```

or

``` groovy
// build.gradle
allprojects {
    repositories {
        // ...
        maven { url "https://jitpack.io" }
    }
    // ...
}
```

In newer projects, you need to also update the `settings.gradle(.kts)` file's `dependencyResolutionManagement` block:

```
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }  // <--
        jcenter() // Warning: this repository is going to shut down soon
    }
}
```
then, in your module's `build.gradle(.kts)`, you need to add:
```kotlin
// build.gradle.kts
dependencies {
    //...
    implementation("com.github.KotlinGeekDev.kostr:kostr-core:v1.0-beta-04")

}

```

## License

    MIT License
    
    Copyright (c) 2022 KotlinGeekDev
    
    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
