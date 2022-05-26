 val scarletVersion = "0.1.12"
 val kotlinVersion = "1.5.31"
 val jacksonVersion = "2.13.3"

plugins {
    `java-library`
   kotlin("jvm") version "1.5.31"
    `maven-publish`

}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

dependencies {

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    implementation("com.tinder.scarlet:scarlet:${scarletVersion}")
    implementation("com.tinder.scarlet:websocket-okhttp:${scarletVersion}")
    implementation("com.tinder.scarlet:stream-adapter-built-in:${scarletVersion}")
    implementation("com.tinder.scarlet:message-adapter-jackson:${scarletVersion}")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.fasterxml.jackson.core:jackson-databind:${jacksonVersion}")
      implementation("fr.acinq.secp256k1:secp256k1-kmp-jvm:0.6.4")
    //  implementation("fr.acinq.secp256k1:secp256k1-kmp-jni-common:0.6.1")
    //implementation("fr.acinq.secp256k1:secp256k1-kmp-jni-jvm-extract:0.6.2")
    //implementation("org.junit.jupiter:junit-jupiter:5.8.2")
    // implementation("fr.acinq.secp256k1:secp256k1-kmp-jni-jvm:0.6.1")

    implementation(kotlin("stdlib"))
   // implementation("com.squareup.moshi:moshi:1.13.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${jacksonVersion}")
    implementation("org.jetbrains.kotlin:kotlin-test-junit:${kotlinVersion}")
    testImplementation("junit:junit:4.13.2")


    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("org.assertj:assertj-core:3.22.0")
    runtimeOnly("fr.acinq.secp256k1:secp256k1-kmp-jni-jvm-linux:0.6.4")
    testRuntimeOnly("fr.acinq.secp256k1:secp256k1-kmp-jni-jvm-linux:0.6.4")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.8.2")

}



tasks.test {
    useJUnitPlatform()
}


publishing {
    publications {
        create<MavenPublication>("maven"){
            groupId = project.parent?.group.toString()
            artifactId = project.name
            version = project.parent?.version.toString()
            //artifact(tasks.kotlinSourcesJar)
            from(components["kotlin"])
        }
    }
}