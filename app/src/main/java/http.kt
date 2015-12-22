package nl.mplatvoet.komponents.kovenant.android.demo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.result.Result
import nl.komponents.kovenant.Promise
import nl.komponents.kovenant.deferred
import nl.komponents.kovenant.then
import java.io.ByteArrayInputStream

public class FuelHttpService {
    public fun textUrl(url: String, parameters: List<Pair<String, Any?>>? = null): Promise<String, Exception> {
        return Fuel.get(url, parameters).promise() then {
            val (response, array) = it
            //should get encoding from the response.
            String(array, "UTF-8")
        }
    }

    public fun bitmapUrl(url: String): Promise<Bitmap, Exception> {
        return Fuel.get(url).promise() then {
            BitmapFactory.decodeStream(ByteArrayInputStream(it.second))
        }
    }
}


public fun Request.promise(): Promise<Pair<Response, ByteArray>, Exception> {
    val deferred = deferred<Pair<Response, ByteArray>, Exception>()
    response { request, response, result ->
        when (result) {
            is Result.Failure -> deferred.reject(result.error!!)
            is Result.Success -> deferred.resolve(Pair(response, result.value!!))
        }
    }
    return deferred.promise
}


