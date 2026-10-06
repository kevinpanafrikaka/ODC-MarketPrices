package com.odc.prixdumarche.viewmodels

import com.odc.prixdumarche.data.local.entity.ReleveePrix
import com.odc.prixdumarche.domain.Tendance

/**
 * Tendance simple (direction uniquement, sans pourcentage) à partir de
 * relevés triés du plus récent au plus ancien. Partagée par les ViewModels
 * qui affichent une liste de produits (liste principale, favoris).
 */
fun calculerTendance(releves: List<ReleveePrix>): Tendance {
    if (releves.size < 2) {
        return Tendance.STABLE
    }

    val dernier = releves[0].prixGnf
    val precedent = releves[1].prixGnf

    return when {
        dernier > precedent -> Tendance.HAUSSE
        dernier < precedent -> Tendance.BAISSE
        else -> Tendance.STABLE
    }
}
