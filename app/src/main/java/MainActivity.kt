package nl.mplatvoet.komponents.kovenant.android.demo

import android.R
import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import nl.mplatvoet.komponents.kovenant.android.Disposable
import nl.mplatvoet.komponents.kovenant.android.configureKovenant
import org.jetbrains.anko.*

class MainActivity : Activity() {
    var disposable : Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        disposable = configureKovenant()

        val customStyle = { v: Any ->
            when (v) {
                is Button -> v.textSize = 26f
                is EditText -> v.textSize = 24f
            }
        }

        verticalLayout {
            padding = dip(32)

            imageView(R.drawable.ic_menu_manage).layoutParams {
                margin = dip(16)
                gravity = Gravity.CENTER
            }

            button("Load") {
                onClick {
                    startActivity<GithubActivity>()
                }
            }
        }.style(customStyle)
    }

    override fun onDestroy() {
        disposable?.close()
    }
}