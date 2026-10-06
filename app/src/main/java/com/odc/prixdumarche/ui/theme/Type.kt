package com.odc.prixdumarche.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Convention de qualité du cahier des charges : taille de texte minimale 14sp.
val Typography = Typography(
    titleLarge = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold),
    titleMedium = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
    titleSmall = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),

    bodyLarge = TextStyle(fontSize = 16.sp),
    bodyMedium = TextStyle(fontSize = 14.sp),
    bodySmall = TextStyle(fontSize = 14.sp),

    labelLarge = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
    labelMedium = TextStyle(fontSize = 14.sp),
    labelSmall = TextStyle(fontSize = 14.sp)
)