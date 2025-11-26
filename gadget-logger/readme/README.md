# gadget-logger

> logcat日志。



## Download

1. 添加仓库及插件：

   ```kotlin
   /** If you use jitpack.io */
   // root build.gradle or build.gradle.kts
   buildscript {
     dependencies {
       classpath("com.github.Zhupff.gadgets:gadget-logger:$version")
     }
   }
   // module build.gradle or build.gradle.kts
   plugins {
     id("gadget.logger")
   }
   
   
   /** If you use mavenLocal */
   plugins {
     id("gadget.logger") version "0"
   }
   ```

2. 按需接入所需功能：

   ```kotlin
   GadgetLogger {
     dependencies {
       implementation(core())
     }
   }
   ```

