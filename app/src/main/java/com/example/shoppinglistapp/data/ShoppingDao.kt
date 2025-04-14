package com.example.shoppinglistapp.data

import androidx.room.*

@Dao
interface ShoppingDao {

    @Query("SELECT * FROM shopping_items ORDER BY isBought ASC, name ASC")
    suspend fun getAllItems(): List<ShoppingItem>

    @Insert
    suspend fun insertItem(item: ShoppingItem)

    @Update
    suspend fun updateItem(item: ShoppingItem)

    @Delete
    suspend fun deleteItem(item: ShoppingItem)
}