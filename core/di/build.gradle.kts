plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.devtools.ksp)
}

// Load API key from local.properties
val localProperties = java.util.Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(localPropertiesFile.inputStream())
    }
}

android {
    namespace = "com.moviescatalog.core.di"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.compileSdk.get().toInt()

        // Read API key from local.properties or use empty string as fallback
        val tmdbApiKey = localProperties.getProperty("TMDB_API_KEY") ?: ""
        buildConfigField("String", "TMDB_API_KEY", "\"$tmdbApiKey\"")
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // Hilt (Dependency Injection)
    implementation(libs.hilt)
    ksp(libs.hiltCompiler)

    // Retrofit + Gson for Networking
    implementation(libs.retrofit)
    implementation(libs.gsonConverter)

    // OkHttp Logging for HTTP logs
    implementation(libs.okhttpLogging)

    // Room (for providing DAOs or Database instance)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Modules
    implementation(project(":core:util"))
    implementation(project(":data"))
    implementation(project(":domain"))
    testImplementation(kotlin("test"))
}
