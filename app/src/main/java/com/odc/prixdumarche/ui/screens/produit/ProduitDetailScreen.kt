package com.odc.prixdumarche.ui.screens.produit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.odc.prixdumarche.domain.model.ComparaisonAffichee
import com.odc.prixdumarche.domain.model.PointCourbe
import com.odc.prixdumarche.domain.model.PrixMarcheAffiche
import com.odc.prixdumarche.ui.theme.*
import com.odc.prixdumarche.ui.util.enGnf
import com.odc.prixdumarche.ui.util.produitImageRes
import com.odc.prixdumarche.ui.components.TendancePill

// TODO(données/logique) : remplacer par les vrais modèles du Repository / ViewModel

/**
 * Écran Détail d'un produit.
 * Ne contient aucune logique métier : historique, comparaison, min/max/moyenne
 * et tendance sont calculés en amont (ViewModel) et transmis tout prêts ici.
 * Le bandeau héros reprend le prix le plus récent de l'historique (dernier
 * point de la courbe 30 jours) plutôt que de recalculer quoi que ce soit ici.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProduitDetailScreen(
    produitId: Long,
    // TODO(logique) : remplacer ces valeurs par défaut par viewModel.uiState.collectAsState()
    nomProduit: String = "Chargement...",
    unite: String = "",
    tendance: String? = null, // "HAUSSE" | "BAISSE" | "STABLE" — TODO(logique) : aligner en enum partagé
    historique: List<PointCourbe> = emptyList(),
    prixParMarche: List<PrixMarcheAffiche> = emptyList(),
    comparaison: ComparaisonAffichee? = null,
    chargement: Boolean = false,
    onRetour: () -> Unit = {}
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(nomProduit, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
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
                BandeauHeros(
                    nomProduit = nomProduit,
                    unite = unite,
                    prixActuelGnf = historique.lastOrNull()?.prixGnf,
                    tendance = tendance
                )
            }

            item {
                Text("Évolution sur 30 jours", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            item {
                if (!chargement && historique.size < 2) {
                    Text("Aucune donnée pour le moment", fontSize = 14.sp)
                } else if (historique.size >= 2) {
                    CourbePrix(historique)
                }
            }

            item {
                Text(
                    if (comparaison != null) "Prix par marché · moyenne ${comparaison.moyenneGnf.enGnf()}" else "Prix par marché",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (!chargement && prixParMarche.isEmpty()) {
                item { Text("Aucune donnée pour le moment", fontSize = 14.sp) }
            } else if (prixParMarche.isNotEmpty()) {
                item {
                    val tries = prixParMarche.sortedBy { it.prixGnf }
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(Modifier.padding(horizontal = 16.dp)) {
                            tries.forEachIndexed { i, m ->
                                val teinte = when (m.marcheId) {
                                    comparaison?.moinsCherMarcheId -> BaisseFond
                                    comparaison?.plusCherMarcheId -> HausseFond
                                    else -> null
                                }
                                LigneMarche(m, teinte, dernier = i == tries.lastIndex)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Image du produit (fallback icône générique tant que la photo n'a pas été
 * ajoutée) avec le prix actuel et la tendance en surimpression.
 */
@Composable
private fun BandeauHeros(
    nomProduit: String,
    unite: String,
    prixActuelGnf: Long?,
    tendance: String?
) {
    val context = LocalContext.current
    val imageRes = remember(nomProduit) { produitImageRes(context, nomProduit) }

    Box(
        Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        if (imageRes != null) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = nomProduit,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(Modifier.fillMaxSize().background(StableFond), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.ShoppingBasket, contentDescription = null, tint = Stable, modifier = Modifier.size(56.dp))
            }
        }
        Box(
            Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f))))
                .padding(12.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(
                        prixActuelGnf?.enGnf() ?: "Aucun relevé",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (unite.isNotBlank()) {
                        Text("par $unite", fontSize = 13.sp, color = Color.White.copy(alpha = 0.9f))
                    }
                }
                if (tendance != null) TendancePill(tendance)
            }
        }
    }
}

