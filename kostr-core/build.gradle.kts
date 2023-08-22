import org.jetbrains.kotlin.gradle.tasks.KotlinNativeCompile

val kotlinVersion = "1.9.0"
val ktorVersion = "2.3.2"

plugins {
    //`java-library`
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    `maven-publish`
}



kotlin {
    //explicitApi()

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
            testLogging {
                events("passed", "skipped", "failed")
            }
        }
    }

    linuxX64("linux") {

    }





    sourceSets {
        val commonMain by getting {
            dependencies {
                //Ktor
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-websockets:$ktorVersion")

                //Kotlin base
                implementation("org.jetbrains.kotlin:kotlin-stdlib:${kotlinVersion}")
                implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
                implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")

                //Secp256k1-kmp
                implementation("fr.acinq.secp256k1:secp256k1-kmp:0.10.1")

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
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))

            }
        }

        val jvmMain by getting {
            dependencies {
                //implementation("fr.acinq.secp256k1:secp256k1-kmp-jvm:0.6.4")
                implementation("fr.acinq.secp256k1:secp256k1-kmp-jni-jvm:0.10.1")

                implementation("com.squareup.okhttp3:okhttp:4.10.0")
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
            }
        }

        val jvmTest by getting {

            dependencies {
                implementation(kotlin("test-junit5"))


                implementation("org.junit.jupiter:junit-jupiter:5.8.2")
                implementation("org.junit.jupiter:junit-jupiter-params:5.8.2")
                implementation("org.assertj:assertj-core:3.23.1")
                runtimeOnly("fr.acinq.secp256k1:secp256k1-kmp-jni-jvm-linux:0.10.1")
                runtimeOnly("fr.acinq.secp256k1:secp256k1-kmp-jni-jvm-linux:0.10.1")
                runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
                runtimeOnly("org.junit.vintage:junit-vintage-engine:5.8.2")
            }
        }

        val linuxMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-cio:$ktorVersion")
                implementation("io.ktor:ktor-client-curl:$ktorVersion")
            }
        }
        val linuxTest by getting
    }
}

//tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>() {
//    kotlinOptions.jvmTarget = "11"
//}

tasks.withType<KotlinNativeCompile>().configureEach {
    compilerOptions.freeCompilerArgs.add("-opt-in=kotlinx.cinterop.ExperimentalForeignApi")
}



publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.parent?.group.toString()
            artifactId = project.name
            version = project.parent?.version.toString()
            //artifact(tasks.kotlinSourcesJar)
            from(components["kotlin"])
        }
    }
}
