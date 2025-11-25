package gadget.widget.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import gadget.widget.app.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {

    private val viewBinding: MainActivityBinding by lazy {
        MainActivityBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        viewBinding.gramophone.setOnClickListener {
            if (viewBinding.gramophone.isSpinning) {
                viewBinding.gramophone.stopSpinning()
            } else {
                viewBinding.gramophone.startSpinning()
            }
        }
    }
}