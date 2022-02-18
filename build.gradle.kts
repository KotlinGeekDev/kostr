buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {


        classpath("com.android.tools.build:gradle:7.0.4")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")

    }
}

plugins {
    kotlin("jvm") version "1.5.31"
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>() {
        kotlinOptions.jvmTarget = "1.8"
    }

    group = "tk.anonymousgeek"
    version = "1.0-SNAPSHOT"
}
dependencies {
   // implementation("junit:junit:4.13.1")
}

//tasks.register<Delete>("clean"){
//    delete(rootProject.buildDir)
//}

repositories {
    mavenCentral()
}
