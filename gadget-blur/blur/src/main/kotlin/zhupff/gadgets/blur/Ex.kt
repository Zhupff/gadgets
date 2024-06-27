package zhupff.gadgets.blur

internal inline val Int.red: Int get() = (this shr 16) and 0xFF

internal inline val Int.green: Int get() = (this shr 8) and 0xFF

internal inline val Int.blue: Int get() = this and 0xFF