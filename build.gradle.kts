plugins {
    kotlin("jvm") version "2.2.21"
    application
}

sourceSets {
    main {
        kotlin.srcDir("src")
    }
}

tasks {
    wrapper {
        gradleVersion = "9.2.1"
    }
}

application {
    mainClass.set("MainKt")  // OR your.package.MainKt
}