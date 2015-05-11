package nl.mplatvoet.komponents.kovenant.android.demo

import android.graphics.Bitmap
import argo.jdom.JdomParser
import nl.mplatvoet.komponents.kovenant.properties.lazyPromise
import java.util.ArrayList

class GithubSearchJsonParser {
    companion object {
        val parser = JdomParser()
    }

    public fun parse(text: String): Result {
        val rootNode = parser.parse(text)

        val items = ArrayList<Item>()
        rootNode.getArrayNode("items") forEach {
            val name = it.getStringValue("name")
            val owner = it.getNode("owner")
            val imageUrl = owner.getStringValue("avatar_url")

            items add Item(name, imageUrl)
        }
        return Result(items)
    }
}

data class Result(val items: List<Item>)

data class Item(val name: String, imageUrl: String) {
    companion object {
        private val service = HttpGetService()
    }

    val image by lazyPromise {
        service.bitmapUrl(imageUrl)
    }
}
