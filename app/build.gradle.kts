plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.mafaly.moviematchduel"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mafaly.moviematchduel"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            buildConfigField("String", "TMDB_API_URL", "\"https://api.themoviedb.org/3/\"")
            buildConfigField(
                "String", "TMDB_API_TOKEN",
                "\"eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIxZjQ1YTI0Mjk5OWVkOWE4NDQ0OGZmZDAzYjI2NWZiZiIsInN1YiI6IjY1YThkZDRmZWEzOTQ5MDEyODFmNDQ3NCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.sKeCYsJpLHsLGrzATjJO-_bXcFp2cRhzsXn2HRIL1Uw\""
            )
            buildConfigField("String", "TMDB_IMAGE_URL", "\"https://image.tmdb.org/t/p/original\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // UI
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // HTTP
    implementation("com.google.code.gson:gson:2.8.9") // Serialization to and from JSON

    // Retrofit ==> HTTP client for Android
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:adapter-rxjava3:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // HTTP Client
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")

    // Rx
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")

    // Koin DI
    implementation("io.insert-koin:koin-core:3.5.0")
    implementation("io.insert-koin:koin-android:3.5.0")

} 