package main.fetcher.web

import flyingkite.tool.TicTac2
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException

open class WebFetcher {
//    fun sendRequest(link : String, onWeb: OnWebFetch?) : String {
//        val client = OkHttpClient()
//        val req = Request.Builder().url(link).build()
//        var body = ""
//        try {
//            onWeb?.onPreExecute(link)
//            val res = client.newCall(req).execute()
//            onWeb?.onPostExecute()
//            body = res.body()?.string() ?: ""
//        } catch (e: IOException) {
//            e.printStackTrace()
//        } finally {
//            onWeb?.onDestroy()
//        }
//        return body
//    }

    fun getDocument(link : String) : Document? {
        // Step 1: Get the xml node from link by Jsoup
        var doc:Document? = null
        val ts = TicTac2()
        ts.tic()
        try {
            doc = Jsoup.connect(link).timeout(60_000).maxBodySize(0).get()
        } catch (e : IOException) {
            e.printStackTrace()
        }
        ts.tac("JSoup OK $link")
        return doc
    }

    fun sendRequest(link : String, onWeb: OnWebFetch?) : String {
        val client = OkHttpClient()
        val req = Request.Builder().url(link).build()
        var body = ""
        try {
            onWeb?.onPreExecute(link)
            val res = client.newCall(req).execute()
            onWeb?.onPostExecute()
            body = res.body()?.string() ?: ""
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            onWeb?.onDestroy()
        }
        return body
    }

//    fun sendAndParseDom(link: String, onWeb: OnWebFetch?) : Document {
//        return Jsoup.parse(sendRequest(link, onWeb))
//    }
}