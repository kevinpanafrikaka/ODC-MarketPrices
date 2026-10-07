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
     * Point d'attention trouvé en revue : ProduitListViewModel.calculerTendance
     * (et l'équivalent dans ProduitDetailViewModel) compare les 2 relevés les
     * plus récents d'un produit TOUS MARCHÉS CONFONDUS, pas marché par marché.
     * Chaque produit a donc une chronologie combinée unique ci-dessous, sans
     * collision de jour entre marchés — vérifié par script avant d'écrire ce
     * fichier, pas seulement à l'œil.
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
        private val riz = 1L
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
                        Produit(nom = "Arachide", unite = "kg", categorie = "Oléagineux"),
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
            // Riz : hausse à Madina, stable à Niger, baisse à Enco5
            addAll(releves(riz, madina, listOf(28 to 9_200L, 20 to 9_300L, 10 to 9_450L, 3 to 9_550L, 0 to 9_600L)))
            addAll(releves(riz, niger, listOf(25 to 9_100L, 12 to 9_150L, 1 to 9_150L)))
            addAll(releves(riz, enco5, listOf(21 to 9_300L, 8 to 9_150L, 2 to 9_000L)))

            // Maïs : hausse à Matoto, baisse à Madina
            addAll(releves(mais, matoto, listOf(24 to 4_600L, 14 to 4_750L, 5 to 4_850L, 0 to 4_950L)))
            addAll(releves(mais, madina, listOf(18 to 4_900L, 9 to 4_800L, 1 to 4_650L)))

            // Fonio : hausse à Niger, stable à Madina
            addAll(releves(fonio, niger, listOf(22 to 6_200L, 14 to 6_400L, 6 to 6_650L, 0 to 6_800L)))
            addAll(releves(fonio, madina, listOf(19 to 6_500L, 9 to 6_450L, 2 to 6_500L)))

            // Mil : stable à Matoto, légère hausse à Madina
            addAll(releves(mil, matoto, listOf(20 to 4_300L, 10 to 4_320L, 1 to 4_350L)))
            addAll(releves(mil, madina, listOf(24 to 4_250L, 13 to 4_300L, 4 to 4_400L)))

            // Manioc : stable à Enco5, hausse à Matoto
            addAll(releves(manioc, enco5, listOf(22 to 2_700L, 10 to 2_750L, 0 to 2_750L)))
            addAll(releves(manioc, matoto, listOf(26 to 2_500L, 15 to 2_650L, 6 to 2_800L, 1 to 2_950L)))

            // Igname : hausse à Niger, baisse à Madina
            addAll(releves(igname, niger, listOf(27 to 6_100L, 16 to 6_350L, 7 to 6_600L, 0 to 6_800L)))
            addAll(releves(igname, madina, listOf(19 to 6_700L, 9 to 6_500L, 1 to 6_200L)))

            // Patate douce : stable à Enco5, légère baisse à Niger
            addAll(releves(patateDouce, enco5, listOf(21 to 3_600L, 11 to 3_620L, 2 to 3_650L)))
            addAll(releves(patateDouce, niger, listOf(25 to 3_700L, 14 to 3_680L, 5 to 3_600L)))

            // Taro : hausse à Matoto, baisse à Madina
            addAll(releves(taro, matoto, listOf(23 to 4_400L, 12 to 4_650L, 3 to 4_950L)))
            addAll(releves(taro, madina, listOf(19 to 4_800L, 9 to 4_700L, 1 to 4_550L)))

            // Huile de palme : hausse à Madina, stable à Enco5
            addAll(releves(huileDePalme, madina, listOf(25 to 15_200L, 14 to 15_800L, 5 to 16_400L, 0 to 17_000L)))
            addAll(releves(huileDePalme, enco5, listOf(20 to 15_900L, 9 to 15_950L, 1 to 15_950L)))

            // Huile d'arachide : baisse à Niger, stable à Matoto
            addAll(releves(huileDArachide, niger, listOf(22 to 18_200L, 13 to 17_700L, 4 to 17_000L)))
            addAll(releves(huileDArachide, matoto, listOf(18 to 17_500L, 8 to 17_550L, 1 to 17_600L)))

            // Arachide : baisse à Matoto, hausse à Niger
            addAll(releves(arachide, matoto, listOf(23 to 8_900L, 11 to 8_600L, 0 to 8_200L)))
            addAll(releves(arachide, niger, listOf(21 to 8_100L, 10 to 8_450L, 1 to 8_800L)))

            // Voandzou : stable à Enco5, légère hausse à Madina
            addAll(releves(voandzou, enco5, listOf(20 to 7_400L, 10 to 7_420L, 2 to 7_450L)))
            addAll(releves(voandzou, madina, listOf(24 to 7_300L, 13 to 7_380L, 5 to 7_500L)))

            // Haricot niébé : baisse à Matoto, hausse à Niger
            addAll(releves(haricotNiebe, matoto, listOf(21 to 7_500L, 11 to 7_300L, 3 to 7_100L)))
            addAll(releves(haricotNiebe, niger, listOf(25 to 6_700L, 14 to 6_900L, 6 to 7_050L)))

            // Oignon : stable à Madina, hausse à Enco5, baisse à Niger
            addAll(releves(oignon, madina, listOf(26 to 7_300L, 13 to 7_350L, 0 to 7_350L)))
            addAll(releves(oignon, enco5, listOf(24 to 7_000L, 12 to 7_400L, 1 to 7_800L)))
            addAll(releves(oignon, niger, listOf(18 to 8_000L, 6 to 7_700L, 2 to 7_400L)))

            // Tomate : hausse à Matoto, baisse à Madina
            addAll(releves(tomate, matoto, listOf(24 to 4_000L, 12 to 4_600L, 0 to 5_200L)))
            addAll(releves(tomate, madina, listOf(20 to 5_000L, 7 to 4_600L, 1 to 4_200L)))

            // Gombo : stable à Niger, hausse à Enco5
            addAll(releves(gombo, niger, listOf(22 to 3_200L, 9 to 3_250L, 0 to 3_250L)))
            addAll(releves(gombo, enco5, listOf(19 to 3_000L, 8 to 3_400L, 1 to 3_800L)))

            // Piment : baisse à Madina, stable à Matoto
            addAll(releves(piment, madina, listOf(22 to 6_200L, 12 to 5_800L, 4 to 5_300L)))
            addAll(releves(piment, matoto, listOf(18 to 5_650L, 9 to 5_680L, 1 to 5_700L)))

            // Aubergine africaine : stable à Niger, légère hausse à Enco5
            addAll(releves(aubergineAfricaine, niger, listOf(20 to 3_900L, 10 to 3_920L, 2 to 3_950L)))
            addAll(releves(aubergineAfricaine, enco5, listOf(24 to 3_800L, 13 to 3_880L, 5 to 3_980L)))

            // Concombre : baisse à Matoto, stable à Madina
            addAll(releves(concombre, matoto, listOf(21 to 3_400L, 11 to 3_250L, 3 to 3_100L)))
            addAll(releves(concombre, madina, listOf(17 to 2_950L, 8 to 2_920L, 1 to 2_900L)))

            // Chou : hausse à Enco5, baisse à Niger
            addAll(releves(chou, enco5, listOf(23 to 5_200L, 12 to 5_600L, 4 to 6_000L)))
            addAll(releves(chou, niger, listOf(19 to 5_900L, 9 to 5_750L, 1 to 5_600L)))

            // Carotte : stable à Matoto, légère hausse à Madina
            addAll(releves(carotte, matoto, listOf(20 to 6_400L, 10 to 6_430L, 2 to 6_450L)))
            addAll(releves(carotte, madina, listOf(24 to 6_300L, 13 to 6_380L, 5 to 6_500L)))

            // Gingembre : hausse à Niger, baisse à Enco5
            addAll(releves(gingembre, niger, listOf(22 to 12_200L, 12 to 12_900L, 4 to 13_700L)))
            addAll(releves(gingembre, enco5, listOf(18 to 13_500L, 8 to 13_200L, 1 to 12_900L)))

            // Poisson fumé : hausse à Madina, baisse à Matoto
            addAll(releves(poissonFume, madina, listOf(27 to 25_500L, 15 to 26_400L, 5 to 27_300L, 0 to 28_000L)))
            addAll(releves(poissonFume, matoto, listOf(21 to 27_500L, 9 to 26_600L, 1 to 25_800L)))

            // Poisson frais (Capitaine) : baisse à Madina, hausse à Niger
            addAll(releves(poissonFraisCapitaine, madina, listOf(21 to 33_500L, 11 to 32_500L, 3 to 31_800L)))
            addAll(releves(poissonFraisCapitaine, niger, listOf(25 to 29_800L, 14 to 30_500L, 6 to 31_200L)))

            // Crevettes : stable à Enco5, légère hausse à Madina
            addAll(releves(crevettes, enco5, listOf(19 to 47_000L, 9 to 47_250L, 1 to 47_500L)))
            addAll(releves(crevettes, madina, listOf(23 to 46_500L, 12 to 46_900L, 4 to 47_300L)))

            // Banane plantain : hausse à Enco5, stable à Niger
            addAll(releves(bananePlantain, enco5, listOf(23 to 12_200L, 11 to 12_900L, 0 to 13_700L)))
            addAll(releves(bananePlantain, niger, listOf(17 to 13_000L, 6 to 13_050L, 1 to 13_050L)))

            // Mangue : baisse à Madina, stable à Matoto
            addAll(releves(mangue, madina, listOf(22 to 4_200L, 12 to 3_750L, 4 to 3_300L)))
            addAll(releves(mangue, matoto, listOf(18 to 3_650L, 9 to 3_680L, 1 to 3_700L)))

            // Orange : stable à Niger, légère hausse à Enco5
            addAll(releves(orange, niger, listOf(20 to 2_800L, 10 to 2_820L, 2 to 2_850L)))
            addAll(releves(orange, enco5, listOf(24 to 2_700L, 13 to 2_780L, 5 to 2_880L)))

            // Ananas : hausse à Matoto, baisse à Madina
            addAll(releves(ananas, matoto, listOf(23 to 8_200L, 12 to 9_000L, 4 to 9_800L)))
            addAll(releves(ananas, madina, listOf(19 to 9_500L, 9 to 9_300L, 1 to 9_100L)))

            // Poulet : hausse à Enco5, stable à Niger
            addAll(releves(poulet, enco5, listOf(21 to 48_000L, 11 to 51_000L, 3 to 54_000L)))
            addAll(releves(poulet, niger, listOf(17 to 52_500L, 8 to 52_750L, 1 to 53_000L)))
        }
    }
}
