package com.example.videolist.ui.main

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.savedstate.SavedStateRegistryOwner
import com.example.videolist.model.FilterQuery
import com.example.videolist.model.VideoFeedModel
import com.example.videolist.network.ApiService
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(val apiService: ApiService) : ViewModel() {
    private var getHomeListMutableLiveData = MutableLiveData<VideoFeedModel>()
    val getHomeListLiveData: LiveData<VideoFeedModel> get() = getHomeListMutableLiveData
    fun getHomeList(
        loading: Boolean,
        limit: Int = 10,
        offset: Int = 0
    ) {
        //offset:Int, @Query ("limit") limit:Int
        val filterMap=HashMap<String,Int>()
        filterMap["offset"] = offset
        filterMap["limit"] = limit

        //hashMapOf(Pair("offset",offset),Pair("limit",limit))
        val filter=Gson().toJson(FilterQuery(offset,limit),FilterQuery::class.java)//"{offset:$offset,limit:$limit}"
        val sort=Gson().toJson(hashMapOf(Pair("smartSort",true)))//"{smartSort:true}"
        Log.e("TestReqQuery","$filter , $sort")
        apiService.getVideoList(filter,sort   ).enqueue(object : Callback<VideoFeedModel> {
            override fun onResponse(
                call: Call<VideoFeedModel>,
                response: Response<VideoFeedModel>
            ) {
                Log.e("TestResponseRow","r${response.raw()}")

                Log.e("TestResponse","${response.body()?.meta} ${response.code()},r${response.raw()}")
                getHomeListMutableLiveData.postValue(response.body())
            }

            override fun onFailure(call: Call<VideoFeedModel>, t: Throwable) {
                Log.e("Test","${t.localizedMessage} ${t.message}")
            }
        })
    }
    companion object{
        /*
                val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(
                        modelClass: Class<T>,
                        extras: CreationExtras
                    ): T {
                        // Get the Application object from extras
                        val application =
                            checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                        // Create a SavedStateHandle for this ViewModel from extras
                        val savedStateHandle = extras.createSavedStateHandle()

                        return MainViewModel(
                            (application as ToDoApplication).databaseService
                        ) as T
                    }
                }
        */
        fun provideFactory(
            myRepository: ApiService,
            owner: SavedStateRegistryOwner,
            defaultArgs: Bundle? = null,
        ): AbstractSavedStateViewModelFactory =
            object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    key: String,
                    modelClass: Class<T>,
                    handle: SavedStateHandle
                ): T {
                    return MainViewModel(myRepository) as T
                }
            }
    }
}
