package com.odc.prixdumarche.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.odc.prixdumarche.data.local.entity.Marche
import kotlinx.coroutines.flow.Flow

@Dao
interface MarcheDao {
    @Insert
    suspend fun insert(marche: Marche): Long

    @Insert
    suspend fun insertAll(marches: List<Marche>)

    @Query("SELECT * FROM marches ORDER BY nom ASC")
    fun observerTous(): Flow<List<Marche>>
}
