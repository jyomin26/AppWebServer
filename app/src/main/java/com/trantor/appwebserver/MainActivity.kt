package com.trantor.appwebserver

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.trantor.appwebserver.R
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.StringRequestListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import com.rx2androidnetworking.Rx2AndroidNetworking
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

private val TAG = "app-ui"
var deviceBaseUrl: String? = "http://127.0.0.1:5001" //local host IP Address

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Start web server
        WebServer.startServer()
        setClickListener()
    }

    /**
     * Register click listeners
     */
    private fun setClickListener() {
        btnGetApi.setOnClickListener {
            callGetApi()
        }
        btnPostApi.setOnClickListener {
            callPostApi()
        }
        btnPutApi.setOnClickListener {
            callPutApi()
        }
        btnDeleteApi.setOnClickListener {
            callDeleteApi()
        }
    }

    /**
     * Call to Webserve get Api
     */
    fun callGetApi() {
        val url = deviceBaseUrl + "/status"  //create url
        var job: Disposable? = null
        job = Rx2AndroidNetworking.get(url)
            .build()
            .getObjectObservable(JSONObject::class.java)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ status ->
                tvGetApiResponse.text = status.getString("status")
                job?.dispose()
            }, { erorr ->
                Log.w(TAG, "GET $url failed: $erorr")
                job?.dispose()
            })
    }

    /**
     * Call to webserver post Api
     */
    fun callPostApi() {
        val data = "android"
        val url = deviceBaseUrl + "/username/$data"
        var job: Disposable? = null
        job = Rx2AndroidNetworking.post(url)
            .build()
            .getObjectObservable(JSONObject::class.java)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ status ->
                tvPostApiResponse.text = status.getString("response")
                job?.dispose()
            }, { erorr ->
                Log.w(TAG, "Post $url failed: $erorr")
                job?.dispose()
            })
    }

    /**
     * Call to webserver put Api
     */
    fun callPutApi() {
        val url = deviceBaseUrl + "/update"
        Rx2AndroidNetworking.put(url).addJSONObjectBody(JSONObject().put("key", "value"))
            .build().getAsString(object : StringRequestListener {
                override fun onResponse(response: String?) {
                    tvPutApiResponse.text = response
                    Log.d(TAG, "Put api response $response")
                }

                override fun onError(anError: ANError?) {
                    Log.w(TAG, "Failed to get output detection: ${anError}")
                }

            })
    }

    /**
     * Call to webserver delete Api
     */
    fun callDeleteApi() {
        val url = deviceBaseUrl + "/delete"
        var job: Disposable? = null
        job = Rx2AndroidNetworking.delete(url)
            .build()
            .getObjectObservable(JSONObject::class.java)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ status ->
                tvDeleteApiResponse.text = status.getString("response")
                job?.dispose()
            }, { erorr ->
                Log.w(TAG, "Delete $url failed: $erorr")
                job?.dispose()
            })
    }

}