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
    fun observerFavoris(): Flow<List<Produit>>
    suspend fun ajouter(produit: Produit): Long
    suspend fun basculerFavori(produitId: Long, favori: Boolean)
}

class ProduitRepositoryImpl(private val produitDao: ProduitDao) : ProduitRepository {
    override fun observerProduits(): Flow<List<Produit>> = produitDao.observerTous()

    override fun observerProduit(produitId: Long): Flow<Produit?> =
        produitDao.observerParId(produitId)

    override fun observerFavoris(): Flow<List<Produit>> = produitDao.observerFavoris()

    override suspend fun ajouter(produit: Produit): Long = produitDao.insert(produit)

    override suspend fun basculerFavori(produitId: Long, favori: Boolean) =
        produitDao.definirFavori(produitId, favori)
}


