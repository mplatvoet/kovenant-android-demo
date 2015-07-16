package nl.mplatvoet.komponents.kovenant.android.demo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import nl.komponents.progress.ProgressControl
import nl.komponents.progress.SingleProgressControl
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


public class HttpGetService {
    public fun textUrl(url: String, progressControl: SingleProgressControl? = null): String {
        val sb = StringBuilder()
        val streamed = stream(url, progressControl) {
            it.reader() forEachLine { line -> sb.append(line) }
        }
        return if (streamed) sb.toString() else ""
    }

    public fun bitmapUrl(url: String, progressControl: SingleProgressControl? = null): Bitmap {
        stream(url, progressControl) {

            return BitmapFactory.decodeStream(it)
        }
        throw Exception("could not load $url")
    }

    private inline fun stream(url: String,
                              progressControl: SingleProgressControl?,
                              fn: (BufferedInputStream) -> Unit): Boolean {
        val conn = URL(url).openConnection() as HttpURLConnection
        try {
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                val length = conn.getContentLength()

                val inputStream = if (progressControl != null && length > -1) {
                    ProgressInputStream(progressControl, 8L * length, conn.getInputStream())
                } else {
                    conn.getInputStream()
                }

                BufferedInputStream(inputStream) use { fn(it) }
                return true
            }
        } finally {
            conn.disconnect();
        }
        return false
    }
}

private class ProgressInputStream(private val progressControl: SingleProgressControl,
                                  length: Long,
                                  private val inputStream: InputStream) : InputStream() {
    private var read = 0L
    private val total = length.toDouble()
    override fun read(): Int {
        val i = inputStream.read()
        updateProgress(if (i < 0) -1L else 1)
        return i
    }


    override fun read(buffer: ByteArray?, byteOffset: Int, byteCount: Int): Int {
        val i = inputStream.read(buffer, byteOffset, byteCount)
        updateProgress(i.toLong())
        return i
    }


    override fun read(buffer: ByteArray?): Int {
        val i = inputStream.read(buffer)
        updateProgress(i.toLong())
        return i
    }

    override fun available(): Int {
        return inputStream.available()
    }


    override fun skip(byteCount: Long): Long {
        val i = inputStream.skip(byteCount)
        updateProgress(i)
        return i
    }

    private fun updateProgress(length: Long) {
        if (length < 0) {
            progressControl.markAsDone()
        } else {
            read += length
            progressControl.value = read / total
        }
    }

    override fun close() {
        inputStream.close()
    }
}
