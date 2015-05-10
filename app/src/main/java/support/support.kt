package nl.mplatvoet.komponents.kovenant.android.demo.support

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.util.ArrayList


open class GithubDeserializer : JsonDeserializer<List<String>> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): List<String> {

        val root = json.getAsJsonObject()
        val itemsArray = root.getAsJsonArray("items")

        val results = ArrayList<String>()
        itemsArray forEach {
            results add it.getAsJsonObject().get("name").getAsString()
        }

        return results
    }
}
