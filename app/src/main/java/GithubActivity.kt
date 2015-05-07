package nl.mplatvoet.komponents.kovenant.android.demo

import android.app.LauncherActivity
import android.app.ListActivity
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.google.gson.*
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import nl.mplatvoet.komponents.kovenant.android.demo.support.GithubDeserializer
import nl.mplatvoet.komponents.kovenant.android.failUi
import nl.mplatvoet.komponents.kovenant.android.successUi
import nl.mplatvoet.komponents.kovenant.async
import nl.mplatvoet.komponents.kovenant.then
import org.jetbrains.anko.toast
import java.lang.reflect.Type
import java.net.Proxy
import java.util.ArrayList
import kotlin.platform.platformStatic
import kotlin.properties.Delegates

public class GithubActivity : ListActivity() {
    val url = "https://api.github.com/search/repositories?q=android+language:kotlin&sort=updated&order=desc"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        async {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            response.body().string()
        } then {
            val gson = GsonBuilder()
                    .serializeNulls()
                    .registerTypeAdapter(javaClass<List<String>>(), GithubDeserializer())
                    .create()

            gson.fromJson(it, javaClass<List<String>>())
        } successUi {
            val adapter = ArrayAdapter(this, R.layout.list_item, it)
            setListAdapter(adapter)
        } failUi {
            toast("${it.getMessage()}")
        }


    }
}

