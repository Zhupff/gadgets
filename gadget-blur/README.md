# gadget-blur

 [![](https://jitpack.io/v/Zhupff/gadgets.svg)](https://jitpack.io/#Zhupff/gadgets)

## 引入

### 借助 [`gadgets`](../README.md)

```kotlin
// root project build.gradle.kts
buildscript {
    dependencies {
        // jitpack.io
        classpath("com.github.Zhupff.gadgets:gadget-blur:<version>")
        
        // or mavenLocal()
        // classpath("zhupff.gadgets:gadget-blur:0")
    }
}

// module build.gradle.kts
gadgets {
    Blur {
        blur()
    }
}
```

### 独立使用

```kotlin
// module build.gradle.kts
dependencies {
    // jitpack.io
    implementation("com.github.Zhupff.gadgets:blur:<version>")
    
    // or mavenLocal()
    // implementation("zhupff.gadgets:blur:0")
}
```



## 使用

> TODO