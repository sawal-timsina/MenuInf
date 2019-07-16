package com.codeace.menuinf

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_layout.view.*

class Adapter(
    private val clickListener: (Int) -> Unit,
    private val deleteListener: (Int) -> Unit,
    private val updateListener: (Int) -> Unit
) : RecyclerView.Adapter<Adapter.ViewHolder>() {

    private var foodArray: ArrayList<FoodData>? = null

    fun setFoodArray(arrayList: ArrayList<FoodData>) {
        foodArray = arrayList
        notifyDataSetChanged()
    }

    fun getFoodArray(): ArrayList<FoodData> {
        return foodArray!!
    }

    fun getData(position: Int): FoodData {
        return foodArray!![position]
    }

    fun addData(foodData: FoodData) {
        var i = itemCount
        do {
            i--
            if (i < 0) {
                foodArray!!.add(0, foodData)
                break
            } else if (foodData.name >= foodArray!![i].name) {
                foodArray!!.add(i + 1, foodData)
                break
            }
        } while (foodData.name < foodArray!![i].name)
        notifyItemInserted(i + 1)
    }

    fun removeData(position: Int) {
        foodArray!!.removeAt(position)
        notifyItemRemoved(position)
    }

    fun updateData(foodData: FoodData, position: Int) {
        foodArray!![position] = foodData
        notifyItemChanged(position)
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
            foodData: FoodData,
            clickListener: (Int) -> Unit,
            deleteListener: (Int) -> Unit,
            updateListener: (Int) -> Unit
        ) {
            Glide.with(itemView).load(foodData.image).centerCrop()
                .placeholder(R.drawable.imageplaceholder).into(itemView.iFoodImage)
            itemView.iFoodName.text = foodData.name
            itemView.iFoodPrice.text = foodData.price.toString().plus(" Rs")
            itemView.optionButton.setOnClickListener {
                val popupMenu = PopupMenu(itemView.context, itemView.optionButton)
                popupMenu.menuInflater.inflate(R.menu.recycler_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_delete -> {
                            deleteListener(adapterPosition)
                        }
                        R.id.action_update -> {
                            updateListener(adapterPosition)
                        }
                    }
                    true
                }
                popupMenu.show()
            }
            itemView.setOnClickListener { clickListener(adapterPosition) }
        }
    }
}