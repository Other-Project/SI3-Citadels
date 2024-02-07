# Projet Citadels - Team C

**[Lassauniere Théo](https://github.com/theoLassauniere), [Galli Evan](https://github.com/06Games),
[Lubrat Jilian](https://github.com/LubratJilian), [Michelozzi Antoine-Marie](https://github.com/mantoniu)**  
Polytech Nice-Sophia - SI3 PS5

------------------------

# Avancement
## Le jeu

### Les règles du jeu Citadelles implémentées

Citadelles est un jeu de société composé de cartes personnage ayant toute une capacité différente
et de cartes quartier de couleurs différentes (rouge, jaune, bleu, vert et violet),
les cartes violettes ont, comme les personnages, des capacités propres.

Un tour se déroule de la façon suivante :

* Chaque joueur en partant de celui qui a la couronne, choisit à tour de rôle son personnage.
* Puis chaque personnage est appelé dans un ordre défini.
* La personne ayant choisi le personnage peut alors réaliser différentes actions :
    * piocher 2 cartes ou prendre 2 pièces d'or
    * construire un quartier : pour construire un quartier, il faut avoir assez de pièces pour pouvoir payer le coût
      indiqué sur la carte.
    * réaliser l'action spéciale associée à son personnage ou à un de ses quartiers violets déjà construit.

La partie se termine quand une personne a construit 8 quartiers (pour une partie de 4 à 7 joueurs) ou 10 quartiers (pour
une partie à 3 joueurs) et que le tour de jeu est terminé. Puis, on décompte pour chaque joueur son nombre de
points, qui est égal à la somme des points donnés par chacun des quartiers et des différents bonus.

Les bonus possibles sont :

* +4 points pour le premier joueur ayant construit le dernier quartier nécessaire pour finir la partie
* +3 points si le joueur construit au moins un quartier de chaque couleur
* +2 points pour tous les autres joueurs qui ont construit le nombre de quartiers nécessaire pour terminer la partie

Lors de la phase de choix des personages d'un tour de jeu, les règles varient en fonction du nombre de joueurs :

* A 3 joueurs : 2 cartes personnages sont écartées faces visibles et deux autres face cachée, l'assassin est exclus des
  personnages
* A 4 joueurs : 2 cartes personnages sont écartées faces visibles et deux autres faces cachées
* A 5 joueurs : 1 carte personnages sont écartées faces visibles et deux autres faces cachées
* A 6 et 7 joueurs : aucunes cartes personnages sont écartées faces visibles et deux autres faces cachées

(Dans tous les cas le roi ne peut pas être parmi les cartes placées faces visibles, mais il peut être parmi les cartes
placées faces cachées)

### Les subtilités

* Si personne ne prend le Roi, alors la couronne ne tourne pas entre les joueurs et reste en possession de la personne
  qui détenait la couronne au tour précédent
* Il n'y a pas de maximum de pièces, il est possible pour chacun des joueurs d'avoir une infinité de pièces
* Un joueur qui a construit le nombre de quartiers nécessaire pour finir la partie peut, s'il en a la capacité,
  construire des quartiers jusqu'à la fin de son tour. (Par exemple avec l'Architecte)

## Les Bots

Différents types de bots ont été implémenté, il y en a 6 et ils représentent chacun une stratégie différente.

### Bot

&nbsp; &nbsp; &nbsp; C'est l'implémentation de base, dont dérive d'autres types de bot. Il prend des décisions fondées
sur des critères de rentabilités déterminés mathématiquement.

### Discreet Bot

&nbsp; &nbsp; &nbsp; Basé sur `Bot`, ce bot a pour but de ne pas se faire repérer par les autres joueurs et essaye de
rester
discret afin de gagner. Dès qu'il est en capacité de terminer la partie, il prend l'opportunité. Il construit ni des
quartiers rapportant trop de points pour éviter de se faire attaquer par les autres joueurs, ni ceux qui n'en rapportent
trop peu
pour quand même essayer de marquer un maximum de points.

### FearFul Bot

&nbsp; &nbsp; &nbsp; Basé sur `Bot`, ce bot ne construit que des quartiers rapportant au moins 3 points. Et s'il se sent
en danger, de destruction d'un de ses quartiers, d'être volé, d'être tué ou qu'on lui échange ses cartes tente de se
protéger.
À chaque tour, il va donc étudier son jeu et celui des autres joueurs pour choisir un personnage lui permettant de se
protéger s'il se sent en danger. Les dangers sont classés par ordre de priorité : en premier celui de la destruction de
l'un
de ses quartiers, en second celui de voir ses cartes échanger par un autre joueur, en troisième celui d'être volé et en
dernier celui d'être tué.

### Agressive Bot

&nbsp; &nbsp; &nbsp; Basé sur `Bot`, ce bot a pour but d'être plus agressif que `Bot` sans, pour autant, l'être tout le
temps sans quoi il n'aurait qu'une infime chance de gagner ne serais-ce qu'une partie.

### Random Bot

&nbsp; &nbsp; &nbsp; Ce bot n'hérite d'aucun comportement de `Bot`. Il est directement basé sur `Player`. Il prend
toutes ses décisions de manières aléatoires.

### Richard Bot

&nbsp; &nbsp; &nbsp; Basé sur `Bot`, ce bot a pour but d'implémenter la stratégie de Richard postée sur
ce [forum](https://forum.trictrac.net/t/citadelles-charte-citadelles-de-base/509)

### Statistiques et analyse sur les bots

## Choix d'implémentation

### Journalisation

Pour la journalisation le choix a été fait d'avoir un `Level.INFO` pour tout ce qui est statistique sur un nombre de n
parties
entre k bots de différents types et un `Level.FINE` pour toutes les informations de la partie.
Cela permet de changer le niveau de journalisation en fonction de quel paramètre est passé au lancement programme.

### CSV contenant les statistiques

Le format du fichier CSV est le suivant :

* Chaque ligne donne les statistiques d'un bot pour un ensemble de parties jouées avec d'autres bots.
* Chaque colones donne dans l'ordre, le nombre de parties jouées par les bots, le nom des bots (ce qui permet aussi d'en
  déduire le type de bot). Les colonnes suivantes donnent les statistiques, de victoires, d'égalités et de défaite avec
  pour chacune 2 colones, d'abord le nombre de parties gagnées puis le pourcentage que cela représente,
  ainsi qu'une dernière colonne avec le nombre de points moyen du bot sur l'ensemble de partie.

# Architecture et qualité

## Architecture

Le projet est découpée en plusieurs gros ensembles :

* La représentation des éléments du jeu par les classes des paquets `fr.univ_cotedazur.polytech.si3.team_c.citadels.characters` et `fr.univ_cotedazur.polytech.si3.team_c.citadels.districts`.  
  Chaque personnage et chaque quartier est représenté par une classe qui lui est propre. Celle-ci hérite respectivement de `Character` ou de `District` qui tous deux sont des `Card`.  
  Cette implémentation permet une grande flexibilité dans les comportements puisqu'il suffit de redéfinir les méthodes afin de changer de comportement.
  * Les cartes (`Card`) disposent toutes d'un nom et d'une couleur (qui peut être `Colors.None`), deux cartes de même nom sont considérées comme égales.  
    Elles permettent aussi l'ajout d'actions (énuméré `Action` détaillé au point suivant) durant le tour 
    *(ex. le voleur ajoute l'action de voler ou le laboratoire permet de se débarrasser d'une carte en main afin de recevoir en échange d'une pièce)* 
    ou encore d'ajouter des actions évènementielles *(ex. le cimetière permet de récupérer une carte détruite)*.
  * En plus de ces possibilités, les personnages (`Character`) disposent d'un numéro d'ordre (ceux-ci jouent dans l'ordre déterminé par ce numéro),
    de leur capacité ou non à être en défausse face visible *(ex. le Roi)* 
    et d'un nombre minimum de joueurs dans la partie pour pouvoir être mis en jeu *(ex. l'Assassin ne peut être joué à moins de 4 joueurs)*.  
    Ils peuvent aussi définir le nombre de quartiers constructibles en un tour *(ex. l'Architecte peut en construire 3)* et empêcher la destruction de leur quartier *(ex. l'Évêque)*.
    Ils ont aussi la capacité de déclencher une action en début de tour *(ex. le Roi obtient la couronne)*
  * Les quartiers (`District`) définissent un cout et un gain de points. 
    Ils peuvent être destructibles ou non *(ex. le donjon)*. 
    Ils définissent un nombre de quartiers à piocher (le plus grand nombre parmi les quartiers construits est celui appliqué), de quartiers à conserver après avoir pioché.
    Ils sont aussi responsable de déterminer s'ils correspondent à une couleur lors du revenu généré par la couleur du personnage choisi
    *(ex. l'école de magie est décompté dans le revenu peu importe la couleur)*.
    Ils définissent aussi quelles couleurs ils peuvent représenter durant le calcul du bonus.
  
  Ainsi, de part toutes ces propriétés, l'ensemble du jeu peut être représenté en garantissant une abstraction suffisante 
  pour une potentielle évolution future avec des cartes disposant de comportements hybrides.


* Pour le moteur de jeu, nous avons décidé de séparer les différentes responsabilités dans les classes : `Game`, `Action`, `CharacterManager` et `Deck`.
  * La classe `CharacterManager` permet de gérer les personnages lors du tour de sélection des personnages et de stocker les différentes informations
  liées à celui-ci. Elle donne la liste des personnages aux joueurs et prend en compte les variations liés à leur nombre, à la fois pour la défausse,
  mais aussi pour les personnages disponibles.
  * L'énuméré `Action` liste et permet d'exécuter les différentes actions. Il interagit étroitement avec `Game` et `Player`. 
  * La classe `Deck` gère la pioche et permet de tirer un nombre de cartes donné. À sa création, les cartes sont ajoutées et mélangées.

* L'intéraction du joueur avec le moteur de jeu se fait au moyen de la classe `Player`. Cette classe est abstraite, car destinée à représenter un joueur générique.

## Documentation

Toutes les méthodes, ainsi que les points importants de notre code sont expliqués à l'aide de commentaires.
Ceux-ci contiennent pour les méthodes : la description des paramètres, la description de l'élément retourné, ainsi
qu'une explication de l'action qu'elle réalise. Lorsqu'un code est complexe et qu'il est difficile à comprendre, 
des explications sont données pour faciliter la compréhension à la relecture afin d'améliorer la productivité.

## Qualité



# Processus

### Stratégie de branchement

Nous avons opté pour la stratégie [GitHub flow](https://docs.github.com/fr/get-started/using-github/github-flow) 
car celle-ci nous semblait être la plus adaptée à la taille de notre équipe par sa simplicité de mise en place,
tandis que Git flow et Gitlab flow sont plus adaptées à des projets de plus grande envergure puisqu'elle dispose
d'un plus grand nombre d'étapes avant la livraison.
