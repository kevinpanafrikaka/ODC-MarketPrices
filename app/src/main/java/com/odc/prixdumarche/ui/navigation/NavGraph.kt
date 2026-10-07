package com.odc.prixdumarche.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.odc.prixdumarche.PrixDuMarcheApplication
import com.odc.prixdumarche.data.AppContainer
import com.odc.prixdumarche.ui.screens.dashboard.TableauBordScreen
import com.odc.prixdumarche.ui.screens.produit.ProduitDetailScreen
import com.odc.prixdumarche.ui.screens.produit.ProduitListScreen
import com.odc.prixdumarche.ui.screens.releve.ReleveFormulaireScreen
import com.odc.prixdumarche.ui.screens.splash.SplashScreen
import com.odc.prixdumarche.viewmodels.ProduitDetailViewModel
import com.odc.prixdumarche.viewmodels.ProduitListViewModel
import com.odc.prixdumarche.viewmodels.ReleveFormulaireViewModel
import com.odc.prixdumarche.viewmodels.TableauBordViewModel
import com.odc.prixdumarche.viewmodels.UiState

/**
 * Graphe de navigation reliant les 4 écrans du MVP, chacun branché sur son
 * ViewModel (StateFlow<UiState<...>>) via le conteneur de dépendances
 * manuel exposé par PrixDuMarcheApplication (pas de Hilt dans ce projet).
 */
@Composable
fun PrixDuMarcheNavGraph(navController: NavHostController = rememberNavController()) {
    val container = appContainer()

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onTermine = {
                    navController.navigate(Screen.ProduitListe.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.ProduitListe.route) {
            val viewModel: ProduitListViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        ProduitListViewModel(
                            container.produitRepository,
                            container.marcheRepository,
                            container.releveePrixRepository
                        )
                    }
                }
            )
            val uiState by viewModel.uiState.collectAsState()
            val data = (uiState as? UiState.Success)?.data

            ProduitListScreen(
                onProduitClick = { produitId ->
                    navController.navigate(Screen.ProduitDetail.buildRoute(produitId))
                },
                produits = data?.produits ?: emptyList(),
                categories = data?.categories ?: listOf("Toutes"),
                marches = data?.marches ?: emptyList(),
                categorieFiltre = data?.categorieFiltre ?: "Toutes",
                marcheFiltre = data?.marcheFiltre,
                chargement = uiState is UiState.Loading,
                onCategorieChoisie = viewModel::onCategorieChoisie,
                onMarcheChoisi = viewModel::onMarcheChoisi,
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

            val viewModel: ProduitDetailViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        ProduitDetailViewModel(
                            produitId,
                            container.produitRepository,
                            container.marcheRepository,
                            container.releveePrixRepository
                        )
                    }
                }
            )
            val uiState by viewModel.uiState.collectAsState()
            val data = (uiState as? UiState.Success)?.data

            ProduitDetailScreen(
                produitId = produitId,
                nomProduit = data?.nomProduit ?: "Chargement...",
                unite = data?.unite ?: "",
                tendance = data?.tendance?.name,
                historique = data?.historique ?: emptyList(),
                prixParMarche = data?.prixParMarche ?: emptyList(),
                comparaison = data?.comparaison,
                chargement = uiState is UiState.Loading,
                onRetour = { navController.popBackStack() }
            )
        }
        composable(Screen.ReleveFormulaire.route) {
            val viewModel: ReleveFormulaireViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        ReleveFormulaireViewModel(
                            container.produitRepository,
                            container.marcheRepository,
                            container.releveePrixRepository
                        )
                    }
                }
            )
            val uiState by viewModel.uiState.collectAsState()
            val data = (uiState as? UiState.Success)?.data

            ReleveFormulaireScreen(
                produits = data?.produits ?: emptyList(),
                marches = data?.marches ?: emptyList(),
                produitChoisi = data?.produitChoisi,
                marcheChoisi = data?.marcheChoisi,
                prixTexte = data?.prixTexte ?: "",
                dateTexte = data?.dateTexte ?: "",
                erreurProduit = data?.erreurProduit,
                erreurMarche = data?.erreurMarche,
                erreurPrix = data?.erreurPrix,
                erreurDate = data?.erreurDate,
                erreurGenerale = data?.erreurGenerale,
                enregistrementReussi = data?.enregistrementReussi ?: false,
                onProduitChoisi = viewModel::onProduitChoisi,
                onMarcheChoisi = viewModel::onMarcheChoisi,
                onPrixChange = viewModel::onPrixChange,
                onDateChange = viewModel::onDateChange,
                onEnregistrer = viewModel::onEnregistrer,
                onRetour = { navController.popBackStack() }
            )
        }
        composable(Screen.TableauBord.route) {
            val viewModel: TableauBordViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        TableauBordViewModel(
                            container.produitRepository,
                            container.releveePrixRepository
                        )
                    }
                }
            )
            val uiState by viewModel.uiState.collectAsState()
            val data = (uiState as? UiState.Success)?.data

            TableauBordScreen(
                panierMoyenGnf = data?.panierMoyenGnf,
                nbProduitsPanier = data?.nbProduitsPanier ?: 0,
                hausses = data?.hausses ?: emptyList(),
                baisses = data?.baisses ?: emptyList(),
                chargement = uiState is UiState.Loading,
                onRetour = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun appContainer(): AppContainer {
    val context = LocalContext.current.applicationContext
    return (context as PrixDuMarcheApplication).container
}
