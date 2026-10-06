package com.odc.prixdumarche.ui.navigation

/**
 * Contrat de navigation partagé par l'équipe. Toute nouvelle route doit être
 * ajoutée ici pour éviter que deux personnes ne définissent des routes
 * concurrentes dans leurs branches respectives.
 */
sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object ProduitListe : Screen("produits")
    data object ProduitDetail : Screen("produits/{produitId}") {
        fun buildRoute(produitId: Long) = "produits/$produitId"
    }
    data object ReleveFormulaire : Screen("releve")
    data object TableauBord : Screen("tableau-de-bord")
    data object Favoris : Screen("favoris")
}
