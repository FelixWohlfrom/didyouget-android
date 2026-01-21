plugins {
    id("com.android.application")
    id("org.jetbrains.kotlinx.kover")
    id("com.apollographql.apollo") version "4.4.0"
    id("androidx.navigation.safeargs.kotlin") version "2.9.6"
}

android {
    namespace = "de.wohlfrom.didyouget"
    compileSdk = 36
    compileSdkMinor = 1

    defaultConfig {
        applicationId = "de.wohlfrom.didyouget"
        minSdk = 24
        versionCode = 1
        versionName = "0.1"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

apollo {
    service("service") {
        packageNamesFromFilePaths()
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.annotation:annotation:1.9.1")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")

    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")

    implementation("androidx.security:security-crypto-ktx:1.1.0")

    implementation("androidx.navigation:navigation-fragment-ktx:2.9.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.9.6")
    implementation("androidx.navigation:navigation-dynamic-features-fragment:2.9.6")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.4.0")

    implementation("com.google.android.material:material:1.13.0")
    implementation("com.apollographql.apollo:apollo-runtime:4.4.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    implementation("com.google.code.gson:gson:2.13.2")

    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.14.7")
    testImplementation("org.robolectric:robolectric:4.16.1")
    testImplementation("androidx.test:runner:1.7.0")
    testImplementation("androidx.test.ext:junit:1.3.0")
    testImplementation("androidx.test.ext:junit-ktx:1.3.0")
    testImplementation("androidx.test.ext:truth:1.7.0")
    testImplementation("androidx.test.espresso:espresso-core:3.7.0")
    testImplementation("androidx.test.espresso:espresso-contrib:3.7.0")
    testImplementation("androidx.navigation:navigation-testing:2.9.6")

    // Those need to be included together to be working
    testImplementation("androidx.fragment:fragment-testing:1.8.9")
    debugImplementation("androidx.fragment:fragment-testing-manifest:1.8.9")

    androidTestImplementation("androidx.test.ext:junit:1.3.0")
}
