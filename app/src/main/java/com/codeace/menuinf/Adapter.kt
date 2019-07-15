package com.codeace.menuinf

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_layout.view.*

// Adapter Class

class Adapter(
    private val clickListener: (Data) -> Unit,
    private val deleteListener: (Int) -> Unit,
    private val updateListener: (Data) -> Unit
) : RecyclerView.Adapter<Adapter.ViewHolder>() {

    companion object {
        val foodArray_ = ArrayList<Data>()
    }

    private var foodArray: ArrayList<Data>? = null

    fun setFoodArray(arrayList: ArrayList<Data>) {
        foodArray = arrayList
        notifyDataSetChanged()
    }

    fun getFoodArray(): ArrayList<Data> {
        return foodArray!!
    }

    fun removeData(position: Int) {
        foodArray_.removeAt(position)
        notifyItemRemoved(position)
    }

    fun addData(image: Int, name: String, category: String, spiciness: String, price: Double) {
        val data = Data(image, name, category, spiciness, price)
        var i = itemCount
        do {
            i--
            if (i < 0) {
                foodArray_.add(0, data)
                break
            } else if (data.name >= foodArray_[i].name) {
                foodArray_.add(i + 1, data)
                break
            }
        } while (data.name < foodArray_[i].name)
        notifyItemInserted(i + 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return foodArray?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        foodArray?.get(position)?.let { holder.bindItems(it, clickListener, deleteListener, updateListener) }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(
            data: Data,
            clickListener: (Data) -> Unit,
            deleteListener: (Int) -> Unit,
            updateListener: (Data) -> Unit
        ) {
            itemView.iFoodImage.setImageResource(data.image)
            itemView.iFoodName.text = data.name
            itemView.iFoodPrice.text = data.price.toString().plus(" Rs")
            itemView.optionButton.setOnClickListener {
                val popupMenu = PopupMenu(itemView.context, itemView.optionButton)
                popupMenu.menuInflater.inflate(R.menu.recycler_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_delete -> {
                            deleteListener(adapterPosition)
                        }
                        R.id.action_update -> {
                            updateListener(data)
                        }
                    }
                    true
                }
                popupMenu.show()
            }
            itemView.setOnClickListener { clickListener(data) }
        }
    }
}