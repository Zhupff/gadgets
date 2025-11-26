# gadget-rxjava

> 一些关于`rxjava`的封装。



## Download

1. 添加仓库及插件：

   ```kotlin
   /** If you use jitpack.io */
   // root build.gradle or build.gradle.kts
   buildscript {
     dependencies {
       classpath("com.github.Zhupff.gadgets:gadget-rxjava:$version")
     }
   }
   // module build.gradle or build.gradle.kts
   plugins {
     id("gadget.rxjava")
   }
   
   
   /** If you use mavenLocal */
   plugins {
     id("gadget.rxjava") version "0"
   }
   ```

2. 按需接入所需功能：

   ```kotlin
   GadgetRxJava {
     dependencies {
       // About rxjava3.
       implementation(core3())
     }
   }
   ```

