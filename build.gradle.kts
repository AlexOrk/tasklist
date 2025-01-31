plugins {
	java
	id("org.springframework.boot") version "3.3.6"
	id("io.spring.dependency-management") version "1.1.6"
	idea
	jacoco
	checkstyle
}

group = "orkhoian.aleksei"
version = "0.1.0"

java {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	val spring = "3.4.0"
	val retry = "2.0.11"
	val hibernate = "8.0.1.Final"
	val jakarta = "3.1.0"
	val minio = "8.5.14"
	val liquibase = "4.30.0"
	val springdoc = "2.6.0"
	val jjwt = "0.12.6"
	val mapstruct = "1.6.3"
	val securityTest = "6.4.2"

	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-configuration-processor:${spring}")
	implementation("org.springframework.retry:spring-retry:${retry}")
	implementation("org.hibernate.validator:hibernate-validator:${hibernate}")
	implementation("jakarta.validation:jakarta.validation-api:${jakarta}")
	implementation("io.minio:minio:${minio}")
	implementation("org.liquibase:liquibase-core:${liquibase}")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${springdoc}")
	implementation("io.jsonwebtoken:jjwt-api:${jjwt}")

	runtimeOnly("io.jsonwebtoken:jjwt-impl:${jjwt}")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:${jjwt}")
	runtimeOnly("org.postgresql:postgresql")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	implementation("org.mapstruct:mapstruct:${mapstruct}")
	annotationProcessor("org.mapstruct:mapstruct-processor:${mapstruct}")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test:${securityTest}")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testCompileOnly("org.projectlombok:lombok")
	testAnnotationProcessor("org.projectlombok:lombok")
}

tasks {
	test {
		configure<JacocoTaskExtension> {
			isEnabled = true
			setDestinationFile(file(layout.buildDirectory.dir("jacoco/$name.exec")))
		}
		maxParallelForks = Runtime.getRuntime().availableProcessors()
		useJUnitPlatform()
		finalizedBy(jacocoTestReport)
	}

	jacocoTestReport {
		dependsOn(test)
		reports {
			xml.required.set(true)
			html.required.set(true)
			csv.required.set(false)
			xml.outputLocation.set(file(layout.buildDirectory.dir("reports/jacoco/jacocoTestReport.xml")))
			html.outputLocation.set(file(layout.buildDirectory.dir("reports/jacoco")))
		}
	}

	jacocoTestCoverageVerification {
		dependsOn(jacocoTestReport)
		violationRules {
			rule {
				limit {
					minimum = BigDecimal.valueOf(0.1)
				}
			}
		}
	}

	build {
		dependsOn(checkstyleMain)
		dependsOn(jacocoTestCoverageVerification)
	}
}

checkstyle {
	toolVersion = "10.12.4"
	isIgnoreFailures = false
	isShowViolations = true
}