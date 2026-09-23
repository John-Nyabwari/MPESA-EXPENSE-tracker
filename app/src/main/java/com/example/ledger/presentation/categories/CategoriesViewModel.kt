package com.example.ledger.presentation.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ledger.data.local.dao.CategoryDao
import com.example.ledger.data.local.entities.CategoryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryDao: CategoryDao,
) : ViewModel() {

    val categories: StateFlow<List<CategoryEntity>> =
        categoryDao.observeAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addCategory(name: String, colorHex: String) {
        viewModelScope.launch {
            categoryDao.insert(CategoryEntity(name = name, colorHex = colorHex, isCustom = true))
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch { categoryDao.delete(category) }
    }
}
