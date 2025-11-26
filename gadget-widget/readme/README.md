# gadget-widget

> 一些自定义View以及通用功能的封装等。



## Download

1. 添加仓库及插件：

   ```kotlin
   /** If you use jitpack.io */
   // root build.gradle or build.gradle.kts
   buildscript {
     dependencies {
       classpath("com.github.Zhupff.gadgets:gadget-widget:$version")
     }
   }
   // module build.gradle or build.gradle.kts
   plugins {
     id("gadget.widget")
   }
   
   
   /** If you use mavenLocal */
   plugins {
     id("gadget.widget") version "0"
   }
   ```

2. 按需接入所需功能：

   ```kotlin
   GadgetWidget {
     dependencies {
       // 使用到的注解，一般和compile()搭配使用。
       implementation(annotation())
       // 对View体系的模糊功能的相关研究与实现。
       implementation(blur())
       // 一些自定义View及功能的封装，如圆角裁切、自适应系统栏等。
       implementation(core())
       // 对annotation()中的注解进行处理，也支持kapt和ksp。
       annotationProcessor(compile())
     }
   }
   ```

