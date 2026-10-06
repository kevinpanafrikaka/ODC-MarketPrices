package com.odc.prixdumarche.ui.util

import android.content.Context
import androidx.annotation.DrawableRes

/**
 * Nom de fichier attendu dans res/drawable pour chaque produit (sans
 * extension). Déposer un PNG/WebP portant exactement ce nom suffit à faire
 * apparaître l'image dans la grille — aucun changement de code nécessaire.
 */
private val NOMS_RESSOURCE_IMAGE = mapOf(
    "Riz local" to "produit_riz_local",
    "Maïs" to "produit_mais",
    "Manioc" to "produit_manioc",
    "Igname" to "produit_igname",
    "Huile de palme" to "produit_huile_de_palme",
    "Arachide" to "produit_arachide",
    "Oignon" to "produit_oignon",
    "Tomate" to "produit_tomate",
    "Gombo" to "produit_gombo",
    "Poisson fumé" to "produit_poisson_fume",
    "Banane plantain" to "produit_banane_plantain"
)

/**
 * Résout l'image d'un produit par son nom. Retourne null tant que le
 * drawable correspondant n'a pas été ajouté : l'appelant doit alors afficher
 * un repli (icône de catégorie), jamais planter sur une ressource absente.
 */
@DrawableRes
fun produitImageRes(context: Context, nom: String): Int? {
    val nomRessource = NOMS_RESSOURCE_IMAGE[nom] ?: return null
    val id = context.resources.getIdentifier(nomRessource, "drawable", context.packageName)
    return id.takeIf { it != 0 }
}
