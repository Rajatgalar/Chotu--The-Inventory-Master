package com.itechnowizard.chotu.utils

import android.widget.Filterable
import androidx.appcompat.widget.SearchView

object SearchViewUtil {

    fun setupSearchView(searchView: SearchView, adapter: Filterable) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
    }
}