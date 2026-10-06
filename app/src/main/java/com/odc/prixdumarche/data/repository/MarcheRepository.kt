package com.odc.prixdumarche.data.repository

import com.odc.prixdumarche.data.local.dao.MarcheDao
import com.odc.prixdumarche.data.local.entity.Marche
import kotlinx.coroutines.flow.Flow

/**
 * Interface entre les ViewModels et la source des marchés. Permet de
 * remplacer un jour Room par une vraie API sans toucher à l'UI.
 */
interface MarcheRepository {
    fun observerMarches(): Flow<List<Marche>>
    suspend fun ajouter(marche: Marche): Long
}

class MarcheRepositoryImpl(private val marcheDao: MarcheDao) : MarcheRepository {
    override fun observerMarches(): Flow<List<Marche>> = marcheDao.observerTous()

    override suspend fun ajouter(marche: Marche): Long = marcheDao.insert(marche)
}
