description = "Kotlin Build Common"

plugins {
    kotlin("jvm")
    id("jps-compatible")
}

dependencies {
    compileOnly(project(":core:util.runtime"))
    compileOnly(project(":compiler:backend.common.jvm"))
    compileOnly(project(":compiler:util"))
    compileOnly(project(":compiler:cli-common"))
    compileOnly(project(":compiler:frontend.java"))
    compileOnly(project(":js:js.serializer"))
    compileOnly(project(":js:js.config"))
    compileOnly(project(":kotlin-util-klib-metadata"))
    compileOnly(intellijCoreDep()) { includeJars("intellij-core") }
    compileOnly(intellijDep()) { includeJars("asm-all", "trove4j", "util", rootProject = rootProject) }
    compileOnly(project(":kotlin-reflect-api"))

    testCompileOnly(project(":compiler:cli-common"))
    testApi(projectTests(":compiler:tests-common"))
    testApi(commonDep("junit:junit"))
    testApi(protobufFull())
    testApi(kotlinStdlib())
    testImplementation(project(":kotlin-reflect"))
}

sourceSets {
    "main" { projectDefault() }
    "test" { projectDefault() }
}

publish()

runtimeJar()
sourcesJar()
javadocJar()

testsJar()

projectTest(parallel = true)
