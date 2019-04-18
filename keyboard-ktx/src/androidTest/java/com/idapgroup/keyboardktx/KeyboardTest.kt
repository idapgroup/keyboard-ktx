package com.idapgroup.keyboardktx

import android.app.Activity
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class KeyboardTest {

    @get:Rule
    val rule = ActivityTestRule(ExampleActivity::class.java, false, true)

    @Test
    fun keyboardTest() {
        rule.activity.apply {
            onKeyboardShowChanged { shown, inAppHeight ->
                val condition = if(shown) inAppHeight > 0 else inAppHeight == 0
                assertTrue(condition)
            }
            editText.showKeyboard()
            Thread.sleep(1000)
            assertTrue(isKeyboardShown)
            assertTrue(editText.isKeyboardShown)
            assertTrue(keyboardHeight > 0)
            assertTrue(editText.keyboardHeight > 0)
            editText.hideKeyboard()
            Thread.sleep(1000)
            assertFalse(isKeyboardShown)
            assertFalse(editText.isKeyboardShown)
            assertTrue(keyboardHeight== 0)
            assertTrue(editText.keyboardHeight== 0)
        }
    }

}

class ExampleActivity : Activity() {

    lateinit var editText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editText = EditText(this)
        val content = LinearLayout(this)
        content.addView(editText)
        setContentView(content)
    }

}