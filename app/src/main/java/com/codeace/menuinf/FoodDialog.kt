package com.codeace.menuinf

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class FoodDialog : DialogFragment() {
    private val IMAGE_PICK_CODE = 1000
    private val PERMISSION_CODE = 1001
    private var data: Data = Data(Uri.EMPTY, "", "", "", 0.0)
    private var pos: Int = -1
    private lateinit var listener: FoodDialogListener
    private lateinit var foodImage: ImageView

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
        foodImage = dialogView.findViewById(R.id.foodImage)
        val editItemName = dialogView.findViewById<EditText>(R.id.editItemName)
        val editItemCategory = dialogView.findViewById<EditText>(R.id.editItemCategory)
        val editItemSpiciness = dialogView.findViewById<EditText>(R.id.editItemSpiciness)
        val editItemPrice = dialogView.findViewById<EditText>(R.id.editItemPrice)
        if (pos != -1) {
            foodImage.setImageURI(data.image)
            editItemName.text = Editable.Factory.getInstance().newEditable(data.name)
            editItemCategory.text = Editable.Factory.getInstance().newEditable(data.category)
            editItemSpiciness.text = Editable.Factory.getInstance().newEditable(data.spiciness)
            editItemPrice.text = Editable.Factory.getInstance().newEditable(data.price.toString())
        }

        dialogView.findViewById<Button>(R.id.imageButton).setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        println("got Image : ".plus(requestCode).plus(" : " + Activity.RESULT_OK).plus(" : $IMAGE_PICK_CODE"))

        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            foodImage.setImageURI(data.data)
            this.data.image = data.data!!
            println("got Image")
        }
    }


    fun dataToUpdate(data: Data, pos: Int) {
        this.data = data
        this.pos = pos
    }
}