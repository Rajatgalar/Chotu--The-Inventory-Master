package com.itechnowizard.chotu.presentation.expiry

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.itechnowizard.chotu.databinding.ActivityExpiryBinding
import com.itechnowizard.chotu.domain.model.ExpiryModel
import com.itechnowizard.chotu.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Expiry : AppCompatActivity() {
    private lateinit var binding : ActivityExpiryBinding
    private val viewModel: ExpiryViewModel by viewModels()
    private lateinit var adapter: ExpiryAdapter
    private var listOfProduct : List<ExpiryModel> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpiryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ExpiryAdapter()
        binding.apply {

            recyclerView.adapter = adapter
            setSupportActionBar(toolbarLayout.toolbar)
            ToolbarUtils.setToolbar(
                toolbarLayout,
                true,
                Constants.TOOLBAR_PRODUCT_LIST,
                Constants.TOOLBAR_NO_MENU_TEXT
            )

            toolbarLayout.toolbarBack.setOnClickListener { finish() }

            SearchViewUtil.setupSearchView(searchFilter,adapter)
        }
    }

    override fun onStart() {
        super.onStart()

        viewModel.loadProductList()

        viewModel.productListState.observe(this) { state ->
            if (state.isLoading) {
                ProgressBarUtil.showProgressBar(binding.progressBarLayout)
            } else if (state.error.isNotBlank()) {
                ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                Toast.makeText(this, "Error : ${state.error}", Toast.LENGTH_SHORT).show()
            } else if (state.list != null) {
                ProgressBarUtil.hideProgressBar(binding.progressBarLayout)

                if(state.list.isNotEmpty()) {
                    listOfProduct = state.list
                    setupRecyclerView(listOfProduct)
                }
            }
        }

    }

    private fun setVisiblityOfRecyclerView(recyclerview: Boolean) {
        if (recyclerview) {
            binding.tvNoData.visibility = View.INVISIBLE
            binding.recyclerView.visibility = View.VISIBLE
        } else {
            binding.tvNoData.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.INVISIBLE
        }
    }

    private fun setupRecyclerView(list: List<ExpiryModel>) {
        if (list.isEmpty()) {
            setVisiblityOfRecyclerView(false)
        } else {
            setVisiblityOfRecyclerView(true)
            adapter.setProductsList(list)
        }
    }

}