@Composable
private fun LigneMarche(m: PrixMarcheAffiche, teinte: Color?, dernier: Boolean) {
    Row(
        Modifier
            .fillMaxWidth()
            .then(if (teinte != null) Modifier.background(teinte, RoundedCornerShape(8.dp)) else Modifier)
            .padding(horizontal = if (teinte != null) 10.dp else 0.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(m.marche, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(m.commune, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(m.date, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(m.prixGnf.enGnf(), fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
    if (!dernier) HorizontalDivider(color = MaterialTheme.colorScheme.outline)
}

/**
 * Dessine la courbe à partir de points déjà calculés (jour relatif, prix).
 * Ne calcule rien : min/max/positionnement viennent uniquement des données
 * reçues. Repères visuels ajoutés par rapport à une simple ligne : lignes de
 * niveau horizontales, remplissage dégradé, dernier point mis en évidence
 * avec un repère pointillé, et bornes de la période en texte.
 */
@Composable
private fun CourbePrix(points: List<PointCourbe>) {
    val couleur = MaterialTheme.colorScheme.primary
    val couleurTexte = MaterialTheme.colorScheme.onSurfaceVariant
    val textMeasurer = rememberTextMeasurer()
    val styleAxe = remember(couleurTexte) { TextStyle(fontSize = 11.sp, color = couleurTexte) }
    val min = points.minOf { it.prixGnf }
    val max = points.maxOf { it.prixGnf }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Min : ${min.enGnf()} · Max : ${max.enGnf()}", fontSize = 14.sp, color = couleurTexte)
            Spacer(Modifier.height(12.dp))
            Canvas(Modifier.fillMaxWidth().height(180.dp)) {
                val zoneAxe = 20.dp.toPx()
                val bas = size.height - zoneAxe
                val ecartPrix = (max - min).coerceAtLeast(1L).toFloat()
                val ecartJour = (points.last().jour - points.first().jour).coerceAtLeast(1).toFloat()

                // Lignes de niveau (haut / milieu / bas) pour donner une échelle visuelle.
                listOf(0f, 0.5f, 1f).forEach { f ->
                    val y = bas - f * bas
                    drawLine(
                        couleurTexte.copy(alpha = 0.15f),
                        Offset(0f, y),
                        Offset(size.width, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                val coords = points.map {
                    Offset(
                        x = (it.jour - points.first().jour) / ecartJour * size.width,
                        y = bas - (it.prixGnf - min) / ecartPrix * bas
                    )
                }
                val ligne = Path().apply {
                    moveTo(coords.first().x, coords.first().y)
                    coords.drop(1).forEach { lineTo(it.x, it.y) }
                }
                val aire = Path().apply {
                    addPath(ligne)
                    lineTo(coords.last().x, bas)
                    lineTo(coords.first().x, bas)
                    close()
                }
                drawPath(
                    aire,
                    brush = Brush.verticalGradient(
                        colors = listOf(couleur.copy(alpha = 0.28f), Color.Transparent),
                        startY = 0f,
                        endY = bas
                    )
                )
                drawPath(ligne, couleur, style = Stroke(width = 4f))

                coords.forEachIndexed { i, p ->
                    if (i == coords.lastIndex) {
                        drawLine(
                            couleur.copy(alpha = 0.4f),
                            Offset(p.x, p.y),
                            Offset(p.x, bas),
                            strokeWidth = 2f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f))
                        )
                        drawCircle(couleur, radius = 9f, center = p)
                        drawCircle(Color.White, radius = 4f, center = p)
                    } else {
                        drawCircle(couleur.copy(alpha = 0.5f), radius = 4f, center = p)
                    }
                }

                val nbJours = points.last().jour - points.first().jour
                val texteDebut = textMeasurer.measure("Il y a $nbJours j", styleAxe)
                drawText(texteDebut, topLeft = Offset(0f, size.height - texteDebut.size.height))
                val texteFin = textMeasurer.measure("Aujourd'hui", styleAxe)
                drawText(texteFin, topLeft = Offset(size.width - texteFin.size.width, size.height - texteFin.size.height))
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
                PrixMarcheAffiche(1, "Madina", "Matam", "26/09/2026", 9_500),
                PrixMarcheAffiche(2, "Niger", "Kaloum", "27/09/2026", 9_000),
                PrixMarcheAffiche(3, "Matoto", "Matoto", "25/09/2026", 10_200)
            ),
            comparaison = ComparaisonAffichee(
                moinsCherMarcheId = 2, moinsCherMarche = "Niger", moinsCherCommune = "Kaloum", moinsCherPrixGnf = 9_000,
                plusCherMarcheId = 3, plusCherMarche = "Matoto", plusCherCommune = "Matoto", plusCherPrixGnf = 10_200,
                moyenneGnf = 9_567
            )
        )
    }
}
