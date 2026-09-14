import java.time.Instant

plugins {
    alias(libs.plugins.android.application)
}

fun String.asBuildConfigString(): String =
    "\"" + replace("\\", "\\\\").replace("\"", "\\\"") + "\""

val buildEnvironment = mapOf(
    "BUILD_TIME_UTC" to Instant.now().toString(),
    "BUILD_HOST" to (System.getenv("COMPUTERNAME")
        ?: System.getenv("HOSTNAME")
        ?: "unknown"),
    "BUILD_JAVA_VERSION" to System.getProperty("java.version", "unknown"),
    "BUILD_JAVA_VENDOR" to System.getProperty("java.vendor", "unknown"),
    "BUILD_GRADLE_VERSION" to gradle.gradleVersion,
    "BUILD_OS" to listOf(
        System.getProperty("os.name", "unknown"),
        System.getProperty("os.version", "unknown"),
        System.getProperty("os.arch", "unknown")
    ).joinToString(" "),
    "BUILD_CI" to (System.getenv("CI") ?: "false"),
    "BUILD_GITHUB_ACTIONS" to (System.getenv("GITHUB_ACTIONS") ?: "false"),
    "BUILD_GITHUB_RUN_ID" to (System.getenv("GITHUB_RUN_ID") ?: "not available"),
    "BUILD_GITHUB_RUN_ATTEMPT" to (System.getenv("GITHUB_RUN_ATTEMPT") ?: "not available"),
    "BUILD_GITHUB_WORKFLOW" to (System.getenv("GITHUB_WORKFLOW") ?: "not available"),
    "BUILD_GITHUB_JOB" to (System.getenv("GITHUB_JOB") ?: "not available"),
    "BUILD_GIT_REF" to (System.getenv("GITHUB_REF_NAME") ?: "not available"),
    "BUILD_GIT_SHA" to (System.getenv("GITHUB_SHA") ?: "not available")
)

android {
    namespace = "jp.co.personal.githubrunnertest"
    compileSdk {
        version = release(37)
    }

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "jp.co.personal.githubrunnertest"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildEnvironment.forEach { (name, value) ->
            buildConfigField("String", name, value.asBuildConfigString())
        }
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}