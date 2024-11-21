import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeCompile

val kotlinVersion = "2.0.0"
val ktorVersion = "3.0.1"
val kotlinCryptoVersion = "0.3.1"
val junitJupiterVersion = "5.10.1"

plugins {
    `java-library`
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    `maven-publish`
}


kotlin {
    //explicitApi()

    jvm {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_17)

        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
            testLogging {
                events("passed", "skipped", "failed")
            }
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
//        }
    }

    //Apple targets
    macosX64()
    macosArm64()
    iosX64()
    iosArm64()
    iosSimulatorArm64()


    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            //Ktor
            implementation("io.ktor:ktor-client-core:$ktorVersion")
            implementation("io.ktor:ktor-client-websockets:$ktorVersion")
            implementation("io.ktor:ktor-client-logging:$ktorVersion")

            //Kotlin base
            implementation("org.jetbrains.kotlin:kotlin-stdlib:${kotlinVersion}")
            implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")

            //Crypto(Secp256k1-utils, SecureRandom, Hashing, etc.)
            implementation("fr.acinq.secp256k1:secp256k1-kmp:0.15.0")
            implementation("dev.whyoleg.cryptography:cryptography-core:$kotlinCryptoVersion")
            implementation("dev.whyoleg.cryptography:cryptography-random:$kotlinCryptoVersion")

            //Serialization
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
            //Coroutines
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
            //Atomics
            implementation("org.jetbrains.kotlinx:atomicfu:0.25.0")
            //Date-time
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
            //UUID
            implementation("com.benasher44:uuid:0.8.4")
        }

        commonTest.dependencies {
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
        }

        jvmMain.dependencies {
            //implementation("fr.acinq.secp256k1:secp256k1-kmp-jvm:0.6.4")
            implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
            implementation("fr.acinq.secp256k1:secp256k1-kmp-jni-jvm:0.15.0")
            implementation("dev.whyoleg.cryptography:cryptography-provider-jdk:$kotlinCryptoVersion")

            implementation("com.squareup.okhttp3:okhttp:4.12.0")
            implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
            implementation("ch.qos.logback:logback-classic:1.4.14")
        }

        jvmTest.dependencies {
            implementation(kotlin("test-junit5"))

            implementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
            implementation("org.junit.jupiter:junit-jupiter-params:$junitJupiterVersion")
            implementation("org.assertj:assertj-core:3.23.1")
            runtimeOnly("fr.acinq.secp256k1:secp256k1-kmp-jni-jvm-linux:0.15.0")
            runtimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
            runtimeOnly("org.junit.vintage:junit-vintage-engine:$junitJupiterVersion")
        }

        linuxMain.dependencies {
            implementation("io.ktor:ktor-client-cio:$ktorVersion")
            //implementation("io.ktor:ktor-client-curl:$ktorVersion")
            implementation("dev.whyoleg.cryptography:cryptography-provider-openssl3-prebuilt:$kotlinCryptoVersion")
        }

        linuxTest.dependencies {

        }

        appleMain.dependencies {
            implementation("io.ktor:ktor-client-darwin:$ktorVersion")
            implementation("dev.whyoleg.cryptography:cryptography-provider-apple:$kotlinCryptoVersion")
        }
        macosMain.get().dependsOn(appleMain.get())
        iosMain.get().dependsOn(appleMain.get())

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


val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

publishing {
    publications.withType<MavenPublication>(){
        artifact(javadocJar)
    }
//    publications {
//        project.sourceSets.forEach { sourceSet ->
//            withType<MavenPublication> {
//                groupId = project.parent?.group.toString()
//                artifactId = sourceSet.name
//                version = project.parent?.version.toString()
//                artifact(tasks.jar)
//                //from(components["kotlin"])
//            }
//        }
//    }
}
