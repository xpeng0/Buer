plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // safeargs removed — now using Compose Navigation
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.cscyxp.buer"
    compileSdk = 35

    buildFeatures {
        compose = true
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
    implementation(project(":finance"))
    implementation(project(":feature:bookkeeping"))
    implementation(project(":feature:fitness"))

    // 如果要在 Activity / Fragment 中用 viewModels() 这种 Kotlin 扩展
    implementation (libs.androidx.activity.ktx)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Room 运行时
    implementation (libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    // Room 编译器（Kotlin 用 kapt，Java 用 annotationProcessor）
    ksp (libs.androidx.room.compiler)
    implementation(libs.kotlinx.serialization.json)

    // 协程核心库
    implementation(libs.kotlinx.coroutines.android)
    // 协程测试库
    testImplementation(libs.kotlinx.coroutines.test)
    // 测试协程的工具库
    testImplementation (libs.turbine)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    val composeBom = platform("androidx.compose:compose-bom:2026.04.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Choose one of the following:
    // Material Design 3
    implementation("androidx.compose.material3:material3")
    // or only import the main APIs for the underlying toolkit systems,
    // such as input and measurement/layout
    implementation("androidx.compose.ui:ui")

    // Android Studio Preview support
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // UI Tests
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    // 图标库
    implementation("androidx.compose.material:material-icons-extended")
}

// 告诉 Room schemas文件存哪
room {
    schemaDirectory("$projectDir/schemas")
}
