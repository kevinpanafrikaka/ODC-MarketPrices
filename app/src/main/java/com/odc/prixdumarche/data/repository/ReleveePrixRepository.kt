package com.odc.prixdumarche.data.repository

import com.odc.prixdumarche.data.local.dao.ReleveePrixDao
import com.odc.prixdumarche.data.local.entity.ReleveePrix
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit

/**
 * Interface entre les ViewModels et la source des relevés de prix. Permet de
 * remplacer un jour Room par une vraie API sans toucher à l'UI.
 *
 * Ne calcule aucune tendance, min/max ou moyenne : ces résultats restent du
 * ressort du ViewModel (aucune logique métier dans la couche données).
 */
interface ReleveePrixRepository {
    fun observerDerniersPrixParProduit(): Flow<List<ReleveePrix>>
    fun observerTousLesReleves(): Flow<List<ReleveePrix>>
    fun observerHistorique30Jours(produitId: Long): Flow<List<ReleveePrix>>
    fun observerHistorique7Jours(produitId: Long): Flow<List<ReleveePrix>>
    fun observerDernierPrixParMarche(produitId: Long): Flow<List<ReleveePrix>>
    suspend fun ajouter(releve: ReleveePrix): Long
}

class ReleveePrixRepositoryImpl(
    private val releveePrixDao: ReleveePrixDao
) : ReleveePrixRepository {

    override fun observerDerniersPrixParProduit(): Flow<List<ReleveePrix>> =
        releveePrixDao.observerDerniersPrixParProduit()

    override fun observerTousLesReleves(): Flow<List<ReleveePrix>> =
        releveePrixDao.observerTousLesReleves()

    override fun observerHistorique30Jours(produitId: Long): Flow<List<ReleveePrix>> {
        val depuis = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(30)
        return releveePrixDao.observerHistorique(produitId, depuis)
    }

    override fun observerHistorique7Jours(produitId: Long): Flow<List<ReleveePrix>> {
        val depuis = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)
        return releveePrixDao.observerHistorique(produitId, depuis)
    }

    override fun observerDernierPrixParMarche(produitId: Long): Flow<List<ReleveePrix>> =
        releveePrixDao.observerDernierPrixParMarche(produitId)

    override suspend fun ajouter(releve: ReleveePrix): Long = releveePrixDao.insert(releve)
}
