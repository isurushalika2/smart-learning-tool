plugins {
    id("java")
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "org.example"
version = "0.1.0"

java {
    // Compile to Java 21 bytecode for Spring Framework compatibility
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    toolchain {
        // Use a Java 24 toolchain so developers with only JDK 24 installed can build/run
        languageVersion.set(JavaLanguageVersion.of(24))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Swagger/OpenAPI UI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")

    // For reactive WebClient to call AI providers if desired
    implementation("org.springframework.boot:spring-boot-starter-webflux")


    // AWS SDK v2 for DynamoDB (used when dynamodb.enabled=true)
    implementation("software.amazon.awssdk:dynamodb:2.25.61")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Compile with --release 21 to avoid Java 24 classfile (major 68) incompatibilities with Spring's ASM
tasks.withType<JavaCompile> {
    options.release.set(21)
}

springBoot {
    mainClass.set("org.learningtool.LearningToolApplication")
}