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
    entities = [
        Marche::class,
        Produit::class,
        ReleveePrix::class
    ],
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

        fun getInstance(
            context: Context,
            scope: CoroutineScope
        ): AppDatabase {

            instance?.let {
                return it
            }

            synchronized(this) {

                instance?.let {
                    return it
                }

                lateinit var database: AppDatabase

                database = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "prix_du_marche.db"
                )
                    .addCallback(
                        SeedCallback(
                            scope = scope,
                            databaseProvider = { database }
                        )
                    )
                    .build()

                instance = database

                return database
            }
        }
    }

    /**
     * Insère les données initiales lors de la création de la base.
     */
    private class SeedCallback(
        private val scope: CoroutineScope,
        private val databaseProvider: () -> AppDatabase
    ) : Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            scope.launch(Dispatchers.IO) {

                val database = databaseProvider()

                // =========================================================
                // MARCHÉS
                // =========================================================

                database.marcheDao().insertAll(
                    listOf(
                        Marche(
                            nom = "Madina",
                            commune = "Matam"
                        ),
                        Marche(
                            nom = "Niger",
                            commune = "Kaloum"
                        ),
                        Marche(
                            nom = "Matoto",
                            commune = "Matoto"
                        ),
                        Marche(
                            nom = "Enco5",
                            commune = "Ratoma"
                        )
                    )
                )

                // =========================================================
                // PRODUITS
                // =========================================================

                database.produitDao().insertAll(
                    listOf(
                        Produit(
                            nom = "Riz",
                            unite = "kg",
                            categorie = "Céréales"
                        ),
                        Produit(
                            nom = "Huile",
                            unite = "litre",
                            categorie = "Épicerie"
                        ),
                        Produit(
                            nom = "Oignon",
                            unite = "kg",
                            categorie = "Légumes"
                        ),
                        Produit(
                            nom = "Tomate",
                            unite = "tas",
                            categorie = "Légumes"
                        ),
                        Produit(
                            nom = "Poisson",
                            unite = "kg",
                            categorie = "Poissons"
                        ),
                        Produit(
                            nom = "Pomme de terre",
                            unite = "kg",
                            categorie = "Légumes"
                        ),
                        Produit(
                            nom = "Haricot",
                            unite = "kg",
                            categorie = "Céréales"
                        ),
                        Produit(
                            nom = "Banane",
                            unite = "régime",
                            categorie = "Fruits"
                        ),
                        Produit(
                            nom = "Mangue",
                            unite = "kg",
                            categorie = "Fruits"
                        ),
                        Produit(
                            nom = "Poulet",
                            unite = "kg",
                            categorie = "Viandes"
                        )
                    )
                )

                // =========================================================
                // RELEVÉS DE PRIX
                // =========================================================

                val maintenant = System.currentTimeMillis()

                val unJour = 24L * 60L * 60L * 1000L

                database.releveePrixDao().insertAll(
                    listOf(

                        // =================================================
                        // RIZ - produitId = 1
                        // =================================================

                        ReleveePrix(
                            produitId = 1L,
                            marcheId = 1L,
                            prixGnf = 9_200L,
                            date = maintenant - 25L * unJour
                        ),

                        ReleveePrix(
                            produitId = 1L,
                            marcheId = 1L,
                            prixGnf = 9_400L,
                            date = maintenant - 12L * unJour
                        ),

                        ReleveePrix(
                            produitId = 1L,
                            marcheId = 1L,
                            prixGnf = 9_600L,
                            date = maintenant
                        ),

                        ReleveePrix(
                            produitId = 1L,
                            marcheId = 2L,
                            prixGnf = 9_300L,
                            date = maintenant - 5L * unJour
                        ),

                        ReleveePrix(
                            produitId = 1L,
                            marcheId = 3L,
                            prixGnf = 9_800L,
                            date = maintenant - 3L * unJour
                        ),

                        // =================================================
                        // HUILE - produitId = 2
                        // =================================================

                        ReleveePrix(
                            produitId = 2L,
                            marcheId = 1L,
                            prixGnf = 19_500L,
                            date = maintenant - 20L * unJour
                        ),

                        ReleveePrix(
                            produitId = 2L,
                            marcheId = 1L,
                            prixGnf = 18_900L,
                            date = maintenant
                        ),

                        ReleveePrix(
                            produitId = 2L,
                            marcheId = 2L,
                            prixGnf = 19_200L,
                            date = maintenant - 4L * unJour
                        ),

                        // =================================================
                        // OIGNON - produitId = 3
                        // =================================================

                        ReleveePrix(
                            produitId = 3L,
                            marcheId = 3L,
                            prixGnf = 7_000L,
                            date = maintenant - 15L * unJour
                        ),

                        ReleveePrix(
                            produitId = 3L,
                            marcheId = 3L,
                            prixGnf = 7_300L,
                            date = maintenant
                        ),

                        ReleveePrix(
                            produitId = 3L,
                            marcheId = 1L,
                            prixGnf = 7_500L,
                            date = maintenant - 2L * unJour
                        ),

                        // =================================================
                        // TOMATE - produitId = 4
                        // =================================================

                        ReleveePrix(
                            produitId = 4L,
                            marcheId = 1L,
                            prixGnf = 5_200L,
                            date = maintenant - 18L * unJour
                        ),

                        ReleveePrix(
                            produitId = 4L,
                            marcheId = 1L,
                            prixGnf = 4_800L,
                            date = maintenant
                        ),

                        ReleveePrix(
                            produitId = 4L,
                            marcheId = 2L,
                            prixGnf = 5_000L,
                            date = maintenant - 6L * unJour
                        ),

                        // =================================================
                        // POISSON - produitId = 5
                        // =================================================

                        ReleveePrix(
                            produitId = 5L,
                            marcheId = 1L,
                            prixGnf = 36_000L,
                            date = maintenant - 22L * unJour
                        ),

                        ReleveePrix(
                            produitId = 5L,
                            marcheId = 1L,
                            prixGnf = 34_500L,
                            date = maintenant
                        ),

                        ReleveePrix(
                            produitId = 5L,
                            marcheId = 3L,
                            prixGnf = 35_000L,
                            date = maintenant - 7L * unJour
                        ),

                        // =================================================
                        // POMME DE TERRE - produitId = 6
                        // =================================================

                        ReleveePrix(
                            produitId = 6L,
                            marcheId = 2L,
                            prixGnf = 6_500L,
                            date = maintenant - 16L * unJour
                        ),

                        ReleveePrix(
                            produitId = 6L,
                            marcheId = 2L,
                            prixGnf = 6_800L,
                            date = maintenant
                        ),

                        ReleveePrix(
                            produitId = 6L,
                            marcheId = 3L,
                            prixGnf = 6_600L,
                            date = maintenant - 5L * unJour
                        ),

                        // =================================================
                        // HARICOT - produitId = 7
                        // =================================================

                        ReleveePrix(
                            produitId = 7L,
                            marcheId = 1L,
                            prixGnf = 8_000L,
                            date = maintenant - 14L * unJour
                        ),

                        ReleveePrix(
                            produitId = 7L,
                            marcheId = 1L,
                            prixGnf = 8_300L,
                            date = maintenant
                        ),

                        ReleveePrix(
                            produitId = 7L,
                            marcheId = 2L,
                            prixGnf = 8_100L,
                            date = maintenant - 3L * unJour
                        ),

                        // =================================================
                        // BANANE - produitId = 8
                        // =================================================

                        ReleveePrix(
                            produitId = 8L,
                            marcheId = 3L,
                            prixGnf = 12_000L,
                            date = maintenant - 10L * unJour
                        ),

                        ReleveePrix(
                            produitId = 8L,
                            marcheId = 3L,
                            prixGnf = 11_500L,
                            date = maintenant
                        ),

                        ReleveePrix(
                            produitId = 8L,
                            marcheId = 1L,
                            prixGnf = 12_200L,
                            date = maintenant - 2L * unJour
                        ),

                        // =================================================
                        // MANGUE - produitId = 9
                        // =================================================

                        ReleveePrix(
                            produitId = 9L,
                            marcheId = 2L,
                            prixGnf = 10_000L,
                            date = maintenant - 9L * unJour
                        ),

                        ReleveePrix(
                            produitId = 9L,
                            marcheId = 2L,
                            prixGnf = 9_500L,
                            date = maintenant
                        ),

                        ReleveePrix(
                            produitId = 9L,
                            marcheId = 3L,
                            prixGnf = 10_200L,
                            date = maintenant - 1L * unJour
                        ),

                        // =================================================
                        // POULET - produitId = 10
                        // =================================================

                        ReleveePrix(
                            produitId = 10L,
                            marcheId = 1L,
                            prixGnf = 55_000L,
                            date = maintenant - 11L * unJour
                        ),

                        ReleveePrix(
                            produitId = 10L,
                            marcheId = 1L,
                            prixGnf = 52_000L,
                            date = maintenant
                        ),

                        ReleveePrix(
                            produitId = 10L,
                            marcheId = 2L,
                            prixGnf = 53_500L,
                            date = maintenant - 4L * unJour
                        )
                    )
                )
            }
        }
    }
}