/*
 * *
 *  Created by Vijay
 * /
 */

package `in`.angryvijay.vjuploader.utils

import `in`.angryvijay.vjuploader.R
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ImageView

class CustomProgressDialog : Dialog {

    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, theme: Int) : super(context!!, theme) {}

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        val imageView =
            findViewById<View>(R.id.spinnerImageView) as ImageView
        val spinner = imageView.background as AnimationDrawable
        spinner.start()
    }

    companion object {
        var dialog: CustomProgressDialog? = null
        fun show(
            context: Context,
            indeterminate: Boolean,
            cancelable: Boolean
        ): CustomProgressDialog {
            if (dialog == null)
            dialog =
                CustomProgressDialog(context, R.style.ProgressDialogTheme)
            dialog!!.setTitle("")
            dialog!!.setContentView(R.layout.progress_dialog_layout)
            dialog!!.setCancelable(cancelable)
            dialog!!.window!!.attributes.gravity = Gravity.CENTER
            val lp = dialog!!.window!!.attributes
            lp.dimAmount = 0.2f
            dialog!!.window!!.attributes = lp
        //    dialog!!.window!!.setWindowAnimations(R.style.dialog_zoom)
            try {
                if (!dialog!!.isShowing) {
                    dialog!!.show()
                }else{
                    dialog!!.dismiss()
                }
            }catch (e:WindowManager.BadTokenException){
                dialog =
                   CustomProgressDialog(context, R.style.ProgressDialogTheme)
                dialog!!.setTitle("")
                dialog!!.setContentView(R.layout.progress_dialog_layout)
                dialog!!.setCancelable(cancelable)
                dialog!!.window!!.attributes.gravity = Gravity.CENTER
                val lp = dialog!!.window!!.attributes
                lp.dimAmount = 0.2f
                dialog!!.window!!.attributes = lp
             //   dialog!!.window!!.setWindowAnimations(R.style.dialog_zoom)
                dialog!!.show()
            }
            return dialog as CustomProgressDialog
        }

    }
}
