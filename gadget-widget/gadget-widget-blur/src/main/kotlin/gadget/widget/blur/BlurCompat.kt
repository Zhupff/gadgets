package gadget.widget.blur

class BlurCompat : Blur by impl() {
    companion object {
        private fun impl(): Blur {
            return StackBlur()
        }
    }
}