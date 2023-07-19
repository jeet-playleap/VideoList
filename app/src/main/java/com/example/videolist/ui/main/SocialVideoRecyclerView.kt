package com.example.videolist.ui.main

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.size
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.videolist.adapter.ExtensionInfo
import com.example.videolist.adapter.SelectiveAction
import com.example.videolist.adapter.SocialVideoAdapter
import com.example.videolist.adapter.ViewHolderExtension


class SocialVideoRecyclerView : RecyclerView {
    private var attachedToWindow = false

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(
        context,
        attributeSet,
        defStyle
    )

    override fun setAdapter(adapter: Adapter<*>?) {
        val oldAdapter=getAdapter()
        super.setAdapter(adapter)

    }

    override fun onAttachedToWindow() {
        attachedToWindow = true
        super.onAttachedToWindow()
        firstItemRender=false
        val adapter = adapter
        Log.d(TAG,"onAttachedToWindow")
        //viewHolderExtensions
       // if (adapter is ViewHolderExtension) (adapter.ho as ViewHolderExtension?)?.onAttachedToWindow()
    }
    var firstItemRender=false

    override fun onDetachedFromWindow() {
        attachedToWindow = false
        super.onDetachedFromWindow()
        Log.d(TAG,"onDetachedFromWindow")
        val adapter = adapter
        //if (adapter is ViewHolderExtension) (adapter as ViewHolderExtension?)?.onDetachedFromWindow()

    }
    var currentAttachItemCount=0

