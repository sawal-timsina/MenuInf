package com.codeace.menuinf

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class FoodDialog : DialogFragment() {
    lateinit var data: Data
    var pos: Int = 0
    private lateinit var listener: FoodDialogListener

    interface FoodDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment, data: Data, pos: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as FoodDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                (context.toString() +
                        " must implement NoticeDialogListener")
            )
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        val dialogView = LayoutInflater.from(activity!!).inflate(R.layout.dialog_layout, null)
        val editItemName = dialogView.findViewById<EditText>(R.id.editItemName)
        val editItemCategory = dialogView.findViewById<EditText>(R.id.editItemCategory)
        val editItemSpiciness = dialogView.findViewById<EditText>(R.id.editItemSpiciness)
        val editItemPrice = dialogView.findViewById<EditText>(R.id.editItemPrice)
        editItemName.text = Editable.Factory.getInstance().newEditable(data.name)
        editItemCategory.text = Editable.Factory.getInstance().newEditable(data.category)
        editItemSpiciness.text = Editable.Factory.getInstance().newEditable(data.spiciness)
        editItemPrice.text = Editable.Factory.getInstance().newEditable(data.price.toString())

        builder.setView(dialogView)
        builder.setPositiveButton(R.string.ok) { _, _ ->
            data.name = editItemName.text.toString()
            data.category = editItemCategory.text.toString()
            data.spiciness = editItemSpiciness.text.toString()
            data.price = editItemPrice.text.toString().toDouble()
            listener.onDialogPositiveClick(this, data, pos)
        }
        builder.setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }
        return builder.create()
    }

    fun dataToUpdate(data: Data, pos: Int) {
        this.data = data
        this.pos = pos
    }
}