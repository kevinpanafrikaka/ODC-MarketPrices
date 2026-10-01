package com.odc.prixdumarche.ui.screens.produit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.odc.prixdumarche.ui.theme.*
import com.odc.prixdumarche.ui.util.enGnf
import com.odc.prixdumarche.ui.components.TendancePill

// TODO(données) : remplacer par les vrais modèles du Repository
data class ProduitAffiche(
    val id: Long,
    val nom: String,
    val unite: String,
    val categorie: String,
    val dernierPrixGnf: Long?,
    val tendance: String // "HAUSSE" | "BAISSE" | "STABLE" — TODO(logique) : idéalement un enum Tendance partagé
)

data class MarcheAffiche(
    val id: Long,
    val nom: String,
    val commune: String
)

/**
 * Écran Liste des produits.
 * Ne contient aucune logique métier : le filtrage, le calcul de tendance et le
 * chargement sont faits en amont (ViewModel) et transmis tout prêts ici.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProduitListScreen(
    onProduitClick: (Long) -> Unit,
    // TODO(logique) : remplacer ces valeurs par défaut par viewModel.uiState.collectAsState()
    produits: List<ProduitAffiche> = PRODUITS_FAKE,
    categories: List<String> = CATEGORIES_FAKE,
    marches: List<MarcheAffiche> = MARCHES_FAKE,
    categorieFiltre: String = "Toutes",
    marcheFiltre: Long? = null, // null = "Tous marchés"
    chargement: Boolean = false,
    onCategorieChoisie: (String) -> Unit = {},
    onMarcheChoisi: (Long?) -> Unit = {},
    onAjouterReleve: () -> Unit = {},
    onTableauDeBord: () -> Unit = {}
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Prix du Marché", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                    Surface(
                        onClick = onTableauDeBord,
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(999.dp)
                    ) {
                        Text(
                            "Tableau de bord",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
                Text("Comparez les prix à Conakry", fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimary)
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
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { c ->
                    ChipFiltre(c, c == categorieFiltre) { onCategorieChoisie(c) }
                }
            }
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    ChipFiltre("Tous marchés", marcheFiltre == null) { onMarcheChoisi(null) }
                }
                items(marches, key = { it.id }) { m ->
                    ChipFiltre("${m.nom} (${m.commune})", m.id == marcheFiltre) { onMarcheChoisi(m.id) }
                }
            }

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
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(produits, key = { it.id }) { p ->
                        CarteProduit(p) { onProduitClick(p.id) }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChipFiltre(label: String, actif: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = if (actif) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (actif) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(999.dp)
    ) {
        Text(
            label,
            fontSize = 14.sp,
            fontWeight = if (actif) FontWeight.SemiBold else FontWeight.Normal,
            color = if (actif) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun CarteProduit(p: ProduitAffiche, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(44.dp).background(StableFond, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(p.nom.first().toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Stable)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(p.nom, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("par ${p.unite} · ${p.categorie}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(p.dernierPrixGnf?.enGnf() ?: "Aucun relevé", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                TendancePill(p.tendance)
            }
        }
    }
}

// Données uniquement pour l'aperçu (@Preview) — jamais utilisées dans l'app réelle une fois le ViewModel branché
// ⚠️ TODO(logique) : valeurs par défaut temporaires, à retirer une fois le ViewModel branché
private val CATEGORIES_FAKE = listOf("Toutes", "Céréales", "Épicerie", "Légumes", "Poissons")
private val MARCHES_FAKE = listOf(
    MarcheAffiche(1, "Madina", "Matam"),
    MarcheAffiche(2, "Niger", "Kaloum"),
    MarcheAffiche(3, "Matoto", "Matoto")
)
private val PRODUITS_FAKE = listOf(
    ProduitAffiche(1, "Riz", "kg", "Céréales", 9_600, "HAUSSE"),
    ProduitAffiche(2, "Huile", "litre", "Épicerie", 18_900, "HAUSSE"),
    ProduitAffiche(3, "Oignon", "kg", "Légumes", 7_300, "STABLE"),
    ProduitAffiche(4, "Tomate", "tas", "Légumes", 4_800, "BAISSE"),
    ProduitAffiche(5, "Poisson", "kg", "Poissons", 34_500, "BAISSE")
)

@Preview(showBackground = true)
@Composable
fun ProduitListScreenPreview() {
    PrixDuMarcheTheme {
        ProduitListScreen(onProduitClick = {})
    }
}