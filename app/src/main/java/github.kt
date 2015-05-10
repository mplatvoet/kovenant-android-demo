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


/*
Using a lazy promises.

This promise gets initialized on a background thread upon first access.
So it's safe to call from the UI Thread.
 */
val searchParser by lazyPromise { GithubSearchJsonParser() }
val httpGetService by lazyPromise { HttpGetService() }

public class GithubActivity : ListActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Use the lazyPromise HttpGetService.
        // So this gets initialized on a background thread now
        httpGetService thenUse {
            // use the service and retrieve the url
            // `thenUse` keeps everything on background threads
            textUrl(url)

        // use `and` to combine the result of the `textUrl` call
        // with the lazy loaded searchParser
        } and searchParser then { tuple ->
            //now we can use the result and parser together
            val (msg, parser) = tuple
            parser.parse(msg)
        } then {
            // do a transformation from Item to String
            it.items map { it.name }
        } successUi {
            // For the first time we are going to touch the UI
            // This is the only code operating on the UI thread.
            val adapter = ArrayAdapter(this, R.layout.list_item, it)
            setListAdapter(adapter)
        } failUi {

            //If somewhere in the chain something went wrong
            toast("${it.getMessage()}")
        }
    }
}

