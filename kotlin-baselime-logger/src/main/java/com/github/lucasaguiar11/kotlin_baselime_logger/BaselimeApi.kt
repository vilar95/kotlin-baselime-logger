package com.github.lucasaguiar11.kotlin_baselime_logger

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path


internal class BaselimeApi() {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BaselimeConfig.getBaseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val logApi: LogApi by lazy { retrofit.create(LogApi::class.java) }

    suspend fun sendLogs(logs: List<LogEvent>) {
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
)

