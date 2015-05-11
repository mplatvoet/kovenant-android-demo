package nl.mplatvoet.komponents.kovenant.android.demo

import android.database.DataSetObserver
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import java.util.concurrent.ConcurrentLinkedQueue

public class ListAdapter<T, V : View>(private val items: List<T>,
                                      private val viewFactory: (id: Int, item: T, reusable: V?) -> V) : ObservableAdapter() {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? = viewFactory(position, items[position], convertView as V?)

    override fun isEnabled(position: Int): Boolean = true
    override fun areAllItemsEnabled(): Boolean =  true
    override fun isEmpty(): Boolean = items.isEmpty()
    override fun getItemViewType(position: Int): Int = Adapter.IGNORE_ITEM_VIEW_TYPE
    override fun getItem(position: Int): Any? = items[position]
    override fun getViewTypeCount(): Int = 1
    override fun getItemId(position: Int): Long = position.toLong()
    override fun hasStableIds(): Boolean = false
    override fun getCount(): Int = items.size()
}

private abstract class ObservableAdapter : android.widget.ListAdapter {
    private val observers = ConcurrentLinkedQueue<DataSetObserver>()

    override fun unregisterDataSetObserver(observer: DataSetObserver?) {
        if (observer != null) observers remove observer
    }

    override fun registerDataSetObserver(observer: DataSetObserver?) {
        if (observer != null) observers add observer
    }

    protected fun notifyObservers(): Unit = observers forEach {
        it.onChanged()
    }
}
