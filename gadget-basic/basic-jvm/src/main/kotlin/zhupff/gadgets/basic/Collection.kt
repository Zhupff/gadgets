package zhupff.gadgets.basic

fun <T> List<T>.mutable(): MutableList<T> =
    if (this is MutableList) this else throw IllegalStateException("It's not a mutable object.")

fun <T> Set<T>.mutable(): MutableSet<T> =
    if (this is MutableSet) this else throw IllegalStateException("It's not a mutable object.")

fun <K, V> Map<K, V>.mutable(): MutableMap<K, V> =
    if (this is MutableMap) this else throw IllegalStateException("It's not a mutable object.")

fun <E> MutableCollection<E>.addIf(element: E, condition: (E) -> Boolean) {
    if (condition(element)) {
        add(element)
    }
}

fun <E> MutableCollection<E>.addIfNotNull(element: E?) {
    if (element != null) {
        add(element)
    }
}

fun <E> MutableCollection<E>.clearIfNotEmpty(): Boolean {
    if (isNotEmpty()) {
        clear()
        return true
    }
    return false
}

fun <E> MutableCollection<E>.addAfterClearIfNotEmpty(element: E) {
    clearIfNotEmpty()
    add(element)
}

fun <E> MutableCollection<E>.addAfterClearIfNotEmpty(elements: Collection<E>) {
    clearIfNotEmpty()
    addAll(elements)
}