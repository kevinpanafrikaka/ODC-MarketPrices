# Prix du Marché

Application Android (Kotlin) réalisée dans le cadre de la formation en
développement mobile de l'Orange Digital Center (Conakry). Projet fil rouge
n°2 : comparer les prix des denrées entre les marchés de Conakry.

## Le problème

Les prix des denrées (riz, huile, oignon, poisson, tomate...) varient
fortement d'un marché à l'autre et d'un jour à l'autre, sans référence facile
à consulter pour les ménagères, commerçants et étudiants.

## Le MVP

- Liste des produits avec dernier prix connu et tendance (hausse/baisse/stable)
- Saisie communautaire d'un relevé de prix (produit, marché, prix GNF, date)
- Historique d'un produit avec courbe d'évolution sur 30 jours
- Comparaison d'un produit entre marchés (le moins cher, le plus cher, la moyenne)
- Filtres par catégorie de produit et par marché

**Exigence non négociable : l'application est offline-first.** Toutes les
fonctionnalités doivent marcher en mode avion, avec les données persistées
dans Room. Aucun appel réseau n'est obligatoire pour utiliser le MVP.

## Équipe et rôles

| Rôle | Responsabilités | Branche |
|---|---|---|
| Chef de projet et intégration | Planning, dépôt Git, fusion des branches, résolution des conflits, APK final | `main` |
| Responsable données | Entités Room, migrations, DAO, requêtes, clés étrangères et index | `feature/data` |
| Responsable interface | Maquettes, écrans, navigation, thème, accessibilité, états vides | `feature/ui` |
| Responsable logique métier | ViewModels, règles métier, validations, exposition d'état (StateFlow) | `feature/logique` |
| Responsable qualité, tests et documentation | Tests, recette mode avion, suivi des bugs, dossier technique | `feature/qualite` |

## Architecture (MVVM imposé)

```
UI (Compose)  →  ViewModel (StateFlow)  →  Repository (interface)  →  DAO / Room
```

- **Aucune logique métier dans l'interface.** Les Composables affichent un état, rien de plus.
- **Le Repository est toujours défini par une interface**, implémentée séparément
  (ex. `interface ProduitRepository` + `class ProduitRepositoryImpl`). Ça permet de
  remplacer un jour les données Room par un vrai appel API sans toucher l'UI.
- **Convention de packages** (le code métier n'est pas encore écrit, c'est
  le rôle de chaque responsable de le remplir) :

```
com.odc.prixdumarche/
├── MainActivity.kt
├── ui/
│   ├── theme/            → thème Compose (déjà en place)
│   ├── navigation/       → routes + NavHost (déjà en place, écrans stubs)
│   └── screens/
│       ├── produit/      → liste + détail produit
│       ├── releve/       → formulaire de saisie
│       └── dashboard/    → tableau de bord
├── data/
│   ├── local/
│   │   ├── entity/       → @Entity Room (Marche, Produit, ReleveePrix)
│   │   └── dao/          → @Dao Room
│   └── repository/       → interfaces + implémentations
└── viewmodel/            → un ViewModel par écran, expose un StateFlow
```

Le squelette Gradle, les dépendances et les 4 écrans de navigation (encore en
`TODO`) sont déjà en place sur `main` pour que chaque responsable puisse
travailler dans sa branche sans attendre les autres.

### Modèle de données Room (à affiner par le Responsable données)

Proposition de base du cahier des charges — à discuter avant implémentation,
notamment sur les clés étrangères et les index :

- `Marche` : id, nom, commune
- `Produit` : id, nom, unite (kg, litre, tas), categorie
- `ReleveePrix` : id, produitId, marcheId, prixGnf, date

## Convention de qualité

- Interface en français, textes lisibles (14sp minimum), bon contraste.
- Montants affichés en GNF avec séparateur de milliers (ex. `150 000 GNF`).
- États vides gérés (« Aucune donnée pour le moment ») et erreurs de saisie
  avec messages clairs.
- Aucun mot de passe ni donnée personnelle réelle dans le dépôt Git.

## Workflow Git

- `main` est la branche d'intégration, protégée. Elle contient toujours une
  version qui compile.
- Chaque responsable travaille sur sa branche `feature/<domaine>` et ouvre
  une Pull Request vers `main` quand son travail est prêt à être relu.
- Le chef de projet relit, fusionne, et résout les conflits d'intégration.
- Commits clairs et réguliers : chacun des 4-5 membres doit avoir des
  commits visibles dans l'historique (critère d'évaluation).

## Lancer le projet

Prérequis : Android Studio récent, JDK 17+.

```bash
git clone <url-du-depot>
cd ODC-MarketPrices
./gradlew assembleDebug
```

> Le premier `./gradlew` synchronise automatiquement les composants du SDK
> Android manquants (compileSdk 35) via Android Studio — une connexion
> internet est nécessaire pour cette étape uniquement, pas pour utiliser
> l'application ensuite.

## Livrables attendus

- Application installable (APK) testée en mode avion
- Code source sur ce dépôt, avec commits des 4-5 membres
- Dossier technique (3 à 5 pages) : problème, fonctionnalités, schéma des
  entités Room, schéma d'architecture MVVM
- Démonstration orale de 15 minutes (3 min présentation, 7 min démo mode
  avion, 5 min questions)
