plugins {
    `maven-publish`
}

buildscript {

    repositories {
        google()
        mavenCentral()
    }

    dependencies {

        classpath("com.android.tools.build:gradle:7.0.4")
        //classpath("")

    }

}


allprojects {
    repositories {
        google()
        mavenCentral()
    }

    group = "com.github.AnonymousGeekDev"
    version = "0.1-pre-alpha-2"

}

subprojects {
    apply(plugin = "maven-publish")


    publishing {
        publications {
            create<MavenPublication>("maven"){
                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()
                //from(project.components["src"])
            }
        }
    }

}


