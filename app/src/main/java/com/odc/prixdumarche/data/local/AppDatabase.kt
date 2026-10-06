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
import java.util.concurrent.TimeUnit

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
     * Pré-remplit la base au premier lancement avec des produits et marchés
     * guinéens, et plusieurs relevés par produit étalés sur les 30 derniers
     * jours (pas tous au même instant) : sans ça, la tendance, la courbe
     * d'évolution et la comparaison entre marchés n'ont rien à afficher.
     *
     * Les id référencés dans les relevés correspondent à l'ordre d'insertion
     * ci-dessous (Room les auto-génère à partir de 1).
     */
    private class SeedCallback(
        private val scope: CoroutineScope,
        private val databaseProvider: () -> AppDatabase
    ) : Callback() {

        // Marchés (id 1 à 4, dans l'ordre d'insertion)
        private val madina = 1L
        private val niger = 2L
        private val matoto = 3L
        private val enco5 = 4L

        // Produits (id 1 à 11, dans l'ordre d'insertion)
        private val riz = 1L
        private val mais = 2L
        private val manioc = 3L
        private val igname = 4L
        private val huileDePalme = 5L
        private val arachide = 6L
        private val oignon = 7L
        private val tomate = 8L
        private val gombo = 9L
        private val poissonFume = 10L
        private val bananePlantain = 11L

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            scope.launch(Dispatchers.IO) {
                val database = databaseProvider()

                database.marcheDao().insertAll(
                    listOf(
                        Marche(nom = "Madina", commune = "Matam"),
                        Marche(nom = "Niger", commune = "Kaloum"),
                        Marche(nom = "Matoto", commune = "Matoto"),
                        Marche(nom = "Enco5", commune = "Ratoma")
                    )
                )

                database.produitDao().insertAll(
                    listOf(
                        Produit(nom = "Riz local", unite = "kg", categorie = "Céréales"),
                        Produit(nom = "Maïs", unite = "kg", categorie = "Céréales"),
                        Produit(nom = "Manioc", unite = "kg", categorie = "Tubercules"),
                        Produit(nom = "Igname", unite = "kg", categorie = "Tubercules"),
                        Produit(nom = "Huile de palme", unite = "litre", categorie = "Huiles"),
                        Produit(nom = "Arachide", unite = "kg", categorie = "Légumineuses"),
                        Produit(nom = "Oignon", unite = "kg", categorie = "Légumes"),
                        Produit(nom = "Tomate", unite = "tas", categorie = "Légumes"),
                        Produit(nom = "Gombo", unite = "tas", categorie = "Légumes"),
                        Produit(nom = "Poisson fumé", unite = "kg", categorie = "Poissons"),
                        Produit(nom = "Banane plantain", unite = "régime", categorie = "Fruits")
                    )
                )

                database.releveePrixDao().insertAll(releves())
            }
        }

        /** (jours avant aujourd'hui, prix en GNF), du plus ancien au plus récent. */
        private fun releves(produitId: Long, marcheId: Long, points: List<Pair<Int, Long>>): List<ReleveePrix> {
            val maintenant = System.currentTimeMillis()
            return points.map { (joursAvant, prixGnf) ->
                ReleveePrix(
                    produitId = produitId,
                    marcheId = marcheId,
                    prixGnf = prixGnf,
                    date = maintenant - TimeUnit.DAYS.toMillis(joursAvant.toLong())
                )
            }
        }

        private fun releves() = buildList {
            // Riz : hausse à Madina, stable à Niger, baisse à Enco5
            addAll(releves(riz, madina, listOf(28 to 9_200L, 20 to 9_300L, 10 to 9_450L, 3 to 9_550L, 0 to 9_600L)))
            addAll(releves(riz, niger, listOf(25 to 9_100L, 12 to 9_150L, 0 to 9_150L)))
            addAll(releves(riz, enco5, listOf(20 to 9_300L, 8 to 9_150L, 0 to 9_000L)))

            // Maïs : hausse à Matoto, baisse à Madina
            addAll(releves(mais, matoto, listOf(24 to 4_600L, 14 to 4_750L, 5 to 4_850L, 0 to 4_950L)))
            addAll(releves(mais, madina, listOf(18 to 4_900L, 9 to 4_800L, 0 to 4_650L)))

            // Manioc : stable à Enco5, hausse à Matoto
            addAll(releves(manioc, enco5, listOf(22 to 2_700L, 10 to 2_750L, 0 to 2_750L)))
            addAll(releves(manioc, matoto, listOf(26 to 2_500L, 15 to 2_650L, 6 to 2_800L, 0 to 2_950L)))

            // Igname : hausse à Niger, baisse à Madina
            addAll(releves(igname, niger, listOf(27 to 6_100L, 16 to 6_350L, 7 to 6_600L, 0 to 6_800L)))
            addAll(releves(igname, madina, listOf(19 to 6_700L, 9 to 6_500L, 0 to 6_200L)))

            // Huile de palme : hausse à Madina, stable à Enco5
            addAll(releves(huileDePalme, madina, listOf(25 to 15_200L, 14 to 15_800L, 5 to 16_400L, 0 to 17_000L)))
            addAll(releves(huileDePalme, enco5, listOf(20 to 15_900L, 9 to 15_950L, 0 to 15_950L)))

            // Arachide : baisse à Matoto, hausse à Niger
            addAll(releves(arachide, matoto, listOf(23 to 8_900L, 11 to 8_600L, 0 to 8_200L)))
            addAll(releves(arachide, niger, listOf(21 to 8_100L, 10 to 8_450L, 0 to 8_800L)))

            // Oignon : stable à Madina, hausse à Enco5, baisse à Niger
            addAll(releves(oignon, madina, listOf(26 to 7_300L, 13 to 7_350L, 0 to 7_350L)))
            addAll(releves(oignon, enco5, listOf(24 to 7_000L, 12 to 7_400L, 0 to 7_800L)))
            addAll(releves(oignon, niger, listOf(18 to 8_000L, 6 to 7_700L, 0 to 7_400L)))

            // Tomate : hausse à Matoto, baisse à Madina
            addAll(releves(tomate, matoto, listOf(24 to 4_000L, 12 to 4_600L, 0 to 5_200L)))
            addAll(releves(tomate, madina, listOf(20 to 5_000L, 7 to 4_600L, 0 to 4_200L)))

            // Gombo : stable à Niger, hausse à Enco5
            addAll(releves(gombo, niger, listOf(22 to 3_200L, 9 to 3_250L, 0 to 3_250L)))
            addAll(releves(gombo, enco5, listOf(19 to 3_000L, 8 to 3_400L, 0 to 3_800L)))

            // Poisson fumé : hausse à Madina, baisse à Matoto
            addAll(releves(poissonFume, madina, listOf(27 to 25_500L, 15 to 26_400L, 5 to 27_300L, 0 to 28_000L)))
            addAll(releves(poissonFume, matoto, listOf(21 to 27_500L, 9 to 26_600L, 0 to 25_800L)))

            // Banane plantain : hausse à Enco5, stable à Niger
            addAll(releves(bananePlantain, enco5, listOf(23 to 12_200L, 11 to 12_900L, 0 to 13_700L)))
            addAll(releves(bananePlantain, niger, listOf(17 to 13_000L, 6 to 13_050L, 0 to 13_050L)))
        }
    }
}
