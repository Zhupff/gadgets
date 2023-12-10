package zhupf.gadget.basic

import java.io.Serializable

interface Tuple : Serializable {
    val size: Int
    fun mutable(): MutableTuple
}

data class Tuple2<A, B>(
    val a: A, val b: B
) : Tuple {
    override val size: Int = 2
    override fun mutable() = MutableTuple2(a, b)
}

data class Tuple3<A, B, C>(
    val a: A, val b: B, val c: C
) : Tuple {
    override val size: Int = 3
    override fun mutable() = MutableTuple3(a, b, c)
}

data class Tuple4<A, B, C, D>(
    val a: A, val b: B, val c: C, val d: D
) : Tuple {
    override val size: Int = 4
    override fun mutable() = MutableTuple4(a, b, c, d)
}

data class Tuple5<A, B, C, D, E>(
    val a: A, val b: B, val c: C, val d: D, val e: E
) : Tuple {
    override val size: Int = 5
    override fun mutable() = MutableTuple5(a, b, c, d, e)
}

data class Tuple6<A, B, C, D, E, F>(
    val a: A, val b: B, val c: C, val d: D, val e: E, val f: F
) : Tuple {
    override val size: Int = 6
    override fun mutable() = MutableTuple6(a, b, c, d, e, f)
}

data class Tuple7<A, B, C, D, E, F, G>(
    val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G
) : Tuple {
    override val size: Int = 7
    override fun mutable() = MutableTuple7(a, b, c, d, e, f, g)
}

data class Tuple8<A, B, C, D, E, F, G, H>(
    val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H
) : Tuple {
    override val size: Int = 8
    override fun mutable() = MutableTuple8(a, b, c, d, e, f, g, h)
}

data class Tuple9<A, B, C, D, E, F, G, H, I>(
    val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I
) : Tuple {
    override val size: Int = 9
    override fun mutable() = MutableTuple9(a, b, c, d, e, f, g, h, i)
}



interface MutableTuple : Tuple {
    override fun mutable(): MutableTuple = this
    fun immutable(): Tuple
}

data class MutableTuple2<A, B>(
    var a: A, var b: B
) : MutableTuple {
    override val size: Int = 2
    override fun immutable() = Tuple2(a, b)
}

data class MutableTuple3<A, B, C>(
    var a: A, var b: B, var c: C
) : MutableTuple {
    override val size: Int = 3
    override fun immutable() = Tuple3(a, b, c)
}

data class MutableTuple4<A, B, C, D>(
    var a: A, var b: B, var c: C, var d: D
) : MutableTuple {
    override val size: Int = 4
    override fun immutable() = Tuple4(a, b, c, d)
}

data class MutableTuple5<A, B, C, D, E>(
    var a: A, var b: B, var c: C, var d: D, var e: E
) : MutableTuple {
    override val size: Int = 5
    override fun immutable() = Tuple5(a, b, c, d, e)
}

data class MutableTuple6<A, B, C, D, E, F>(
    var a: A, var b: B, var c: C, var d: D, var e: E, var f: F
) : MutableTuple {
    override val size: Int = 6
    override fun immutable() = Tuple6(a, b, c, d, e, f)
}

data class MutableTuple7<A, B, C, D, E, F, G>(
    var a: A, var b: B, var c: C, var d: D, var e: E, var f: F, var g: G
) : MutableTuple {
    override val size: Int = 7
    override fun immutable() = Tuple7(a, b, c, d, e, f, g)
}

data class MutableTuple8<A, B, C, D, E, F, G, H>(
    var a: A, var b: B, var c: C, var d: D, var e: E, var f: F, var g: G, var h: H
) : MutableTuple {
    override val size: Int = 8
    override fun immutable() = Tuple8(a, b, c, d, e, f, g, h)
}

data class MutableTuple9<A, B, C, D, E, F, G, H, I>(
    var a: A, var b: B, var c: C, var d: D, var e: E, var f: F, var g: G, var h: H, var i: I
) : MutableTuple {
    override val size: Int = 9
    override fun immutable() = Tuple9(a, b, c, d, e, f, g, h, i)
}



