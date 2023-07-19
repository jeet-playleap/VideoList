package com.example.leapvideofeed.ui.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.videolist.MainActivity
import com.example.videolist.databinding.FragmentMainBinding
import com.example.videolist.model.FeedItem
import com.example.videolist.network.ApiService
import com.example.videolist.ui.main.MainViewModel


class MainFragment : Fragment() {
    val mainActivity:MainActivity get() {
        return context as MainActivity
    }

    lateinit var apiService:ApiService
    companion object {
        fun newInstance(service: ApiService) = MainFragment().apply {
            this.apiService=service
        }
    }

    private val viewModel: MainViewModel =MainViewModel.provideFactory(apiService,this).create(MainViewModel::class.java)
    //private lateinit var homePagerAdapter: SocialVideoAdapter
    private var _viewBinding: FragmentMainBinding?=null
    private val viewBinding:FragmentMainBinding get()  = _viewBinding!!
    private var loadingFirst: Boolean = true
    private var currentFragPos: Int = 0
    private var currentCallPos: Int = 0
    private val fragList = ArrayList<FeedItem>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _viewBinding=FragmentMainBinding.inflate(inflater)
        return viewBinding.root //inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadInitData()
        setObserver()
    }

    private fun setObserver() {
        viewModel.apply {
            viewBinding?.apply {
                getHomeListLiveData.observe(viewLifecycleOwner) {
                    it?.let {
                        hasMore=true
                        // hasMore= it.it?.streams_paging?.hasMore==true||it.data?.video_paging?.hasMore==true
                        if (it.feeds.isNotEmpty()) {

                            //ivWilli.visibilityGone()
                            for ( (i,streams_videos) in it.feeds.withIndex()) {

                                if (i == 0) {
                                    currentCallPos = fragList.size + 1
                                }
                                fragList.add(streams_videos)
                                Log.e("Test"," id ${streams_videos.id},${streams_videos.videos.first().reference}")
                            }
                            //homePagerAdapter.upDataList(fragList)
                            Log.e("Test","${fragList.size} ")

                        } else {
                            // rvVideoFeed.visibilityGone()
                            // ivWilli.visibilityVisible()
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
        initHomePagerAdapter()
        isLoading = true
        viewModel.getHomeList(true, limit = 10)
        initScrollListener()
    }

    private fun initHomePagerAdapter() {
        try {
            val snapHelper = PagerSnapHelper() // Or PagerSnapHelper or LinearSnapHelper
            snapHelper.attachToRecyclerView(viewBinding?.rvVideoFeed)
/*
            homePagerAdapter = SocialVideoAdapter(
                context
            )
*/
            viewBinding?.rvVideoFeed?.apply {
                isSaveEnabled = false
                //adapter = homePagerAdapter
                // orientation = ViewPager2.ORIENTATION_VERTICAL
                //offscreenPageLimit = 2
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    var isLoading = false
    var hasMore = true
    private fun initScrollListener() {
        viewBinding?.rvVideoFeed?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                Log.i("TestLiveFrag", "onScr hasMore $hasMore isLoading $isLoading")
                if (hasMore) {
                    if (!isLoading) {
                        val maxValue = linearLayoutManager.findLastVisibleItemPosition()
                        Log.i(
                            "TestLiveFrag",
                            "onScroll ${linearLayoutManager.findLastVisibleItemPosition()}, , ,   max $maxValue ${linearLayoutManager.itemCount}"
                        )
                        if (  maxValue > linearLayoutManager.itemCount - 6) {
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

    fun loadMore(){
        viewModel.getHomeList(
            false,
            limit = 10,
            offset =  fragList.size       )
    }

    override fun onDestroy() {
        super.onDestroy()
        _viewBinding=null
    }
}