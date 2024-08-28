# gadget-theme

> 简单易用的Android App 主题换肤框架。

## 添加依赖

- 借助`gadgets`(推荐)：

  1. 添加classpath

     ```kotlin
     /**
      * root project build.gradle.kts
      */
     buildscript {
         dependencies {
             // gradle script.
             classpath("com.github.Zhupff.gadgets:gadget-theme:xxx")
             // theme-resource pack&merge.
             classpath("com.github.Zhupff.gadgets:theme-plugin:xxx")
         }
     }
     ```

  2. 启用功能

     ```kotlin
     /**
      * Application module build.gradle.kts
      */
     gadgets {
         Theme {
             // add merge-plugin.
             themeMerge()
             // add theme-function dependencies.
             theme()
             // add theme-dsl-function dependencies.
             dsl()
             // add annotations about dsl, work with dsl() and compile().
             annotation()
             // add annotation-processor-tool(or kotlin-symbol-processor), work with dsl() and annotation().
             compile("annotationProcessor"/* or "kapt" or "ksp" */)
         }
     }
     ```

  3. 皮肤资源打包

     ```kotlin
     /**
      * theme-resource module build.gradle.kts
      */
     gadgets {
         Theme {
             // add pack-plugin.
             themePack()
        }
     }
     ```

- 不借助`gadgets`

  1. 添加classpath

     ```kotlin
     /**
      * root project build.gradle.kts
      */
     buildscript {
         dependencies {
             // theme-resource pack&merge.
             classpath("com.github.Zhupff.gadgets:theme-plugin:xxx")
         }
     }
     plugins {
         id("zhupff.gadgets.theme.merge") version "xxx" apply false
         id("zhupff.gadgets.theme.pack") version "xxx" apply false
     }
     ```

  2. 启用功能

     ```kotlin
     /**
      * Application module build.gradle.kts
      */
     plugins {
         // add merge-plugin.
         id("zhupff.gadgets.theme.merge")
     }
     dependencies {
         // add theme-function dependencies.
         implementation("com.github.Zhupff.gadgets:theme:xxx")
         // add theme-dsl-function dependencies.
         implementation("com.github.Zhupff.gadgets:theme-dsl:xxx")
         // add annotations about dsl, work with dsl() and compile().
         implementation("com.github.Zhupff.gadgets:theme-annotation:xxx")
         // add annotation-processor-tool(or kotlin-symbol-processor), work with dsl() and annotation().
         implementation("com.github.Zhupff.gadgets:theme-compile:xxx")
     }
     ```

  3. 皮肤资源打包

     ```kotlin
     /**
      * theme-resource module build.gradle.kts
      */
     plugins {
         // add pack-plugin
         id("zhupff.gadgets.theme.pack")
     }
     ```

## 功能使用

  ### 换肤框架

  1. 声明默认主题（即默认使用的主题资源）

     在项目的资源的`assets`目录添加`theme.json`文件，内容如下：

     ```json
     {
       "theme_id": "the same as application-module's applicationId",
       "is_origin": true # default-value for theme-pack is false
     }
     ```

     实现一个`Theme`实例：

     ```kotlin
     val ORIGIN_THEME = Theme(
         Application.resources,
         null,
     )
     ```

  2. 自定义`ThemeConfig`

     ```kotlin
     open class ThemeConfig(
         /**
          * 主题资源的前缀(如：<color name="theme__primary_color">#FFFFFFFF</color>)，
          * prefix就是"theme__"，其他用于换肤的资源也需要带有这个前缀，
          * 后续inflate的时候会根据用到的资源是否带有该前缀，来判断这个View的这个属性是否需要换肤。
          */
         val prefix: String,
         /**
          * 项目中所有自定义换肤属性
          */
         val attributes: List<ThemeAttribute>,
     )
     
     /**
      * 下面简单举个ThemeAttribute的例子
      */
     // 这里我借用AutoService汇总所有的ThemeAttribute给ThemeConfig，可以根据项目实际情况使用自己的方式
     @AutoService(ThemeAttribute::class)
     class TextColor : ThemeAttribute(
         // 对应xml中的android:textColor="xxx"
         "textColor",
     ) {
         // 切换主题时会触发
         override fun apply(view: View, theme: Theme) {
             val colorStateList = theme.getColorStateList(resourceId, resourceName, resourceType)
             val color: Int? = if (colorStateList == null) theme.getColor(resourceId, resourceName, resourceType) else null
             when (view) {
                 is TextView -> {
                     if (colorStateList != null) {
                         view.setTextColor(colorStateList)
                     } else if (color != null) {
                         view.setTextColor(color)
                     }
                 }
             }
         }
     }
     ```
     
  3. 替换`LayoutInflator.Factory`

     ```kotlin
     class MainActivity : AppCompatActivity {
         override fun onCreate(savedInstanceState: Bundle?) {
             layoutInflater.factory2 = ThemeFactory(
                 layoutInflater.factory2, // 可以理解为用默认的factory兜底
                 YourThemeConfig,
             )
             super.onCreate(savedInstanceState)
         }
     }
     ```

  4. 声明主题分发

     一个view对象会使用view-tree上层距离最近的分发者分发的主题：

     ```
     Activity(dispatch theme A)
         |--- View(use theme A)
         |--- ViewGroup(dispatch theme B)
         |        |--- View (use theme B)
         |--- Fragment(dispatch theme C)
                  |--- View (use theme C)
     ```

     - 通过Activity分发

       ```kotlin
       class MainActivity : AppCompatActivity, ThemeDispacter {
           override fun observableTheme(): LiveData<Theme> {
               // implement by yourself.
           }
       }
       ```

     - 通过View分发

       ```kotlin
       class YourView : FrameLayout, ThemeDispacter {
           override fun observableTheme(): LiveData<Theme> {
               // implement by yourself.
           }
       } 
       ```

     使用者可以通过在不同的Activity或页面的root-view实现各自的主题分发，比较常见在一些“主题试用”的功能。

  5. 以上便完成了换肤框架的接入。

  ### 主题资源包