fun <A> Tuple2<A, A>.toList(): List<A> = listOf(a, b)
fun <A> Tuple3<A, A, A>.toList(): List<A> = listOf(a, b, c)
fun <A> Tuple4<A, A, A, A>.toList(): List<A> = listOf(a, b, c, d)
fun <A> Tuple5<A, A, A, A, A>.toList(): List<A> = listOf(a, b, c, d, e)
fun <A> Tuple6<A, A, A, A, A, A>.toList(): List<A> = listOf(a, b, c, d, e, f)
fun <A> Tuple7<A, A, A, A, A, A, A>.toList(): List<A> = listOf(a, b, c, d, e, f, g)
fun <A> Tuple8<A, A, A, A, A, A, A, A>.toList(): List<A> = listOf(a, b, c, d, e, f, g, h)
fun <A> Tuple9<A, A, A, A, A, A, A, A, A>.toList(): List<A> = listOf(a, b, c, d, e, f, g, h, i)
fun <A> Tuple2<A, A>.toMutableList(): MutableList<A> = mutableListOf(a, b)
fun <A> Tuple3<A, A, A>.toMutableList(): MutableList<A> = mutableListOf(a, b, c)
fun <A> Tuple4<A, A, A, A>.toMutableList(): MutableList<A> = mutableListOf(a, b, c, d)
fun <A> Tuple5<A, A, A, A, A>.toMutableList(): MutableList<A> = mutableListOf(a, b, c, d, e)
fun <A> Tuple6<A, A, A, A, A, A>.toMutableList(): MutableList<A> = mutableListOf(a, b, c, d, e, f)
fun <A> Tuple7<A, A, A, A, A, A, A>.toMutableList(): MutableList<A> = mutableListOf(a, b, c, d, e, f, g)
fun <A> Tuple8<A, A, A, A, A, A, A, A>.toMutableList(): MutableList<A> = mutableListOf(a, b, c, d, e, f, g, h)
fun <A> Tuple9<A, A, A, A, A, A, A, A, A>.toMutableList(): MutableList<A> = mutableListOf(a, b, c, d, e, f, g, h, i)
fun <A> Tuple2<A, A>.toSet(): Set<A> = setOf(a, b)
fun <A> Tuple3<A, A, A>.toSet(): Set<A> = setOf(a, b, c)
fun <A> Tuple4<A, A, A, A>.toSet(): Set<A> = setOf(a, b, c, d)
fun <A> Tuple5<A, A, A, A, A>.toSet(): Set<A> = setOf(a, b, c, d, e)
fun <A> Tuple6<A, A, A, A, A, A>.toSet(): Set<A> = setOf(a, b, c, d, e, f)
fun <A> Tuple7<A, A, A, A, A, A, A>.toSet(): Set<A> = setOf(a, b, c, d, e, f, g)
fun <A> Tuple8<A, A, A, A, A, A, A, A>.toSet(): Set<A> = setOf(a, b, c, d, e, f, g, h)
fun <A> Tuple9<A, A, A, A, A, A, A, A, A>.toSet(): Set<A> = setOf(a, b, c, d, e, f, g, h, i)
fun <A> Tuple2<A, A>.toMutableSet(): MutableSet<A> = mutableSetOf(a, b)
fun <A> Tuple3<A, A, A>.toMutableSet(): MutableSet<A> = mutableSetOf(a, b, c)
fun <A> Tuple4<A, A, A, A>.toMutableSet(): MutableSet<A> = mutableSetOf(a, b, c, d)
fun <A> Tuple5<A, A, A, A, A>.toMutableSet(): MutableSet<A> = mutableSetOf(a, b, c, d, e)
fun <A> Tuple6<A, A, A, A, A, A>.toMutableSet(): MutableSet<A> = mutableSetOf(a, b, c, d, e, f)
fun <A> Tuple7<A, A, A, A, A, A, A>.toMutableSet(): MutableSet<A> = mutableSetOf(a, b, c, d, e, f, g)
fun <A> Tuple8<A, A, A, A, A, A, A, A>.toMutableSet(): MutableSet<A> = mutableSetOf(a, b, c, d, e, f, g, h)
fun <A> Tuple9<A, A, A, A, A, A, A, A, A>.toMutableSet(): MutableSet<A> = mutableSetOf(a, b, c, d, e, f, g, h, i)
fun <A> Tuple2<A, A>.reverse() = Tuple2(b, a)
fun <A> Tuple3<A, A, A>.reverse() = Tuple3(c, b, a)
fun <A> Tuple4<A, A, A, A>.reverse() = Tuple4(d, c, b, a)
fun <A> Tuple5<A, A, A, A, A>.reverse() = Tuple5(e, d, c, b, a)
fun <A> Tuple6<A, A, A, A, A, A>.reverse() = Tuple6(f, e, d, c, b, a)
fun <A> Tuple7<A, A, A, A, A, A, A>.reverse() = Tuple7(g, f, e, d, c, b, a)
fun <A> Tuple8<A, A, A, A, A, A, A, A>.reverse() = Tuple8(h, g, f, e, d, c, b, a)
fun <A> Tuple9<A, A, A, A, A, A, A, A, A>.reverse() = Tuple9(i, h, g, f, e, d, c, b, a)

