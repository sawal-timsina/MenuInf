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
import com.codeace.menuinf.FoodAdapter.Companion.setImage

class FoodDialog : DialogFragment() {
    private val imagePickCode = 1000
    private var foodData: FoodData? = null
    private lateinit var listener: FoodDialogListener
    private lateinit var foodImage: ImageView

    interface FoodDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment, foodData: FoodData)
    }

    fun dataToUpdate(foodData: FoodData) {
        this.foodData = foodData
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
        val editItemName = dialogView.findViewById<EditText>(R.id.itemName_)
        val editItemCategory = dialogView.findViewById<EditText>(R.id.itemCategory_)
        val editItemSpiciness = dialogView.findViewById<EditText>(R.id.itemSpiciness_)
        val editItemPrice = dialogView.findViewById<EditText>(R.id.itemPrice_)
        if (tag.equals(getString(R.string.fda))) {
            foodData = FoodData(null, "", "", "", "", 0.0)
        } else {
            if (foodData == null) foodData = FoodData(tag!!.toInt(), "", "", "", "", 0.0)
            if (foodData != null) {
                setImage(activity!!, foodData!!.image, foodImage)
                editItemName.text = getEditableText(foodData!!.name)
                editItemCategory.text = getEditableText(foodData!!.category)
                editItemSpiciness.text = getEditableText(foodData!!.spiciness)
                editItemPrice.text = getEditableText(foodData!!.price.toString())
            }
        }

        dialogView.findViewById<ImageView>(R.id.foodImage).setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, imagePickCode)
        }

        builder.setView(dialogView)
        builder.setPositiveButton(R.string.ok) { _, _ ->
            foodData!!.name = editItemName.text.toString()
            foodData!!.category = editItemCategory.text.toString()
            foodData!!.spiciness = editItemSpiciness.text.toString()
            foodData!!.price = editItemPrice.text.toString().toDouble()
            listener.onDialogPositiveClick(this, foodData!!)
        }
        builder.setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }
        return builder.create()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == imagePickCode) {
            foodData!!.image = data?.data.toString()
            setImage(activity!!, foodData!!.image, foodImage)
        }
    }

    private fun getEditableText(text: String): Editable {
        return Editable.Factory.getInstance().newEditable(text)
    }
}