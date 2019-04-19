package com.idapgroup.keyboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.idapgroup.keyboardktx.hideKeyboard
import com.idapgroup.keyboardktx.isKeyboardShown
import com.idapgroup.keyboardktx.onKeyboardShowChanged
import com.idapgroup.keyboardktx.showKeyboard
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        changeStateButton.text

        changeStateButton.setOnClickListener {
            if(isKeyboardShown) hideKeyboard() else editText.showKeyboard()

        }
        onKeyboardShowChanged { shown, height ->
            changeStateButton.text = if(!shown) "Show keyboard" else "Hide keyboard"
            keyboardHeightView.text = getString(R.string.keyboard_height, height)
            keyboardStateView.text = getString(R.string.keyboard_state, if(shown) "SHOWN" else "HIDDEN")
        }

    }


}
