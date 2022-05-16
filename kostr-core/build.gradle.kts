
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

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${properties["version.kotlin"]}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${properties["version.kotlin"]}")
    implementation("com.tinder.scarlet:scarlet:${properties["version.scarlet"]}")
    implementation("com.tinder.scarlet:websocket-okhttp:${properties["version.scarlet"]}")
    implementation("com.tinder.scarlet:stream-adapter-built-in:${properties["version.scarlet"]}")
    implementation("com.tinder.scarlet:message-adapter-jackson:${properties["version.scarlet"]}")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.fasterxml.jackson.core:jackson-databind:${properties["version.jackson"]}")
      implementation("fr.acinq.secp256k1:secp256k1-kmp-jvm:0.6.4")
    //  implementation("fr.acinq.secp256k1:secp256k1-kmp-jni-common:0.6.1")
    //implementation("fr.acinq.secp256k1:secp256k1-kmp-jni-jvm-extract:0.6.2")
    //implementation("org.junit.jupiter:junit-jupiter:5.8.2")
    // implementation("fr.acinq.secp256k1:secp256k1-kmp-jni-jvm:0.6.1")

    implementation(kotlin("stdlib"))
   // implementation("com.squareup.moshi:moshi:1.13.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${properties["version.jackson"]}")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    runtimeOnly("fr.acinq.secp256k1:secp256k1-kmp-jni-jvm-linux:0.6.4")
    testRuntimeOnly("fr.acinq.secp256k1:secp256k1-kmp-jni-jvm-linux:0.6.4")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")

}



tasks.getByName<Test>("test") {
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