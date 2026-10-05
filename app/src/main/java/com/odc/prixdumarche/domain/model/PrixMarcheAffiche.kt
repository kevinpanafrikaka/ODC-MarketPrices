package com.odc.prixdumarche.domain.model

data class PrixMarcheAffiche(
    val marcheId: Long,
    val marche: String,
    val commune: String,
    val date: String,
    val prixGnf: Long
)