package com.odc.prixdumarche.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.odc.prixdumarche.data.local.entity.ReleveePrix
import kotlinx.coroutines.flow.Flow

/**
 * Les requêtes ci-dessous renvoient des lignes brutes de relevés.
 * Aucun calcul de tendance, min/max ou moyenne ici : c'est le rôle du
 * Repository / ViewModel (aucune logique métier dans la couche données).
 */
@Dao
interface ReleveePrixDao {
    @Insert
    suspend fun insert(releve: ReleveePrix): Long

    @Insert
    suspend fun insertAll(relevees: List<ReleveePrix>)

    @Query(
        """
        SELECT r.* FROM relevees_prix r
        INNER JOIN (
            SELECT produitId, MAX(date) AS maxDate
            FROM relevees_prix
            GROUP BY produitId
        ) dernier ON r.produitId = dernier.produitId AND r.date = dernier.maxDate
        """
    )
    fun observerDerniersPrixParProduit(): Flow<List<ReleveePrix>>

    @Query("SELECT * FROM relevees_prix ORDER BY date DESC")
    fun observerTousLesReleves(): Flow<List<ReleveePrix>>

    @Query(
        "SELECT * FROM relevees_prix WHERE produitId = :produitId AND date >= :depuis ORDER BY date ASC"
    )
    fun observerHistorique(produitId: Long, depuis: Long): Flow<List<ReleveePrix>>

    @Query(
        """
        SELECT r.* FROM relevees_prix r
        INNER JOIN (
            SELECT marcheId, MAX(date) AS maxDate
            FROM relevees_prix
            WHERE produitId = :produitId
            GROUP BY marcheId
        ) dernier ON r.marcheId = dernier.marcheId AND r.date = dernier.maxDate
        WHERE r.produitId = :produitId
        """
    )
    fun observerDernierPrixParMarche(produitId: Long): Flow<List<ReleveePrix>>
}
