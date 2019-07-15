package com.codeace.menuinf

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), FoodDialog.FoodDialogListener {

    private var foodAdapter = Adapter({ pos: Int -> onItemClicked(pos) },
        { pos: Int -> onDeleteClicked(pos) },
        { pos: Int -> onUpdateClicked(pos) })

    private fun onDeleteClicked(pos: Int) {
        foodAdapter.removeData(pos)
    }

    private fun onUpdateClicked(pos: Int) {
        val dialog = FoodDialog()
        dialog.show(supportFragmentManager, "FoodDialog")
        dialog.dataToUpdate(
            foodAdapter.getData(pos).name, foodAdapter.getData(pos).category,
            foodAdapter.getData(pos).spiciness, foodAdapter.getData(pos).price
        )
    }

    private fun onItemClicked(pos: Int) {
        startActivity(Intent(this@MainActivity, ItemDetails::class.java))
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {

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
