package com.odc.prixdumarche.domain.model

data class VariationAffichee(
    val nomProduit: String,
    val prixGnf: Long,
    val pourcentage: Int,
    val tendance: String
)