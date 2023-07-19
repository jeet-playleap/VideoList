package com.example.videolist

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.StreamKey
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheWriter
import androidx.media3.exoplayer.hls.offline.HlsDownloader
import androidx.media3.exoplayer.hls.playlist.HlsMultivariantPlaylist

import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.videolist.MainActivity.Companion.cache
import com.example.videolist.MainActivity.Companion.cacheDataSourceFactory

import kotlinx.coroutines.*


import java.util.*

private const val TAG = "PreCachingService"
@UnstableApi
class PreCachingService(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {
    private var cachingJob: Deferred<Unit?>? = null
 //   private var cacheDataSourceFactory: CacheDataSource.Factory? = null
//    val upstreamDataSourceFactory by lazy { DefaultHttpDataSource.Factory() }
/*
    val cacheDataSourceFactory by lazy {
        val cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(upstreamDataSourceFactory)
            .setEventListener(object : CacheDataSource.EventListener {
                override fun onCachedBytesRead(cacheSizeBytes: Long, cachedBytesRead: Long) {
                    Log.d(
                        MainActivity.TAG,
                        "onCachedBytesRead. cacheSizeBytes:$cacheSizeBytes, cachedBytesRead: $cachedBytesRead"
                    )
                }

                override fun onCacheIgnored(reason: Int) {
                    Log.d(MainActivity.TAG, "onCacheIgnored. reason:$reason")
                }
            }
            )
         cacheDataSourceFactory
    }
*/


    @SuppressLint("UnsafeOptInUsageError")
    override suspend fun doWork(): Result = coroutineScope {
       // cacheDataSourceFactory = CacheDataSource.Factory(simpleCache,)

        val dataList = inputData.getStringArray("KEY_VIDEOS_LIST_DATA")

        val jobs = dataList?.mapIndexed  { index, data ->
            async {
                val dataUri = Uri.parse(data)
                val downloader=getHls(dataUri)
                preCacheVideo(dataUri,index,downloader)
               /* val dataSpec = DataSpec(dataUri, 0, 500 * 1024, null)

                val dataSource: DefaultDataSourceFactory =
                    DefaultDataSourceFactory(
                        applicationContext,
                        Util.getUserAgent(
                            applicationContext,
                            "exo"
                        )
                    )
                val mCacheDataSource = CacheDataSource.Factory()
                    .setCache(simpleCache)
                    .setUpstreamDataSourceFactory(dataSource)
                    .createDataSource()*/
                //val dataSpec = DataSpec(dataUri, 0, 500 * 1024, null)

/*
                val dataSource: DataSource =
                    DefaultDataSourceFactory(
                        applicationContext,
                        Util.getUserAgent(
                            applicationContext,
                            "exo"
                        )
                    ).createDataSource()
*/
/*
                preloadVideo(
                    dataSpec,
                    simpleCache,
                    dataSource,
                    CacheUtil.ProgressListener { requestLength: Long, bytesCached: Long, newBytesCached: Long ->
                        val downloadPercentage = (bytesCached * 100.0
                                / requestLength)
                        Log.d(TAG, "downloadPercentage: $downloadPercentage")
                    }
                )
*/
            }
        }
        jobs?.joinAll()
        Result.success()
    }

    private suspend fun preCacheVideo(uri: Uri,position:Int,downloader:HlsDownloader) = withContext(
        Dispatchers.IO) {

        runCatching {
            // do nothing if already cache enough
            if (cache.isCached(uri.toString(), 0, MainActivity.PRE_CACHE_SIZE)) {
                Log.d(MainActivity.TAG, "video has been cached, return")
                return@runCatching
            }

            Log.d(MainActivity.TAG, "start pre-caching for position: $position")
            downloader.download { contentLength, bytesDownloaded, percentDownloaded ->
                if (bytesDownloaded >= MainActivity.PRE_CACHE_SIZE) downloader.cancel()
                Log.d(
                    MainActivity.TAG,
                    "contentLength($position): $contentLength, bytesDownloaded: $bytesDownloaded, percentDownloaded: $percentDownloaded"
                )
            }
        }.onFailure {
            if (it is InterruptedException) return@onFailure

            Log.d(MainActivity.TAG, "Cache fail for position: $position with exception: $it}")
            it.printStackTrace()
        }.onSuccess {
            Log.d(MainActivity.TAG, "Cache success for position: $position")
        }
        Unit
    }

    fun getHls(uri:Uri): HlsDownloader {

        val hlsDownloader = HlsDownloader(
            MediaItem.Builder()
                .setUri(uri)
                .setStreamKeys(
                    Collections.singletonList(
                        StreamKey(HlsMultivariantPlaylist.GROUP_INDEX_VARIANT, 0)
                    ))
                .build(),

            cacheDataSourceFactory
        )
        return hlsDownloader
    }

    private fun preloadVideo(
        dataSpec: DataSpec,
        cache: Cache?,
        upstream: DataSource,
        progressListener: CacheWriter.ProgressListener?
    ) {
        Log.d(TAG, "preloadVideo")
        try {

/*
            CacheUtil.cache(
                dataSpec,
                cache,
                CacheUtil.DEFAULT_CACHE_KEY_FACTORY,
                upstream,
                progressListener,
                null)
*/
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}