package com.github.lucasaguiar11.kotlin_baselime_logger

object BaselimeConfig {
    private var baseUrl = "https://events.baselime.io"
    private var apiKey = ""
    private var serviceName = "Kotlin Baselime Logger"
    private var dataSet = "logs"
    private var defaultData: Map<String, String>? = null
    private var isDebug = false
    private var batchQueueSize = 10
    private var timeDelay = 5000L // 5 seconds


    fun new(
        baseUrl: String? = null,
        apiKey: String? = null,
        serviceName: String? = null,
        dataSet: String? = null,
        defaultData: Map<String, String>? = null,
        isDebug: Boolean? = null,
        batchQueueSize: Int? = null,
        timeDelay: Long? = null

    ) {
        if (baseUrl != null) {
            BaselimeConfig.baseUrl = baseUrl
        }

        if (apiKey != null) {
            BaselimeConfig.apiKey = apiKey
        }

        if (serviceName != null) {
            BaselimeConfig.serviceName = serviceName
        }

        if (dataSet != null) {
            BaselimeConfig.dataSet = dataSet
        }

        if (defaultData != null) {
            BaselimeConfig.defaultData = defaultData
        }

        if (isDebug != null) {
            BaselimeConfig.isDebug = isDebug
        }

        if (batchQueueSize != null) {
            BaselimeConfig.batchQueueSize = batchQueueSize
        }

        if (timeDelay != null) {
            BaselimeConfig.timeDelay = timeDelay
        }

    }

    fun getBaseUrl(): String {
        return baseUrl
    }

    fun getApiKey(): String {
        return apiKey
    }

    fun getServiceName(): String {
        return serviceName
    }

    fun getDataSet(): String {
        return dataSet
    }

    fun getDefaultData(): Map<String, String>? {
        return defaultData
    }

    fun getIsDebug(): Boolean {
        return isDebug
    }

    fun getBatchQueueSize(): Int {
        return batchQueueSize
    }

    fun getTimeDelay(): Long {
        return timeDelay
    }

}
