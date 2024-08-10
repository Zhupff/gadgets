package zhupff.gadgets.basic

fun <T> Boolean?.ifElse(yes: T, no: T): T = if (this == true) yes else no
fun <T> Boolean?.ifElse(yes: () -> T, no: () -> T): T = if (this == true) yes() else no()

fun <T, F> T?.ifElse(condition: (T?) -> Boolean?, yes: F, no: F): F = if (condition(this) == true) yes else no
fun <T, F> T?.ifElse(condition: (T?) -> Boolean?, yes: (T?) -> F, no: (T?) -> F): F = if (condition(this) == true) yes(this) else no(this)

inline fun <T> T.alsoIf(condition: Boolean?, block: (T) -> Unit): T {
    if (condition == true) return this.also(block)
    return this
}

inline fun <T> T.applyIf(condition: Boolean?, block: T.() -> Unit): T {
    if (condition == true) return this.apply(block)
    return this
}

inline fun <T> T.letIf(condition: Boolean?, block: T.() -> Unit) {
    if (condition == true) let(block)
}

inline fun <T> T.runIf(condition: Boolean?, block: T.() -> Unit) {
    if (condition == true) run(block)
}