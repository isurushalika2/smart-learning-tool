plugins {
    id("java")
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "org.example"
version = "0.1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_24
    targetCompatibility = JavaVersion.VERSION_24
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // For reactive WebClient to call AI providers if desired
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // MongoDB persistence (activates when spring.data.mongodb.uri configured)
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.release.set(24)
}

springBoot {
    mainClass.set("org.learningtool.LearningToolApplication")
}