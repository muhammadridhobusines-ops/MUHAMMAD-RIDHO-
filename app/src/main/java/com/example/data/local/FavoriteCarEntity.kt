package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_cars")
data class FavoriteCarEntity(
    @PrimaryKey val carId: String,
    val addedAt: Long = System.currentTimeMillis()
)
