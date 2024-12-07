# gadget-basic

 [![](https://jitpack.io/v/Zhupff/gadgets.svg)](https://jitpack.io/#Zhupff/gadgets)

## 引入

### 借助 [`gadgets`](../README.md)

```kotlin
// root project build.gradle.kts
buildscript {
    dependencies {
        // jitpack.io
        classpath("com.github.Zhupff.gadgets:gadget-basic:<version>")
        
        // or mavenLocal()
        // classpath("zhupff.gadgets:gadget-basic:0")
    }
}

// module build.gradle.kts
gadgets {
    Basic {
        android() // aar，一些常用功能封装
        jvm() // jar，一些常用功能封装
    }
}
```

### 独立使用

```kotlin
// module build.gradle.kts
dependencies {
    // jitpack.io
    implementation("com.github.Zhupff.gadgets:basic-android:<version>") // aar，一些常用功能封装
    implementation("com.github.Zhupff.gadgets:basic-jvm:<version>") // jar，一些常用功能封装
    
    // or mavenLocal()
    // implementation("zhupff.gadgets:basic-android:0")
    // implementation("zhupff.gadgets:basic-jvm:0")
}
```

