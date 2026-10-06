
package com.odc.prixdumarche.ui.util

import java.util.Locale

/**
 * Formatage d'affichage uniquement (aucune logique métier) :
 * respecte l'exigence du document "montants en GNF avec séparateur de milliers".
 */
fun Long.enGnf(): String =
    String.format(Locale.US, "%,d", this).replace(',', ' ') + " GNF"