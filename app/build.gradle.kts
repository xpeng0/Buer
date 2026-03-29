plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp") version "2.0.21-1.0.25" // 版本要和 Kotlin 对应
    alias(libs.plugins.navigation.safeargs.kotlin)
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.cscyxp.buer"
    compileSdk = 35

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.cscyxp.buer"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
    kotlinOptions {
        jvmTarget = "11"
    }
}

// 必须添加这个，Gradle 才会把 JUnit 5 的库加入测试编译路径
tasks.withType<Test> {
    useJUnitPlatform()
}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    // ViewModel 核心库
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    implementation(libs.gson)
    implementation (project(":xpviews"))

    // 如果要在 Activity / Fragment 中用 viewModels() 这种 Kotlin 扩展
    implementation (libs.androidx.activity.ktx)
    implementation (libs.androidx.fragment.ktx)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Room 运行时
    implementation (libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    // Room 编译器（Kotlin 用 kapt，Java 用 annotationProcessor）
    ksp (libs.androidx.room.compiler)

    // 协程核心库
    implementation(libs.kotlinx.coroutines.android)
    // 协程测试库
    testImplementation(libs.kotlinx.coroutines.test)
    // 测试协程的工具库
    testImplementation (libs.turbine)

    implementation (libs.androidx.navigation.fragment.ktx)
    implementation (libs.androidx.navigation.ui.ktx)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
}