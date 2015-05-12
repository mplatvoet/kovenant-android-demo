package nl.mplatvoet.komponents.kovenant.android.demo

import android.app.Activity
import android.app.ListActivity
import android.content.Context
import android.database.DataSetObserver
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import nl.mplatvoet.komponents.kovenant.android.failUi
import nl.mplatvoet.komponents.kovenant.android.successUi
import nl.mplatvoet.komponents.kovenant.combine.and
import nl.mplatvoet.komponents.kovenant.properties.lazyPromise
import nl.mplatvoet.komponents.kovenant.then
import nl.mplatvoet.komponents.kovenant.thenUse
import org.jetbrains.anko.*


val url = "https://api.github.com/search/repositories?q=android+language:kotlin&sort=updated&order=desc"


/*
Using a lazy promises.

This promise gets initialized on a background thread upon first access.
So it's safe to call from the UI Thread.
 */
val searchParser by lazyPromise { GithubSearchJsonParser() }
val httpGetService by lazyPromise { HttpGetService() }

public class GithubActivity : Activity() {

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
        } successUi {
            // For the first time we are going to touch the UI
            // This is the only code operating on the UI thread.
            result ->
            addResultToView(result)
        } failUi {

            //If somewhere in the chain something went wrong
            toast("${it.getMessage()}")
        }
    }

    private fun addResultToView(result: Result) {
        listView {
            setAdapter(ListAdapter(
                    result.items,
                    { parent -> createView(parent) },
                    { view, id, item -> populateView(view, item) }
            ))
        }
    }

    private fun populateView(view: View, item: Item) {
        val textView = view.find<TextView>(R.id.text)
        textView.setText(item.name)

        val imageView = view.find<ImageView>(R.id.image)

        item.image then {
            Bitmap.createScaledBitmap(it, dip(50), dip(50), false)
        } successUi  {
            imageView.setImageBitmap(it)
        }

    }

    private fun createView(parent: ViewGroup): View
            = layoutInflater.inflate(R.layout.list_item, parent, false)

}

