package com.lightworld.childtrack.helper

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.lightworld.childtrack.TRACK_ENTITY_NAME
import com.lightworld.childtrack.TrackMapActivity
import org.jetbrains.anko.startActivity

/**
 * Created by heyue on 2018/1/2.
 */
object ClipboardManagerHelper {
    fun discernSymbol(mContext: Context) {
        //获取 剪切板
        val cm = mContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val copyText = cm.primaryClip?.getItemAt(0)?.text
        copyText?.let {
            if (it.contains('¥') && copyText.endsWith('¥')) {
                var split: List<String> = copyText?.split('¥')
                var id = split[1]
                mContext.startActivity<TrackMapActivity>(TRACK_ENTITY_NAME to id)
                //重置剪切板
                val mClipData = ClipData.newPlainText("", "")
                cm.primaryClip = mClipData
            }
        }
    }


    fun splitSymbol(copyText: CharSequence?): String {
        copyText?.let {
            if (it.contains('¥') && copyText.endsWith('¥')) {
                var split: List<String> = copyText?.split('¥')
                return split[1]
            }
        }
        return copyText.toString()

    }
}