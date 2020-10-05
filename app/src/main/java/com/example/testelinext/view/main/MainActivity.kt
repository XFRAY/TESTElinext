package com.example.testelinext.view.main

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testelinext.R
import com.example.testelinext.view.main.custom.GridPagerSnapHelper
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModel()

    private lateinit var imageAdapter: ImagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initRecyclerView()
        observeData()
    }

    private fun initRecyclerView() {
        imageAdapter = ImagesAdapter()
        val gridLayoutManager =
            object : GridLayoutManager(this, MainViewModel.DEFAULT_SPAN_COUNT, HORIZONTAL, false) {
                override fun checkLayoutParams(lp: RecyclerView.LayoutParams): Boolean {
                    lp.width = width / MainViewModel.DEFAULT_COLUMN_COUNT
                    return true
                }

            }
        with(rcclPhotos) {
            setHasFixedSize(true)
            layoutManager = gridLayoutManager
            GridPagerSnapHelper(
                MainViewModel.DEFAULT_SPAN_COUNT,
                MainViewModel.DEFAULT_COLUMN_COUNT
            ).attachToRecyclerView(this)
            adapter = imageAdapter
        }
    }

    private fun observeData() {
        mainViewModel.imageListSubject.subscribe {
            val imagesDiffResult = DiffUtil.calculateDiff(ImagesDiffUtils(imageAdapter.items, it))
            imageAdapter.setData(it)
            imagesDiffResult.dispatchUpdatesTo(imageAdapter)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_add_photo -> {
            mainViewModel.loadOneImage()
            true
        }
        R.id.action_update -> {
            mainViewModel.reloadPhotos()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}