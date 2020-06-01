package com.github.reactnativehero.qiniu

import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.qiniu.android.common.FixedZone
import com.qiniu.android.storage.Configuration
import com.qiniu.android.storage.UploadManager
import com.qiniu.android.storage.UploadOptions

class RNTQiniuModule(private val reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    companion object {
        private const val ZONE_HUADONG = "huadong"
        private const val ZONE_HUABEI = "huabei"
        private const val ZONE_HUANAN = "huanan"
        private const val ZONE_BEIMEI = "beimei"
        private const val ERROR_CODE_UPLOAD_FAILURE = "1"
    }

    override fun getName(): String {
        return "RNTQiniu"
    }

    override fun getConstants(): Map<String, Any>? {

        val constants: MutableMap<String, Any> = HashMap()

        constants["ZONE_HUADONG"] = ZONE_HUADONG
        constants["ZONE_HUABEI"] = ZONE_HUABEI
        constants["ZONE_HUANAN"] = ZONE_HUANAN
        constants["ZONE_BEIMEI"] = ZONE_BEIMEI

        constants["ERROR_CODE_UPLOAD_FAILURE"] = ERROR_CODE_UPLOAD_FAILURE

        return constants

    }

    @ReactMethod
    fun upload(options: ReadableMap, promise: Promise) {

        val index = if (options.hasKey("index")) {
            options.getInt("index")
        }
        else {
            0
        }

        val timeout = if (options.hasKey("timeout")) {
            options.getInt("timeout")
        }
        else {
            0
        }

        val path = options.getString("path")
        val key = options.getString("key")
        val zone = options.getString("zone")
        val token = options.getString("token")
        val mimeType = options.getString("mimeType")

        var builder = Configuration.Builder()
                .useHttps(true)
                .zone(
                    when (zone) {
                        ZONE_HUADONG -> FixedZone.zone0
                        ZONE_HUABEI -> FixedZone.zone1
                        ZONE_HUANAN -> FixedZone.zone2
                        else -> FixedZone.zoneNa0
                    }
                )

        if (timeout > 0) {
            builder = builder.connectTimeout(timeout)
                    .responseTimeout(timeout)
        }

        val config = builder.build()
        val uploadManager = UploadManager(config)

        val uploadOptions = UploadOptions(
                null,
                mimeType,
                false,
                { _, percent ->
                    if (index > 0) {
                        val map = Arguments.createMap()
                        map.putInt("index", index)
                        map.putDouble("progress", percent)
                        sendEvent("progress", map)
                    }
                },
                null
        )

        uploadManager.put(
                path, key, token,
                { _, info, response ->
                    // res 包含 hash、key 等信息，具体字段取决于上传策略的设置
                    if (info.isOK) {

                        val map = Arguments.createMap()

                        val iterator = response.keys()
                        while (iterator.hasNext()) {
                            val name = iterator.next()
                            when (val value = response.get(name)) {
                                is String -> {
                                    map.putString(name, value)
                                }
                                is Int -> {
                                    map.putInt(name, value)
                                }
                                is Boolean -> {
                                    map.putBoolean(name, value)
                                }
                                is Double -> {
                                    map.putDouble(name, value)
                                }
                                is Float -> {
                                    map.putDouble(name, value.toDouble())
                                }
                                else -> {

                                }
                            }
                        }

                        promise.resolve(map)
                    }
                    else {
                        promise.reject(ERROR_CODE_UPLOAD_FAILURE, info.error)
                    }
                },
                uploadOptions
        )

    }

    private fun sendEvent(eventName: String, params: WritableMap) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                .emit(eventName, params)
    }

}
