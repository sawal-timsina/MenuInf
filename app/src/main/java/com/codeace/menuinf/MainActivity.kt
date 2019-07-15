package com.codeace.menuinf

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var foodAdapter = Adapter({ pos: Int -> onItemClicked(pos) },
        { pos: Int -> onDeleteClicked(pos) },
        { pos: Int -> onUpdateClicked(pos) })

    private fun onDeleteClicked(pos: Int) {
        foodAdapter.removeData(pos)
    }

    private fun onUpdateClicked(pos: Int) {
        val dialog = FoodDialog()
        dialog.show(supportFragmentManager, "FoodDialog")
//        foodAdapter.notifyItemChanged(pos)
        /*val dialog = Dialog(ctx)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_layout)
        dialog.editItemName.setText(name)
        dialog.editItemCategory.setText(category)
        dialog.editItemSpiciness.setText(spiciness)
        dialog.editItemPrice.setText(price.toString())
        val buttonOk = dialog.findViewById<Button>(R.id.dialogYes)
        val buttonCancel = dialog.findViewById<Button>(R.id.dialogNo)
        buttonOk.setOnClickListener {
            when {
                dialog.editItemName.length() == 0 -> dialog.editItemName.error = "Field empty!"
                dialog.editItemCategory.length() == 0 -> dialog.editItemCategory.error = "Field empty!"
                dialog.editItemSpiciness.length() == 0 -> dialog.editItemSpiciness.error = "Field empty!"
                dialog.editItemPrice.length() == 0 -> dialog.editItemPrice.error = "Field empty!"
                else -> {
                    if (pos >= 0) { recyclerListItem.removeAt(pos) }
                    quickInsertion(
                        recyclerListItem, Data(
                            R.drawable.ic_launcher_background,
                            dialog.editItemName.text.toString(),
                            dialog.editItemCategory.text.toString(),
                            dialog.editItemSpiciness.text.toString(),
                            dialog.editItemPrice.text.toString().toDouble()
                        )
                    ).also { recyclerAdapter.notifyDataSetChanged() }
                    dialog.dismiss()
                }
            }
        }
        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()*/
    }

    private fun onItemClicked(pos: Int) {
        startActivity(Intent(this@MainActivity, ItemDetails::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
//        window.enterTransition = Explode()
//        window.exitTransition = Explode()
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        foodAdapter.setFoodArray(Adapter.foodArray_)
        itemRecyclerView.layoutManager = LinearLayoutManager(this)
        itemRecyclerView.adapter = foodAdapter

        floatingActionButton.setOnClickListener {
            foodAdapter.addData(R.drawable.ic_launcher_background, "hello ".plus((0..3).random()), "adfa", "sad", 0.0)
            //Utils.foodDialog(this,"","","",0.0,-1)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchItem = menu.findItem(R.id.action_search)
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                foodAdapter.setFoodArray(Adapter.foodArray_)
                return true
            }
        })
        val searchView = searchItem.actionView as SearchView
        searchView.isSubmitButtonEnabled = true
        searchView.queryHint = getString(R.string.action_search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            @SuppressLint("DefaultLocale")
            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isNotEmpty()) {
                    val listItems =
                        Adapter.foodArray_.filter { s -> s.name.toLowerCase().contains(newText.toLowerCase()) }
                    foodAdapter.setFoodArray((listItems as ArrayList<Data>))
                } else {
                    foodAdapter.setFoodArray(Adapter.foodArray_)
                }
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }
        })
        return true
    }

}
