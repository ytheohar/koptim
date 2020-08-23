plugins {
  kotlin("jvm") version "1.4.0"
  application
}

group = "uk.co.alephn"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
  jcenter()
}

dependencies {
  implementation(kotlin("stdlib"))
  implementation("org.apache.commons:commons-math3:3.6.1")

  val spekVersion = "2.0.12"
  implementation(kotlin("test-junit"))
  implementation("org.spekframework.spek2:spek-dsl-jvm:$spekVersion")
  testImplementation("org.spekframework.spek2:spek-runner-junit5:$spekVersion")
  testRuntimeOnly(kotlin("reflect"))

  implementation("io.strikt:strikt-core:0.4.1")
}

tasks {
  compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }

  compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }

  test {
    useJUnitPlatform {
      includeEngines = setOf("spek2")
    }
  }
}

application {
  mainClassName = "uk.co.alephn.koptim.KoptimKt"
}