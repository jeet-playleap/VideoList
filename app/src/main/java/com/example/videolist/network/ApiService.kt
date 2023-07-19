package com.example.videolist.network

import com.example.videolist.model.VideoFeedModel
import retrofit2.Call
import retrofit2.http.*


interface ApiService {
    @GET("feeds/feedsByUserId")
    @Headers("fmsvisitorid: 20fez7n0q78jum28rrwnw")
    fun getVideoList(@Query("filter") filter:String, @Query("q") q:String):Call<VideoFeedModel>
}