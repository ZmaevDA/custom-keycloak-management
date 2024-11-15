plugins {
    id("java")
}

group = "ru.zmaev"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.keycloak:keycloak-server-spi:21.1.2")
    implementation("org.keycloak:keycloak-server-spi-private:21.1.2")
    implementation("org.keycloak:keycloak-core:21.1.2")
}

tasks.register<DefaultTask>("buildWithDockerImage") {
    group = "build"
    description = "Build project and docker image"

    dependsOn("build")

    doLast {
        val jarFile = file("build/libs/${project.name}-${project.version}.jar")
        val destinationDir = file("docker/plugin")

        if (!jarFile.exists()) {
            throw GradleException("JAR file not found: $jarFile")
        }

        println("Copy jar to $destinationDir")
        jarFile.copyTo(file("$destinationDir/${jarFile.name}"), overwrite = true)

        println("Building docker image")
        val process = ProcessBuilder("docker", "build", "-t", "custom-keycloak", ".")
            .directory(file("docker"))
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()

        val exitCode = process.waitFor()
        if (exitCode != 0) {
            throw GradleException("Error while building docker image. Error code: $exitCode")
        }

        println("Docker image built with name: custom-keycloak")
    }
}

tasks.test {
    useJUnitPlatform()
}