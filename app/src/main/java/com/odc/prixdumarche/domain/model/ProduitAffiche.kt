package com.odc.prixdumarche.domain.model

data class ProduitAffiche(
    val id: Long,
    val nom: String,
    val unite: String,
    val categorie: String,
    val dernierPrixGnf: Long?,
    val tendance: String // "HAUSSE" | "BAISSE" | "STABLE" — TODO(logique) : idéalement un enum Tendance partagé
)
