import org.jetbrains.kotlin.gradle.tasks.KotlinNativeCompile

val kotlinVersion = "1.9.20"
val ktorVersion = "2.3.2"

plugins {
    `java-library`
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    `maven-publish`
}



kotlin {
    //explicitApi()

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "17"
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
            testLogging {
                events("passed", "skipped", "failed")
            }
        }
    }


    linuxX64("linux") {
        compilations.all {
            cinterops {
                val libs by creating {
                    defFile("src/linuxMain/cinterop/libs.def")
                }
            }
        }
        binaries {
            executable()
        }
    }


    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            //Ktor
            implementation("io.ktor:ktor-client-core:$ktorVersion")
            implementation("io.ktor:ktor-client-websockets:$ktorVersion")

            //Kotlin base
            implementation("org.jetbrains.kotlin:kotlin-stdlib:${kotlinVersion}")
            implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
            implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")

            //Crypto(Secp256k1-utils, SecureRandom, Hashing, etc.)
            implementation("fr.acinq.secp256k1:secp256k1-kmp:0.10.1")
            implementation("dev.whyoleg.cryptography:cryptography-core:0.2.0")
            implementation("dev.whyoleg.cryptography:cryptography-random:0.2.0")

            //Serialization
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
            //Coroutines
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            //Atomics
            implementation("org.jetbrains.kotlinx:atomicfu:0.22.0")
            //Date-time
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
            //UUID
            implementation("com.benasher44:uuid:0.8.0")
        }

        commonTest.dependencies {
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
        }

        jvmMain.dependencies {
            //implementation("fr.acinq.secp256k1:secp256k1-kmp-jvm:0.6.4")
            implementation("fr.acinq.secp256k1:secp256k1-kmp-jni-jvm:0.10.1")
            implementation("dev.whyoleg.cryptography:cryptography-provider-jdk:0.2.0")

            implementation("com.squareup.okhttp3:okhttp:4.11.0")
            implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
        }

        jvmTest.dependencies {
            implementation(kotlin("test-junit5"))

            implementation("org.junit.jupiter:junit-jupiter:5.8.2")
            implementation("org.junit.jupiter:junit-jupiter-params:5.8.2")
            implementation("org.assertj:assertj-core:3.23.1")
            runtimeOnly("fr.acinq.secp256k1:secp256k1-kmp-jni-jvm-linux:0.10.1")
            runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
            runtimeOnly("org.junit.vintage:junit-vintage-engine:5.8.2")
        }

        linuxMain.dependencies {
            implementation("io.ktor:ktor-client-cio:$ktorVersion")
            implementation("io.ktor:ktor-client-curl:$ktorVersion")
            implementation("dev.whyoleg.cryptography:cryptography-provider-openssl3-prebuilt:0.2.0")
        }

        linuxTest.dependencies {

        }

    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>() {
    kotlinOptions.jvmTarget = "17"
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
