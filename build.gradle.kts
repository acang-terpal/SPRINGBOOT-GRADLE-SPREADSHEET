plugins {
    java
    id("org.springframework.boot") version "4.0.3"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "spreadsheet"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://projectlombok.org") }
}

dependencies {
    // --- SPRING BOOT STARTERS ---
    implementation("org.springframework.boot:spring-boot")
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("org.springframework.boot:spring-boot-starter-web") {
//        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-freemarker")
    }
    implementation("org.springframework.boot:spring-boot-starter-jetty")

    // --- JETTY CORE & COMPRESSION ---
    implementation("org.eclipse.jetty.compression:jetty-compression-server")
    implementation("org.eclipse.jetty:jetty-server")

    // --- LOGGING (Log4j 2 + Bridge for old code) ---
    implementation("org.slf4j:log4j-over-slf4j")
    implementation("log4j:apache-log4j-extras:1.1") {
        isTransitive = false // <--- PAKSA JANGAN AMBIL TEMAN-TEMANNYA
    }

    // --- DATABASES ---
    implementation("org.xerial:sqlite-jdbc:3.51.2.0")
    implementation("org.postgresql:postgresql:9.2-1003-jdbc4")
    implementation("mysql:mysql-connector-java:5.1.23")
    implementation("org.hibernate.orm:hibernate-community-dialects:6.6.5.Final")

    // --- LOMBOK ---
    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")

    // --- JSON LIBRARIES ---
    implementation("com.googlecode.json-simple:json-simple:1.1.1")
    implementation("org.json:json:20171018")
    implementation("com.google.code.gson:gson") // Versi diatur Spring Boot Parent
    implementation("com.flipkart.zjsonpatch:zjsonpatch:0.4.12")

    // --- APACHE COMMONS & UTILS ---
    implementation("commons-codec:commons-codec:1.8")
    implementation("commons-io:commons-io:2.16.1")
    implementation("commons-lang:commons-lang:2.6")
    implementation("commons-collections:commons-collections:3.2.1")
    implementation("commons-logging:commons-logging:1.1.2")
    implementation("commons-net:commons-net:3.6")
    implementation("commons-fileupload:commons-fileupload:1.3.3")
    implementation("com.google.guava:guava:16.0")

    // --- APACHE POI (Office Docs) ---
    val poiVersion = "5.4.0"
    implementation("org.apache.poi:poi:$poiVersion")
    implementation("org.apache.poi:poi-examples:$poiVersion")
    implementation("org.apache.poi:poi-excelant:$poiVersion")
    implementation("org.apache.poi:poi-ooxml:$poiVersion")
    implementation("org.apache.poi:poi-scratchpad:$poiVersion")

    // --- NETWORKING & API CLIENTS ---
    implementation("org.apache.httpcomponents:httpclient:4.5.13")
    implementation("org.apache.httpcomponents:httpmime:4.5")
    implementation("io.joshworks.unirest:unirest-java:1.7.1")
    implementation("com.github.scribejava:scribejava-core:5.5.0")
    implementation("org.igniterealtime:rest-api-client:1.1.5")

    // --- TEMPLATE & PARSER ---
    implementation("org.apache.velocity:velocity:1.7")
    implementation("org.freemarker:freemarker:2.3.31")
    implementation("org.jsoup:jsoup:1.12.1")

    // --- OTHERS ---
    implementation("jmagick:jmagick:6.2.4")
    implementation("com.sun.mail:javax.mail:1.6.2")
    implementation("net.sf.ehcache:ehcache:2.10.6")
    implementation("com.google.zxing:core:3.3.0")
    implementation("com.google.zxing:javase:3.3.0")
    implementation("me.xuender:unidecode:0.0.7")
    implementation("com.google.gcm:gcm-server:1.0.0")
    implementation("org.jdom:jdom2:2.0.6")

    // --- TESTING ---
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.assertj:assertj-core:3.26.3")
    testImplementation("org.skyscreamer:jsonassert:1.5.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Menangani pembuatan JAR seperti spring-boot-maven-plugin
tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    mainClass.set("com.spreadsheet.MainApplication") // Ganti dengan main class-mu bro
    archiveFileName.set("app.jar") // Opsional: biar namanya simpel jadi app.jar
}

configurations.all {
    // 1. Paksa: Siapa pun yang minta log4j jadul, kasih 'log4j-over-slf4j' sebagai gantinya
    resolutionStrategy.dependencySubstitution {
        substitute(module("log4j:log4j")).using(module("org.slf4j:log4j-over-slf4j:2.0.16"))
    }
    // 2. Tetap exclude saingan logback yang lain
    exclude(group = "org.apache.logging.log4j", module = "log4j-slf4j2-impl")
    exclude(group = "org.apache.logging.log4j", module = "log4j-slf4j-impl")
    exclude(group = "org.slf4j", module = "slf4j-simple")
}
