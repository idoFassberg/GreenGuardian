plugins {
	java
	id("org.springframework.boot") version "3.0.5"
	id("io.spring.dependency-management") version "1.1.0"
}

group = "com.greenguardian"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

dependencies {
	//implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
// https://mvnrepository.com/artifact/com.google.firebase/firebase-admin
	implementation ("com.google.firebase:firebase-admin:9.1.1")
	//implementation ("com.google.cloud:google-cloud-storage:1.114.5")

// https://mvnrepository.com/artifact/com.google.firebase/firebase-admin
	//implementation platform('com.google.firebase:firebase-bom:31.5.0')
	//implementation("com.google.firebase:firebase-database")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
