package com.github.lucasaguiar11.kotlin_baselime_logger

import android.util.Log
import com.github.lucasaguiar11.kotlinBaselimeLogger.LoggerLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.util.PriorityQueue
import java.util.Timer
import java.util.TimerTask

object Logger {


    private val logQueue = PriorityQueue<LogEvent>(1, compareBy {
        it.timestamp
    })

    private val batchQueue = PriorityQueue<LogEvent>(1, compareBy {
        it.timestamp
    })


    private var timerCreated = false

    init {
        LoggerUtil.debug("Logger: init (timer = $timerCreated)")
        if (!timerCreated) {
            Timer().scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    LoggerUtil.debug("TimerTask: processLogQueue")
                    processUniqueLogQueue()
                }
            }, 0, BaselimeConfig.getTimeDelay())
            timerCreated = true
        }

    }

    private fun processLogQueue() {
        while (logQueue.isNotEmpty() && batchQueue.size < BaselimeConfig.getBatchQueueSize()) {
            LoggerUtil.debug("processLogQueue: logQueue.size = ${logQueue.size} - batchQueue.size = ${batchQueue.size}")
            val logEvent = logQueue.poll()
            if (logEvent == null) {
                LoggerUtil.debug("processLogQueue: poll: logEvent is null")
                break
            }

            LoggerUtil.debug("processLogQueue: poll: $logEvent")
            batchQueue.add(logEvent)
        }

        if (batchQueue.size >= BaselimeConfig.getBatchQueueSize()) {
            sendLogsAsync()
        }

    }

    private fun sendLogsAsync() {
        val toSend = batchQueue.toList()
        CoroutineScope(IO).launch {
            val logService = BaselimeApi()
            LoggerUtil.debug("processLogQueue: sendLogs")
            try {
                logService.sendLogs(toSend)
            } catch (e: Exception) {
                println("Error sending logs: ${e.message}")
            }
        }
        batchQueue.clear()
    }

    private fun processUniqueLogQueue() {
        if (batchQueue.isNotEmpty()) {
            LoggerUtil.debug("processUniqueLogQueue: batchQueue.size = ${batchQueue.size}")
            sendLogsAsync()
        }
    }

    private fun addEventToQueue(logs: LogEvent) {
        try {
            LoggerUtil.debug("sendLogsAsync: $logs")
            logQueue.add(logs)
            processLogQueue()
        } catch (e: InterruptedException) {
            println("Error adding log to queue: ${e.message}")
        }
    }

    private fun makeEvent(
        level: LoggerLevel,
        data: Map<String, String>?,
        message: String,
        tag: String,
        duration: Long?,
        throwable: Throwable? = null
    ): LogEvent {
        LoggerUtil.debug("makeEvent: $level - $message")
        val innerData =
            data?.let { BaselimeConfig.getDefaultData()?.plus(it) }
                ?: BaselimeConfig.getDefaultData()

        return LogEvent(
            level = level,
            message,
            namespace = tag,
            data = innerData,
            duration = duration,
            error = LoggerUtil.getError(throwable)
        )
    }

    fun i(
        tag: String,
        message: String,
        data: Map<String, String>? = null,
        duration: Long? = null,
    ) {
        LoggerUtil.debug("i: $tag - $message")
        val event = makeEvent(LoggerLevel.INFO, data, message, tag, duration)
        addEventToQueue(event)
        Log.i(tag, message)
    }

    fun e(
        tag: String,
        message: String,
        data: Map<String, String>? = null,
        duration: Long? = null,
        throwable: Throwable? = null
    ) {
        LoggerUtil.debug("e: $tag - $message")
        val event = makeEvent(LoggerLevel.ERROR, data, message, tag, duration, throwable)
        addEventToQueue(event)
        Log.e(tag, message, throwable)
    }

    fun d(
        tag: String,
        message: String,
        data: Map<String, String>? = null,
        duration: Long? = null,
    ) {
        LoggerUtil.debug("d: $tag - $message")
        val event = makeEvent(LoggerLevel.DEBUG, data, message, tag, duration)
        addEventToQueue(event)
        Log.d(tag, message)
    }

    fun w(
        tag: String,
        message: String,
        data: Map<String, String>? = null,
        duration: Long? = null,
    ) {
        LoggerUtil.debug("w: $tag - $message")
        val event = makeEvent(LoggerLevel.WARN, data, message, tag, duration)
        addEventToQueue(event)
        Log.w(tag, message)
    }

}