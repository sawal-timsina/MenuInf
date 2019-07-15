package com.codeace.menuinf

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class FoodDialog : DialogFragment() {
    private lateinit var listener: FoodDialogListener

    interface FoodDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
    }

    private lateinit var itemName: String
    private lateinit var itemCategory: String
    private lateinit var itemSpiciness: String
    private lateinit var itemPrice: String

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as FoodDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                (context.toString() +
                        " must implement FoodDialogListener")
            )
        }
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        println("Created Dialog")
        return activity?.let {

            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(inflater.inflate(R.layout.dialog_layout, null))
                .setPositiveButton(R.string.ok) { _, _ ->
                    listener.onDialogPositiveClick(this)
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun dataToUpdate(name: String, category: String, spiciness: String, price: Double) {
        itemName = name
        itemCategory = category
        itemSpiciness = spiciness
        itemPrice = price.toString()
    }
}