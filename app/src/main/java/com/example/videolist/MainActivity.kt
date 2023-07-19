package com.example.videolist

import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.StreamKey
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.ExoDatabaseProvider
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.LoadControl
import androidx.media3.exoplayer.hls.offline.HlsDownloader
import androidx.media3.exoplayer.hls.playlist.HlsMultivariantPlaylist
import androidx.media3.exoplayer.upstream.DefaultAllocator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.videolist.adapter.SocialVideoAdapter
import com.example.videolist.adapter.SocialVideoAdapterCallback
import com.example.videolist.databinding.ActivityMainBinding
import com.example.videolist.layoutmanager.PreCachingLayoutManager
import com.example.videolist.model.FeedItem
import com.example.videolist.model.VideoItem
import com.example.videolist.network.ApiService
import com.example.videolist.network.RetrofitClientInstance
import com.example.videolist.ui.main.MainViewModel
import kotlinx.coroutines.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


@UnstableApi
class MainActivity : AppCompatActivity() {
    val queue: ArrayList<HlsDownloader> = ArrayList()
    lateinit var homePagerAdapter: SocialVideoAdapter
    val fragList = ArrayList<FeedItem>()
    var currentCallPos = 0
    var currentFragPos = 0
    var loadingFirst = true
    lateinit var   snapHelper:PagerSnapHelper
    val service: ApiService = RetrofitClientInstance.retrofitInstance!!.create(
        ApiService::class.java
    )
    private val viewModel: MainViewModel by lazy {

        MainViewModel.provideFactory(service, this).create(MainViewModel::class.java)
    }
    lateinit var viewBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        /*if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }*/
        viewBinding.apply {

/*
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    queue.get(position).cancel()

                    if (fragList.size==10&&(position+4)==queue.size){

                        if (fragList.size>=queue.size){
                            val url=fragList.get(queue.size).videos.first().reference
                            val uri=Uri.parse( url)
                            val downloader=getHls(uri)
                            queue.add(downloader)
                            lifecycleScope.launch {
                                preCacheVideo(uri,i,downloader)
                            }
                            queue.add(downloader)
                        }
                    }else if(fragList.size>10&&(position+3)== queue.size){

                        if (fragList.size> queue.size){
                            val url=fragList.get(queue.size).videos.first().reference
                            val uri=Uri.parse( url)
                            val downloader=getHls(uri)
                            queue.add(downloader)
                            lifecycleScope.launch {
                                preCacheVideo(uri,i,downloader)
                            }
                            queue.add(downloader)
                        }
                    }
                    Log.e(
                        "test",
                        "onPageSelected $position,$currentCallPos,${viewPager.currentItem} "
                    )
                    currentFragPos = position
                    if ((currentCallPos - 3) == position) {
                        loadingFirst = false
                        if (hasMore) {

                            loadMore()
                        }
                    }
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                    Log.e("test", "onPageScrolled $position,${viewPager.currentItem} ")
                    */
/*if (positionOffset != 0.0f) {
                        disableViewPagerSwiping(viewPager)
                    } else {
                        enableViewPagerSwiping(viewPager)
                    }*//*

                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    if (state == ViewPager2.SCROLL_STATE_IDLE) {
                        if (viewPager.currentItem != pagerLastItem.value) {
                            pagerLastItem.value = viewPager.currentItem
                        }
                        player.playWhenReady = true
                    } else {
                        player.playWhenReady = false
                    }

                }

            })
*/


        }
        cache = cacheInstance ?: run {
            val exoCacheDir = File("${cacheDir.absolutePath}/exo")
            val evictor = LeastRecentlyUsedCacheEvictor(CACHE_SIZE)
            SimpleCache(exoCacheDir, evictor, ExoDatabaseProvider(this)).also {
                cacheInstance = it
            }
        }
        cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(upstreamDataSourceFactory)
            .setEventListener(object : CacheDataSource.EventListener {
                override fun onCachedBytesRead(cacheSizeBytes: Long, cachedBytesRead: Long) {
                    Log.d(
                        TAG,
                        "onCachedBytesRead. cacheSizeBytes:$cacheSizeBytes, cachedBytesRead: $cachedBytesRead"
                    )
                }

                override fun onCacheIgnored(reason: Int) {
                    Log.d(TAG, "onCacheIgnored. reason:$reason")
                }
            }
            )



