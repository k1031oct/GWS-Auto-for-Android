import java.util.Properties
import com.google.firebase.appdistribution.gradle.firebaseAppDistribution

// --- Start of Dynamic Versioning Logic ---

// Returns the total number of git commits as an integer.
fun fetchGitCommitCount(): Int {
    return try {
        val process = ProcessBuilder("git", "rev-list", "--count", "HEAD")
            .redirectErrorStream(true)
            .start()
        val output = process.inputStream.bufferedReader().readText()
        process.waitFor()
        output.trim().toInt()
    } catch (_: Exception) {
        // Fallback for environments where git isn't available or during initial Gradle sync.
        1
    }
}

// Read version properties for major and minor versions.
val versionProps = Properties()
val versionPropsFile = file("version.properties")
if (versionPropsFile.canRead()) {
    versionProps.load(versionPropsFile.reader())
}
val major = versionProps.getProperty("VERSION_MAJOR", "1")
val minor = versionProps.getProperty("VERSION_MINOR", "0")

val gitCommitCount = fetchGitCommitCount()

// --- End of Dynamic Versioning Logic ---

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.firebase.appdistribution)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.oss.licenses)
}

android {
    namespace = "com.gws.auto.mobile.android"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.gws.auto.mobile.android"
        minSdk = 26
        targetSdk = 36
        multiDexEnabled = true

        // Set the version codes dynamically using Git commit count
        versionCode = gitCommitCount
        versionName = "$major.$minor.$gitCommitCount"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            multiDexKeepProguard = file("multidex-config.pro")
            firebaseAppDistribution {
                appId = System.getenv("FIREBASE_APP_ID")
                groups = "testers"
                artifactType = "APK" // Use APK for test distribution
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }
    
    defaultConfig {
        // Slack config removed
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "2.0.0-beta04"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/LICENSE.md"
            excludes += "/META-INF/NOTICE.md"
            pickFirsts += "META-INF/DEPENDENCIES"
            pickFirsts += "META-INF/INDEX.LIST"
        }
    }
}

dependencies {
    coreLibraryDesugaring(libs.android.desugar.jdk.libs)

    // Firebase - BOM for consistent versions
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    // Room Database
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Google & AndroidX - All using version catalog
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.google.gson) // Added for Room TypeConverters
    implementation(libs.playServicesAuth)
    implementation(libs.googleOssLicenses)
    implementation(libs.google.api.client.android)
    implementation(libs.google.http.client.gson)
    implementation(libs.google.api.services.drive)
    implementation(libs.google.api.services.sheets)
    implementation(libs.google.api.services.calendar)
    implementation(libs.google.api.services.gmail)
    implementation(libs.google.api.services.tasks)
    implementation(libs.google.oauth.client)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.androidx.splashscreen)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.bundles.navigation)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.preference)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation("androidx.hilt:hilt-work:1.2.0")
    ksp("androidx.hilt:hilt-compiler:1.2.0")

    // JavaMail API for Android
    implementation(libs.com.sun.mail.android.mail)
    implementation(libs.com.sun.mail.android.activation)

    // Third Party
    implementation(libs.coil.compose)
    implementation(libs.jakewharton.timber)
    implementation(libs.mpandroidchart)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.ext.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
}

afterEvaluate {
    tasks.named("debugOssLicensesCleanUp") {
        dependsOn(tasks.named("debugOssDependencyTask"))
    }

    tasks.named("releaseOssLicensesCleanUp") {
        dependsOn(tasks.named("releaseOssDependencyTask"))
    }
}
