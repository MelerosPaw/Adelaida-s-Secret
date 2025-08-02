plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.jetbrains.kotlin.android)
  alias(libs.plugins.compose.compiler)
  id("kotlin-kapt")
  id("com.google.dagger.hilt.android")
  alias(libs.plugins.serializable)
  id("kotlin-parcelize")
}

android {
  namespace = "com.example.composetest"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.example.composetest"
    minSdk = 24
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables {
      useSupportLibrary = true
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_19
    targetCompatibility = JavaVersion.VERSION_19
  }

  kotlinOptions {
    jvmTarget = "19"
  }

  buildFeatures {
    compose = true
  }

  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
}

kapt {
  correctErrorTypes = true
}

dependencies {
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel)

  // Compose
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.ui)
  implementation(libs.androidx.ui.graphics)
  implementation(libs.androidx.ui.tooling.preview)
  implementation(libs.androidx.material3)
  implementation(libs.androidx.material.icons)
  implementation(libs.androidx.material3.windowsize)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.runtime.livedata)
  implementation(libs.accompanist.pager.indicator)

  // Testing
  testImplementation(libs.junit)
  testImplementation(libs.mockk)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.ui.test.junit4)
  debugImplementation(libs.androidx.ui.tooling)
  debugImplementation(libs.androidx.ui.test.manifest)

  // Hilt
  implementation(libs.hilt.android)
  kapt(libs.hilt.compiler)

  // Room
  implementation(libs.room)
  kapt(libs.room.compiler) {
    exclude(group = "com.intellij", module = "annotations")
  }
  implementation(libs.room.coroutines)

  // Permissions
  implementation(libs.accompanist.permissions)

  // Splash
  implementation(libs.splashscreen)

  // Navigation
  implementation (libs.navigation)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.navigation.viewmodel)

  // Coil
  implementation(libs.coil)
}
