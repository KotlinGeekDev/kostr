//plugins {
//    `maven-publish`
//}

buildscript {

    repositories {
        google()
        mavenCentral()
    }

    dependencies {

        classpath("com.android.tools.build:gradle:7.2.2")
        //classpath(kotlin("stdlib", "1.5.31"))

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


