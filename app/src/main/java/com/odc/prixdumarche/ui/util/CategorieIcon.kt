package com.odc.prixdumarche.ui.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Grain
import androidx.compose.material.icons.outlined.LocalFlorist
import androidx.compose.material.icons.outlined.Nature
import androidx.compose.material.icons.outlined.SetMeal
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Icône associée à chaque catégorie de produit, pour un repérage visuel plus
 * rapide que le cercle-lettre générique. Approximations volontaires (pas
 * d'icône "igname" ou "arachide" dans Material Icons) : l'objectif est de
 * distinguer les catégories au premier coup d'œil, pas une exactitude littérale.
 */
fun categorieIcon(categorie: String): ImageVector = when (categorie) {
    "Toutes" -> Icons.Outlined.Apps
    "Céréales" -> Icons.Outlined.Grain
    "Tubercules" -> Icons.Outlined.Spa
    "Huiles" -> Icons.Outlined.WaterDrop
    "Légumineuses" -> Icons.Outlined.Nature
    "Légumes" -> Icons.Outlined.Eco
    "Poissons" -> Icons.Outlined.SetMeal
    "Fruits" -> Icons.Outlined.LocalFlorist
    else -> Icons.Outlined.ShoppingBasket
}
