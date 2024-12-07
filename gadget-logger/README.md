# gadget-logger

 [![](https://jitpack.io/v/Zhupff/gadgets.svg)](https://jitpack.io/#Zhupff/gadgets)

## 引入

### 借助 [`gadgets`](../README.md)

```kotlin
// root project build.gradle.kts
buildscript {
    dependencies {
        // jitpack.io
        classpath("com.github.Zhupff.gadgets:gadget-logger:<version>")
        
        // or mavenLocal()
        // classpath("zhupff.gadgets:gadget-logger:0")
    }
}

// module build.gradle.kts
gadgets {
    Logger {
        logger()
    }
}
```

### 独立使用

```kotlin
// module build.gradle.kts
dependencies {
    // jitpack.io
    implementation("com.github.Zhupff.gadgets:logger:<version>")
    
    // or mavenLocal()
    // implementation("zhupff.gadgets:logger:0")
}
```



## 使用

> TODO



## TODO

- 本地读写支持
- 格式化输出支持