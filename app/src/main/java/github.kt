package nl.mplatvoet.komponents.kovenant.android.demo

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.util.LruCache
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import nl.komponents.kovenant.Promise
import nl.komponents.kovenant.functional.unwrap
import nl.komponents.kovenant.jvm.Throttle
import nl.komponents.kovenant.then
import nl.komponents.kovenant.ui.failUi
import nl.komponents.kovenant.ui.successUi
import org.jetbrains.anko.dip
import org.jetbrains.anko.find
import org.jetbrains.anko.listView
import org.jetbrains.anko.toast
import uy.kohesive.injekt.injectLazy


val url = "https://api.github.com/search/repositories"


public class GithubActivity : Activity() {
    val searchParser: GithubSearchJsonParser by injectLazy()
    val fuelService: FuelHttpService by injectLazy()
    val imageThrottle = Throttle()

    val cache = LruCache<String, Promise<Bitmap, Exception>>(100)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // returns a promise
        val resultPromise = fuelService.textUrl(url, listOf(
                "q" to "android language:kotlin",
                "sort" to "updated",
                "order" to "desc"))

        // parse the results and display them
        resultPromise.then { msg ->
            searchParser.parse(msg)
        } successUi {
            // For the first time we are going to touch the UI
            // This is the only code operating on the UI thread.
            result ->
            addResultToView(result)
        } failUi {

            //If somewhere in the chain something went wrong
            toast("${it.message}")
        }
    }

    private fun addResultToView(result: Result) {
        listView {
            adapter = ListAdapter(
                    result.items,
                    { parent -> createView(parent) },
                    { view, id, item -> populateView(view, item) }
            )
        }
    }

    private fun populateView(view: View, item: Item) {
        view.find<TextView>(R.id.text).text = item.name
        view.find<TextView>(R.id.stars).text = item.stars
        view.find<TextView>(R.id.forks).text = item.forks

        val imageView = view.find<ImageView>(R.id.image)

        item.bitmap() successUi  {
            imageView.setImageBitmap(it)
        }

    }

    private fun Item.bitmap(): Promise<Bitmap, Exception> {
        val cached = cache.get(imageUrl)
        if (cached != null) return cached

        val promise = imageThrottle.task {
            fuelService.bitmapUrl(imageUrl) then { bitmap ->
                val scaled = Bitmap.createScaledBitmap(bitmap, dip(50), dip(50), false)
                //returns the same instance if there is nothing
                //to resize
                if (scaled != bitmap) bitmap.recycle()
                scaled
            }
        }.unwrap()
        cache.put(imageUrl, promise)
        return promise
    }

    private fun createView(parent: ViewGroup): View
            = layoutInflater.inflate(R.layout.list_item, parent, false)

}

