package nl.mplatvoet.komponents.kovenant.android.demo

import java.io.BufferedInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


public class HttpGetService {
    public fun textUrl(url: String): String {
        val sb = StringBuilder()
        val streamed = stream(url) {
            it.reader() forEachLine { line -> sb.append(line) }
        }
        return if (streamed) sb.toString() else ""
    }

    private inline fun stream(url: String, fn: (BufferedInputStream) -> Unit): Boolean {
        val conn = URL(url).openConnection() as HttpURLConnection
        try {
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedInputStream(conn.getInputStream()) use { fn(it) }
                return true
            }
        } finally {
            conn.disconnect();
        }
        return false
    }
}