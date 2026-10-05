package com.odc.prixdumarche.ui.screens.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.odc.prixdumarche.domain.model.VariationAffichee
import com.odc.prixdumarche.ui.components.TendancePill
import com.odc.prixdumarche.ui.theme.PrixDuMarcheTheme
import com.odc.prixdumarche.ui.util.enGnf

// TODO(données/logique) : remplacer par le vrai modèle du Repository / ViewModel

/**
 * Écran Tableau de bord.
 * Ne contient aucune logique métier : panier moyen, hausses et baisses de la
 * semaine sont calculés en amont (ViewModel) et transmis tout prêts ici.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableauBordScreen(
    // TODO(logique) : remplacer ces valeurs par défaut par viewModel.uiState.collectAsState()
    panierMoyenGnf: Long? = null,
    nbProduitsPanier: Int = 0,
    hausses: List<VariationAffichee> = emptyList(),
    baisses: List<VariationAffichee> = emptyList(),
    chargement: Boolean = false,
    onRetour: () -> Unit = {}
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Tableau de bord", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Cette semaine · Conakry", fontSize = 14.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onRetour) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        if (!chargement && panierMoyenGnf == null && hausses.isEmpty() && baisses.isEmpty()) {
            Box(
                Modifier.padding(padding).fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Aucune donnée pour le moment", fontSize = 16.sp)
            }
        } else {
            LazyColumn(
                Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (panierMoyenGnf != null) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text("Panier moyen", fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimary)
                                Text(
                                    panierMoyenGnf.enGnf(),
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Text(
                                    "1 unité de chacun des $nbProduitsPanier produits",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }

                item { Text("En hausse cette semaine", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                if (hausses.isEmpty()) {
                    item { Text("Aucune hausse cette semaine", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                } else {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Column(Modifier.padding(horizontal = 16.dp)) {
                                hausses.forEachIndexed { i, v ->
                                    LigneVariation(v, dernier = i == hausses.lastIndex)
                                }
                            }
                        }
                    }
                }

                item { Text("En baisse cette semaine", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                if (baisses.isEmpty()) {
                    item { Text("Aucune baisse cette semaine", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                } else {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Column(Modifier.padding(horizontal = 16.dp)) {
                                baisses.forEachIndexed { i, v ->
                                    LigneVariation(v, dernier = i == baisses.lastIndex)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LigneVariation(v: VariationAffichee, dernier: Boolean) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(v.nomProduit, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(v.prixGnf.enGnf(), fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        TendancePill(v.tendance, v.pourcentage)    }
    if (!dernier) HorizontalDivider(color = MaterialTheme.colorScheme.outline)
}

@Preview(showBackground = true)
@Composable
fun TableauBordScreenPreview() {
    PrixDuMarcheTheme {
        TableauBordScreen(
            panierMoyenGnf = 75_100,
            nbProduitsPanier = 5,
            hausses = listOf(
                VariationAffichee("Riz", 9_600, 4, "HAUSSE"),
                VariationAffichee("Huile", 18_900, 3, "HAUSSE")
            ),
            baisses = listOf(
                VariationAffichee("Tomate", 4_800, -5, "BAISSE"),
                VariationAffichee("Poisson", 34_500, -3, "BAISSE")
            )
        )
    }
}