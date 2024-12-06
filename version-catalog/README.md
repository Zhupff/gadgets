# version-catalog

> gadgets中使用到的依赖的版本管理，其他项目可以通过这个使用相同版本的依赖，最大限度提高使用gadgets的兼容性。

## 引入

```kotlin
// project setting.gradle.kts

dependencyResolutionManagement {
    repositories {
        maven("https://jitpack.io")
    }
    versionCatalogs {
        create("gvc") {
            from("com.github.Zhupff.gadgets:version-catalog:xxx")
        }
    }
}
```

## 注意

[jitpack](https://jitpack.io) 打不出`.toml`文件，所以这个功能暂时用不了。[issue](https://github.com/jitpack/jitpack.io/issues/5713)
