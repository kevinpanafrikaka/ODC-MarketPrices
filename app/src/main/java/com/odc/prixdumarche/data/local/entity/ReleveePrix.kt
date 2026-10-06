package com.odc.prixdumarche.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "relevees_prix",
    foreignKeys = [
        ForeignKey(
            entity = Produit::class,
            parentColumns = ["id"],
            childColumns = ["produitId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Marche::class,
            parentColumns = ["id"],
            childColumns = ["marcheId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("produitId"), Index("marcheId")]
)
data class ReleveePrix(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val produitId: Long,
    val marcheId: Long,
    val prixGnf: Long,
    val date: Long
)
