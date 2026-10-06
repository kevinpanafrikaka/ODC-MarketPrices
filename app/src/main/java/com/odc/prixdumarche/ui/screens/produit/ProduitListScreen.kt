package com.odc.prixdumarche.ui.screens.produit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.odc.prixdumarche.domain.model.ProduitAffiche
import com.odc.prixdumarche.ui.components.CarteProduitGrille
import com.odc.prixdumarche.ui.theme.*
import com.odc.prixdumarche.ui.util.categorieIcon

/**
 * Écran Liste des produits (onglet Accueil).
 * Ne contient aucune logique métier : le filtrage, le calcul de tendance et le
 * chargement sont faits en amont (ViewModel) et transmis tout prêts ici.
 * La comparaison entre marchés vit sur l'écran détail produit, pas ici.
 */
@Composable
fun ProduitListScreen(
    onProduitClick: (Long) -> Unit,
    // TODO(logique) : remplacer ces valeurs par défaut par viewModel.uiState.collectAsState()
    produits: List<ProduitAffiche> = PRODUITS_FAKE,
    categories: List<String> = CATEGORIES_FAKE,
    categorieFiltre: String = "Toutes",
    chargement: Boolean = false,
    onCategorieChoisie: (String) -> Unit = {},
    onToggleFavori: (Long, Boolean) -> Unit = { _, _ -> },
    onAjouterReleve: () -> Unit = {}
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .statusBarsPadding()
            ) {
                Text(
                    "eMarket",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { c ->
                        ChipFiltreCategorie(c, c == categorieFiltre) { onCategorieChoisie(c) }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAjouterReleve,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Text("+ Ajouter un relevé", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            Text(
                "${produits.size} produits",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (!chargement && produits.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aucune donnée pour le moment", fontSize = 16.sp)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(produits, key = { it.id }) { p ->
                        CarteProduitGrille(
                            p = p,
                            onClick = { onProduitClick(p.id) },
                            onToggleFavori = { favori -> onToggleFavori(p.id, favori) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Chip de catégorie affichée sur le bandeau vert du header : la variante
 * "sélectionnée" s'inverse en blanc/crème pour rester lisible sur ce fond,
 * la variante inactive reste un simple contour blanc translucide.
 */
@Composable
private fun ChipFiltreCategorie(label: String, actif: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = if (actif) MaterialTheme.colorScheme.surface else Color.Transparent,
        border = BorderStroke(1.dp, if (actif) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(999.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(
                categorieIcon(label),
                contentDescription = null,
                tint = if (actif) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(15.dp)
            )
            Spacer(Modifier.width(5.dp))
            Text(
                label,
                fontSize = 13.sp,
                fontWeight = if (actif) FontWeight.SemiBold else FontWeight.Normal,
                color = if (actif) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

// Données uniquement pour l'aperçu (@Preview) — jamais utilisées dans l'app réelle une fois le ViewModel branché
private val CATEGORIES_FAKE = listOf("Toutes", "Céréales", "Fruits", "Légumes", "Poissons")
private val PRODUITS_FAKE = listOf(
    ProduitAffiche(1, "Riz local", "kg", "Céréales", 9_600, "HAUSSE", listOf(8_900, 9_100, 9_300, 9_600)),
    ProduitAffiche(2, "Huile de palme", "litre", "Huiles", 18_900, "HAUSSE", listOf(17_500, 18_000, 18_900)),
    ProduitAffiche(3, "Oignon", "kg", "Légumes", 7_300, "STABLE", listOf(7_300, 7_300, 7_300), estFavori = true),
    ProduitAffiche(4, "Tomate", "tas", "Légumes", 4_800, "BAISSE", listOf(5_900, 5_200, 4_800)),
    ProduitAffiche(5, "Poisson fumé", "kg", "Poissons", 34_500, "BAISSE", listOf(37_000, 35_800, 34_500))
)

@Preview(showBackground = true)
@Composable
fun ProduitListScreenPreview() {
    PrixDuMarcheTheme {
        ProduitListScreen(onProduitClick = {})
    }
}
