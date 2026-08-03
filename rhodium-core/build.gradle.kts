/*
 * MIT License
 *
 * Copyright (c) 2025 KotlinGeekDev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */


import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeSimulatorTest
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.vanniktech.maven.publish)
}

kotlin {
    //explicitApi()
    jvmToolchain(17)

    jvm("commonJvm") {

        compilerOptions.jvmTarget.set(JvmTarget.JVM_17)


        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
            testLogging {
                events("passed", "skipped", "failed")
            }
        }
    }

    android {
        namespace = "io.github.kotlingeekdev.rhodium.android"
        compileSdk = 36
        minSdk = 21
        enableCoreLibraryDesugaring = false

        withJava()
        compilerOptions {
           jvmTarget.set(JvmTarget.JVM_1_8)
        }
        optimization {
            keepRules.files.add(project.file("proguard-rules.pro"))
            consumerKeepRules.files.add(project.file("consumer-rules.pro"))

        }
        withHostTestBuilder {  }.configure {  }
        withDeviceTestBuilder {
//            sourceSetTreeName = "test"
        }


    }


    linuxX64("linux") {
//        compilations.all {
//            cinterops {
//                val libs by creating {
//                    defFile("src/linuxMain/cinterop/libs.def")
//                }
//            }
//        }
//
//        binaries {
//            sharedLib {
//
//            }
//            executable {
//                entryPoint = "main"
//            }
//        }
    }

    //Apple targets
    val macosX64 = macosX64()
    val macosArm64 = macosArm64()
    val iosArm64 = iosArm64()
    val iosX64 = iosX64()
    val iosSimulatorArm64 = iosSimulatorArm64()
    val appleTargets = listOf(
        macosX64, macosArm64,
        iosArm64, iosX64, iosSimulatorArm64,
    )

    appleTargets.forEach { target ->
        with(target) {
            binaries {
                framework {
                    baseName = "Rhodium"
                }
            }
        }
    }


    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            //Ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.websockets)
            implementation(libs.ktor.client.logging)

            //Kotlin base
            implementation(libs.kotlin.stdlib)
            implementation(libs.kotlin.reflect)

            //Crypto(Secp256k1-utils, SecureRandom, Hashing, etc.)
            implementation(libs.secp256k1.kmp)
            implementation(libs.whyoleg.cryptography.core)
            implementation(libs.whyoleg.cryptography.random)

            //Serialization
            implementation(libs.kotlinx.serialization.json)
            //Coroutines
            implementation(libs.kotlinx.coroutines.core)
            //Atomics
            implementation(libs.atomicfu)
            //Date-time
            implementation(libs.kotlinx.datetime)
            //UUID
            implementation(libs.benasher.uuid)
            //ByteBuffer(until a kotlinx-io replacement appears)
            implementation(libs.kmp.bytebuffer)
            //Logging
            implementation(libs.kermit)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test.common)
            implementation(libs.kotlin.test.annotations.common)
            implementation(libs.kotlinx.coroutines.test)
        }

        val commonJvmMain by getting {

            dependencies {
                implementation(libs.whyoleg.cryptography.provider.jdk)

                implementation(libs.okhttp)
                implementation(libs.ktor.client.okhttp)
            }
        }

        val commonJvmTest by getting {

            dependencies {
                implementation(libs.kotlin.test.junit5)

                implementation(libs.junit.jupiter)
                implementation(libs.junit.jupiter.params)
                implementation(libs.assertj.core)
                runtimeOnly(libs.secp256k1.kmp.jni.jvm.linux)
                runtimeOnly(libs.junit.jupiter.engine)
                runtimeOnly(libs.junit.vintage.engine)
            }
        }



        androidMain.configure {

            dependencies {
                implementation(libs.appcompat)
                //        coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.3")
                implementation(libs.whyoleg.cryptography.provider.jdk)
                implementation(libs.okhttp)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.secp256k1.kmp.jni.android)
            }
        }

        getByName("androidHostTest") {
            dependsOn(commonJvmTest)
            dependencies {
                implementation(libs.junit)
            }
        }

        getByName("androidDeviceTest") {
            dependsOn(commonJvmTest)
            dependencies {
                implementation(libs.ext.junit)
                implementation(libs.espresso.core)
            }
        }

        linuxMain.configure {
            dependencies {
//                implementation("io.ktor:ktor-client-cio:$ktorVersion")
                implementation(libs.ktor.client.curl)
                implementation(libs.whyoleg.cryptography.provider.openssl3.prebuilt)
            }
        }

        linuxTest.configure {
            dependencies {

            }

        }

        appleMain.configure {
            dependencies {
                implementation(libs.ktor.client.darwin)
                implementation(libs.whyoleg.cryptography.provider.apple)
            }
        }

    }
}


tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>() {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

tasks.withType<KotlinNativeCompile>().configureEach {
    compilerOptions.freeCompilerArgs.add("-opt-in=kotlinx.cinterop.ExperimentalForeignApi")
}


val deviceName = project.findProperty("iosDevice") as? String ?: "iPhone 17"

tasks.register<Exec>("bootIOSSimulator") {
    onlyIf { org.gradle.internal.os.OperatingSystem.current().isMacOsX }
    isIgnoreExitValue = true
    commandLine("xcrun", "simctl", "boot", deviceName)

    doLast {
        val result = executionResult.get()
        if (result.exitValue != 148 && result.exitValue != 149) { // ignoring device already booted errors
            println(errorOutput.toString())
            result.assertNormalExitValue()
        }
    }
}

tasks.withType<KotlinNativeSimulatorTest>().configureEach {
    dependsOn("bootIOSSimulator")
    standalone.set(false)
    device.set(deviceName)

}

tasks.register<Exec>("shutdownSimulator") {
    onlyIf { org.gradle.internal.os.OperatingSystem.current().isMacOsX }
    val allSimulatorTests = tasks.withType<KotlinNativeSimulatorTest>()
    if (allSimulatorTests.all { task -> task.state.failure == null }) {
        commandLine("xcrun", "simctl", "shutdown", "booted")
    }

}

tasks.named { it.contains("ios") && it.contains("Test") }.configureEach {
    finalizedBy("shutdownSimulator")
}

mavenPublishing {
    val isJitpack = System.getenv("JITPACK") == "true"

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    if (!isJitpack){
        signAllPublications()
    }


    coordinates(group.toString(), "rhodium", version.toString())

//        configure(KotlinMultiplatform(
//            javadocJar = JavadocJar.Javadoc(),
//            sourcesJar = true
//        ))

    pom {
        name = "Rhodium"
        description = " A Kotlin Multiplatform library for Nostr"
        url = "https://github.com/KotlinGeekDev/Rhodium"

        licenses {
            license {
                name = "The MIT License"
                url = "https://opensource.org/license/MIT"
                distribution = "https://opensource.org/license/MIT"
            }
        }

        developers {
            developer {
                name = "KotlinGeekDev"
                email = "kotlingeek@protonmail.com"
                url = "https://github.com/KotlinGeekDev"
            }
        }

        scm {
            connection = "scm:git:git://github.com/KotlinGeekDev/Rhodium.git"
            url = "https://github.com/KotlinGeekDev/Rhodium"

        }
    }
}
