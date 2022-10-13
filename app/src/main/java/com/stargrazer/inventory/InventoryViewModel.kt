package com.stargrazer.inventory

import androidx.lifecycle.*
import com.stargrazer.inventory.data.Item
import com.stargrazer.inventory.data.ItemDao
import kotlinx.coroutines.launch

class InventoryViewModel(private val itemDao: ItemDao) : ViewModel() {
    val allItems: LiveData<List<Item>> = itemDao.getItems().asLiveData()

    private fun insertItem(item: Item) {
        viewModelScope.launch {
            itemDao.insert(item)
        }
    }
    //Updates existing Item in database
    fun updateItem(
        itemId: Int,
        itemName: String,
        itemPrice: String,
        itemCount: String
    ) {
        val updatedItem = getUpdatedItemEntry(itemId, itemName, itemPrice, itemCount)
        updateItem(updatedItem)
    }
    //Launches coroutine to update item
    private fun updateItem(item: Item) {
        viewModelScope.launch {
            itemDao.update(item)
        }
    }
    fun deleteItem(item: Item) {
        viewModelScope.launch{
            itemDao.delete(item)
        }
    }
    fun sellItem(item: Item) {
        if(item.quantity > 0) {
            val newItem = item.copy(quantity = item.quantity - 1)
            updateItem(newItem)
        }
    }
    private fun getNewItemEntry(itemName:String, itemPrice: String, itemCount: String) : Item {
        return Item(
            name = itemName,
            price = itemPrice.toDouble(),
            quantity = itemCount.toInt()
        )
    }
    private fun getUpdatedItemEntry(
        itemId: Int,
        itemName: String,
        itemPrice: String,
        itemCount: String
    ) : Item {
        return Item(
            id = itemId,
            name = itemName,
            price = itemPrice.toDouble(),
            quantity = itemCount.toInt()
        )
    }
    fun addNewItem(itemName: String, itemPrice: String, itemCount: String) {
        val newItem = getNewItemEntry(itemName, itemPrice, itemCount)
        insertItem(newItem)
    }
    fun isEntryValid(itemName: String, itemPrice: String, itemCount: String): Boolean {
        return !(itemName.isBlank()||itemPrice.isBlank()||itemCount.isBlank())
    }
    fun isStockAvailable(item: Item) : Boolean {
        return (item.quantity>0)
    }
    fun retrieveItem(id: Int) : LiveData<Item> {
        return itemDao.getItem(id).asLiveData()
    }
}

class InventoryViewModelFactory(private val itemDao: ItemDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InventoryViewModel(itemDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}