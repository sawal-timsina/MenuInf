package com.codeace.menuinf

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
import com.codeace.menuinf.adapters.FoodAdapter
import com.codeace.menuinf.dataHolders.FoodViewModel
import com.codeace.menuinf.foodData.FoodData
import com.codeace.menuinf.ui.FoodDialog
import com.codeace.menuinf.ui.FoodItemDetails
import com.innovattic.rangeseekbar.RangeSeekBar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_layout.*
import kotlinx.android.synthetic.main.nav_layout.*
import java.io.Serializable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class MainActivity : AppCompatActivity(), FoodDialog.FoodDialogListener, RangeSeekBar.SeekBarChangeListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var foodVM: FoodViewModel
    private var foodAdapter = FoodAdapter(
        { pos: Int, image: Pair<View, String> -> onItemClicked(pos, image) },
        { pos: Int -> onDeleteClicked(pos) },
        { pos: Int -> onUpdateClicked(pos) })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.enterTransition = Explode()
        window.exitTransition = Explode()

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        mAuth = FirebaseAuth.getInstance()

        itemRecyclerView.layoutManager = LinearLayoutManager(this)
        itemRecyclerView.adapter = foodAdapter
        itemRecyclerView.setHasFixedSize(true)

        categoryList.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        categoryList.divider = null

        rangeSeekBar.seekBarChangeListener = this

        foodVM = ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return FoodViewModel(application) as T
            }
        }).get(FoodViewModel::class.java)

        foodVM.allFoodData.observe(this, Observer { foodArray ->
            foodAdapter.submitList(foodArray as MutableList<FoodData>)
            foodVM.getCategories().also {
                categoryList.adapter =
                    ArrayAdapter(this@MainActivity, R.layout.nav_header, foodVM.categoryListItems.toList())
                rangeSeekBar.max = foodVM._maxPrice.toInt()
                minText.text = rangeSeekBar.getMinThumbValue().toString()
                maxText.text = rangeSeekBar.getMaxThumbValue().toString()
            }
        })

        floatingActionButton.setOnClickListener {
            val dialog = FoodDialog()
            dialog.isCancelable = false
            dialog.show(supportFragmentManager, "FoodDialogAdd")
        }

        floatingActionButton.setOnLongClickListener {
            foodVM.deleteAll()
            true
        }

        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.drawer_open, R.string.drawer_close)

        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        categoryList.setOnItemClickListener { _, view, position, _ ->
            if (foodVM.selectedCategories.contains(position)) foodVM.selectedCategories.remove(position)
            else foodVM.selectedCategories.add(position)

            view.background = resources.getDrawable(if (foodVM.selectedCategories.contains(position)) R.drawable.selected else R.drawable.dselected,null)

            foodVM.setFoodDataList(foodVM.filterByData(rangeSeekBar.getMinThumbValue().toDouble(), rangeSeekBar.getMaxThumbValue().toDouble()))

            drawer_layout.closeDrawer(GravityCompat.START)
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
//        updateUI(currentUser)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
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
                foodVM.setFoodDataList(foodVM.filterByData(rangeSeekBar.getMinThumbValue().toDouble(), rangeSeekBar.getMaxThumbValue().toDouble()))
                return true
            }
        })
        val searchView = searchItem.actionView as SearchView
        searchView.isSubmitButtonEnabled = true
        searchView.queryHint = getString(R.string.action_search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isNotEmpty()) {
                    val listItems = foodVM.allFoodData.value!!.filter { s ->
                        s.name.contains(newText,true)
                    }
                    foodVM.setFoodDataList(listItems)
                } else {
                    foodVM.setDefaults()
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
            foodVM.insert(foodData)
        } else {
            foodVM.update(foodData)
        }
    }

    override fun onStartedSeeking() {}
    override fun onStoppedSeeking() {}

    override fun onValueChanged(minThumbValue: Int, maxThumbValue: Int) {
        minText.text = "$minThumbValue"
        maxText.text = "$maxThumbValue"
        foodAdapter.submitList(foodVM.filterByData(minThumbValue.toDouble(), maxThumbValue.toDouble()))
    }

    private fun onDeleteClicked(pos: Int) {
        foodVM.delete(foodAdapter.getDataAt(pos))
    }

    private fun onUpdateClicked(pos: Int) {
        val dialog = FoodDialog()
        dialog.isCancelable = false
        dialog.dataToUpdate(foodAdapter.getDataAt(pos))
        dialog.show(supportFragmentManager, foodAdapter.getDataAt(pos).id.toString())
    }

    private fun onItemClicked(pos: Int, image: Pair<View, String>) {
        val intent = Intent(this, FoodItemDetails::class.java)
        val option = ActivityOptionsCompat.makeSceneTransitionAnimation(this, image)
        intent.putExtra("extra_object", foodAdapter.getDataAt(pos) as Serializable)
        startActivity(intent, option.toBundle())
    }
}
