
val kotlinVersion = "1.7.10"
val jacksonVersion = "2.14.2"
val ktorVersion = "2.3.0"

plugins {
    `java-library`
    kotlin("jvm") version "1.7.10"
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

    //Ktor
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-websockets:$ktorVersion")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:${jacksonVersion}")
    implementation("fr.acinq.secp256k1:secp256k1-kmp:0.7.1")
    //implementation("fr.acinq.secp256k1:secp256k1-kmp-jvm:0.6.4")
    implementation("fr.acinq.secp256k1:secp256k1-kmp-jni-jvm:0.7.1")



    // implementation("com.squareup.moshi:moshi:1.13.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${jacksonVersion}")
    implementation("org.jetbrains.kotlin:kotlin-test-junit:${kotlinVersion}")


    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.2")
    testImplementation("org.assertj:assertj-core:3.23.1")
    runtimeOnly("fr.acinq.secp256k1:secp256k1-kmp-jni-jvm-linux:0.7.1")
    testRuntimeOnly("fr.acinq.secp256k1:secp256k1-kmp-jni-jvm-linux:0.7.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.8.2")
}



tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
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
