package com.trantor.appwebserver

import android.util.Log
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CachingHeaders
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.CacheControl
import io.ktor.request.receiveText
import io.ktor.response.cacheControl
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.json.JSONObject
import java.util.concurrent.TimeUnit

private val TAG = "app-server"
private const val second = 1000L

/**
 * Server class to handle Apis Calls
 */
object WebServer {
    private var server = embeddedServer(Netty, port = 5001) {
        Log.d(TAG, "Initializing the system server...")

        install(CallLogging)
        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
            }
        }
        install(CachingHeaders)

        routing {
            get("/status") {
                // Stopped saving cache on client end
                call.response.cacheControl(CacheControl.NoCache(null))
                call.respond(JSONObject().put("status", "Server is running"))
            }

            post("/username/{data?}") {
                // Stopped saving cache on client end
                call.response.cacheControl(CacheControl.NoCache(null))
                // fetch post data
                val postData = call.parameters["data"]
                if (postData.isNullOrEmpty()) {
                    Log.w(TAG, "Missing data")
                    call.respond(JSONObject().put("response", "Missing data"))
                    return@post
                }
                call.respond(JSONObject().put("response", "$postData successfully"))
            }

            put("/update/{data?}") {
                // Stopped saving cache on client end
                call.response.cacheControl(CacheControl.NoCache(null))
                val putData = call.receiveText()
                if (putData.isNullOrEmpty()) {
                    Log.w(TAG, "Missing data")
                    call.respond(JSONObject().put("response", "Missing data"))
                    return@put
                }
                call.respond(JSONObject().put("response", "$putData successfully"))
            }
        }
    }

    /**
     * Start server
     */
    fun startServer() {
        server.start(wait = false)
        Log.i(TAG, "System server has initialized")
    }

    fun close() {
        server.stop(second, second, TimeUnit.MILLISECONDS)
    }
}