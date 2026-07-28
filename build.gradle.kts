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

buildscript {

    val kotlinVersion = "2.1.10"

    dependencies {
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
        classpath("com.android.tools.build:gradle:8.7.3")
        classpath(kotlin("serialization", version = kotlinVersion))
    }

}

plugins {
    kotlin("multiplatform") version "2.1.10" apply false
    id("com.android.library") version "8.7.3" apply false
    id("org.jetbrains.kotlinx.atomicfu") version "0.27.0"
    id("com.vanniktech.maven.publish") version "0.30.0"

}


allprojects {
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
    apply(plugin = "com.vanniktech.maven.publish")

    val isJitpack = System.getenv("JITPACK") == "true"

    group = "io.github.kotlingeekdev"
    version = "1.0-beta-19"

    mavenPublishing {
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
}


