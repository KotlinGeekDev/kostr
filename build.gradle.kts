buildscript {

    repositories {
        google()
        mavenCentral()
    }

    dependencies {

        classpath("com.android.tools.build:gradle:8.0.2")

    }

}


allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }

    group = "com.github.AnonymousGeekDev"
    version = "0.1-pre-alpha-9"

}


