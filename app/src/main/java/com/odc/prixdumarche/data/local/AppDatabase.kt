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
     * guinéens, et plusieurs relevés par produit étalés sur les derniers jours.
     *
     * Point d'attention trouvé en revue : ProduitListViewModel.calculerTendance
     * (et l'équivalent dans ProduitDetailViewModel) compare les 2 relevés les
     * plus récents d'un produit TOUS MARCHÉS CONFONDUS, pas marché par marché.
     * Si deux marchés ont un relevé le même jour, le résultat dépend de l'ordre
     * d'insertion (non déterministe en pratique) plutôt que d'un vrai signal.
     * Chaque produit ci-dessous a donc une chronologie combinée unique, sans
     * collision de jour entre marchés, conçue pour que les 2 points les plus
     * récents (quel que soit le marché) donnent le sens de tendance voulu —
     * vérifié par simulation avant d'écrire ce fichier, pas seulement visuel.
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
        private val rizLocal = 1L
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
            // Riz local : hausse
            addAll(releves(rizLocal, niger, listOf(24 to 9_100L)))
            addAll(releves(rizLocal, matoto, listOf(17 to 9_250L)))
            addAll(releves(rizLocal, enco5, listOf(5 to 9_500L)))
            addAll(releves(rizLocal, madina, listOf(11 to 9_400L, 0 to 9_600L)))

            // Maïs : baisse
            addAll(releves(mais, matoto, listOf(20 to 4_950L)))
            addAll(releves(mais, niger, listOf(7 to 4_750L)))
            addAll(releves(mais, madina, listOf(13 to 4_850L, 0 to 4_650L)))

            // Manioc : hausse
            addAll(releves(manioc, enco5, listOf(22 to 2_600L, 6 to 2_850L)))
            addAll(releves(manioc, matoto, listOf(14 to 2_700L, 0 to 2_950L)))

            // Igname : baisse
            addAll(releves(igname, niger, listOf(19 to 6_700L, 4 to 6_350L)))
            addAll(releves(igname, madina, listOf(11 to 6_500L, 0 to 6_200L)))

            // Huile de palme : hausse
            addAll(releves(huileDePalme, enco5, listOf(21 to 15_300L, 4 to 16_400L)))
            addAll(releves(huileDePalme, madina, listOf(12 to 15_900L, 0 to 17_000L)))

            // Arachide : hausse
            addAll(releves(arachide, niger, listOf(9 to 8_450L)))
            addAll(releves(arachide, matoto, listOf(18 to 8_200L, 0 to 8_800L)))

            // Oignon : baisse (3 marchés, pour une comparaison bien démontrable)
            addAll(releves(oignon, enco5, listOf(17 to 7_000L)))
            addAll(releves(oignon, madina, listOf(11 to 7_350L)))
            addAll(releves(oignon, niger, listOf(5 to 7_700L, 0 to 7_400L)))

            // Tomate : hausse
            addAll(releves(tomate, madina, listOf(19 to 5_000L)))
            addAll(releves(tomate, matoto, listOf(9 to 4_600L, 0 to 5_200L)))

            // Gombo : hausse
            addAll(releves(gombo, enco5, listOf(7 to 3_400L)))
            addAll(releves(gombo, niger, listOf(16 to 3_150L, 0 to 3_800L)))

            // Poisson fumé : hausse
            addAll(releves(poissonFume, matoto, listOf(23 to 25_800L, 6 to 27_300L)))
            addAll(releves(poissonFume, madina, listOf(14 to 26_600L, 0 to 28_000L)))

            // Banane plantain : hausse
            addAll(releves(bananePlantain, enco5, listOf(9 to 12_900L)))
            addAll(releves(bananePlantain, niger, listOf(18 to 12_200L, 0 to 13_700L)))
        }
    }
}
