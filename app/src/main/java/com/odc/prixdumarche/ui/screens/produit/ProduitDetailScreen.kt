package com.odc.prixdumarche.ui.screens.produit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.odc.prixdumarche.ui.theme.*
import java.util.Locale

// TODO(données/logique) : remplacer par les vrais modèles du Repository / ViewModel
data class PointCourbe(val jour: Int, val prixGnf: Long)
data class PrixMarcheAffiche(val marche: String, val date: String, val prixGnf: Long)
data class ComparaisonAffichee(
    val moinsCherMarche: String,
    val moinsCherPrixGnf: Long,
    val plusCherMarche: String,
    val plusCherPrixGnf: Long,
    val moyenneGnf: Long
)

private fun Long.enGnf(): String =
    String.format(Locale.US, "%,d", this).replace(',', ' ') + " GNF"

/**
 * Écran Détail d'un produit.
 * Ne contient aucune logique métier : historique, comparaison, min/max/moyenne
 * et tendance sont calculés en amont (ViewModel) et transmis tout prêts ici.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProduitDetailScreen(
    produitId: Long,
    // TODO(logique) : remplacer ces valeurs par défaut par viewModel.uiState.collectAsState()
    nomProduit: String = "Chargement...",
    unite: String = "",
    tendance: String? = null, // "HAUSSE" | "BAISSE" | "STABLE" — TODO(logique) : aligner en enum partagé avec ProduitListScreen
    historique: List<PointCourbe> = emptyList(),
    prixParMarche: List<PrixMarcheAffiche> = emptyList(),
    comparaison: ComparaisonAffichee? = null,
    chargement: Boolean = false,
    onRetour: () -> Unit = {} // TODO(chef de projet) : à brancher sur navController.popBackStack() dans NavGraph.kt
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(nomProduit, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        if (unite.isNotBlank()) {
                            Text("Prix en GNF par $unite", fontSize = 14.sp)
                        }
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
        LazyColumn(
            Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Évolution sur 30 jours", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    if (tendance != null) PastilleTendance(tendance)
                }
            }

            item {
                if (!chargement && historique.size < 2) {
                    Text("Aucune donnée pour le moment", fontSize = 14.sp)
                } else if (historique.size >= 2) {
                    CourbePrix(historique)
                }
            }

            if (comparaison != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Comparaison entre marchés", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                TuileComparaison("Le moins cher", comparaison.moinsCherMarche, comparaison.moinsCherPrixGnf.enGnf(), Baisse, BaisseFond, Modifier.weight(1f))
                                TuileComparaison("Le plus cher", comparaison.plusCherMarche, comparaison.plusCherPrixGnf.enGnf(), Hausse, HausseFond, Modifier.weight(1f))
                            }
                            Text("Moyenne : ${comparaison.moyenneGnf.enGnf()}", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            item { Text("Prix par marché", fontSize = 18.sp, fontWeight = FontWeight.Bold) }

            if (!chargement && prixParMarche.isEmpty()) {
                item { Text("Aucune donnée pour le moment", fontSize = 14.sp) }
            } else if (prixParMarche.isNotEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(Modifier.padding(horizontal = 16.dp)) {
                            prixParMarche.forEachIndexed { i, m ->
                                LigneMarche(m, dernier = i == prixParMarche.lastIndex)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PastilleTendance(tendance: String) {
    val (texte, couleur, fond) = when (tendance) {
        "HAUSSE" -> Triple("↑ Hausse", Hausse, HausseFond)
        "BAISSE" -> Triple("↓ Baisse", Baisse, BaisseFond)
        else -> Triple("→ Stable", Stable, StableFond)
    }
    Surface(color = fond, shape = RoundedCornerShape(999.dp)) {
        Text(texte, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = couleur, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
    }
}

@Composable
private fun TuileComparaison(label: String, marche: String, prix: String, texteColor: Color, fond: Color, modifier: Modifier = Modifier) {
    Column(
        modifier
            .background(fond, shape = RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text(label, fontSize = 14.sp, color = texteColor)
        Text(marche, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = texteColor)
        Text(prix, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = texteColor)
    }
}

@Composable
private fun LigneMarche(m: PrixMarcheAffiche, dernier: Boolean) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(m.marche, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(m.date, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(m.prixGnf.enGnf(), fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
    if (!dernier) HorizontalDivider(color = MaterialTheme.colorScheme.outline)
}

/**
 * Dessine la courbe à partir de points déjà calculés (jour relatif, prix).
 * Ne calcule rien : min/max/positionnement viennent uniquement des données reçues.
 */
@Composable
private fun CourbePrix(points: List<PointCourbe>) {
    val couleur = MaterialTheme.colorScheme.primary
    val min = points.minOf { it.prixGnf }
    val max = points.maxOf { it.prixGnf }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Min : ${min.enGnf()} · Max : ${max.enGnf()}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Canvas(Modifier.fillMaxWidth().height(140.dp)) {
                val ecartPrix = (max - min).coerceAtLeast(1L).toFloat()
                val ecartJour = (points.last().jour - points.first().jour).coerceAtLeast(1).toFloat()
                val coords = points.map {
                    Offset(
                        x = (it.jour - points.first().jour) / ecartJour * size.width,
                        y = size.height - (it.prixGnf - min) / ecartPrix * size.height
                    )
                }
                val ligne = Path().apply {
                    moveTo(coords.first().x, coords.first().y)
                    coords.drop(1).forEach { lineTo(it.x, it.y) }
                }
                val aire = Path().apply {
                    addPath(ligne)
                    lineTo(coords.last().x, size.height)
                    lineTo(coords.first().x, size.height)
                    close()
                }
                drawPath(aire, couleur.copy(alpha = 0.12f))
                drawPath(ligne, couleur, style = Stroke(width = 4f))
                coords.forEachIndexed { i, p ->
                    val dernier = i == coords.lastIndex
                    drawCircle(couleur, radius = if (dernier) 7f else 5f, center = p)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProduitDetailScreenPreview() {
    PrixDuMarcheTheme {
        ProduitDetailScreen(
            produitId = 1L,
            nomProduit = "Riz",
            unite = "kg",
            tendance = "HAUSSE",
            historique = listOf(
                PointCourbe(1, 9000), PointCourbe(6, 9200), PointCourbe(12, 9100),
                PointCourbe(18, 9400), PointCourbe(24, 9300), PointCourbe(30, 9600)
            ),
            prixParMarche = listOf(
                PrixMarcheAffiche("Madina", "26/09/2026", 9_500),
                PrixMarcheAffiche("Niger", "27/09/2026", 9_000),
                PrixMarcheAffiche("Matoto", "25/09/2026", 10_200)
            ),
            comparaison = ComparaisonAffichee("Niger", 9_000, "Matoto", 10_200, 9_567)
        )
    }
}