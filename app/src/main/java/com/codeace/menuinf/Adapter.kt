package com.codeace.menuinf

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.PopupMenu
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_layout.view.*


class Adapter(
    private val clickListener: (Int, Pair<View, String>) -> Unit, private val deleteListener: (Int) -> Unit,
    private val updateListener: (Int) -> Unit
) : ListAdapter<FoodData, Adapter.ViewHolder>(DIFF_CALLBACK) {

    fun getDataAt(position: Int): FoodData {
        return getItem(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(inflater)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(getItem(position), clickListener, deleteListener, updateListener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(
            foodData: FoodData,
            clickListener: (Int, Pair<View, String>) -> Unit,
            deleteListener: (Int) -> Unit,
            updateListener: (Int) -> Unit
        ) {
            setImage(itemView.context, foodData.image, itemView.iFoodImage)
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
            itemView.setOnClickListener {
                clickListener(adapterPosition, Pair.create(itemView.iFoodImage, "FoodImage"))
            }
        }
    }

    companion object {

        fun setImage(context: Context, url: String, imageView: ImageView) {
            Glide.with(context).load(url).centerCrop()
                .placeholder(R.drawable.imageplaceholder).into(imageView)
        }

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FoodData>() {
            override fun areItemsTheSame(oldItem: FoodData, newItem: FoodData): Boolean {
                return oldItem.id === newItem.id
            }

            override fun areContentsTheSame(oldItem: FoodData, newItem: FoodData): Boolean {
                return oldItem.name == newItem.name &&
                        oldItem.category == newItem.category &&
                        oldItem.spiciness == newItem.spiciness &&
                        oldItem.price == newItem.price
            }
        }
    }
}