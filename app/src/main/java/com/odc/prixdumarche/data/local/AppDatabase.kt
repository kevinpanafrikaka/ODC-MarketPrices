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

        // Produits (id 1 à 30, dans l'ordre d'insertion)
        private val rizLocal = 1L
        private val mais = 2L
        private val fonio = 3L
        private val mil = 4L
        private val manioc = 5L
        private val igname = 6L
        private val patateDouce = 7L
        private val taro = 8L
        private val huileDePalme = 9L
        private val huileDArachide = 10L
        private val arachide = 11L
        private val voandzou = 12L
        private val haricotNiebe = 13L
        private val oignon = 14L
        private val tomate = 15L
        private val gombo = 16L
        private val piment = 17L
        private val aubergineAfricaine = 18L
        private val concombre = 19L
        private val chou = 20L
        private val carotte = 21L
        private val gingembre = 22L
        private val poissonFume = 23L
        private val poissonFraisCapitaine = 24L
        private val crevettes = 25L
        private val bananePlantain = 26L
        private val mangue = 27L
        private val orange = 28L
        private val ananas = 29L
        private val poulet = 30L

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
                        Produit(nom = "Fonio", unite = "kg", categorie = "Céréales"),
                        Produit(nom = "Mil", unite = "kg", categorie = "Céréales"),
                        Produit(nom = "Manioc", unite = "kg", categorie = "Tubercules"),
                        Produit(nom = "Igname", unite = "kg", categorie = "Tubercules"),
                        Produit(nom = "Patate douce", unite = "kg", categorie = "Tubercules"),
                        Produit(nom = "Taro", unite = "kg", categorie = "Tubercules"),
                        Produit(nom = "Huile de palme", unite = "litre", categorie = "Huiles"),
                        Produit(nom = "Huile d'arachide", unite = "litre", categorie = "Huiles"),
                        Produit(nom = "Arachide", unite = "kg", categorie = "Légumineuses"),
                        Produit(nom = "Voandzou", unite = "kg", categorie = "Légumineuses"),
                        Produit(nom = "Haricot niébé", unite = "kg", categorie = "Légumineuses"),
                        Produit(nom = "Oignon", unite = "kg", categorie = "Légumes"),
                        Produit(nom = "Tomate", unite = "tas", categorie = "Légumes"),
                        Produit(nom = "Gombo", unite = "tas", categorie = "Légumes"),
                        Produit(nom = "Piment", unite = "kg", categorie = "Légumes"),
                        Produit(nom = "Aubergine africaine", unite = "kg", categorie = "Légumes"),
                        Produit(nom = "Concombre", unite = "kg", categorie = "Légumes"),
                        Produit(nom = "Chou", unite = "pièce", categorie = "Légumes"),
                        Produit(nom = "Carotte", unite = "kg", categorie = "Légumes"),
                        Produit(nom = "Gingembre", unite = "kg", categorie = "Épicerie"),
                        Produit(nom = "Poisson fumé", unite = "kg", categorie = "Poissons"),
                        Produit(nom = "Poisson frais (Capitaine)", unite = "kg", categorie = "Poissons"),
                        Produit(nom = "Crevettes", unite = "kg", categorie = "Poissons"),
                        Produit(nom = "Banane plantain", unite = "régime", categorie = "Fruits"),
                        Produit(nom = "Mangue", unite = "tas", categorie = "Fruits"),
                        Produit(nom = "Orange", unite = "tas", categorie = "Fruits"),
                        Produit(nom = "Ananas", unite = "pièce", categorie = "Fruits"),
                        Produit(nom = "Poulet", unite = "pièce", categorie = "Volaille")
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

            // Fonio : hausse
            addAll(releves(fonio, enco5, listOf(18 to 6_200L)))
            addAll(releves(fonio, madina, listOf(9 to 6_500L)))
            addAll(releves(fonio, niger, listOf(0 to 6_800L)))

            // Mil : stable
            addAll(releves(mil, madina, listOf(8 to 4_350L)))
            addAll(releves(mil, matoto, listOf(16 to 4_300L, 0 to 4_350L)))

            // Manioc : hausse
            addAll(releves(manioc, enco5, listOf(22 to 2_600L, 6 to 2_850L)))
            addAll(releves(manioc, matoto, listOf(14 to 2_700L, 0 to 2_950L)))

            // Igname : baisse
            addAll(releves(igname, niger, listOf(19 to 6_700L, 4 to 6_350L)))
            addAll(releves(igname, madina, listOf(11 to 6_500L, 0 to 6_200L)))

            // Patate douce : stable
            addAll(releves(patateDouce, enco5, listOf(7 to 3_650L)))
            addAll(releves(patateDouce, madina, listOf(15 to 3_600L, 0 to 3_650L)))

            // Taro : hausse
            addAll(releves(taro, matoto, listOf(8 to 4_650L)))
            addAll(releves(taro, niger, listOf(17 to 4_400L, 0 to 4_950L)))

            // Huile de palme : hausse
            addAll(releves(huileDePalme, enco5, listOf(21 to 15_300L, 4 to 16_400L)))
            addAll(releves(huileDePalme, madina, listOf(12 to 15_900L, 0 to 17_000L)))

            // Huile d'arachide : baisse
            addAll(releves(huileDArachide, niger, listOf(8 to 17_600L)))
            addAll(releves(huileDArachide, matoto, listOf(16 to 18_200L, 0 to 17_000L)))

            // Arachide : hausse
            addAll(releves(arachide, niger, listOf(9 to 8_450L)))
            addAll(releves(arachide, matoto, listOf(18 to 8_200L, 0 to 8_800L)))

            // Voandzou : stable
            addAll(releves(voandzou, enco5, listOf(6 to 7_450L)))
            addAll(releves(voandzou, madina, listOf(14 to 7_400L, 0 to 7_450L)))

            // Haricot niébé : baisse
            addAll(releves(haricotNiebe, matoto, listOf(10 to 7_100L)))
            addAll(releves(haricotNiebe, niger, listOf(20 to 7_500L, 0 to 6_700L)))

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

            // Piment : baisse
            addAll(releves(piment, madina, listOf(6 to 5_700L)))
            addAll(releves(piment, matoto, listOf(15 to 6_200L, 0 to 5_300L)))

            // Aubergine africaine : stable
            addAll(releves(aubergineAfricaine, niger, listOf(5 to 3_950L)))
            addAll(releves(aubergineAfricaine, enco5, listOf(13 to 3_900L, 0 to 3_950L)))

            // Concombre : baisse
            addAll(releves(concombre, matoto, listOf(4 to 3_100L)))
            addAll(releves(concombre, madina, listOf(12 to 3_400L, 0 to 2_900L)))

            // Chou : hausse
            addAll(releves(chou, enco5, listOf(6 to 5_600L)))
            addAll(releves(chou, niger, listOf(14 to 5_200L, 0 to 6_000L)))

            // Carotte : stable
            addAll(releves(carotte, matoto, listOf(4 to 6_450L)))
            addAll(releves(carotte, madina, listOf(11 to 6_400L, 0 to 6_450L)))

            // Gingembre : hausse
            addAll(releves(gingembre, niger, listOf(7 to 12_900L)))
            addAll(releves(gingembre, enco5, listOf(16 to 12_200L, 0 to 13_700L)))

            // Poisson fumé : hausse
            addAll(releves(poissonFume, matoto, listOf(23 to 25_800L, 6 to 27_300L)))
            addAll(releves(poissonFume, madina, listOf(14 to 26_600L, 0 to 28_000L)))

            // Poisson frais (Capitaine) : baisse
            addAll(releves(poissonFraisCapitaine, madina, listOf(8 to 31_800L)))
            addAll(releves(poissonFraisCapitaine, niger, listOf(17 to 33_500L, 0 to 30_200L)))

            // Crevettes : stable
            addAll(releves(crevettes, enco5, listOf(5 to 47_500L)))
            addAll(releves(crevettes, madina, listOf(13 to 47_000L, 0 to 47_500L)))

            // Banane plantain : hausse
            addAll(releves(bananePlantain, enco5, listOf(9 to 12_900L)))
            addAll(releves(bananePlantain, niger, listOf(18 to 12_200L, 0 to 13_700L)))

            // Mangue : baisse
            addAll(releves(mangue, madina, listOf(6 to 3_700L)))
            addAll(releves(mangue, matoto, listOf(14 to 4_200L, 0 to 3_300L)))

            // Orange : stable
            addAll(releves(orange, niger, listOf(4 to 2_850L)))
            addAll(releves(orange, enco5, listOf(12 to 2_800L, 0 to 2_850L)))

            // Ananas : hausse
            addAll(releves(ananas, matoto, listOf(7 to 9_000L)))
            addAll(releves(ananas, madina, listOf(15 to 8_200L, 0 to 9_800L)))

            // Poulet : hausse
            addAll(releves(poulet, enco5, listOf(10 to 51_000L)))
            addAll(releves(poulet, niger, listOf(20 to 48_000L, 0 to 54_000L)))
        }
    }
}
