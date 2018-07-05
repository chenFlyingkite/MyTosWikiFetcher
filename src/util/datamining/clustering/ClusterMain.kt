package util.datamining.clustering

import util.datamining.clustering.algo.ClusteringBySynchronization
import util.datamining.clustering.util.Clusterable
import util.logging.L

object ClusterMain {


    fun main(args : Array<String>) {
        val dataset = get_1D()
        L.log("set = $dataset")


        //CLHistogram().run()
    }

    private fun runSync(dataset: List<Data>) {
        val cs = ClusteringBySynchronization<Data>()
        cs.setDataSet(dataset)
        cs.run()
    }

    private fun get_1D(): List<Data> {
        val list = ArrayList<Data>()
        val s = arrayOf(1, 5, 3, 4, 2, 9, 10)
        for (i in s) {
        //for (i in 1..10) {
            val d = Data()
            d.x = i * 1.0
            list.add(d)
        }
        return list
    }

    class Data : Clusterable<Data> {
        override fun toString(): String {
            return "$x"
        }
        var x = 0.0
        override fun distance(y: Data): Double {
            return Math.abs(x - y.x)
        }

        override fun copy(): Data {
            val d = Data()
            d.x = x
            return d
        }

        override fun getDimension(): Int {
            return 1
        }

        override fun getXi(i: Int): Double {
            return x
        }

        override fun setXi(i: Int, xi: Double) {
            x = xi
        }

    }

}