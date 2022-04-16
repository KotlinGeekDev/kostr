
plugins {
    `java-library`
   kotlin("jvm") version "1.5.31"

}

group = "kt.nostr"
version = "1.0-SNAPSHOT"

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}


dependencies {

      implementation("fr.acinq.secp256k1:secp256k1-kmp-jvm:0.6.2")
    //  implementation("fr.acinq.secp256k1:secp256k1-kmp-jni-common:0.6.1")
    //implementation("fr.acinq.secp256k1:secp256k1-kmp-jni-jvm-extract:0.6.2")
    //implementation("org.junit.jupiter:junit-jupiter:5.8.2")
    // implementation("fr.acinq.secp256k1:secp256k1-kmp-jni-jvm:0.6.1")

    implementation(kotlin("stdlib-jdk8"))
   // implementation("com.squareup.moshi:moshi:1.13.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testRuntimeOnly("fr.acinq.secp256k1:secp256k1-kmp-jni-jvm-linux:0.6.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")

}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}