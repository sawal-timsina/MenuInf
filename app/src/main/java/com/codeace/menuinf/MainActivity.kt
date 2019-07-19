package com.codeace.menuinf

import android.content.Intent
import android.os.Bundle
import android.transition.Explode
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
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
        window.enterTransition = Explode()
        window.exitTransition = Explode()
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
            Observer { foodArray ->
                foodAdapter.submitList(foodArray as MutableList<FoodData>)
            })

        floatingActionButton.setOnClickListener {
            val dialog = FoodDialog()
            dialog.show(supportFragmentManager, "FoodDialogAdd")
        }

        floatingActionButton.setOnLongClickListener {
            foodViewModel.deleteAll()
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchItem = menu.findItem(R.id.action_search)
        searchItem.setOnMenuItemClickListener {

            true
        }
        /*searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                return false
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                return false
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
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
        })*/
        return true
    }

    private fun onDeleteClicked(pos: Int) {
        foodViewModel.delete(foodViewModel.allFoodData.value!![pos])
    }

    private fun onUpdateClicked(pos: Int) {
        val dialog = FoodDialog()
        dialog.dataToUpdate(foodAdapter.getDataAt(pos))
        dialog.show(supportFragmentManager, foodViewModel.allFoodData.value!![pos].id.toString())
    }

    private fun onItemClicked(pos: Int) {
        val option = ActivityOptionsCompat.makeSceneTransitionAnimation(this, Adapter.imageView)
        startActivity(Intent(this, ItemDetails::class.java), option.toBundle())
    }

    override fun onDialogPositiveClick(dialog: DialogFragment, foodData: FoodData) {
        if (dialog.tag.equals(getString(R.string.fda))) {
            foodViewModel.insert(foodData)
        } else {
            foodViewModel.update(foodData)
            foodAdapter.notifyItemChanged(foodViewModel.allFoodData.value!!.indexOf(foodData))
        }
    }
}
