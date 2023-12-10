package zhupf.gadget.basic

fun <T> Boolean?.ifElse(yes: T, no: T): T = if (this == true) yes else no
fun <T> Boolean?.ifElse(yes: () -> T, no: () -> T): T = if (this == true) yes() else no()

fun <T, F> T?.ifElse(condition: (T?) -> Boolean?, yes: F, no: F): F = if (condition(this) == true) yes else no
fun <T, F> T?.ifElse(condition: (T?) -> Boolean?, yes: (T?) -> F, no: (T?) -> F): F = if (condition(this) == true) yes(this) else no(this)