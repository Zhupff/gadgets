# gadgets

[![](https://jitpack.io/v/Zhupff/gadgets.svg)](https://jitpack.io/#Zhupff/gadgets)

---

## 用法

- 添加 `JitPack` 仓库。

```groovy
// For Groovy.
buildscript {
  repositories {
    maven { url "https://jitpack.io" }
  }
}
```

```kotlin
// For Kotlin
buildscript {
  repositories {
    maven("https://jitpack.io")
  }
}
```

- 添加并使用 `zhupf.gadgets` 插件（推荐）

  > 所有 `Library` 都可以单独添加依赖使用，如果不想使用 `zhupf.gadgets` 插件管理可跳过这步。

  项目根目录 `build.gradle` 或 `build.gradle.kts` 添加：

  ```groovy
  buildscript {
    dependencies {
      classpath("com.github.Zhupff.gadgets:gadget:$version")
    }
  }
  ```

  在项目中需要使用的模块的 `build.gradle` 或 `build.gradle.kts` 添加插件：

  ```groovy
  plugins {
    id("zhupf.gadgets")
  }
  ```

  使用 `gadgets` ：

  ```groovy
  // For Groovy
  gadgets.compose {
    Basic {
      // 举例使用 gadget-basic 库
    }
  }
  ```

  ```kotlin
  // For Kotlin
  gadgets {
    Basic {
      // 举例使用 gadget-basic 库
    }
  }
  ```

## 库

- [gadget-basic](./gadget-basic/README.md)
- [gadget-logger](./gadget-logger/README.md)
- [gadget-media](./gadget-media/README.md)
- [gadget-theme](./gadget-theme/README.md)
- [gadget-toast](./gadget-toast/README.md)
- [gadget-widget](./gadget-widget/README.md)

---

## LICENSE

```markdown
Copyright 2023 Zhupf

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```