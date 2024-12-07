# gadget-toast

 [![](https://jitpack.io/v/Zhupff/gadgets.svg)](https://jitpack.io/#Zhupff/gadgets)

## 引入

### 借助 [`gadgets`](../README.md)

```kotlin
// root project build.gradle.kts
buildscript {
    dependencies {
        // jitpack.io
        classpath("com.github.Zhupff.gadgets:gadget-toast:<version>")
        
        // or mavenLocal()
        // classpath("zhupff.gadgets:gadget-toast:0")
    }
}

// module build.gradle.kts
gadgets {
    Toast {
        toast()
    }
}
```

### 独立使用

```kotlin
// module build.gradle.kts
dependencies {
    // jitpack.io
    implementation("com.github.Zhupff.gadgets:toast:<version>")
    
    // or mavenLocal()
    // implementation("zhupff.gadgets:toast:0")
}
```



## 使用

> TODO