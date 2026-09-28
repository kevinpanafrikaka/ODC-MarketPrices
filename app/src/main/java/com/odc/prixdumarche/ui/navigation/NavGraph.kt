package com.odc.prixdumarche.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.odc.prixdumarche.ui.screens.dashboard.TableauBordScreen
import com.odc.prixdumarche.ui.screens.produit.ProduitDetailScreen
import com.odc.prixdumarche.ui.screens.produit.ProduitListScreen
import com.odc.prixdumarche.ui.screens.releve.ReleveFormulaireScreen

/**
 * Graphe de navigation de référence reliant les 4 écrans du MVP.
 * Chaque écran est encore un stub (voir TODO dans son fichier) : cette
 * fonction ne fait que prouver que la navigation compile et fonctionne,
 * pour que chacun puisse brancher son travail sans attendre les autres.
 */
@Composable
fun PrixDuMarcheNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.ProduitListe.route) {
        composable(Screen.ProduitListe.route) {
            ProduitListScreen(
                onProduitClick = { produitId ->
                    navController.navigate(Screen.ProduitDetail.buildRoute(produitId))
                }
            )
        }
        composable(Screen.ProduitDetail.route) { backStackEntry ->
            val produitId = backStackEntry.arguments
                ?.getString("produitId")
                ?.toLongOrNull()
                ?: 0L
            ProduitDetailScreen(produitId = produitId)
        }
        composable(Screen.ReleveFormulaire.route) {
            ReleveFormulaireScreen()
        }
        composable(Screen.TableauBord.route) {
            TableauBordScreen()
        }
    }
}
