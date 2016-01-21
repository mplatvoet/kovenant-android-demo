package nl.mplatvoet.komponents.kovenant.android.demo

import android.app.Activity
import android.os.Bundle
import com.github.kittinunf.fuel.core.Manager
import nl.komponents.kovenant.Kovenant
import nl.komponents.kovenant.android.startKovenant
import nl.komponents.kovenant.android.stopKovenant
import nl.komponents.kovenant.jvm.asExecutorService
import org.jetbrains.anko.*
import uy.kohesive.injekt.InjektMain
import uy.kohesive.injekt.api.InjektRegistrar
import uy.kohesive.injekt.api.addSingleton

class MainActivity : Activity() {

    companion object : InjektMain() {
        override fun InjektRegistrar.registerInjectables() {
            addSingleton(GithubSearchJsonParser())
            addSingleton(FuelHttpService())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configure Kovenant with standard dispatchers
        // suitable for an Android environment.
        // It's just convenience, you can still use
        // `Kovenant.configure { }` if you want to keep
        // matters in hand.
        startKovenant()

        verticalLayout {
            padding = dip(32)

            button("Load") {
                onClick { startActivity<GithubActivity>() }
            }
        }
    }

    override fun onDestroy() {

        // Dispose of the Kovenant thread pools.
        // For quicker shutdown you could use
        // `force=true`, which ignores all current
        // scheduled tasks
        stopKovenant()
        super.onDestroy()
    }
}