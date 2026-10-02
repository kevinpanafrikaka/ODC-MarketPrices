package com.odc.prixdumarche.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.odc.prixdumarche.data.local.entity.Produit
import kotlinx.coroutines.flow.Flow

@Dao
interface ProduitDao {
    @Insert
    suspend fun insert(produit: Produit): Long

    @Insert
    suspend fun insertAll(produits: List<Produit>)

    @Query("SELECT * FROM produits ORDER BY nom ASC")
    fun observerTous(): Flow<List<Produit>>

    @Query("SELECT * FROM produits WHERE id = :produitId")
    fun observerParId(produitId: Long): Flow<Produit?>
}
