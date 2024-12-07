# gadget-widget

 [![](https://jitpack.io/v/Zhupff/gadgets.svg)](https://jitpack.io/#Zhupff/gadgets)

## 引入

### 借助 [`gadgets`](../README.md)

```kotlin
// root project build.gradle.kts
buildscript {
    dependencies {
        // jitpack.io
        classpath("com.github.Zhupff.gadgets:gadget-widget:<version>")
        
        // or mavenLocal()
        // classpath("zhupff.gadgets:gadget-widget:0")
    }
}

// module build.gradle.kts
gadgets {
    Widget {
        widget() // 一些View及功能的封装，如圆角裁切、自适应系统栏、手势响应等
        // annotation、compile、dsl三个一般搭配使用，提供了类似Jetpack Compose或Flutter的UI声明语法
        annotation()
        compile("kapt") // or ksp or annotationProcessor
        dsl()
    }
}
```

### 独立使用

```kotlin
// module build.gradle.kts
dependencies {
    // jitpack.io
    implementation("com.github.Zhupff.gadgets:widget:<version>") // 一些View及功能的封装，如圆角裁切、自适应系统栏、手势响应等
    // annotation、compile、dsl三个一般搭配使用，提供了类似Jetpack Compose或Flutter的UI声明语法
    implementation("com.github.Zhupff.gadgets:widget-annotation:<version>")
    kapt("com.github.Zhupff.gadgets:widget-compile:<version>") // or ksp or annotationProcessor
    implementation("com.github.Zhupff.gadgets:widget-dsl:<version>")
    
    // or mavenLocal()
    // implementation("zhupff.gadgets:widget:0")
    // implementation("zhupff.gadgets:widget-annotation:0")
    // kapt("zhupff.gadgets:widget-compile:0")
    // implementation("zhupff.gadgets:widget-dsl:0")
}
```



## 使用

> TODO



## 样例

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(
        FrameLayout(
            context = this,
            size = MATCH_PARENT to MATCH_PARENT,
        ) {
            theme {
                addThemeAttribute("background", ThemeRColor.theme__background)
            }

            ImageView (
                size = 256.dp to 256.dp,
            ) {
                frameLayoutParams {
                    gravity = Gravity.CENTER
                }
                setImageResource(zhupff.gadget.basic.R.drawable.logo)
                scaleType = ImageView.ScaleType.CENTER_CROP
                theme {
                    addThemeAttribute("tint", ThemeRColor.theme__on_background)
                }
            }

            TextView(
                size = WRAP_CONTENT to WRAP_CONTENT,
            ) {
                frameLayoutParams {
                    gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
                    setMargins(0, 0, 0, 300)
                }
                text = "Welcome to Gadget!"
                textSize = 20F
                theme {
                    addThemeAttribute("textColor", ThemeRColor.theme__on_background)
                }
                setOnClickListener {
                    GadgetTheme.switch()
                }
            }
        }
    )
}
```



## TODO

- 写个IDEA插件用来预览dsl写出来的布局。