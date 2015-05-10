package nl.mplatvoet.komponents.kovenant.android.demo

import argo.jdom.JdomParser
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
            items add Item(name)
        }
        return Result(items)
    }
}

data class Result(val items: List<Item>)

data class Item(val name: String)
