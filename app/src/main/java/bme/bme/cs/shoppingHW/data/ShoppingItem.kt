package bme.bme.cs.shoppingHW.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "shopping")
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true) var shoppingID : Long?,
    @ColumnInfo(name = "shoppingName") var shoppingName: String,
    @ColumnInfo(name = "shoppingPrice")var shoppingPrice: String,
    @ColumnInfo(name = "shoppingDescription")var shoppingDescription: String,
    @ColumnInfo(name = "isPurchased") var isPurchased: Boolean,
    @ColumnInfo(name = "category") var category: Int

) : Serializable

