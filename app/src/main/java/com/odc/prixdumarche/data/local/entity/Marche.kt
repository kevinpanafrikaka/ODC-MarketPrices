package com.odc.prixdumarche.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "marches")
data class Marche(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nom: String,
    val commune: String
)
