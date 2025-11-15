package gadget.basic

import java.util.LinkedList

fun <T> List<T>.mutable(): MutableList<T> =
    if (this is MutableList) this else throw IllegalStateException("It's not a MutableList!")

fun <T> Set<T>.mutable(): MutableSet<T> =
    if (this is MutableSet) this else throw IllegalStateException("It's not a MutableSet!")

fun <K, V> Map<K, V>.mutable(): MutableMap<K, V> =
    if (this is MutableMap) this else throw IllegalStateException("It's not a MutableMap!")

fun <E> MutableCollection<E>.addIf(element: E, condition: (E) -> Boolean): Boolean =
    condition(element) && add(element)

fun <E> MutableCollection<E>.removeIf(element: E, condition: (E) -> Boolean): Boolean =
    condition(element) && remove(element)

fun <E> LinkedList<E>.popOrNull(): E? {
    if (isNotEmpty()) {
        return pop()
    }
    return null
}