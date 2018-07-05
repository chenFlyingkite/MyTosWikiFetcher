package util.datamining.clustering.algo

import util.datamining.clustering.util.CLClus
import util.datamining.clustering.util.IDPair
import util.logging.LF
import util.math.Statistics
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
        log.setLogToL(false)
        val groupIds = ArrayList<Int>()
        var groupCount = NO_ID
        var groupI: Int
        var subdata: List<T>
        val preKGroups = ArrayList<Int>()
        for (i in 0 until dataset.size) {
            subdata = dataset.subList(0, i)
            preKGroups.clear()
            val x = dataset[i]
            val neighbor = getKNN(x, i, 31, subdata)
            log.log("--> #$i : x = $x")

            if (neighbor.size == 0) {
                groupCount++
                groupI = groupCount
            } else {
                // Take the list's ids out
                val ids = ArrayList<Int>()
                for (id in neighbor) {
                    ids.add(groupIds[id.k])
                }
                val modes = Statistics.mode(ids)
                log.log("    ids = $ids, mode = $modes")
                groupI = ids[modes[0]] // First mode's group number
            }
            groupIds.add(groupI)
            log.log("  in g# = $groupI, NN = $neighbor")
            log.log("  => groupIds = $groupIds")
        }

        log.setLogToL(true)
        // init
        val grouping = ArrayList<ArrayList<T>>()
        for (i in 0..groupCount) {
            grouping.add(ArrayList())
        }
        for (i in 0 until dataset.size) {
            val gi = grouping[groupIds[i]]
            gi.add(dataset[i])
        }
        log.log("--- Group done with ----")
        for (i in 0..groupCount) {
            log.log("#$i => ${grouping[i]}")
        }
        log.log("--- End ----")

        log.file.close()
    }

    private fun getKNN(x: T, xIndex: Int, k: Int, set: List<T>): List<IDPair> {
        val indices = ArrayList<IDPair>()
        for (i in 0 until set.size) {
            // Omit index of x itself
            if (i == xIndex) continue

            val y = set[i]
            if (x.isSimilarTo(y)) {
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
        }
        return indices
    }


}