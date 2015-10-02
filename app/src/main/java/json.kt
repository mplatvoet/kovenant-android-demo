package nl.mplatvoet.komponents.kovenant.android.demo

import argo.jdom.JdomParser
import java.util.*

class GithubSearchJsonParser {
    companion object {
        val parser = JdomParser()
    }

    public fun parse(text: String): Result {
        val rootNode = parser.parse(text)

        val items = ArrayList<Item>()
        rootNode.getArrayNode("items") forEach {
            val name = it.getStringValue("name")
            val stars = it.getNumberValue("stargazers_count")
            val forks = it.getNumberValue("forks")
            val owner = it.getNode("owner")
            val imageUrl = owner.getStringValue("avatar_url")

            items add Item(name, imageUrl, forks = forks, stars = stars)
        }
        return Result(items)
    }
}

data class Result(val items: List<Item>)

data class Item(val name: String,
                val imageUrl: String,
                val forks: String,
                val stars: String)
