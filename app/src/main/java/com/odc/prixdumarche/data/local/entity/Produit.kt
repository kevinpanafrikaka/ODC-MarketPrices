package com.odc.prixdumarche.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "produits")
data class Produit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nom: String,
    val unite: String,
    val categorie: String
)
