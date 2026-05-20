plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    // 1. 加上 ksp 插件
    alias(libs.plugins.ksp)
    // 2. 加上 hilt 插件
    alias(libs.plugins.hilt.android)
    // safeargs插件 用于navigation跳转传参
    alias(libs.plugins.navigation.safeargs.kotlin)
    // compose插件
    alias(libs.plugins.compose.compiler)
    // Parcelable 使用@Parcelize序列化插件
    alias(libs.plugins.kotlin.parcelize)


}

android {
    namespace = "com.cscyxp.finance"
    compileSdk = 35

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    buildFeatures {
        viewBinding = true
        compose = true
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

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    // ViewModel 核心库
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    // 如果要在 Activity / Fragment 中用 viewModels() 这种 Kotlin 扩展
    implementation (libs.androidx.activity.ktx)
    implementation (libs.androidx.fragment.ktx)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.gson)
    implementation(libs.hilt.android)
    // Retrofit 核心库
    implementation(libs.retrofit)
    // Retrofit 的 Gson 转换器
    implementation(libs.converter.gson)
    // OkHttp 日志拦截器（强烈推荐，用于在 Logcat 看请求和返回的 JSON）
    implementation(libs.logging.interceptor)
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
    // MockWebServer (版本号尽量与你项目里的 OkHttp 版本保持一致)
    testImplementation (libs.mockwebserver)

    implementation (libs.androidx.navigation.fragment.ktx)
    implementation (libs.androidx.navigation.ui.ktx)
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.android.compiler)

    implementation(project(":xpviews"))

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