主题资源包本质上是一个“只包含资源的apk”文件，里面的资源都能且必须能在app中找到对应的一样名字的同类型资源，比如：

```xml
<!-- orgin theme in app -->
<color name="theme__primary_color">#FFFFFFFF</color>)

<!-- other theme in themepack -->
<color name="theme__primary_color">#FF000000</color>)
```

如果想随App一起打包（默认是亮色主题，想随App增加一个暗色主题），可以在项目中新增一个`Application module`并使用`pack-plugin（见上面的添加依赖）`，这样主题包便会打包进App中的`assets/themepacks/`下，可在代码中通过`Application.resources.assets`将其导出到设备文件夹。

如果想通过网络下载的方式进行分发，请结合项目情况自行搭建文件下载功能。



获取到主题资源包的文件后，可借助Theme加载资源包的Resources实例（当然也可以使用其他方法，只要能拿到Resources就行）：

```kotlin
fun loadThemeResources(filePath: String): Resources? =
        try {
            val assetManager = AssetManager::class.java.newInstance()
            val addAssetPath = assetManager::class.java.getDeclaredMethod("addAssetPath", String::class.java)
            addAssetPath.invoke(assetManager, filePath)
            Resources(assetManager, resources.displayMetrics, resources.configuration)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
```

拿到`Resources`之后便可以创建主题实例了：

```kotlin
val darkThemeResources = ORIGIN_THEME.loadThemeResources(filePath)
val darkTheme = Theme(
    resources = darkThemeResources,
    parent= ORIGIN_THEME,
)
```

这里解释下使用`ORIGIN_THEME`做参数的意义：

1. 每个主题实例（默认主题除外）都必须有一个`parent`主题，所有主题实例的最老的祖宗都必须是ORIGIN_THEME。

2. 代码中使用到的资源id都是基于ORIGIN_THEME.resources的，所以不同主题实例之间需要通过ORIGIN_THEME建立联系，以确保资源映射的正确性。

3. 当主题资源包缺少某个资源（比如一个色值、一张图片或一个字符串），那么在切换该主题找不到这个资源的时候，就会往上查找`parent`主题中对应的资源，如果一直找不到，则最终会回到ORIGIN_THEME中拿到默认资源。

   这样可以一定程度上减少主题资源包的体积，比如某个主题只在默认的基础上有几张背景图不同，那么该主题资源包可以只打包这几张图，其余的资源可以复用默认资源。

### 样例

```xml
<!-- main_activity.xml -->
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <TextView
        android:id="@+id/tv"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:text="wtfu"
        android:textColor="@color/theme__themeColor"
        android:background="@drawable/logo"/>

</FrameLayout>
```

一眼过去是不是没看出有什么特别的地方？？？

确实和寻常编写没有任何区别，不需要增加xmlns也不需要增加tag，只需要在资源命名上花点功夫就行了（通常开发时对于换肤资源本来就要特殊化管理，所以这点功夫应该不算什么，属于常规内容）。

所以，除了那句`android:textColor="@color/theme__themeColor"`，因为我们声明了`textColor`的ThemeAttribute（见上文）同时资源名带有我们声明的前缀`theme__`，所以在切换主题的时候，这个TextView的文字颜色也会发生相应改变，其他的属性不受影响。



TODO：后续再补个示例动图吧。

  