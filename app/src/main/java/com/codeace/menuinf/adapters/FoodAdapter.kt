package com.codeace.menuinf.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codeace.menuinf.R
import com.codeace.menuinf.foodData.FoodData
import com.codeace.menuinf.helpers.setImage
import kotlinx.android.synthetic.main.item_layout.view.*


class FoodAdapter(
    private val clickListener: (Int, Pair<View, String>) -> Unit, private val deleteListener: (Int) -> Unit,
    private val updateListener: (Int) -> Unit
) : ListAdapter<FoodData, FoodAdapter.ViewHolder>(
    DIFF_CALLBACK
) {
    var visibility = false

    fun getDataAt(position: Int): FoodData {
        return getItem(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(inflater)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(getItem(position), visibility, clickListener, deleteListener, updateListener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(foodData: FoodData,visible : Boolean, clickListener: (Int, Pair<View, String>) -> Unit,
            deleteListener: (Int) -> Unit, updateListener: (Int) -> Unit
        ) {
            setImage(itemView.context, foodData.food_image, itemView.iFoodImage)
            itemView.iFoodName.text =
                foodData.food_name.plus("\n" + foodData.food_price.toString().plus(" Rs"))
            itemView.setOnClickListener {
                clickListener(adapterPosition, Pair.create(itemView.iFoodImage, "FoodImage"))
            }
            if(visible){
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
                itemView.optionButton.setOnClickListener {
                    popupMenu.show()
                }
                itemView.setOnLongClickListener {
                    popupMenu.show()
                    true
                }
            } else { itemView.optionButton.visibility = View.GONE }
        }
    }

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FoodData>() {
            override fun areItemsTheSame(oldItem: FoodData, newItem: FoodData): Boolean {
                return oldItem.id === newItem.id
            }

            override fun areContentsTheSame(oldItem: FoodData, newItem: FoodData): Boolean {
                return oldItem.food_name == newItem.food_name &&
                        oldItem.food_category == newItem.food_category &&
                        oldItem.food_spiciness == newItem.food_spiciness &&
                        oldItem.food_price == newItem.food_price
            }
        }
    }
}