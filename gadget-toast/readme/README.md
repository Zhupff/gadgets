# gadget-toast

> 对系统`Toast`功能的一些封装。



## Download

1. 添加仓库及插件：

   ```kotlin
   /** If you use jitpack.io */
   // root build.gradle or build.gradle.kts
   buildscript {
     dependencies {
       classpath("com.github.Zhupff.gadgets:gadget-toast:$version")
     }
   }
   // module build.gradle or build.gradle.kts
   plugins {
     id("gadget.toast")
   }
   
   
   /** If you use mavenLocal */
   plugins {
     id("gadget.toast") version "0"
   }
   ```

2. 按需接入所需功能：

   ```kotlin
   GadgetToast {
     dependencies {
       implementation(core())
     }
   }
   ```

