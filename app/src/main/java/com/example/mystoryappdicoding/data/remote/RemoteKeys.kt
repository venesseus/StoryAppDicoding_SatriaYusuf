package com.example.mystoryappdicoding.data.remote

import androidx.room.Entity
import androidx.room.PrimaryKey

//Prevkey
@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey
    val id: String,
    val prevKey: Int?,
    val nextKey: Int?
)