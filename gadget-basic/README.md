# gadget-basic

> 一些基础功能的整理和封装。



## Download

1. 添加仓库及插件：

   ```kotlin
   /** If you use jitpack.io */
   // root build.gradle or build.gradle.kts
   buildscript {
     dependencies {
       classpath("com.github.Zhupff.gadgets:gadget-basic:$version")
     }
   }
   // module build.gradle or build.gradle.kts
   plugins {
     id("gadget.basic")
   }
   
   
   /** If you use mavenLocal */
   plugins {
     id("gadget.basic") version "0"
   }
   ```

2. 按需接入所需功能：

   ```kotlin
   GadgetBasic {
     dependencies {
       implementation(jvm())
       implementation(android())
     }
   }
   ```

