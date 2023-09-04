plugins {
    kotlin("jvm")
    id("maven-publish")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0-RC")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:unchecked")
    options.isIncremental = true
    options.encoding = "UTF-8"
}

val releaseVersion = "1.0.1"

publishing {
    publications {
        create<MavenPublication>("release") {
            from(components["java"])
            groupId = "com.mcal"
            artifactId = "preferences"
            version = releaseVersion
        }
    }
}
