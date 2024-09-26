package com.itechnowizard.chotu.domain.model

data class ProductInventory(
    val product: ProductModel,
    val inventory: List<InventoryModel>
)
