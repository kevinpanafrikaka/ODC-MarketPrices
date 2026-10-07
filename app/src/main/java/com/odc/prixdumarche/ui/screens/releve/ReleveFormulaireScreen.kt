package com.odc.prixdumarche.ui.screens.releve

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.odc.prixdumarche.domain.model.MarcheChoix
import com.odc.prixdumarche.domain.model.OptionChoix
import com.odc.prixdumarche.ui.theme.PrixDuMarcheTheme

// TODO(données/logique) : remplacer par les vrais modèles du Repository

/**
 * Écran Formulaire de relevé.
 * Ne contient aucune logique métier : la validation (prix > 0, champs
 * obligatoires, format de date) est faite en amont (ViewModel) et les
 * messages d'erreur sont reçus tout prêts ici.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReleveFormulaireScreen(
    // TODO(logique) : remplacer ces valeurs par défaut par viewModel.uiState.collectAsState()
    produits: List<OptionChoix> = PRODUITS_FAKE,
    marches: List<MarcheChoix> = MARCHES_FAKE,
    produitChoisi: OptionChoix? = null,
    marcheChoisi: MarcheChoix? = null,
    prixTexte: String = "",
    dateTexte: String = "",
    erreurProduit: String? = null,
    erreurMarche: String? = null,
    erreurPrix: String? = null,
    erreurDate: String? = null,
    erreurGenerale: String? = null,
    enregistrementReussi: Boolean = false,
    onProduitChoisi: (OptionChoix) -> Unit = {},
    onMarcheChoisi: (MarcheChoix) -> Unit = {},
    onPrixChange: (String) -> Unit = {},
    onDateChange: (String) -> Unit = {},
    onEnregistrer: () -> Unit = {},
    onRetour: () -> Unit = {}
) {
    LaunchedEffect(enregistrementReussi) {
        if (enregistrementReussi) onRetour()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Nouveau relevé", fontSize = 20.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        Text("Partagez le prix relevé au marché", fontSize = 14.sp)
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
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (erreurGenerale != null) {
                Text(
                    "⚠ $erreurGenerale",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.error
                )
            }
            SelecteurProduit(
                choix = produitChoisi?.label,
                options = produits,
                erreur = erreurProduit,
                onChoisir = onProduitChoisi
            )
            SelecteurMarche(
                choix = marcheChoisi?.let { "${it.nom} (${it.commune})" },
                options = marches,
                erreur = erreurMarche,
                onChoisir = onMarcheChoisi
            )

            ChampTexte(
                label = "Prix (GNF)",
                valeur = prixTexte,
                placeholder = "Saisissez un prix",
                erreur = erreurPrix,
                clavierNumerique = true,
                onValeurChange = onPrixChange
            )

            ChampTexte(
                label = "Date",
                valeur = dateTexte,
                placeholder = "jj/mm/aaaa",
                erreur = erreurDate,
                clavierNumerique = false,
                onValeurChange = onDateChange
            )

            Button(
                onClick = onEnregistrer,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Enregistrer le relevé", fontSize = 16.sp)
            }

            OutlinedButton(onClick = onRetour, modifier = Modifier.fillMaxWidth()) {
                Text("Annuler", fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun SelecteurProduit(
    choix: String?,
    options: List<OptionChoix>,
    erreur: String?,
    onChoisir: (OptionChoix) -> Unit
) {
    var ouvert by remember { mutableStateOf(false) }
    Column {
        Text("Produit", fontSize = 14.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        Box {
            OutlinedButton(
                onClick = { ouvert = true },
                modifier = Modifier.fillMaxWidth(),
                colors = if (erreur != null) {
                    ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                } else {
                    ButtonDefaults.outlinedButtonColors()
                }
            ) {
                Text(choix ?: "Choisir...", fontSize = 16.sp, modifier = Modifier.fillMaxWidth())
            }
            DropdownMenu(expanded = ouvert, onDismissRequest = { ouvert = false }) {
                if (options.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("Aucune donnée pour le moment", fontSize = 14.sp) },
                        onClick = { ouvert = false },
                        enabled = false
                    )
                } else {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.label, fontSize = 16.sp) },
                            onClick = { onChoisir(option); ouvert = false }
                        )
                    }
                }
            }
        }
        if (erreur != null) {
            Text("⚠ $erreur", fontSize = 14.sp, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun SelecteurMarche(
    choix: String?,
    options: List<MarcheChoix>,
    erreur: String?,
    onChoisir: (MarcheChoix) -> Unit
) {
    var ouvert by remember { mutableStateOf(false) }
    Column {
        Text("Marché", fontSize = 14.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        Box {
            OutlinedButton(
                onClick = { ouvert = true },
                modifier = Modifier.fillMaxWidth(),
                colors = if (erreur != null) {
                    ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                } else {
                    ButtonDefaults.outlinedButtonColors()
                }
            ) {
                Text(choix ?: "Choisir...", fontSize = 16.sp, modifier = Modifier.fillMaxWidth())
            }
            DropdownMenu(expanded = ouvert, onDismissRequest = { ouvert = false }) {
                if (options.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("Aucune donnée pour le moment", fontSize = 14.sp) },
                        onClick = { ouvert = false },
                        enabled = false
                    )
                } else {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text("${option.nom} (${option.commune})", fontSize = 16.sp) },
                            onClick = { onChoisir(option); ouvert = false }
                        )
                    }
                }
            }
        }
        if (erreur != null) {
            Text("⚠ $erreur", fontSize = 14.sp, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun ChampTexte(
    label: String,
    valeur: String,
    placeholder: String,
    erreur: String?,
    clavierNumerique: Boolean,
    onValeurChange: (String) -> Unit
) {
    Column {
        Text(label, fontSize = 14.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = valeur,
            onValueChange = onValeurChange,
            placeholder = { Text(placeholder, fontSize = 16.sp) },
            isError = erreur != null,
            keyboardOptions = if (clavierNumerique) {
                KeyboardOptions(keyboardType = KeyboardType.Number)
            } else {
                KeyboardOptions.Default
            },
            modifier = Modifier.fillMaxWidth(),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp)
        )
        if (erreur != null) {
            Text("⚠ $erreur", fontSize = 14.sp, color = MaterialTheme.colorScheme.error)
        }
    }
}

// ⚠️ TODO(logique) : valeurs par défaut temporaires, à retirer une fois le ViewModel branché
private val PRODUITS_FAKE = listOf(
    OptionChoix(1, "Riz"), OptionChoix(2, "Huile"), OptionChoix(3, "Oignon")
)
private val MARCHES_FAKE = listOf(
    MarcheChoix(1, "Madina", "Matam"),
    MarcheChoix(2, "Niger", "Kaloum"),
    MarcheChoix(3, "Matoto", "Matoto")
)

@Preview(showBackground = true)
@Composable
fun ReleveFormulaireScreenPreview() {
    PrixDuMarcheTheme {
        ReleveFormulaireScreen(
            produitChoisi = OptionChoix(1, "Riz"),
            prixTexte = "9500",
            dateTexte = "28/09/2026",
            erreurMarche = "Choisissez un marché"
        )
    }
}