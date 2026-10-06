package com.odc.prixdumarche.data

import android.content.Context
import com.odc.prixdumarche.data.local.AppDatabase
import com.odc.prixdumarche.data.repository.MarcheRepository
import com.odc.prixdumarche.data.repository.MarcheRepositoryImpl
import com.odc.prixdumarche.data.repository.ProduitRepository
import com.odc.prixdumarche.data.repository.ProduitRepositoryImpl
import com.odc.prixdumarche.data.repository.ReleveePrixRepository
import com.odc.prixdumarche.data.repository.ReleveePrixRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

/**
 * Point d'assemblage manuel des dépendances (pas de Hilt dans ce projet) :
 * une seule instance de la base et des Repository, partagée par toute l'app.
 */
interface AppContainer {
    val produitRepository: ProduitRepository
    val marcheRepository: MarcheRepository
    val releveePrixRepository: ReleveePrixRepository
}

class DefaultAppContainer(context: Context) : AppContainer {
    private val applicationScope = CoroutineScope(SupervisorJob())
    private val database = AppDatabase.getInstance(context, applicationScope)

    override val produitRepository: ProduitRepository by lazy {
        ProduitRepositoryImpl(database.produitDao())
    }

    override val marcheRepository: MarcheRepository by lazy {
        MarcheRepositoryImpl(database.marcheDao())
    }

    override val releveePrixRepository: ReleveePrixRepository by lazy {
        ReleveePrixRepositoryImpl(database.releveePrixDao())
    }
}
