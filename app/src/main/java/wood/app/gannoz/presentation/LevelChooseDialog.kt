package wood.app.gannoz.presentation

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.DialogFragment
import wood.app.gannoz.R
import wood.app.gannoz.domain.entity.Level

class LevelChooseDialog : DialogFragment() {
    var onItemClickListener: ((Level) -> Unit)? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.choose_level))
            .setItems(R.array.levels) { _, which ->
                onItemClickListener?.invoke(Level.values()[which])
            }
            .setCancelable(false)
        val dialog = builder.create()
        dialog.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (dialog.isShowing) {
                    activity?.finish()
                }
            }
            true
        }
        return dialog
    }

}