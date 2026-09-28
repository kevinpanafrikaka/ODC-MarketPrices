package com.odc.prixdumarche.ui.screens.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

// TODO(Responsable interface + Responsable logique métier) :
// produits en hausse/baisse cette semaine + panier moyen.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableauBordScreen() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Tableau de bord") }) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("TODO : tableau de bord")
        }
    }
}
