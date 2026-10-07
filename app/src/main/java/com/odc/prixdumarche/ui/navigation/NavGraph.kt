package com.odc.prixdumarche.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
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

private data class OngletBas(val route: String, val label: String, val icone: ImageVector)

private val ONGLETS_BAS = listOf(
    OngletBas(Screen.ProduitListe.route, "Accueil", Icons.Outlined.Home),
    OngletBas(Screen.TableauBord.route, "Tableau de bord", Icons.Outlined.BarChart)
)

/**
 * Graphe de navigation reliant les écrans du MVP, chacun branché sur son
 * ViewModel (StateFlow<UiState<...>>) via le conteneur de dépendances
 * manuel exposé par PrixDuMarcheApplication (pas de Hilt dans ce projet).
 *
 * Accueil / Tableau de bord sont les 2 onglets de la navbar du bas (visible
 * uniquement sur ces 2 routes) ; détail produit et formulaire de relevé
 * restent des écrans empilés par-dessus, sans navbar.
 */
@Composable
fun PrixDuMarcheNavGraph(navController: NavHostController = rememberNavController()) {
    val container = appContainer()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val routeActuelle = backStackEntry?.destination?.route
    val afficherNavBas = ONGLETS_BAS.any { it.route == routeActuelle }

    Scaffold(
        bottomBar = {
            if (afficherNavBas) {
                BarreNavigationBasse(
                    routeActuelle = routeActuelle,
                    onOngletChoisi = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(padding)
        ) {
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
                    categorieFiltre = data?.categorieFiltre ?: "Toutes",
                    chargement = uiState is UiState.Loading,
                    onCategorieChoisie = viewModel::onCategorieChoisie,
                    onAjouterReleve = {
                        navController.navigate(Screen.ReleveFormulaire.route)
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
                    chargement = uiState is UiState.Loading
                )
            }
        }
    }
}

/**
 * Navbar du bas volontairement compacte (≈52dp) : le NavigationBar Material3
 * standard impose ~80dp, bien plus que nécessaire pour 3 onglets texte+icône.
 */
@Composable
private fun BarreNavigationBasse(
    routeActuelle: String?,
    onOngletChoisi: (String) -> Unit
) {
    Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 2.dp) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(52.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ONGLETS_BAS.forEach { onglet ->
                val actif = routeActuelle == onglet.route
                val couleur = if (actif) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                Column(
                    modifier = Modifier
                        .clickable { onOngletChoisi(onglet.route) }
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(onglet.icone, contentDescription = onglet.label, tint = couleur, modifier = Modifier.height(20.dp))
                    Text(onglet.label, fontSize = 10.sp, color = couleur, maxLines = 1)
                }
            }
        }
    }
}

@Composable
private fun appContainer(): AppContainer {
    val context = LocalContext.current.applicationContext
    return (context as PrixDuMarcheApplication).container
}
