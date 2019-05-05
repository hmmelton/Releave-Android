package com.hmmelton.releave.views.viewmodels

import android.arch.lifecycle.ViewModel
import com.hmmelton.releave.data.SaveRestroomCallback
import com.hmmelton.releave.data.models.Restroom
import com.hmmelton.releave.data.models.RestroomRequestBody
import com.hmmelton.releave.services.ReleaveClient
import com.hmmelton.releave.services.ReleaveService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.LinkedList
import java.util.Queue

class MainActivityViewModel : ViewModel(), SaveRestroomCallback {

    /**
     * Used to alert the user of an error saving a restroom
     */
    var errorHandler: (() -> Unit)? = null

    private val releaveService: ReleaveService by lazy { ReleaveClient.INSTANCE }
    private val requestQueue: Queue<Call<Restroom>> = LinkedList()
    private var call: Call<Restroom>? = null

    private val saveRestroomNetworkCallback = object : Callback<Restroom> {
        override fun onFailure(call: Call<Restroom>, t: Throwable) {
            processNextRequest()
            errorHandler?.invoke()
        }

        override fun onResponse(call: Call<Restroom>, response: Response<Restroom>) {
            processNextRequest()
        }
    }

    override fun onSaveRestroom(requestBody: RestroomRequestBody) {
        val call = releaveService.addRestroom(requestBody = requestBody)
        synchronized(requestQueue) {
            requestQueue.add(call)

            // If the queue's size is not 1, it is either empty or already processing its contents
            if (requestQueue.count() == 1) {
                processNextRequest()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        call?.cancel()
    }

    /**
     * This function attempts to save a restroom to the server.
     */
    private fun processNextRequest() {
        synchronized(requestQueue) {
            if (!requestQueue.isEmpty()) {
                call = requestQueue.remove()
                call?.enqueue(saveRestroomNetworkCallback)
            }
        }
    }
}
