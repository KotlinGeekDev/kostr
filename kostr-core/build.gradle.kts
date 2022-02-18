plugins {
    `java-library`
   id("kotlin")

}

group = "kt.nostr"
version = "1.0-SNAPSHOT"



//java {
//    sourceCompatibility = JavaVersion.VERSION_1_8
//    targetCompatibility = JavaVersion.VERSION_1_8
//}

dependencies {
    //  implementation("fr.acinq.secp256k1:secp256k1-kmp-jvm:0.6.1")
    //  implementation("fr.acinq.secp256k1:secp256k1-kmp-jni-common:0.6.1")
    implementation("fr.acinq.secp256k1:secp256k1-kmp-jni-jvm-linux:0.6.2")
    implementation("fr.acinq.secp256k1:secp256k1-kmp-jni-jvm-extract:0.6.2")
    implementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    // implementation("fr.acinq.secp256k1:secp256k1-kmp-jni-jvm:0.6.1")

    implementation(kotlin("stdlib-jdk8"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}