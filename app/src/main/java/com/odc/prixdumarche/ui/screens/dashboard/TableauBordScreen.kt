package com.odc.prixdumarche.ui.screens.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.odc.prixdumarche.domain.model.VariationAffichee
import com.odc.prixdumarche.ui.components.TendancePill
import com.odc.prixdumarche.ui.theme.*
import com.odc.prixdumarche.ui.util.enGnf

/**
 * Écran Tableau de bord (onglet bas).
 * Ne contient aucune logique métier : panier moyen, nb favoris, hausses et
 * baisses sont calculés en amont (ViewModel) et transmis tout prêts ici.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableauBordScreen(
    // TODO(logique) : remplacer ces valeurs par défaut par viewModel.uiState.collectAsState()
    panierMoyenGnf: Long? = null,
    nbProduitsPanier: Int = 0,
    nbFavoris: Int = 0,
    hausses: List<VariationAffichee> = emptyList(),
    baisses: List<VariationAffichee> = emptyList(),
    chargement: Boolean = false,
    onRetour: (() -> Unit)? = null
) {
    val mouvements = (hausses + baisses).sortedByDescending { it.pourcentage }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text("eMarket • Tableau de Bord", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    if (onRetour != null) {
                        IconButton(onClick = onRetour) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Retour",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        if (!chargement && panierMoyenGnf == null && mouvements.isEmpty()) {
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        TuileStat("Panier moyen", panierMoyenGnf?.enGnf() ?: "—", Modifier.weight(1f))
                        TuileStat("Produits", "$nbProduitsPanier", Modifier.weight(1f))
                        TuileStat("Favoris", "$nbFavoris", Modifier.weight(1f))
                    }
                }

                item {
                    Text(
                        "Mouvements de prix",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                if (mouvements.isEmpty()) {
                    item { Text("Aucun mouvement notable pour le moment", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                } else {
                    items(mouvements.size) { i ->
                        CarteMouvement(mouvements[i])
                    }
                }
            }
        }
    }
}

@Composable
private fun TuileStat(label: String, valeur: String, modifier: Modifier = Modifier) {
    Column(
        modifier
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 14.dp)
    ) {
        Text(valeur, fontSize = 17.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

/**
 * Un mouvement = une carte individuelle (nom + badge tendance en haut, prix
 * en dessous), comme les autres cartes de l'app, plutôt qu'une barre.
 */
@Composable
private fun CarteMouvement(v: VariationAffichee) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    v.nomProduit,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                TendancePill(v.tendance, v.pourcentage)
            }
            Spacer(Modifier.height(4.dp))
            Text(v.prixGnf.enGnf(), fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TableauBordScreenPreview() {
    PrixDuMarcheTheme {
        TableauBordScreen(
            panierMoyenGnf = 75_100,
            nbProduitsPanier = 5,
            nbFavoris = 2,
            hausses = listOf(
                VariationAffichee("Riz", 9_600, 4, "HAUSSE"),
                VariationAffichee("Huile", 18_900, 16, "HAUSSE")
            ),
            baisses = listOf(
                VariationAffichee("Tomate", 4_800, 19, "BAISSE"),
                VariationAffichee("Poisson", 34_500, 3, "BAISSE")
            )
        )
    }
}
