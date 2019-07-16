package com.codeace.menuinf

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), FoodDialog.FoodDialogListener {


    private lateinit var foodViewModel: FoodViewModel
    private var foodAdapter = Adapter({ pos: Int -> onItemClicked(pos) },
        { pos: Int -> onDeleteClicked(pos) },
        { pos: Int -> onUpdateClicked(pos) })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        itemRecyclerView.layoutManager = LinearLayoutManager(this)
        itemRecyclerView.adapter = foodAdapter

        foodViewModel = ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return FoodViewModel(application) as T
            }
        }).get(FoodViewModel::class.java)


        foodViewModel.allFoodData.observe(
            this,
            Observer<MutableList<FoodData>> { foodArray ->
                foodAdapter.setFoodArray(foodArray)
            })

        floatingActionButton.setOnClickListener {
            val dialog = FoodDialog()
            dialog.show(supportFragmentManager, "FoodDialogAdd")
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
//                    val listItems = Adapter.foodArray_.filter { s -> s.name.toLowerCase().contains(newText.toLowerCase()) }
//                    foodAdapter.setFoodArray((listItems as ArrayList<FoodData>))
                } else {
//                    foodAdapter.setFoodArray(Adapter.foodArray_)
                }
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }
        })
        return true
    }

    private fun onDeleteClicked(pos: Int) {
        foodAdapter.removeData(pos)
    }

    private fun onUpdateClicked(pos: Int) {
        val dialog = FoodDialog()
        dialog.show(supportFragmentManager, "FoodDialogUpdate")
        dialog.dataToUpdate(foodAdapter.getData(pos), pos)
    }

    private fun onItemClicked(pos: Int) {
        startActivity(Intent(this@MainActivity, ItemDetails::class.java))
    }

    override fun onDialogPositiveClick(dialog: DialogFragment, foodData: FoodData, pos: Int) {
        if (pos == -1) {
            foodViewModel.insert(foodData)
//            foodAdapter.addData(foodData)
        } else {
            foodAdapter.updateData(foodData, pos)
        }
    }
}
