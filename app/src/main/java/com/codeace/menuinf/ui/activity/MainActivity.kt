package com.codeace.menuinf.ui.activity

import android.content.Intent
import android.os.Bundle
import android.transition.Explode
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.codeace.menuinf.R
import com.codeace.menuinf.adapters.FoodAdapter
import com.codeace.menuinf.dataHolders.FoodViewModel
import com.codeace.menuinf.foodData.FoodData
import com.codeace.menuinf.helpers.setImage
import com.codeace.menuinf.ui.fragments.FoodDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.innovattic.rangeseekbar.RangeSeekBar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_layout.*
import kotlinx.android.synthetic.main.nav_layout.*
import kotlinx.android.synthetic.main.toolbar.*
import java.io.Serializable

class MainActivity : AppCompatActivity(), FoodDialog.FoodDialogListener,
    RangeSeekBar.SeekBarChangeListener {

    private lateinit var mAuth: FirebaseAuth
    private var foodVM: FoodViewModel? = null
    private var foodAdapter = FoodAdapter(
        { pos: Int, image: Pair<View, String> -> onItemClicked(pos, image) },
        { pos: Int -> onDeleteClicked(pos) },
        { pos: Int -> onUpdateClicked(pos) })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Status", resources.getDimensionPixelSize(resources.getIdentifier("status_bar_height", "dimen", "android")).toString())
        window.enterTransition = Explode()
        window.exitTransition = Explode()

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        setImage(this, "", foodImageNav, R.drawable.background)
        mAuth = FirebaseAuth.getInstance()

        itemRecyclerView.layoutManager = LinearLayoutManager(this)
        itemRecyclerView.adapter = foodAdapter
        itemRecyclerView.setHasFixedSize(true)

        categoryList.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        categoryList.divider = null

        rangeSeekBar.seekBarChangeListener = this

        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        )

        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        categoryList.setOnItemClickListener { _, view, position, _ ->
            if (foodVM?.selectedCategories!!.contains(position)) foodVM?.selectedCategories?.remove(position)
            else foodVM?.selectedCategories!!.add(position)

            view.background = resources.getDrawable(
                if (foodVM?.selectedCategories!!.contains(position)) R.drawable.selected else R.drawable.dselected,
                null
            )

            foodVM?.setFoodDataList(foodVM?.filterByData(rangeSeekBar.getMinThumbValue().toDouble(), rangeSeekBar.getMaxThumbValue().toDouble())!!)
            drawer_layout.closeDrawer(GravityCompat.START)
        }

        logOut.setOnClickListener {
            drawer_layout.closeDrawer(GravityCompat.START)
            mAuth.signOut()
            drawer_layout.addDrawerListener(object : DrawerLayout.DrawerListener {
                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
                override fun onDrawerOpened(drawerView: View) {}
                override fun onDrawerClosed(drawerView: View) {
                    getUser()
                }
                override fun onDrawerStateChanged(newState: Int) {

                }
            })
        }

    }

    override fun onStart() {
        super.onStart()
        getUser()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            finish()
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
                foodVM?.setFoodDataList(
                    foodVM?.filterByData(
                        rangeSeekBar.getMinThumbValue().toDouble(),
                        rangeSeekBar.getMaxThumbValue().toDouble()
                    )!!
                )
                return true
            }
        })
        val searchView = searchItem.actionView as SearchView
        searchView.isSubmitButtonEnabled = true
        searchView.queryHint = getString(R.string.action_search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isNotEmpty()) {
                    val listItems = foodVM?.getAllFoodData()?.value!!.filter { s ->
                        s.food_name.contains(newText, true)
                    }
                    foodVM?.setFoodDataList(listItems)
                } else {
                    foodVM?.setDefaults()
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
            foodVM?.insert(foodData)
        } else {
            foodVM?.update(foodData)
        }
    }

    override fun onStartedSeeking() {}

    override fun onStoppedSeeking() {}

    override fun onValueChanged(minThumbValue: Int, maxThumbValue: Int) {
        minText.text = "$minThumbValue"
        maxText.text = "$maxThumbValue"
        foodVM?.setFoodDataList(
            foodVM?.filterByData(
                minThumbValue.toDouble(),
                maxThumbValue.toDouble()
            )!!
        )
    }

    private fun getUser() {
        foodAdapter.visibility = false
        floatingActionButton.isVisible = false
        avatar.isVisible = false
        setImage(this, "", avatar)
        mail.text = ""
        position.text = ""

        if (mAuth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            Toast.makeText(this, "Please Login/SignUp to use the app", Toast.LENGTH_LONG).show()
            finish()
        } else {
            updateUi(mAuth.currentUser)
        }
    }

    private fun initViewModel(){
        foodVM = ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return FoodViewModel(application) as T
            }
        }).get(FoodViewModel::class.java)

        foodVM?.getAllFoodData()!!.observe(this, Observer { foodArray ->
            foodAdapter.submitList(foodArray)
            foodVM?.getCategories().also {
                if(foodVM?.isChanged!!){
                    categoryList.adapter = ArrayAdapter(this@MainActivity,
                        R.layout.nav_header, foodVM?.categoryListItems!!.toList())

                    rangeSeekBar.max = foodVM?._maxPrice!!.toInt()

                    minText.text = rangeSeekBar.getMinThumbValue().toString()
                    maxText.text = rangeSeekBar.getMaxThumbValue().toString()
                    foodVM?.isChanged = false
                }
            }
        })
    }

    private fun updateUi(currentUser: FirebaseUser?) {
        if (currentUser?.uid == resources.getString(R.string.admin)) {
            foodAdapter.visibility = true
            floatingActionButton.isVisible = true
            floatingActionButton.setOnClickListener {
                val dialog = FoodDialog()
                dialog.show(supportFragmentManager, "FoodDialogAdd")
            }

            floatingActionButton.setOnLongClickListener {
                foodVM?.deleteAll()
                true
            }
        }
        avatar.isVisible = true
        setImage(this, currentUser?.photoUrl.toString(), avatar, R.drawable.ic_user)
        mail.text = currentUser?.displayName
        position.text = currentUser?.email
        initViewModel()
    }

    private fun onDeleteClicked(pos: Int) {
        foodVM?.delete(foodAdapter.getDataAt(pos))
        foodAdapter.notifyItemRemoved(pos)
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
}
