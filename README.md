Keyboard Kotlin Extensions
============

Kotlin extensions for hiding/showing and observe keyboard state.

Download
--------
[ ![Download](https://api.bintray.com/packages/idapgroup/kotlin/keyboard-ktx/images/download.svg?version=1.0.0) ](https://bintray.com/idapgroup/kotlin/keyboard-ktx/1.0.0/link)

Add repository to your root `build.gradle`

```groovy
repositories {
    jcenter()
}
```


```groovy
dependencies {
  implementation 'com.idapgroup:keyboard-ktx:latest-version'
}
```


Usage sample
-------------

```kotlin

class ExampleActivity  :  AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeKeyboardStateButton.setOnClickListener {
            if(isKeyboardShown) hideKeyboard() else editText.showKeyboard()
        }
        onKeyboardShowChanged { shown, height ->
            // your use of shown and height
        }
    }
}

```