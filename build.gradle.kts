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


    group = "tk.anonymousgeek"
    version = "1.0-SNAPSHOT"
}