    override fun onChildAttachedToWindow(child: View) {
        super.onChildAttachedToWindow(child)
        getChildViewHolder(child)
        if (getChildViewHolder(child) is ViewHolderExtension) {
            val extensionInfo=  (getChildViewHolder(child) as ViewHolderExtension?)
            if (extensionInfo != null) {
               if (!firstItemRender){
                   val data=  (adapter as SocialVideoAdapter).listItem.get(extensionInfo.getItemPosition())
                   extensionInfo.action(
                       ExtensionInfo(SelectiveAction.ATTACHED_CANDIDATE),
                       data,
                       null
                   )
                   extensionInfo.action(ExtensionInfo(SelectiveAction.ATTACHED_WIN), null, null)
                   viewHolderExtensions.add(extensionInfo)
                   firstItemRender=true
               }else {
                   val data=  (adapter as SocialVideoAdapter).listItem.get(extensionInfo.getItemPosition())
                   extensionInfo.action(
                       ExtensionInfo(SelectiveAction.ATTACHED_CANDIDATE),
                       data,
                       null
                   )
                   viewHolderExtensions.add(extensionInfo)
               }
            }
            currentAttachItemCount++
            Log.d(TAG,"onChildAttached ${extensionInfo?.getItemPosition()} ${viewHolderExtensions.size} currentAttachItemCount $currentAttachItemCount")
        }
    }
   /* var oldY:Float?=0f;
    override fun onTouchEvent(e: MotionEvent?): Boolean {
        Log.d(TAG,"${e?.y} ,oldY ${oldY}")

        when(e?.action){
            MotionEvent.ACTION_DOWN->{

            }
            MotionEvent.ACTION_MOVE->{

            }
            MotionEvent.ACTION_UP->{
                oldY=this.y
            }
        }
        return super.onTouchEvent(e)
    }*/
    var tempDetached:ViewHolderExtension?=null
    override fun onChildDetachedFromWindow(child: View) {
        super.onChildDetachedFromWindow(child)
        val viewHolder=getChildViewHolder(child)

        if (viewHolder is ViewHolderExtension) {
            /*if (tempDetached==null){
                tempDetached=viewHolder
                return
            }*/

           /* var isDownScrollEnable=false
            lastDettacheItemUp.forEach {
                if (viewHolder.getItemPosition()+1>it.getItemPosition()){
                    isDownScrollEnable=true
                }
            }*/
            val data = (adapter as SocialVideoAdapter).listItem[viewHolder.getItemPosition()?:0]
            currentAttachItemCount--
             if (viewHolder.getItemPosition()>firstCompletelyVisible&&viewHolder.getItemPosition()<=firstCompletelyVisible+2){
               /* viewHolder.action(ExtensionInfo(SelectiveAction.DETACHED), data, null)
                lastDettacheItemUp.remove(viewHolder)
                Log.e(TAG,"1onChildDetached${viewHolder?.getItemPosition()} $firstCompletelyVisible ${lastDettacheItemUp.size},${lastDettacheItemUp}")
*/
                 if (lastDettacheItemUp.size>2){
                     val firstViewHolder=lastDettacheItemUp.firstOrNull()
                     Log.e(TAG,"onChildDetached${firstViewHolder?.getItemPosition()} ")
                     firstViewHolder?.action(ExtensionInfo(SelectiveAction.DETACHED), data, null)
                     lastDettacheItemUp.remove(firstViewHolder)
                 }
                 viewHolder.action(ExtensionInfo(SelectiveAction.ATTACHED_LOST), null, null)
                 lastDettacheItemUp.add(viewHolder)
                 Log.e(TAG,"1onChildDetached${viewHolder?.getItemPosition()} $firstCompletelyVisible ${lastDettacheItemUp.size},${lastDettacheItemUp}")

             }else if (viewHolder.getItemPosition()<firstCompletelyVisible&&viewHolder.getItemPosition()>=firstCompletelyVisible-2){
                /* viewHolder.action(ExtensionInfo(SelectiveAction.DETACHED), data, null)
                 lastDettacheItemDown.remove(viewHolder)
                 Log.e(TAG,"2onChildDetached${viewHolder?.getItemPosition()} $firstCompletelyVisible ${lastDettacheItemUp.size},${lastDettacheItemUp}")
*/
                 if (lastDettacheItemDown.size>2){
                     val firstViewHolder=lastDettacheItemDown.firstOrNull()
                     Log.e(TAG,"onChildDetached${firstViewHolder?.getItemPosition()} ")
                     firstViewHolder?.action(ExtensionInfo(SelectiveAction.DETACHED), data, null)
                     lastDettacheItemDown.remove(firstViewHolder)
                 }
                 viewHolder.action(ExtensionInfo(SelectiveAction.ATTACHED_LOST), null, null)
                 lastDettacheItemDown.add(viewHolder)
                 Log.e(TAG,"2onChildDetached${viewHolder?.getItemPosition()} $firstCompletelyVisible ${lastDettacheItemUp.size},${lastDettacheItemUp}")

             }else if (firstCompletelyVisible==viewHolder.getItemPosition()){
                 viewHolder.action(ExtensionInfo(SelectiveAction.ATTACHED_LOST), null, null)
                 lastDettacheItemDown.add(viewHolder)
                 lastDettacheItemUp.add(viewHolder)
                 Log.e(TAG,"3onChildDetached${viewHolder?.getItemPosition()} $firstCompletelyVisible ,up${lastDettacheItemUp.size},down ${lastDettacheItemDown.size}")
                 /*if (lastDettacheItemDown.size>2){
                     val firstViewHolder=lastDettacheItemDown.firstOrNull()
                     Log.e(TAG,"onChildDetached${firstViewHolder?.getItemPosition()} ")
                     if (firstCompletelyVisible!=firstViewHolder?.getItemPosition()){
                         firstViewHolder?.action(ExtensionInfo(SelectiveAction.DETACHED), data, null)
                     }
                     lastDettacheItemDown.remove(firstViewHolder)
                 }
                 if (lastDettacheItemUp.size>2){
                     val firstViewHolder=lastDettacheItemUp.firstOrNull()
                     Log.e(TAG,"onChildDetached${firstViewHolder?.getItemPosition()} ")
                     if (firstCompletelyVisible!=firstViewHolder?.getItemPosition()){
                         firstViewHolder?.action(ExtensionInfo(SelectiveAction.DETACHED), data, null)
                     }
                     lastDettacheItemUp.remove(firstViewHolder)
                 }*/
             }

             /*else if (lastDettacheItemUp.size>4){
                val firstViewHolder=lastDettacheItemUp.firstOrNull()
                Log.e(TAG,"onChildDetached${firstViewHolder?.getItemPosition()} ")
                firstViewHolder?.action(ExtensionInfo(SelectiveAction.DETACHED), data, null)
                lastDettacheItemUp.remove(firstViewHolder)
                viewHolder.action(ExtensionInfo(SelectiveAction.ATTACHED_LOST), null, null)
                lastDettacheItemUp.add(viewHolder)
                Log.e(TAG,"2onChildDetached${viewHolder?.getItemPosition()} $firstCompletelyVisible ${lastDettacheItemUp.size},${lastDettacheItemUp}")

            }*/
            else{
                 viewHolder.action(ExtensionInfo(SelectiveAction.DETACHED), data, null)
                 lastDettacheItemUp.remove(viewHolder)
                 lastDettacheItemDown.remove(viewHolder)
                 Log.e(TAG,"3onChildDetached${viewHolder?.getItemPosition()} $firstCompletelyVisible ${lastDettacheItemUp.size},${lastDettacheItemDown.size}")

                 /* viewHolder.action(ExtensionInfo(SelectiveAction.ATTACHED_LOST), null, null)
                  lastDettacheItemUp.add(viewHolder)
                  Log.e(TAG,"3onChildDetached${viewHolder?.getItemPosition()} $firstCompletelyVisible ${lastDettacheItemUp.size},${lastDettacheItemUp}")
  */
            }

            if (isUpScrollEnable && firstItemRender ){
                val tempList=ArrayList<ViewHolderExtension>()
                lastDettacheItemUp.forEach {
                    if (firstCompletelyVisible-2>it.getItemPosition()&&it.getItemPosition()>firstCompletelyVisible+3){
                        it.action(ExtensionInfo(SelectiveAction.DETACHED), data, null)
                        tempList.add(it)
                        Log.d(TAG,"onChildAttached Detached ${it.getItemPosition()}")
                    }

                }
                tempList.forEach {
                    lastDettacheItemUp.remove(it)
                }
            }else if (isDownScrollEnable && firstItemRender ){
                val tempList=ArrayList<ViewHolderExtension>()

                lastDettacheItemDown.forEach {

                    if (it.getItemPosition()>firstCompletelyVisible+2&&firstCompletelyVisible-3>it.getItemPosition()){
                       it.action(ExtensionInfo(SelectiveAction.DETACHED), data, null)
                        Log.d(TAG,"onChildAttached Detached ${it.getItemPosition()}")
                        tempList.add(it)
                   }

                }
                tempList.forEach {
                    lastDettacheItemUp.remove(it)
                }

            }
            viewHolderExtensions.remove(viewHolder)
/*
            if (!isDownScrollEnable){
                lastDettacheItemUp.addFirst(viewHolder)
                Log.e(TAG,"onChild${viewHolder.getItemPosition()} Went To Detached  isUpScrollEnable")
                if (lastDettacheItemUp.size>=4){
                    val firstViewHolder=lastDettacheItemUp.lastOrNull()
                    firstViewHolder?.action(ExtensionInfo(SelectiveAction.DETACHED), null, null)
                    // lastDettacheItem.remove(viewHolder)
                    lastDettacheItemUp.remove(firstViewHolder)
                    Log.e(TAG,"onChildDetached${firstViewHolder?.getItemPosition()} isUpScrollEnable  findFirstVisbileItem: $findFirstVisbileItem, lastVisibilItem: $lastVisibilItem ")
                }
                lastDettacheItemDown.clear()
            }else  {
                lastDettacheItemDown.addFirst(viewHolder)
                Log.e(TAG,"onChild${viewHolder.getItemPosition()} Went To Detached isDownScrollEnable ")

                if (lastDettacheItemDown.size>=4){
                    val firstViewHolder=lastDettacheItemDown.firstOrNull()
                    firstViewHolder?.action(ExtensionInfo(SelectiveAction.DETACHED), null, null)
                    // lastDettacheItem.remove(viewHolder)
                    lastDettacheItemDown.remove(firstViewHolder)
                    Log.e(TAG,"onChildDetached${firstViewHolder?.getItemPosition()} isDownScrollEnable findFirstVisbileItem: $findFirstVisbileItem, lastVisibilItem: $lastVisibilItem ")
                }
                lastDettacheItemUp.clear()
            }
*/
        }
    }

