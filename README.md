![](./logo.svg)

# gadgets

`Gadgets` 是一系列自定义库的集合，本身提供了对这些库的统一管理，能够方便快捷的引入并使用这些库。

当然如果想不依赖`Gadgets`去使用某个库也是可以的。

>  这个项目的初衷，是将本人在开发过程中遇到的一些常用功能或想法进行实现、封装、汇总，作为经验累积的同时，也能方便快速地引入到其他项目开发中，更希望能通过这个项目进行交流分享，一起学习，共同进步。
> 想法很多，一直在路上，一步一步慢慢来～



## 用法

> - 如果你只想体验或使用某个稳定版本的功能，那么可以使用 `jitpack.io` 仓库去引入依赖。
> - 如果你想修改功能或快速迭代，那么推荐将本项目拉取到本地后，发布到`mavenLocal()`再使用。

### 使用 `jitpack.io`

1. 添加 `jitpack.io` 仓库 [![](https://jitpack.io/v/Zhupff/gadgets.svg)](https://jitpack.io/#Zhupff/gadgets)

2. 引入项目

   ```kotlin
   // 项目根目录 build.gradle.kts
   buildscript {
       dependencies {
           classpath("com.github.Zhupff.gadgets:api:<version>")
       }
   }
   
   // 项目模块 build.gradle.kts
   plugins {
       id("zhupff.gadgets")
   }
   gadgets {
       // 在这里对库进行管理&使用
   }
   ```

### 使用 `mavenLocal()`

1. 拉取项目后，在 Terminal 执行 `./gradlew publish`，就会发布到本地的 `.m2` 仓库。

2. 添加 `mavenLocal` 仓库

3. 引入项目

   ```kotlin
   // 项目根目录 build.gradle.kts
   buildscript {
       dependencies {
           classpath("zhupff.gadgets:api:0")
       }
   }
   
   // 项目模块 build.gradle.kts
   plugins {
       id("zhupff.gadgets")
   }
   gadgets {
       // 在这里对库进行管理&使用
   }
   ```

### 补充说明

- 使用 `jitpack.io` 和 `mavenLocal()` 没有太大区别，但需要注意两者的 `group` 和 `version` 并不相同：

  - `jitpack.io`： group = `com.github.Zhupff.gadgets` ，version = 具体发布的版本
  - `mavenLocal()` ：group = `zhupff.gadgets` ，version = `0`

- 这个使用说明是按照 `build.gradle.kts` 的格式出的，如果还在使用 `build.gradle` 的话，需要使用以下语法去兼容 `groovy`：

  ```groovy
  gadgets.compose {
      // 在这里对库进行管理&使用
  }
  // 因为还没对groovy脚本进行完整测试，如果有问题欢迎反馈issue。
  ```



## 自定义库

- [gadget-basic](./gadget-basic/README.md)：一些基础功能的封装。
- [gadget-blur](./gadget-blur/README.md)：对View体系的模糊功能的相关研究与实现。
- [gadget-logger](./gadget-logger/README.md)：日志与logcat，还在整理中。。。
- [gadget-media](./gadget-media/README.md)：对系统自带的MediaPlayer的一些封装（内容简单，重点是记录思考与实现的过程）。
- [gadget-qrcode](./gadget-qrcode/README.md)：对`zxing`中关于二维码的功能进行尽可能的精简与一些使用上的优化。
- [gadget-theme](./gadget-theme/README.md)：App的主题换肤框架（推荐，花了不少心思去创新迭代优化:)，欢迎交流）。
- [gadget-toast](./gadget-toast/README.md)：对系统Toast功能的一些封装处理。
- [gadget-transform](./gadget-transform/README.md)：对以往写过的字节码插桩相关的内容进行整理，还在整理中。。。
- [gadget-widget](./gadget-widget/README.md)：主要是一些自定义View、通用功能封装以及dsl化使用。
- [version-catalog](./version-catalog/README.md)：整理gadgets里使用到的各个依赖的版本，方便项目快速添加依赖并确保兼容性。



## LICENSE

```markdown
Copyright 2024 Zhu.Pengfu

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