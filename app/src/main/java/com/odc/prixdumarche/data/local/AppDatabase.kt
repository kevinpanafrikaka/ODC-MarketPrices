package com.odc.prixdumarche.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.odc.prixdumarche.data.local.dao.MarcheDao
import com.odc.prixdumarche.data.local.dao.ProduitDao
import com.odc.prixdumarche.data.local.dao.ReleveePrixDao
import com.odc.prixdumarche.data.local.entity.Marche
import com.odc.prixdumarche.data.local.entity.Produit
import com.odc.prixdumarche.data.local.entity.ReleveePrix
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Marche::class, Produit::class, ReleveePrix::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun marcheDao(): MarcheDao
    abstract fun produitDao(): ProduitDao
    abstract fun releveePrixDao(): ReleveePrixDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context, scope: CoroutineScope): AppDatabase {
            instance?.let { return it }
            synchronized(this) {
                instance?.let { return it }
                lateinit var database: AppDatabase
                database = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "prix_du_marche.db"
                )
                    .addCallback(SeedCallback(scope) { database })
                    .build()
                instance = database
                return database
            }
        }
    }

    /**
     * Pré-remplit la base au premier lancement, pour que l'app ne soit
     * jamais vide à la démo. Les noms reprennent ceux déjà utilisés comme
     * données factices côté UI (Madina/Matam, Niger/Kaloum, Matoto/Matoto).
     */
    private class SeedCallback(
        private val scope: CoroutineScope,
        private val databaseProvider: () -> AppDatabase
    ) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            scope.launch(Dispatchers.IO) {
                val database = databaseProvider()
                val marcheDao = database.marcheDao()
                val produitDao = database.produitDao()
                val releveeDao = database.releveePrixDao()

                marcheDao.insertAll(
                    listOf(
                        Marche(nom = "Madina", commune = "Matam"),
                        Marche(nom = "Niger", commune = "Kaloum"),
                        Marche(nom = "Matoto", commune = "Matoto")
                    )
                )
                produitDao.insertAll(
                    listOf(
                        Produit(nom = "Riz", unite = "kg", categorie = "Céréales"),
                        Produit(nom = "Huile", unite = "litre", categorie = "Épicerie"),
                        Produit(nom = "Oignon", unite = "kg", categorie = "Légumes"),
                        Produit(nom = "Tomate", unite = "tas", categorie = "Légumes"),
                        Produit(nom = "Poisson", unite = "kg", categorie = "Poissons")
                    )
                )
                releveeDao.insertAll(
                    listOf(
                        ReleveePrix(produitId = 1, marcheId = 1, prixGnf = 9_600, date = System.currentTimeMillis()),
                        ReleveePrix(produitId = 1, marcheId = 2, prixGnf = 9_300, date = System.currentTimeMillis()),
                        ReleveePrix(produitId = 2, marcheId = 1, prixGnf = 18_900, date = System.currentTimeMillis()),
                        ReleveePrix(produitId = 3, marcheId = 3, prixGnf = 7_300, date = System.currentTimeMillis())
                    )
                )
            }
        }
    }
}
