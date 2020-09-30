package com.example.testelinext.view.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testelinext.R
import com.example.testelinext.view.main.custom.GridPagerSnapHelper
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModel()

    private lateinit var adapter: PhotosAdapter

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initRecyclerView()
        initRecyclerAdapter()
        observeData()
    }

    private fun initRecyclerView() {
        val gridLayoutManager =
            object : GridLayoutManager(this, MainViewModel.DEFAULT_SPAN_COUNT, HORIZONTAL, false) {
                override fun checkLayoutParams(lp: RecyclerView.LayoutParams): Boolean {
                    lp.width = width / MainViewModel.DEFAULT_COLUMN_COUNT
                    return true
                }

            }
        with(rcclPhotos) {
            setHasFixedSize(true)
            stateListAnimator = null
            layoutManager = gridLayoutManager
            GridPagerSnapHelper(
                MainViewModel.DEFAULT_SPAN_COUNT,
                MainViewModel.DEFAULT_COLUMN_COUNT
            ).attachToRecyclerView(this)
        }
    }

    private fun initRecyclerAdapter() {
        adapter = PhotosAdapter()
        rcclPhotos.adapter = adapter
    }

    private fun observeData() {
        compositeDisposable.add(mainViewModel.pageListSubject.subscribe {
            adapter.updatePages(it)
        })

        compositeDisposable.add(mainViewModel.updateImageSubject.subscribe {
            adapter.updateItem(it)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_photo -> {
                mainViewModel.addNewPhoto()
                true
            }
            R.id.action_update -> {
                initRecyclerAdapter()
                mainViewModel.reloadPhotos()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}