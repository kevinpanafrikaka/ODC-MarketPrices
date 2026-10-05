package com.odc.prixdumarche.data.repository

import com.odc.prixdumarche.data.local.dao.ProduitDao
import com.odc.prixdumarche.data.local.entity.Produit
import kotlinx.coroutines.flow.Flow

/**
 * Interface entre les ViewModels et la source des produits. Permet de
 * remplacer un jour Room par une vraie API sans toucher à l'UI.
 */
interface ProduitRepository {
    fun observerProduits(): Flow<List<Produit>>
    fun observerProduit(produitId: Long): Flow<Produit?>
    suspend fun ajouter(produit: Produit): Long
}

class ProduitRepositoryImpl(private val produitDao: ProduitDao) : ProduitRepository {
    override fun observerProduits(): Flow<List<Produit>> = produitDao.observerTous()

    override fun observerProduit(produitId: Long): Flow<Produit?> =
        produitDao.observerParId(produitId)

    override suspend fun ajouter(produit: Produit): Long = produitDao.insert(produit)
}


