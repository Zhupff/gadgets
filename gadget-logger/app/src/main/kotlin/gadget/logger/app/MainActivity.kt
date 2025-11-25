package gadget.logger.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logI { "onCreate($savedInstanceState)" }
    }

    override fun onStart() {
        super.onStart()
        logD { "onStart()" }
    }

    override fun onResume() {
        super.onResume()
        logD { "onResume()" }
    }
}