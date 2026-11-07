
plugins {
    kotlin("jvm")
}

kotlin {
    // generator jar 由 Gradle daemon 直接加载，与 daemon 的 JDK 保持一致。
    jvmToolchain(17)
}

// 固定 jar 名，供 :bilimiao-grpc:proto 的 pbandk 生成任务（kotlin_service_gen option）引用
tasks.jar {
    archiveBaseName.set("grpc-generator")
}

tasks.withType<JavaCompile>().all {
    enabled = false
}

dependencies {
    compileOnly(libs.pbandk.runtime)
    compileOnly(libs.pbandk.genlib)
}