package com.example.testelinext.view.main

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testelinext.R
import com.example.testelinext.view.main.custom.SnapToBlock
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModel()

    private lateinit var adapter: PhotosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUI()
        observeData()
        for(a: Int in 0..140){
            mainViewModel.addPhoto()
        }
    }

    private fun initUI() {
        adapter = PhotosAdapter()
        val layoutManager =
            object : GridLayoutManager(this, MainViewModel.DEFAULT_SPAN_COUNT, HORIZONTAL, false) {
                override fun checkLayoutParams(lp: RecyclerView.LayoutParams): Boolean {
                    lp.width = width / MainViewModel.DEFAULT_COLUMN_COUNT
                    return true
                }

            }
        rcclPhotos.setHasFixedSize(true)
        rcclPhotos.stateListAnimator = null
        rcclPhotos.layoutManager = layoutManager
        val snapHelper = SnapToBlock()
        snapHelper.attachToRecyclerView(rcclPhotos)
        rcclPhotos.adapter = adapter
    }

    private fun observeData() {
        mainViewModel.addPageData.observe(this, Observer {
            adapter.addItems(it.listOfItems)
        })

        mainViewModel.updateItemData.observe(this, Observer {
            adapter.updateItem(it)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_photo -> {
                mainViewModel.addPhoto()
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }
}