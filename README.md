![](./logo.svg)

# gadgets

[![](https://jitpack.io/v/Zhupff/gadgets.svg)](https://jitpack.io/#Zhupff/gadgets)

这个项目的初衷将本人在开发过程中遇到的一些常用功能或想法进行实现、封装、汇总，作为经验累积的同时也能方便快速地引入到其他项目开发中，更希望能通过这个项目进行交流分享，一起学习，共同进步。

> demo很多，想法也很多，一步一步慢慢来～

---

## 用法

1. 添加`jitpack.io`仓库

   ```kotlin
   repositories {
       maven { url 'https://jitpack.io' }
   }
   ```

2. 使用`zhupff.gadgets`插件

   ```kotlin
   /**
    * root project build.gradle.kts
    */
   plugins {
       id("zhupff.gadgets") version "xxx" apply false
   }
   // If can NOT find "zhupff.gadgets", try to add "api" classpath:
   // classpath("com.github.Zhupff.gadgets:api:xxx")
   
   
   /**
    * module build.gradle.kts
    */
   plugins {
       id("zhupff.gadgets")
   }
   ...
   gadgets {
     // Configure whatever you need.
   }
   // If you use groovy-language with build.gradle file, try this.
   gadgets.compose {
     // Configure whatever you need.
   }
   ```

3. 使用某项功能（这里以`basic`为例）

   ```kotlin
   // root project build.gradle.kts
   buildscript {
       dependencies {
           // 这里的版本yyy可以与"zhupff.gadgets"插件的版本xxx不同，自行挑选适合的版本使用吧～
           classpath("com.github.Zhupff.gadgets:gadget-basic:yyy")
       }
   }
   
   // module build.gradle.kts
   gadgets {
       // 使用com.github.Zhupff.gadgets:gadget-basic:yyy
       Basic {
           // implementation("com.github.Zhupff.gadgets:basic-jvm:yyy")
           jvm("implementation")
           // api("com.github.Zhupff.gadgets:basic-android:yyy")
           android("api")
       }
   }
   ```

4. Enjoy

## 功能

- [gadget-basic](./gadget-basic/README.md)：一些基础功能的封装。
- [gadget-blur](./gadget-blur/README.md)：对View体系的模糊功能的相关研究与实现。
- [gadget-logger](./gadget-logger/README.md)：日志与logcat，还在整理中。。。
- [gadget-media](./gadget-media/README.md)：对系统自带的MediaPlayer的一些封装（内容简单，重点是记录思考与实现的过程）。
- [gadget-qrcode](./gadget-qrcode/README.md)：对`zxing`中关于二维码的功能进行尽可能的精简与一些使用上的优化。
- [gadget-theme](./gadget-theme/README.md)：App的主题换肤框架（推荐，花了不少心思去创新迭代优化:)，欢迎交流）。
- [gadget-toast](./gadget-toast/README.md)：对系统Toast功能的一些封装处理。
- [gadget-transform](./gadget-transform/README.md)：对以往写过的字节码插桩相关的内容进行整理，还在整理中。。。
- [gadget-widget](./gadget-widget/README.md)：主要是一些自定义View、通用功能封装以及dsl化使用。
- [version-catalog](./version-catalog/README.md)：整理gadgets里使用到的各个依赖的版本，项目使用这里的版本可以获得最大的兼容性。

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