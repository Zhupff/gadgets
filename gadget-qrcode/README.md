# gadget-qrcode

 [![](https://jitpack.io/v/Zhupff/gadgets.svg)](https://jitpack.io/#Zhupff/gadgets)

对`zxing`的代码做了删减：

- 仅保留二维码相关的功能
- 移除非必要的配置选项



## 引入

### 借助 [`gadgets`](../README.md)

```kotlin
// root project build.gradle.kts
buildscript {
    dependencies {
        // jitpack.io
        classpath("com.github.Zhupff.gadgets:gadget-qrcode:<version>")
        
        // or mavenLocal()
        // classpath("zhupff.gadgets:gadget-qrcode:0")
    }
}

// module build.gradle.kts
gadgets {
    QRCode {
        qrcode()
    }
}
```

### 独立使用

```kotlin
// module build.gradle.kts
dependencies {
    // jitpack.io
    implementation("com.github.Zhupff.gadgets:qrcode:<version>")
    
    // or mavenLocal()
    // implementation("zhupff.gadgets:qrcode:0")
}
```



## 使用

> TODO