package nl.mplatvoet.komponents.kovenant.android.demo

import android.app.ListActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import nl.mplatvoet.komponents.kovenant.android.failUi
import nl.mplatvoet.komponents.kovenant.android.successUi
import nl.mplatvoet.komponents.kovenant.combine.and
import nl.mplatvoet.komponents.kovenant.properties.lazyPromise
import nl.mplatvoet.komponents.kovenant.then
import nl.mplatvoet.komponents.kovenant.thenUse
import org.jetbrains.anko.toast


val url = "https://api.github.com/search/repositories?q=android+language:kotlin&sort=updated&order=desc"

val searchParser by lazyPromise {
    GithubSearchJsonParser()
}

val httpGetService by lazyPromise { HttpGetService() }

public class GithubActivity : ListActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        httpGetService thenUse {
            textUrl(url)
        } and searchParser then { tuple ->
            val (msg, parser) = tuple
            parser.parse(msg)
        } successUi {
            val strings = it.items map { it.name }
            val adapter = ArrayAdapter(this, R.layout.list_item, strings)
            setListAdapter(adapter)
        } failUi {
            toast("${it.getMessage()}")
        }
    }
}

