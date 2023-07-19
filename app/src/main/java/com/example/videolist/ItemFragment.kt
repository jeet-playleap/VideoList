package com.example.videolist

import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.example.videolist.layoutmanager.PreCachingLayoutManager
import com.example.videolist.placeholder.PlaceholderContent

/**
 * A fragment representing a list of Items.
 */
class ItemFragment : Fragment() {

    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)

        // Set the adapter
        val snapHelper=PagerSnapHelper()

        val adapterItem=MyItemRecyclerViewAdapter()
        if (view is RecyclerView) {
            snapHelper.attachToRecyclerView(view)

            with(view) {
                setItemViewCacheSize(10)
                layoutManager = when {
                    columnCount <= 1 -> PreCachingLayoutManager(context).apply {
                        val displayHeight=Resources.getSystem().displayMetrics.heightPixels
                        setExtraLayoutSpace(displayHeight*10)
                    }//LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter =adapterItem
                PlaceholderContent.ITEMS.forEach {
                    adapterItem.addItem(it)
                }
            }
        }
        return view
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            ItemFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}