package layout

import android.app.Dialog
import android.content.Context
import android.text.method.ScrollingMovementMethod
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.solicity.provider.R
import com.general.files.GeneralFunctions
import com.view.MButton
import com.view.MTextView
import com.view.MaterialRippleLayout

class PreferenceDailog(val actContext: Context) {


    lateinit var hotoUseDialog: AlertDialog
    var generalFunc = GeneralFunctions(actContext)
    fun showPreferenceDialog(title: String, Msg: String, img: Int, isUpload: Boolean) {
        val builder = AlertDialog.Builder(actContext)
        val inflater = actContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView = inflater.inflate(R.layout.desgin_preference_help, null)
        builder.setView(dialogView)
        val iamage_source = dialogView.findViewById<View>(R.id.iamage_source) as ImageView
        val uploadArea = dialogView.findViewById<View>(R.id.uploadArea) as LinearLayout
        val uploadImgArea = dialogView.findViewById<View>(R.id.uploadImgArea) as LinearLayout
        val uploadTitleTxt = dialogView.findViewById<View>(R.id.uploadTitleTxt) as MTextView

        uploadTitleTxt.setText(generalFunc.retrieveLangLBl("Upload item Photo","LBL_UPLOAD_ITEM_PHOTO"))

        if (img != null && !img.equals("")) {
            iamage_source!!.setImageResource(img)
        }
        if (isUpload) {
            uploadArea.visibility == View.VISIBLE
        }



        val okTxt = dialogView.findViewById<View>(R.id.okTxt) as MTextView
        val titileTxt = dialogView.findViewById<View>(R.id.titileTxt) as MTextView
        val msgTxt = dialogView.findViewById<View>(R.id.msgTxt) as MTextView
        titileTxt.text = title
        okTxt.text = generalFunc.retrieveLangLBl("Ok", "LBL_BTN_OK_TXT ")
        msgTxt.text = "" + Msg
        msgTxt.movementMethod = ScrollingMovementMethod()
        okTxt.setOnClickListener { hotoUseDialog.dismiss() }
        hotoUseDialog = builder.create()
        hotoUseDialog.setCancelable(true)
        if (generalFunc.isRTLmode == true) {
            generalFunc.forceRTLIfSupported(hotoUseDialog)
        }
        hotoUseDialog!!.getWindow()!!.setBackgroundDrawable(actContext.getResources().getDrawable(R.drawable.all_roundcurve_card))
        hotoUseDialog.show()
    }



}