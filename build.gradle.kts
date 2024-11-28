buildscript {

    val kotlinVersion = "2.0.0"

    dependencies {
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
        classpath("com.android.tools.build:gradle:8.2.2")
        classpath(kotlin("serialization", version = kotlinVersion))
    }

}

plugins {
    id("com.android.library") version "8.2.2" apply false
    id("org.jetbrains.kotlinx.atomicfu") version "0.25.0"

}


allprojects {
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

    group = "com.github.KotlinGeekDev"
    version = "1.0-beta-05"

}


