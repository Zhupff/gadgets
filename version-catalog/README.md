# version-catalog

 [![](https://jitpack.io/v/Zhupff/gadgets.svg)](https://jitpack.io/#Zhupff/gadgets)

`gadgets` 中使用到的依赖的版本管理，其他项目可以通过这个使用相同版本的依赖，最大限度提高使用gadgets的兼容性。

## 引入

```kotlin
// project setting.gradle.kts
dependencyResolutionManagement {
    repositories {
        mavenLocal()
    }
    versionCatalogs {
        create("gvc") {
            from("zhupff.gadgets:version-catalog:0")
        }
    }
}
```

## 注意

[jitpack](https://jitpack.io) 打不出`.toml`文件，所以这个功能暂时只能通过 `mavenLocal()` 使用。[issue](https://github.com/jitpack/jitpack.io/issues/5713)
