plugins {
    id("org.jetbrains.kotlin.jvm").version("1.4.0")
    idea
}

repositories {
    jcenter()
}

dependencies {
    implementation(group = "org.jetbrains.kotlin", name = "kotlin-stdlib-jdk8")
    implementation(group = "org.jsoup", name = "jsoup", version = "1.13.1")
    implementation(group = "commons-io", name = "commons-io", version = "2.6")
    implementation(group = "org.apache.poi", name = "poi", version = "4.1.2")
    implementation(group = "org.apache.poi", name = "poi-ooxml", version = "4.1.2")
}
