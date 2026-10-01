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
 * Graphe de navigation reliant les 4 écrans du MVP (liste, détail, formulaire,
 * tableau de bord). Les écrans sont terminés côté interface (Responsable interface) :
 * mise en page, thème et navigation sont en place, avec des valeurs factices
 * par défaut (voir TODO(logique) dans chaque fichier d'écran). Les ViewModels
 * du Responsable logique métier restent à brancher pour remplacer ces valeurs
 * par les vraies données issues du Repository.
 */
@Composable
fun PrixDuMarcheNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.ProduitListe.route) {
        composable(Screen.ProduitListe.route) {
            ProduitListScreen(
                onProduitClick = { produitId ->
                    navController.navigate(Screen.ProduitDetail.buildRoute(produitId))
                },
                onAjouterReleve = {
                    navController.navigate(Screen.ReleveFormulaire.route)
                },
                onTableauDeBord = {
                    navController.navigate(Screen.TableauBord.route)
                }
            )
        }
        composable(Screen.ProduitDetail.route) { backStackEntry ->
            val produitId = backStackEntry.arguments
                ?.getString("produitId")
                ?.toLongOrNull()
                ?: 0L
            ProduitDetailScreen(
                produitId = produitId,
                onRetour = { navController.popBackStack() }
            )
        }
        composable(Screen.ReleveFormulaire.route) {
            ReleveFormulaireScreen(onRetour = { navController.popBackStack() })
        }
        composable(Screen.TableauBord.route) {
            TableauBordScreen(onRetour = { navController.popBackStack() })
        }
    }
}