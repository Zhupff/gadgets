# version-catalog

> 整理`gadgets`里使用到的各个依赖的版本，方便项目快速添加依赖并确保兼容性。



## Download

```kotlin
/** If you use jitpack.io */
// root setting.gradle.kts
dependencyResolutionManagement {
  repositories {
    maven(url = "https://jitpack.io")
  }
  versionCatalogs {
    create("libs") {
      from("com.github.Zhupff.gadgets:version-catalog:$version")
    }
  }
}


/** If you use mavenLocal */
// root setting.gradle.kts
dependencyResolutionManagement {
  repositories {
    mavenLocal()
  }
  versionCatalogs {
    create("libs") {
      from("gadgets:version-catalog:0")
    }
  }
}
```

