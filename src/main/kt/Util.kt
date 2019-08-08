package main.kt

import flyingkite.log.L

class Util {
    companion object me {

        // Returns the string of src, head ~ tail part
        fun stringWithin(src : String, head : String, tail : String) : String {
            return src.substringAfter(head).substringBefore(tail)
        }

        fun <T> print(a: List<T>?) {
            val n = a?.size ?: 0
            L.log("%s items", n)
            for (i in 0 until n) {
                L.log("#%2d : %s", i, a!![i])
            }
        }
    }
}