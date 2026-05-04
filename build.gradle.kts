plugins {
    java
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.springdoc.openapi.gradle.plugin)
}

group = "ru.miphi"
version = "0.0.1-SNAPSHOT"
description = "otp-auth-service-hw"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.java.get().toInt())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // web
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.validation)

    // security
    implementation(libs.spring.boot.starter.security)

    // database
    implementation(libs.spring.boot.starter.jdbc)
    implementation(libs.postgresql)
    implementation(libs.liquibase.core)

    // jwt
    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)

    // notifications
    implementation(libs.angus.mail)
    implementation(libs.jsmpp)

    // lombok
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    // openapi
    implementation(libs.springdoc.openapi.starter.webmvc.ui)

    // test
    testImplementation(libs.spring.boot.starter.test)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

openApi {
    apiDocsUrl.set("http://localhost:8080/v3/api-docs.yaml")
    outputDir.set(file("$rootDir/docs"))
    outputFileName.set("openapi.yaml")
    waitTimeInSeconds.set(30)
}