fun <A> MutableTuple2<A, A>.toList(): List<A> = listOf(a, b)
fun <A> MutableTuple3<A, A, A>.toList(): List<A> = listOf(a, b, c)
fun <A> MutableTuple4<A, A, A, A>.toList(): List<A> = listOf(a, b, c, d)
fun <A> MutableTuple5<A, A, A, A, A>.toList(): List<A> = listOf(a, b, c, d, e)
fun <A> MutableTuple6<A, A, A, A, A, A>.toList(): List<A> = listOf(a, b, c, d, e, f)
fun <A> MutableTuple7<A, A, A, A, A, A, A>.toList(): List<A> = listOf(a, b, c, d, e, f, g)
fun <A> MutableTuple8<A, A, A, A, A, A, A, A>.toList(): List<A> = listOf(a, b, c, d, e, f, g, h)
fun <A> MutableTuple9<A, A, A, A, A, A, A, A, A>.toList(): List<A> = listOf(a, b, c, d, e, f, g, h, i)
fun <A> MutableTuple2<A, A>.toMutableList(): MutableList<A> = mutableListOf(a, b)
fun <A> MutableTuple3<A, A, A>.toMutableList(): MutableList<A> = mutableListOf(a, b, c)
fun <A> MutableTuple4<A, A, A, A>.toMutableList(): MutableList<A> = mutableListOf(a, b, c, d)
fun <A> MutableTuple5<A, A, A, A, A>.toMutableList(): MutableList<A> = mutableListOf(a, b, c, d, e)
fun <A> MutableTuple6<A, A, A, A, A, A>.toMutableList(): MutableList<A> = mutableListOf(a, b, c, d, e, f)
fun <A> MutableTuple7<A, A, A, A, A, A, A>.toMutableList(): MutableList<A> = mutableListOf(a, b, c, d, e, f, g)
fun <A> MutableTuple8<A, A, A, A, A, A, A, A>.toMutableList(): MutableList<A> = mutableListOf(a, b, c, d, e, f, g, h)
fun <A> MutableTuple9<A, A, A, A, A, A, A, A, A>.toMutableList(): MutableList<A> = mutableListOf(a, b, c, d, e, f, g, h, i)
fun <A> MutableTuple2<A, A>.toSet(): Set<A> = setOf(a, b)
fun <A> MutableTuple3<A, A, A>.toSet(): Set<A> = setOf(a, b, c)
fun <A> MutableTuple4<A, A, A, A>.toSet(): Set<A> = setOf(a, b, c, d)
fun <A> MutableTuple5<A, A, A, A, A>.toSet(): Set<A> = setOf(a, b, c, d, e)
fun <A> MutableTuple6<A, A, A, A, A, A>.toSet(): Set<A> = setOf(a, b, c, d, e, f)
fun <A> MutableTuple7<A, A, A, A, A, A, A>.toSet(): Set<A> = setOf(a, b, c, d, e, f, g)
fun <A> MutableTuple8<A, A, A, A, A, A, A, A>.toSet(): Set<A> = setOf(a, b, c, d, e, f, g, h)
fun <A> MutableTuple9<A, A, A, A, A, A, A, A, A>.toSet(): Set<A> = setOf(a, b, c, d, e, f, g, h, i)
fun <A> MutableTuple2<A, A>.toMutableSet(): MutableSet<A> = mutableSetOf(a, b)
fun <A> MutableTuple3<A, A, A>.toMutableSet(): MutableSet<A> = mutableSetOf(a, b, c)
fun <A> MutableTuple4<A, A, A, A>.toMutableSet(): MutableSet<A> = mutableSetOf(a, b, c, d)
fun <A> MutableTuple5<A, A, A, A, A>.toMutableSet(): MutableSet<A> = mutableSetOf(a, b, c, d, e)
fun <A> MutableTuple6<A, A, A, A, A, A>.toMutableSet(): MutableSet<A> = mutableSetOf(a, b, c, d, e, f)
fun <A> MutableTuple7<A, A, A, A, A, A, A>.toMutableSet(): MutableSet<A> = mutableSetOf(a, b, c, d, e, f, g)
fun <A> MutableTuple8<A, A, A, A, A, A, A, A>.toMutableSet(): MutableSet<A> = mutableSetOf(a, b, c, d, e, f, g, h)
fun <A> MutableTuple9<A, A, A, A, A, A, A, A, A>.toMutableSet(): MutableSet<A> = mutableSetOf(a, b, c, d, e, f, g, h, i)
fun <A> MutableTuple2<A, A>.reverse() = MutableTuple2(b, a)
fun <A> MutableTuple3<A, A, A>.reverse() = MutableTuple3(c, b, a)
fun <A> MutableTuple4<A, A, A, A>.reverse() = MutableTuple4(d, c, b, a)
fun <A> MutableTuple5<A, A, A, A, A>.reverse() = MutableTuple5(e, d, c, b, a)
fun <A> MutableTuple6<A, A, A, A, A, A>.reverse() = MutableTuple6(f, e, d, c, b, a)
fun <A> MutableTuple7<A, A, A, A, A, A, A>.reverse() = MutableTuple7(g, f, e, d, c, b, a)
fun <A> MutableTuple8<A, A, A, A, A, A, A, A>.reverse() = MutableTuple8(h, g, f, e, d, c, b, a)
fun <A> MutableTuple9<A, A, A, A, A, A, A, A, A>.reverse() = MutableTuple9(i, h, g, f, e, d, c, b, a)



