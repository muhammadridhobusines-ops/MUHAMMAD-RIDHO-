package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteCarDao {
    @Query("SELECT carId FROM favorite_cars ORDER BY addedAt DESC")
    fun getAllFavoriteIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteCarEntity)

    @Query("DELETE FROM favorite_cars WHERE carId = :carId")
    suspend fun removeFavorite(carId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_cars WHERE carId = :carId)")
    fun isFavorite(carId: String): Flow<Boolean>
}
