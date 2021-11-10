package com.reactnativelightcompressor

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule
import java.io.File;
import java.util.UUID;
import android.util.Log
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.abedelazizshe.lightcompressorlibrary.config.Configuration
import android.net.Uri;

class LightcompressorModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
  private val reactContext: ReactApplicationContext = reactContext

  override fun initialize() {
    super.initialize()
    // reactContext?.addActivityEventListener(mActivityEventListener)
  }

  fun generateCacheFilePath(extension: String ): String {
    var outputDir: File = reactContext.getCacheDir();

    var outputUri: String  = String.format("%s/%s." + extension, outputDir.getPath(), UUID.randomUUID().toString());
    return outputUri;
  }

    override fun getName(): String {
        return "LightCompressor"
    }


    companion object {
      private fun emitDeviceEvent(reactContext: ReactApplicationContext, eventName: String, eventData: WritableMap?) {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java).emit(eventName, eventData)
      }
    }

    @ReactMethod
    fun cancel(promise: Promise) {
      VideoCompressor.cancel()
      promise.resolve(true)
    }

    @ReactMethod
    fun compress(uri: String, quality: String, promise: Promise) {

      val out: String = generateCacheFilePath("mp4")

      val videoQuality = when (quality) {
          "VERY_HIGH" -> VideoQuality.VERY_HIGH
          "HIGH" -> VideoQuality.HIGH
          "MEDIUM" -> VideoQuality.MEDIUM 
          "LOW" -> VideoQuality.LOW
          "VERY_LOW" -> VideoQuality.VERY_LOW
          
          else -> VideoQuality.MEDIUM
      }

      var lastProgress = 0

      VideoCompressor.start(
        context = reactContext, 
        srcUri = Uri.parse(uri), 
        srcPath = null, 
        destPath = out,
        streamableFile = null, 
        listener = object : CompressionListener {
            override fun onProgress(percent: Float) {
              val intVal = percent.toInt();
              if (intVal > lastProgress) {
                lastProgress = intVal;
                var params: WritableMap = Arguments.createMap();
                params.putDouble("percent", percent.toDouble())
                emitDeviceEvent(reactContext, "progress", params)
              }
              
            }

            override fun onStart() {
              emitDeviceEvent(reactContext, "start", Arguments.createMap())
                
            }

            override fun onSuccess() {
              promise.resolve(out)
            }

            override fun onFailure(failureMessage: String) {
              // On Failure
              Log.wtf("failureMessage", failureMessage)
              promise.reject(failureMessage)
            }

            override fun onCancelled() {
              // On Cancelled
              Log.wtf("TAG", "compression has been cancelled")
              promise.reject("cancelled")
            }

        },
        configureWith = Configuration(
            quality = videoQuality,
            frameRate = 24, /*Int, ignore, or null*/
            isMinBitrateCheckEnabled = true,
            null
        )
      )
      
      
    
    }

    
}
