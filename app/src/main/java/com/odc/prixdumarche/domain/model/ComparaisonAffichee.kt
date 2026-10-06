package com.odc.prixdumarche.domain.model

data class ComparaisonAffichee(
    val moinsCherMarcheId: Long,
    val moinsCherMarche: String,
    val moinsCherCommune: String,
    val moinsCherPrixGnf: Long,
    val plusCherMarcheId: Long,
    val plusCherMarche: String,
    val plusCherCommune: String,
    val plusCherPrixGnf: Long,
    val moyenneGnf: Long
)