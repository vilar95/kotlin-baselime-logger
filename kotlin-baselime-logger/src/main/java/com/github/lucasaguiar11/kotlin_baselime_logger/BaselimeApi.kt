package com.github.lucasaguiar11.kotlin_baselime_logger

import android.annotation.SuppressLint
import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


internal class BaselimeApi() {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BaselimeConfig.getBaseUrl())
            .client(createOkHttpClient() ?: OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun createOkHttpClient(): OkHttpClient? {
        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.M) {
            try {
                val trustAllCerts = arrayOf<TrustManager>(@SuppressLint("CustomX509TrustManager")
                object : X509TrustManager {
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) = Unit

                    override fun checkServerTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) = Unit

                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                })

                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, java.security.SecureRandom())

                val sslSocketFactory = sslContext.socketFactory

                val builder = OkHttpClient.Builder()
                builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                builder.hostnameVerifier { _, _ -> true }

                builder.callTimeout(3, java.util.concurrent.TimeUnit.MINUTES)
                builder.connectTimeout(3, java.util.concurrent.TimeUnit.MINUTES)
                builder.readTimeout(3, java.util.concurrent.TimeUnit.MINUTES)
                builder.writeTimeout(3, java.util.concurrent.TimeUnit.MINUTES)

                return builder.build()
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
        return null
    }


    private val logApi: LogApi by lazy { retrofit.create(LogApi::class.java) }

    suspend fun sendLogs(logs: List<LogEvent>) {
        try {
            LoggerUtil.debug("qtd: ${logs.count()} logs")
            LoggerUtil.debug("logs: $logs")
            val response = logApi.sendLogs(
                BaselimeConfig.getDataSet(),
                BaselimeConfig.getApiKey(),
                BaselimeConfig.getServiceName(),
                logs
            )
            LoggerUtil.debug("response logs: response = $response")
            if (!response.isSuccessful) {
                println("Failed to send logs: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("BaselimeApi", "Error sending logs (message): ${e.message}")
            Log.e("BaselimeApi", "Error sending logs (stackTrace): ${e.stackTraceToString()}")
            Log.e("BaselimeApi", "Error sending logs (cause): ${e.cause}")
        }

    }

    interface LogApi {
        @POST("/v1/{logs}")
        suspend fun sendLogs(
            @Path("logs") path: String,
            @Header("x-api-key") apiKey: String,
            @Header("x-service") service: String,
            @Body logs: List<LogEvent>
        ): Response<Unit>
    }
}

internal data class LogEvent(
    val level: LoggerLevel = LoggerLevel.INFO,
    val message: String,
    val error: String? = null,
    val namespace: String? = null,
    val data: Map<String, Any>? = null,
    val duration: Long? = null,
    val requestId: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
) {
    init {
        LoggerUtil.debug("LogEvent created with timestamp: $timestamp")
    }
}

