package com.codeace.menuinf

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.transition.Explode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.GravityCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import java.io.Serializable

class MainActivity : AppCompatActivity(), FoodDialog.FoodDialogListener {

    private lateinit var foodViewModel: FoodViewModel
    private var foodAdapter = FoodAdapter(
        { pos: Int, image: Pair<View, String> -> onItemClicked(pos, image) },
        { pos: Int -> onDeleteClicked(pos) },
        { pos: Int -> onUpdateClicked(pos) })

    private var categoryListItems = mutableSetOf("Select Category")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.enterTransition = Explode()
        window.exitTransition = Explode()

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        itemRecyclerView.layoutManager = LinearLayoutManager(this)
        itemRecyclerView.adapter = foodAdapter

        categoryList.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        categoryList.divider = null

        foodViewModel = ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return FoodViewModel(application) as T
            }
        }).get(FoodViewModel::class.java)

        foodViewModel.allFoodData.observe(this, Observer { foodArray ->
            foodAdapter.submitList(foodArray as MutableList<FoodData>)
            getCategories().also {
                categoryList.adapter =
                    ArrayAdapter(this@MainActivity, R.layout.nav_header, categoryListItems.toList())
            }
        })

        floatingActionButton.setOnClickListener {
            val dialog = FoodDialog()
            dialog.show(supportFragmentManager, "FoodDialogAdd")
        }

        floatingActionButton.setOnLongClickListener {
            foodViewModel.deleteAll()
            true
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.drawer_open, R.string.drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        categoryList.setOnItemClickListener { parent, view, position, id ->
            if (position != 0) {
                val listItems = foodViewModel.allFoodData.value!!.filter { s ->
                    s.category == categoryListItems.toList()[position]
                }
                foodAdapter.submitList(listItems)
            } else {
                foodAdapter.submitList(foodViewModel.allFoodData.value!!)
            }

            drawer_layout.closeDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
            super.onBackPressed()
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
                    val listItems = foodViewModel.allFoodData.value!!.filter { s ->
                        s.name.toLowerCase().contains(newText.toLowerCase())
                    }
                    foodAdapter.submitList(listItems)
                } else {
                    foodAdapter.submitList(foodViewModel.allFoodData.value!!)
                }
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
        })
        return true
    }

    override fun onDialogPositiveClick(dialog: DialogFragment, foodData: FoodData) {
        if (dialog.tag.equals(getString(R.string.fda))) {
            foodViewModel.insert(foodData)
        } else {
            foodViewModel.update(foodData)
        }
    }

    private fun onDeleteClicked(pos: Int) {
        foodViewModel.delete(foodViewModel.allFoodData.value!![pos])
    }

    private fun onUpdateClicked(pos: Int) {
        val dialog = FoodDialog()
        dialog.dataToUpdate(foodAdapter.getDataAt(pos))
        dialog.show(supportFragmentManager, foodAdapter.getDataAt(pos).id.toString())
    }

    private fun onItemClicked(pos: Int, image: Pair<View, String>) {
        val intent = Intent(this, FoodItemDetails::class.java)
        val option = ActivityOptionsCompat.makeSceneTransitionAnimation(this, image)
        intent.putExtra("extra_object", foodAdapter.getDataAt(pos) as Serializable)
        startActivity(intent, option.toBundle())
    }

    private fun getCategories() {
        foodViewModel.allFoodData.value!!.forEach {
            categoryListItems.add(it.category)
        }
    }
}
