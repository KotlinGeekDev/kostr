buildscript {

    val kotlinVersion = "1.9.20"
    repositories {
        google()
        mavenCentral()
    }

    dependencies {

        classpath(kotlin("gradle-plugin", version = kotlinVersion))
        classpath("com.android.tools.build:gradle:8.2.0")
        classpath(kotlin("serialization", version = kotlinVersion))
        classpath("org.jetbrains.kotlinx:atomicfu-gradle-plugin:0.23.2")
        classpath("org.jetbrains.kotlin:atomicfu:$kotlinVersion")

    }

}


allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }

    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
    apply(plugin = "kotlinx-atomicfu")

    group = "com.github.KotlinGeekDev"
    version = "1.0-beta-01"

}


