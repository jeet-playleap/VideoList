package com.example.videolist

 import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
 import android.widget.EditText


class CustomDialogClass(context: Context, val vId: String,val imageLink: String) : Dialog(context),android.view.View.OnClickListener  {


    var c: Activity? = null
    var d: Dialog? = null
    var yes: Button? = null
    var no:Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.video_adapter_dialog)

        yes = findViewById<View>(R.id.btn_yes) as Button
        no = findViewById<View>(R.id.btn_no) as Button
        findViewById<EditText>(R.id.et_videoId).apply {
            this.setText(vId)
        }
        findViewById<EditText>(R.id.et_image_url).apply {
            this.setText(imageLink)
        }
        yes!!.setOnClickListener(this)
        no!!.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.btn_yes->{
                dismiss()
            }

        }
    }

}