package nl.mplatvoet.komponents.kovenant.android.demo

import android.app.Activity
import android.os.Bundle
import nl.mplatvoet.komponents.kovenant.android.startKovenant
import nl.mplatvoet.komponents.kovenant.android.stopKovenant
import org.jetbrains.anko.*

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startKovenant()

        verticalLayout {
            padding = dip(32)

            button("Load") {
                onClick { startActivity<GithubActivity>() }
            }
        }
    }

    override fun onDestroy() {
        stopKovenant()
        super.onDestroy()
    }
}