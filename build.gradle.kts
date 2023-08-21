buildscript {

    val kotlinVersion = "1.9.0"
    repositories {
        google()
        mavenCentral()
    }

    dependencies {

        classpath("com.android.tools.build:gradle:8.0.2")
        classpath(kotlin("serialization", version = kotlinVersion))

    }

}


allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }

    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

    group = "com.github.AnonymousGeekDev"
    version = "0.1-pre-alpha-9"

}


