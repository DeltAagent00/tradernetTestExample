package com.example.tradernet.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tradernet.databinding.ActivityMainBinding
import com.example.tradernet.di.Injector
import com.example.tradernet.ui.adapter.QuoteListAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel by lazy {
        val _viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        Injector.getInstance().appComponent().inject(_viewModel)
        _viewModel
    }

    private var adapter: QuoteListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initActionBar()
        initView()
        initRecyclerView()
        onFirstViewAttach()
    }

    private fun initActionBar() {
        setSupportActionBar(binding.toolBarView)
    }

    private fun initView() {
    }

    private fun initRecyclerView() {
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = QuoteListAdapter()
        recyclerView.adapter = adapter
    }

    private fun onFirstViewAttach() {
        viewModel.quotes.observe(this) {
            adapter?.quotesList = it
        }

        viewModel.getUseListTicker()
    }

    override fun onStart() {
        super.onStart()

        viewModel.startSocket()
    }

    override fun onPause() {
        super.onPause()

        viewModel.onCloseSocket()
    }
}