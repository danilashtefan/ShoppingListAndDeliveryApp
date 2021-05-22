package bme.bme.cs.shoppingHW.data

import androidx.room.*


@Dao
interface ShoopingDAO {
    @Query("SELECT * FROM shopping")
    fun getAllShoppings(): List<ShoppingItem>

    @Insert
    fun insertShopping(shoppingItem: ShoppingItem) : Long

    @Delete
    fun deleteTodo(shoppingItem: ShoppingItem)

    @Update
    fun updateShopping(shoppingItem: ShoppingItem)

    @Query("DELETE FROM shopping")
    fun nukeTable()

    }