        loadInitData()
        setObserver()
    }


    private fun setObserver() {

        viewModel.apply {
            viewBinding.apply {
                getHomeListLiveData.observe(this@MainActivity) {
                    it?.let {
                        hasMore = true
                        // hasMore= it.it?.streams_paging?.hasMore==true||it.data?.video_paging?.hasMore==true
                        //startPreCaching(it.feeds)
                        if (it.feeds.isNotEmpty()) {

                            //ivWilli.visibilityGone()
                            for ((i, streams_videos) in it.feeds.withIndex()) {

/*
                                if (i<5){
                                    val url=streams_videos.videos.first().reference
                                    val uri=Uri.parse( url)
                                    val downloader=getHls(uri)
                                    queue.add(downloader)
                                    lifecycleScope.launch {
                                        //preCacheVideo(uri,i,downloader)
                                    }
                                }
                                else if (i<3&&!loadingFirst){
                                    val url=streams_videos.videos.first().reference
                                    val uri=Uri.parse( url)
                                    val downloader=getHls(uri)
                                    queue.add(downloader)
                                    lifecycleScope.launch {
                                       // preCacheVideo(uri,i,downloader)
                                    }
                                }
*/

                                fragList.add(streams_videos)
                                cacheStreamKeys.add(StreamKey(fragList.size - 1, 1))
                                Log.e("Test"," id ${streams_videos.id},${streams_videos.videos.first().imageLink}")

                            }
                            currentCallPos = fragList.size
                            if (fragList.isNotEmpty()) {

                                Log.e("Test", "${fragList.size}")
                                //startPreCaching(fragList)
                                homePagerAdapter.addList(fragList)

                            }
                            /*lifecycleScope.launch {
                                delay(2000)
                                withContext(Dispatchers.Main){
                                    viewBinding.rvVideoFeed.visibility=View.GONE
                                }
                            }*/
                        } else {

                        }
                        isLoading = false
                    }

                }

            }
        }
    }

    private fun loadInitData() {
        loadingFirst = true
        fragList.clear()
        cacheStreamKeys.clear()
        initHomePagerAdapter()
        isLoading = true
        //viewBinding.rvVideoFeed.visibility=View.VISIBLE
        viewModel.getHomeList(true, limit = 10)
        initScrollListener()
    }

    private fun initHomePagerAdapter() {
        homePagerAdapter = SocialVideoAdapter(this,object : SocialVideoAdapterCallback{
            override fun showDialog(feedItem: FeedItem) {
                val videoItem=feedItem.videos.first()
                val dialog=CustomDialogClass(this@MainActivity,videoItem.id,videoItem.imageLink)
                dialog.show()
            }

        })
        snapHelper= PagerSnapHelper()

        viewBinding.apply {
            snapHelper.attachToRecyclerView(rvVideoFeed)
            rvVideoFeed.apply {
                adapter = homePagerAdapter
                layoutManager= PreCachingLayoutManager(context).apply {
                    val displayHeight= Resources.getSystem().displayMetrics.heightPixels
                    setExtraLayoutSpace(displayHeight*4)

                }

                setItemViewCacheSize(10)
                setHasFixedSize(true)
                 /*registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageScrollStateChanged(state: Int) {
                    }
                })*/
            }

        }
    }

    var isLoading = false
    var hasMore = true
    private fun initScrollListener() {
        viewBinding?.rvVideoFeed?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState==RecyclerView.SCROLL_STATE_IDLE){
                    val position=(recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
/*
                    if (fragList.size==10&&(position+4)==queue.size){

                        if (fragList.size>=queue.size){
                            val url=fragList.get(queue.size).videos.first().reference
                            val uri=Uri.parse( url)
                            val downloader=getHls(uri)
                            queue.add(downloader)
                            lifecycleScope.launch {
                                preCacheVideo(uri,i,downloader)
                            }
                            queue.add(downloader)
                        }
                    }else if(fragList.size>10&&(position+3)== queue.size){

                        if (fragList.size> queue.size){
                            val url=fragList.get(queue.size).videos.first().reference
                            val uri=Uri.parse( url)
                            val downloader=getHls(uri)
                            queue.add(downloader)
                            lifecycleScope.launch {
                                preCacheVideo(uri,i,downloader)
                            }
                            queue.add(downloader)
                        }
                    }
*/
                    Log.e(
                        TAG,
                        "onPageSelected $position,$currentCallPos, "
                    )
                    currentFragPos = position

                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                Log.i(TAG, "onScr hasMore $hasMore isLoading $isLoading")
                if (hasMore) {
                    if (!isLoading) {
                        val maxValue = linearLayoutManager.findLastVisibleItemPosition()
                        Log.i(
                            TAG,
                            "onScroll ${linearLayoutManager.findLastVisibleItemPosition()}, , ,   max $maxValue ${linearLayoutManager.itemCount}"
                        )
                        if (  maxValue > linearLayoutManager.itemCount - 4) {
                            //bottom of list!
                            //liveFragmentAdapter?.addData(LiveFragmentAdapter.ViewType.PROGRESS_BAR,fullDataList.size)
                            loadingFirst = false
                            loadMore()
                            //testLoadMore()
                            isLoading = true
                        }
                    }
                }
            }
        })
    }

    fun loadMore() {
        viewModel.getHomeList(
            false,
            limit = 10,
            offset = fragList.size
        )
    }

    var pagerLastItem = MutableLiveData(0)


    val upstreamDataSourceFactory by lazy { DefaultHttpDataSource.Factory() }

