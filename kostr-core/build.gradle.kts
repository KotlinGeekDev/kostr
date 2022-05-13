
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

    implementation("com.tinder.scarlet:scarlet:0.1.12")
    implementation("com.tinder.scarlet:websocket-okhttp:0.1.12")
    implementation("com.tinder.scarlet:stream-adapter-built-in:0.1.12")
    implementation("com.tinder.scarlet:message-adapter-jackson:0.1.12")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
      implementation("fr.acinq.secp256k1:secp256k1-kmp-jvm:0.6.4")
    //  implementation("fr.acinq.secp256k1:secp256k1-kmp-jni-common:0.6.1")
    //implementation("fr.acinq.secp256k1:secp256k1-kmp-jni-jvm-extract:0.6.2")
    //implementation("org.junit.jupiter:junit-jupiter:5.8.2")
    // implementation("fr.acinq.secp256k1:secp256k1-kmp-jni-jvm:0.6.1")

    implementation(kotlin("stdlib"))
   // implementation("com.squareup.moshi:moshi:1.13.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    runtimeOnly("fr.acinq.secp256k1:secp256k1-kmp-jni-jvm-linux:0.6.4")
    testRuntimeOnly("fr.acinq.secp256k1:secp256k1-kmp-jni-jvm-linux:0.6.4")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")

}



tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.create<org.gradle.jvm.tasks.Jar>("libSourcesJar"){
    classifier = "sources"
    from(sourceSets.main)
}


publishing {
    publications {
        create<MavenPublication>("maven"){
            groupId = project.parent?.group.toString()
            artifactId = project.name
            version = project.parent?.version.toString()
            artifact("libSourcesJar")
            artifact(tasks.kotlinSourcesJar)
            from(components["kotlin"])
        }
    }
}