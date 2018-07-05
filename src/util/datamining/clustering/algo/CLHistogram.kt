package util.datamining.clustering.algo

import util.datamining.clustering.util.CLClus
import util.datamining.clustering.util.IDPair
import util.logging.LF
import java.util.*
import kotlin.collections.ArrayList

class CLHistogram<T: CLClus<T>> : Runnable {
    private val dataset = ArrayList<T>()

    private val folder = "datamining/clustering/CLhis"
    private val log = LF(folder)

    private val NO_ID = -1

    fun setDataSet(set: List<T>): CLHistogram<T> {
        copy(set, dataset)

        return this
    }

    private fun copy(set: List<T>, list: MutableList<T>): List<T> {
        list.clear()
        for (i in 0 until set.size) {
            list.add(set[i].copy())
        }
        return list
    }
    //private val result = ClusterResult()

    private fun setData() {

    }

    override fun run() {
        log.file.open(false)

        val groupIds = ArrayList<Int>()
        var group = NO_ID
        var subdata: List<T>
        val preKGroups = ArrayList<Int>()
        for (i in 0 until dataset.size) {
            subdata = dataset.subList(0, i)
            preKGroups.clear()
            //getKNN()
            for (j in 0 until i) {
                preKGroups.add(j)
                //groups.add()
                var y = dataset[j]

            }
            //result.clusterIds[i] = group

            //result
            groupIds.add(group)

        }

        //val c = ClusteringBySynchronization.ClusterInfo()

        log.file.close()
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun getKNN(x: T, xIndex: Int, k: Int, set: List<T>): List<IDPair> {
        val indices = ArrayList<IDPair>()
        for (i in 0 until set.size) {
            // Omit index of x itself
            if (i == xIndex) continue

            val y = set[i]
            val dist = x.distance(y)
            val p = IDPair(i, dist)
            //val n = Collections.binarySearch(knnIndices, p, compareBy({it.v}, {it.k}))
            val n = Collections.binarySearch(indices, p)

            if (n < 0) {
                indices.add(-n-1, p)
            }
            if (indices.size > k) {
                indices.removeAt(k)
            }
        }
        return indices
    }


}