/*
    val cacheDataSourceFactory by lazy {
        val cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(upstreamDataSourceFactory)
            .setEventListener(object : CacheDataSource.EventListener {
                override fun onCachedBytesRead(cacheSizeBytes: Long, cachedBytesRead: Long) {
                    Log.d(
                        TAG,
                        "onCachedBytesRead. cacheSizeBytes:$cacheSizeBytes, cachedBytesRead: $cachedBytesRead"
                    )
                }

                override fun onCacheIgnored(reason: Int) {
                    Log.d(TAG, "onCacheIgnored. reason:$reason")
                }
            }
            )

        cacheDataSourceFactory
    }
*/

    val player by lazy {
        val loadControl: LoadControl = DefaultLoadControl.Builder()
            .setAllocator(DefaultAllocator(true, 16))
            .setBufferDurationsMs(
                1000,
                5000,
                500,
                200
            )
            .setTargetBufferBytes(-1)
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()

        ExoPlayer.Builder(this)
            .setLoadControl(loadControl)
            .build().apply {
                //repeatMode = Player.REPEAT_MODE_OFF
                repeatMode = Player.REPEAT_MODE_ONE
                playWhenReady = true
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)
                        Log.d(
                            TAG,
                            "onPlayerStateChanged. playWhenReady: $playWhenReady, playbackState: $playbackState)"
                        )

                    }

                    override fun onPlayerError(error: PlaybackException) {
                        super.onPlayerError(error)
                        Log.d(TAG, "onPlayerError $error")
                        error.printStackTrace()
                    }
                })
            }
    }
    val playerListener = object : Player.Listener {

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            super.onPlayerStateChanged(playWhenReady, playbackState)
            when (playbackState) {

                Player.STATE_ENDED -> {

                }

                Player.STATE_BUFFERING -> {
                    //binding?.progress?.pbProgress?.visibilityVisible()

                }


                Player.STATE_READY -> {
                    //binding?.ivThumbnailView?.visibilityGone()
                    //binding?.progress?.pbProgress?.visibilityGone()

                }

            }
        }


        override fun onIsPlayingChanged(isPlaying: Boolean) {
            /*if (isPlaying){
                binding.ivThumbnailView.visibility=View.GONE
            }else  binding.ivThumbnailView.visibility=View.VISIBLE*/
        }
    }

    override fun onPause() {
        super.onPause()
    }

    companion object {
        private const val CACHE_SIZE = 50 * 1024 * 1024L
        private var cacheInstance: Cache? = null
        const val TAG = "MainActivity"
        internal var cacheStreamKeys = arrayListOf<StreamKey>()
        const val PRE_CACHE_SIZE = 1 * 1024 * 1024L
        internal lateinit var cache:Cache
        internal lateinit var cacheDataSourceFactory:CacheDataSource.Factory

    }

    private suspend fun preCacheVideo(uri: Uri,position:Int,downloader:HlsDownloader) = withContext(Dispatchers.IO) {

        runCatching {
            // do nothing if already cache enough
            if (cache.isCached(uri.toString(), 0, PRE_CACHE_SIZE)) {
                Log.d(TAG, "video has been cached, return")
                return@runCatching
            }

            Log.d(TAG, "start pre-caching for position: $position")
            downloader.download { contentLength, bytesDownloaded, percentDownloaded ->
                if (bytesDownloaded >= PRE_CACHE_SIZE) downloader.cancel()
                Log.d(
                    TAG,
                    "contentLength($position): $contentLength, bytesDownloaded: $bytesDownloaded, percentDownloaded: $percentDownloaded"
                )
            }
        }.onFailure {
            if (it is InterruptedException) return@onFailure

            Log.d(TAG, "Cache fail for position: $position with exception: $it}")
            it.printStackTrace()
        }.onSuccess {
            Log.d(TAG, "Cache success for position: $position")
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

    private fun startPreCaching(dataList: List<FeedItem>) {
        Log.d("caching_follow", "true")
        val urlList = arrayOfNulls<String>(dataList.size)
        dataList.mapIndexed { index, storiesDataModel ->

            urlList[index] = storiesDataModel.videos.first().reference//"http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        }
        val inputData =
            Data.Builder().putStringArray("KEY_VIDEOS_LIST_DATA", urlList).build()
        val preCachingWork = OneTimeWorkRequestBuilder<PreCachingService>().setInputData(inputData)
            .build()
        WorkManager.getInstance(this)
            .enqueue(preCachingWork)
    }

    var i=-1
    val videoDatas = arrayListOf(
        FeedItem(
            id = "${i++}",
            listOf(VideoItem("dsf","dsf","dsfsd","https://d1hus0nx0ytxoz.cloudfront.net/out/v1/999b1bddb281459593d808d2e75537e0/26fab79877664df5bc4cb946a3aeb817/5efb491ddaba42b186fc2ea1575f8020/index.m3u8","https://d1hus0nx0ytxoz.cloudfront.net/out/v1/999b1bddb281459593d808d2e75537e0/26fab79877664df5bc4cb946a3aeb817/5efb491ddaba42b186fc2ea1575f8020/index.m3u8","https://d1hus0nx0ytxoz.cloudfront.net/out/v1/999b1bddb281459593d808d2e75537e0/26fab79877664df5bc4cb946a3aeb817/5efb491ddaba42b186fc2ea1575f8020/index.m3u8","dfs",0f))
        ),
        FeedItem(
            id = "${i++}",
            listOf(VideoItem("dsf","dsf","dsfsd","https://d1hus0nx0ytxoz.cloudfront.net/out/v1/ed4ffea99ba04f13bc1b39655f194fad/26fab79877664df5bc4cb946a3aeb817/5efb491ddaba42b186fc2ea1575f8020/index.m3u8","https://d1hus0nx0ytxoz.cloudfront.net/out/v1/ed4ffea99ba04f13bc1b39655f194fad/26fab79877664df5bc4cb946a3aeb817/5efb491ddaba42b186fc2ea1575f8020/index.m3u8","https://d1hus0nx0ytxoz.cloudfront.net/out/v1/999b1bddb281459593d808d2e75537e0/26fab79877664df5bc4cb946a3aeb817/5efb491ddaba42b186fc2ea1575f8020/index.m3u8","dfs",0f))
        ),
        FeedItem(
            id = "${i++}",
            listOf(VideoItem("dsf","dsf","dsfsd","https://d1hus0nx0ytxoz.cloudfront.net/out/v1/c6368f82edd84c47b8b5d7e7ce0df6c0/26fab79877664df5bc4cb946a3aeb817/5efb491ddaba42b186fc2ea1575f8020/index.m3u8","https://d1hus0nx0ytxoz.cloudfront.net/out/v1/c6368f82edd84c47b8b5d7e7ce0df6c0/26fab79877664df5bc4cb946a3aeb817/5efb491ddaba42b186fc2ea1575f8020/index.m3u8","https://d1hus0nx0ytxoz.cloudfront.net/out/v1/999b1bddb281459593d808d2e75537e0/26fab79877664df5bc4cb946a3aeb817/5efb491ddaba42b186fc2ea1575f8020/index.m3u8","dfs",0f))
        ),
        FeedItem(
            id = "${i++}",
            listOf(VideoItem("dsf","dsf","dsfsd","https://d1hus0nx0ytxoz.cloudfront.net/out/v1/2d061cb4d3d0465e8348a5da0d8cf615/26fab79877664df5bc4cb946a3aeb817/5efb491ddaba42b186fc2ea1575f8020/index.m3u8","https://d1hus0nx0ytxoz.cloudfront.net/out/v1/2d061cb4d3d0465e8348a5da0d8cf615/26fab79877664df5bc4cb946a3aeb817/5efb491ddaba42b186fc2ea1575f8020/index.m3u8","https://d1hus0nx0ytxoz.cloudfront.net/out/v1/999b1bddb281459593d808d2e75537e0/26fab79877664df5bc4cb946a3aeb817/5efb491ddaba42b186fc2ea1575f8020/index.m3u8","dfs",0f))
        ),
        FeedItem(
            id = "${i++}",
            listOf(VideoItem("dsf","dsf","dsfsd","https://d1hus0nx0ytxoz.cloudfront.net/out/v1/cb2dc33782664980b3ce1e7d4d4f800b/26fab79877664df5bc4cb946a3aeb817/5efb491ddaba42b186fc2ea1575f8020/index.m3u8","https://d1hus0nx0ytxoz.cloudfront.net/out/v1/cb2dc33782664980b3ce1e7d4d4f800b/26fab79877664df5bc4cb946a3aeb817/5efb491ddaba42b186fc2ea1575f8020/index.m3u8","https://d1hus0nx0ytxoz.cloudfront.net/out/v1/999b1bddb281459593d808d2e75537e0/26fab79877664df5bc4cb946a3aeb817/5efb491ddaba42b186fc2ea1575f8020/index.m3u8","dfs",0f))
        )

    )

    override fun onDestroy() {
        super.onDestroy()
        cache.release()
    }

}