    override fun onScreenStateChanged(screenState: Int) {
        super.onScreenStateChanged(screenState)

    }

    var firstCompletelyVisible=0
    var currentSState:Int = RecyclerView.SCROLL_STATE_IDLE
    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        currentSState=state

        Log.d(TAG,"currentSState state ${state} ")

        if (state == SCROLL_STATE_IDLE) {
            val firstCompletelyVisibleItemPosition =
                (layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
            viewHolderExtensions.forEach {

                val position = it.getItemPosition()
                Log.d(
                    TAG,
                    "currentSState SCROLL_STATE_IDLE ${state},$position,$firstCompletelyVisibleItemPosition ${firstCompletelyVisibleItemPosition == position} startPosition  "
                )
               // val data=     (adapter as SocialVideoAdapter).listItem.get(it.getItemPosition())
                if (firstCompletelyVisibleItemPosition == position) {

                    it.action(ExtensionInfo(SelectiveAction.ATTACHED_WIN), null,adapter)
                } else {
                    it.action(ExtensionInfo(SelectiveAction.ATTACHED_LOST), null, null)
                }
            }
             firstCompletelyVisible=firstCompletelyVisibleItemPosition
        } else if (state == SCROLL_STATE_DRAGGING) {


/*
            viewHolderExtensions.forEach {
                val firstCompletelyVisibleItemPosition =
                    (layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                val position = it.getItemPosition()
                if (firstCompletelyVisibleItemPosition == position) {

                }
            }
*/
            val currentFirstVisible =
                (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

            if (currentFirstVisible > firstVisibleInListview) {
                isUpScrollEnable==true
                Log.i(
                    TAG,
                    "RecyclerView scrolled: scroll up!"
                )
            } else {
                isDownScrollEnable==true
                Log.i(TAG, "RecyclerView scrolled: scroll down!")
            }

            firstVisibleInListview = currentFirstVisible

        }



    }


    var isUpScrollEnable=false
    var isDownScrollEnable=false

   /*
    override fun onScrolled(dx: Int, dy: Int) {
        super.onScrolled(dx, dy)
        //val currentFirstVisible: Int = (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        if (dy > 0) {
            isDownScrollEnable=false
            isUpScrollEnable=true
            lastDettacheItemDown.clear()
            Log.i(TAG, "RecyclerView  scroll up! isUpScrollEnable $isUpScrollEnable isDownScrollEnable $isDownScrollEnable")
        } else {
            isDownScrollEnable=true
            isUpScrollEnable=false
            lastDettacheItemUp.clear()
            Log.i(TAG, "RecyclerView  scroll down! isDownScrollEnable $isDownScrollEnable isUpScrollEnable $isUpScrollEnable")

        }

    }*/

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        Log.d(TAG,"onWindowFocusChanged")
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        Log.d(TAG,"onVisibilityChanged $visibility")
    }

    override fun attachViewToParent(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        super.attachViewToParent(child, index, params)
       // Log.d(TAG,"attachViewToParent $index")
    }



    val viewHolderExtensions:HashSet<ViewHolderExtension> = hashSetOf()
    var lastDettacheItemUp:ArrayDeque<ViewHolderExtension> = ArrayDeque()
    var lastDettacheItemDown:ArrayDeque<ViewHolderExtension> = ArrayDeque()
     val TAG = "SocialVideoRecyclerView"

    private var firstVisibleInListview = 0


}