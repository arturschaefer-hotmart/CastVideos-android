package com.google.sample.cast.refplayer.utils

import android.content.Context
import android.graphics.Bitmap
import androidx.collection.LruCache
import com.android.volley.Cache
import com.android.volley.Network
import com.android.volley.RequestQueue
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.ImageLoader.ImageCache

class CustomVolleyRequest private constructor(context: Context) {
    private var requestQueue: RequestQueue?
    val imageLoader: ImageLoader
    private fun getRequestQueue(): RequestQueue {
        if (requestQueue == null) {
            val cache: Cache = DiskBasedCache(context.cacheDir, 10 * 1024 * 1024)
            val network: Network = BasicNetwork(HurlStack())
            requestQueue = RequestQueue(cache, network)
            requestQueue!!.start()
        }
        return requestQueue!!
    }

    companion object {
        private var customVolleyRequest: CustomVolleyRequest? = null
        lateinit var context: Context

        @JvmStatic
        @Synchronized
        fun getInstance(context: Context): CustomVolleyRequest? {
            if (customVolleyRequest == null) {
                customVolleyRequest = CustomVolleyRequest(context)
            }
            return customVolleyRequest
        }
    }

    init {
        requestQueue = getRequestQueue()
        imageLoader = ImageLoader(requestQueue,
                object : ImageCache {
                    private val cache = LruCache<String, Bitmap>(20)
                    override fun getBitmap(url: String): Bitmap {
                        return cache[url]!!
                    }

                    override fun putBitmap(url: String, bitmap: Bitmap) {
                        cache.put(url, bitmap)
                    }
                })
    }
}