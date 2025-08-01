

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kapt) // <-- به این شکل صحیح است
    alias(libs.plugins.hilt)
    id("org.jetbrains.kotlin.plugin.serialization") // <-- این پلاگین را اضافه کنید
}

android {
    namespace = "ir.dekot.fileto"
    compileSdk = 36

    defaultConfig {
        applicationId = "ir.dekot.fileto"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin { // Or tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>()
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11) // Or your specific JVM version
            // You can add other Kotlin compiler options here if needed
            // e.g., freeCompilerArgs.add("-X opt-in=kotlin.RequiresOptIn")
        }
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes.add("META-INF/versions/9/OSGI-INF/MANIFEST.MF")
        }
    }
}

dependencies {
    // --- هسته اصلی اندروید و کاتلین ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    // --- Jetpack Compose (برای طراحی UI) ---
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
//    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.kotlinx.serialization.json)
    // --- JSON Serialization ---
    implementation(libs.gson) // این خط را اضافه کنید
    // از آنجایی که پروژه متن‌باز است، از نسخه Community با لایسنس AGPL استفاده می‌کنیم.
    // --- iText (برای کار با PDF) ---
    implementation(libs.itext.core) {
        exclude(group = "org.bouncycastle")
    }
    implementation(libs.bouncycastle.prov)
    implementation(libs.bouncycastle.pkix)
    // --- تست ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    // --- Hilt (برای تزریق وابستگی - Dependency Injection) ---
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    // --- Room (برای دیتابیس تاریخچه) ---
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
   //noinspection KaptUsageInsteadOfKsp
   kapt(libs.androidx.room.compiler)
    // --- DataStore (برای ذخیره تنظیمات برنامه) ---
    implementation(libs.androidx.datastore.preferences)
}