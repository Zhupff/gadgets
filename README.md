![](./logo.svg)

# gadgets

> 这个项目的初衷，是将本人在学习过程中遇到的一些常用功能或想法进行实现和封装并进行统一管理，作为经验积累的同时，也能对外进行分享和交流。
>
> 想法很多，一直在路上～



## Download

> - 如果您只想体验或使用某个稳定版本的功能，可以通过`jitpack.io`去接入；
> - 如果您想实际使用并快速迭代，那么推荐将本项目拉取到本地并发布到`mavenLocal`再接入；

### 通过`jitpack.io`接入

1. 添加`jitpack.io`仓库：

   ```kotlin
   repositories {
     maven(url = "https://jitpack.io")
   }
   ```

2. 按需接入对应的`gadget`，这里以`gadget-basic`为例：

   ```kotlin
   // 项目根目录下的 build.gradle 或 build.gradle.kts
   buildscript {
     dependencies {
       classpath("com.github.Zhupff.gadgets:gadget-basic:$version")
     }
   }
   
   
   // 项目module目录下的 build.gradle 或 build.gradle.kts
   plugins {
     id("gadget.basic")
   }
   
   GadgetBasic {
     dependencies {
       // 按需接入功能
       implementation(jvm())
       implementation(android())
     }
   }
   ```

   

### 通过`mavenLocal`接入

1. 拉取项目到本地后，执行`./gradlew publishToMavenLocal`发布到本地的`.m2`仓库。

2. 添加`mavenLocal`仓库：

   ```kotlin
   repositories {
     mavenLocal()
   }
   ```

3. 按需接入对应的`gadget`，这里以`gadget-basic`为例：

   ```kotlin
   // 项目module目录下的 build.gradle 或 build.gradle.kts
   plugins {
     id("gadget.basic") version "0"
   }
   
   GadgetBasic {
     dependencies {
       // 按需接入功能
       implementation(jvm())
       implementation(android())
     }
   }
   ```

### 补充说明

1. 两种接入方法没有太大区别，主要是`group`和`version`不同：
   - jetpack.io: group=`com.github.Zhupff.gadgets`, version=[![](https://jitpack.io/v/Zhupff/gadgets.svg)](https://jitpack.io/#Zhupff/gadgets)
   - mavenLocal: group=`gadgets`, version=`0`



## Libraries

- [gadget-basic](./gadget-basic/README.md): 一些基础功能的整理和封装；



## License

```markdown
 Copyright 2024 Zhupff

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

