package util.datamining.clustering.util

interface CLClus<T> {
    fun distance(y: T): Double
    fun copy(): T
}