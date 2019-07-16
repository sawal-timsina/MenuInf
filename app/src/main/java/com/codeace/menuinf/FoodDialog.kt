package com.codeace.menuinf

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide

class FoodDialog : DialogFragment() {
    private val IMAGE_PICK_CODE = 1000
    private var foodData: FoodData = FoodData(0, "", "", "", "", 0.0)
    private var pos: Int = -1
    private lateinit var listener: FoodDialogListener
    private lateinit var foodImage: ImageView

    interface FoodDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment, foodData: FoodData, pos: Int)
    }

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
        val builder = AlertDialog.Builder(activity!!)
        val dialogView = LayoutInflater.from(activity!!).inflate(R.layout.dialog_layout, null)
        foodImage = dialogView.findViewById(R.id.foodImage)
        val editItemName = dialogView.findViewById<EditText>(R.id.editItemName)
        val editItemCategory = dialogView.findViewById<EditText>(R.id.editItemCategory)
        val editItemSpiciness = dialogView.findViewById<EditText>(R.id.editItemSpiciness)
        val editItemPrice = dialogView.findViewById<EditText>(R.id.editItemPrice)
        if (pos != -1) {
            Glide.with(this).load(foodData.image).centerCrop()
                .placeholder(R.drawable.imageplaceholder).into(foodImage)
            editItemName.text = Editable.Factory.getInstance().newEditable(foodData.name)
            editItemCategory.text = Editable.Factory.getInstance().newEditable(foodData.category)
            editItemSpiciness.text = Editable.Factory.getInstance().newEditable(foodData.spiciness)
            editItemPrice.text = Editable.Factory.getInstance().newEditable(foodData.price.toString())
        }

        dialogView.findViewById<ImageView>(R.id.foodImage).setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        builder.setView(dialogView)
        builder.setPositiveButton(R.string.ok) { _, _ ->
            foodData.name = editItemName.text.toString()
            foodData.category = editItemCategory.text.toString()
            foodData.spiciness = editItemSpiciness.text.toString()
            foodData.price = editItemPrice.text.toString().toDouble()
            listener.onDialogPositiveClick(this, foodData, pos)
        }
        builder.setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }
        return builder.create()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            foodImage.setImageURI(data.data)
            this.foodData.image = data.data.toString()
        }
    }


    fun dataToUpdate(foodData: FoodData, pos: Int) {
        this.foodData = foodData
        this.pos = pos
    }
}