fun <A> List<A>.toTuple(): Tuple = if (size < 2)
    throw IllegalArgumentException("size < 2.")
else
    when (size) {
        2 -> Tuple2(get(0), get(1))
        3 -> Tuple3(get(0), get(1), get(2))
        4 -> Tuple4(get(0), get(1), get(2), get(3))
        5 -> Tuple5(get(0), get(1), get(2), get(3), get(4))
        6 -> Tuple6(get(0), get(1), get(2), get(3), get(4), get(5))
        7 -> Tuple7(get(0), get(1), get(2), get(3), get(4), get(5), get(6))
        8 -> Tuple8(get(0), get(1), get(2), get(3), get(4), get(5), get(6), get(7))
        else -> Tuple9(get(0), get(1), get(2), get(3), get(4), get(5), get(6), get(7), get(8))
    }

fun <A> List<A>.toMutableTuple(): MutableTuple = if (size < 2)
    throw IllegalArgumentException("size < 2.")
else
    when (size) {
        2 -> MutableTuple2(get(0), get(1))
        3 -> MutableTuple3(get(0), get(1), get(2))
        4 -> MutableTuple4(get(0), get(1), get(2), get(3))
        5 -> MutableTuple5(get(0), get(1), get(2), get(3), get(4))
        6 -> MutableTuple6(get(0), get(1), get(2), get(3), get(4), get(5))
        7 -> MutableTuple7(get(0), get(1), get(2), get(3), get(4), get(5), get(6))
        8 -> MutableTuple8(get(0), get(1), get(2), get(3), get(4), get(5), get(6), get(7))
        else -> MutableTuple9(get(0), get(1), get(2), get(3), get(4), get(5), get(6), get(7), get(8))
    }



fun <A, B> Pair<A, B>.toTuple(): Tuple2<A, B> = Tuple2(first, second)
fun <A, B> Pair<A, B>.toMutableTuple(): MutableTuple2<A, B> = MutableTuple2(first, second)
fun <A, B, C> Triple<A, B, C>.toTuple(): Tuple3<A, B, C> = Tuple3(first, second, third)
fun <A, B, C> Triple<A, B, C>.toMutableTuple(): MutableTuple3<A, B, C> = MutableTuple3(first, second, third)

fun <A, B> Tuple2<A, B>.toPair(): Pair<A, B> = Pair(a, b)
fun <A, B> MutableTuple2<A, B>.toPair(): Pair<A, B> = Pair(a, b)
fun <A, B, C> Tuple3<A, B, C>.toTriple(): Triple<A, B, C> = Triple(a, b, c)
fun <A, B, C> MutableTuple3<A, B, C>.toTriple(): Triple<A, B, C> = Triple(a, b, c)