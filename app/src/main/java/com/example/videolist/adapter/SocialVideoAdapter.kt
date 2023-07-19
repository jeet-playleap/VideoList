package com.example.videolist.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.icu.text.Transliterator
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.LoadControl
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.upstream.DefaultAllocator
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

import com.example.videolist.MainActivity
import com.example.videolist.MainActivity.Companion.cacheDataSourceFactory

import com.example.videolist.model.FeedItem
import com.example.videolist.databinding.FragmentSocialVideoBinding

import java.util.*
import kotlin.collections.ArrayList

class SocialVideoAdapter(val mainActivity: MainActivity, val socialVideoAdapterCallback:SocialVideoAdapterCallback) :
    RecyclerView.Adapter<SocialVideoAdapter.ViewHolder>() {

    var listItem: ArrayList<FeedItem> = arrayListOf()
    fun upDataList(listItem: ArrayList<FeedItem>) {
        this.listItem = listItem
        notifyDataSetChanged()
    }

    fun addList(listItem: ArrayList<FeedItem>) {
        val startIndex = this.listItem.size
        val endIndex = listItem.size
        this.listItem=listItem
        notifyItemRangeInserted(startIndex, endIndex)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentSocialVideoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemData = listItem[position]
        Log.d(TAG, "TestBind pos $position")
        holder.apply {

            //setImage(binding.ivThumbnailView,itemData.videos.first().imageLink,mainActivity.applicationContext)
            itemData.apply {
                val videoItem=this.videos.first()
               // playSocialVideo(videoItem.reference, "m3u8")
               // preparedPlayer(videoItem.reference,mainActivity)
            }

        binding.view.setOnLongClickListener {
            socialVideoAdapterCallback.showDialog(itemData)
            return@setOnLongClickListener true
        }

        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        Log.d("TestBind", "onViewRecycled ${holder.absoluteAdapterPosition}")
        if (holder.absoluteAdapterPosition>=0){
            holder.releasePlayer()
        }

    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
    }


     override fun getItemCount(): Int {
        return listItem.size
    }

    class ViewHolder internal constructor(binding: FragmentSocialVideoBinding) :
        RecyclerView.ViewHolder(binding.root), ViewHolderExtension {
        internal val binding: FragmentSocialVideoBinding = binding
        private var exoPlayer: ExoPlayer? = null
        private var isPlayerRelease=false
        private var isPlayerStop=false
        fun setImage(imageView: ImageView, image_url: String,context:Context) {
            Log.d(TAG,"setImage $image_url")
            val requestBuilder = Glide.with(context)
                .load(image_url).override(200,200).sizeMultiplier(0.1f)
            val requestOptions: RequestOptions = RequestOptions()
                //  .override(200,200)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(true)
                .centerCrop()
                .dontAnimate()
                .dontTransform()

                // .placeholder()
                .priority(Priority.IMMEDIATE)
                //.encodeFormat(Bitmap.CompressFormat.PNG)
                .format(DecodeFormat.DEFAULT)
            context.let {
                Glide.with(it).load(image_url).thumbnail(requestBuilder).apply(requestOptions).into(imageView)
                /* Glide.with(it).applyDefaultRequestOptions(requestOptions)
                     .load(image_url).into(imageView)*/
            }

        }


        override fun action(
            extensionInfo: ExtensionInfo,
            itemData: FeedItem?,
            adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>?
        ) {
            Log.e(TAG, "${extensionInfo.action} $itemData, $position")
            val videoItem=itemData?.videos?.first()
            when (extensionInfo.action) {
                SelectiveAction.ATTACHED_CANDIDATE -> {
                    Log.e(TAG, "ATTACHED_CANDIDATE $position, isPlayerStop$isPlayerStop  isPlayerRelease $isPlayerRelease exoPlayer $exoPlayer ")
                    if (videoItem?.reference != null) {
                        if (exoPlayer==null||isPlayerRelease){
                            playSocialVideo(videoItem.reference, "m3u8")
                            isPlayerRelease=false
                            preparedPlayer(videoItem.reference,binding.root.context)
                            isPlayerStop=false
                        }else{
                            if (isPlayerStop){
                                preparedPlayer(videoItem.reference,binding.root.context)
                                isPlayerStop=false
                            }
                        }

                    }
                 }

                SelectiveAction.ATTACHED_WIN -> {
                    Log.e(TAG, "ATTACHED_WIN Play $position")

                    /*if (isPlayerRelease){
                        val video=(adapter as? SocialVideoAdapter)?.listItem?.get(position)?.videos?.first()
                        //playSocialVideo(videoItem?.reference,"m3u8")
                       // preparedPlayer(video?.reference,binding.root.context)
                    }*/
                    startPlayer()
                    binding.ivThumbnailView.visibility=View.GONE

                }
                SelectiveAction.ATTACHED_LOST -> {
                    Log.e(TAG, "ATTACHED_LOST pausePlayer ,$position")
                    pausePlayer()
                }
                SelectiveAction.DETACHED -> {
                    Log.e(TAG, "DETACHED  stopPlayer, ${position}")
                    //releasePlayer()
                    exoPlayer?.setPlayWhenReady(false)
                    exoPlayer?.stop();
                    exoPlayer?.seekTo(0)
                    /*videoItem?.imageLink?.let {

                        binding.ivThumbnailView.visibility=View.VISIBLE
                        setImage(binding.ivThumbnailView,
                            it,binding.root.context)

                    }*/
                    isPlayerStop=true
                    //pausePlayer()
                }
                else -> {
                    Log.e(TAG, "Elese  ,")
                }
            }
        }


        override fun getItemPosition(): Int {
            return position
        }

        override fun wantsToAttach(): Boolean {

            TODO("Not yet implemented")
        }

        override fun getAttchOrder(): Int {

            TODO("Not yet implemented")
        }

        override fun getAttachView(): View {
            return itemView
        }

        val playerListener=object : Player.Listener{


            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying){
                    binding.ivThumbnailView.visibility=View.GONE
                }else  binding.ivThumbnailView.visibility=View.VISIBLE
            }
        }
        @SuppressLint("UnsafeOptInUsageError")
        fun playSocialVideo(videoUrl: String?, type: String) {
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

            exoPlayer = ExoPlayer.Builder(binding.root.context)
                .setLoadControl(loadControl)
                .build().apply {
                    //repeatMode = Player.REPEAT_MODE_OFF
                    repeatMode = Player.REPEAT_MODE_ONE
                    //playWhenReady = true
                    addListener(object : Player.Listener {
                        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                            super.onMediaItemTransition(mediaItem, reason)
                            Log.d(
                                TAG,
                                "onMediaItemTransition.${position} reason$reason)"
                            )

                            when(reason){
                                Player.MEDIA_ITEM_TRANSITION_REASON_AUTO->{

                                }
                                3->{

                                }
                            }
                        }
                        override fun onPlaybackStateChanged(playbackState: Int) {
                            super.onPlaybackStateChanged(playbackState)
                            Log.d(
                                TAG,
                                "onPlayerStateChanged. playWhenReady: $playWhenReady, playbackState: $playbackState)"
                            )

                        }

                        override fun onPlayerError(error: PlaybackException) {
                            super.onPlayerError(error)
                            Log.d(TAG, "onPlayerError$position $error")
                            error.printStackTrace()
                        }
                    })
                }


            try {

             } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        @SuppressLint("UnsafeOptInUsageError")
        fun preparedPlayer(videoUrl: String?,context: Context){
            val uri=Uri.parse(videoUrl)
            val dataSourceFactory = cacheDataSourceFactory
           val mediaSource= HlsMediaSource.Factory(DefaultDataSource.Factory(context))
                //.setStreamKeys(cacheStreamKeys)
                .setAllowChunklessPreparation(true)
                .createMediaSource(MediaItem.fromUri(uri))
            //player.stop(true)
            binding.socialVideoView.player=exoPlayer
            exoPlayer?.prepare(mediaSource)
            exoPlayer?.playWhenReady=false
            //player.addListener(playerListener)
        }
        fun releasePlayer() {
            try {
                //exoPlayer?.playWhenReady = false
                exoPlayer?.release()
                //exoPlayer?.stop()
               // exoPlayer=null
                isPlayerRelease=true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun pausePlayer() {
            exoPlayer?.playWhenReady = false
            exoPlayer?.seekTo(0)
        }

        fun startPlayer() {
            exoPlayer?.playWhenReady = true
         }
    }

    companion object{
        const val TAG = "SocialVideoAdapter"

    }
}

interface SocialVideoAdapterCallback {
    fun showDialog(feedItem: FeedItem)

}

interface ViewHolderExtension {
    // Every state change via this method from RecyclerView
    fun action(
        extensionInfo: ExtensionInfo,
        itemData: FeedItem?,
        adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>?=null
    )

    // Identity viewholder
    fun getItemPosition(): Int

    // Another option is letting outside control this item should win or lose when selecting win or lose from attach candidate list
    fun wantsToAttach(): Boolean

    // It can change the action invoke order
    fun getAttchOrder(): Int

    fun getAttachView(): View
}

data class ExtensionInfo(var action: SelectiveAction = SelectiveAction.NONE)


enum class SelectiveAction {
    NONE,
    ATTACHED_WIN,
    ATTACHED_LOST,
    ATTACHED_CANDIDATE,
    DETACHED
}
