package nl.mplatvoet.komponents.kovenant.android.demo

import android.app.Activity
import android.app.Application
import android.os.Bundle
import nl.komponents.kovenant.android.startKovenant
import nl.komponents.kovenant.android.stopKovenant
import org.jetbrains.anko.*
import uy.kohesive.injekt.InjektMain
import uy.kohesive.injekt.api.InjektRegistrar
import uy.kohesive.injekt.api.addSingleton

class DemoApplication : Application() {

    companion object : InjektMain() {
        override fun InjektRegistrar.registerInjectables() {
            addSingleton(GithubSearchJsonParser())
            addSingleton(FuelHttpService())
        }

    }

    override fun onCreate() {
        super.onCreate()
        // Configure Kovenant with standard dispatchers
        // suitable for an Android environment.
        // It's just convenience, you can still use
        // `Kovenant.configure { }` if you want to keep
        // matters in hand.
        startKovenant()
    }

    override fun onTerminate() {
        super.onTerminate()
        // Dispose of the Kovenant thread pools.
        // For quicker shutdown you could use
        // `force=true`, which ignores all current
        // scheduled tasks
        stopKovenant()
    }
}

class MainActivity : Activity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        verticalLayout {
            padding = dip(32)

            button("Load") {
                onClick { startActivity<GithubActivity>() }
            }
        }
    }
}