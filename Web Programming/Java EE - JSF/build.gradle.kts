plugins {
    id("java")
    id("war")
}

group = "mitya.haha"
version = "1.1"


val springframeworkVersion = "6.0.13"
val springsecurityVersion = "6.2.0"
val javaVersion = "17"
val nodeVersion = "v18.17.0"
val npmVersion = "9.6.7"


repositories {
    mavenCentral()
}

dependencies {
    compileOnly("jakarta.platform:jakarta.jakartaee-web-api:9.1.0")
    implementation("org.springframework:spring-context:$springframeworkVersion")
    implementation("org.springframework:spring-web:$springframeworkVersion")
    implementation("org.springframework:spring-webmvc:$springframeworkVersion")

    implementation(platform("org.springframework.data:spring-data-bom:2023.1.0"))
    implementation("org.springframework.data:spring-data-jpa:")

    implementation(platform("org.springframework.security:spring-security-bom:$springsecurityVersion"))
    implementation("org.springframework.security:spring-security-web:")
    implementation("org.springframework.security:spring-security-config:")

    implementation("org.springframework.security.oauth:spring-security-oauth2:2.5.2.RELEASE")
    implementation("org.springframework.security:spring-security-oauth2-authorization-server:1.2.0")

    implementation("org.hibernate.orm:hibernate-core:6.3.1.Final")

    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")


    // testImplementation(platform("org.junit:junit-bom:5.9.1"))
   // testImplementation("org.junit.jupiter:junit-jupiter")
}


tasks.test {
    useJUnitPlatform()
}

tasks.processResources {
    dependsOn("copyFrontendToBuild")
}

tasks.register<Copy>("copyFrontendToBuild"){
    dependsOn("npmBuild")
    from("$projectDir/src/frontend/dist/")
    into ("$buildDir/resources/main/static/")
}

tasks.register<Exec>("npmBuild"){
    workingDir("$projectDir/src/frontend/")
    commandLine("npm.cmd", "run", "build")
}
